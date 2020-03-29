package ru.hse.edu.vafilonov.Ihara;

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
    private final MainModel model = new MainModel();

    @FXML
    private AnchorPane workingField;

    @FXML
    private void workingFieldClickHandler(MouseEvent e){
        if (e.getButton() == MouseButton.SECONDARY){
            return;
        }

        double x = e.getX();
        double y = e.getY();

        addNewCircle(x, y);
    }

    private void addNewCircle(double x, double y){
        Circle c = new Circle(x, y, nodeRadius, Color.RED);
        GraphNode graphNode = new GraphNode(c);
        model.addElement(graphNode);

        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                //delete node
                if (event.getButton() == MouseButton.SECONDARY){
                    workingField.getChildren().remove(event.getTarget());
                }//select node
                else{

                }
                event.consume();
            }
        });

        workingField.getChildren().add(c);
    }

}
