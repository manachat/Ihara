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
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.List;

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
        GraphNode graphNode = new GraphNode();
        Circle c = new Circle(x,y,nodeRadius,nodeColor);

        //--------------------------------------------------------------------------------
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                //delete node
                if (event.getButton() == MouseButton.SECONDARY){
                    deleteNode(graphNode); // closure on created node object TODO CLOSURE
                }//select node
                else{
                    if (!nodeSelected) { //select
                        selectedNode = graphNode; // TODO CLOSURE
                        ((Circle)event.getTarget()).setStroke(Color.BLACK);
                        nodeSelected = true;
                    }
                    else { //remove selection
                        if (selectedNode != graphNode){ //create node (or not create)TODO CLOSURE
                            GraphEdge conn = GraphNode.getConnection(selectedNode, graphNode);
                            if (conn == null) {//nodes not connected
                                //CREATE EDGE
                                GraphNode origin = selectedNode;
                                GraphNode tail = graphNode;
                                GraphEdge graphEdge = new GraphEdge(origin, tail);
                                Circle oCircle = (Circle) nodemap.get(origin);
                                Circle tCircle = (Circle) nodemap.get(tail);
                                Shape arc = constructArrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                        tCircle.getCenterX(), tCircle.getCenterY());


                                //-------------------------------------------------------------------------
                                arc.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        if (mouseEvent.getButton() == MouseButton.PRIMARY) { //set weight
                                            //TODO implement, dialog maybe
                                        }
                                        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { //delete
                                            deleteEdge(graphEdge); // TODO CLOSURE
                                        }
                                        mouseEvent.consume();
                                    }
                                });
                                //--------------------------------------------------------------------------

                                workingField.getChildren().add(arc);
                                model.addEdge(graphEdge);
                                edgemap.put(graphEdge, arc);
                            }
                            else{
                                //TODO alert dialog
                            }
                            ((Circle)nodemap.get(selectedNode)).setStroke(null);
                            nodeSelected = false;
                        }
                    }
                }
                event.consume();
            }
        });
        //-------------------------------------------------------------------------------------------------

        nodemap.put(graphNode, c); //connect node with GUI via hashmap
        model.addNode(graphNode); //add node to graph
        workingField.getChildren().add(c); //add visualisations to display
    }

    private boolean nodeSelected = false;
    private GraphNode selectedNode;

    private double nodeRadius = 7.5;
    private Paint nodeColor = Color.RED;

    HashMap<GraphNode, Shape> nodemap = new HashMap<>();
    HashMap<GraphEdge, Shape> edgemap = new HashMap<>();
    private GraphModel model = new GraphModel(workingField);

    private void createEdge(){

    }


    private void deleteNode(GraphNode node){
        Shape figure = nodemap.remove(node); //remove from map
        // removal of GUI components
        workingField.getChildren().remove(figure);
        List<GraphEdge> adjacentEdges = node.getConnections(); //get node edges to find reps
        for (GraphEdge e : adjacentEdges){ //remove arrows from user screen
            Shape arc = edgemap.remove(e); //remove from map
            workingField.getChildren().remove(arc);
        }

        // removal of model components
        model.removeNode(node);
    }

    private void deleteEdge(GraphEdge edge){
        //removal of GUI components
        Shape figure = edgemap.remove(edge);
        workingField.getChildren().remove(figure);
        //removal of model components
        model.removeEdge(edge);
    }

    private Shape constructArrow(double x1, double y1, double x2, double y2){
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
        double length = Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
        vectorX /= length;
        vectorY /= length;
        x1 += nodeRadius * vectorX;
        y1 += nodeRadius * vectorY;
        x2 -= nodeRadius * vectorX;
        y2 -= nodeRadius * vectorY;
        Line mainLine = new Line(x1, y1, x2, y2);
        mainLine.setStrokeWidth(2.5);
        return mainLine;
    }

}
