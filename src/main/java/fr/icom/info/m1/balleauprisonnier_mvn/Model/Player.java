package fr.icom.info.m1.balleauprisonnier_mvn.Model;

import fr.icom.info.m1.balleauprisonnier_mvn.Const;
import fr.icom.info.m1.balleauprisonnier_mvn.Controller.ProjectileController;
import fr.icom.info.m1.balleauprisonnier_mvn.Sprite;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Classe gerant un joueur
 */
public abstract class Player extends GameObject
{
	protected ProjectileController pc;
	protected boolean hasBall;
	protected boolean isAlive;		// défini l'état vivant = true ou mort = false du joueur
	protected String playerColor;	// ...
	protected double projectileSpeed = 5;
	protected Sprite sprite; 		// Sprite du joueur (différents états)
	protected int side;

	Player(String color, int xInit, int yInit, int side, double moveSpeed, ProjectileController ball, Image image)
	{
		super( 0, moveSpeed, 2);
		this.playerColor = color;
		this.side = side;
		this.isAlive = true;
		this.pc = ball;

		sprite = new Sprite(image, Duration.seconds(.2), side);
		this.width = sprite.getCellSize();
		this.height = sprite.getCellSize();

		this.x = xInit - this.width/2;
		this.y = yInit - this.height/2;
		sprite.setX(x);
		sprite.setY(y);

		hasBall = false;

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

	public abstract void update(ArrayList<String> input, int indice);

	public void Animate(ArrayList<String> input, int indice) { }

	public void setHasBall(boolean status) {
		this.hasBall = status;
	}

	public boolean hasBall() { return hasBall; }

	public int getSide() {
		return side;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public Sprite getSprite() {
		return sprite;
	}
}
