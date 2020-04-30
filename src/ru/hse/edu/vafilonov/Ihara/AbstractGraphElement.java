package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Node;
import javafx.scene.shape.Shape;

public abstract class AbstractGraphElement {

    protected GraphModel graph;

    /**
     * Connects element with graphical representation
     */
    protected Figure representation;

    /**
     * Removes all connections to other elements and notifies them about deletion
     */
    public abstract void deleteElement();


    /**
     * Connects current element to other
     * @param el element to be connected
     */
    protected abstract void connect(AbstractGraphElement el);

    /**
     * Disconnects element
     * @param el element that should be disconnected
     */
    protected abstract void disconnect(AbstractGraphElement el);

    public Figure getRepresentation(){
        return representation;
    }
}
