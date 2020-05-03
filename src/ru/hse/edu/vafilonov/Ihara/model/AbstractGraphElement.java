package ru.hse.edu.vafilonov.Ihara.model;

import javafx.scene.Node;
import javafx.scene.shape.Shape;

public abstract class AbstractGraphElement {

    protected static int nextHash = 0;

    protected int hash;

    protected GraphModel graph;

    public AbstractGraphElement(){
        hash = nextHash;
        nextHash++;
    }

    /**
     * Removes all connections to other elements and notifies them about deletion
     */
    public abstract void deleteElement();



    @Override
    public int hashCode() {
        return hash;
    }
}
