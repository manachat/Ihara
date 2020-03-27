package ru.hse.edu.vafilonov.Ihara;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controller {
    @FXML
    private Canvas mainCanvas;

    private final int nodeRadius = 5;

    @FXML
    private AnchorPane workingField;

    @FXML
    private void workingFieldClickHandler(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        Circle c = new Circle(x, y, nodeRadius, Color.RED);

        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                if (event.getButton() == MouseButton.SECONDARY){
                    workingField.getChildren().remove(event.getTarget());
                }

                event.consume();
            }
        });

        workingField.getChildren().add(c);
    }

}
