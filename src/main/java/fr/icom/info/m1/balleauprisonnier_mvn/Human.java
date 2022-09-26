package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Human extends Player {

    Human(GraphicsContext gc, String color, int xInit, int yInit, String side, double moveSpeed) {
        super(gc, color, xInit, yInit, side, moveSpeed);
    }

    public void Animate(ArrayList<String> input, int indice) {
        if(!this.isAlive) return;
        for(Projectile p : this.getListProjectiles()) {
            p.display();
        }

        if (indice==0 && input.contains("LEFT"))
        {
            this.moveLeft();
        }
        if (indice==0 && input.contains("RIGHT"))
        {
            this.moveRight();
        }
        if (indice==0 && input.contains("UP"))
        {
            this.turnLeft();
        }
        if (indice==0 && input.contains("DOWN"))
        {
            this.turnRight();
        }
        if (indice==1 && input.contains("Q"))
        {
            this.moveLeft();
        }
        if (indice==1 && input.contains("D"))
        {
            this.moveRight();
        }
        if (indice==1 && input.contains("Z"))
        {
            this.turnLeft();
        }
        if (indice==1 && input.contains("S"))
        {
            this.turnRight();
        }
        if (indice==0 && input.contains("NUMPAD0"))
        {
            this.shoot();
        }
        if (indice==1 && input.contains("A"))
        {
            this.shoot();
        }
        this.display();
    }
}
