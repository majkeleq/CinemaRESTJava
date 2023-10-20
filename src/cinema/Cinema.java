package cinema;

import java.util.Arrays;
import java.util.function.Predicate;

public class Cinema {
    private int rows;
    private int columns;
    private final Seat[] seats;

    public Cinema(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        seats = new Seat[rows * columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                seats[i * rows + j] = new Seat(i + 1, j + 1);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Seat getSeat(int row, int col) {
        return seats[(row - 1) * row + col - 1];
    }
    public Seat[] getSeats() {
        return Arrays.stream(seats).filter(Predicate.not(Seat::isTaken)).toArray(Seat[]::new);
    }

}
