package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import javafx.scene.image.Image;
import java.util.ArrayList;

public class Human extends Player {
    private final int indice;
    public Human(int xInit, int yInit, int side, double moveSpeed, ProjectileController ball, Image image, int indice) {
        super(xInit, yInit, side, moveSpeed, ball, image);
        this.indice = indice;
    }

    /**
     * Envoie la balle selon un angle et modifie le sprite du joueur.
     */
    @Override
    public void shoot() {
        if(!hasBall) return;
        pc.send(angle, this.side);
        hasBall = false;
        sprite.playShoot();
    }

    @Override
    public void update() { }

    public void update(ArrayList<String> input) {
        Animate(input);
    }

    /**
     * Déplace le joueur selon l'entrée clavier (relative à l'indice du joueur) et uniquement s'il est en vie.
     * Prends aussi en compte le lancement de la balle.
     */
    public void Animate(ArrayList<String> input) {
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
