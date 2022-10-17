package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Human extends Player {

    public Human(String color, int xInit, int yInit, int side, double moveSpeed, ProjectileController ball, Image image) {
        super(color, xInit, yInit, side, moveSpeed, ball, image);
    }

    @Override
    public void shoot() {
        if(!hasBall) return;
        pc.send(angle, this.side);
        hasBall = false;
        sprite.playShoot();
    }

    @Override
    public void update() {

    }

    public void update(ArrayList<String> input, int indice) {
        Animate(input, indice);
    }

    public void Animate(ArrayList<String> input, int indice) {
        if(!this.isAlive) return;

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
    }
}
