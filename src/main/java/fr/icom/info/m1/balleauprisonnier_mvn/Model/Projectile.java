package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;

public class Projectile extends GameObject {
    private int teamField;
    private boolean isMoving;
    private boolean isStatic;
    private double rotation;
    private double vX;
    private double vY;
    private PlayerController holder;

    public Projectile(double angle, double speed, double x, double y, int initialDirection, double width, double height) {
        super(angle, speed, 5);
        this.width = width;
        this.height = height;
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

    /**
     * envoie la balle selon l'angle et effectue les modifications relatives à la position du lanceur.
     */
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

    /**
     * Déplace la balle sur le joueur quand il l'a porte
     * Déplace la balle si elle est en mouvement.
     */
    void move() {
        if(holder != null) {
            setAt(holder.getX(), holder.getY());
        } else {
            if(isStatic) return;
            x += vX * moveSpeed;
            y += vY * moveSpeed;
        }
    }

    /**
     * Met à jour la position de la balle (et rotation sur elle-même)
     */
    public void update() {
        move();
        if(!isStatic && holder == null) rotation += rotationSpeed;
        if(rotation >= 360) rotation = 0;
    }

    public void setHolder(PlayerController p) {
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

    public int getTeamfield() {
        return teamField;
    }


    public double getRotation() {
        return rotation;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public PlayerController getHolder() {
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

    public void setvX(double vX) {
        this.vX = vX;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }
}
