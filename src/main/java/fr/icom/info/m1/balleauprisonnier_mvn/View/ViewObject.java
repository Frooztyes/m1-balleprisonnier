package fr.icom.info.m1.balleauprisonnier_mvn.View;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public abstract class ViewObject {
    protected GraphicsContext graphicsContext;
    protected Image image;

    public ViewObject(GraphicsContext gc, Image image) {
        this.graphicsContext = gc;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    protected void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
}
