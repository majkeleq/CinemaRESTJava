package cinema;

import cinema.exceptions.CustomErrorMessage;
import cinema.exceptions.PlaceIsTakenException;
import cinema.exceptions.RowColumnOutOfBoundException;
import cinema.exceptions.WrongTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RowColumnOutOfBoundException.class)
    public ResponseEntity<CustomErrorMessage> handleRowColumnOutOfBound(
            RowColumnOutOfBoundException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlaceIsTakenException.class)
    public ResponseEntity<CustomErrorMessage> handlePlaceIsTaken(
            PlaceIsTakenException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongTokenException.class)
    public ResponseEntity<CustomErrorMessage> handleWrongToken(
            WrongTokenException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
