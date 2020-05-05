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
    /*
    public boolean checkConnectivity(){
        //TODO: implement BFS/DFS
    }

     */

    //-----------------------------------------------------FORMULAS-------------------------------------

    public ComplexNumber calculateZetaTheoremOneA(ComplexNumber u){
        ComplexMatrix id = ComplexMatrix.getIdentityMatrix(graphEdges.size()*2); //I
        ComplexMatrix edgeMatrix = constructEdgeMatrix();
        ComplexMatrix multipliedEdgeMatrix = edgeMatrix.scalarMult(u.getAddInverse()); // -u(B-J)
        ComplexMatrix resultMatrix = ComplexMatrix.sum(id, multipliedEdgeMatrix);
        return resultMatrix.getDeterminant();
    }

    public ComplexNumber calculateZetaTheoremOneB(ComplexNumber u){
        if (u.equals(ComplexNumber.getMultId())){
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
        ComplexNumber base = ComplexNumber.sum(ComplexNumber.getAddId(), uSquared.getAddInverse()); // 1 - u^2
        ComplexNumber coef = ComplexNumber.pow(base, power); //(1 - u^2)^(m - n)
        ComplexNumber det = result.getDeterminant(); // det(I - uA + u^2Q)
        return ComplexNumber.multiply(coef, det);
    }


    /**
     * Constructs unweighted adracency matrix
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
