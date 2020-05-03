package ru.hse.edu.vafilonov.Ihara.model;

public class ComplexMatrix{
    private ComplexNumber[][] matrix;

    private int N;

    public ComplexMatrix(int size, ComplexNumber[][] matrix){
        this.matrix = matrix;
        N = size;
    }

    public ComplexNumber[][] getMatrix(){
        return matrix;
    }

    public void add(ComplexMatrix el){
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){

            }
        }
    }

}
