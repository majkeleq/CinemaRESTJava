package cinema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    //private final Cinema cinema = new Cinema(9, 9);

    private final BookingService bookingService;
    @Autowired
    public Controller( BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/seats")
    public Cinema getCinema() {
        return bookingService.getCinema();
    }

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseSeat(@RequestBody Seat seat) {
        return bookingService.purchaseSeat(seat);
    }

    @PostMapping("/return")
    public ResponseEntity<Map<String, Seat>> returnSeat(@RequestBody Map<String, String> token) {
        return bookingService.returnSeat(token.get("token"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> stats(@RequestParam Map<String, String> params) {
        return bookingService.showStats(params.getOrDefault("password", ""));
    }
}
