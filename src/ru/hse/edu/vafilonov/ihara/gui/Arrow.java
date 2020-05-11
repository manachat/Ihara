package ru.hse.edu.vafilonov.ihara.gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper class that represents arrow drawing for edge visualization
 * Arrow can have two visualizations:
 *      - undirected: only main line is visible
 *      - weighted-directed: wings and weight text are also visible
 *
 * @apiNote reinitialize() method should always be used
 *          after deserialization process of this class
 *          instance as graphics components are not being
 *          serialized (due to they don't support Serializable
 *          interface) and should be reinitialized properly.
 *          Otherwise, all getter getters and setters
 *          will throw IllegalStateException.
 * @see java.io.Serializable
 * @see java.lang.IllegalStateException
 * @version 2
 * @author Filonov Vsevolod
 */
public class Arrow implements Serializable {
    /*
    all constant values are not being serialized
    as they are already present in class file
    */

    /**
     * serialization version identifier
     */
    private static final long serialVersionUID = 2L;

    /**
     * length of arrow wing drawing
     */
    private static transient final double ARROW_WING = 10;

    /**
     * cos value used in arrow wing rotation
     */
    private static transient final double COS_WING = Math.cos(Math.PI / 12);

    /**
     * sin value used in arrow wing rotation
     */
    private static transient final double SIN_WING = Math.sin(Math.PI / 12);

    /**
     * color of the text used i drawing
     */
    private static transient final Paint TEXT_COLOR = Color.rgb(34,139,34);

    /**
     * coordinates of circle centers used in drawing
     * index 1 for origin point, 2 for end point
     * actual main line coordinates are calculated
     * using NODE_RADIUS value passed in constructor
     */
    private double x1, y1, x2, y2;

    /**
     * weight of the associated edge
     * used in text drawing
     */
    private double weight;

    /*
    graphics components are also not being serialized
    as they don't support serialization and should be reinitialized
     */

    /**
     * main line of the arrow
     * always visible
     */
    private transient Line mainLine;

    /**
     * first wing of the arrow
     */
    private transient Line firstWing;

    /**
     * second wing of the arrow
     */
    private transient Line secondWing;

    /**
     * weight text of the arrow
     */
    private transient Text weightText;

    /**
     * initialization flag
     */
    private transient boolean initialized = false;

    /**
     * Constructor
     * Weighted parameter defines if arc should have
     * undirected or weighted directed view (see class description)
     * @param x1 x coordinate of origin circle center
     * @param y1 y coordinate of origin circle center
     * @param x2 x coordinate of end circle center
     * @param y2 y coordinate of end circle center
     * @param nodeRadius radius of circle used in node visualization
     * @param weighted shows if arrow weight should be displayed
     * @param weight wight value of edge
     */
    Arrow(double x1, double y1, double x2, double y2, double nodeRadius, boolean weighted, double weight) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.weight = weight;

        // normalized vector calculation
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        vectorX /= length;
        vectorY /= length;

        // line ends coordinates
        x1 += (nodeRadius + 2) * vectorX;
        y1 += (nodeRadius + 2) * vectorY;
        x2 -= (nodeRadius + 2) * vectorX;
        y2 -= (nodeRadius + 2) * vectorY;
        mainLine = new Line(x1, y1, x2, y2);
        mainLine.setStrokeWidth(3);

        /*
        arrow wings coordinates calculated
        from main line end with rotation matrix
         */
        double xWproj = -ARROW_WING * vectorX;
        double yWproj = -ARROW_WING * vectorY;
        double x = xWproj * COS_WING - yWproj * SIN_WING;
        double y = xWproj * SIN_WING + yWproj * COS_WING;
        firstWing = new Line(x2, y2, x2 + x, y2 + y);
        firstWing.setStrokeWidth(2);

        x = xWproj * COS_WING + yWproj * SIN_WING;
        y = -xWproj * SIN_WING + yWproj * COS_WING;
        secondWing = new Line(x2, y2, x2 + x, y2 + y);
        secondWing.setStrokeWidth(2);

        // text configuration
        if (((vectorX >= 0) && (vectorY >= 0)) //text doesn't cross the arrow
                || ((vectorX <= 0) && (vectorY <= 0))) {
            weightText = new Text((x1 + x2) / 2., (y1 + y2) / 2.,
                    String.format("%.2f", weight));
        }
        else { //text crosses the arrow, need to move to the left
            weightText = new Text((x1 + x2) / 2. - 30, (y1 + y2) / 2.,
                    String.format("%.2f", weight));
        }
        weightText.setFont(new Font(15));
        weightText.setFill(TEXT_COLOR);
        weightText.setStroke(Color.BLACK);
        weightText.setStrokeWidth(0.5);

        // text is not weighted so nothig but main line should be displayed
        if (!weighted){
            firstWing.setVisible(false);
            secondWing.setVisible(false);
            weightText.setVisible(false);
        }

        // set initialization flag
        initialized = true;
    }

    /**
     * method used to reinitialize graphics
     * components after deserialization
     * copy of constructor method
     * @param nodeRadius radius of node
     * @param weighted shows if arrow should have undirected form
     */
    void reinitialize(double nodeRadius, boolean weighted){
        /* see constructor method for body description*/

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
        mainLine.setStrokeWidth(3);

        double xWproj = -ARROW_WING * vectorX;
        double yWproj = -ARROW_WING * vectorY;
        double x = xWproj * COS_WING - yWproj * SIN_WING;
        double y = xWproj * SIN_WING + yWproj * COS_WING;
        firstWing = new Line(x2, y2, x2 + x, y2 + y);
        firstWing.setStrokeWidth(2);

        x = xWproj * COS_WING + yWproj * SIN_WING;
        y = -xWproj * SIN_WING + yWproj * COS_WING;
        secondWing = new Line(x2, y2, x2 + x, y2 + y);
        secondWing.setStrokeWidth(2);

        if (((vectorX >= 0) && (vectorY >= 0))
                || ((vectorX <= 0) && (vectorY <= 0))) {
            weightText = new Text((x1 + x2) / 2., (y1 + y2) / 2., String.format("%.2f", weight));
        }
        else {
            weightText = new Text((x1 + x2) / 2. - 30, (y1 + y2) / 2., String.format("%.2f", weight));
        }

        weightText.setFont(new Font(15));
        weightText.setFill(TEXT_COLOR);
        weightText.setStroke(Color.BLACK);
        weightText.setStrokeWidth(0.5);
        if (!weighted){
            firstWing.setVisible(false);
            secondWing.setVisible(false);
            weightText.setVisible(false);
        }

        initialized = true;
    }

    /**
     * sets visibility of arrow to
     * either undirected or weighted-directed
     * @param visible sets visibility parameter
     * @exception  IllegalStateException if arrow
     *              wasn't reinitialized after deserialization
     */
    void setOrientationVisibility(boolean visible) {
        if (!initialized){
            throw new IllegalStateException("Arrow wasn't initialized");
        }

        if (visible){
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

    /**
     * sets weight value for arrow visualization
     * and constructs Text object according to it
     * @param weight weight of edge
     * @exception IllegalStateException if arrow
     *              wasn't reinitialized after deserialization
     */
    void setWeightText(double weight) {
        if (!initialized){
            throw new IllegalStateException("Arrow wasn't initialized");
        }

        this.weight = weight;
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;
        var handler = weightText.getOnMouseClicked();
        boolean visibility = weightText.isVisible();

        if ((vectorX >= 0) && (vectorY >= 0) || (vectorX <= 0) && (vectorY <= 0)) {
            weightText = new Text((x1 + x2) / 2., (y1 + y2) / 2., String.format("%.2f", weight));
        }
        else { //text crosses arrow, need to move to the left
            weightText = new Text((x1 + x2) / 2. - 30, (y1 + y2) / 2., String.format("%.2f", weight));
        }

        weightText.setFont(new Font(15));
        weightText.setFill(TEXT_COLOR);
        weightText.setStroke(Color.BLACK);
        weightText.setStrokeWidth(0.5);
        weightText.setVisible(visibility);
        weightText.setOnMouseClicked(handler);
    }

    /**
     * Getter method. Returns Text object
     * associated with weight
     * @return Text with weight
     * @throws IllegalStateException if arrow
     *          wasn't reinitialized after deserialization
     */
    Text getWeightText() throws IllegalStateException{
        if (!initialized){
            throw new IllegalStateException("Arrow wasn't initialized");
        }

        return weightText;
    }

    /**
     * Returns immutable list of graphics elements
     * @return list of graphics elements
     * @throws IllegalStateException if arrow
     *          wasn't reinitialized after deserialization
     */
    List<Shape> getAllElements() throws IllegalStateException{
        if (!initialized){
            throw new IllegalStateException("Arrow wasn't initialized");
        }

        return List.of(mainLine, firstWing, secondWing, weightText);
    }


}
