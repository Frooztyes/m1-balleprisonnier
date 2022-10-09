package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public class Projectile {

    private int teamField;
    private boolean isMoving;
    private boolean isStatic;
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
    private Projectile(GraphicsContext gc, double angle, double speed, double x, double y) {
        this.graphicsContext = gc;
        this.angle = angle;
        this.speed = speed;
        arrowSprite = new Image("assets/ball.png");
        this.width = arrowSprite.getWidth();
        this.height = arrowSprite.getHeight();
        this.x = x - width/2;
        this.y = y - height/2;
        isStatic = false;
        rotation = 0;
    }

    private static Projectile instance;
    public static Projectile Instantiate(GraphicsContext gc, double angle, double speed, double x, double y) {
        if(instance == null) {
            instance = new Projectile(gc, angle, speed, x, y);
        }
        return instance;
    }

    public static Projectile getInstance() {
        return instance;
    }

    private double rotation;
    void display()
    {
        move();
        graphicsContext.save(); // saves the current state on stack, including the current transform
        rotate(graphicsContext, angle, x + arrowSprite.getWidth() / 2, y + arrowSprite.getHeight() / 2);
        if(!isStatic) rotation += 5;
        if(rotation >= 360) rotation = 0;
        graphicsContext.drawImage(arrowSprite, x, y);
        graphicsContext.restore(); // back to original state (before rotation)
    }

    void move() {
        if(holder != null) {
            x = holder.getCenterX();
            y = holder.getCenterY();
        } else {
            if(isStatic) return;
            double radAngle =  Math.toRadians(angle);
            double vX = -Math.cos(radAngle);
            double vY = Math.sin(radAngle);
            x += vY * speed;
            y += vX * speed;
        }
    }

    private Player holder;
    public void setHolder(Player p) {
        holder = p;
    }

    public void setStatic(boolean status) {
        this.isStatic = status;
        angle = 0;
        teamField = 0;
    }

    public void setStatic(boolean status, int position) {
        setStatic(status);
        teamField = position;
    }

    public int getTeamField() {
        return teamField;
    }

    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle + rotation, px, py);
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

    public boolean getStatic() {
        return isStatic;
    }


    public Player getHolder() {
        return this.holder;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }
}
