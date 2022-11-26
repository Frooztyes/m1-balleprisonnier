package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.HumanController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.IAController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Game extends GameObject {
    public static boolean isPause = false;
    private int equipeBall;
    private Field field;
    private List<PlayerController> equipe1;
    private List<PlayerController> equipe2;
    private ProjectileController ball;
    private GraphicsContext gc;
    private ImageView imgPause;
    private ImageView imgPlay;
    private int indiceJoueur = 0;
    private final int width;
    private final int height;
    private final Stage stage;
    public Game(Stage stage, int width, int height) {
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
        // génère le bon joueur (controlleur et modèle) s'il est humain ou une IA
        // la vue est la même peut importe quel type le joueur est
        if(isHuman) {
            p = new Human(
                    this.width*(index+1)/(nbEq + 1),
                    height,
                    side,
                    Const.MOVESPEED,
                    ball,
                    new Image("assets/orc.png"),
                    indiceJoueur
            );
            pc = new HumanController(
                    (Human) p,
                    pv
            );
            indiceJoueur++;
        } else {
            p = new IA(
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

    /**
     * Génère les deux tableaux d'équipe et les joueurs
     */
    private void generateTeams() {
        equipe1 = new ArrayList<>();
        equipe2 = new ArrayList<>();
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

    /**
     * Génère la balle de jeu.
     */
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

        // création d'une instance du controlleur de la balle (singleton)
        ball = ProjectileController.Instantiate(p, pv);
    }

    /**
     * Fonction de vérification de superposition de deux rectangles.
     * Utilisée pour les collisions.
     */
    private boolean rectangleOverlap(Point l1, Point r1, Point l2, Point r2) {
        if (l1.x == r1.x || l1.y == r1.y || r2.x == l2.x || l2.y == r2.y)
            return false;

        if (l1.x > r2.x || l2.x > r1.x)
            return false;

        return l1.y <= r2.y && l2.y <= r1.y;
    }

    /**
     * Vérifie la collision des joueurs avec la balle.
     */
    private void check(List<PlayerController> listJoueur, int numEquipe) {
        if(numEquipe == equipeBall) return;
        List<PlayerController> toKill = new ArrayList<>();
        for (PlayerController rec : listJoueur)
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

    /**
     * vérifie l'ensemble des collisions (balle/terrain et balle/joueurs).
     */
    public void checkCollision() {
        if(!ball.isMoving()) {
            // arrête la balle lorsque elle atteint les bordures d'un des côtés.
            if(ball.getY() <= Const.HEIGHT_EQ2) ball.setStatic(true, Const.SIDE_TOP);
            else if(ball.getY() >= Const.HEIGHT_EQ1) ball.setStatic(true, Const.SIDE_BOT);
        }
        if(ball.getY() >= ((double) Const.FIELD_DIM.height / 2) - 10 && ball.getY() <= ((double) Const.FIELD_DIM.height / 2) + 10) {
            ball.setMoving(false);
        }
        if(ball.getX() <= 0 || ball.getX() >= (Const.FIELD_DIM.width - ball.getWidth())) {
            ball.setvX(-ball.getvX());
        }

        check(this.equipe1, 1);
        check(this.equipe2, 2);
    }


    /**
     * Génère le bouton pause.
     */
    private void generateButton(StackPane canvas) {
        GridPane gp = new GridPane();

        Button pauseButton = new Button();
        pauseButton.setPrefSize(48*3, 48);
        imgPause = new ImageView("assets/pause.png");
        imgPlay = new ImageView("assets/play.png");
        pauseButton.setFocusTraversable(false);
        pauseButton.setGraphic(imgPlay);

        pauseButton.setOnAction(actionEvent -> {
            Game.isPause = !Game.isPause;
            if(Game.isPause) pauseButton.setGraphic(imgPause);
            else pauseButton.setGraphic(imgPlay);
        });

        gp.setAlignment(Pos.BOTTOM_CENTER);
        GridPane.setMargin(pauseButton, new Insets(
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1
        ));
        GridPane.setConstraints(pauseButton, 0, 0);


        gp.getChildren().addAll( pauseButton);
        canvas.getChildren().add(gp);
    }

    /**
     * Génère l'ensemble de la scène de jeu (joueur, balle, bouton).
     */
    private void generateScene() {
        stage.setTitle("BalleAuPrisonnier");

        Group root = new Group();
        Scene scene = new Scene(root);

        GridPane gridpane = new GridPane();

        StackPane  canvas = new StackPane();

        canvas.setPrefSize(Const.SCREEN_DIM.width - Const.FIELD_DIM.width,Const.SCREEN_DIM.height);
        GridPane.setConstraints(canvas, 2, 0);

        generateButton(canvas);

        // On cree le terrain de jeu et on l'ajoute a la racine de la scene
        field = new Field(Const.FIELD_DIM.width, Const.FIELD_DIM.height);

        gc = field.getGraphicsContext2D();

        generateBall();
        generateTeams();

        GridPane.setConstraints(field, 1, 0);

        gridpane.getChildren().addAll(canvas, field);

        root.getChildren().add(gridpane);

        for(PlayerController p : this.equipe1) {
            root.getChildren().add(p.getSprite());
        }

        for(PlayerController p : this.equipe2) {
            root.getChildren().add(p.getSprite());
        }

        // On ajoute la scene a la fenetre et on affiche
        stage.setScene(scene);
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
    public void update() { }
}
