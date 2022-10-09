package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;

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
    private final int CD_SHOOT_MAX = 150;
    private final int CD_SHOOT_MAX_OFFSET = 20;
    private List<Player> ennemies;
    private boolean canShoot;
    private boolean canMove = true;
    private double angleToGet;
    private double precision;


    IA(GraphicsContext gc, String color, int xInit, int yInit, String side, double moveSpeed) {
        super(gc, color, xInit, yInit, side, moveSpeed);
        this.cooldownMove = CD_MOVE;
        this.cooldownShoot = ThreadLocalRandom.current().nextInt(CD_SHOOT_MAX - CD_SHOOT_MAX_OFFSET, CD_SHOOT_MAX + CD_SHOOT_MAX_OFFSET);
        this.projectileSpeed = -projectileSpeed;
        this.precision = 0.85;
    }

    public void setEnnemies(List<Player> ennemies) {
        this.ennemies = ennemies;
    }

    /**
     * Donne l'endroit où il y a le plus d'adversaires en vie
     * @param alive liste d'ennemies en vie
     */
    private int getInterestCoef(List<Player> alive) {
        int n = 0;
        for(Player p : alive) {
            if(this.getCenterX() > p.getCenterX()) n++;
            else if(this.getCenterX() < p.getCenterX()) n--;
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
        List<Player> alives = new ArrayList<>();
        for (Player p : ennemies) {
            if(p.isAlive) alives.add(p);
        }

        // un seul adversaire en vie
        if(alives.size() == 1)
            return new Point(
                    (int)alives.get(0).getCenterX(),
                    (int)alives.get(0).getCenterY()
            );
        else if(alives.size()  == 0) return null;


        if(getInterestCoef(alives) == 0) {
            // dans le cas où il y a autant d'adversaires de chaque côté
            // on prends un adversaire aléatoire à viser
            int randomNum = ThreadLocalRandom.current().nextInt(0, alives.size());
            return new Point((int) alives.get(randomNum).getCenterX(), (int) alives.get(randomNum).getCenterY());
        } else {
            // on prends la position moyenne des adversaires
            int cpt = 0;
            for(Player p : alives) {
                if(getInterestCoef(alives) > 0) {
                    // on ignore ceux à gauche
                    if(this.getCenterX() < p.getCenterX()) continue;
                } else if(getInterestCoef(alives) < 0) {
                    // on ignore ceux à droite
                    if(this.getCenterX() > p.getCenterX()) continue;
                }
                if(x == -1) {
                    x = p.getCenterX();
                } else {
                    x += p.getCenterX();
                }
                if(y == -1) {
                    y = p.getCenterY();
                } else {
                    y += p.getCenterY();
                }
                cpt += 1;
            }
            return new Point(
                    (int) (x / cpt),
                    (int) (y / cpt)
            );
        }
    }

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
        if(side == 1) {
            delta_x = this.getCenterX() - middle.x;
            delta_y = this.getCenterY() - middle.y;
        } else {
            delta_x =  middle.x - this.getCenterX();
            delta_y =  middle.y - this.getCenterY();
        }

        // calcul de l'angle entre le point à atteindre et sois même
        double deg = (Math.atan2(delta_x, -delta_y) * 180) / Math.PI;
        // ajout d'imprécision
        double un_deg = deg * precision;
        if(((int) (deg - un_deg)) > (((int) (deg + un_deg)))) {
            deg = ThreadLocalRandom.current().nextInt((int) (deg + un_deg), (int) (deg - un_deg));
        } else if(((int) (deg - un_deg)) < (((int) (deg + un_deg)))) {
            deg = ThreadLocalRandom.current().nextInt((int) (deg - un_deg), (int) (deg + un_deg));
        }

        return deg;
    }

    public void Animate(ArrayList<String> input, int indice) {
        if(this.isAlive) {
            this.move();
            // si peut tirer, alors tire
            if(ball != null) {
                System.out.println(angleToGet);
                prepareShot();
            }
            this.display();
        }
    }

    public void prepareShot() {
        cooldownShoot--;
        if(angle != angleToGet) {
            if(Math.abs(angleToGet - angle) < 1) {
                angle = angleToGet;
            }

            if(angleToGet < angle) {
                turnRight();
            } else if(angleToGet > angle) {
                turnLeft();
            }
        }
        if(cooldownShoot <= 0 && canShoot) {
            if(angle == angleToGet) {
                ball.setStatic(false);
                ball.setMoving(true);
                cooldownShoot = ThreadLocalRandom.current().nextInt(CD_SHOOT_MAX - CD_SHOOT_MAX_OFFSET, CD_SHOOT_MAX + CD_SHOOT_MAX_OFFSET);
                ball.setAngle(angle);
                ball.setSpeed(-ball.getSpeed());
                shoot();
                ball.setHolder(null);
                ball = null;
            }
        }
    }

    private void goToBall() {
        if(Projectile.getInstance().getX() < this.x) {
            moveLeft();
        } else if(Projectile.getInstance().getX() > this.x){
            moveRight();
        }
    }


    public void move() {

        if(canMove) {
            if(this.side == Projectile.getInstance().getTeamField() && Projectile.getInstance().getHolder() == null) {
                goToBall();
            } else {
                if(dir) {
                    moveLeft();
                } else {
                    moveRight();
                }
                cooldownMove--;
            }
            angleToGet = calculateAngle();
        }



        // calcul de la prochaine direction où l'ia ira.
        if(cooldownMove <= 0) {
            cooldownMove = CD_MOVE;
            dir = ThreadLocalRandom.current().nextFloat() < 0.5;
        }
    }

    public void setHasBall(Projectile ball) {
        this.ball = ball;
        System.out.println("J'ai la balle ");
        cooldownShoot = ThreadLocalRandom.current().nextInt(CD_SHOOT_MAX - CD_SHOOT_MAX_OFFSET, CD_SHOOT_MAX + CD_SHOOT_MAX_OFFSET);

    }
}
