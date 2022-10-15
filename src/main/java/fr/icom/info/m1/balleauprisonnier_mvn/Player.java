package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe gerant un joueur
 */
public abstract class Player
{
	protected Projectile ball;

	protected boolean hasBall;
	protected double width; 		// largeur du joueur
	protected double height; 		// hauteur du joueur
	protected boolean isAlive;		// défini l'état vivant = true ou mort = false du joueur
	protected double x;       		// position horizontale du joueur
	protected final double y; 	  	// position verticale du joueur
	protected double angle; 	// rotation du joueur, devrait toujours être en 0 et 180
	protected double step;    		// pas d'un joueur lors d'un déplacement
	protected String playerColor;	// ...
	// Liste de projectiles lancé par le joueur
	protected double projectileSpeed = 5;
	// vitesse du projectile
	protected Image directionArrow; // image de la flêche de visée du joueur
	protected Sprite sprite; 		// Sprite du joueur (différents états)
	protected ImageView PlayerDirectionArrow;
	// objet amélioré de la flêche de visée
	protected GraphicsContext graphicsContext;
	// ..

	protected Image directionProjectile;
	//

	protected int side;

	Player(GraphicsContext gc, String color, int xInit, int yInit, String side, double moveSpeed)
	{
		// Tous les joueurs commencent au centre du canvas,
		this.graphicsContext = gc;
		this.playerColor=color;

		this.angle = 0;

		// On charge la representation du joueur
		if(Objects.equals(side, "top")){
			this.side = Const.SIDE_TOP;
			directionArrow = new Image("assets/PlayerArrowDown.png");
		}
		else{
			this.side = Const.SIDE_BOT;
			directionArrow = new Image("assets/PlayerArrowUp.png");
		}

		PlayerDirectionArrow = new ImageView();
		PlayerDirectionArrow.setImage(directionArrow);
		PlayerDirectionArrow.setFitWidth(10);
		PlayerDirectionArrow.setPreserveRatio(true);
		PlayerDirectionArrow.setSmooth(true);
		PlayerDirectionArrow.setCache(true);

		Image tilesheetImage = new Image("assets/orc.png");
		sprite = new Sprite(tilesheetImage, 0,0, Duration.seconds(.2), side);
		this.width = sprite.getCellSize();
		this.height = sprite.getCellSize();

		this.x = xInit - width/2;
		this.y = yInit - height/2;

		sprite.setX(x);
		sprite.setY(y);

		step = moveSpeed;
		isAlive = true;
	}

	/**
	 *  Affichage du joueur (seulement flêche)
	 */
	public void display()
	{
		if(ball == null) return;
		graphicsContext.save(); // saves the current state on stack, including the current transform
		if(this.side == Const.SIDE_BOT) {
			rotate(graphicsContext, angle, x + directionArrow.getWidth() / 2, this.getCenterY());
			graphicsContext.drawImage(directionArrow, x, y - this.width/2);
		}
		else {
			rotate(graphicsContext, angle, x + directionArrow.getWidth() / 2, this.getCenterY() + this.width/2);
			graphicsContext.drawImage(directionArrow, x, y);
		}
		graphicsContext.restore(); // back to original state (before rotation)
	}

	private void rotate(GraphicsContext gc, double angle, double px, double py) {
		Rotate r = new Rotate(angle, px, py);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	/**
	 *  Deplacement du joueur vers la gauche, on cantonne le joueur sur le plateau de jeu
	 */
	public void moveLeft()
	{
		if (this.getCenterX() - this.width/2 >= Const.OFFSET_FIELD)
			x -= step;
		else
			x = Const.OFFSET_FIELD;
		spriteAnimate();
	}

	/**
	 *  Deplacement du joueur vers la droite
	 */
	public void moveRight()
	{
		if (this.getCenterX() + this.width/2 <= Const.FIELD_DIM.width - Const.OFFSET_FIELD)
			x += step;
		else
			x = Const.FIELD_DIM.width - Const.OFFSET_FIELD - width;
		spriteAnimate();
	}


	/**
	 *  Rotation du joueur vers la gauche
	 */
	public void turnLeft()
	{
		if(angle < 0) angle = 360 + angle;
		angle = angle % 360;
		if(this.side == Const.SIDE_BOT) {
			if(angle > 270 || angle < 90) angle += 1;
			else if(angle == 270) angle = 271;
			else angle = 89;
		} else {
			if(angle < 270 || angle > 90) angle += 1;
			else if(angle == 270) angle = 269;
			else angle = 91;
		}

	}


	/**
	 *  Rotation du joueur vers la droite
	 */
	public void turnRight()
	{
		if(angle < 0) angle = 360 + angle;
		angle = angle % 360;
		if(this.side == Const.SIDE_BOT) {
			if(angle > 270 || angle < 90) angle -= 1;
			else if(angle == 270) angle = 271;
			else angle = 89;
		} else {
			if(angle < 270 || angle > 90) angle -= 1;
			else if(angle == 270) angle = 269;
			else angle = 91;
		}
	}
	public void shoot() {
		sprite.playShoot();
	}

	public void kill() {
		if(isAlive) {
			isAlive = false;
			sprite.playDie();
		}
	}
	/**
	 *  Deplacement en mode boost
	 */
	public void boost()
	{
		x += step*2;
		spriteAnimate();
	}

	private void spriteAnimate(){
		//System.out.println("Animating sprite");
		if(!sprite.isRunning) {sprite.playContinuously();}
		sprite.setX(x);
		sprite.setY(y);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getCenterX() {
		return x + width / 2;
	}

	public double getCenterY() {
		return y + height / 2;
	}

	public void Animate(ArrayList<String> input, int indice) { }

	public void setHasBall(Projectile ball) {
		this.ball = ball;
	}

	public Projectile getHasBall() { return ball; }

	public boolean hasBall() {
		return hasBall;
	}
}
