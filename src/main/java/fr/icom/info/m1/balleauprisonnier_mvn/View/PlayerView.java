package fr.icom.info.m1.balleauprisonnier_mvn.View;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PlayerView extends ViewObject {
    public PlayerView(GraphicsContext gc, Image image) {
        super(gc, image);
    }

    public void display(boolean hasBall, int side, double x, double y, double height, double angle)
    {
        if(!hasBall) return;
		graphicsContext.save(); // saves the current state on stack, including the current transform
		if(side == Const.SIDE_BOT) {
			rotate(graphicsContext, angle, x + image.getWidth() / 2, y + height/2);
		}
		else {
			rotate(graphicsContext, (angle + 180) % 360, x + image.getWidth() / 2, y + height/2);
		}
		graphicsContext.drawImage(image, x, y - height/2);
		graphicsContext.restore(); // back to original state (before rotation)
    }
}
