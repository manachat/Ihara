package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * represents
 */
public class Arc extends Figure{

    public Arc(double x1, double y1, double x2, double y2){
        line = new Line()
    }

    private Line line;
    private Line left;
    private Line right;

    @Override
    public List<Shape> getComponents(){
        List<Shape> ret = new ArrayList<>(3);
        ret.add(line);
        ret.add(left);
        ret.add(right);
        return
    }
}
