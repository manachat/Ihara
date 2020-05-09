package ru.hse.edu.vafilonov.ihara.gui;

import javafx.scene.Scene;
import java.awt.Dimension;

/**
 * Class represents base class for
 * all pages' controllers and provides
 * static fields for app functioning
 *
 * @apiNote setScene and setScreenSize methods
 *          should always be called BEFORE
 *          show() or showAndWait() methods.
 *          Otherwise, app behaviour is undefined.
 * @version 2
 * @author Filonov Vsevolod
 */
public class BaseController {
    /**
     * Reference to scene object for pages
     */
    static Scene scene;

    /**
     * Screen size settings
     */
    static Dimension screenSize;

    /**
     * Sets scene for controllers
     * @param s scene reference
     */
    public static void setScene(Scene s){
        scene = s;
    }

    /**
     * Sets screen size for cor controller
     * @param size screen size of user
     */
    public static void setScreenSize(Dimension size){
        screenSize = size;
    }
}
