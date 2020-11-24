package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

import java.util.List;

public class Customer7 {
    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash,
                command.name
        );
    }

    public static List<Event> confirmEmailAddress(List<Event> eventStream, ConfirmCustomerEmailAddress command) {
        boolean isEmailAddressConfirmed = false;
        Hash confirmationHash = null;
        for (Event event: eventStream) {
            if (event instanceof CustomerRegistered) {
                confirmationHash = ((CustomerRegistered) event).confirmationHash;
            } else if (event instanceof CustomerEmailAddressConfirmed) {
                isEmailAddressConfirmed = true;
            } else if (event instanceof CustomerEmailAddressChanged) {
                isEmailAddressConfirmed = false;
                confirmationHash = ((CustomerEmailAddressChanged) event).confirmationHash;
            }
        }

        assert confirmationHash != null;
        if (!confirmationHash.equals(command.confirmationHash)) {
            return List.of(CustomerEmailAddressConfirmationFailed.build(command.customerID));
        }

        if (isEmailAddressConfirmed) {
            return List.of();
        }

        return List.of(CustomerEmailAddressConfirmed.build(command.customerID));
    }

    public static List<Event> changeEmailAddress(List<Event> eventStream, ChangeCustomerEmailAddress command) {
        EmailAddress emailAddress = null;
        for (Event event: eventStream) {
            if (event instanceof CustomerRegistered) {
                emailAddress = ((CustomerRegistered) event).emailAddress;
            } else if (event instanceof CustomerEmailAddressChanged) {
                emailAddress = ((CustomerEmailAddressChanged) event).emailAddress;
            }
        }

        assert emailAddress != null;
        if (emailAddress.equals(command.emailAddress)) {
            return List.of();
        }

        return List.of(CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash));
    }

    public static List<Event> changeName(List<Event> eventStream, ChangeCustomerName command) {
        PersonName name = null;
        for (Event event: eventStream) {
            if (event instanceof CustomerRegistered) {
                name = ((CustomerRegistered) event).name;
            }
            else if (event instanceof CustomerNameChanged) {
                name = ((CustomerNameChanged) event).name;
            }
        }

        assert name != null;
        if (name.equals(command.name)) {
            return List.of();
        }

        return List.of(CustomerNameChanged.build(command.customerID, command.name));
    }
}
