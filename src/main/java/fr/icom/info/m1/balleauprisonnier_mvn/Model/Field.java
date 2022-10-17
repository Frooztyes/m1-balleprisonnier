package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Classe gerant le terrain de jeu.
 *
 */
public class Field extends Canvas {
	/** Tableau traçant les evenements */
	ArrayList<String> input = new ArrayList<>();


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

		/*
		 Event Listener du clavier
		 quand une touche est relachee on l'enleve de la liste d'input
		 */
		this.setOnKeyPressed(
				e -> {
					String code = e.getCode().toString();
					// only add once... prevent duplicates
					if ( !input.contains(code) )
						input.add( code );
				});


		this.setOnKeyReleased(
				e -> {
					String code = e.getCode().toString();
					input.remove( code );
				});

	}


	public ArrayList<String> getInput() {
		return input;
	}
}
