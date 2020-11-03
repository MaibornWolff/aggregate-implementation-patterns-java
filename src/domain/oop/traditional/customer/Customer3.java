package domain.oop.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;

public class Customer3 {
    final ID id;
    EmailAddress emailAddress;
    Hash confirmationHash;
    boolean isEmailAddressConfirmed;
    PersonName name;

    private Customer3(ID id, EmailAddress emailAddress, Hash confirmationHash, PersonName name) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.confirmationHash = confirmationHash;
        this.name = name;
    }

    public static Customer3 register(RegisterCustomer command) {
        return new Customer3(command.customerID, command.emailAddress, command.confirmationHash, command.name);
    }

    public void confirmEmailAddress(ConfirmCustomerEmailAddress command) throws Exception {
        if (isEmailAddressConfirmed) {
            return;
        }

        if (!command.confirmationHash.equals(confirmationHash)) {
            throw new Exception("confirmation hash does not match");
        }

        isEmailAddressConfirmed = true;
    }

    public void changeEmailAddress(ChangeCustomerEmailAddress command) {
        emailAddress = command.emailAddress;
        confirmationHash = command.confirmationHash;
        isEmailAddressConfirmed = false;
    }

    public void changeName(ChangeCustomerName command) {
        name = command.name;
    }
}
