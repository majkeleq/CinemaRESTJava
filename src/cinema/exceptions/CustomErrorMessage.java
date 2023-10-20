package cinema.exceptions;

import java.time.LocalDateTime;

public class CustomErrorMessage {
    private int statusCode;
    private LocalDateTime timestamp;
    private String error;
    private String description;

    public CustomErrorMessage(
            int statusCode,
            LocalDateTime timestamp,
            String error,
            String description) {

        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.error = error;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
