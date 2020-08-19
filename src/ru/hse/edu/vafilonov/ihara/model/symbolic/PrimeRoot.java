package ru.hse.edu.vafilonov.ihara.model.symbolic;

class PrimeRoot implements Comparable<PrimeRoot> {
    private int root;
    private int replication = 1; // not used

    public PrimeRoot(int root) {
        if (root < 1) {
            throw new IllegalArgumentException("Root should be positive.");
        }
        // check for primal
        for (int i = 2; i <= root / 2; i++) {
            if (root % i == 0) {
                throw new IllegalArgumentException("Root is not primal.");
            }
        }
        this.root = root;
    }

    public PrimeRoot(int root, int replication) {
        this(root);
        if (replication < 1) {
            throw new IllegalArgumentException("Replication can't be less than 1.");
        }
        this.replication = replication;
    }

    /**
     * allows to set any number, needed to represent integer values
     * @param root
     * @param replication
     */
    void setNumber(int root, int replication) {
        this.root = root;
        this.replication = replication;
    }

    public int getRoot() {
        return root;
    }

    public int getReplication() {
        return replication;
    }

    public PrimeRoot copy() {
        return new PrimeRoot(root, replication);
    }

    @Override
    public int compareTo(PrimeRoot o) {
        return Integer.compare(this.root, o.root);
    }

    @Override
    public String toString() {
        if (root == 1) {
            return "1";
        }
        StringBuilder builder = new StringBuilder();
        if (replication > 1) {
            builder.append((int) Math.pow(root, replication >> 1)); // ide don't like my (replication / 2)
            if (replication % 2 != 0) {
                builder.append("\\sqrt{");
                builder.append(root);
                builder.append('}');
            }
        } else {
            builder.append("\\sqrt{");
            builder.append(root);
            builder.append('}');
        }
        return builder.toString();
    }
}
