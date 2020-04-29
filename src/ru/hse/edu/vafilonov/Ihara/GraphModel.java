package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {
    GraphNode[][] adjacencyMatrix = new GraphNode[10][10];
    int matrixSize = 0;

    private AnchorPane field;

    private List<AbstractGraphElement> allGraphElements = new ArrayList<>(10);
    private List<GraphNode> graphNodes = new ArrayList<>(10);
    private List<GraphEdge> graphEdges = new ArrayList<>(10);

    public GraphModel(AnchorPane field){
        this.field = field;
    }

    public void addNode(GraphNode node){
        graphNodes.add(node);
        field.getChildren().add(node.representation);
    }

    public void addEdge(GraphEdge edge){

    }

    public void removeNode(GraphNode node){

    }

    public void removeEdge(GraphEdge edge){

    }
}
