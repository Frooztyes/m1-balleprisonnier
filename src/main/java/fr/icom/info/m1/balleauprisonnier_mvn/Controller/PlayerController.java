package fr.icom.info.m1.balleauprisonnier_mvn.Controller;

import fr.icom.info.m1.balleauprisonnier_mvn.Model.Player;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.Sprite;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;

import java.util.ArrayList;

public abstract class PlayerController extends ObjectController {
    public PlayerController(Player model, PlayerView view) {
        super(model, view);
    }

    public boolean isAlive() {
        return ((Player) model).isAlive();
    }

    public void kill() {
        ((Player) model).kill();
    }
    public void setHasBall(boolean status) {
        ((Player) model).setHasBall(status);
    }

    public Sprite getSprite() {
        return ((Player) model).getSprite();
    }


    @Override
    public void updateView() {
    }

    public void updateView(ArrayList<String> input, int indice) {
        ((Player) model).update(input, indice);
        ((PlayerView) view).display(
                ((Player) model).hasBall(),
                ((Player) model).getSide(),
                model.getX(),
                model.getY(),
                model.getHeight(),
                model.getAngle()
        );

    }
}
