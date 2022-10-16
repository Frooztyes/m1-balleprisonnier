package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected double x;
    protected double y;

    protected double width;
    protected double height;

    protected double angle;
    protected double moveSpeed;
    protected double rotationSpeed;

    protected Image image;

    protected GraphicsContext graphicsContext;
    public GameObject(GraphicsContext gc, double angle, double moveSpeed, double rotationSpeed, Image image) {
        this.angle = angle;
        this.graphicsContext = gc;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;
        this.image = image;
    }

    protected void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getAngle() { return angle; }
    public double getMoveSpeed() { return moveSpeed; }

    public void setAt(double x, double y) {
        this.x = x + this.width/2;
        this.y = y + this.height/2;
    }
}
