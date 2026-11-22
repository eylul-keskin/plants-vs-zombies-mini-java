package pvz;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Cell[][] cells;

    public Board() {
        cells = new Cell[GameConstants.ROWS][GameConstants.COLS];
        for (int row = 0; row < GameConstants.ROWS; row++) {
            for (int col = 0; col < GameConstants.COLS; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < GameConstants.ROWS && col >= 0 && col < GameConstants.COLS;
    }

    public Cell getCell(int row, int col) {
        if (!isInsideBoard(row, col)) {
            return null;
        }
        return cells[row][col];
    }

    public Plant getPlant(int row, int col) {
        Cell cell = getCell(row, col);
        return cell == null ? null : cell.getPlant();
    }

    public boolean placePlant(Plant plant) {
        Cell cell = getCell(plant.getRow(), plant.getCol());
        if (cell == null || !cell.isEmpty()) {
            return false;
        }
        cell.setPlant(plant);
        return true;
    }

    public void removePlant(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            cell.clearPlant();
        }
    }

    public void clear() {
        for (int row = 0; row < GameConstants.ROWS; row++) {
            for (int col = 0; col < GameConstants.COLS; col++) {
                cells[row][col].clearPlant();
            }
        }
    }

    public List<Plant> getPlants() {
        List<Plant> plants = new ArrayList<>();
        for (int row = 0; row < GameConstants.ROWS; row++) {
            for (int col = 0; col < GameConstants.COLS; col++) {
                Plant plant = cells[row][col].getPlant();
                if (plant != null) {
                    plants.add(plant);
                }
            }
        }
        return plants;
    }
}
