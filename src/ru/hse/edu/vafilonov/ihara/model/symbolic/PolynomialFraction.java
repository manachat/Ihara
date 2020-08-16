package ru.hse.edu.vafilonov.ihara.model.symbolic;
/**
 * WARNING!
 * ALL CLASSES IN PACKAGE "SYMBOLIC" ARE
 * IMPLEMENTED TO EXTENT NEEDED FOR CALCULATION
 * OF SYMBOLIC EXPRESSION OF BASS FUNCTION
 */

public class PolynomialFraction {
    private Polynomial numerator;
    private Polynomial denominator;


    public PolynomialFraction(Polynomial numerator, Polynomial denominator) {
        if (denominator.isAddId()) {
            throw new IllegalArgumentException("Denominator can't be zero.");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public PolynomialFraction(int numerator, int denominator) {
        this.numerator = new Polynomial(numerator);
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator can't be zero");
        }
        this.denominator = new Polynomial(denominator);
    }

    public static PolynomialFraction getMultId() {
        return new PolynomialFraction(1,1);
    }

    public static PolynomialFraction getAddId() {
        return new PolynomialFraction(0,1);
    }

    public boolean isAddId() {
        return numerator.getTerms().size() == 0;
    }

    public boolean isMultId() {
        return numerator.isMultId() && denominator.isMultId();
    }

    public PolynomialFraction getMultInverse() {
        return new PolynomialFraction(denominator.copy(), numerator.copy());
    }

    public PolynomialFraction getAddInverse() {
        return new PolynomialFraction(numerator.getAddInverse(), denominator);
    }

    public static PolynomialFraction multiply(PolynomialFraction a, PolynomialFraction b) {
        Polynomial num = Polynomial.multiply(a.numerator, b.numerator);
        Polynomial den = Polynomial.multiply(a.denominator, b.denominator);
        return new PolynomialFraction(num, den);
    }

    public static PolynomialFraction sum(PolynomialFraction a, PolynomialFraction b) {

    }

    public PolynomialFraction copy() {
        Polynomial num = denominator.copy();
        Polynomial den = numerator.copy();
        return new PolynomialFraction(num, den);
    }


}
