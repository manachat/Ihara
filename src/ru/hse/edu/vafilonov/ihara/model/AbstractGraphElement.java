package ru.hse.edu.vafilonov.ihara.model;

/**
 * Base class for graph elements model
 * @version 2
 * @author Filonov Vsevolod
 */
public abstract class AbstractGraphElement {

    /**
     * hash pool. every object gets hash and increments field
     */
    protected static int nextHash = 0;

    /**
     * hash of current object
     */
    protected int hash;

    /**
     * Constructor for base class
     * manages hash field
     */
    AbstractGraphElement(){
        hash = nextHash;
        nextHash++;
    }

    /**
     * Removes all connections to other elements and notifies them about deletion
     */
    public abstract void deleteElement();

    /**
     * returns hash code value
     * @return hash value
     */
    @Override
    public int hashCode() {
        return hash;
    }
}
