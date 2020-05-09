package ru.hse.edu.vafilonov.ihara.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.edu.vafilonov.ihara.model.ComplexNumber;

import static org.junit.jupiter.api.Assertions.*;

class ComplexNumberTest {

    @Test
    void getAddId() {
    }

    @Test
    void getMultId() {
    }

    @Test
    void sum() {
    }

    @Test
    void multiply() {
    }

    @Test
    void pow() {
        double accuracy = 0.00000000001;
        ComplexNumber testBase = new ComplexNumber(3*Math.cos(Math.PI / 3), 3*Math.sin(Math.PI / 3));
        ComplexNumber res = ComplexNumber.pow(testBase, 3);
        Assertions.assertTrue(res.getRe() - 27 < accuracy && res.getIm() - 0.0 < accuracy);
        res = ComplexNumber.pow(testBase, 6);
        Assertions.assertTrue(res.getRe() - 729 < accuracy && res.getIm() - 0.0 < accuracy);
    }

    @Test
    void modulus() {
    }

    @Test
    void testModulus() {
    }

    @Test
    void getAddInverse() {
    }

    @Test
    void getMultInverse() {
    }

    @Test
    void testEquals() {
    }
}