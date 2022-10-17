package fr.icom.info.m1.balleauprisonnier_mvn.View;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ProjectileView extends ViewObject{


    public ProjectileView(GraphicsContext gc, Image image) {
        super(gc, image);
    }

    public void display(double x, double y, double rotation, double angle)
    {
        graphicsContext.save(); // saves the current state on stack, including the current transform
        rotate(graphicsContext, angle + rotation, x + image.getWidth() / 2, y + image.getHeight() / 2);
        graphicsContext.drawImage(image, x, y);
        graphicsContext.restore(); // back to original state (before rotation)
    }
}
