package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.shape.Shape;

public class GraphEdge extends AbstractGraphElement {
    private GraphNode origin;
    private GraphNode tail;
    private double weight;

    public GraphEdge(GraphNode origin, GraphNode tail){
        this.origin = origin;
        this.tail = tail;
    }

    public void setWeight(double w){
        weight = w;
    }

    @Override
    public void deleteElement(){
        origin = null;
        tail = null;
    }


    public GraphNode getOrigin() {
        return origin;
    }

    public GraphNode getTail() {
        return tail;
    }

    public boolean contains(GraphNode node){
        return origin == node || tail == node;
    }
}
