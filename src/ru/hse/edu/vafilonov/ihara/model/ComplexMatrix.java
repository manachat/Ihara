package ru.hse.edu.vafilonov.ihara.model;

/**
 * Class represents square matrix of complex numbers
 * and basic operations with them
 * @see ComplexNumber
 * @version 2
 * @author Filonov Vsevolod
 */
public class ComplexMatrix{
    /**
     * matrix 2-dim array
     */
    private final ComplexNumber[][] matrix;

    /**
     * matrix size
     */
    private final int size;

    /**
     * Copy constructor
     * Uses 2-dimensional ComplexNumber array to create matrix
     * @see ComplexNumber
     * @param matrix square 2-dim array
     */
    public ComplexMatrix(ComplexNumber[][] matrix){
        if (matrix.length != matrix[0].length){
            throw new IllegalArgumentException("Non-square matrix");
        }
        size = matrix.length;
        this.matrix = new ComplexNumber[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                this.matrix[i][j] = matrix[i][j].copy();
            }
        }
    }

    /**
     * Constructor
     * Constructs ComplexMatrix from 2-dimensional double array
     * @param dmatrix 2-dim array of double type
     */
    public ComplexMatrix(double[][] dmatrix){
        if (dmatrix.length != dmatrix[0].length){
            throw new IllegalArgumentException("Non-square matrix");
        }
        size = dmatrix.length;
        this.matrix = new ComplexNumber[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                this.matrix[i][j] = new ComplexNumber(dmatrix[i][j]);
            }
        }
    }

    /**
     * sums to complex matrices
     * @exception  IllegalArgumentException if argument dimensions do not match
     * @param a first arg
     * @param b second arg
     * @return sum of matrices
     */
    public static ComplexMatrix sum(ComplexMatrix a, ComplexMatrix b){
        if (a.size != b.size){
            throw new IllegalArgumentException("Sizes do not match");
        }
        ComplexNumber[][] res = new ComplexNumber[a.size][a.size];
        for (int i = 0; i < a.size; i++){
            for (int j = 0; j < a.size; j++){
                res[i][j] = ComplexNumber.sum(a.get(i, j), b.get(i, j));
            }
        }
        return new ComplexMatrix(res);
    }

    /**
     * Constructs identity complex matrix of given size
     * @param size matrix size
     * @return id matrix
     */
    public static ComplexMatrix getIdentityMatrix(int size){
        double[][] carcass = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                carcass[i][j] = i == j ? 1. : 0.;
            }
        }
        return new ComplexMatrix(carcass);
    }

    /**
     * returns copy of given element
     * @param i row index
     * @param j column index
     * @return copy of element
     */
    public ComplexNumber get(int i, int j){
        return matrix[i][j].copy();
    }

    /**
     * Multiplies matrix on given number
     * @param coef coefficient
     * @return multiplied matrix
     */
    public ComplexMatrix scalarMult(ComplexNumber coef){
        ComplexNumber[][] res = new ComplexNumber[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                res[i][j] = ComplexNumber.multiply(coef, matrix[i][j]);
            }
        }
        return new ComplexMatrix(res);
    }

    /**
     * Calculates complex determinant of matrix using Gaussian transform to triangle form
     * @return determinant of matrix
     */
    public ComplexNumber getDeterminant(){
        ComplexNumber[][] copy = new ComplexNumber[size][size]; //copy matrix due to it immutability
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                copy[i][j] = matrix[i][j].copy();
            }
        }

        ComplexNumber det = ComplexNumber.getMultId(); //result
        int swaps = 0; //number of row swaps
        //going down diagonal, search for leading element and reduce all the elements under the lead
        for (int i = 0; i < size; i++){
            int leadRow = i; //row with lead element
            //going down the column until we find lead element
            while (leadRow < size && copy[leadRow][i].equals(ComplexNumber.getAddId())){
                leadRow++;
            }
            //no lead element for current step was found => Rank < N => det == 0
            if (leadRow == size){
                return ComplexNumber.getAddId();
            }
            //elevate row with lead element to preserve triangle form
            if (leadRow > i){
                switchRows(copy, i, leadRow);
                swaps++; //each swap changes det sigh
            }
            ComplexNumber lead = copy[i][i];
            ComplexNumber invLead = lead.getMultInverse();
            for (int j = i + 1; j < size; j++){
                ComplexNumber coefficient = ComplexNumber.multiply(copy[j][i], invLead); //c = m[j][i]/lead
                addRow(copy, j, i, coefficient.getAddInverse()); // subtraction of row
            }
            det = ComplexNumber.multiply(det, copy[i][i]);
        }
        if (swaps % 2 != 0){
            det = det.getAddInverse();
        }
        return det;
    }

    /**
     * helper method
     * switches two rows of given matrix
     * @param matrix matrix to edit
     * @param i row index
     * @param j column index
     */
    private void switchRows(ComplexNumber[][] matrix, int i, int j){
        for (int k = 0; k < size; k++){
            ComplexNumber temp = matrix[i][k];
            matrix[i][k] = matrix[j][k];
            matrix[i][k] = temp;
        }
    }

    /**
     * helper method
     * adds one row multiplied by given coefficient to another
     * @param matrix matrix to edit
     * @param target row to be added to
     * @param source row to add
     * @param coef multiplicative coefficient
     */
    private void addRow(ComplexNumber[][] matrix, int target, int source, ComplexNumber coef){
        for (int i = 0; i < size; i++){
            matrix[target][i] = ComplexNumber.sum(matrix[target][i], ComplexNumber.multiply(matrix[source][i], coef));
        }
    }
}
