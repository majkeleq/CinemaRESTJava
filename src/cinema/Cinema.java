package cinema;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;

@Component
public class Cinema {
    @Value("9")
    private int rows;

    @Value("9")
    private int columns;
    private Seat[] seats;

    @PostConstruct
    void init() {
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
        return seats[(row - 1) * columns + col - 1];
    }

    public Seat[] getSeats() {
        return Arrays.stream(seats).filter(Predicate.not(Seat::isTaken)).toArray(Seat[]::new);
    }

}
