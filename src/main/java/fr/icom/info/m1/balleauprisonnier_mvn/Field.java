package fr.icom.info.m1.balleauprisonnier_mvn;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import fr.icom.info.m1.balleauprisonnier_mvn.Controller.HumanController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.IAController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.Human;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.IA;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.Player;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.Projectile;
import fr.icom.info.m1.balleauprisonnier_mvn.View.PlayerView;
import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Classe gerant le terrain de jeu.
 *
 */
public class Field extends Canvas {
	/** Joueurs */
	List<PlayerController> equipe1 = new ArrayList<>();
	List<PlayerController> equipe2 = new ArrayList<>();
	/** Couleurs possibles */
	String[] colorMap = new String[] {"blue", "green", "orange", "purple", "yellow"};
	/** Tableau traçant les evenements */
	ArrayList<String> input = new ArrayList<>();

	ProjectileController ball;



	final GraphicsContext gc;
	final int width;
	final int height;

	Scene s;
	Group grp;

	/**
	 * Canvas dans lequel on va dessiner le jeu.
	 *
	 * @param scene Scene principale du jeu a laquelle on va ajouter notre Canvas
	 * @param w largeur du canvas
	 * @param h hauteur du canvas
	 *
	 */
	public Field(Scene scene, int w, int h, Group root)
	{
		super(w, h);
		this.s = scene;
		this.grp = root;
		this.width = w;
		this.height = h;
		gc = this.getGraphicsContext2D();

		// permet de capturer le focus et donc les evenements clavier et souris
		this.setFocusTraversable(true);

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



		// On initialise le terrain de jeu
		generateEnnemies();

		/*
		 Event Listener du clavier
		 quand une touche est pressee on la rajoute a la liste d'input
		 */
		this.setOnKeyPressed(
				e -> {
					String code = e.getCode().toString();
					// only add once... prevent duplicates
					if ( !input.contains(code) )
						input.add( code );
				});

		/*
		 Event Listener du clavier
		 quand une touche est relachee on l'enleve de la liste d'input
		 */
		this.setOnKeyReleased(
				e -> {
					String code = e.getCode().toString();
					input.remove( code );
				});


		/*
		 Boucle principale du jeu
		 handle() est appelee a chaque rafraichissement de frame
		 soit environ 60 fois par seconde.
		 */
		new AnimationTimer()
		{
			public void handle(long currentNanoTime)
			{
				// On nettoie le canvas a chaque frame
				gc.setFill(Color.LIGHTGRAY);

				gc.fillRect(0, 0, Const.FIELD_DIM.width, Const.FIELD_DIM.height + Const.OFFSET_FIELD);

				// Deplacement et affichage des joueurs
				handleInputs();

				checkCollision();
			}
		}.start(); // On lance la boucle de rafraichissement

	}

	private PlayerController generateTeam(int nbEq, int height, boolean isHuman, int index, int side) {
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

	private void setIAEnnemies() {
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

	private void generateEnnemies() {
		for (int i = 0; i < Const.NB_EQ1; i++) {
			equipe1.add(
					generateTeam(Const.NB_EQ1, Const.HEIGHT_EQ1, i < (Const.NB_EQ1 - Const.NB_IA_EQ1), i, Const.SIDE_BOT)
			);
		}
		for (int i = 0; i < Const.NB_EQ2; i++) {
			equipe2.add(
					generateTeam(Const.NB_EQ2, Const.HEIGHT_EQ2, i < (Const.NB_EQ2 - Const.NB_IA_EQ2), i, Const.SIDE_TOP)
			);
		}
		setIAEnnemies();
	}

	private int equipeBall;

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
	private void checkCollision() {
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

	private void handleInputs() {
		for (PlayerController p : equipe1) {
			p.updateView(input, equipe1.indexOf(p));
		}

		for (PlayerController p : equipe2) {
			p.updateView(input, equipe2.indexOf(p));
		}

		ball.updateView();
	}

	public List<PlayerController> getEquipe1() {
		return equipe1;
	}
	public List<PlayerController> getEquipe2() {
		return equipe2;
	}
}
