package ru.hse.edu.vafilonov.ihara.model;

import java.io.Serializable;

/**
 * Represents model of Ihara graph edge
 * Edge represents pair of symmetric oriented edges
 * Direction shows main direction with weight w
 * twin edge has opposite direction and weight 1/w
 * @see ru.hse.edu.vafilonov.ihara.model.AbstractGraphElement
 * @see java.io.Serializable
 * @version 2
 * @author Filonov Vsevolod
 */
public class GraphEdge extends AbstractGraphElement implements Serializable {
    /**
     * serialization version identifier
     */
    private static final long serialVersionUID = 2L;

    /**
     * origin node for main direction
     */
    private GraphNode origin;

    /**
     * tail node for main direction
     */
    private GraphNode tail;

    /**
     * weight of main direction
     */
    private double weight = 0.0;

    /**
     * Constructor
     * @param origin origin node for main direction
     * @param tail tail node for main direction
     */
    public GraphEdge(GraphNode origin, GraphNode tail){
        this.origin = origin;
        this.tail = tail;
    }

    /**
     * Nullifies refs. to nodes
     */
    @Override
    public void deleteElement(){
        origin = null;
        tail = null;
    }

    /**
     * Sets weight for edge's main direction
     * @param w weight value
     */
    public void setWeight(double w){
        weight = w;
    }

    /**
     * returns wight of main direction
     * @return weight value
     */
    public double getWeight() {
        return weight;
    }

    /**
     * returns origin for main direction
     * @return weight value
     */
    public GraphNode getOrigin() {
        return origin;
    }

    /**
     * returns tail for main direction
     * @return weight value
     */
    public GraphNode getTail() {
        return tail;
    }

    /**
     * Tells if edge is connected to given node
     * @param node node ref.
     * @return result
     */
    public boolean contains(GraphNode node){
        return origin == node || tail == node;
    }
}
