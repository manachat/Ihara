package ru.hse.edu.vafilonov.Ihara.gui;

import ru.hse.edu.vafilonov.Ihara.model.GraphModel;

import java.io.Serializable;
import java.util.List;

public class SavedSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private final GraphModel model;
    private final List<Arrow> arrows;
    private final double[] circleX;
    private final double[] circleY;

    public SavedSession(GraphModel model, List<Arrow> arrows, double[] circleX, double[] circleY){
        this.model = model;
        this.arrows = arrows;
        this.circleX = circleX;
        this.circleY = circleY;
    }

    public GraphModel getModel() {
        return model;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public double[] getCircleX() {
        return circleX;
    }

    public double[] getCircleY() {
        return circleY;
    }
}
