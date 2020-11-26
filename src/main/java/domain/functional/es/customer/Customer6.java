package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;

import java.util.List;

public class Customer6 {
    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash,
                command.name
        );
    }

    public static List<Event> confirmEmailAddress(List<Event> eventStream, ConfirmCustomerEmailAddress command) {
        var current = CustomerState.reconstitute(eventStream);

        if (!current.confirmationHash.equals(command.confirmationHash)) {
            return List.of(CustomerEmailAddressConfirmationFailed.build(command.customerID));
        }

        if (current.isEmailAddressConfirmed) {
            return List.of();
        }

        return List.of(CustomerEmailAddressConfirmed.build(command.customerID));
    }

    public static List<Event> changeEmailAddress(List<Event> eventStream, ChangeCustomerEmailAddress command) {
        var current = CustomerState.reconstitute(eventStream);

        if (current.emailAddress.equals(command.emailAddress)) {
            return List.of();
        }

        return List.of(CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash));
    }
}
