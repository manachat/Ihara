package ru.hse.edu.vafilonov.Ihara;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Controller {
    @FXML
    private Canvas mainCanvas;

    private final int nodeRadius = 5;

    private List<Node> nodes = new ArrayList<>();

    @FXML
    private void mainCanvasClickHandler(MouseEvent e){

        GraphicsContext canvasContext = mainCanvas.getGraphicsContext2D();
        double mouseX = e.getX();
        double mouseY = e.getY();
        canvasContext.setFill(Color.RED);
        for (Node n : nodes){
            double dist = n.getDistanceTo(mouseX, mouseY);
            if (dist < 2*nodeRadius){
                if (dist < nodeRadius){
                    System.out.println("Point (" + n.getX() + "," + n.getY() + ") clicked.");
                    canvasContext.setStroke(Color.BLACK);
                    nodeClickHandler(n, canvasContext);
                }
                return;
            }
        }

        nodes.add(new Node(mouseX, mouseY, nodeRadius));

        canvasContext.fillOval(mouseX - nodeRadius, mouseY - nodeRadius, 2*nodeRadius, 2*nodeRadius);
    }

    private void nodeClickHandler(Node node, GraphicsContext gc){
        gc.strokeOval(node.getX() - nodeRadius, node.getY() - nodeRadius, 2*nodeRadius, 2*nodeRadius);
    }
}
