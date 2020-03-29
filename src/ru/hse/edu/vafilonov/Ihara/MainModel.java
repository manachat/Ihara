package ru.hse.edu.vafilonov.Ihara;

import java.util.ArrayList;
import java.util.List;

public class MainModel {
    GraphNode[][] adjacencyMatrix = new GraphNode[10][10];
    int matrixSize = 0;

    private List<AbstractGraphElement> allGraphElements = new ArrayList<>(10);

    public void addElement(AbstractGraphElement el){
        allGraphElements.add(el);
    }
}
