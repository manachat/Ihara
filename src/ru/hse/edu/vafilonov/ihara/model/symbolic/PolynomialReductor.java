package ru.hse.edu.vafilonov.ihara.model.symbolic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PolynomialReductor {
    /*
    public static String reduceFraction(PolynomialFraction fraction, char arg) {
        ArrayList<Monomial> numeratorRef = fraction.getNumerator().getTerms();
        ArrayList<Monomial> denominatorRef = fraction.getDenominator().getTerms();

        ArrayList<Monomial> numeratorCopy = new ArrayList<>(numeratorRef.size());
        ArrayList<Monomial> denominatorCopy = new ArrayList<>(denominatorRef.size());



        for (Monomial m : numeratorRef) {
            Monomial copy = m.copy();
            copy.reduce();
            numeratorCopy.add(copy);;
        }

        numeratorCopy.forEach((monomial) -> {
            int prod = 1;
            for (PrimeRoot r : monomial.getCoefficients()) {
                if (r.getReplication() % 2 != 0) {
                    prod *= r.getRoot();
                }
            }
            monomial.irrational = prod;
        });
        Map<Integer, List<Monomial>> powerGroups = numeratorCopy.stream().collect(Collectors.groupingBy(Monomial::getPower));
        powerGroups.forEach( (power, list) -> {

        });






        for (Monomial m : denominatorRef) {
            Monomial copy = m.copy();
            copy.reduce();
            denominatorCopy.add(copy);
        }





    }

     */
}
