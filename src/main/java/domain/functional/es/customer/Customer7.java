package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;

import java.util.List;

public class Customer7 {
    public static CustomerRegistered register(RegisterCustomer command) {
        return null; // TODO
    }

    public static List<Event> confirmEmailAddress(CustomerState current, ConfirmCustomerEmailAddress command) {
        // TODO

        return List.of(); // TODO
    }

    public static List<Event> changeEmailAddress(CustomerState current, ChangeCustomerEmailAddress command) {
        // TODO

        return List.of(); // TODO
    }
}
