package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Node;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class GraphNode extends AbstractGraphElement {
    private List<GraphEdge> connections = new ArrayList<>();

    public GraphNode(Figure rep, GraphModel graph){
        representation = rep;
        this.graph = graph;
    }

    @Override
    public void deleteElement(){ //по идее должен вызываться из модели
        for (var con : connections){
            con.disconnect(this);
        }
        connections.clear();
    }

    @Override
    public void connect(AbstractGraphElement el){
        connections.add((GraphEdge)el);
    }

    @Override
    public void disconnect(AbstractGraphElement el){
        connections.remove(el);
    }

}
