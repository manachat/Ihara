package ru.hse.edu.vafilonov.ihara.model;

/**
 * Class represents complex number
 * @see java.lang.Number
 * @see java.io.Serializable
 * @version 2
 * @author Filonov Vsevolod
 */
public final class ComplexNumber extends Number{
    /**
     * Real value of number
     */
    private final double re;

    /**
     * imaginary value of number
     */
    private final double im;

    /**
     * Constructor. Creates complex number with given values
     * @param re real value
     * @param im imaginary value
     */
    public ComplexNumber(double re, double im){
        this.re = re;
        this.im = im;
    }

    /**
     * Constructs complex number from other non-complex number
     * @param arg number
     * @param <T> class extended from number
     */
    public <T extends Number> ComplexNumber(T arg){
        re = arg.doubleValue();
        im = 0.0;
    }

    /**
     * returns additive neutral element
     * @return neutral element
     */
    public static ComplexNumber getAddId() {
        return new ComplexNumber(0., 0.);
    }

    /**
     * return multiplicative neutral element
     * @return neutral element
     */
    public static ComplexNumber getMultId() {
        return new ComplexNumber(1., 0.);
    }

    /**
     * Adds two complex numbers
     * @param a first arg
     * @param b second arg
     * @return sum
     */
    public static ComplexNumber sum(ComplexNumber a, ComplexNumber b){
        double x = a.re + b.re;
        double y = a.im + b.im;
        return new ComplexNumber(x, y);
    }

    /**
     * Multiplies two complex numbers
     * @param a first arg
     * @param b second arg
     * @return product
     */
    public static ComplexNumber multiply(ComplexNumber a, ComplexNumber b){
        double x = a.re*b.re - a.im*b.im;
        double y = a.re*b.im + a.im*b.re;
        return new ComplexNumber(x, y);
    }

    /**
     * returns number to the given power
     * @param base base of number
     * @param power power
     * @return result
     */
    public static ComplexNumber pow(ComplexNumber base, int power){
        if (power == 0){
            return ComplexNumber.getMultId();
        }
        double re = base.getRe();
        double im = base.getIm();
        double x = base.getRe();
        double y = base.getIm();
        for (int i = 1; i < power; i++) {
            double newX = x*re - y*im;
            double newY = x*im + re*y;
            x = newX;
            y = newY;
        }
        ComplexNumber ret = new ComplexNumber(x, y);
        if (power < 0){
            return ret.getMultInverse();
        }
        return ret;
    }

    /**
     * @return modulus of number
     */
    public double modulus(){
        return Math.sqrt(re*re + re*re);
    }

    /**
     * Returns modulus of number in given form
     * @param squared squared form for true
     * @return modulus
     */
    public double modulus(boolean squared){
        double r2 = re*re + im*im;
        return (squared ? r2 : Math.sqrt(r2));
    }

    /**
     * @return additive inverse for current number
     */
    public ComplexNumber getAddInverse(){
        return new ComplexNumber(-re, - im);
    }

    /**
     * @return multiplicative inverse for current number
     */
    public ComplexNumber getMultInverse() {
        ComplexNumber conjugate = new ComplexNumber(re, -im);
        double r = modulus(true);
        if (r == 0.0) {
            return new ComplexNumber(0, 0);
        }
        double x = conjugate.re / r;
        double y = conjugate.im / r;
        return new ComplexNumber(x, y);
    }

    /**
     * @return copy of number
     */
    public ComplexNumber copy(){
        return new ComplexNumber(re, im);
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexNumber){
            ComplexNumber z = (ComplexNumber)obj;
            return z.re == re && z.im == im;
        }
        else {
            return false;
        }
    }

    @Override
    public int intValue() {
        return (int)re;
    }

    @Override
    public long longValue() {
        return (long)re;
    }

    @Override
    public float floatValue() {
        return (float)re;
    }

    @Override
    public double doubleValue() {
        return re;
    }

    /**
     * Given number with accuracy of 3 digits
     * @return string representation
     */
    @Override
    public String toString(){
        return accurateToString(3);
    }

    /**
     * String representation
     * @param accuracy positive value for accuracy,
     *                 non-positive value for max accuracy
     * @return String representation with given accuracy
     */
    public String accurateToString(int accuracy){
        String format, res;
        if (accuracy <= 0){
            format = "%f";
        }
        else {
            format = "%." + accuracy + "f";
        }

        StringBuilder builder = new StringBuilder();
        if (re != 0.0){
            builder.append(format);
            if (im > 0.0){
                builder.append(" + i");
            }
            else if (im < 0.0){
                builder.append(" - i");
            }

            if (im != 0.0){
                builder.append(format);
                res = String.format(builder.toString(), re, im);
            }
            else {
                res = String.format(builder.toString(), re);
            }
        }
        else {
            if (im > 0.0){
                builder.append("i");
                builder.append(format);
                res = String.format(builder.toString(), im);
            }
            else if (im < 0.0){
                builder.append("-i");
                builder.append(format);
                res = String.format(builder.toString(), im);
            }
            else return "0.0";
        }
        return res;
    }
}
