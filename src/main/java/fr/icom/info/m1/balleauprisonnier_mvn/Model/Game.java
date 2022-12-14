package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.HumanController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.IAController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Game extends GameObject {
    public boolean isPause = false;
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

    private Label victoryLabel;
    public Game(Stage stage, int width, int height) {
        super(0, 0, 0);
        this.stage = stage;
        this.width = width;
        this.height = height;

        generateScene();
    }

    public boolean isFinish() {
        AtomicInteger countAliveEq1 = new AtomicInteger();
        AtomicInteger countAliveEq2 = new AtomicInteger();
        equipe1.forEach((PlayerController e) -> {
            int _ = (e.isAlive()) ? countAliveEq1.getAndIncrement() : 0;
        });
        equipe2.forEach((PlayerController e) -> {
            int _ = (e.isAlive()) ? countAliveEq2.getAndIncrement() : 0;
        });
        if(countAliveEq1.intValue() == 0) {
            victoryLabel.setText("Victoire de l'??quipe 2 !");
        }
        if(countAliveEq2.intValue() == 0) {
            victoryLabel.setText("Victoire de l'??quipe 1 !");

        }
        return countAliveEq1.intValue() <= 0 && countAliveEq2.intValue() <= 0;
    }


    private PlayerController generatePlayer(int nbEq, int height, boolean isHuman, int index, int side) {
        PlayerController pc;
        Player p;
        PlayerView pv = new PlayerView(
                gc, new Image("assets/PlayerArrow.png")
        );
        // g??n??re le bon joueur (controlleur et mod??le) s'il est humain ou une IA
        // la vue est la m??me peut importe quel type le joueur est
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
     * G??n??re les deux tableaux d'??quipe et les joueurs
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
     * G??n??re la balle de jeu.
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

        // cr??ation d'une instance du controlleur de la balle (singleton)
        ball = ProjectileController.Instantiate(p, pv);
    }

    /**
     * Fonction de v??rification de superposition de deux rectangles.
     * Utilis??e pour les collisions.
     */
    private boolean rectangleOverlap(Point l1, Point r1, Point l2, Point r2) {
        if (l1.x == r1.x || l1.y == r1.y || r2.x == l2.x || l2.y == r2.y)
            return false;

        if (l1.x > r2.x || l2.x > r1.x)
            return false;

        return l1.y <= r2.y && l2.y <= r1.y;
    }

    /**
     * V??rifie la collision des joueurs avec la balle.
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
     * v??rifie l'ensemble des collisions (balle/terrain et balle/joueurs).
     */
    public void checkCollision() {
        if(!ball.isMoving()) {
            // arr??te la balle lorsque elle atteint les bordures d'un des c??t??s.
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
     * G??n??re le panneau de droite avec bouton, texte et slider.
     */
    private void generateSidePanel(StackPane canvas) {
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.TOP_CENTER);

        Button pauseButton = new Button();
        pauseButton.setPrefSize(48*3, 48);
        imgPause = new ImageView("assets/pause.png");
        imgPlay = new ImageView("assets/play.png");
        pauseButton.setFocusTraversable(false);
        pauseButton.setGraphic(imgPlay);

        pauseButton.setOnAction(actionEvent -> {
            isPause = !isPause;
            if(isPause) pauseButton.setGraphic(imgPause);
            else pauseButton.setGraphic(imgPlay);
        });
        GridPane.setHalignment(pauseButton, HPos.CENTER);
        GridPane.setConstraints(pauseButton, 0, 3); // col = 0 ; row = 2

        Label label = new Label("BALLE AU PRISONNIER");
        label.setFont(new Font(20));
        label.setStyle("-fx-font-weight: bold");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
//        label.setStyle("-fx-background-color: #000000;");

        GridPane.setMargin(label, new Insets(
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                0,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                0
        ));
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setConstraints(label, 0, 0);

        Text text = new Text();
        text.setFont(new Font(15));
        text.setText("Joueur haut : \nZ, Q, S et D pour se d??placer \n A pour tirer\n\n" +
                "Joueur bas : \n???, ???, ??? et ??? pour le joueur bas\n Numpad0 pour tirer\n\nVitesse de la balle : ");

        GridPane.setMargin(text, new Insets(
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1
        ));
        GridPane.setConstraints(text, 0, 1);

        Slider slider = new Slider(1, 10, 1);
        slider.adjustValue(5);

        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.25f);
        slider.setBlockIncrement(0.1f);

        slider.valueProperty().addListener((observableValue, number, t1) -> ball.setSpeed(number.doubleValue()));
        GridPane.setMargin(slider, new Insets(
                0,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1
        ));
        slider.setFocusTraversable(false);
        GridPane.setConstraints(slider, 0, 2);

        victoryLabel = new Label("");
        victoryLabel.setFont(new Font(20));
        victoryLabel.setStyle("-fx-font-weight: bold");
        victoryLabel.setTextAlignment(TextAlignment.CENTER);
        victoryLabel.setAlignment(Pos.CENTER);

        GridPane.setMargin(victoryLabel, new Insets(
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                0,
                (Const.SCREEN_DIM.width - Const.FIELD_DIM.width) * 0.1,
                0
        ));
        GridPane.setHalignment(victoryLabel, HPos.CENTER);
        GridPane.setConstraints(victoryLabel, 0, 5);


        gp.getChildren().addAll( pauseButton, label, text, slider, victoryLabel);
        canvas.getChildren().add(gp);
    }

    /**
     * G??n??re l'ensemble de la sc??ne de jeu (joueur, balle, bouton).
     */
    private void generateScene() {
        stage.setTitle("BalleAuPrisonnier");

        Group root = new Group();
        Scene scene = new Scene(root);

        GridPane gridpane = new GridPane();

        StackPane  canvas = new StackPane();

        canvas.setPrefSize(Const.SCREEN_DIM.width - Const.FIELD_DIM.width,Const.SCREEN_DIM.height);
        GridPane.setConstraints(canvas, 2, 0);

        generateSidePanel(canvas);

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

    public Field getField() {
        return field;
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

    public boolean isPause() {
        return isPause;
    }

    @Override
    public void update() { }
}
