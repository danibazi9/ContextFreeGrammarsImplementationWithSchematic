// Theory of Computation Second Project
// Instructor: Dr. Hosein Rahmani
// Written by: Danial Bazmandeh & Alireza Haqani
// Number of lines: About 1814 (+473)
package Project2;

import javafx.application.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static String start;
    static boolean hasSpace;
    static File greibachFile = new File("src/Project2/log/Greibach.txt");
    static File chomskyFile = new File("src/Project2/log/Chomsky.txt");
    static File deleteTrashFile = new File("src/Project2/log/Delete Trash.txt");
    static Scanner reader;
    static FileWriter writer;
    static boolean isGreibachCalculated = false, isChomskyCalculated = false, isDeleteTrashCalculated = false;

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("*** WELCOME ***");

        System.out.println("Please enter the User Interface you want to continue with:");
        System.out.println("1. Terminal");
        System.out.println("2. GUI");
        int selectUI = Integer.parseInt(input.nextLine());

        if (selectUI == 1) {
            int productionNums;
            System.out.println("Enter Grammar Production Rules Number: ");
            productionNums = input.nextInt();

            System.out.println("Enter Grammar Productions: ");
            String[] allProductionRules = new String[productionNums];
            input.nextLine();
            for (int line = 0; line < productionNums; line++) {
                allProductionRules[line] = input.nextLine();
            }

            //region Example:
            // <START> -> <SUBJECT><DISTANCE><VERB><DISTANCE><OBJECTS>
            // <SUBJECT> -> i|we
            // <DISTANCE> -> " "
            // <VERB> -> eat|drink|go
            // <OBJECTS> -> food|water|lambda
            //endregion

            ArrayList<String> variables = new ArrayList<>();
            ArrayList<String> terminals = new ArrayList<>();
            Map<String, Set<ArrayList<String>>> productions = new LinkedHashMap<>();

            //split into 2 parts
            GrammarParse(allProductionRules, variables, terminals, productions, start);
            /*
            for (String productionRules: allProductionRules){
                String[] parts = productionRules.split(" -> ");
                //find variables
                //variables = [<START>, <SUBJECT>, <DISTANCE>, <VERB>, <OBJECTS>]
                String variable = parts[0].substring(1, parts[0].length()-1);
                variables.add(variable);

                if (allProductionRules[0].equals(productionRules))
                    start = variable;

                //find terminals
                //construct productions
                String[] productionRulesParts = parts[1].split("\\|");
                productions.put(variable, new LinkedHashSet<>());
                for (String productionRule : productionRulesParts) {
                    ArrayList<String> productionRuleParameters = new ArrayList<>();
                    StringTokenizer tokenizer = new StringTokenizer(productionRule, "<>");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        productionRuleParameters.add(token);
                        if (Character.isLowerCase(token.charAt(0)) || token.equals(" "))
                            terminals.add(token);
                    }
                    productions.get(variable).add(productionRuleParameters);
                }
            }
            */
            if (terminals.contains(" "))
                hasSpace = true;
            Grammar grammar = new Grammar(variables, terminals, start, productions);

            int command = 0;
            while (command != 8) {
                System.out.println();
                System.out.println("Select Command Number: ");
                System.out.println("1. IsChomskyForm");
                System.out.println("2. IsGreibachNormalForm");
                System.out.println("3. IsDeleteTrash");
                System.out.println("4. ChangeToGreibachForm");
                System.out.println("5. ChangeToChomskyForm");
                System.out.println("6. DeleteTrash");
                System.out.println("7. IsGenerateByGrammar");
                System.out.println("8. Exit");
                command = input.nextInt();
                String word;

                switch (command) {
                    case 1:
                        System.out.println();
                        System.out.println("1. IsChomskyForm");
                        String isChomskeyForm = grammar.isChomskyForm() ? "Yes!" : "No!";
                        System.out.println("Input Grammar 'IsChomskyForm': " + isChomskeyForm);
                        break;
                    case 2:
                        System.out.println();
                        System.out.println("2. IsGreibachNormalForm");
                        String isGreibachForm = grammar.isGreibachNormalForm() ? "Yes!" : "No!";
                        System.out.println("Input Grammar 'IsGreibachNormalForm': " + isGreibachForm);
                        break;
                    case 3:
                        System.out.println();
                        System.out.println("3. IsDeleteTrash");
                        String isDeleteTrash = grammar.isDeleteTrash() ? "Yes!" : "No!";
                        System.out.println("Input Grammar 'IsDeleteTrash': " + isDeleteTrash);
                        break;
                    case 4:
                        System.out.println();
                        System.out.println("4. ChangeToGreibachForm");
                        System.out.println("=====Change to GNF Has Successfully Done!!!=====");
                        System.out.println("New Grammar: ");
                        if (!isGreibachCalculated) {
                            grammar.ChangeToGreibachForm();
                            writer = new FileWriter("src/Project2/log/Greibach.txt");
                            writer.write(grammar.printGrammar());
                            writer.close();
                            System.out.println(grammar.printGrammar());
                            isGreibachCalculated = true;
                        } else {
                            reader = new Scanner(greibachFile);
                            while (reader.hasNextLine())
                                System.out.println(reader.nextLine());
                        }
                        break;
                    case 5:
                        System.out.println();
                        System.out.println("5. ChangeToChomskyForm");
                        System.out.println("=====Change to CNF Has Successfully Done!!!=====");
                        System.out.println("New Grammar: ");
                        if (!isChomskyCalculated) {
                            grammar.ChangeToChomskyForm();
                            writer = new FileWriter("src/Project2/log/Chomsky.txt");
                            writer.write(grammar.printGrammar());
                            writer.close();
                            System.out.println(grammar.printGrammar());
                            isChomskyCalculated = true;
                        } else {
                            reader = new Scanner(chomskyFile);
                            while (reader.hasNextLine())
                                System.out.println(reader.nextLine());
                        }
                        break;
                    case 6:
                        System.out.println();
                        System.out.println("6. DeleteTrash");
                        System.out.println("=====Deleting Trash Has Successfully Done!!!=====");
                        System.out.println("New Grammar: ");
                        if (!isDeleteTrashCalculated) {
                            grammar.DeleteTrash();
                            writer = new FileWriter("src/Project2/log/Delete Trash.txt");
                            writer.write(grammar.printGrammar());
                            writer.close();
                            System.out.println(grammar.printGrammar());
                            isDeleteTrashCalculated = true;
                        } else {
                            reader = new Scanner(deleteTrashFile);
                            while (reader.hasNextLine())
                                System.out.println(reader.nextLine());
                        }
                        break;
                    case 7:
                        System.out.println();
                        System.out.println("7. IsGenerateByGrammar");
                        System.out.println("Please enter the word you want to check:");
                        input.nextLine();
                        word = input.nextLine();
                        String isAccepted = grammar.IsGenerateByGrammar(word) ? "Accepted!" : "Rejected!";
                        System.out.println(word + ": " + isAccepted);
                        break;
                    default:
                        if (command != 8)
                            System.out.println("Command Not Found! \n\t Enter Command Again: ");
                        break;
                }
            }

            System.out.println("8. Exit");
            System.out.println("*** GoodBye! ***");
            System.exit(0);
        } else if (selectUI == 2)
            Application.launch(MainPage.class);
        else
            System.out.println("Input Error!");
    }

    public static void GrammarParse(String[] allProductionRules, ArrayList<String> variables, ArrayList<String> terminals, Map<String, Set<ArrayList<String>>> productions, String start) {
        for (String productionRules: allProductionRules){
            String[] parts = productionRules.split(" -> ");
            //find variables
            //variables = [<START>, <SUBJECT>, <DISTANCE>, <VERB>, <OBJECTS>]
            String variable = parts[0].substring(1, parts[0].length()-1);
            variables.add(variable);

            if (allProductionRules[0].equals(productionRules))
                Main.start = variable;

            //find terminals
            //construct productions
            String[] productionRulesParts = parts[1].split("\\|");
            productions.put(variable, new LinkedHashSet<>());
            for (String productionRule : productionRulesParts) {
                ArrayList<String> productionRuleParameters = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(productionRule, "<>");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    productionRuleParameters.add(token);
                    if (Character.isLowerCase(token.charAt(0)) || token.equals(" ") ||
                            Character.isDigit(token.charAt(0)) || !Character.isLetter(token.charAt(0)))
                        if (!terminals.contains(token))
                            terminals.add(token);
                }
                productions.get(variable).add(productionRuleParameters);
            }
        }
    }
}