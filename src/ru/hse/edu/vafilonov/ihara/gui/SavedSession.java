package ru.hse.edu.vafilonov.ihara.gui;

import ru.hse.edu.vafilonov.ihara.model.GraphModel;

import java.io.Serializable;
import java.util.List;

/**
 * Class represents saved session file
 * Uses serialization
 * @see java.io.Serializable
 * @version 2
 * @author Filonov Vsevolod
 */
public class SavedSession implements Serializable {
    /**
     * serialization version identifier
     */
    private static final long serialVersionUID = 2L;

    /**
     * model reference
     */
    private final GraphModel model;

    /**
     * saved arrows
     */
    private final List<Arrow> arrows;

    /**
     * saved circle x-coords
     */
    private final double[] circleX;

    /**
     * saved circle y-coords
     */
    private final double[] circleY;

    /**
     * Constructor. Creates session object
     * @param model model ref.
     * @param arrows arrows refs.
     * @param circleX circle x coords.
     * @param circleY circle y coords
     */
    SavedSession(GraphModel model, List<Arrow> arrows, double[] circleX, double[] circleY){
        this.model = model;
        this.arrows = arrows;
        this.circleX = circleX;
        this.circleY = circleY;
    }

    /* getters for fields */

    GraphModel getModel() {
        return model;
    }

    List<Arrow> getArrows() {
        return arrows;
    }

    double[] getCircleX() {
        return circleX;
    }

    double[] getCircleY() {
        return circleY;
    }
}
