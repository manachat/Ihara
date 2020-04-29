package ru.hse.edu.vafilonov.Ihara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainFrame.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Ihara calculator(name temporary)");
        Scene scene = new Scene(root, 1000, 400);
        primaryStage.setScene(scene);
        BaseController.setScene(scene);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        BaseController.setScreenSize(screenSize);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
