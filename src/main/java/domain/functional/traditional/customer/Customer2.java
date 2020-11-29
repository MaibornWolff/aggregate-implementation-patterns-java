package domain.functional.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.exception.WrongConfirmationHashException;

public class Customer2 {
    public static CustomerState register(RegisterCustomer command) {
        return new CustomerState(
                command.customerID,
                command.emailAddress,
                command.confirmationHash,
                command.name
        );
    }

    public static CustomerState confirmEmailAddress(CustomerState current, ConfirmCustomerEmailAddress command) throws WrongConfirmationHashException {
        if (!command.confirmationHash.equals(current.confirmationHash)) {
            throw new WrongConfirmationHashException();
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
}
