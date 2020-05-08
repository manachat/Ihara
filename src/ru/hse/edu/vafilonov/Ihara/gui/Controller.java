package ru.hse.edu.vafilonov.Ihara.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
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
    private TextField accuracyField;
    @FXML
    private TextField resultField;
    @FXML
    private Button calculateButton;
    @FXML
    private ComboBox<String> functionComboBox;
    @FXML
    private MenuItem saveAsMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem loadMenu;
    @FXML
    private MenuItem infoMenu;
    @FXML
    private MenuItem aboutMenu;

    private boolean isNodeSelected = false;
    private GraphNode selectedNode;
    private String filepath = null;

    private final double nodeRadius = 7.5;
    private final double menuBarHeight = 30;
    private final String functionHashimoto = "Hashimoto";
    private final String functionBass = "Bass";
    private final String functionMizunoSato = "Mizuno, Sato";
    private final String aboutString = "Программа разработана в рамках выполения курсовой работы ОП ПИ НИУ ВШЭ\n " +
            "Исполнитель: \n Филонов Всеволод Андреевич\n группа БПИ185";
    private Paint nodeColor = Color.RED;

    private HashMap<GraphNode, Shape> nodemap = new HashMap<>();
    private HashMap<GraphEdge, Arrow> edgemap = new HashMap<>();
    private GraphModel model;

    @FXML
    public void initialize(){
        functionComboBox.getItems().addAll(functionHashimoto, functionBass, functionMizunoSato);
        functionComboBox.setValue(functionHashimoto);
        model = new GraphModel();
        workingField.setMaxWidth(screenSize.getWidth());
        workingField.setPrefWidth(screenSize.getWidth() * 3 / 8);
        workingField.setPrefHeight(screenSize.getHeight() / 2 - menuBarHeight);
        controlBox.setMaxWidth(screenSize.getWidth());
        controlBox.setPrefWidth(screenSize.getWidth() / 8.);
        controlBox.setPrefHeight(screenSize.getHeight() / 2 - menuBarHeight);
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
        int accuracy= -1;

        try {
            re = Double.parseDouble(reText.getText());
            im = Double.parseDouble(imText.getText());
            if (!accuracyField.getText().isEmpty()){
                accuracy = Integer.parseInt(accuracyField.getText());
                if  (accuracy <= 0){
                    throw new IllegalStateException("Точность должна быть положительной.");
                }
            }
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
                msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод чисел", ButtonType.OK);
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
        resultField.setText(res.accurateToString(accuracy));
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
            GraphNode graphNode = model.addNode();
            Circle c = new Circle(x, y, nodeRadius, nodeColor);

            //--------------------------------------------------------------------------------
            attachNodeHandler(c, graphNode);
            //-------------------------------------------------------------------------------------------------

            nodemap.put(graphNode, c); //connect node with GUI via hashmap
            workingField.getChildren().add(c); //add visualisations to display
        }
    }

    @FXML
    private void saveMenuHandler(ActionEvent event){
        if (filepath != null){
            saveSession(filepath);
        }
        else {
            saveAsMenuHandler(null);
        }
    }

    @FXML
    private void saveAsMenuHandler(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save session");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IHR", "*.ihr"));
        File path = chooser.showSaveDialog(scene.getWindow());
        if (path != null){
            saveSession(path.getPath());
            filepath = path.getPath();
        }
    }

    @FXML
    private void loadMenuHandler(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save session");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IHR", "*.ihr"));
        File path = chooser.showOpenDialog(scene.getWindow());
        if (path != null){
            readSession(path.getPath());
            filepath = path.getPath();
        }
    }

    @FXML
    private void infoMenuHandler(ActionEvent event){

    }

    @FXML
    private void aboutMenuHandler(ActionEvent event){
        Alert msg = new Alert(Alert.AlertType.ERROR, aboutString, ButtonType.OK);
        msg.setTitle("About");
        msg.setHeaderText(null);
        msg.setGraphic(null);
        msg.show();
    }

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
                                GraphEdge graphEdge = model.addEdge(origin, tail);
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

    private void saveSession(String path){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            List<Arrow> savedArrows = new ArrayList<>(edgemap.size());
            for (GraphEdge e : model.getGraphEdges()) {
                savedArrows.add(edgemap.get(e));
            }
            double[] xs = new double[nodemap.size()];
            double[] ys = new double[nodemap.size()];
            int index = 0;
            for (GraphNode n : model.getGraphNodes()) {
                Circle c = (Circle) nodemap.get(n);
                xs[index] = c.getCenterX();
                ys[index] = c.getCenterY();
                index++;
            }
            SavedSession session = new SavedSession(model, savedArrows, xs, ys);
            oos.writeObject(session);
        }
        catch (IOException ioex){
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не удалось сохранить сессию.", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
        }
    }

    private void readSession(String path){
        SavedSession session;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))){
            session = (SavedSession) ois.readObject();
            GraphModel model = session.getModel();
            List<Arrow> arrows = session.getArrows();
            double[] xs = session.getCircleX();
            double[] ys = session.getCircleY();
            this.model = model;
            workingField.getChildren().clear();

            nodemap.clear();
            int index = 0;
            for (GraphNode n : model.getGraphNodes()){
                Circle c = new Circle(xs[index], ys[index], nodeRadius, nodeColor);
                attachNodeHandler(c, n);
                workingField.getChildren().add(c);
                nodemap.put(n, c);
                index++;
            }

            edgemap.clear();
            index = 0;
            for (GraphEdge e : model.getGraphEdges()){
                Arrow arc = arrows.get(index);
                if (functionComboBox.getValue().equals(functionMizunoSato)) {
                    arc.reinitialize(nodeRadius, true);
                }
                else {
                    arc.reinitialize(nodeRadius, false);
                }
                attachEdgeHandler(arc, e);
                workingField.getChildren().addAll(arc.getAllElements());
                edgemap.put(e, arc);
                index++;
            }
        }
        catch (ClassNotFoundException cnfex){
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не найден ", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
        }
        catch (IOException ioex){
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не удалось прочитать файл сессии.", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
        }
    }


}
