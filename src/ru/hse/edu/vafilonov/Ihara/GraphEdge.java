package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.shape.Shape;

public class GraphEdge extends AbstractGraphElement {
    private GraphNode origin;
    private GraphNode tail;
    private double weight;

    public GraphEdge(Figure rep, GraphModel graph, GraphNode origin, GraphNode tail){
        representation = rep;
        this.graph = graph;
        this.origin = origin;
        this.tail = tail;
    }

    @Override
    public void deleteElement(){
        origin.disconnect(this);
        tail.disconnect(this);
        origin = null;
        tail = null;
    }

    @Override
    public void connect(AbstractGraphElement el) {

        //TODO а зачем он?
        if (tail != null && origin != null){
            throw new IllegalArgumentException(); //TODO: доделать
        }

        if (tail != null){
            tail = el;
        }
        else {
            origin = el;
        }
    }

    @Override
    public void disconnect(AbstractGraphElement el){

    }

    public GraphNode getOrigin() {
        return origin;
    }

    public GraphNode getTail() {
        return tail;
    }
}
