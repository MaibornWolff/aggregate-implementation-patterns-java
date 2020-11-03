package domain.shared.command;

import domain.shared.value.ID;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

public final class RegisterCustomer {
    public final ID customerID;
    public final EmailAddress emailAddress;
    public final Hash confirmationHash;
    public final PersonName name;

    private RegisterCustomer(String emailAddress, String givenName, String familyName) {
        this.customerID = ID.generate();
        this.confirmationHash = Hash.generate();
        this.emailAddress = EmailAddress.build(emailAddress);
        this.name = PersonName.build(givenName, familyName);
    }

    public static RegisterCustomer build(String emailAddress, String givenName, String familyName) {
        return new RegisterCustomer(emailAddress, givenName, familyName);
    }
}
