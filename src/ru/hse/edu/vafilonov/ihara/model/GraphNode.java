package ru.hse.edu.vafilonov.ihara.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents model of Ihara graph node.
 * @see ru.hse.edu.vafilonov.ihara.model.AbstractGraphElement
 * @see java.io.Serializable
 * @version 2
 * @author Filonov Vsevolod
 */
public class GraphNode extends AbstractGraphElement implements Serializable {
    /**
     * serialization version identifier
     */
    private static final long serialVersionUID = 2L;

    /**
     * list of adjacent edges
     */
    private List<GraphEdge> connections = new ArrayList<>(10);

    /**
     * color identifier, used in graph traversals
     * 0 - white
     * 1 - grey
     * 2 - black
     */
    private int color = 0;

    /**
     * @return unoriented degree of graph
     */
    public int getDegree(){
        return connections.size();
    }

    /**
     * @return list of adjacent edges
     */
    public List<GraphEdge> getConnections(){
        return connections;
    }

    /**
     * clears connections list
     */
    @Override
    public void deleteElement(){ //по идее должен вызываться из модели
        connections.clear();
    }

    /**
     * adds edge to list of connections
     * @param el edge ref.
     */
    public void connect(GraphEdge el){
        connections.add(el);
    }

    /**
     * removes edge from list of connections
     * @param el edge ref.
     */
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
        // looks for connections in node with lesser number of edges
        if (a.getConnections().size() < b.getConnections().size()){
            connections = a.getConnections();
            compared = b;
        } else {
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
