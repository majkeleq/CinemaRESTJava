package cinema.exceptions;

public class WrongTokenException extends RuntimeException{
    public WrongTokenException(String message) {
        super(message);
    }
}
