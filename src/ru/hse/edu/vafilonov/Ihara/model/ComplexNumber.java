package ru.hse.edu.vafilonov.Ihara.model;

public final class ComplexNumber extends Number{
    private final double re;
    private final double im;

    public static ComplexNumber getAddId() {
        return new ComplexNumber(0., 0.);
    }

    public static ComplexNumber getMultId() {
        return new ComplexNumber(1., 0.);
    }

    public static ComplexNumber sum(ComplexNumber a, ComplexNumber b){
        double x = a.re + b.re;
        double y = a.im + b.im;
        return new ComplexNumber(x, y);
    }

    public static ComplexNumber multiply(ComplexNumber a, ComplexNumber b){
        double x = a.re*b.re - a.im*b.im;
        double y = a.re*b.im + a.im*b.re;
        return new ComplexNumber(x, y);
    }

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

    public ComplexNumber(double re, double im){
        this.re = re;
        this.im = im;
    }

    public <T extends Number> ComplexNumber(T arg){
        re = arg.doubleValue();
        im = 0.0;
    }

    public double modulus(){
        return Math.sqrt(re*re + re*re);
    }

    public double modulus(boolean squared){
        double r2 = re*re + im*im;
        return squared ? r2 : Math.sqrt(r2);
    }

    public ComplexNumber getAddInverse(){
        return new ComplexNumber(-re, - im);
    }

    public ComplexNumber getMultInverse(){
        ComplexNumber conjugate = new ComplexNumber(re, -im);
        double r = modulus(true);
        double x = conjugate.re / r;
        double y = conjugate.im / r;
        return new ComplexNumber(x, y);
    }

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

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        if (re != 0.0){
            builder.append(String.format("%.4f", re));
            if (im > 0.0){
                builder.append(String.format(" + i%.3f", im));
            }
            else if (im < 0.0){
                builder.append(String.format(" - i%.3f", Math.abs(im)));
            }
        }
        else {
            if (im > 0.0){
                builder.append(String.format(" + i%.3f", im));
            }
            else if (im < 0.0){
                builder.append(String.format("-i%.3f", Math.abs(im)));
            }
            else return "0.0";
        }
        return builder.toString();
    }
}
