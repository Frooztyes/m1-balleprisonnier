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
	protected double width; 		// largeur du joueur
	protected double height; 		// hauteur du joueur
	protected boolean isAlive;		// défini l'état vivant = true ou mort = false du joueur
	protected double x;       		// position horizontale du joueur
	protected final double y; 	  	// position verticale du joueur
	protected double angle; 	// rotation du joueur, devrait toujour être en 0 et 180
	protected double step;    		// pas d'un joueur lors d'un déplacement
	protected String playerColor;	// ...
	protected List<Projectile> listProjectiles = new ArrayList<>();
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

	private boolean shooted = false;

	Player(GraphicsContext gc, String color, int xInit, int yInit, String side, double moveSpeed)
	{
		// Tous les joueurs commencent au centre du canvas,
		this.x = xInit;
		this.y = yInit;
		this.graphicsContext = gc;
		this.playerColor=color;

		this.angle = 0;

		// On charge la representation du joueur
		if(Objects.equals(side, "top")){
			directionArrow = new Image("assets/PlayerArrowDown.png");
			directionProjectile = new Image("assets/ProjectileDown.png");
		}
		else{
			directionArrow = new Image("assets/PlayerArrowUp.png");
			directionProjectile = new Image("assets/ProjectileUp.png");
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
		graphicsContext.save(); // saves the current state on stack, including the current transform
		rotate(graphicsContext, angle, x + directionArrow.getWidth() / 2, y + directionArrow.getHeight() / 2);
		graphicsContext.drawImage(directionArrow, x, y);
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
		if ((this.x + this.width) >= Const.OFFSET_FIELD)
		{
			spriteAnimate();
			x -= step;
		} else {
			x = Const.OFFSET_FIELD - this.width;
		}
	}

	/**
	 *  Deplacement du joueur vers la droite
	 */
	public void moveRight()
	{
		if (x <= Const.FIELD_DIM.width - Const.OFFSET_FIELD)
		{
			spriteAnimate();
			x += step;
		} else {
			x = Const.FIELD_DIM.width - Const.OFFSET_FIELD;
		}
	}


	/**
	 *  Rotation du joueur vers la gauche
	 */
	public void turnLeft()
	{
		if (angle > 0 && angle < 180)
		{
			angle += 1;
		}
		else {
			angle += 1;
		}

	}


	/**
	 *  Rotation du joueur vers la droite
	 */
	public void turnRight()
	{
		if (angle > 0 && angle < 180)
		{
			angle -=1;
		}
		else {
			angle -= 1;
		}
	}
	public void shoot(){
		if(shooted) return;
//		shooted = !shooted;
		sprite.playShoot();
		Projectile p = new Projectile(graphicsContext, angle, projectileSpeed, getCenterX(), getCenterY(), directionProjectile);
		p.display();
		listProjectiles.add(p);
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

	public List<Projectile> getListProjectiles() {
		return listProjectiles;
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
}
