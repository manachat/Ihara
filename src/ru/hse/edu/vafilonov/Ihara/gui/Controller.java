package ru.hse.edu.vafilonov.Ihara.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.hse.edu.vafilonov.Ihara.model.*;

public class Controller extends BaseController{
    @FXML
    private AnchorPane workingField;

    @FXML
    private VBox controlBox;

    @FXML
    private TextField reText;

    @FXML
    private TextField imText;

    @FXML
    private Label resultLabel;

    @FXML
    private Button calculateButton;

    @FXML
    private ComboBox<String> functionComboBox;

    @FXML
    public void initialize(){
        functionComboBox.getItems().addAll(functionHashimoto, functionBass, functionMizunoSato);
        functionComboBox.setValue(functionHashimoto);
        model = new GraphModel();
        workingField.setMaxWidth(screenSize.getWidth());
        workingField.setPrefWidth(screenSize.getWidth() * 3 / 8);
        controlBox.setMaxWidth(screenSize.getWidth());
        controlBox.setPrefWidth(screenSize.getWidth() / 8.);
    }

    public void setListeners(){
        /*
        listener for value of selected function
        changes shape of arrows according to function type
         */
        functionComboBox.valueProperty().addListener(property -> {
            if (model.getGraphEdges().size() == 0){
                return; //nothing to change
            }
            if (functionComboBox.getValue().equals(functionMizunoSato)){
                for (GraphEdge e : model.getGraphEdges()){
                    Arrow arc = edgemap.get(e);
                    arc.setOrientationVisibility(true);
                }
            }
            else{
                for (GraphEdge e : model.getGraphEdges()){
                    Arrow arc = edgemap.get(e);
                    arc.setOrientationVisibility(false);
                }
            }
        });
        //resize panels
        scene.widthProperty().addListener(property -> {
            double newWidth = scene.getWidth();
            workingField.setPrefWidth(newWidth * 3 / 4);
            controlBox.setPrefWidth(newWidth / 4);
        });

        scene.heightProperty().addListener(property -> {
            double newHeight = screenSize.getHeight();
            workingField.setPrefHeight(newHeight - menuBarHeight);
            controlBox.setPrefHeight(newHeight - menuBarHeight);
        });
    }

    @FXML
    private void calculateButtonClickHandler(MouseEvent e){
        if (reText.getText().isEmpty() || imText.getText().isEmpty()){
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не задан аргумент функции", ButtonType.OK);
            msg.setTitle("Error");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return;
        }

        double re;
        double im;

        try {
            re = Double.parseDouble(reText.getText());
            im = Double.parseDouble(imText.getText());
            if (!model.checkConnectivity()){
                Alert msg = new Alert(Alert.AlertType.ERROR, "Граф должен быть связным.", ButtonType.OK);
                msg.setTitle("Внимание");
                msg.setHeaderText(null);
                msg.setGraphic(null);
                msg.show();
                return;
            }
        }
        catch (NumberFormatException | IllegalStateException nex){
            Alert msg;
            if (nex instanceof NumberFormatException) {
                msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод", ButtonType.OK);
            }
            else {
                msg = new Alert(Alert.AlertType.ERROR, nex.getMessage(), ButtonType.OK);
            }
            msg.setTitle("Error");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return;
        }

        ComplexNumber res = null;
        switch (functionComboBox.getValue()){
            case functionHashimoto:
                res = model.calculateZetaTheoremOneA(new ComplexNumber(re, im));
                break;
            case functionBass:
                res = model.calculateZetaTheoremOneB(new ComplexNumber(re, im));
                break;
            case functionMizunoSato:
                try {
                    res = model.calculateZetaTheoremThree(new ComplexNumber(re, im));
                }
                catch (ArithmeticException arex){
                    Alert msg = new Alert(Alert.AlertType.ERROR, arex.getMessage(), ButtonType.OK);
                    msg.setTitle("Error");
                    msg.setHeaderText(null);
                    msg.setGraphic(null);
                    msg.show();
                    return;
                }
                break;
        }
        resultLabel.setText(res.toString());
    }

    @FXML
    private void workingFieldClickHandler(MouseEvent e){
        if (e.getButton() == MouseButton.SECONDARY){
            if (isNodeSelected){
                ((Circle) nodemap.get(selectedNode)).setStroke(null);
                isNodeSelected = false;
            }
            return;
        }
        else { //create new node
            double x = e.getX();
            double y = e.getY();
            GraphNode graphNode = new GraphNode();
            Circle c = new Circle(x, y, nodeRadius, nodeColor);

            //--------------------------------------------------------------------------------
            attachNodeHandler(c, graphNode);
            //-------------------------------------------------------------------------------------------------

            nodemap.put(graphNode, c); //connect node with GUI via hashmap
            model.addNode(graphNode); //add node to graph
            workingField.getChildren().add(c); //add visualisations to display
        }
    }

    private boolean isNodeSelected = false;
    private GraphNode selectedNode;

    private final double nodeRadius = 7.5;
    private final double menuBarHeight = 30;
    private final String functionHashimoto = "Hashimoto";
    private final String functionBass = "Bass";
    private final String functionMizunoSato = "Mizuno,Sato";
    private Paint nodeColor = Color.RED;

    private HashMap<GraphNode, Shape> nodemap = new HashMap<>();
    private HashMap<GraphEdge, Arrow> edgemap = new HashMap<>();
    private GraphModel model;

    private void deleteNode(GraphNode node){
        Shape figure = nodemap.remove(node); //remove from map
        // removal of GUI components
        workingField.getChildren().remove(figure);
        List<GraphEdge> adjacentEdges = node.getConnections(); //get node edges to find reps
        for (GraphEdge e : adjacentEdges){ //remove arrows from user screen
            Arrow arc = edgemap.remove(e); //remove from map
            workingField.getChildren().removeAll(arc.getAllElements());
        }
        // removal of model components
        model.removeNode(node);
    }

    private void deleteEdge(GraphEdge edge){
        //removal of GUI components
        Arrow figure = edgemap.remove(edge);
        workingField.getChildren().removeAll(figure.getAllElements());
        //removal of model components
        model.removeEdge(edge);
    }


    /**
     * Helper method
     * Sets click handler for created circle
     * @param circle circle shape
     * @param graphNode associated node object
     */
    private void attachNodeHandler(Shape circle, GraphNode graphNode){
        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                //delete node
                if (event.getButton() == MouseButton.SECONDARY){
                    deleteNode(graphNode); // closure on created node object TODO CLOSURE
                }//select node
                else{
                    if (!isNodeSelected) { //select
                        selectedNode = graphNode; // TODO CLOSURE
                        ((Circle)event.getTarget()).setStroke(Color.BLACK);
                        isNodeSelected = true;
                    }
                    else {
                        if (selectedNode != graphNode){ //create edge (or not create)TODO CLOSURE
                            GraphEdge conn = GraphNode.getConnection(selectedNode, graphNode);
                            if (conn == null) {//nodes not connected
                                //CREATE EDGE
                                GraphNode origin = selectedNode;
                                GraphNode tail = graphNode;
                                GraphEdge graphEdge = new GraphEdge(origin, tail);
                                Circle oCircle = (Circle) nodemap.get(origin);
                                Circle tCircle = (Circle) nodemap.get(tail);
                                Arrow arc;
                                if (functionComboBox.getValue().equals(functionMizunoSato)){ //weighted func
                                    arc = new Arrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                            tCircle.getCenterX(), tCircle.getCenterY(), nodeRadius, true, 0.0);
                                }
                                else {
                                    arc = new Arrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                            tCircle.getCenterX(), tCircle.getCenterY(), nodeRadius, false, 0.0);
                                }

                                //-------------------------------------------------------------------------
                                attachEdgeHandler(arc, graphEdge);
                                //--------------------------------------------------------------------------

                                workingField.getChildren().addAll(arc.getAllElements());
                                model.addEdge(graphEdge);
                                edgemap.put(graphEdge, arc);
                            }
                            else{ //connection already exists
                                Alert msg = new Alert(Alert.AlertType.ERROR, "Узлы уже соединены", ButtonType.OK);
                                msg.setTitle("Предупреждение");
                                msg.setHeaderText(null);
                                msg.setGraphic(null);
                                msg.show();
                            }
                        }
                        if (!event.isControlDown()) {
                            ((Circle) nodemap.get(selectedNode)).setStroke(null);
                            isNodeSelected = false;
                        }

                    }
                }
                event.consume();
            }
        });
    }

    /**
     * Helper method
     * Sets click handler for created arc
     * @param arc arc shape
     * @param graphEdge associated edge object
     */
    private void attachEdgeHandler(Arrow arc, GraphEdge graphEdge){
        var handler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                        functionComboBox.getValue().equals(functionMizunoSato)) { //set weight
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setHeaderText(null);
                    dialog.setGraphic(null);
                    dialog.setTitle("Введите вес");
                    dialog.setContentText("Вес: ");

                    Optional<String> result = dialog.showAndWait();
                    double w;
                    if (result.isPresent()) {
                        try {
                            w = Double.parseDouble(result.get());
                        }
                        catch (NumberFormatException nex){
                            Alert msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод", ButtonType.OK);
                            msg.setTitle("Error");
                            msg.setHeaderText(null);
                            msg.setGraphic(null);
                            msg.show();
                            w = 0.0;
                        }
                        graphEdge.setWeight(w); //changes edge's weight
                        Text oldText = arc.getWeightText();
                        workingField.getChildren().remove(oldText); //remove old arc from gui
                        arc.setWeightText(w); //set new text
                        workingField.getChildren().add(arc.getWeightText()); //add new text
                    }
                }
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) { //delete
                    deleteEdge(graphEdge); // TODO CLOSURE
                }
            }
        };
        for (Shape s : arc.getAllElements()){
            s.setOnMouseClicked(handler);
        }
    }


}
