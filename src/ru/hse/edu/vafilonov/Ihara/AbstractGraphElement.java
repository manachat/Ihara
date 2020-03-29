package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Node;

public abstract class AbstractGraphElement {

    /**
     * Connects element with graphical representation
     */
    protected Node representation;

    /**
     * Removes all connections to other elements and notifys them about deletion
     */
    public abstract void deleteElement();


    /**
     * Connects current element to other
     * @param el element to be connected
     */
    public abstract void connect(AbstractGraphElement el);

    /**
     * Disconnects element
     * @param el element that should be disconnected
     */
    public abstract void disconnect(AbstractGraphElement el);
}
