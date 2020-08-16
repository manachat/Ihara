package ru.hse.edu.vafilonov.ihara.model.symbolic;

import javafx.scene.layout.Priority;

import java.util.ArrayList;
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
        int aSize = a.coefficients.size();
        int bSize = b.coefficients.size();
        boolean[] aCheck = new boolean[aSize];
        boolean[] bCheck = new boolean[bSize];
        for (int i = 0; i < aSize; i++) {
            aCheck[i] = false;
        }
        for (int i = 0; i < bSize; i++) {
            bCheck[i] = false;
        }

        if (aSize >= bSize) {
            
        } else {

        }
    }

    public Monomial copy() {
        PriorityQueue<PrimeRoot> coefs = new PriorityQueue<>(coefficients.size());
        for (PrimeRoot r : coefficients) {
            coefs.add(r.copy());
        }
        return new Monomial(coefs, sign, power);
    }
}


