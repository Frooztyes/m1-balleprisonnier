package fr.icom.info.m1.balleauprisonnier_mvn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Classe gerant un joueur
 */
public abstract class Player extends GameObject
{
	protected Projectile ball;
	protected boolean hasBall;
	protected boolean isAlive;		// défini l'état vivant = true ou mort = false du joueur
	protected String playerColor;	// ...
	protected double projectileSpeed = 5;
	protected Sprite sprite; 		// Sprite du joueur (différents états)
	protected ImageView PlayerDirectionArrow;
	// objet amélioré de la flêche de visée
	// ..

	protected int side;

	Player(GraphicsContext gc, String color, int xInit, int yInit, int side, double moveSpeed)
	{
		super(gc, 0, moveSpeed, 2, new Image("assets/PlayerArrow.png"));
		this.playerColor = color;
		this.side = side;
		this.isAlive = true;


		Image tilesheetImage = new Image("assets/orc.png");
		this.sprite = new Sprite(tilesheetImage, 0,0, Duration.seconds(.2), side);

		this.width = sprite.getCellSize();
		this.height = sprite.getCellSize();

		this.x = xInit - this.width/2;
		this.y = yInit - this.height/2;

		sprite.setX(x);
		sprite.setY(y);

	}


	/**
	 *  Affichage du joueur (seulement flêche)
	 */
	public void display()
	{
		if(ball == null) return;
		graphicsContext.save(); // saves the current state on stack, including the current transform
		if(this.side == Const.SIDE_BOT) {
			rotate(graphicsContext, angle, x + image.getWidth() / 2, this.getY() + this.width/2);
		}
		else {
			rotate(graphicsContext, (angle + 180) % 360, x + image.getWidth() / 2, this.getY() + this.width/2);
		}
		graphicsContext.drawImage(image, x, y - this.width/2);
		graphicsContext.restore(); // back to original state (before rotation)
	}

	/**
	 *  Deplacement du joueur vers la gauche, on cantonne le joueur sur le plateau de jeu
	 */
	public void moveLeft()
	{
		if (this.getX() > Const.OFFSET_FIELD - width/2)
			x -= moveSpeed;
		else
			x = Const.OFFSET_FIELD - width/2;
		spriteAnimate();
	}

	/**
	 *  Deplacement du joueur vers la droite
	 */
	public void moveRight()
	{
		if (this.getX() < Const.FIELD_DIM.width - Const.OFFSET_FIELD - width/2 )
			x += moveSpeed;
		else
			x = Const.FIELD_DIM.width - Const.OFFSET_FIELD - width/2;
		spriteAnimate();
	}


	/**
	 *  Rotation du joueur vers la gauche
	 */
	public void turnLeft()
	{
		if(angle < 0) angle = 360 + angle;
		angle = angle % 360;
		angle += rotationSpeed;
		if(angle < 270 && angle >= 180) angle = 270;
		else if(angle >= 90 && angle <= 180) angle = 90;
	}


	/**
	 *  Rotation du joueur vers la droite
	 */
	public void turnRight()
	{
		if(angle < 0) angle = 360 + angle;
		angle = angle % 360;
		angle -= rotationSpeed;
		if(angle <= 270 && angle >= 180) angle = 270;
		else if(angle > 90 && angle <= 180) angle = 90;
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
		x += moveSpeed *2;
		spriteAnimate();
	}

	private void spriteAnimate(){
		//System.out.println("Animating sprite");
		if(!sprite.isRunning) {sprite.playContinuously();}
		sprite.setX(x);
		sprite.setY(y);
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
