package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.List;

public final class Customer3 {
    private EmailAddress emailAddress;
    private Hash confirmationHash;
    private boolean isEmailAddressConfirmed;
    private PersonName name;

    private Customer3() {
    }

    public static CustomerRegistered register(RegisterCustomer command) {
        return null; // TODO
    }

    public static Customer3 reconstitute(List<Event> events) {
        Customer3 customer = new Customer3();

        customer.apply(events);

        return customer;
    }

    public List<Event> confirmEmailAddress(ConfirmCustomerEmailAddress command) {
        // TODO

        return List.of(); // TODO
    }

    public List<Event> changeEmailAddress(ChangeCustomerEmailAddress command) {
        // TODO

        return List.of(); // TODO
    }

    void apply(List<Event> events) {
        for (Event event : events) {
            apply(event);
        }
    }

    void apply(Event event) {
        if (event.getClass() == CustomerRegistered.class) {
            // TODO
        } else if (event.getClass() == CustomerEmailAddressConfirmed.class) {
            // TODO
        } else if (event.getClass() == CustomerEmailAddressChanged.class) {
            // TODO
        }
    }
}
