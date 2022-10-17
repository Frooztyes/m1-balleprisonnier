package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.HumanController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.IAController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game extends GameObject {
    private int equipeBall;
    Field field;
    List<PlayerController> equipe1 = new ArrayList<>();
    List<PlayerController> equipe2 = new ArrayList<>();
    ProjectileController ball;
    GraphicsContext gc;

    /** Couleurs possibles */
    String[] colorMap = new String[] {"blue", "green", "orange", "purple", "yellow"};

    int width;
    int height;

    Stage stage;
    public Game(Stage stage, int nbJoueurEq1, int nbJoueurEq2, int width, int height) {
        // TODO: utiliser les variables nbJoueurs
        super(0, 0, 0);
        this.stage = stage;
        this.width = width;
        this.height = height;

        generateScene();
    }

    private PlayerController generatePlayer(int nbEq, int height, boolean isHuman, int index, int side) {
        PlayerController pc;
        Player p;
        PlayerView pv = new PlayerView(
                gc, new Image("assets/PlayerArrow.png")
        );
        if(isHuman) {
            p = new Human(
                    colorMap[0],
                    this.width*(index+1)/(nbEq + 1),
                    height,
                    side,
                    Const.MOVESPEED,
                    ball,
                    new Image("assets/orc.png")
            );
            pc = new HumanController(
                    (Human) p,
                    pv
            );
        } else {
            p = new IA(
                    colorMap[0],
                    this.width*(index+1)/(nbEq + 1),
                    height,
                    side,
                    Const.MOVESPEED,
                    ball,
                    new Image("assets/orc.png")
            );
            pc = new IAController(
                    (IA) p,
                    pv
            );
        }
        return pc;
    }

    private void generateTeams() {
        for (int i = 0; i < Const.NB_EQ1; i++) {
            equipe1.add(
                    generatePlayer(Const.NB_EQ1, Const.HEIGHT_EQ1, i < (Const.NB_EQ1 - Const.NB_IA_EQ1), i, Const.SIDE_BOT)
            );
        }
        for (int i = 0; i < Const.NB_EQ2; i++) {
            equipe2.add(
                    generatePlayer(Const.NB_EQ2, Const.HEIGHT_EQ2, i < (Const.NB_EQ2 - Const.NB_IA_EQ2), i, Const.SIDE_TOP)
            );
        }

        for(PlayerController p : equipe1) {
            if(p.getClass() == IAController.class) {
                ((IAController) p).setEnnemies(equipe2);
            }
        }
        for(PlayerController p : equipe2) {
            if(p.getClass() == IAController.class) {
                ((IAController) p).setEnnemies(equipe1);
            }
        }
    }

    private void generateBall() {
        ProjectileView pv = new ProjectileView(
                gc, new Image("assets/ball.png")
        );
        Projectile p = new Projectile(
                0,
                5,
                (double) Const.FIELD_DIM.width / 2 - 100,
                (double) Const.FIELD_DIM.height / 2,
                1,
                pv.getImage().getWidth(),
                pv.getImage().getHeight()
        );

        ball = ProjectileController.Instantiate(p, pv);
    }

    private boolean rectangleOverlap(Point l1, Point r1, Point l2, Point r2) {
        // if rectangle has area 0, no overlap
        if (l1.x == r1.x || l1.y == r1.y || r2.x == l2.x || l2.y == r2.y)
            return false;

        // If one rectangle is on left side of other
        if (l1.x > r2.x || l2.x > r1.x) {
            return false;
        }

        // If one rectangle is above other
        return l1.y <= r2.y && l2.y <= r1.y;
    }

    private void check(List<PlayerController> recepteur, int numEquipe) {
        if(numEquipe == equipeBall) return;
        List<PlayerController> toKill = new ArrayList<>();
        for (PlayerController rec : recepteur)
        {
            if(!rec.isAlive()) continue;
            if(rectangleOverlap(
                    new Point(
                            (int) (ball.getX() - (ball.getWidth() / 2)),
                            (int) (ball.getY() - (ball.getHeight() / 2))
                    ),
                    new Point(
                            (int) (ball.getX() + (ball.getWidth() / 2)),
                            (int) (ball.getY() + (ball.getHeight() / 2))
                    ),
                    new Point(
                            (int) (rec.getX() - (rec.getWidth() / 2)),
                            (int) (rec.getY() - (rec.getHeight() / 2))
                    ),
                    new Point(
                            (int) (rec.getX() + (rec.getWidth() / 2)),
                            (int) (rec.getY() + (rec.getHeight() / 2))
                    )
            )) {
                if(ball.isStatic()) {
                    ball.setMoving(false);
                    rec.setHasBall(true);
                    ball.setHolder(rec);
                    equipeBall = numEquipe;
                } else {
                    toKill.add(rec);
                }
            }
        }

        for (PlayerController player : toKill) {
            player.kill();
        }

    }

    public void checkCollision() {
        if(!ball.isMoving()) {
            if(ball.getY() <= Const.HEIGHT_EQ2) ball.setStatic(true, Const.SIDE_TOP);
            else if(ball.getY() >= Const.HEIGHT_EQ1) ball.setStatic(true, Const.SIDE_BOT);
        }
        if(ball.getY() >= ((double) Const.FIELD_DIM.height / 2) - 10 && ball.getY() <= ((double) Const.FIELD_DIM.height / 2) + 10) {
            ball.setMoving(false);
        }

        check(this.equipe1, 1);
        check(this.equipe2, 2);
    }

    private void generateScene() {
        stage.setTitle("BalleAuPrisonnier");

        Group root = new Group();
        Scene scene = new Scene(root);

        // On cree le terrain de jeu et on l'ajoute a la racine de la scene
        field = new Field(scene, Const.FIELD_DIM.width, Const.FIELD_DIM.height, root);
        gc = field.getGraphicsContext2D();

        generateBall();
        generateTeams();

        root.getChildren().add(field);

        for(PlayerController p : this.equipe1) {
            root.getChildren().add(p.getSprite());
        }

        for(PlayerController p : this.equipe2) {
            root.getChildren().add(p.getSprite());
        }

        // On ajoute la scene a la fenetre et on affiche
        stage.setScene( scene );
        stage.show();
    }

    public List<PlayerController> getEquipe1() {
        return equipe1;
    }

    public List<PlayerController> getEquipe2() {
        return equipe2;
    }

    public ProjectileController getBall() {
        return ball;
    }

    public ArrayList<String> getInput() {
        return field.getInput();
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    @Override
    public void update() {

    }
}
