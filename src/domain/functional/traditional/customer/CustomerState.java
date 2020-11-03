package domain.functional.traditional.customer;

import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;

public class CustomerState {
    final ID id;
    final EmailAddress emailAddress;
    final Hash confirmationHash;
    final PersonName name;
    final Boolean isConfirmed;

    public CustomerState(ID id, EmailAddress emailAddress, Hash confirmationHash, PersonName name, Boolean isConfirmed) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.confirmationHash = confirmationHash;
        this.name = name;
        this.isConfirmed = isConfirmed;
    }

    public CustomerState(ID id, EmailAddress emailAddress, Hash confirmationHash, PersonName name) {
        this(id, emailAddress, confirmationHash, name, false);
    }
}
