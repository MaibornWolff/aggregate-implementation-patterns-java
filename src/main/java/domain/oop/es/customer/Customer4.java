package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.ArrayList;
import java.util.List;

public final class Customer4 {
    private EmailAddress emailAddress;
    private Hash confirmationHash;
    private boolean isEmailAddressConfirmed;
    private PersonName name;

    private final List<Event> recordedEvents;

    private Customer4() {
        recordedEvents = new ArrayList<>();
    }

    public static Customer4 register(RegisterCustomer command) {
        Customer4 customer = new Customer4();

        customer.recordThat(
                CustomerRegistered.build(command.customerID, command.emailAddress, command.confirmationHash, command.name)
        );

        return customer;
    }

    public static Customer4 reconstitute(List<Event> events) {
        var customer = new Customer4();

        customer.apply(events);

        return customer;
    }

    public void confirmEmailAddress(ConfirmCustomerEmailAddress command) {
        if (!confirmationHash.equals(command.confirmationHash)) {
            recordThat(
                    CustomerEmailAddressConfirmationFailed.build(command.customerID)
            );

            return;
        }

        if (!isEmailAddressConfirmed) {
            recordThat(
                    CustomerEmailAddressConfirmed.build(command.customerID)
            );
        }
    }

    public void changeEmailAddress(ChangeCustomerEmailAddress command) {
        if (!emailAddress.equals(command.emailAddress)) {
            recordThat(
                    CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash)
            );
        }
    }

    public List<Event> getRecordedEvents() {
        return recordedEvents;
    }

    private void recordThat(Event event) {
        recordedEvents.add(event);
    }

    void apply(List<Event> events) {
        for (Event event : events) {
            apply(event);
        }
    }

    void apply(Event event) {
        if (event.getClass() == CustomerRegistered.class) {
            emailAddress = ((CustomerRegistered) event).emailAddress;
            confirmationHash = ((CustomerRegistered) event).confirmationHash;
            name = ((CustomerRegistered) event).name;
        } else if (event.getClass() == CustomerEmailAddressConfirmed.class) {
            isEmailAddressConfirmed = true;
        } else if (event.getClass() == CustomerEmailAddressChanged.class) {
            emailAddress = ((CustomerEmailAddressChanged) event).emailAddress;
            confirmationHash = ((CustomerEmailAddressChanged) event).confirmationHash;
            isEmailAddressConfirmed = false;
        }
    }
}
