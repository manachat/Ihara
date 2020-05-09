package ru.hse.edu.vafilonov.ihara.tests;

//import org.junit.Test;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import ru.hse.edu.vafilonov.ihara.model.ComplexNumber;
import ru.hse.edu.vafilonov.ihara.model.ComplexMatrix;

import static org.junit.jupiter.api.Assertions.*;

class ComplexMatrixTest {

    @Test
    void getDeterminant() {
        ComplexNumber[][] testMatrix = new ComplexNumber[5][5];
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                if (i == j){
                    testMatrix[i][j] = ComplexNumber.getMultId();
                }
                else {
                    testMatrix[i][j] = ComplexNumber.getAddId();
                }
            }
        }

        System.out.println(testMatrix.length);
        ComplexMatrix test = new ComplexMatrix(testMatrix);
        Assertions.assertEquals(ComplexNumber.getMultId(), test.getDeterminant());

        testMatrix[3][3] = ComplexNumber.getAddId();
        test = new ComplexMatrix(testMatrix);
        Assertions.assertEquals(ComplexNumber.getAddId(), test.getDeterminant());
    }
}