package ru.hse.edu.vafilonov.ihara.model.symbolic;

import java.util.ArrayList;

class Polynomial {

    private ArrayList<Monomial> terms = new ArrayList<>();

    public Polynomial(ArrayList<Monomial> terms) {
        this.terms = terms;
    }

    public Polynomial(int base) {
        if (base != 0) {
            terms.add(new Monomial(base));
        }
    }

    public ArrayList<Monomial> getTerms() {
        return terms;
    }

    public Polynomial getAddInverse() {
        ArrayList<Monomial> copy = new ArrayList<>(terms.size());
        for (Monomial m : terms) {
            copy.add(m.getAddInverse());
        }
        return new Polynomial(copy);
    }

    public boolean isAddId() {
        return terms.size() == 0;
    }

    public boolean isMultId() {
        return (terms.size() == 1 && terms.get(0).isMultId());
    }

    public static Polynomial multiply(Polynomial a, Polynomial b) {

    }

    public Polynomial copy() {
        ArrayList<Monomial> mons = new ArrayList<>(terms.size());
        for (Monomial m : terms){
            mons.add(m.copy());
        }
        return new Polynomial(mons);
    }
}
