package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public class Projectile {
    private double angle;
    private double speed;
    private GraphicsContext graphicsContext;
    private Image arrowSprite;

    // position x de la flêche
    private double x;
    // position y de la flêche
    private double y;
    private double width;
    private double height;
    public Projectile(GraphicsContext gc, double angle, double speed, double x, double y, Image directionProjectile) {
        this.graphicsContext = gc;
        this.angle = angle;
        this.speed = speed;
        arrowSprite = directionProjectile;
        this.width = arrowSprite.getWidth();
        this.height = arrowSprite.getHeight();
        this.x = x - width/2;
        this.y = y - height/2;
    }

    void display()
    {
        move();
        graphicsContext.save(); // saves the current state on stack, including the current transform
        rotate(graphicsContext, angle, x + arrowSprite.getWidth() / 2, y + arrowSprite.getHeight() / 2);
        graphicsContext.drawImage(arrowSprite, x, y);
        graphicsContext.restore(); // back to original state (before rotation)
    }

    void move() {
        double radAngle =  Math.toRadians(angle);
        double vX = -Math.cos(radAngle);
        double vY = Math.sin(radAngle);
        x += vY * speed;
        y += vX * speed;
    }

    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    public double getX() {
        return x + width/2;
    }

    public double getY() {
        return y;
    }
    public double getWidth() {
        return this.width;
    }
    public double getHeight() {
        return this.height;
    }
}
