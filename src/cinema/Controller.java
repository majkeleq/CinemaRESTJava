package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    private final Cinema cinema = new Cinema(9, 9);

    @GetMapping("/seats")
    public Cinema getCinema() {
        return cinema;
    }

    @PostMapping("/purchase")
    public ResponseEntity purchaseSeat(@RequestBody Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();
        if (row > cinema.getRows() || row <= 0 || column > cinema.getColumns() || column <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(Map.of("error","The number of a row or a column is out of bounds!"));
        }
        Seat targetSeat = cinema.getSeat(row, column);
        if (targetSeat.isTaken()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(Map.of("error","The ticket has been already purchased!"));

        } else {
            targetSeat.purchase();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(targetSeat);
        }
    }
}
