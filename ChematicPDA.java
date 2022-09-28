package Project2;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class ChematicPDA extends Application {
    private Grammar PDAGrammar = PDA.PDAGrammar;
    private static ArrayList<String> statesNames = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        statesNames.addAll(Arrays.asList("q0", "q1", "q2"));
        Collections.sort(statesNames);
        Image image = new Image(new FileInputStream("src/Project2/pictures/Arrow.png"));

        Group root = new Group();

        Text title = new Text("Schematic Visualization of PDA");
        title.setX(240);
        title.setY(60);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 32));
        title.setFill(Color.rgb(255, 0, 30));
        root.getChildren().add(title);

        HashMap<String, int[]> positions = new HashMap<>();
        int counterX = 0;

        //region Draw Circles
        for (String state: statesNames) {
            Circle circle = new Circle();
            int circleX = 175 + counterX;
            int circleY = 550;
            circle.setCenterX(circleX);
            circle.setCenterY(circleY);

            int[] position = new int[2];
            position[0] = circleX;
            position[1] = circleY;
            positions.put(state, position);

            circle.setRadius(25);
            circle.setFill(Color.GOLD);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(3);
            root.getChildren().add(circle);

            if (state.equals("q2")) {
                circle = new Circle();
                circle.setCenterX(circleX);
                circle.setCenterY(circleY);
                circle.setRadius(19);
                circle.setFill(Color.GOLD);
                circle.setStroke(Color.BLUE);
                circle.setStrokeWidth(3);
                root.getChildren().add(circle);
            }

            Text text = new Text(state);
            text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 22));
            text.setX(positions.get(state)[0] - 13);
            text.setY(557);

            root.getChildren().add(text);
            counterX += 330;
        }
        //endregion

        int startX, endX, startY, endY, middleX, middleY = 490;

        //region first state
        ImageView arrowHead1 = new ImageView(image);
        arrowHead1.setFitHeight(22);
        arrowHead1.setFitWidth(22);

        startX = positions.get("q0")[0] + 25;
        startY = positions.get("q0")[1];
        endX = positions.get("q1")[0] - 25;
        endY = positions.get("q1")[1];
        middleX = (positions.get("q0")[0] + positions.get("q1")[0]) / 2;

        arrowHead1.setX(middleX + 15);
        arrowHead1.setY(middleY + 19);
        Rotate rotatedArrow1 = new Rotate(90, arrowHead1.getX(), arrowHead1.getY());
        arrowHead1.getTransforms().add(rotatedArrow1);
        QuadCurve transitionLine1 = new QuadCurve(startX, startY, middleX, middleY, endX, endY);
        transitionLine1.setStroke(Color.BLACK);
        transitionLine1.setStrokeWidth(3);
        transitionLine1.setFill(Color.TRANSPARENT);

        Text transitionAlphabet1 = new Text("#, # -> " + PDA.PDAGrammar.start);
        transitionAlphabet1.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 16));
        transitionAlphabet1.setX(235);
        transitionAlphabet1.setY(500);

        root.getChildren().add(transitionAlphabet1);
        root.getChildren().add(transitionLine1);
        root.getChildren().add(arrowHead1);
        //endregion

        //region second state

        //region setup productions
        String[] newProductions = PDAGrammar.printGrammar().split("\n");
        for (int i = 1; i < newProductions.length; i++)
            newProductions[i] = "#, " + newProductions[i];
        String[] terminals = new String[PDAGrammar.terminals.size()];
        for (int i = 0; i < PDAGrammar.terminals.size(); i++)
            terminals[i] = PDAGrammar.terminals.get(i) + ", " + PDAGrammar.terminals.get(i) + " -> #";
        //endregion

        ImageView arrowLoop = new ImageView(image);
        arrowLoop.setFitHeight(22);
        arrowLoop.setFitWidth(22);

        arrowLoop.setX(positions.get("q1")[0] + 15);
        arrowLoop.setY(positions.get("q1")[1] - 94);

        Rotate rotatedArrowLoop = new Rotate(90, arrowLoop.getX(), arrowLoop.getY());
        arrowLoop.getTransforms().add(rotatedArrowLoop);
        Arc loop = new Arc(positions.get("q1")[0], positions.get("q1")[1] - 48.0, 75.0, 35.0, -74.0, 328.0);
        loop.setFill(Color.TRANSPARENT);
        loop.setStrokeWidth(3);
        loop.setStroke(Color.BLACK);

        int counterY = 0;
        for (String newProduction : newProductions) {
            if (!newProduction.equals("")) {
                Text transitionAlphabet = new Text(newProduction);
                transitionAlphabet.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 14));
                transitionAlphabet.setX(100);
                transitionAlphabet.setY(120 + counterY);
                counterY += 20;
                root.getChildren().add(transitionAlphabet);
            }
        }

        counterY = 0;
        for (String terminal : terminals) {
            Text transitionAlphabet = new Text(terminal);
            transitionAlphabet.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 14));
            transitionAlphabet.setX(760);
            transitionAlphabet.setY(120 + counterY);
            counterY += 20;
            root.getChildren().add(transitionAlphabet);
        }


        root.getChildren().add(loop);
        root.getChildren().add(arrowLoop);
        //endregion

        //region third state
        ImageView arrowHead2 = new ImageView(image);
        arrowHead2.setFitHeight(22);
        arrowHead2.setFitWidth(22);

        startX = positions.get("q1")[0] + 25;
        startY = positions.get("q1")[1];
        endX = positions.get("q2")[0] - 25;
        endY = positions.get("q2")[1];
        middleX = (positions.get("q1")[0] + positions.get("q2")[0]) / 2;

        Text transitionAlphabet2 = new Text("#, $ -> $");
        transitionAlphabet2.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 16));
        transitionAlphabet2.setX(640);
        transitionAlphabet2.setY(500);

        arrowHead2.setX(middleX + 15);
        arrowHead2.setY(middleY + 19);
        Rotate rotatedArrow2 = new Rotate(90, arrowHead2.getX(), arrowHead2.getY());
        arrowHead2.getTransforms().add(rotatedArrow2);
        QuadCurve transitionLine2 = new QuadCurve(startX, startY, middleX, middleY, endX, endY);
        transitionLine2.setStroke(Color.BLACK);
        transitionLine2.setStrokeWidth(3);
        transitionLine2.setFill(Color.TRANSPARENT);
        root.getChildren().add(transitionLine2);
        root.getChildren().add(arrowHead2);
        root.getChildren().add(transitionAlphabet2);
        //endregion

        Scene scene = new Scene(root, 1000, 650);
        scene.setFill(Color.rgb(206, 197, 146));
        primaryStage.setTitle("Schematic PDA");
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}