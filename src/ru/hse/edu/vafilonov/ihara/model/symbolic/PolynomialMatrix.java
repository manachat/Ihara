package ru.hse.edu.vafilonov.ihara.model.symbolic;

public class PolynomialMatrix {
    PolynomialFraction[][] matrix;

    private final int size;

    public PolynomialMatrix(PolynomialFraction[][] matrix;) {
        if (matrix.length != matrix[0].length){
            throw new IllegalArgumentException("Non-square matrix");
        }
        size = matrix.length;
        this.matrix = new PolynomialFraction[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }

    public static PolynomialMatrix sum(PolynomialMatrix a, PolynomialMatrix b) {

    }

    public static PolynomialMatrix getIdentityMatrix(int size) {

    }

    public PolynomialFraction get(int i, int j) {
        return matrix[i][j];
    }


}
