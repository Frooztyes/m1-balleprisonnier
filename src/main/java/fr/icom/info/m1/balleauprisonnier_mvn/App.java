package fr.icom.info.m1.balleauprisonnier_mvn;

import fr.icom.info.m1.balleauprisonnier_mvn.Controller.GameController;
import fr.icom.info.m1.balleauprisonnier_mvn.Model.Game;
import fr.icom.info.m1.balleauprisonnier_mvn.View.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale de l'application
 * s'appuie sur javafx pour le rendu
 */
public class App extends Application
{

	/**
	 * En javafx start() lance l'application
	 * <p>
	 * @see <a href="https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm">Link</a>
	 */
	@Override
	public void start(Stage stage)
	{
		// création du modèle jeu
		// génère le graphics context à envoyer à la vue
		Game game = new Game(stage,
				Const.FIELD_DIM.width,
				Const.FIELD_DIM.height
		);

		// création de la vue du jeu
		GameView gv = new GameView(game.getGraphicsContext(), null);

		// création du controlleur reliant modèle et vue
		GameController gc = new GameController(
				game, gv
		);

		// lancement de la partie
		gc.start();

	}

	public static void main(String[] args)
	{
		//System.out.println( "Hello World!" );
		Application.launch(args);
	}
}
