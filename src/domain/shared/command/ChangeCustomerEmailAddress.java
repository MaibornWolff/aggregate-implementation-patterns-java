package domain.shared.command;

import domain.shared.value.ID;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;

public final class ChangeCustomerEmailAddress {
    public final ID customerID;
    public final EmailAddress emailAddress;
    public final Hash confirmationHash;

    private ChangeCustomerEmailAddress(String customerID, String emailAddress) {
        this.customerID = ID.build(customerID);
        this.emailAddress = EmailAddress.build(emailAddress);
        this.confirmationHash = Hash.generate();
    }

    public static ChangeCustomerEmailAddress build(String customerID, String emailAddress) {
        return new ChangeCustomerEmailAddress(customerID, emailAddress);
    }
}
