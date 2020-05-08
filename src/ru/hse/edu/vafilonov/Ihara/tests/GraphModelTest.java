package ru.hse.edu.vafilonov.Ihara.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import ru.hse.edu.vafilonov.Ihara.model.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphModelTest {

    private GraphModel createTestModel(){
        GraphModel testModel = new GraphModel();
        GraphNode n1 = testModel.addNode();
        GraphNode n2 = testModel.addNode();
        GraphEdge e12 = testModel.addEdge(n1, n2);
        GraphNode n3 = testModel.addNode();
        testModel.addEdge(n2, n3);
        testModel.addEdge(n3, n1);
        return testModel;
    }

    @Test
    void serializationTest(){
        GraphModel testModel = createTestModel();
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("model.ihr"))){
            oos.writeObject(testModel);
        }
        catch (IOException ioex){
            System.out.println("fail writing");
        }
        GraphModel parsedModel = new GraphModel();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("model.ihr"))){
            parsedModel = (GraphModel) ois.readObject();
        }
        catch (IOException ioex){
            System.out.println("fail reading");
        }
        catch (ClassNotFoundException clex){
            System.out.println("Class not found");
        }
        List<GraphNode> origNodes = testModel.getGraphNodes();
        List<GraphEdge> origEdges = testModel.getGraphEdges();
        List<GraphNode> extractedNodes = parsedModel.getGraphNodes();
        List<GraphEdge> extractedEdges = parsedModel.getGraphEdges();
        for (var n : origNodes){
            System.out.print(n.hashCode() + ": -> ");
            for (var e : n.getConnections()){
                GraphNode ad = e.getOrigin() == n ? e.getTail() : e.getOrigin();
                System.out.print(ad.hashCode() + " ");
            }
            System.out.println();
        }
        System.out.println();
        for (var n : extractedNodes){
            System.out.print(n.hashCode() + ": -> ");
            for (var e : n.getConnections()){
                GraphNode ad = e.getOrigin() == n ? e.getTail() : e.getOrigin();
                System.out.print(ad.hashCode() + " ");
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < origEdges.size(); i++)
            System.out.print(origEdges.get(i).hashCode() + " ");
        System.out.println();
        for (int i = 0; i < extractedEdges.size(); i++)
            System.out.print(extractedEdges.get(i).hashCode() + " ");
        Assertions.assertEquals(testModel.hashCode(), parsedModel.hashCode());
    }
}