package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;

import java.util.List;

public class Customer5 {

    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                        command.customerID,
                        command.emailAddress,
                        command.confirmationHash,
                        command.name
                );
    }

    public static List<Event> confirmEmailAddress(CustomerState current, ConfirmCustomerEmailAddress command) {
        if (!current.confirmationHash.equals(command.confirmationHash)) {
            return List.of(CustomerEmailAddressConfirmationFailed.build(command.customerID));
        }

        if (current.isConfirmed) {
            return List.of();
        }

        return List.of(CustomerEmailAddressConfirmed.build(command.customerID));
    }

    public static List<Event> changeEmailAddress(CustomerState current, ChangeCustomerEmailAddress command) {
        if (current.emailAddress.equals(command.emailAddress)) {
            return List.of();
        }

        return List.of(CustomerEmailAddressChanged.build(command.customerID, command.emailAddress, command.confirmationHash));
    }

    public static List<Event> changeName(CustomerState current, ChangeCustomerName command) {
        if (current.name.equals(command.name)) {
            return List.of();
        }

        return List.of(CustomerNameChanged.build(command.customerID, command.name));
    }
}
