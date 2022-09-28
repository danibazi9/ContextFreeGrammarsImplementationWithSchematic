package Project2;

import javafx.application.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PDA {
    static Grammar PDAGrammar;
    //Start/State/Final/Productions
    public static void main(String[] args) {
        File file = new File("src/Project2/PDA.txt");
        try {
            //region CreateGrammar
            Scanner reader = new Scanner(file);
            int productionNums;
            productionNums = Integer.parseInt(reader.nextLine());
            String[] allProductionRules = new String[productionNums];
            for (int line = 0; line < productionNums; line++)
                allProductionRules[line] = reader.nextLine();

            ArrayList<String> variables = new ArrayList<>();
            ArrayList<String> terminals = new ArrayList<>();
            Map<String, Set<ArrayList<String>>> productions = new LinkedHashMap<>();
            String start = "EXPRESSION";

            Main.GrammarParse(allProductionRules, variables, terminals, productions, start);

            PDAGrammar = new Grammar(variables, terminals, start, productions);
            //endregion

            System.out.println("Show Schematic?    1. Yes    2. No (Enter Expression)");
            reader = new Scanner(System.in);
            int commandNum = reader.nextInt();
            switch (commandNum) {
                case 1:
                    Application.launch(ChematicPDA.class, args);
                    break;
                case 2:
                    System.out.println("Enter Expression: ");
                    break;
                default:
                    System.out.println("Input Error!");
                    break;
            }

            reader.nextLine();
            String expression = reader.nextLine();
            expression = expression.trim();
            String isAccepted = PDAGrammar.IsGenerateByGrammar(expression) ? "Accepted" : "Rejected";
            System.out.println(isAccepted);

//            isAccepted = "Accepted";
            if (isAccepted.equals("Accepted"))
                System.out.println(Calculate(expression));
            //System.out.println(PDAGrammar.printGrammar());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String Calculate(String expression) {
        // region RPN(Reverse Polish Notation)

        // convert infix to postfix
        ArrayList<String> postfix = new ArrayList<>();
        String note = InfixToPostfix(expression, postfix) ? "Result: " : "ERROR!";
        String result = note;
        if (note.equals("Result: ")) {
            // using RPN(Reversed Polish Notation) to calculate
            ArrayList<String> Ops = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "^", "(", ")"));
            ArrayList<String> Functions = new ArrayList<>(Arrays.asList("Sin", "Cos", "Tan", "Cot", "Sqrt", "Log", "Ln", "Floor", "Dec"));
            Stack<String> stack = new Stack<>();
            stack.push(postfix.get(0));
            stack.push(postfix.get(1));
            for (String s : postfix) {
                if (Ops.contains(s) || Functions.contains(s)) {
                    switch (s) {
                        case "+":
                            stack.push(String.valueOf(Double.parseDouble(stack.pop()) + Double.parseDouble(stack.pop())));
                            break;
                        case "-":
                            stack.push(String.valueOf(Double.parseDouble(stack.pop()) - Double.parseDouble(stack.pop())));
                            break;
                        case "*":
                            stack.push(String.valueOf(Double.parseDouble(stack.pop()) * Double.parseDouble(stack.pop())));
                            break;
                        case "/":
                            stack.push(String.valueOf(Double.parseDouble(stack.pop()) / Double.parseDouble(stack.pop())));
                            break;
                        case "^":
                            stack.push(String.valueOf(Math.pow(Double.parseDouble(stack.pop()), Double.parseDouble(stack.pop()))));
                            break;
                        case "Cos":
                            stack.push(String.valueOf(Math.cos(Double.parseDouble(stack.pop()))));
                            break;
                        case "Sin":
                            stack.push(String.valueOf(Math.sin(Double.parseDouble(stack.pop()))));
                            break;
                        case "Tan":
                            stack.push(String.valueOf(Math.tan(Double.parseDouble(stack.pop()))));
                            break;
                        case "Cot":
                            stack.push(String.valueOf(1 / Math.tan(Double.parseDouble(stack.pop()))));
                            break;
                        case "Sqrt":
                            stack.push(String.valueOf(Math.sqrt(Double.parseDouble(stack.pop()))));
                            break;
                        case "Log":
                            stack.push(String.valueOf(Math.log10(Double.parseDouble(stack.pop()))));
                            break;
                        case "Ln":
                            stack.push(String.valueOf(Math.log(Double.parseDouble(stack.pop()))));
                            break;
                        case "Floor":
                            stack.push(String.valueOf(Math.floor(Double.parseDouble(stack.pop()))));
                            break;
                        case "Dec":
                            double number = Double.parseDouble(stack.pop());
                            stack.push(String.valueOf(number - Math.floor(number)));
                            break;
                    }
                } else
                    stack.push(s);
            }
            result += stack.peek();
        //endregion
        }

        return result;
    }

    private static int Precendence(String op){
        switch (op){
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            case "Sin":
            case "Cos":
            case "Tan":
            case "Cot":
            case "Sqrt":
            case "Log":
            case "Ln":
            case "Floor":
            case "Dec":
                return 4;
        }
        return -1;
    }

    private static boolean InfixToPostfix(String expression, ArrayList<String> postfix) {
         Stack<String> stack =  new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "(+-*/^)");
        ArrayList<String> Ops = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "^", "(", ")"));
        ArrayList<String> Functions = new ArrayList<>(Arrays.asList("Sin", "Cos", "Tan", "Cot", "Sqrt", "Log", "Ln", "Floor", "Dec"));

        //region Replacements
            //region Ops
        expression = expression.replace("+", " + ");
        expression = expression.replace("-", " - ");
        expression = expression.replace("*", " * ");
        expression = expression.replace("/", " / ");
        expression = expression.replace("^", " ^ ");
        expression = expression.replace("(", "( ");
        expression = expression.replace(")", " )");
            //endregion

            //region Functions
                //region Trigonometric
        expression = expression.replace("Sin", "Sin ");
        expression = expression.replace("Cos", "Cos ");
        expression = expression.replace("Tan", "Tan ");
        expression = expression.replace("Cot", "Cot ");
                //endregion

                //region Exponential
        expression = expression.replace("Sqrt", "Sqrt ");
        expression = expression.replace("Log", "Log ");
        expression = expression.replace("Ln", "Ln ");
                //endregion

                //region [],{}
        expression = expression.replace("Floor", "Floor ");
        expression = expression.replace("Dec", "Dec ");
                //endregion
            //endregion

            //region ScientificNumbers
        expression = expression.replace("e", "2.718281828459045");
        expression = expression.replace("pi", "3.141592653589793");
            //endregion
        //endregion

        String[] tokens = expression.split(" ");
        for (String token : tokens){
            if (!Ops.contains(token) && !Functions.contains(token))
                postfix.add(token);
            else if (token.equals("("))
                stack.push(token);
            else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    postfix.add(stack.pop());
                if (!stack.isEmpty() && !stack.peek().equals("("))
                    return false;
                else
                    stack.pop();
                if (Functions.contains(stack.peek()))
                    postfix.add(stack.pop());
            } else {
                while (!stack.isEmpty() && Precendence(token) <= Precendence(stack.peek())) {
                    if (stack.peek().equals("("))
                        return false;
                    postfix.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("("))
                return false;
            postfix.add(stack.pop());
        }
        return true;
    }
}
