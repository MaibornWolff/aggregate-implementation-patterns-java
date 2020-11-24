package domain.shared.event;

import domain.shared.value.ID;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.PersonName;

public final class CustomerRegistered implements Event {
    public final ID customerID;
    public final EmailAddress emailAddress;
    public final Hash confirmationHash;
    public final PersonName name;

    private CustomerRegistered(ID customerID, EmailAddress emailAddress, Hash confirmationHash, PersonName name) {
        this.customerID = customerID;
        this.emailAddress = emailAddress;
        this.confirmationHash = confirmationHash;
        this.name = name;
    }

    public static CustomerRegistered build(
            ID id,
            EmailAddress emailAddress,
            Hash confirmationHash,
            PersonName name
    ) {
        return new CustomerRegistered(id, emailAddress, confirmationHash, name);
    }
}
