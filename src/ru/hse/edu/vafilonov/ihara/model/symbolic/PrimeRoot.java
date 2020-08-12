package ru.hse.edu.vafilonov.ihara.model.symbolic;

public class PrimeRoot {
    private int root;
    public PrimeRoot(int root) {
        if (root < 0 ) {
            throw new IllegalArgumentException("Root can't be negative");
        }
        // check for primal
        for (int i = 2; i <= root / 2; i++) {
            if (root % i == 0) {
                throw new IllegalArgumentException("Root is not primal");
            }
        }
        this.root = root;
    }
}
