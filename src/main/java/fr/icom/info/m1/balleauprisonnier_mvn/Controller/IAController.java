package fr.icom.info.m1.balleauprisonnier_mvn.Controller;

import fr.icom.info.m1.balleauprisonnier_mvn.Model.IA;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;

import java.util.List;

public class IAController extends PlayerController {
    public IAController(IA model, PlayerView view) {
        super(model, view);
    }

    public void setEnnemies(List<PlayerController> equipe) {
        ((IA) model).setEnnemies(equipe);
    }
}
