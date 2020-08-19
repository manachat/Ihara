package ru.hse.edu.vafilonov.ihara.model.symbolic;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean isInvMultId() {
        return (terms.size() == 1 && terms.get(0).isInvMultId());
    }

    public static Polynomial multiply(Polynomial a, Polynomial b) {
        if (a.isAddId() || b.isAddId()) {
            return new Polynomial(0);
        }
        if (a.isMultId()) {
            return b.copy();
        }
        if (b.isMultId()) {
            return a.copy();
        }

        ArrayList<Monomial> res = new ArrayList<>(a.terms.size() * b.terms.size());
        for (Monomial aM : a.terms) {
            for (Monomial bM : b.terms) {
                res.add(Monomial.multiply(aM, bM));
            }
        }
        return new Polynomial(res);
    }

    public static Polynomial sum(Polynomial a, Polynomial b) {
        if (a.isAddId()) {
            return b.copy();
        }
        if (b.isAddId()) {
            return a.copy();
        }

        ArrayList<Monomial> res = new ArrayList<>(a.terms.size() + b.terms.size());
        for (Monomial aM : a.terms) {
            res.add(aM.copy());
        }
        for (Monomial bM : b.terms) {
            res.add(bM.copy());
        }

        return new Polynomial(res);
    }

    public Polynomial copy() {
        ArrayList<Monomial> mons = new ArrayList<>(terms.size());
        for (Monomial m : terms){
            mons.add(m.copy());
        }
        return new Polynomial(mons);
    }

    @Override
    public String toString() {
        return toString('u');
    }

    public String toString(char arg) {
        if (isAddId()) {
            return "0";
        }
        if (isMultId()) {
            return "1";
        }

        StringBuilder builder = new StringBuilder();
        terms.sort(new MonomialPowerComparator());
        int currPower = -1;             // по идее power не должна быть меньше 0 в дробной структуре
        int monNumber = 0;              // number of monomials with this power

        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i).getPower() != currPower) { // write down previous coefs
                if (monNumber == 1) {
                    builder.append(terms.get(i - 1).toString(arg));
                }
                if (monNumber > 1) {
                    builder.append('(');
                    for (int j = i - monNumber; j < i; j++) {
                        builder.append(terms.get(j).coefsToString());
                    }
                    builder.append(')');
                    if (currPower > 0) {
                        builder.append(arg);
                        builder.append("^{");
                        builder.append(currPower);
                        builder.append('}');
                    }
                }

                currPower = terms.get(i).getPower();
                monNumber = 1;
            } else {
                monNumber++;
            }
        }
        // append last element
        if (monNumber == 1) {
            builder.append(terms.get(terms.size() - 1).toString(arg));
        }
        if (monNumber > 1) {
            builder.append('(');
            for (int j = terms.size() - monNumber; j < terms.size(); j++) {
                builder.append(terms.get(j).coefsToString());
            }
            builder.append(')');
            if (currPower > 0) {
                builder.append(arg);
                builder.append("^{");
                builder.append(currPower);
                builder.append('}');
            }
        }

        return builder.toString();
    }

    public void multByArg(int power) {
        if (power < 1) {
            throw new IllegalArgumentException("Power of argument should be positive");
        }
        for (Monomial m : terms) {
            m.multByArg(power);
        }
    }

    public void reduce() {
        ArrayList<Monomial> res = new ArrayList<>();
        terms.forEach((monomial) -> {
            int prod = 1;
            for (PrimeRoot r : monomial.getCoefficients()) {
                if (r.getReplication() % 2 != 0) {
                    prod *= r.getRoot();
                }
            }
            monomial.irrational = prod;
        });

        // разбить слагаемые по степени аргуменита
        Map<Integer, List<Monomial>> powerGroups = terms.stream().collect(Collectors.groupingBy(Monomial::getPower));
        powerGroups.forEach( (power, list) -> {
            // разбить слагаемые по иррациональному корню
            Map<Integer, List<Monomial>> irrGroups = list.stream().collect(Collectors.groupingBy(Monomial::getIrrational));
            irrGroups.forEach((irrational, irrlist) -> {

                if (irrlist.size() > 1) { // no need to optimize 1 element

                    int coef = 0;
                    // подсчитать сумму кэффициентов группы
                    for (Monomial mon : irrlist) {
                        int monCoefficient = 1;
                        // подсчитать целый коэффициент слагаемого
                        for (PrimeRoot r : mon.getCoefficients()) {
                            if (r.getReplication() > 1) {
                                monCoefficient *= (r.getRoot() * (r.getReplication() / 2)); // опять ide жалуется на деление
                            }
                        }
                        coef += mon.isSign() ? monCoefficient : -monCoefficient;
                    }

                    // reduce values and construct new reduced Monomial
                    if (coef != 0) {
                        boolean sign = coef > 0;
                        if (!sign) {
                            coef *= -1;
                        }
                        PrimeRoot rationalPart = new PrimeRoot(1);
                        rationalPart.setNumber(coef, 2);
                        PrimeRoot irrationalPart = new PrimeRoot(1);
                        irrationalPart.setNumber(irrational, 1);
                        PriorityQueue<PrimeRoot> insertion = new PriorityQueue<>();
                        insertion.offer(rationalPart);
                        insertion.offer(irrationalPart);
                        res.add(new Monomial(insertion, sign, power));
                    }
                } else {
                    res.add(irrlist.get(0));
                }
            });
        });

        terms = res;
    }
}
