package ru.hse.edu.vafilonov.Ihara;

public final class ComplexNumber extends Number{
    private final double re;
    private final double im;

    public ComplexNumber(double re, double im){
        this.re = re;
        this.im = im;
    }

    public <T extends Number> ComplexNumber(T arg){
        re = arg.doubleValue();
        im = 0.0;
    }

    public ComplexNumber plus(ComplexNumber arg){
        double x = re + arg.re;
        double y = im + arg.im;
        return new ComplexNumber(x, y);
    }

    public double modulus(){
        return Math.sqrt(re*re + re*re);
    }

    public double modulus(boolean squared){
        double r2 = re*re + im*im;
        return squared ? r2 : Math.sqrt(r2);
    }

    public ComplexNumber minus(ComplexNumber arg){
        double x = re - arg.re;
        double y = im - arg.im;
        return new ComplexNumber(x, y);
    }

    public ComplexNumber mult(ComplexNumber arg){
        double x = re*arg.re - im*arg.im;
        double y = re*arg.im + im*arg.re;
        return new ComplexNumber(x, y);
    }

    /**
     * WARNING DiVByZero not handled
     * @param arg
     * @return
     */
    public ComplexNumber div(ComplexNumber arg){
        ComplexNumber conjugate = new ComplexNumber(re, -im);
        ComplexNumber numerator = mult(conjugate);
        double denominator = modulus(true);
        return new ComplexNumber(numerator.re / denominator, numerator.im / denominator);
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
}
