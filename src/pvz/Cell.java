package pvz;

public class Cell {
    private final int row;
    private final int col;
    private Plant plant;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Plant getPlant() {
        return plant;
    }

    public boolean isEmpty() {
        return plant == null;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public void clearPlant() {
        plant = null;
    }
}
