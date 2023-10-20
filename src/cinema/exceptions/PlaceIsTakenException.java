package cinema.exceptions;

public class PlaceIsTakenException extends RuntimeException{
    public PlaceIsTakenException(String message) {
        super(message);
    }
}
