package ru.hse.edu.vafilonov.Ihara.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private Canvas mainCanvas;

    @FXML
    private AnchorPane workingField;

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
        //TODO приделать листнер который заменит отрисовки ребер на вариант с числами
        functionComboBox.getItems().addAll(functionHashimoto, functionBass, functionMizunoSato);
        functionComboBox.setValue(functionHashimoto);
        model = new GraphModel(workingField);
    }

    @FXML
    private void fireButtonClickHandler(MouseEvent e){
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
            return;
        }

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

    private boolean nodeSelected = false;
    private GraphNode selectedNode;

    private final double nodeRadius = 7.5;
    private final double arrowWing = 10;
    private final double cosWing = Math.cos(Math.PI / 12);
    private final double sinWing = Math.sin(Math.PI / 12);
    private final String functionHashimoto = "Hashimoto";
    private final String functionBass = "Bass";
    private final String functionMizunoSato = "Mizuno,Sato";
    private Paint nodeColor = Color.RED;

    private HashMap<GraphNode, Shape> nodemap = new HashMap<>();
    private HashMap<GraphEdge, Shape> edgemap = new HashMap<>();
    private GraphModel model;

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

    private Shape constructArrow(double x1, double y1, double x2, double y2, boolean weighted, double value){
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
        double length = Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
        vectorX /= length;
        vectorY /= length;
        x1 += (nodeRadius + 2) * vectorX;
        y1 += (nodeRadius + 2) * vectorY;
        x2 -= (nodeRadius + 2) * vectorX;
        y2 -= (nodeRadius + 2) * vectorY;
        Line mainLine = new Line(x1, y1, x2, y2);
        mainLine.setStrokeWidth(2.5);
        if (weighted) {
            double xWproj = -arrowWing * vectorX;
            double yWproj = -arrowWing * vectorY;
            double x = xWproj * cosWing - yWproj * sinWing;
            double y = xWproj * sinWing + yWproj * cosWing;
            Line firstwing = new Line(x2, y2, x2 + x, y2 + y);
            x = xWproj * cosWing + yWproj * sinWing;
            y = -xWproj * sinWing + yWproj * cosWing;
            Line secondwing = new Line(x2, y2, x2 + x, y2 + y);
            firstwing.setStrokeWidth(2);
            secondwing.setStrokeWidth(2);
            Text weight = new Text((x1 + x2)/2., (y1 + y2)/2., String.format("%.3f", value));
            weight.setFont(new Font(20));
            Shape arrow = Shape.union(mainLine, Shape.union(firstwing, secondwing));
            List<PathElement> els = ((Path)arrow).getElements();
            return Shape.union(arrow, weight);
        }
        else{
            return mainLine;
        }
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
                    if (!nodeSelected) { //select
                        selectedNode = graphNode; // TODO CLOSURE
                        ((Circle)event.getTarget()).setStroke(Color.BLACK);
                        nodeSelected = true;
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
                                Shape arc;
                                if (functionComboBox.getValue().equals(functionMizunoSato)){ //weighted func
                                    arc = constructArrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                            tCircle.getCenterX(), tCircle.getCenterY(), true, 0.0);
                                }
                                else {
                                    arc = constructArrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                            tCircle.getCenterX(), tCircle.getCenterY(), false, 0.0);
                                }

                                //-------------------------------------------------------------------------
                                attachEdgeHandler(arc, graphEdge);
                                //--------------------------------------------------------------------------

                                workingField.getChildren().add(arc);
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
                        ((Circle)nodemap.get(selectedNode)).setStroke(null);
                        nodeSelected = false;

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
    private void attachEdgeHandler(Shape arc, GraphEdge graphEdge){
        arc.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
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
                        workingField.getChildren().remove(arc); //remove old arc from gui
                        //replace old arc with a new one
                        Circle oCircle = (Circle) nodemap.get(graphEdge.getOrigin());
                        Circle tCircle = (Circle) nodemap.get(graphEdge.getTail());
                        Shape newarc = constructArrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                tCircle.getCenterX(), tCircle.getCenterY(), true, graphEdge.getWeight());
                        attachEdgeHandler(newarc, graphEdge);
                        edgemap.replace(graphEdge, newarc);
                        workingField.getChildren().add(newarc);
                    }
                    else {
                        return;
                    }
                }
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) { //delete
                    deleteEdge(graphEdge); // TODO CLOSURE
                }
                mouseEvent.consume();
            }
        });
    }


}
