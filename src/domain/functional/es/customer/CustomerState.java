package domain.functional.es.customer;

import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;

import java.util.List;

public class CustomerState {
    ID id;
    EmailAddress emailAddress;
    Hash confirmationHash;
    PersonName name;
    Boolean isEmailAddressConfirmed;

    private CustomerState() {}

    public static CustomerState reconstitute(List<Event> events) {
        var customer = new CustomerState();

        customer.apply(events);

        return customer;
    }

    public void forward(List<Event> events) {
        for (Event event : events) {
            apply(event);
        }
    }

    private void apply(List<Event> events) {
        for (Event event : events) {
            apply(event);
        }
    }

    private void apply(Event event) {
        if (event.getClass() == CustomerRegistered.class) {
            emailAddress = ((CustomerRegistered) event).emailAddress;
            confirmationHash = ((CustomerRegistered) event).confirmationHash;
            name = ((CustomerRegistered) event).name;
            isEmailAddressConfirmed = false;
        } else if (event.getClass() == CustomerEmailAddressConfirmed.class) {
            isEmailAddressConfirmed = true;
        } else if (event.getClass() == CustomerEmailAddressChanged.class) {
            emailAddress = ((CustomerEmailAddressChanged) event).emailAddress;
            confirmationHash = ((CustomerEmailAddressChanged) event).confirmationHash;
            isEmailAddressConfirmed = false;
        } else if (event.getClass() == CustomerNameChanged.class) {
            name = ((CustomerNameChanged) event).name;
        }
    }
}
