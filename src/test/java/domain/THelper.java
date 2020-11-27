package domain;

import domain.shared.event.Event;

import java.util.List;

public class THelper {
    public static String typeOfFirst(List<Event> recordedEvents) {
        if (recordedEvents.size() == 0) {
            return "???";
        }

        return recordedEvents.get(0).getClass().getSimpleName();
    }

    public static String propertyIsNull(String property) {
        return String.format(
                "PROBLEM: The %s is null!\n" +
                        "HINT: Maybe you didn't apply the previous events properly!?\n",
                property
        );
    }

    public static String eventIsNull(String method) {
        return String.format(
                "PROBLEM in %s(): The recorded event is NULL!\n" +
                        "HINT: There must be some weird code ;-)\n\n",
                method
        );
    }

    public static String propertyIsWrong(String method, String property) {
        return String.format(
                "PROBLEM in %s(): The event contains a wrong %s!\n" +
                        "HINT: The %s in the event should be taken from the command!\n\n",
                method, property, property
        );
    }

    public static String noEventWasRecorded(String method, String expectedEvent) {
        return String.format(
                "PROBLEM in %s(): No event was recorded!\n" +
                        "HINTS: Build a %s event and use recordThat() to record it!\n" +
                        "       Did you apply all previous events properly?\n" +
                        "       Check your business logic :-)!\n\n",
                method, expectedEvent
        );
    }

    public static String eventOfWrongTypeWasRecorded(String method) {
        return String.format(
                "PROBLEM in %s(): An event of the wrong type was recorded!\n" +
                        "HINTS: Did you apply all previous events properly?\n" +
                        "       Check your business logic :-)!\n\n",
                method
        );
    }

    public static String noEventShouldHaveBeenRecorded(String recordedEventType) {
        return String.format(
                "PROBLEM: No event should have been recorded!\n" +
                        "HINTS: Check your business logic - this command should be ignored (idempotency)!\n" +
                        "       Did you apply all previous events properly?\n" +
                        "       The recorded event is of type %s.\n\n",
                recordedEventType
        );
    }
}
