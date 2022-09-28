package Project2;

import java.lang.reflect.Array;
import java.util.*;

public class Grammar {
    // example:
    // <START> -> <SUBJECT><DISTANCE><VERB><DISTANCE><OBJECTS>
    // <SUBJECT> -> i|we
    // <DISTANCE> -> 
    // <VERB> -> eat|drink|go
    // <OBJECTS> - > food|water|lambda
    public ArrayList<String> variables;
    public ArrayList<String> terminals;
    public Map<String, Set<ArrayList<String>>> productions;
    public String start; //default

    private boolean isLambdaFound;
    private boolean isUnitFound;
    private boolean isUselessFound;
    private boolean foundTerminatorVariable;

    private Set<String> uselessVariables;

    //region Constructor

    public Grammar(ArrayList<String> variables, ArrayList<String> terminals, String start, Map<String, Set<ArrayList<String>>> productions) {
        this.variables = variables;
        this.terminals = terminals;
        this.start = start;
        this.productions = productions;
    }

    //endregion

    //region Validating

    public boolean isChomskyForm() {
        // return true if the Grammar is in Chomsky Normal Form
        if (!isDeleteTrash())
            return false;
        for (Set<ArrayList<String>> productionRules : productions.values()) {
            for (ArrayList<String> productionRule : productionRules) {
                if (productionRule.size() > 2)
                    return false;
                else if (productionRule.size() == 2 &&
                        (!this.variables.contains(productionRule.get(0)) || !this.variables.contains(productionRule.get(1))))
                    return false;
                else if (productionRule.size() == 1 &&
                        this.variables.contains(productionRule.get(0))) ;
                return false;
            }
        }
        return true;
    }

    public boolean isGreibachNormalForm() {
        // return true if the Grammar is in Greibach Normal Form
        if (!isDeleteTrash())
            return false;
        for (Set<ArrayList<String>> productionRules : productions.values()) {
            for (ArrayList<String> productionRule : productionRules) {
                if (this.variables.contains(productionRule.get(0)))
                    return false;
                else if (productionRule.size() > 1)
                    for (int parameter = 1; parameter < productionRule.size(); parameter++)
                        if (!this.variables.contains(productionRule.get(parameter)))
                            return false;
            }
        }
        return true;
    }

    public boolean isDeleteTrash() {
        // return true if these options don't exist:
        // (1) useless transitions
        // (2) unit production
        // (3) lambda transitions

        isLambdaFound = false;
        isUnitFound = false;
        isUselessFound = false;

        if (productions.isEmpty())
            return true;

        if (this.terminals.contains("lambda")) {
            isLambdaFound = true;
            return false;
        }

        for (Set<ArrayList<String>> productionRules : productions.values()) {
            for (ArrayList<String> productionRule : productionRules) {
                if (productionRule.size() == 1 &&
                        this.variables.contains(productionRule.get(0))) {
                    isUnitFound = true;
                    return false;
                }
            }
        }

        uselessVariables = new HashSet<>();
        ArrayList<String> differ = new ArrayList<>();

        Set<String> terminatorVariables = new HashSet<>();
        for (String key : productions.keySet()) {
            for (ArrayList<String> productionRule : productions.get(key)) {
                if (productionRule.size() == 1 &&
                        this.terminals.contains(productionRule.get(0))) {
                    terminatorVariables.add(key);
                    break;
                }
            }
        }

        foundTerminatorVariable = true;
        while (foundTerminatorVariable) {
            foundTerminatorVariable = false;
            for (String key : productions.keySet()) {
                if (terminatorVariables.contains(key))
                    continue;
                for (ArrayList<String> productionRule : productions.get(key)) {
                    ArrayList<String> productionRuleVariables = new ArrayList<>(variables);

                    productionRuleVariables.retainAll(productionRule);
                    productionRuleVariables.removeAll(terminatorVariables);
                    if (productionRuleVariables.isEmpty()) {
                        terminatorVariables.add(key);
                        foundTerminatorVariable = true;
                    }
                }
            }
        }

        if (terminatorVariables.size() != variables.size()) {
            isUselessFound = true;

            //not-terminate variables
            differ.addAll(this.variables);
            differ.removeAll(terminatorVariables);

            uselessVariables.addAll(differ);

            return false;
        }

        ArrayList<String> reachableVariables = new ArrayList<>();
        Queue<String> processingVariables = new LinkedList<>();
        processingVariables.add(this.start);
        reachableVariables.add(this.start);
        while (!processingVariables.isEmpty()) {
            String processingVariable = processingVariables.poll();
            ArrayList<String> variables = new ArrayList<>();
            if (productions.containsKey(processingVariable)) {
                for (ArrayList<String> productionRule : productions.get(processingVariable)) {
                    for (String parameter : productionRule) {
                        if (Character.isUpperCase(parameter.charAt(0))) {
                            if (!reachableVariables.contains(parameter)) {
                                reachableVariables.add(parameter);
                                variables.add(parameter);
                            }
                        }
                    }
                    processingVariables.addAll(variables);
                }
            }
        }

        if (reachableVariables.size() != this.variables.size()) {
            isUselessFound = true;

            //unreachable variables
            differ.addAll(this.variables);
            differ.removeAll(reachableVariables);

            uselessVariables.addAll(differ);
            return false;
        }
        return true;
    }

    //endregion

    public void ChangeToGreibachForm() {
        // if <<isGreibachNormalForm>> is false:
        // return new Grammar that is conversion of previous Grammar into Greibach Normal Form

        // Step 1. Convert the grammar into CNF.
        // If the given grammar is not in CNF, convert it to CNF.
        // Step 2. Eliminate left recursion from grammar if it exists.
        // If CFG contains left recursion, eliminate them.
        // Step 3. Convert the production rules into GNF form.

        if (isGreibachNormalForm())
            return;

        if (!isChomskyForm())
            ChangeToChomskyForm();

        Map<String, Set<ArrayList<String>>> productions = new LinkedHashMap<>();
        for (String key: this.productions.keySet()) {
            productions.put(key, new LinkedHashSet<>());
            for (ArrayList<String> productionRule : this.productions.get(key)) {
                ArrayList<String> newProductionRule = new ArrayList<>();
                newProductionRule.addAll(productionRule);
                productions.get(key).add(newProductionRule);
            }
        }

        while (!isGreibachNormalForm()) {
            eliminateLeftRecursion();
            for (String key : productions.keySet()) {
                Set<ArrayList<String>> addingProductionsRules = new LinkedHashSet<>();
                Set<ArrayList<String>> removingProductionsRules = new LinkedHashSet<>();

                for (ArrayList<String> productionRule : productions.get(key)) {
                    if (terminals.contains(productionRule.get(0)))
                        continue;

                    String variable = productionRule.get(0);
                    productionRule.remove(variable);
                    for (ArrayList<String> addingProductionRule : productions.get(variable)) {
                        ArrayList<String> newProductionRule = new ArrayList<>(productionRule);
                        newProductionRule.addAll(0, addingProductionRule);
                        addingProductionsRules.add(newProductionRule);
                    }
                    removingProductionsRules.add(productionRule);
                }
                ArrayList<ArrayList<String>> temp = new ArrayList<>(productions.get(key));
                temp.removeAll(removingProductionsRules);
                productions.replace(key, new LinkedHashSet<>(temp));
                productions.get(key).addAll(addingProductionsRules);
            }
            this.productions = productions;
            removeUselessProductions();
        }
    }

    public void ChangeToChomskyForm() {
        // if <<isChomskyForm>> is false:
        // return new Grammar that is conversion of previous Grammar into Chomsky Normal Form
        if (isChomskyForm())
            return;

        DeleteTrash();

        Map<String, String> terminalMapping = new LinkedHashMap<>();
        Map<ArrayList<String>, String> variableMapping = new LinkedHashMap<>();

        for (String key : productions.keySet()) {
            for (ArrayList<String> productionRule : productions.get(key)) {
                if (productionRule.size() == 1
                        && terminals.contains(productionRule.get(0)))
                    continue;
                else if (productionRule.size() == 2 &&
                        (variables.contains(productionRule.get(0)) &&
                                variables.contains(productionRule.get(1))))
                    continue;
                else {
                    // replace terminal -> variable
                    for (String parameter : productionRule) {
                        if (terminals.contains(parameter)) {
                            if (!terminalMapping.containsKey(parameter))
                                terminalMapping.put(parameter, "T" + terminalMapping.size());

                            productionRule.set(productionRule.indexOf(parameter),
                                    terminalMapping.get(parameter));
                        }
                    }

                    int lastVariableIndex;
                    while (productionRule.size() != 2) {
                        lastVariableIndex = productionRule.size() - 1;
                        ArrayList<String> pairVariables = new ArrayList<>();
                        pairVariables.add(productionRule.get(lastVariableIndex - 1));
                        pairVariables.add(productionRule.get(lastVariableIndex));

                        if (!variableMapping.containsKey(pairVariables))
                            variableMapping.put(pairVariables, "V" + variableMapping.size());

                        productionRule.remove(lastVariableIndex);
                        productionRule.remove(lastVariableIndex - 1);
                        productionRule.add(variableMapping.get(pairVariables));
                    }
                }
            }
        }

        for (String terminal : terminalMapping.keySet()) {
            String terminalVariable = terminalMapping.get(terminal);
            if (!variables.contains(terminalVariable))
                variables.add(terminalVariable);
            productions.put(terminalVariable, new HashSet<>());
            ArrayList<String> term = new ArrayList<>();
            term.add(terminal);
            productions.get(terminalVariable).add(term);
        }

        for (ArrayList<String> pairVariable : variableMapping.keySet()) {
            String variable = variableMapping.get(pairVariable);
            if (!variables.contains(variable))
                variables.add(variable);
            productions.put(variable, new HashSet<>());
            productions.get(variable).add(pairVariable);
        }
    }

    public void DeleteTrash() {
        // delete:
        // (1) useless transitions
        // (2) unit production
        // (3)lambda transitions
        // modify Grammar
        while (!isDeleteTrash()) {
            if (isUselessFound)
                removeUselessProductions();
            else if (isUnitFound)
                removeUnitProductions();
            else if (isLambdaFound)
                removeLambdaProductions();
//            System.out.println(this.printGrammar());
        }
    }

    public boolean IsGenerateByGrammar(String str) {
        // return true if the string that taken from input can be generated by this grammar
        // if isChomskyForm was true use dynamic programming
        if (!isChomskyForm())
            ChangeToChomskyForm();

        List<String> words = new ArrayList<>();
        String[] strSplited = str.split(" ");
        if (Main.hasSpace) {
            for (int i = 0; i < strSplited.length; i++) {
                words.add(strSplited[i]);
                if (i != strSplited.length - 1)
                    words.add(" ");
            }
        } else
            words = Arrays.asList(str.split(""));

        int strLength = words.size();
        int numOfProductions = this.productions.size();
        Map<String, Integer> variablesMapping = new HashMap<>();
        int counter = 1;
        for (String key : this.productions.keySet())
            variablesMapping.put(key, counter++);

        Boolean[][][] dpTable = new Boolean[strLength + 1][strLength + 1][numOfProductions + 1];
        for (int i = 1; i <= strLength; i++)
            for (int j = 1; j <= strLength; j++)
                for (int k = 1; k <= numOfProductions; k++)
                    dpTable[i][j][k] = false;

        // for each unit production Rv → as set true
        for (int index = 1; index <= strLength; index++)
            for (String key : this.productions.keySet())
                for (ArrayList<String> productionRule : this.productions.get(key))
                    if (productionRule.size() == 1 && productionRule.contains(words.get(index - 1))) {
                        dpTable[1][index][variablesMapping.get(key)] = true;
                        break;
                    }

        // for each production Ra  → Rb Rc set true
        for (int lengthOfSpan = 2; lengthOfSpan <= strLength; lengthOfSpan++)
            for (int startOfSpan = 1; startOfSpan <= (strLength - lengthOfSpan + 1); startOfSpan++)
                for (int partitionOfSpan = 1; partitionOfSpan <= lengthOfSpan - 1; partitionOfSpan++)
                    for (String key: this.productions.keySet())
                        for (ArrayList<String> productionRule: this.productions.get(key))
                            if (productionRule.size() == 2)
                                if (dpTable[partitionOfSpan][startOfSpan][variablesMapping.get(productionRule.get(0))] &&
                                        dpTable[lengthOfSpan - partitionOfSpan][startOfSpan + partitionOfSpan][variablesMapping.get(productionRule.get(1))])
                                    dpTable[lengthOfSpan][startOfSpan][variablesMapping.get(key)] = true;

        return dpTable[strLength][1][1];
    }


    //region HelperFunctions

    private boolean foundUnitProductions = true;

    private void removeUnitProductions() {
        Map<String, ArrayList<String>> unitProductions = new HashMap<>();
        for (String key : productions.keySet()) {
            for (ArrayList<String> productionRule : productions.get(key)) {
                if (productionRule.size() == 1 &&
                        variables.contains(productionRule.get(0))) {
                    if (!unitProductions.containsKey(key))
                        unitProductions.put(key, new ArrayList<>());
                    unitProductions.get(key).add(productionRule.get(0));
                }
            }
        }

        if (unitProductions.isEmpty()) {
            foundUnitProductions = false;
            return;
        }

        while (foundUnitProductions) {
            for (String key : unitProductions.keySet()) {
                for (String productionRule : unitProductions.get(key)) {
                    productions.get(key).addAll(productions.get(productionRule));
                    ArrayList<String> removing = new ArrayList<>();
                    removing.add(productionRule);
                    productions.get(key).remove(removing);
                }
            }
            removeUnitProductions();
        }
    }

    private boolean foundNewUselessVariables;

    private void removeUselessProductions() {
        if (uselessVariables.isEmpty())
            return;
        if (productions.isEmpty())
            return;

        Set<ArrayList<String>> removingProductionRules = new HashSet<>();
        for (String key : productions.keySet()) {
            for (ArrayList<String> productionRule : productions.get(key)) {
                for (String parameter : productionRule) {
                    if (uselessVariables.contains(parameter)) {
                        removingProductionRules.add(productionRule);
                        break;
                    }
                }
            }
            productions.get(key).removeAll(removingProductionRules);
        }

        for (String uselessVariable : uselessVariables) {
            productions.remove(uselessVariable);
            variables.remove(uselessVariable);
        }

        uselessVariables = new HashSet<>();
        foundNewUselessVariables = true;
        while (foundNewUselessVariables) {
            foundNewUselessVariables = false;
            ArrayList<String> keys = new ArrayList<>(productions.keySet());
            for (int keyIndex = 0; keyIndex < productions.keySet().size(); keyIndex++) {
                String key = keys.get(keyIndex);
                if (productions.get(key).isEmpty()) {
                    productions.remove(key);
                    uselessVariables.add(key);
                    foundNewUselessVariables = true;
                }
            }
            if (foundNewUselessVariables)
                removeUselessProductions();
        }
    }

    private void removeLambdaProductions() {
        if (!terminals.contains("lambda"))
            return;

        ArrayList<String> nullableVariables = new ArrayList<>();
        for (String key : productions.keySet()) {
            for (ArrayList<String> productionRule : productions.get(key)) {
                if (productionRule.size() == 1 && productionRule.get(0).equals("lambda")) {
                    if (!nullableVariables.contains(key))
                        nullableVariables.add(key);
                    productions.get(key).remove(productionRule);
                    break;
                }
            }
        }

        terminals.remove("lambda");

        for (String key : productions.keySet()) {
            ArrayList<ArrayList<String>> productionRules = new ArrayList<>(productions.get(key));
            for (int index = 0; index < productionRules.size(); index++) {
                ArrayList<String> productionRule = productionRules.get(index);
                ArrayList<String> intersection = new ArrayList<>(productionRule);
                intersection.retainAll(nullableVariables);
                if (!intersection.isEmpty()) {
                    for (String nullableVariable : intersection) {
                        for (int parameterIndex = 0; parameterIndex < productionRule.size(); parameterIndex++) {
                            String parameter = productionRule.get(parameterIndex);
                            if (parameter.equals(nullableVariable)) {
                                ArrayList<String> newProductionRule = new ArrayList<>(productionRule);
                                newProductionRule.remove(parameterIndex);
                                if (!productionRules.contains(newProductionRule))
                                    productionRules.add(newProductionRule);
                            }
                        }
                    }
                }
            }
            productions.get(key).addAll(productionRules);
        }
    }

    private void eliminateLeftRecursion() {
        for (String key : productions.keySet()){
            Set<ArrayList<String>> leftRecursionProductionRules = new LinkedHashSet<>();
            for (ArrayList<String> productionRule : productions.get(key))
                if (productionRule.get(0).equals(key))
                    leftRecursionProductionRules.add(productionRule);

            if (leftRecursionProductionRules.isEmpty())
                continue;

            //nonLeftRecursionProductionRules
            productions.get(key).removeAll(leftRecursionProductionRules);

            if (!variables.contains(key + "'")) {
                variables.add(key + "'");
                productions.put(key + "'", new LinkedHashSet<>());
            }

            Set<ArrayList<String>> newProductionRules = new LinkedHashSet<>();
            for (ArrayList<String> productionRule : productions.get(key)){
                ArrayList<String> newProductionRule = new ArrayList<>(productionRule);
                newProductionRule.add(key + "'");
                newProductionRules.add(newProductionRule);
            }

            productions.get(key).addAll(newProductionRules);

            productions.get(key + "'").addAll(leftRecursionProductionRules);

            for (ArrayList<String> productionRule : leftRecursionProductionRules){
                ArrayList<String> newProductionRule = new ArrayList<>(productionRule);
                newProductionRule.remove(0);
                newProductionRule.add(key + "'");
                productions.get(key + "'").add(newProductionRule);
            }
        }
    }

    public String printGrammar(){
        StringBuilder output = new StringBuilder();
        if (productions.isEmpty()){
            output = new StringBuilder("NULL");
            return output.toString();
        }

        for (String key : productions.keySet()){
            output.append("\n");
            output.append("<").append(key).append(">").append(" -> ");

            int productionRuleCount = 0;
            for (ArrayList<String> productionRule : productions.get(key)){
                for (String parameter : productionRule){
                    if (Character.isUpperCase(parameter.charAt(0)))
                        output.append("<").append(parameter).append(">");
                    else
                        output.append(parameter);
                }
                productionRuleCount++;
                if (productionRuleCount != productions.get(key).size())
                    output.append("|");
            }
        }
        output.append("\n");

        return output.toString();
    }
    //endregion
}