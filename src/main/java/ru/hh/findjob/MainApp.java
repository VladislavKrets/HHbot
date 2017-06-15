package ru.hh.findjob;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Created by lollipop on 13.06.2017.
 */
public class MainApp extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("glass.accessible.force", "false");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/findjob.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setTitle("HH bot");
        primaryStage.setScene(scene);

        primaryStage.show();

        MainController controller = loader.getController();
        controller.setMainApp(this);



    }
}
