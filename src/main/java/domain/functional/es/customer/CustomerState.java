package domain.functional.es.customer;

import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.List;

public class CustomerState {
    EmailAddress emailAddress;
    Hash confirmationHash;
    PersonName name;
    boolean isEmailAddressConfirmed;

    private CustomerState() {}

    public static CustomerState reconstitute(List<Event> events) {
        var customer = new CustomerState();

        customer.apply(events);

        return customer;
    }

    void apply(List<Event> events) {
        for (Event event : events) {
            if (event.getClass() == CustomerRegistered.class) {
                // TODO
                continue;
            }

            if (event.getClass() == CustomerEmailAddressConfirmed.class) {
                // TODO
                continue;
            }

            if (event.getClass() == CustomerEmailAddressChanged.class) {
                // TODO
            }
        }
    }
}
