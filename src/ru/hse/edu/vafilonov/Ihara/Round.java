package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Round extends Figure{
    public Round(double x, double y, double r, Paint paint){
        circle = new Circle(x, y, r, paint);
    }

    private Circle circle;
    public Circle getCircle() {
        return circle;
    }

    @Override
    public List<Shape> getComponents(){
        List<Shape> ret = new ArrayList<>(1);
        ret.add(circle);
        return ret;
    }
}
