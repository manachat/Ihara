package ru.hse.edu.vafilonov.ihara.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents Ihara graph model and methods
 * for reverse Zeta function calculation
 * @see java.io.Serializable
 * @version 2
 * @author Filonov Vsevolod
 */
public class GraphModel implements Serializable {
    /**
     * serialization version identifier
     */
    private static final long serialVersionUID = 2L;

    /**
     * list of graph nodes
     */
    private List<GraphNode> graphNodes = new ArrayList<>(10);

    /**
     * list of graph edges
     */
    private List<GraphEdge> graphEdges = new ArrayList<>(10);

    /**
     * Adds node to graph
     * @return created node
     */
    public GraphNode addNode(){
        GraphNode node = new GraphNode();
        graphNodes.add(node);
        return node;
    }

    /**
     * Adds edge to graph
     * @return created edge
     */
    public GraphEdge addEdge(GraphNode origin, GraphNode tail){
        GraphEdge edge = new GraphEdge(origin, tail);
        graphEdges.add(edge);
        origin.connect(edge);
        tail.connect(edge);
        return edge;
    }

    /**
     * Disconnects node and removes it from graph
     * Also removes edges connected to this node
     * @param node removed node
     */
    public void removeNode(GraphNode node){
        List<GraphEdge> adjacentEdges = node.getConnections();
        // remove horizontal connections
        for (GraphEdge e : adjacentEdges){ // disconnect other node, concurrent exc otherwise
            if (e.getOrigin() != node){
                e.getOrigin().disconnect(e);
            } else {
                e.getTail().disconnect(e);
            }
        }
        //remove vertical connections
        graphEdges.removeAll(adjacentEdges);
        graphNodes.remove(node);
    }

    /**
     * Disconnects edge and removes it from graph
     * @param edge removed edge
     */
    public void removeEdge(GraphEdge edge) {
        //remove horizontal connections
        edge.getTail().disconnect(edge);
        edge.getOrigin().disconnect(edge);
        edge.deleteElement();
        //remove vertical connection
        graphEdges.remove(edge);
    }

    public List<GraphNode> getGraphNodes() {
        return graphNodes;
    }

    public List<GraphEdge> getGraphEdges() {
        return graphEdges;
    }

    /**
     * Checks connectivity of graph
     * Method uses broad-first search to check connectivity
     *         graph is not connected if white nodes are still present
     *         after BFS
     * @return true for connected graph
     * @throws IllegalStateException if there are no nodes in graph
     */
    public boolean checkConnectivity() throws IllegalStateException {
        if (graphNodes.isEmpty()){
            throw new IllegalStateException("Граф пуст");
        }
        /*
        BFS
         */
        ArrayDeque<GraphNode> traversalQueue = new ArrayDeque<>();
        traversalQueue.addLast(graphNodes.get(0));
        graphNodes.get(0).setColor(1); //mark grey
        GraphNode current;
        while (!traversalQueue.isEmpty()){
            current = traversalQueue.getFirst();
            for (GraphEdge e : current.getConnections()){
                GraphNode adjacent = e.getOrigin() == current ? e.getTail() : e.getOrigin(); //take adjacent node
                if (adjacent.getColor() == 0){ //paint white node grey and add to queue
                    traversalQueue.addLast(adjacent);
                    adjacent.setColor(1);
                }
            }
            //paint node black and remove from queue
            current.setColor(2);
            traversalQueue.removeFirst();
        }
        boolean allBlack = true;
        // look for white nodes
        for (GraphNode n : graphNodes){
            if (n.getColor() == 0){
                allBlack = false;
            }
            n.setColor(0); //return nodes to white color
        }

        return allBlack;
    }

    /**
     * helper method
     * checks if all non-zero weights on edges were set
     * throws
     */
    private void checkWeights() throws ArithmeticException{
        for (GraphEdge e: graphEdges) {
            String w = e.getWeight();
            if (w.charAt(0) != 's') {
                if (Double.parseDouble(w) == 0.0){
                    throw new ArithmeticException("Веса не могут быть нулевыми");
                }
            }
        }
    }

    /*
    Zeta function Formulas
     */

    /**
     * Calculates reverse Ihara Zeta function using Hashimoto formula
     * det(I-u(B-J))
     * @param u argument
     * @return function value
     * @throws IllegalStateException if there are no edges in graph
     */
    public ComplexNumber calculateZetaHashimoto(ComplexNumber u) throws IllegalStateException{
        if (graphEdges.size() == 0){
            throw new IllegalStateException("Отсутствуют ребра");
        }
        ComplexMatrix id = ComplexMatrix.getIdentityMatrix(graphEdges.size()*2); //I
        ComplexMatrix edgeMatrix = constructEdgeMatrix();
        ComplexMatrix multipliedEdgeMatrix = edgeMatrix.scalarMult(u.getAddInverse()); // -u(B-J)
        ComplexMatrix resultMatrix = ComplexMatrix.sum(id, multipliedEdgeMatrix);
        return resultMatrix.getDeterminant();
    }

    /**
     * Calculates reverse Ihara Zeta function using Bass formula
     * det(I - uA + u^2 Q)
     * @param u argument
     * @return function value
     * @throws IllegalStateException if there are no nodes in graph
     */
    public ComplexNumber calculateZetaBass(ComplexNumber u) throws IllegalStateException{
        if (graphNodes.size() == 0) {
            throw new IllegalStateException("Отсутствуют узлы.");
        }

        if (u.equals(ComplexNumber.getMultId())) {
            return ComplexNumber.getAddId();
        }

        ComplexMatrix id = ComplexMatrix.getIdentityMatrix(graphNodes.size());
        ComplexMatrix adjacency = constructAdjacencyMatrix();
        ComplexMatrix Q = constructQMatrix();
        ComplexNumber uSquared = ComplexNumber.multiply(u, u);
        ComplexMatrix uA = adjacency.scalarMult(u.getAddInverse()); //-uA
        ComplexMatrix uSqQ = Q.scalarMult(uSquared);
        ComplexMatrix intermediate = ComplexMatrix.sum(id, uA); // I - uA
        ComplexMatrix result = ComplexMatrix.sum(intermediate, uSqQ); // I - uA + u^2Q
        int power = graphEdges.size() - graphNodes.size(); //m - n
        ComplexNumber base = ComplexNumber.sum(ComplexNumber.getMultId(), uSquared.getAddInverse()); // 1 - u^2
        ComplexNumber coef = ComplexNumber.pow(base, power); //(1 - u^2)^(m - n)
        ComplexNumber det = result.getDeterminant(); // det(I - uA + u^2Q)
        return ComplexNumber.multiply(coef, det);
    }

    /**
     * Calculates reverse weighted Ihara Zeta function using Mizuno and Sato formula
     * @param u argument
     * @return function value
     * @throws ArithmeticException if there are zero weights in graph
     * @throws IllegalStateException if there are no nodes in graph
     */
    public ComplexNumber calculateZetaMizunoSato(ComplexNumber u) throws ArithmeticException, IllegalStateException{
        if (graphNodes.size() == 0){
            throw new IllegalStateException("Отсутствуют узлы.");
        }
        checkWeights();

        if (u.equals(ComplexNumber.getMultId())){
            return ComplexNumber.getAddId();
        }

        ComplexMatrix id = ComplexMatrix.getIdentityMatrix(graphNodes.size());
        ComplexMatrix adjacency = constructWeightedAdjacencyMatrix();
        ComplexMatrix Q = constructQMatrix();
        ComplexNumber uSquared = ComplexNumber.multiply(u, u);
        ComplexMatrix uW = adjacency.scalarMult(u.getAddInverse()); //-uW
        ComplexMatrix uSqQ = Q.scalarMult(uSquared);
        ComplexMatrix intermediate = ComplexMatrix.sum(id, uW); // I - uW
        ComplexMatrix result = ComplexMatrix.sum(intermediate, uSqQ); // I - uW + u^2Q
        int power = graphEdges.size() - graphNodes.size(); //m - n
        ComplexNumber base = ComplexNumber.sum(ComplexNumber.getMultId(), uSquared.getAddInverse()); // 1 - u^2
        ComplexNumber coef = ComplexNumber.pow(base, power); //(1 - u^2)^(m - n)
        ComplexNumber det = result.getDeterminant(); // det(I - uA + u^2Q)
        return ComplexNumber.multiply(coef, det);
    }


    /**
     * Constructs unweighted adjacency matrix
     * form current graph state
     * @return adjacency matrix
     */
    private ComplexMatrix constructAdjacencyMatrix(){
        int size = graphNodes.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j){
                    matrix[i][j] = 0.;
                }
                else {
                    if (GraphNode.getConnection(graphNodes.get(i), graphNodes.get(j)) == null) {
                        matrix[i][j] = 0.;
                        matrix[j][i] = 0.;
                    }
                    else {
                        matrix[i][j] = 1.;
                        matrix[j][i] = 1.;
                    }
                }
            }
        }
        return new ComplexMatrix(matrix);
    }

    /**
     * Constructs weighted adjacency matrix
     * for current graph state
     * @return weighted adjacency matrix
     */
    private ComplexMatrix constructWeightedAdjacencyMatrix(){
        int size = graphNodes.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j){
                    matrix[i][j] = 0.;
                }
                else {
                    GraphEdge edge = GraphNode.getConnection(graphNodes.get(i), graphNodes.get(j));
                    if (edge == null) {
                        matrix[i][j] = 0.;
                        matrix[j][i] = 0.;
                    }
                    else {
                        if (graphNodes.get(i) == edge.getOrigin()) {
                            // function should be called without symbolic
                            matrix[i][j] = Double.parseDouble(edge.getWeight()); // weight in forward direction
                            matrix[j][i] = 1.0 / matrix[i][j]; // 1 / weight in reverse direction
                        }
                        else {
                            matrix[j][i] = Double.parseDouble(edge.getWeight());
                            matrix[i][j] = 1.0 / matrix[j][i];
                        }
                    }
                }
            }
        }
        return new ComplexMatrix(matrix);
    }

    /**
     * constructs edge matrix for bi-edges of graph
     * B(e,f) = 1 if t(e) = o(f), 0 otherwise
     * J(e,f) = 1 if f = e^-1, 0 otherwise
     * edgeMatrix = B - J
     * @return B-matrix for current graph state
     */
    private ComplexMatrix constructEdgeMatrix(){
        int half = graphEdges.size();
        int size = half * 2;
        double[][] res = new double[size][size];

        //fill B-J matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < half) { // row edge is primary
                    if (j < half) { //column edge is primary
                        res[i][j] = graphEdges.get(i).getTail() == graphEdges.get(j).getOrigin() ? 1. : 0.;
                    } else { //column edge is inverse
                        res[i][j] = graphEdges.get(i).getTail() == graphEdges.get(j - half).getTail() ? 1. : 0.;
                        if (i == j - half){ //opposite edges, J-matrix property
                            res[i][j] -= 1.;
                        }
                    }
                }
                else{ //row edge is inverse
                    if (j < half) { //column edge is primary
                        res[i][j] = graphEdges.get(i - half).getOrigin() == graphEdges.get(j).getOrigin() ? 1. : 0.;
                        if (i - half == j){ //opposite edges, J-matrix property
                            res[i][j] -= 1.;
                        }
                    } else { //column edge is inverse
                        res[i][j] = graphEdges.get(i - half).getOrigin() == graphEdges.get(j - half).getTail() ? 1. : 0.;
                    }
                }
            }
        }

        return new ComplexMatrix(res);
    }

    /**
     * Constructs Q-matrix -- diagonal matrix
     * with Q[i][i] = deg(V[i]) - 1
     * @return
     */
    private ComplexMatrix constructQMatrix(){
        int size = graphNodes.size();
        double[][] carcass = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                carcass[i][j] = i == j ? graphNodes.get(i).getDegree() - 1 : 0;
            }
        }
        return new ComplexMatrix(carcass);
    }

    /**
     * Helper method
     * adds two real-value square matrices
     * @param a first arg
     * @param b second arg
     * @return sum
     */
    private double[][] addMatrices(double[][] a, double[][] b){
        int n = a.length;
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                res[i][j] = a[i][j] + b[i][j];
            }
        }
        return res;
    }
}
