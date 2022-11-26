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
        // On nettoie le canvas à chaque frame
        graphicsContext.setFill(Color.LIGHTGRAY);

        graphicsContext.fillRect(0, 0, Const.FIELD_DIM.width, Const.FIELD_DIM.height + Const.OFFSET_FIELD);

        // Déplacement et affichage des joueurs
        handleInputs(equipe1, equipe2, input, ball);
    }

    private void handleInputs(List<PlayerController> equipe1, List<PlayerController> equipe2, ArrayList<String> input, ProjectileController ball) {
        // mise à jour des joueurs de l'équipe 1
        for (PlayerController p : equipe1) {
            p.updateView(input);
        }

        // mise à jour des joueurs de l'équipe 2
        for (PlayerController p : equipe2) {
            p.updateView(input);
        }

        // mise à jour de la balle
        ball.updateView();
    }
}
