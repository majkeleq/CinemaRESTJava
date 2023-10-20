package cinema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"taken"})
public class Seat {
    private int row;
    private int column;
    private int price;
    private boolean isTaken = false;
    public Seat() {};

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
        this.price = row > 4 ? 8 : 10;
    }

    public int getPrice() {
        return price;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

}
