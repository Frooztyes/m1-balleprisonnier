package fr.icom.info.m1.balleauprisonnier_mvn;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
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


	final GraphicsContext gc;
	final int width;
	final int height;

	final int heightTeam1;
	final int heightTeam2;
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

		// On initialise le terrain de jeu
		double ms = 2;

		heightTeam1 = h - 64 - 20;
		heightTeam2 = 20;

		for (int i = 0; i < Const.NB_EQ1; i++) {
			equipe1.add(new Human(
					gc, colorMap[0],
					( w * ( i + 1 ))
							/
							( Const.NB_EQ1 + 1 ),
					heightTeam1, "bottom", ms));
			equipe1.get(i).display();
		}

		for (int i = 0; i < Const.NB_EQ2; i++) {
			equipe2.add(new IA(
					gc, colorMap[1],
					( w * ( i + 1 ))
							/
							( Const.NB_EQ2 + 1 ),
					heightTeam2, "top", ms, equipe1));
			equipe2.get(i).display();
		}

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
	private void check(List<Player> emetteur, List<Player> recepteur) {
		for (Player em : emetteur) {
			List<Projectile> toRemove = new ArrayList<>();
			for(Projectile p : em.getListProjectiles()) {
				List<Player> toKill = new ArrayList<>();
				for (Player rec : recepteur)
				{
					if(!rec.isAlive) continue;
					if(rectangleOverlap(
							new Point(
									(int) (p.getX() - (p.getWidth() / 2)),
									(int) (p.getY() - (p.getHeight() / 2))
							),
							new Point(
									(int) (p.getX() + (p.getWidth() / 2)),
									(int) (p.getY() + (p.getHeight() / 2))
							),
							new Point(
									(int) (rec.getCenterX() - (rec.width / 2)),
									(int) (rec.getCenterY() - (rec.height / 2))
							),
							new Point(
									(int) (rec.getCenterX() + (rec.width / 2)),
									(int) (rec.getCenterY() + (rec.height / 2))
							)
					)) {
						toKill.add(rec);
					}
				}

				for (Player player : toKill) {
					player.kill();
					toRemove.add(p);
				}

				if(p.getY() <= 0 && p.getX() <= 0 && p.getX() >= width) {
					if(!toRemove.contains(p))
						toRemove.add(p);
				}
			}

			for(Projectile p : toRemove) {
				em.getListProjectiles().remove(p);
			}
		}

	}
	private void checkCollision() {
		check(this.equipe1, this.equipe2);
		check(this.equipe2, this.equipe1);
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
	}

	public List<Player> getEquipe1() {
		return equipe1;
	}
	public List<Player> getEquipe2() {
		return equipe2;
	}
}
