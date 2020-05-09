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
    private final GraphModel MODEL;

    /**
     * saved arrows
     */
    private final List<Arrow> ARROWS;

    /**
     * saved circle x-coords
     */
    private final double[] CIRCLE_X;

    /**
     * saved circle y-coords
     */
    private final double[] CIRCLE_Y;

    /**
     * Constructor. Creates session object
     * @param model model ref.
     * @param arrows arrows refs.
     * @param circleX circle x coords.
     * @param circleY circle y coords
     */
    SavedSession(GraphModel model, List<Arrow> arrows, double[] circleX, double[] circleY){
        this.MODEL = model;
        this.ARROWS = arrows;
        this.CIRCLE_X = circleX;
        this.CIRCLE_Y = circleY;
    }

    /* getters for fields */

    GraphModel getMODEL() {
        return MODEL;
    }

    List<Arrow> getARROWS() {
        return ARROWS;
    }

    double[] getCIRCLE_X() {
        return CIRCLE_X;
    }

    double[] getCIRCLE_Y() {
        return CIRCLE_Y;
    }
}
