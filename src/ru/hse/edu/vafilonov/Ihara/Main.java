package ru.hse.edu.vafilonov.Ihara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;

import ru.hse.edu.vafilonov.Ihara.gui.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        BaseController.setScreenSize(screenSize);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("resources/fxml/MainFrame.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Ihara zeta calculator");
        Scene scene = new Scene(root, screenSize.width / 2.0, screenSize.height / 2.0);
        primaryStage.setScene(scene);
        BaseController.setScene(scene);
        ((Controller)loader.getController()).setListeners();


        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
