package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Node;

public class DoubleEdgedConnection extends AbstractGraphElement {
    private AbstractGraphElement head;
    private AbstractGraphElement tail;
    private double weight;

    public DoubleEdgedConnection(Node rep, AbstractGraphElement tail, AbstractGraphElement head){
        representation = rep;
        this.tail = tail;
        this.head = head;
    }

    @Override
    public void deleteElement(){

    }

    @Override
    public void connect(AbstractGraphElement el) {
        if (tail != null && head != null){
            throw new IllegalArgumentException(); //TODO: доделать
        }

        if (tail != null){
            tail = el;
        }
        else {
            head = el;
        }
    }

    @Override
    public void disconnect(AbstractGraphElement el){
        if (head == null && tail == null){
            throw new IllegalArgumentException(); //TODO: доделать
        }

        if (head != null){
            head = null;
        }
        else{
            tail = null;
        }
    }
}
