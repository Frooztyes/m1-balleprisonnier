package fr.icom.info.m1.balleauprisonnier_mvn.Model;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double angle;
    protected double moveSpeed;
    protected double rotationSpeed;

    public GameObject(double angle, double moveSpeed, double rotationSpeed) {
        this.angle = angle;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getAngle() { return angle; }

    public void setAt(double x, double y) {
        this.x = x + this.width/2;
        this.y = y + this.height/2;
    }

    public abstract void update();
}
