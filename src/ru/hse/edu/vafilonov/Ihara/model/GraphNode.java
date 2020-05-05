package ru.hse.edu.vafilonov.Ihara.model;

import javafx.scene.Node;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphNode extends AbstractGraphElement {

    private List<GraphEdge> connections = new ArrayList<>(10);

    private int color = 0; //used in graph traversals

    /**
     * return unoriented degree of graph
     * @return
     */
    public int getDegree(){
        return connections.size();
    }

    /**
     * Returns number of edges that lead to that node
     * @return
     */
    public int getInDegree(){
        int count = 0;
        for (GraphEdge e : connections){
            if (e.getTail() == this){
                count++;
            }
        }
        return count;
    }

    public int getOutDegree(){
        int count = 0;
        for (GraphEdge e : connections){
            if (e.getOrigin() == this){
                count++;
            }
        }
        return count;
    }

    public List<GraphEdge> getConnections(){
        return connections;
    }

    @Override
    public void deleteElement(){ //по идее должен вызываться из модели
        connections.clear();
    }


    public void connect(GraphEdge el){
        connections.add(el);
    }

    public void disconnect(GraphEdge el){
        connections.remove(el);
    }

    /**
     * Looks for connection between two nodes and returns it
     * If nodes are not connected returns null
     * @param a first node
     * @param b second node
     * @return connection between nodes, null otherwise
     */
    public static GraphEdge getConnection(GraphNode a, GraphNode b){
        List<GraphEdge> connections;
        GraphNode compared;
        if (a.getConnections().size() < b.getConnections().size()){
            connections = a.getConnections();
            compared = b;
        }
        else{
            connections = b.getConnections();
            compared = a;
        }

        for (GraphEdge e : connections){
            if (e.contains(compared)){
                return e;
            }
        }

        return null;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
