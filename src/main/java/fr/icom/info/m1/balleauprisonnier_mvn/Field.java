package fr.icom.info.m1.balleauprisonnier_mvn;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe gerant le terrain de jeu.
 *
 */
public class Field extends Canvas {
	/** Joueurs */
	List<Player> equipe1 = new ArrayList<>();
	List<Player> equipe2 = new ArrayList<>();
	/** Couleurs possibles */
	String[] colorMap = new String[] {"blue", "green", "orange", "purple", "yellow"};
	/** Tableau traçant les evenements */
	ArrayList<String> input = new ArrayList<>();

	Projectile ball;



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

		// permet de capturer le focus et donc les evenements clavier et souris
		this.setFocusTraversable(true);

		gc = this.getGraphicsContext2D();
		ball = Projectile.Instantiate(gc,
				0,
				10,
				Const.FIELD_DIM.width / 2 - 100,
				Const.FIELD_DIM.height / 2,
				-1
		);



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

	private Player generateTeam(int nbEq, int height, boolean isHuman, int index, int side) {
		Player p;
		if(isHuman) {
			p = new Human(
					gc,
					colorMap[0],
					this.width*(index+1)/(nbEq + 1),
					height,
					side,
					Const.MOVESPEED
			);
		} else {
			p = new IA(gc,
					colorMap[0],
					this.width*(index+1)/(nbEq + 1),
					height,
					side,
					Const.MOVESPEED
			);
		}
		return p;
	}

	private void setIAEnnemies() {
		for(Player p : equipe1) {
			if(p.getClass() == IA.class) {
				((IA) p).setEnnemies(equipe2);
			}
		}
		for(Player p : equipe2) {
			if(p.getClass() == IA.class) {
				((IA) p).setEnnemies(equipe1);
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

	private void check(List<Player> recepteur, int numEquipe) {
		if(numEquipe == equipeBall) return;
		List<Player> toKill = new ArrayList<>();
		for (Player rec : recepteur)
		{
			if(!rec.isAlive) continue;
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
							(int) (rec.getX() - (rec.width / 2)),
							(int) (rec.getY() - (rec.height / 2))
					),
					new Point(
							(int) (rec.getX() + (rec.width / 2)),
							(int) (rec.getY() + (rec.height / 2))
					)
			)) {
				if(ball.getStatic()) {
					ball.setMoving(false);
					rec.setHasBall(ball);
					ball.setHolder(rec);
					equipeBall = numEquipe;
				} else {
					toKill.add(rec);
				}
			}
		}

		for (Player player : toKill) {
			player.kill();
		}

	}
	private void checkCollision() {
		if(!ball.isMoving()) {
			if(ball.getY() <= Const.HEIGHT_EQ2) ball.setStatic(true, Const.SIDE_TOP);
			else if(ball.getY() >= Const.HEIGHT_EQ1) ball.setStatic(true, Const.SIDE_BOT);
		}
		if(ball.getY() >= (Const.FIELD_DIM.height / 2) - 10 && ball.getY() <= (Const.FIELD_DIM.height / 2) + 10) {
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
		for (Player p : equipe1) {
			p.Animate(input, equipe1.indexOf(p));
		}

		for (Player p : equipe2) {
			p.Animate(input, equipe2.indexOf(p));
		}

		ball.display();
	}

	public List<Player> getEquipe1() {
		return equipe1;
	}
	public List<Player> getEquipe2() {
		return equipe2;
	}
}
