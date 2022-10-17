package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public class Projectile extends GameObject {

    private int teamField;
    private boolean isMoving;
    private boolean isStatic;
    private static Projectile instance;

    private Projectile(GraphicsContext gc, double angle, double speed, double x, double y, int initialDirection) {
        super(gc, angle, speed, 5, new Image("assets/ball.png"));
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.x = x - width/2;
        this.y = y - height/2;
        isStatic = false;
        rotation = 0;


        double radAngle = Math.toRadians(angle);
        if(initialDirection == -1) {
            vX = Math.sin(radAngle);
            vY = -Math.cos(radAngle);
        }
            else {
            vX = -Math.sin(radAngle);
            vY = Math.cos(radAngle);
        }
    }

    public static Projectile Instantiate(GraphicsContext gc, double angle, double speed, double x, double y, int initialDirection) {
        if(instance == null) {
            instance = new Projectile(gc, angle, speed, x, y, initialDirection);
        }
        return instance;
    }

    public static Projectile getInstance() {
        return instance;
    }

    private double rotation;
    private double vX;
    private double vY;

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public void send(double angle, int sideSender) {
        if(holder == null) return;

        double radAngle = Math.toRadians(angle);
        this.angle = angle;
        if(sideSender == Const.SIDE_BOT) {
            vX = Math.sin(radAngle);
            vY = -Math.cos(radAngle);
        }
        else {
            vX = -Math.sin(radAngle);
            vY = Math.cos(radAngle);
        }
        this.setMoving(true, sideSender);
        this.holder = null;
    }

    void move() {
        if(holder != null) {
            setAt(holder.getX(), holder.getY());
        } else {
            if(isStatic) return;
            x += vX * moveSpeed;
            y += vY * moveSpeed;
        }
    }

    private Player holder;
    public void setHolder(Player p) {
        holder = p;
    }

    public void setStatic(boolean status) {
        this.isStatic = status;
        teamField = 0;
    }

    public void setStatic(boolean status, int position) {
        setStatic(status);
        teamField = position;
    }

    public int getTeamField() {
        return teamField;
    }

    void display()
    {
        move();
        graphicsContext.save(); // saves the current state on stack, including the current transform
        if(!isStatic && holder == null) rotation += rotationSpeed;
        if(rotation >= 360) rotation = 0;
        rotate(graphicsContext, angle + rotation, x + image.getWidth() / 2, y + image.getHeight() / 2);
        graphicsContext.drawImage(image, x, y);
        graphicsContext.restore(); // back to original state (before rotation)
    }



    public boolean getStatic() {
        return isStatic;
    }

    public Player getHolder() {
        return this.holder;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public void setMoving(boolean moving, int position) {
        this.setStatic(!moving);
        this.setMoving(moving);
        teamField = position;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }
}
