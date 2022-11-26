package fr.icom.info.m1.balleauprisonnier_mvn.Controller;

import fr.icom.info.m1.balleauprisonnier_mvn.Model.Game;
import fr.icom.info.m1.balleauprisonnier_mvn.View.GameView;
import javafx.animation.AnimationTimer;

public class GameController extends ObjectController {

    public GameController(Game model, GameView view) {
        super(model, view);
    }

    public void start() {
        /*
		 Boucle principale du jeu
		 handle() est appelee a chaque rafraichissement de frame
		 soit environ 60 fois par seconde.
		 */
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                if(Game.isPause) return;
                updateView();
            }
        }.start();
    }

    @Override
    public void updateView() {
        ((GameView) view).display(
                ((Game) model).getEquipe1(),
                ((Game) model).getEquipe2(),
                ((Game) model).getInput(),
                ((Game) model).getBall()
        );
        ((Game) model).checkCollision();
    }
}
