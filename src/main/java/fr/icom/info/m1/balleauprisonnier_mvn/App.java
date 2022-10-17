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
	 * On cree le SceneGraph de l'application ici
	 * @see <a href="https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm">Link</a>
	 *
	 */
	@Override
	public void start(Stage stage)
	{
		Game game = new Game(stage,
				Const.NB_EQ1,
				Const.NB_EQ2,
				Const.FIELD_DIM.width,
				Const.FIELD_DIM.height
		);

		GameView gv = new GameView(game.getGraphicsContext(), null);

		GameController gc = new GameController(
				game, gv
		);

		gc.start();

	}

	public static void main(String[] args)
	{
		//System.out.println( "Hello World!" );
		Application.launch(args);
	}
}
