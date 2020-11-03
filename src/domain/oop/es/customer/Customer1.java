package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.ArrayList;
import java.util.List;

/**
 * This version of a Customer Aggregate is OOP-style, event-sourced, and records events that have happened, the client has to request those recorded events.
 * <p>
 * Enable the disabled test cases (remove the @Disabled annotation) in Customer2Test one by one and make them all green!
 * The first test case (RegisterCustomer) is already enabled for you to start.
 * <p>
 * Bonus challenge:
 * What needs to be changed so that the Aggregate keeps it's own state up-to-date, e.g. to be able to handle multiple
 * Commands within one request from the outside?
 * Hint: To test this, you can extend the test cases so that they handle the same command again, resulting in "no changes".
 */
public final class Customer1 {
    private EmailAddress emailAddress;
    private Hash confirmationHash;
    private boolean isEmailAddressConfirmed;
    private PersonName name;

    private final List<Event> recordedEvents;

    private Customer1() {
        recordedEvents = new ArrayList<>();
    }

    public static Customer1 register(RegisterCustomer command) {
        Customer1 customer = new Customer1();

        customer.recordThat(
                CustomerRegistered.build(command.customerID, command.emailAddress, command.confirmationHash, command.name)
        );

        return customer;
    }

    public static Customer1 reconstitute(List<Event> events) {
        var customer = new Customer1();

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
        if (!command.emailAddress.equals(emailAddress)) {
            recordThat(
                    CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash)
            );
        }
    }

    public void changeName(ChangeCustomerName command) {
        if (!command.name.equals(name)) {
            recordThat(
                    CustomerNameChanged.build(command.customerID, command.name)
            );
        }
    }

    public List<Event> getRecordedEvents() {
        var current = new ArrayList<>(recordedEvents);

        recordedEvents.clear();

        return current;
    }

    private void recordThat(Event event) {
        recordedEvents.add(event);
        apply(event);
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

