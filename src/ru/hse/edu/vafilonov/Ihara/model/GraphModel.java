package ru.hse.edu.vafilonov.Ihara.model;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {

    private AnchorPane field;

    private List<AbstractGraphElement> allGraphElements = new ArrayList<>(10);
    private List<GraphNode> graphNodes = new ArrayList<>(10);
    private List<GraphEdge> graphEdges = new ArrayList<>(10);

    public GraphModel(AnchorPane field){
        this.field = field;
    }

    public void addNode(GraphNode node){
        graphNodes.add(node);
    }

    public void addEdge(GraphEdge edge){
        graphEdges.add(edge);
        edge.getOrigin().connect(edge);
        edge.getTail().connect(edge);
    }

    public void removeNode(GraphNode node){
        List<GraphEdge> adjacentEdges = node.getConnections();
        //remove horisontal connections
        for (GraphEdge e : adjacentEdges){ //disconnect other node, concurrent exc otherwise
            if (e.getOrigin() != node){
                e.getOrigin().disconnect(e);
            }
            else {
                e.getTail().disconnect(e);
            }
        }
        node.deleteElement();
        //remove vertical connections
        graphEdges.removeAll(adjacentEdges);
        graphNodes.remove(node);
    }

    public void removeEdge(GraphEdge edge){
        //remove horizontal connections
        edge.getTail().disconnect(edge);
        edge.getOrigin().disconnect(edge);
        edge.deleteElement();
        //remove vertical connection
        graphEdges.remove(edge);
    }


}
