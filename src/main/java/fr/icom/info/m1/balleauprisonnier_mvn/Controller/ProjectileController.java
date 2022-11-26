package fr.icom.info.m1.balleauprisonnier_mvn.Controller;

import fr.icom.info.m1.balleauprisonnier_mvn.Model.Projectile;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;

public class ProjectileController extends ObjectController {
    private static ProjectileController instance;

    private ProjectileController(Projectile model, ProjectileView view) {
        super(model, view);
    }

    public static ProjectileController Instantiate(Projectile model, ProjectileView view) {
        if(instance == null) {
            instance = new ProjectileController(model, view);
        }
        return instance;
    }

    public void setHolder(PlayerController p) {
        ((Projectile) model).setHolder(p);
    }

    public boolean isStatic() {
        return ((Projectile) model).isStatic();
    }

    public boolean isMoving() {
        return ((Projectile) model).isMoving();
    }

    public void setMoving(boolean status) {
        ((Projectile) model).setMoving(status);
    }

    public void setStatic(boolean status, int position) {
        ((Projectile) model).setStatic(status, position);
    }

    public double getvX() {
        return ((Projectile) model).getvX();
    }

    public double getvY() {
        return ((Projectile) model).getvY();
    }

    public int getTeamfield() {
        return ((Projectile) model).getTeamfield();
    }

    public PlayerController getHolder() {
        return ((Projectile) model).getHolder();
    }

    public void send(double angle, int sideSender) {
        ((Projectile) model).send(angle, sideSender);
    }

    public void setvX(double vX) {
        ((Projectile) model).setvX(vX);
    }

    @Override
    public void updateView() {
        model.update();
        ((ProjectileView) view).display(model.getX(), model.getY(), ((Projectile) model).getRotation(), model.getAngle());
    }


}
