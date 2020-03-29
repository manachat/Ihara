package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class GraphNode extends AbstractGraphElement {
    private List<AbstractGraphElement> connections = new ArrayList<>();

    public GraphNode(Node rep){
        representation = rep;
    }


    @Override
    public void deleteElement(){ //по идее должен вызываться из модели
        for (var con : connections){
            con.disconnect(this);
        }
        connections.clear();
    }

    @Override
    public void connect(AbstractGraphElement el){
        connections.add(el);
    }

    @Override
    public void disconnect(AbstractGraphElement el){
        connections.remove(el);
    }

}
