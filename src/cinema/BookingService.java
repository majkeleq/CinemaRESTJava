package cinema;

import cinema.exceptions.PlaceIsTakenException;
import cinema.exceptions.RowColumnOutOfBoundException;
import cinema.exceptions.WrongPasswordException;
import cinema.exceptions.WrongTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class BookingService {
    private Cinema cinema;
    Map<UUID, Seat> takenSeats = new HashMap<>();

    @Autowired
    public BookingService(Cinema cinema) {
        this.cinema = cinema;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public ResponseEntity<Map<String, Object>> purchaseSeat(Seat seat) throws RuntimeException {
        int row = seat.getRow();
        int column = seat.getColumn();
        if (row > cinema.getRows() || row <= 0 || column > cinema.getColumns() || column <= 0) {
            throw new RowColumnOutOfBoundException("The number of a row or a column is out of bounds!");
        }
        Seat targetSeat = cinema.getSeat(row, column);
        if (targetSeat.isTaken()) {
            throw new PlaceIsTakenException("The ticket has been already purchased!");

        } else {
            targetSeat.setTaken(true);
            UUID uuid = UUID.randomUUID();
            takenSeats.put(uuid, targetSeat);
            Map<String, Object> returnValue = new LinkedHashMap<>();
            returnValue.put("token", uuid);
            returnValue.put("ticket", targetSeat);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(returnValue);
        }
    }
    public ResponseEntity<Map<String,Seat>> returnSeat(String token) throws RuntimeException {
        Seat targetSeat = takenSeats.getOrDefault(UUID.fromString(token), null);
        if (targetSeat == null) {
            throw new WrongTokenException("Wrong token!");
        } else {
            takenSeats.remove(UUID.fromString(token));
            targetSeat.setTaken(false);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(Map.of("ticket", targetSeat));
        }
    }
    public ResponseEntity<Map<String,Integer>> showStats(String password) throws RuntimeException{
        if (!password.equals("super_secret")) {
            throw new WrongPasswordException("The password is wrong!");
        } else {
            Map<String, Integer> response = new LinkedHashMap<>();
            response.put("income", takenSeats.values().stream().map(Seat::getPrice).reduce(0, Integer::sum));
            response.put("available", cinema.getSeats().length);
            response.put("purchased", takenSeats.size());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(response);
        }
    }
}
