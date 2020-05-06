package ru.hse.edu.vafilonov.Ihara.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * wrapper class for arc drawing
 */
public class Arrow {
    private final double arrowWing = 10;
    private final double cosWing = Math.cos(Math.PI / 12);
    private final double sinWing = Math.sin(Math.PI / 12);

    private double x1, y1, x2, y2; //saved circle values

    public Arrow(double x1, double y1, double x2, double y2, double nodeRadius, boolean weighted, double value) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        vectorX /= length;
        vectorY /= length;
        x1 += (nodeRadius + 2) * vectorX;
        y1 += (nodeRadius + 2) * vectorY;
        x2 -= (nodeRadius + 2) * vectorX;
        y2 -= (nodeRadius + 2) * vectorY;
        mainLine = new Line(x1, y1, x2, y2);
        mainLine.setStrokeWidth(2.5);
        double xWproj = -arrowWing * vectorX;
        double yWproj = -arrowWing * vectorY;
        double x = xWproj * cosWing - yWproj * sinWing;
        double y = xWproj * sinWing + yWproj * cosWing;
        firstWing = new Line(x2, y2, x2 + x, y2 + y);
        x = xWproj * cosWing + yWproj * sinWing;
        y = -xWproj * sinWing + yWproj * cosWing;
        secondWing = new Line(x2, y2, x2 + x, y2 + y);
        firstWing.setStrokeWidth(2);
        secondWing.setStrokeWidth(2);
        weightText = new Text((x1 + x2) / 2., (y1 + y2) / 2., String.format("%.3f", value));
        weightText.setFont(new Font(15));
        weightText.setFill(Color.BLUE);
        if (!weighted){
            firstWing.setVisible(false);
            secondWing.setVisible(false);
            weightText.setVisible(false);
        }
    }

    public void setOrientationVisibility(boolean value){
        if (value){
            firstWing.setVisible(true);
            secondWing.setVisible(true);
            weightText.setVisible(true);
        }
        else {
            firstWing.setVisible(false);
            secondWing.setVisible(false);
            weightText.setVisible(false);
        }
    }

    public void setWeightText(double weight){
        boolean visibility = weightText.isVisible();
        weightText = new Text((x1 + x2) / 2., (y1 + y2) / 2., String.format("%.3f", weight));
        weightText.setFont(new Font(15));
        weightText.setFill(Color.BLUE);
        weightText.setVisible(visibility);
    }

    public Line getMainLine(){
        return mainLine;
    }

    public Text getWeightText(){
        return weightText;
    }

    public List<Shape> getAllElements(){
        return List.of(mainLine, firstWing, secondWing, weightText);
    }

    private Line mainLine;
    private Line firstWing;
    private Line secondWing;
    private Text weightText;

}
