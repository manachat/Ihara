package ru.hse.edu.vafilonov.ihara.model.symbolic.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.hse.edu.vafilonov.ihara.model.symbolic.PolynomialFraction;
import ru.hse.edu.vafilonov.ihara.model.symbolic.PolynomialMatrix;

import static org.junit.Assert.*;

public class PolynomialMatrixTest {

    @Test
    public void getDeterminant() {
        PolynomialFraction[][] carcass = {{new PolynomialFraction(2,1), new PolynomialFraction(3,1)},
                {new PolynomialFraction(1,2), new PolynomialFraction(1,3)}};
        PolynomialMatrix test = new PolynomialMatrix(carcass);
        PolynomialFraction det = test.getDeterminant();
        System.out.println(det.toString());
        det.reduce();
        System.out.println(det.toString());

        Assert.assertEquals("kek", det.toString());
    }
}