package pvz;

public class Sun {
    private final int row;
    private final int value;
    private double x;
    private double y;

    public Sun(int row, double x, double y, int value) {
        this.row = row;
        this.x = x;
        this.y = y;
        this.value = value;
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

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + GameConstants.SUN_SIZE
                && mouseY >= y && mouseY <= y + GameConstants.SUN_SIZE;
    }
}
