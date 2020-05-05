package ru.hse.edu.vafilonov.Ihara.model;

/**
 * Class represents square matrix of complex numbers
 * and basic operations with them
 * @see ComplexNumber
 */
public class ComplexMatrix{
    private final ComplexNumber[][] matrix;

    private final int N;

    /**
     * sums to complex matrices
     * throws IllegalArgumentException if argument dimensions do not match
     * @param a first arg
     * @param b second arg
     * @return sum of matrices
     */
    public static ComplexMatrix sum(ComplexMatrix a, ComplexMatrix b){
        if (a.N != b.N){
            throw new IllegalArgumentException("Sizes do not match");
        }
        ComplexNumber[][] res = new ComplexNumber[a.N][a.N];
        for (int i = 0; i < a.N; i++){
            for (int j = 0; j < a.N; j++){
                res[i][j] = ComplexNumber.sum(a.get(i, j), b.get(i, j));
            }
        }
        return new ComplexMatrix(res);
    }

    /**
     * Constructs identity complex matrix of given size
     * @param size
     * @return
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
     * Copy constructor
     * Uses 2-dimensional ComplexNumber array to create matrix
     * @see ComplexNumber
     * @param matrix square 2-dim array
     */
    public ComplexMatrix(ComplexNumber[][] matrix){
        if (matrix.length != matrix[0].length){
            throw new IllegalArgumentException("Non-square matrix");
        }
        N = matrix.length;
        this.matrix = new ComplexNumber[N][N];
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
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
        N = dmatrix.length;
        this.matrix = new ComplexNumber[N][N];
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                this.matrix[i][j] = new ComplexNumber(dmatrix[i][j]);
            }
        }
    }

    /*
    public ComplexNumber[][] getMatrix(){
        return matrix;
    }
     */

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
        ComplexNumber[][] res = new ComplexNumber[N][N];
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
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
        ComplexNumber[][] copy = new ComplexNumber[N][N]; //copy matrix due to it immutability
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                copy[i][j] = matrix[i][j].copy();
            }
        }

        ComplexNumber det = ComplexNumber.getMultId(); //result
        int swaps = 0; //number of row swaps
        //going down diagonal, search for leading element and reduce all the elements under the lead
        for (int i = 0; i < N; i++){
            int leadRow = i; //row with lead element
            //going down the column until we find lead element
            while (leadRow < N && copy[leadRow][i].equals(ComplexNumber.getAddId())){
                leadRow++;
            }
            //no lead element for current step was found => Rank < N => det == 0
            if (leadRow == N){
                return ComplexNumber.getAddId();
            }
            //elevate row with lead element to preserve triangle form
            if (leadRow > i){
                switchRows(copy, i, leadRow);
                swaps++; //each swap changes det sigh
            }
            ComplexNumber lead = copy[i][i];
            ComplexNumber invLead = lead.getMultInverse();
            for (int j = i + 1; j < N; j++){
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
     * switches two row of given matrix
     * @param matrix matrix to edit
     * @param i row index
     * @param j column index
     */
    private void switchRows(ComplexNumber[][] matrix, int i, int j){
        for (int k = 0; k < N; k++){
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
        for (int i = 0; i < N; i++){
            matrix[target][i] = ComplexNumber.sum(matrix[target][i], ComplexNumber.multiply(matrix[source][i], coef));
        }
    }

}
