package fr.icom.info.m1.balleauprisonnier_mvn.View;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GameView extends ViewObject {
    public GameView(GraphicsContext gc, Image image) {
        super(gc, image);
    }

    public void display(List<PlayerController> equipe1, List<PlayerController> equipe2, ArrayList<String> input, ProjectileController ball) {
        // On nettoie le canvas a chaque frame
        graphicsContext.setFill(Color.LIGHTGRAY);

        graphicsContext.fillRect(0, 0, Const.FIELD_DIM.width, Const.FIELD_DIM.height + Const.OFFSET_FIELD);

        // Deplacement et affichage des joueurs
        handleInputs(equipe1, equipe2, input, ball);
    }

    private void handleInputs(List<PlayerController> equipe1, List<PlayerController> equipe2, ArrayList<String> input, ProjectileController ball) {
        for (PlayerController p : equipe1) {
            p.updateView(input, equipe1.indexOf(p));
        }

        for (PlayerController p : equipe2) {
            p.updateView(input, equipe2.indexOf(p));
        }

        ball.updateView();
    }
}
