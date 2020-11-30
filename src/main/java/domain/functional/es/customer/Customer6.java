package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;

import java.util.List;

public class Customer6 {
    public static CustomerRegistered register(RegisterCustomer command) {
        return null; // TODO
    }

    public static List<Event> confirmEmailAddress(List<Event> eventStream, ConfirmCustomerEmailAddress command) {
        var current = CustomerState.reconstitute(eventStream);

        return List.of(); // TODO
    }

    public static List<Event> changeEmailAddress(List<Event> eventStream, ChangeCustomerEmailAddress command) {
        var current = CustomerState.reconstitute(eventStream);

        return List.of(); // TODO
    }
}
