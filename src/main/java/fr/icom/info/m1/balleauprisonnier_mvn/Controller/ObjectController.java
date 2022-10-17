package fr.icom.info.m1.balleauprisonnier_mvn.Controller;

import fr.icom.info.m1.balleauprisonnier_mvn.Model.GameObject;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ViewObject;

public abstract class ObjectController {
    protected GameObject model;
    protected ViewObject view;

    public ObjectController(GameObject model, ViewObject view) {
        this.model = model;
        this.view = view;
    }

    public double getX() {
        return model.getX();
    }

    public double getY() {
        return model.getY();
    }

    public double getWidth() {
        return model.getWidth();
    }

    public double getHeight() {
        return model.getHeight();
    }

    public abstract void updateView();
}
