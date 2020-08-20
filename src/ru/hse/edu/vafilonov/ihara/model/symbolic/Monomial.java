package ru.hse.edu.vafilonov.ihara.model.symbolic;

import javafx.scene.layout.Priority;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Monomial {
    private PriorityQueue<PrimeRoot> coefficients = new PriorityQueue<>();
    private boolean sign = true;                // + by default
    private int power = 0;                      // without argument

    int irrational = 1; // product of irrational parts, used in reducers

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

    public boolean isSign() {
        return sign;
    }

    // product of irrational parts, used in reducers, package-private
    int getIrrational() {
        return irrational;
    }

    public boolean isAddId() {
        return (coefficients.size() == 0 && power == 0);
    }

    public boolean isMultId() {
        return (power == 0 && sign && (coefficients.size() == 1 && coefficients.peek().getRoot() == 1));
    }

    public boolean isInvMultId() {
        return (power == 0 && !sign && (coefficients.size() == 1 && coefficients.peek().getRoot() == 1));
    }

    public Monomial getAddInverse() {
        Monomial inv = copy();
        inv.sign = !sign;
        return inv;
    }

    public PriorityQueue<PrimeRoot> getCoefficients() {
        return coefficients;
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

        if (a.isInvMultId()) {
            return b.getAddInverse();
        }
        if (b.isInvMultId()) {
            return a.getAddInverse();
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

    public void multByArg(int power) {
        if (power < 0) {
            throw new IllegalArgumentException("Power of argument should be non-negative");
        }
        this.power += power;
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
        if (isInvMultId()) {
            return "-1";
        }

        StringBuilder builder = new StringBuilder();
        String coefs = coefsToString();

        if (coefs.equals("1")) {
            coefs = "";
        } else if (coefs.equals("-1")) {
            coefs = "-";
        }

        builder.append(coefs);
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

        reduce();

        int counter = 0;
        for (PrimeRoot p : coefficients) {
            if (counter > 0) {
                builder.append("\\cdot");
            }
            builder.append(p.toString());
            counter++;
        }

        return builder.toString();
    }

    public void reduce() {
        // reduce repeated roots
        ArrayList<PrimeRoot> reducedRoots = new ArrayList<>();
        // create stream, group by root and count replication of each root
        Stream<PrimeRoot> st = coefficients.stream();
        Map<Integer, List<PrimeRoot>> countedRoots = st.collect(Collectors.groupingBy(PrimeRoot::getRoot));
        countedRoots.forEach((root, rootList) -> {
            int replicationNumber = 0;
            for (PrimeRoot p : rootList) {
                replicationNumber += p.getReplication();
            }
            reducedRoots.add(new PrimeRoot(root, replicationNumber));
        });
        if (reducedRoots.size() > 1){
            reducedRoots.removeIf(r -> r.getRoot() == 1);
        }
        coefficients.clear();
        coefficients.addAll(reducedRoots);
    }
}

class MonomialPowerComparator implements Comparator<Monomial> {
    @Override
    public int compare(Monomial o1, Monomial o2) {
        return Integer.compare(o1.getPower(), o2.getPower());
    }
}


