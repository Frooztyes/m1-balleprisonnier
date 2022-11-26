package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import javafx.scene.image.Image;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class IA extends Player {
    private int cooldownMove;
    private int cooldownShoot;
    // dir true = left, false = right
    private boolean dir = false;
    private final int CD_MOVE = 25;
    private List<PlayerController> ennemies;
    private boolean canShoot;
    private double angleToGet;
    private int state;

    public IA(int xInit, int yInit, int side, double moveSpeed, ProjectileController ball, Image image) {
        super(xInit, yInit, side, moveSpeed, ball, image);
        this.cooldownMove = CD_MOVE;
        this.projectileSpeed = -projectileSpeed;
        this.state = Const.STATE_MOVE;
    }

    public void setEnnemies(List<PlayerController> ennemies) {
        this.ennemies = ennemies;
    }

    /**
     * Calcul le coefficient d'intérêt selon les ennemis présent dans le camps adverse
     * Ce coefficient correspond à l'endroit où il y a le plus d'ennemis par rapport à soit même.
     */
    private int getInterestCoef(List<PlayerController> alive) {
        int n = 0;
        for(PlayerController p : alive) {
            if(this.getX() > p.getX()) n++;
            else if(this.getX() < p.getX()) n--;
        }
        return n;
    }

    /**
     * si ennemies à gauche :
     *    viser vers les ennemies à gauche
     *    prendre la moyenne des positions à gauche
     * <p>
     * si ennemies à droite :
     *    viser vers les ennemies à droite
     *    prendre la moyenne des positions à droite
     * <p>
     * si égalité :
     *    viser vers un ennemis aléatoire
     * <p>
     * si un seul ennemi:
     *    viser dessus
     */
    private Point getFocusPoint() {
        double x = -1;
        double y = -1;

        // récupération des adversaires en vie
        List<PlayerController> alives = new ArrayList<>();
        for (PlayerController p : ennemies) {
            if(p.isAlive()) alives.add(p);
        }

        // un seul adversaire en vie
        if(alives.size() == 1)
            return new Point(
                    (int)alives.get(0).getX(),
                    (int)alives.get(0).getY()
            );
        else if(alives.size()  == 0) return null;


        if(getInterestCoef(alives) == 0) {
            // dans le cas où il y a autant d'adversaires de chaque côté
            // on prends un adversaire aléatoire à viser
            int randomNum = ThreadLocalRandom.current().nextInt(0, alives.size());
            return new Point((int) alives.get(randomNum).getX(), (int) alives.get(randomNum).getY());
        } else {
            // on prends la position moyenne des adversaires
            int cpt = 0;
            for(PlayerController p : alives) {
                if(getInterestCoef(alives) > 0) {
                    // on ignore ceux à gauche
                    if(this.getX() < p.getX()) continue;
                } else if(getInterestCoef(alives) < 0) {
                    // on ignore ceux à droite
                    if(this.getX() > p.getX()) continue;
                }
                if(x == -1) {
                    x = p.getX();
                } else {
                    x += p.getX();
                }
                if(y == -1) {
                    y = p.getY();
                } else {
                    y += p.getY();
                }
                cpt += 1;
            }
            return new Point(
                    (int) (x / cpt),
                    (int) (y / cpt)
            );
        }
    }

    /**
     * Calcul l'angle où l'IA doit viser depuis son point à atteindre.
     */
    private double calculateAngle() {
        Point middle = this.getFocusPoint();

        if(middle == null) {
            canShoot = false;
            return 0;
        } else {
            canShoot = true;
        }

        double delta_x;
        double delta_y;

        if(side == Const.SIDE_TOP) {
            delta_x = this.getX() - middle.x;
            delta_y = this.getY() - middle.y;
        } else {
            delta_x =  middle.x - this.getX();
            delta_y =  middle.y - this.getY();
        }

        // calcul de l'angle entre le point à atteindre et sois même
        double deg = (Math.atan2(delta_x, -delta_y) * 180) / Math.PI;

        // correction de l'angle.
        if(deg < 0) {
            deg = 360 - deg;
            deg = deg % 360;
            if(deg > 180) deg = deg - 180;
            else deg = 360 - deg;
        }

        return (int) deg;
    }

    @Override
    public void update() { }

    public void update(ArrayList<String> input) {
        Animate(input);
    }

    /**
     * Gère le comportement de l'ia selon son état.
     * @see <a href="https://gamedevelopment.tutsplus.com/tutorials/finite-state-machines-theory-and-implementation--gamedev-11867">Finite-State Machines: Theory and Implementation</a>
     */
    public void Animate(ArrayList<String> input) {
        switch (state) {
            case Const.STATE_MOVE:
                this.move();
                break;

            case Const.STATE_PREPARE_SHOOT:
                this.move();
                this.prepareShot();
                break;

            case Const.STATE_SHOOT:
                this.shoot();
                break;

            case Const.STATE_DEAD:
                // nothing
                break;

            default:
                break;
        }
    }

    /**
     * Envoie la balle.
     */
    @Override
    public void shoot() {
        if(!hasBall) {
            state = Const.STATE_MOVE;
        }

        pc.send(angle, this.side);
        super.shoot();
        hasBall = false;
        // changement d'état pour recalculer les mouvements.
        state = Const.STATE_MOVE;
    }

    /**
     * Prépare le lancement de la balle. Effectue le lancer lorsque l'angle à atteindre est atteint.
     */
    public void prepareShot() {
        cooldownShoot--;
        if(angle != angleToGet) {
            if(Math.abs(angleToGet - angle) < 1) {
                angle = angleToGet;
            }
            if(Math.abs(angle - angleToGet) < 180) {
                if(angleToGet < angle) {
                    turnRight();
                } else if(angleToGet > angle) {
                    turnLeft();
                }
            } else {
                if(angleToGet < angle) {
                    turnLeft();
                } else if(angleToGet > angle) {
                    turnRight();
                }
            }
        }

        if(cooldownShoot <= 0 && canShoot) {
            if(angle == angleToGet) {
                // changement d'état pour lancer la balle.
                state = Const.STATE_SHOOT;
            }
        }
    }

    /**
     * Déplace le joueur vers la balle.
     */
    private void goToBall() {
        if(this.x > pc.getX()) {
            moveLeft();
        } else if(this.x < pc.getX()){
            moveRight();
        }
    }

    /**
     * Eloigne le joueur de la position finale de la balle.
     */
    private void dodgeBall() {
        // f(x) = ax + b = teamHeight
        int heightToGet;
        if(this.side == Const.SIDE_BOT) {
            heightToGet = Const.HEIGHT_EQ1;
        } else {
            heightToGet = Const.HEIGHT_EQ2;
        }

        // maths
        double xA = pc.getX();
        double yA = pc.getY();


        double vX = pc.getvX();
        double vY = pc.getvY();


        double xB = xA + vX;
        double yB = yA + vY;
        double a = (yA - yB) / (xA - xB);
        double b = yA - xA * a;
        double x = (heightToGet - b) / a;

        if(x < this.x) {
            moveRight();
        } else if(x > this.x) {
            moveLeft();
        }
    }


    /**
     * Déplace le joueur aléatoirement, esquivant la balle ou allant vers elle.
     */
    public void move() {
        if(!this.isAlive) {
            state = Const.STATE_DEAD;
            return;
        }

        if(pc.isStatic() && this.side == pc.getTeamfield() && pc.getHolder() == null) {
            // balle arrêtée sur le terrain
            goToBall();
        } else if(!pc.isStatic() && this.side != pc.getTeamfield()) {
            // balle en mouvement vers le terrain
            dodgeBall();
        } else {
            if(dir) {
                moveLeft();
            } else {
                moveRight();
            }
            cooldownMove--;
        }
        angleToGet = calculateAngle();

        // calcul de la prochaine direction où l'ia ira.
        if(cooldownMove <= 0) {
            cooldownMove = CD_MOVE;
            dir = ThreadLocalRandom.current().nextFloat() < 0.5;
        }

        // dès que le joueur a la balle, il passe en état préparation du tir.
        if(hasBall) state = Const.STATE_PREPARE_SHOOT;
    }

    public void setHasBall(boolean status) {
        hasBall = status;
        cooldownShoot = ThreadLocalRandom.current().nextInt(Const.CD_SHOOT_MAX - Const.CD_SHOOT_MAX_OFFSET, Const.CD_SHOOT_MAX + Const.CD_SHOOT_MAX_OFFSET);
    }

    public void kill() {
        if(isAlive) {
            isAlive = false;
            sprite.playDie();
            // changement d'état pour signifier la mort.
            state = Const.STATE_DEAD;
        }
    }
}
