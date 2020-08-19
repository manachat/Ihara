package ru.hse.edu.vafilonov.ihara.gui;

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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import ru.hse.edu.vafilonov.ihara.model.*;

/**
 * Controller class for main application page.
 * Contains methods for UI and model manipulations.
 *
 * @apiNote setListeners() method should ALWAYS be used
 *          AFTER setScene and setScreenSize methods and
 *          BEFORE application interactions as it sets
 *          values which initialization is impossible
 *          in initialize() method.
 * @see ru.hse.edu.vafilonov.ihara.gui.BaseController
 * @version 2
 * @author Filonov Vsevolod
 */
public class MainController extends BaseController{

    /*
    Application constants
     */

    /**
     * radius for node circle
     */
    private static final double NODE_RADIUS = 7.5;

    /**
     * height of menu bar
     */
    private static final double MENU_BAR_HEIGHT = 30;

    /* strings with function names*/
    private static final String FUNCTION_HASHIMOTO = "Hashimoto";
    private static final String FUNCTION_BASS = "Bass";
    private static final String FUNCTION_MIZUNO_SATO = "Mizuno, Sato";


    private static final String ABOUT_STRING = "Программа разработана в рамках выполения курсовой работы ОП ПИ НИУ ВШЭ\n " +
            "Исполнитель: \n Филонов Всеволод Андреевич\n группа БПИ185";
    private static Paint nodeColor = Color.RED;

    private static final String HELP_STRING = "Программа вычисляет обратную дзета-функцию. \n" +
            "Для вычисления необходимо ввести аргумент функции, выбрать вид функции, при необходимости указать веса ребер.\n" +
            "Точность округления задается целым числом в графе \"Точность\". Пустое поле означает точность по умолчанию (8 знаков)" +
            "Для расчета функции необходимо нажать кнопку \"Расчитать\". Результат появится в графе ниже.\n";

    /* selection controls*/
    /**
     * shows if there is currently selected node
     */
    private boolean isNodeSelected = false;

    /**
     * currently selected node reference
     */
    private GraphNode selectedNode;

    /**
     * path to most recently set file
     */
    private String filepath = null;

    /*
    FXML file references
     */

    /* drawing field */

    /**
     * field for graph elements visualization
     */
    @FXML
    private AnchorPane workingField;

    /* controls */

    /**
     * contains control elements of UI
     */
    @FXML
    private VBox controlBox;

    /**
     * field for Re value reading
     */
    @FXML
    private TextField reText;

    /**
     * field for Im value reading
     */
    @FXML
    private TextField imText;

    /**
     * Checkbox for symbolic computation
     */
    @FXML
    private CheckBox symbolicCheckbox;

    /**
     * field for accuracy value reading
     */
    @FXML
    private TextField accuracyField;

    /**
     * field for result value writing
     */
    @FXML
    private TextField resultField;

    /**
     * button for calculation start
     */
    @FXML
    private Button calculateButton;

    /**
     * field for function choice
     */
    @FXML
    private ComboBox<String> functionComboBox;

    /* Menu controls */

    /**
     * button for Save as.. operation
     */
    @FXML
    private MenuItem saveAsMenu;

    /**
     * button for Save operation
     */
    @FXML
    private MenuItem saveMenu;

    /**
     * button for load operation
     */
    @FXML
    private MenuItem loadMenu;

    /**
     * button for additional info representation
     */
    @FXML
    private MenuItem infoMenu;

    /**
     * button for representation of program info
     */
    @FXML
    private MenuItem aboutMenu;

    /* model-to-view mappings*/
    /**
     * HashMap, connects node' models to views
     * @see HashMap
     */
    private HashMap<GraphNode, Shape> nodemap = new HashMap<>();

    /**
     * HashMap, connects edges' models to views
     */
    private HashMap<GraphEdge, Arrow> edgemap = new HashMap<>();

    /**
     * reference to graph model
     */
    private GraphModel model;

    /**
     * initializer for controller
     * sets UI components initial sizes
     * invoked by FXMLloader
     * @see javafx.fxml.FXMLLoader
     */
    @FXML
    public void initialize(){
        // comboBox items
        functionComboBox.getItems().addAll(FUNCTION_HASHIMOTO, FUNCTION_BASS, FUNCTION_MIZUNO_SATO);
        functionComboBox.setValue(FUNCTION_HASHIMOTO);

        //model creation
        model = new GraphModel();

        //UI sizes
        workingField.setMaxWidth(screenSize.getWidth());
        workingField.setPrefWidth(screenSize.getWidth() * 3 / 8);
        workingField.setPrefHeight(screenSize.getHeight() / 2 - MENU_BAR_HEIGHT);
        controlBox.setMaxWidth(screenSize.getWidth());
        controlBox.setPrefWidth(screenSize.getWidth() / 8.);
        controlBox.setPrefHeight(screenSize.getHeight() / 2 - MENU_BAR_HEIGHT);
    }

    /**
     * Sets listeners for UI elements
     */
    public void setListeners(){
        /*
        listener for value of selected function
        changes shape of arrows according to function type
         */
        functionComboBox.valueProperty().addListener(property -> {
            if (model.getGraphEdges().size() == 0) {
                return;     //nothing to change
            }

            if (functionComboBox.getValue().equals(FUNCTION_MIZUNO_SATO)) {    //weighted function
                for (GraphEdge e : model.getGraphEdges()){
                    Arrow arc = edgemap.get(e);
                    arc.setOrientationVisibility(true);
                }
            } else {                                                           //not weighted functions
                for (GraphEdge e : model.getGraphEdges()){
                    Arrow arc = edgemap.get(e);
                    arc.setOrientationVisibility(false);
                }
            }
        });

        /* panels resize */
        scene.widthProperty().addListener(property -> {     // width property
            double newWidth = scene.getWidth();
            workingField.setPrefWidth(newWidth * 3 / 4);
            controlBox.setPrefWidth(newWidth / 4);
        });
        scene.heightProperty().addListener(property -> {    // height property
            double newHeight = screenSize.getHeight();
            workingField.setPrefHeight(newHeight - MENU_BAR_HEIGHT);
            controlBox.setPrefHeight(newHeight - MENU_BAR_HEIGHT);
        });
    }

    /*
    Handlers for UI controls
     */

    /**
     * Handler for mouse clicks on graph field
     * changes UI and model accordingly
     * @param e event properties
     */
    @FXML
    private void workingFieldClickHandler(MouseEvent e){
        // right mouse button
        if (e.getButton() == MouseButton.SECONDARY) {
            // clears selection of node
            if (isNodeSelected) {
                ((Circle) nodemap.get(selectedNode)).setStroke(null);
                isNodeSelected = false;
            }
        } else { // left mouse button, create new node
            // read coordinates
            double x = e.getX();
            double y = e.getY();

            // create model
            GraphNode graphNode = model.addNode();
            // create view
            Circle c = new Circle(x, y, NODE_RADIUS, nodeColor);

            // set handler for view
            attachNodeHandler(c, graphNode);

            nodemap.put(graphNode, c);              //connect node with GUI via hashmap
            workingField.getChildren().add(c);      //add visualisations to display
        }
    }

    /**
     * click handler for calculation button
     * reads necessary values, calculates
     * function and displays result
     * @param e event properties
     */
    @FXML
    private void calculateButtonClickHandler(MouseEvent e){
        // function argument was not set
        if (reText.getText().isEmpty() || imText.getText().isEmpty()) {
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

        // read values form text fields and inform user in case or errors
        try {
            re = Double.parseDouble(reText.getText());
            im = Double.parseDouble(imText.getText());
            if (!accuracyField.getText().isEmpty()) {        // accuracy was set
                accuracy = Integer.parseInt(accuracyField.getText());
                if  (accuracy <= 0){
                    throw new IllegalStateException("Точность должна быть положительной.");
                }
            }
            if (!model.checkConnectivity()) {                // graph is not connected
                Alert msg = new Alert(Alert.AlertType.ERROR,
                        "Граф должен быть связным.", ButtonType.OK);
                msg.setTitle("Внимание");
                msg.setHeaderText(null);
                msg.setGraphic(null);
                msg.show();
                return;
            }
        } catch (NumberFormatException | IllegalStateException nex){ // incorrect input
            Alert msg;
            if (nex instanceof NumberFormatException) {
                msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод чисел", ButtonType.OK);
            } else {
                msg = new Alert(Alert.AlertType.ERROR, nex.getMessage(), ButtonType.OK);
            }
            msg.setTitle("Error");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return;
        }

        ComplexNumber res;  // result value
        try {
            switch (functionComboBox.getValue()) {       // choice of function
                case FUNCTION_HASHIMOTO:
                    res = model.calculateZetaHashimoto(new ComplexNumber(re, im));
                    break;
                case FUNCTION_BASS:
                    res = model.calculateZetaBass(new ComplexNumber(re, im));
                    break;
                case FUNCTION_MIZUNO_SATO:
                    res = model.calculateZetaMizunoSato(new ComplexNumber(re, im));
                    break;
                default:
                    return;
            }
        } catch (ArithmeticException | IllegalStateException exception) {    // inform user about errors
            Alert msg = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
            msg.setTitle("Error");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return;
        }

        resultField.setText(res.accurateToString(accuracy));    // show result with given accuracy
    }

    /**
     * Handler for "Save" menu option
     * Rewrites previously set session file
     * Otherwise, invokes "Save as..." handler.
     * @param event event properties
     */
    @FXML
    private void saveMenuHandler(ActionEvent event){
        if (filepath != null) {  // file path was set previously
                saveSession(filepath);  // try to save session
        } else {                  // file wasn't set previously
            saveAsMenuHandler(null);
        }
    }

    /**
     * Handler for "Save as.." menu option"
     * Asks user to choose file path
     * and saves session into it
     * @param event event properties
     */
    @FXML
    private void saveAsMenuHandler(ActionEvent event){
        // choose filepath
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save session");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IHR", "*.ihr"));
        File path = chooser.showSaveDialog(scene.getWindow());

        // user haven't aborted choice
        if (path != null) {
            if (saveSession(path.getPath())) {  // try to save session
                filepath = path.getPath();      //set filepath in case of success
            }
        }
    }

    /**
     * Handler for "Load" menu option
     * Loads previously saved session from file
     * @param event event properties
     */
    @FXML
    private void loadMenuHandler(ActionEvent event){
        // choose filepath
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save session");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IHR", "*.ihr"));
        File path = chooser.showOpenDialog(scene.getWindow());

        // user haven't aborted choice
        if (path != null) {
            if (readSession(path.getPath())) {  // try to read session file
                filepath = path.getPath();      // save filepath in case of success
            }
        }
    }

    /**
     * Handler for info menu option
     * @param event event properties
     */
    @FXML
    private void infoMenuHandler(ActionEvent event){
        Alert msg = new Alert(Alert.AlertType.ERROR, HELP_STRING, ButtonType.OK);
        msg.setTitle("Help");
        msg.setHeaderText(null);
        msg.setGraphic(null);
        msg.show();
    }

    /**
     * Handler for about menu option
     * @param event event properties
     */
    @FXML
    private void aboutMenuHandler(ActionEvent event){
        // show content of aboutString
        Alert msg = new Alert(Alert.AlertType.ERROR, ABOUT_STRING, ButtonType.OK);
        msg.setTitle("About");
        msg.setHeaderText(null);
        msg.setGraphic(null);
        msg.show();
    }

    /*
    Helper methods
     */

    /**
     * Deletes node from both model and view
     * @param node node model reference
     */
    private void deleteNode(GraphNode node){
        Shape figure = nodemap.remove(node); //remove from map

        // removal of GUI components
        workingField.getChildren().remove(figure);
        List<GraphEdge> adjacentEdges = node.getConnections(); // get node edges to find reps
        for (GraphEdge e : adjacentEdges) { // remove arrows from user screen
            Arrow arc = edgemap.remove(e);                              // remove from map
            workingField.getChildren().removeAll(arc.getAllElements()); // remove form view
        }
        // removal of model components
        model.removeNode(node);
    }

    /**
     * Deletes edge from both model and view
     * @param edge edge model reference
     */
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
        circle.setOnMouseClicked((EventHandler<MouseEvent>) event -> {
            if (event.getButton() == MouseButton.SECONDARY) {   // right mouse button click, delete node
                deleteNode(graphNode);
            } else {                                            // left mouse click
                if (!isNodeSelected) {  // select node
                    selectedNode = graphNode;
                    ((Circle)event.getTarget()).setStroke(Color.BLACK);
                    isNodeSelected = true;
                } else {                // node already selected
                    if (selectedNode != graphNode){ // node is not the same
                        GraphEdge conn = GraphNode.getConnection(selectedNode, graphNode);
                        if (conn == null) {     // nodes not connected
                            // create edge model
                            GraphNode origin = selectedNode;
                            GraphNode tail = graphNode;
                            GraphEdge graphEdge = model.addEdge(origin, tail);
                            Circle oCircle = (Circle) nodemap.get(origin);
                            Circle tCircle = (Circle) nodemap.get(tail);
                            Arrow arc;
                            // create arc view
                            if (functionComboBox.getValue().equals(FUNCTION_MIZUNO_SATO)) {    //weighted func
                                arc = new Arrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                        tCircle.getCenterX(), tCircle.getCenterY(), NODE_RADIUS,
                                        true, "0.0");
                            } else {                                                        // not weighted func
                                arc = new Arrow(oCircle.getCenterX(), oCircle.getCenterY(),
                                        tCircle.getCenterX(), tCircle.getCenterY(), NODE_RADIUS,
                                        false, "0.0");
                            }

                            // attach handler for edge
                            attachEdgeHandler(arc, graphEdge);

                            // add view
                            workingField.getChildren().addAll(arc.getAllElements());

                            // link model and view
                            edgemap.put(graphEdge, arc);
                        } else {                 //connection already exists, notify user
                            Alert msg = new Alert(Alert.AlertType.ERROR, "Узлы уже соединены", ButtonType.OK);
                            msg.setTitle("Предупреждение");
                            msg.setHeaderText(null);
                            msg.setGraphic(null);
                            msg.show();
                        }
                    }
                    // control key was pressed, don't clear selection
                    if (!event.isControlDown()) {
                        ((Circle) nodemap.get(selectedNode)).setStroke(null);
                        isNodeSelected = false;
                    }
                }
            }
            event.consume();    // consume event
        });
    }

    /**
     * Helper method
     * Sets click handler for created arc
     * @param arc arc shape
     * @param graphEdge associated edge object
     */
    private void attachEdgeHandler(Arrow arc, GraphEdge graphEdge) {
        var handler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();   // consume event
                if ((mouseEvent.getButton() == MouseButton.PRIMARY)
                        && (functionComboBox.getValue().equals(FUNCTION_MIZUNO_SATO))) { //set weight
                    // ask user to set weight
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setHeaderText(null);
                    dialog.setGraphic(null);
                    dialog.setTitle("Введите вес");
                    dialog.setContentText("Вес: ");

                    Optional<String> result = dialog.showAndWait();

                    // user haven't aborted entry
                    if (result.isPresent()) {
                        if (!checkWeight(result.get())) {
                            // notify user
                            Alert msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод", ButtonType.OK);
                            msg.setTitle("Error");
                            msg.setHeaderText(null);
                            msg.setGraphic(null);
                            msg.show();
                            return;
                        }
                        /* OLD VERSION

                        try {                               // try to read value
                            w = Double.parseDouble(result.get());
                        }
                        catch (NumberFormatException nex){  // incorrect input
                            // notify user
                            Alert msg = new Alert(Alert.AlertType.ERROR, "Некорректный ввод", ButtonType.OK);
                            msg.setTitle("Error");
                            msg.setHeaderText(null);
                            msg.setGraphic(null);
                            msg.show();
                            return; // abort
                        }
                        */
                        graphEdge.setWeight(result.get()); //changes edge's weight
                        /* remove old text from view and replace with new */
                        Text oldText = arc.getWeightText();                     // get old text
                        workingField.getChildren().remove(oldText);             // remove old text from gui
                        arc.setWeightText(result.get());                                   // set new text
                        workingField.getChildren().add(arc.getWeightText());    // add new text to gui
                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) { //delete edge
                    deleteEdge(graphEdge);
                }
            }
        };  // end of handler

        for (Shape s : arc.getAllElements()){   // set handler to all elements
            s.setOnMouseClicked(handler);
        }
    }

    private boolean checkWeight(String input) {
        if (input.charAt(0) != 's') {
            try {                               // try to read value
                Double.parseDouble(input);
                return true;
            }
            catch (NumberFormatException nex){  // incorrect input
                return false; // abort
            }
        } else {    // check for sqrt(n)
            if (input.substring(0,5).equals("sqrt(")) {
                int end = input.indexOf(')');
                if (end == -1) {
                    return false;
                }

                try {
                    int weight = Integer.parseInt(input.substring(5, end));
                    if (weight < 1) {
                        return false;
                    }
                    return true;
                } catch (NumberFormatException nex) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Helper method. saves session to .ihr serialization file
     * @param path path to file
     * @return true in case of success
     */
    private boolean saveSession(String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) { // open IO stream
            // get saved info
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
            // create session object
            SavedSession session = new SavedSession(model, savedArrows, xs, ys);
            //write session to file
            oos.writeObject(session);
            return true;    // return success
        } catch (IOException ioex) {    // notify user in case of error
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не удалось сохранить сессию.", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return false;   // return failure
        }
    }

    /**
     * reads serialization file and recovers session from it
     * @param path path to file
     * @return true in case of success
     */
    private boolean readSession(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))){ // open IO stream
            SavedSession session = (SavedSession) ois.readObject(); // try to retrieve object

            // get values
            GraphModel model = session.getModel();
            List<Arrow> arrows = session.getArrows();
            double[] xs = session.getCircleX();
            double[] ys = session.getCircleY();
            this.model = model;

            // clear current session
            workingField.getChildren().clear();
            nodemap.clear();
            edgemap.clear();

            // recreate nodes
            int index = 0;
            for (GraphNode n : model.getGraphNodes()){
                Circle c = new Circle(xs[index], ys[index], NODE_RADIUS, nodeColor);    // create circle
                attachNodeHandler(c, n);                                                // attach handler
                workingField.getChildren().add(c);                                      // add to view
                nodemap.put(n, c);                                                      // link circle with model
                index++;
            }

            //  recreate edges
            index = 0;
            for (GraphEdge e : model.getGraphEdges()){
                Arrow arc = arrows.get(index);                                  // get arc
                if (functionComboBox.getValue().equals(FUNCTION_MIZUNO_SATO)) {   // reinitialize weighted
                    arc.reinitialize(NODE_RADIUS, true);
                } else {                                                        // reinitialize not weighted
                    arc.reinitialize(NODE_RADIUS, false);
                }
                attachEdgeHandler(arc, e);                                      // attach handler
                workingField.getChildren().addAll(arc.getAllElements());        // add to view
                edgemap.put(e, arc);                                            // link model and view
                index++;
            }

            return true;    // return success
        } catch (ClassNotFoundException cnfex) { // class exception
            Alert msg = new Alert(Alert.AlertType.ERROR, "Версия файла не совпадает.", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return false;   // return failure
        } catch (IOException ioex) {             // reading exception
            Alert msg = new Alert(Alert.AlertType.ERROR, "Не удалось прочитать файл сессии.", ButtonType.OK);
            msg.setTitle("Ошибка!");
            msg.setHeaderText(null);
            msg.setGraphic(null);
            msg.show();
            return false;   // return failure
        }
    }
}
