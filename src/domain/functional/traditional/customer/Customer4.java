package domain.functional.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;

public class Customer4 {
    public static CustomerState register(RegisterCustomer command) {
        return new CustomerState(command.customerID, command.emailAddress, command.confirmationHash, command.name);
    }

    public static CustomerState confirmEmailAddress(CustomerState current, ConfirmCustomerEmailAddress command) throws Exception {
        if (current.isConfirmed) {
            return current;
        }

        if (!command.confirmationHash.equals(current.confirmationHash)) {
            throw new Exception("confirmation hash does not match");
        }

        return new CustomerState(
                current.id,
                current.emailAddress,
                current.confirmationHash,
                current.name,
                true
        );
    }

    public static CustomerState changeEmailAddress(CustomerState current, ChangeCustomerEmailAddress command) {
        return new CustomerState(
                current.id,
                command.emailAddress,
                command.confirmationHash,
                current.name,
                false
        );
    }

    public static CustomerState changeName(CustomerState current, ChangeCustomerName command) {
        return new CustomerState(
                current.id,
                current.emailAddress,
                current.confirmationHash,
                command.name,
                current.isConfirmed
        );
    }
}
