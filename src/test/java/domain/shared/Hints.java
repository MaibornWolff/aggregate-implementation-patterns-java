package domain.shared;

public class Hints {
    public static final String SHOULD_BE_NO_EVENT = "HINT: No event should be recorded, but it was.\n\n";
    public static final String WRONG_NUMBER_OF_EVENTS = "HINT: Wrong number of events recorded - it should be exactly 1.\n\n";
    public static final String NULL_EVENT = "HINT: A null event was recorded?\n\n";
    public static final String WRONG_EVENT = "HINT: The wrong event type was recorded.\n\n";

    public static final String WRONG_CUSTOMER_ID = "HINT: The recorded event has a wrong customerID\n\n";
    public static final String WRONG_CONFIRMATION_HASH = "HINT: The recorded event has a wrong confirmationHash\n\n";
    public static final String WRONG_EMAIL_ADDRESS = "HINT: The recorded event has a wrong emailAddress\n\n";
    public static final String WRONG_NAME = "HINT: The recorded event has a wrong name\n\n";
}
