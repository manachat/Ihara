package ru.hse.edu.vafilonov.ihara.model.symbolic;

import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class Monomial {
    private PriorityQueue<PrimeRoot> coefficients = new PriorityQueue<>();
    private boolean sign = true;                // + by default
    private int power = 0;                      // without argument

    public Monomial(int root) {
        if (root != 0) {                            // zero value is defined by lack of coefs
            coefficients.add(new PrimeRoot(root));
        }
    }

    public Monomial(PriorityQueue<PrimeRoot> coefs, boolean sign, int power) {
        coefficients = coefs;
        this.sign = sign;
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public boolean isAddId() {
        /*
        if (coefficients.size() == 0 && power == 0) {
            return true;
        }
        return false;
         */
        return (coefficients.size() == 0 && power == 0);
    }

    public boolean isMultId() {
        return (power == 0 && sign && (coefficients.size() == 1 && coefficients.peek().getRoot() == 1));
    }

    public Monomial getAddInverse() {
        Monomial inv = copy();
        inv.sign = !sign;
        return inv;
    }

    public static Monomial multiply(Monomial a, Monomial b) {
        if (a.isAddId() || b.isAddId()) {
            return new Monomial(0);
        }
        if (a.isMultId()) {
            return b.copy();
        }
        if (b.isMultId()) {
            return a.copy();
        }

        int power = a.power + b.power;
        boolean sign = (a.sign == b.sign);
        // copy coefficients
        PriorityQueue<PrimeRoot> resQueue = new PriorityQueue<>();
        for (PrimeRoot r : a.coefficients) {
            resQueue.add(r.copy());
        }
        for (PrimeRoot r : b.coefficients) {
            resQueue.add(r.copy());
        }

        return new Monomial(resQueue, sign, power);
    }

    public Monomial copy() {
        PriorityQueue<PrimeRoot> coefs = new PriorityQueue<>(coefficients.size());
        for (PrimeRoot r : coefficients) {
            coefs.add(r.copy());
        }
        return new Monomial(coefs, sign, power);
    }

    @Override
    public String toString() {
        return toString('u');   // 'u' by default
    }

    public String toString(char arg) {
        if (isAddId()) {
            return "0";
        }
        if (isMultId()) {
            return "1";
        }

        StringBuilder builder = new StringBuilder();
        if (!sign){
            builder.append('-');
        }
        for (PrimeRoot p : coefficients) {
            builder.append(p.toString());
        }
        if (power != 0) {
            builder.append(arg);
            builder.append("^{");
            builder.append(power);
            builder.append('}');
        }

        return builder.toString();
    }

    public String coefsToString() {
        if (isAddId()) {
            return "0";
        }
        if (isMultId()) {
            return "1";
        }

        StringBuilder builder = new StringBuilder();
        if (!sign){
            builder.append('-');
        }
        for (PrimeRoot p : coefficients) {
            builder.append(p.toString());
        }

        return builder.toString();
    }
}

class MonomialPowerComparator implements Comparator<Monomial> {
    @Override
    public int compare(Monomial o1, Monomial o2) {
        return Integer.compare(o1.getPower(), o2.getPower());
    }
}


