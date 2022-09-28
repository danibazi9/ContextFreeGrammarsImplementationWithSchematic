package Project2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    boolean flag = false;
    Grammar grammar;
    Grammar PDAGrammar;
    private File selectedFile = null;
    File greibachFile = new File("src/Project2/log/Greibach.txt");
    File chomskyFile = new File("src/Project2/log/Chomsky.txt");
    File deleteTrashFile = new File("src/Project2/log/Delete Trash.txt");
    Scanner reader;
    FileWriter writer;
    boolean isGreibachCalculated = false, isChomskyCalculated = false, isDeleteTrashCalculated = false;

    private Stage myStage;
    public void setStage(Stage stage) {
        myStage = stage;
    }

    //region FXMLs
    @FXML
    JFXButton closeBtn;

    @FXML
    JFXButton deleteBtn;

    @FXML
    JFXButton listBtn;

    @FXML
    JFXButton submitBtn;

    @FXML
    JFXButton deleteTrashBtn;

    @FXML
    JFXButton cnfBtn;

    @FXML
    JFXButton openBtn;

    @FXML
    JFXButton gnfBtn;

    @FXML
    JFXButton calculateBtn;

    @FXML
    JFXButton generateBtn;

    @FXML
    JFXButton grammarValidationBtn;

    @FXML
    Pane sidebarPane;

    @FXML
    BorderPane borderPane;

    @FXML
    Pane contentAreaPane;

    @FXML
    JFXTextArea txtInput;

    @FXML
    JFXTextArea txtFixed;

    @FXML
    JFXTextArea txtExpression;

    @FXML
    Label lblNotif;
    //endregion

    //region Functions
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borderPane.setLeft(null);

        listBtn.setOnAction(actionEvent -> {
            if (!flag) {
                myStage.setWidth(1090);
                borderPane.setLeft(sidebarPane);
                myStage.centerOnScreen();
                flag = true;
            } else {
                myStage.setWidth(790);
                borderPane.setLeft(null);
                myStage.centerOnScreen();
                flag = false;
            }
        });

        submitBtn.setOnAction(actionEvent -> {
            try{
                FileWriter fw = new FileWriter("src/Project2/log/input.txt");
                fw.write(txtInput.getText());
                fw.close();

                File file = new File("src/Project2/log/input.txt");
                Scanner input = new Scanner(file);
                int productionNums = Integer.parseInt(input.nextLine());

                String[] allProductionRules = new String[productionNums];
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

                Main.GrammarParse(allProductionRules, variables, terminals, productions, Main.start);

                grammar = new Grammar(variables, terminals, Main.start, productions);
                txtFixed.setText(grammar.printGrammar());
                lblNotif.setText("Successfully saved!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        calculateBtn.setOnAction(actionEvent -> {
            try {
                //region CreateGrammar
                File file = new File("D:/Theory of Computation/src/Project2/PDA.txt");
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

                String expression = txtInput.getText().trim();
                String isAccepted = PDAGrammar.IsGenerateByGrammar(expression) ? "Accepted" : "Rejected";
                if (isAccepted.equals("Accepted")) {
                    lblNotif.setText(isAccepted + " -> " + PDA.Calculate(expression));
                    lblNotif.setTextFill(Color.GREEN);
                } else {
                    lblNotif.setText(isAccepted);
                    lblNotif.setTextFill(Color.RED);
                }
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        deleteTrashBtn.setOnAction(actionEvent -> {
            try {
                if (!isDeleteTrashCalculated) {
                    grammar.DeleteTrash();
                    writer = new FileWriter("src/Project2/log/Delete Trash.txt");
                    writer.write(grammar.printGrammar());
                    writer.close();
                    txtFixed.setText(grammar.printGrammar());
                    isDeleteTrashCalculated = true;
                } else {
                    txtFixed.setText("");
                    reader = new Scanner(deleteTrashFile);
                    while (reader.hasNextLine())
                        txtFixed.appendText(reader.nextLine() + "\n");
                }
                lblNotif.setText("Successfully Done!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        cnfBtn.setOnAction(actionEvent -> {
            try {
                if (!isChomskyCalculated) {
                    grammar.ChangeToChomskyForm();
                    writer = new FileWriter("src/Project2/log/Chomsky.txt");
                    writer.write(grammar.printGrammar());
                    writer.close();
                    txtFixed.setText(grammar.printGrammar());
                    isChomskyCalculated = true;
                } else {
                    txtFixed.setText("");
                    reader = new Scanner(chomskyFile);
                    while (reader.hasNextLine())
                        txtFixed.appendText(reader.nextLine() + "\n");
                }
                lblNotif.setText("Successfully Done!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        gnfBtn.setOnAction(actionEvent -> {
            try {
                if (!isGreibachCalculated) {
                    grammar.ChangeToGreibachForm();
                    writer = new FileWriter("src/Project2/log/Greibach.txt");
                    writer.write(grammar.printGrammar());
                    writer.close();
                    txtFixed.setText(grammar.printGrammar());
                    isGreibachCalculated = true;
                } else {
                    txtFixed.setText("");
                    reader = new Scanner(greibachFile);
                    while (reader.hasNextLine())
                        txtFixed.appendText(reader.nextLine() + "\n");
                }
                lblNotif.setText("Successfully Done!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        grammarValidationBtn.setOnAction(actionEvent -> {
            try {
                String isChomskeyForm = grammar.isChomskyForm() ? "Yes!" : "No!";
                txtFixed.setText("'IsChomskyForm': " + isChomskeyForm + "\n");
                String isGreibachForm = grammar.isGreibachNormalForm() ? "Yes!" : "No!";
                txtFixed.appendText("'IsGreibachForm': " + isGreibachForm + "\n");
                String isDeleteTrash = grammar.isDeleteTrash() ? "Yes!" : "No!";
                txtFixed.appendText("'IsDeleteTrash': " + isDeleteTrash);
                lblNotif.setText("Successfully Done!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        generateBtn.setOnAction(actionEvent -> {
            try {
                String expression = txtExpression.getText();
                Main.hasSpace = txtInput.getText().contains("<DISTANCE>");
                String isAccepted = grammar.IsGenerateByGrammar(expression) ? "Accepted!" : "Rejected!";
                if (isAccepted.equals("Accepted!"))
                    lblNotif.setTextFill(Color.GREEN);
                else
                    lblNotif.setTextFill(Color.RED);
                lblNotif.setText(expression + ": " + isAccepted);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        openBtn.setOnAction(actionEvent -> {
            try {
                FileChooser fileChooser = new FileChooser();
                Stage stage = (Stage) borderPane.getScene().getWindow();
                fileChooser.setInitialDirectory(new File("D:/Theory of Computation"));
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                selectedFile = fileChooser.showOpenDialog(stage);

                txtInput.setText("");
                reader = new Scanner(selectedFile);
                while (reader.hasNextLine())
                    txtInput.appendText(reader.nextLine() + "\n");

                lblNotif.setText("File has Successfully loaded!");
                lblNotif.setTextFill(Color.GREEN);
            } catch (Exception e) {
                lblNotif.setText("An error occurred.");
                lblNotif.setTextFill(Color.RED);
            }
        });

        deleteBtn.setOnAction(actionEvent -> txtInput.clear());

        closeBtn.setOnAction(actionEvent -> System.exit(0));
        }
    //endregion
}