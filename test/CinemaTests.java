import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.PresentationError;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.util.Map;
import java.util.UUID;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.*;

public class CinemaTests extends SpringTest {

    private static final String ALREADY_PURCHASED_ERROR_MESSAGE = "The ticket has been already purchased!";
    private static final String OUT_OF_BOUNDS_ERROR_MESSAGE = "The number of a row or a column is out of bounds!";
    private static final String WRONG_TOKEN_ERROR_MESSAGE = "Wrong token!";

    private static final Gson gson = new Gson();

    private static void checkStatusCode(HttpResponse resp, int status) {
        if (resp.getStatusCode() != status) {
            throw new WrongAnswer(
                    resp.getRequest().getMethod() + " " +
                            resp.getRequest().getLocalUri() +
                            " should respond with status code " + status + ", " +
                            "responded: " + resp.getStatusCode() + "\n\n" +
                            "Response body:\n\n" + resp.getContent()
            );
        }
    }

    CheckResult testEndpoint() {
        HttpResponse response = get("/seats").send();
        checkStatusCode(response, 200);
        return CheckResult.correct();
    }

    CheckResult testEndpointAvailableSeats() {
        HttpResponse response = get("/seats").send();
        expect(response.getContent()).asJson().check(
                isObject()
                        .value("seats",
                                isArray(
                                        81,
                                        isObject()
                                                .value("row", isInteger(i -> i >= 1 && i <= 9))
                                                .value("column", isInteger(i -> i >= 1 && i <= 9))
                                                .value("price", isInteger(price -> price == 10 || price == 8))
                                )
                        )
                        .value("columns", 9)
                        .value("rows", 9)
        );
        return CheckResult.correct();
    }

    CheckResult testPurchaseTicket() {
        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", "1",
                        "column", "1"
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("token", isString())
                                .value("ticket",
                                        isObject()
                                                .value("row", 1)
                                                .value("column", 1)
                                                .value("price", 10)
                                )
                );
        return CheckResult.correct();
    }

    CheckResult testErrorMessageThatTicketHasBeenPurchased() {
        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", "1",
                        "column", "1"
                ))
        ).send();

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("error", ALREADY_PURCHASED_ERROR_MESSAGE)
                                .anyOtherValues()
                );
        return CheckResult.correct();
    }

    CheckResult testErrorMessageThatNumbersOutOfBounds() {
        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", "10",
                        "column", "1"
                ))
        ).send();

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("error", OUT_OF_BOUNDS_ERROR_MESSAGE)
                                .anyOtherValues()
                );

        response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", "1",
                        "column", "10"
                ))
        ).send();

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("error", OUT_OF_BOUNDS_ERROR_MESSAGE)
                                .anyOtherValues()
                );

        response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", "-1",
                        "column", "-1"
                ))
        ).send();

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("error", OUT_OF_BOUNDS_ERROR_MESSAGE)
                                .anyOtherValues()
                );


        return CheckResult.correct();
    }

    CheckResult testReturnTicket() {

        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", 2,
                        "column", 5
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson()
                .check(
                        isObject()
                                .value("token", isString())
                                .value("ticket",
                                        isObject()
                                                .value("row", 2)
                                                .value("column", 5)
                                                .value("price", 10)
                                )
                );

        JsonObject jsonResponse = gson.fromJson(response.getContent(), JsonObject.class);

        String tokenFromResponse = jsonResponse.get("token").getAsString();
        String wrongToken = UUID.randomUUID().toString();

        try {
            response = post(
                    "/return",
                    gson.toJson(Map.of(
                            "token", wrongToken
                    ))
            ).send();
        } catch (PresentationError e) {
            return CheckResult.wrong("An error occurred while trying to send POST /return with wrong token. " +
                    "In such scenario your program should respond with a 400 status code.");
        }

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("error", WRONG_TOKEN_ERROR_MESSAGE)
                        .anyOtherValues()
        );

        response = post(
                "/return",
                gson.toJson(Map.of(
                        "token", tokenFromResponse
                ))
        ).send();

        checkStatusCode(response, 200);
        expect(response.getContent()).asJson().check(
                isObject()
                        .value("ticket",
                                isObject()
                                        .value("row", 2)
                                        .value("column", 5)
                                        .value("price", 10)
                        )
        );

        return CheckResult.correct();
    }

    CheckResult testTokenInvalidation() {

        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", 3,
                        "column", 6
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("token", isString())
                        .value("ticket",
                                isObject()
                                        .value("row", 3)
                                        .value("column", 6)
                                        .value("price", 10)
                        )
        );

        JsonObject jsonResponse = gson.fromJson(response.getContent(), JsonObject.class);
        String tokenFromResponse = jsonResponse.get("token").getAsString();

        response = post(
                "/return",
                gson.toJson(Map.of(
                        "token", tokenFromResponse
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("ticket",
                                isObject()
                                        .value("row", 3)
                                        .value("column", 6)
                                        .value("price", 10)
                        )
        );

        response = post(
                "/return",
                gson.toJson(Map.of(
                        "token", tokenFromResponse
                ))
        ).send();

        checkStatusCode(response, 400);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("error", WRONG_TOKEN_ERROR_MESSAGE)
                        .anyOtherValues()
        );

        return CheckResult.correct();
    }

    CheckResult testReturnedTicketAvailability() {

        HttpResponse response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", 3,
                        "column", 6
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("token", isString())
                        .value("ticket",
                                isObject()
                                        .value("row", 3)
                                        .value("column", 6)
                                        .value("price", 10)
                        )
        );

        JsonObject jsonResponse = gson.fromJson(response.getContent(), JsonObject.class);
        String tokenFromResponse = jsonResponse.get("token").getAsString();

        response = post(
                "/return",
                gson.toJson(Map.of(
                        "token", tokenFromResponse
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("ticket",
                                isObject()
                                        .value("row", 3)
                                        .value("column", 6)
                                        .value("price", 10)
                        )
        );

        response = post(
                "/purchase",
                gson.toJson(Map.of(
                        "row", 3,
                        "column", 6
                ))
        ).send();

        checkStatusCode(response, 200);

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("token", isString())
                        .value("ticket",
                                isObject()
                                        .value("row", 3)
                                        .value("column", 6)
                                        .value("price", 10)
                        )
        );

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{
            this::testEndpoint,
            this::testEndpointAvailableSeats,
            this::testPurchaseTicket,
            this::testErrorMessageThatTicketHasBeenPurchased,
            this::testErrorMessageThatNumbersOutOfBounds,
            this::testReturnTicket,
            this::testTokenInvalidation,
            this::testReturnedTicketAvailability
    };
}
