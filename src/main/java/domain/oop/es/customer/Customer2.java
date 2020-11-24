package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.List;

public final class Customer2 {
    private EmailAddress emailAddress;
    private Hash confirmationHash;
    private boolean isEmailAddressConfirmed;
    private PersonName name;

    private Customer2() {
    }

    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash,
                command.name
        );
    }

    public static Customer2 reconstitute(List<Event> events) {
        Customer2 customer = new Customer2();

        customer.apply(events);

        return customer;
    }

    public List<Event> confirmEmailAddress(ConfirmCustomerEmailAddress command) {
        if (!confirmationHash.equals(command.confirmationHash)) {
            return List.of(
                    CustomerEmailAddressConfirmationFailed.build(command.customerID)
            );
        }

        if (isEmailAddressConfirmed) {
            return List.of();
        }

        return List.of(
                CustomerEmailAddressConfirmed.build(command.customerID)
        );
    }

    public List<Event> changeEmailAddress(ChangeCustomerEmailAddress command) {
        if (command.emailAddress.equals(emailAddress)) {
            return List.of();
        }

        return List.of(
                CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash)
        );
    }

    public List<Event> changeName(ChangeCustomerName command) {
        if (command.name.equals(name)) {
            return List.of();
        }

        return List.of(
                CustomerNameChanged.build(command.customerID, command.name)
        );
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
