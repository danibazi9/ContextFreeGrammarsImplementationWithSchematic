package Project2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainPage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = (Parent)loader.load();
        MainController controller = (MainController) loader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
//        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Context-Free Grammars");
        primaryStage.getIcons().add(new Image("src/Project2/pictures/automation.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
