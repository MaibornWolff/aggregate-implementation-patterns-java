package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;

import java.util.List;

public class Customer5 {
    public static CustomerRegistered register(RegisterCustomer command) {
        return null; // TODO
    }

    public static List<Event> confirmEmailAddress(List<Event> eventStream, ConfirmCustomerEmailAddress command) {
        boolean isEmailAddressConfirmed = false;
        Hash confirmationHash = null;
        for (Event event : eventStream) {
            if (event instanceof CustomerRegistered) {
                // TODO
            } else if (event instanceof CustomerEmailAddressConfirmed) {
                // TODO
            } else if (event instanceof CustomerEmailAddressChanged) {
                // TODO
            }
        }

        return List.of(); // TODO
    }

    public static List<Event> changeEmailAddress(List<Event> eventStream, ChangeCustomerEmailAddress command) {
        EmailAddress emailAddress = null;
        for (Event event : eventStream) {
            if (event instanceof CustomerRegistered) {
                // TODO
            } else if (event instanceof CustomerEmailAddressChanged) {
                // TODO
            }
        }

        return List.of(); // TODO
    }
}
