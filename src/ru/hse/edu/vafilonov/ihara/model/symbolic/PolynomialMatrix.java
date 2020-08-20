package ru.hse.edu.vafilonov.ihara.model.symbolic;

public class PolynomialMatrix {
    PolynomialFraction[][] matrix;

    private final int size;

    public PolynomialMatrix(PolynomialFraction[][] matrix) {
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
        if (a.size != b.size){
            throw new IllegalArgumentException("Sizes do not match");
        }
        PolynomialFraction[][] res = new PolynomialFraction[a.size][a.size];
        for (int i = 0; i < a.size; i++){
            for (int j = 0; j < a.size; j++){
                res[i][j] = PolynomialFraction.sum(a.get(i, j), b.get(i, j));
            }
        }
        return new PolynomialMatrix(res);
    }

    public static PolynomialMatrix getIdentityMatrix(int size) {
        PolynomialFraction[][] carcass = new PolynomialFraction[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                carcass[i][j] = i == j ? PolynomialFraction.getMultId() : PolynomialFraction.getAddId();
            }
        }
        return new PolynomialMatrix(carcass);
    }


    public PolynomialFraction get(int i, int j) {
        return matrix[i][j];
    }

    public PolynomialMatrix scalarMult(PolynomialFraction coef){
        PolynomialFraction[][] res = new PolynomialFraction[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                res[i][j] = PolynomialFraction.multiply(coef, matrix[i][j]);
            }
        }
        return new PolynomialMatrix(res);
    }

    public void multByArg(int power) {
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                matrix[i][j].multByArg(power);
            }
        }
    }

    public PolynomialFraction getDeterminant() {
        PolynomialFraction[][] copy = new PolynomialFraction[size][size]; //copy matrix due to it immutability
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                copy[i][j] = matrix[i][j].copy();
                copy[i][j].reduce();
            }
        }

        PolynomialFraction det = PolynomialFraction.getMultId(); //result
        int swaps = 0; //number of row swaps
        //going down diagonal, search for leading element and reduce all the elements under the lead
        for (int i = 0; i < size; i++){
            int leadRow = i; //row with lead element
            //going down the column until we find lead element
            while (leadRow < size && copy[leadRow][i].isAddId()){
                leadRow++;
            }
            //no lead element for current step was found => Rank < N => det == 0
            if (leadRow == size){
                return PolynomialFraction.getAddId();
            }
            //elevate row with lead element to preserve triangle form
            if (leadRow > i){
                switchRows(copy, i, leadRow);
                swaps++; //each swap changes det sigh
            }
            PolynomialFraction lead = copy[i][i];
            PolynomialFraction invLead = lead.getMultInverse();
            for (int j = i + 1; j < size; j++){
                PolynomialFraction coefficient = PolynomialFraction.multiply(copy[j][i], invLead); //c = m[j][i]/lead
                addRow(copy, j, i, coefficient.getAddInverse()); // subtraction of row
            }
            // новиночка
            copy[i][i].reduce();
            det = PolynomialFraction.multiply(det, copy[i][i]);
        }
        if (swaps % 2 != 0){
            det = det.getAddInverse();
        }
        return det;
    }

    private void switchRows(PolynomialFraction[][] matrix, int i, int j){
        for (int k = 0; k < size; k++){
            PolynomialFraction temp = matrix[i][k];
            matrix[i][k] = matrix[j][k];
            matrix[i][k] = temp;
        }
    }

    private void addRow(PolynomialFraction[][] matrix, int target, int source, PolynomialFraction coef){
        for (int i = 0; i < size; i++){
            matrix[target][i] = PolynomialFraction.sum(matrix[target][i],
                    PolynomialFraction.multiply(matrix[source][i], coef));
            matrix[target][i].reduce();
        }
    }

}
