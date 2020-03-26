package ru.hse.edu.vafilonov.Ihara;

public class Node {
    private final double x;
    private final double y;
    private final double radius;

    public Node(double x, double y, double radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public double getDistanceTo(double x, double y){
        return Math.sqrt((x-this.x)*(x-this.x) + (y-this.y)*(y-this.y));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
