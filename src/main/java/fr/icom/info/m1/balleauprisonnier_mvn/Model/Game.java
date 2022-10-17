//package fr.icom.info.m1.balleauprisonnier_mvn.Model;
//
//import fr.icom.info.m1.balleauprisonnier_mvn.Const;
//import fr.icom.info.m1.balleauprisonnier_mvn.Controller.PlayerController;
//import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
//import fr.icom.info.m1.balleauprisonnier_mvn.Field;
//import fr.icom.info.m1.balleauprisonnier_mvn.View.ProjectileView;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.stage.Stage;
//
//public class Game {
//    Field field;
//    List<PlayerController> eq1;
//    List<PlayerController> eq2;
//    Stage stage;
//    public Game(Stage stage) {
//        this.stage = stage;
//        generateScene();
//    }
//
//    private void generatePlayers() {
//
//    }
//
//    private void generateBall() {
//        ProjectileView pv = new ProjectileView(
//                gc, new Image("assets/ball.png")
//        );
//        Projectile p = new Projectile(
//                0,
//                5,
//                (double) Const.FIELD_DIM.width / 2 - 100,
//                (double) Const.FIELD_DIM.height / 2,
//                1,
//                pv.getImage().getWidth(),
//                pv.getImage().getHeight()
//        );
//
//        ball = ProjectileController.Instantiate(p, pv);
//    }
//
//    private void generateScene() {
//        stage.setTitle("BalleAuPrisonnier");
//
//        Group root = new Group();
//        Scene scene = new Scene(root);
//
//        Game game = new Game(stage);
//
//        // On cree le terrain de jeu et on l'ajoute a la racine de la scene
//        Field gameField = new Field(scene, Const.FIELD_DIM.width, Const.FIELD_DIM.height, root);
//
//        generateBall();
//        generatePlayers();
//
//        root.getChildren().add( gameField );
//
//        for(PlayerController p : this.eq1) {
//            root.getChildren().add(p.getSprite());
//        }
//
//        for(PlayerController p : this.eq2) {
//            root.getChildren().add(p.getSprite());
//        }
//
//        // On ajoute la scene a la fenetre et on affiche
//        stage.setScene( scene );
//        stage.show();
//    }
//}
