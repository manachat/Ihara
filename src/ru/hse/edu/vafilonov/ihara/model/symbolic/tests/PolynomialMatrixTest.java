package ru.hse.edu.vafilonov.ihara.model.symbolic.tests;

import org.junit.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import ru.hse.edu.vafilonov.ihara.model.symbolic.PolynomialFraction;
import ru.hse.edu.vafilonov.ihara.model.symbolic.PolynomialMatrix;

import static org.junit.Assert.*;

public class PolynomialMatrixTest {

    @Test
    public void getDeterminant() {
        PolynomialFraction[][] carcass = {{new PolynomialFraction(2,1), new PolynomialFraction(3,1)},
                {new PolynomialFraction(1,2), new PolynomialFraction(1,3)}};
        PolynomialMatrix test = new PolynomialMatrix(carcass);

        System.out.println(test.getDeterminant().toString());
        Assert.assertEquals(1,3);
    }
}