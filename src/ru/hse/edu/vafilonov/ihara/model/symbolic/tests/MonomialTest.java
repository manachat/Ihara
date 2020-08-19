package ru.hse.edu.vafilonov.ihara.model.symbolic.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.hse.edu.vafilonov.ihara.model.symbolic.Monomial;

import static org.junit.Assert.*;

public class MonomialTest {

    @Test
    public void coefsToString() {
        Monomial m1 = new Monomial(1);
        Monomial mult = Monomial.multiply(m1, m1);
        for (int i = 0; i < 4; i++) {
            mult = Monomial.multiply(mult, m1);
        }
        mult = Monomial.multiply(mult, new Monomial(2));
        Assert.assertEquals("\\sqrt{2}", mult.toString());
    }
}