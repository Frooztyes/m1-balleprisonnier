package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;

/**
 * Classe gerant le terrain de jeu.
 */
public class Field extends Canvas {
	/** Tableau traçant les evenements */
	private final ArrayList<String> input = new ArrayList<>();

	/**
	 * Canvas dans lequel on va dessiner le jeu.
	 *
	 * @param w largeur du canvas
	 * @param h hauteur du canvas
	 *
	 */
	public Field(int w, int h)
	{
		super(w, h);

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
