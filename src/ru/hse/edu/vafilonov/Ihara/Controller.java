package ru.hse.edu.vafilonov.Ihara;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Controller extends BaseController{
    @FXML
    private Canvas mainCanvas;
    @FXML
    private AnchorPane workingField;

    @FXML
    private void workingFieldClickHandler(MouseEvent e){
        if (e.getButton() == MouseButton.SECONDARY){
            return;
        }

        double x = e.getX();
        double y = e.getY();

        createNode(x, y);
    }

    private boolean nodeSelected = false;
    private GraphNode selectedNode;

    private int nodeRadius = 5;
    private Paint nodeColor = Color.RED;

    private GraphModel model = new GraphModel(workingField);

    private void createNode(double x, double y){
        Round round = new Round(x, y, nodeRadius, nodeColor);
        GraphNode graphNode = new GraphNode(round, model);
        round.getCircle().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                //delete node
                if (event.getButton() == MouseButton.SECONDARY){
                    model.removeNode(graphNode);
                }//select node
                else{
                    if (!nodeSelected) {
                        selectedNode = graphNode;
                        ((Circle)event.getTarget()).setStroke(Color.YELLOW);
                        nodeSelected = true;
                    }
                    else {
                        if (selectedNode == graphNode){ //remove selection
                            ((Circle)event.getTarget()).setStroke(nodeColor);
                        }
                        else { //create node
                            GraphNode origin = selectedNode;
                            GraphNode tail = graphNode;
                            Circle oCircle = (Circle)origin.getRepresentation().getComponents().get(0);
                            Circle tCircle = (Circle)tail.getRepresentation().getComponents().get(0);
                            Arc arc = new Arc(oCircle.getCenterX(), oCircle.getCenterY(), tCircle.getCenterX(), tCircle.getCenterY());
                            GraphEdge edge = new GraphEdge(arc, model, origin, tail);
                            nodeSelected = false;
                        }
                    }
                }
                event.consume();
            }
        });

        model.addNode(graphNode);
    }

}
