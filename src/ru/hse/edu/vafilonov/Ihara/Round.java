package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Round extends Figure{
    public Round(double x, double y, double r, Paint paint){
        circle = new Circle(x, y, r, paint);
    }

    private Circle circle;

    public Circle getCircle() {
        return circle;
    }
}
