package ru.hse.edu.vafilonov.Ihara;

import javafx.scene.Scene;
import java.awt.Dimension;

public class BaseController {
    protected static Scene scene;
    protected static Dimension screenSize;

    public static void setScene(Scene s){
        scene = s;
    }

    public static void setScreenSize(Dimension size){
        screenSize = size;
    }
}
