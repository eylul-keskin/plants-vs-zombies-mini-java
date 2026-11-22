package pvz;

public class Bullet {
    private final int row;
    private final int damage;
    private final boolean snowBullet;
    private double x;
    private double speed;

    public Bullet(int row, double x, int damage, double speed, boolean snowBullet) {
        this.row = row;
        this.x = x;
        this.damage = damage;
        this.speed = speed;
        this.snowBullet = snowBullet;
    }

    public int getRow() {
        return row;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isSnowBullet() {
        return snowBullet;
    }

    public void move() {
        x += speed;
    }
}
