package domain.shared.command;

import domain.shared.value.ID;
import domain.shared.value.PersonName;

public final class ChangeCustomerName {
    public final ID customerID;
    public final PersonName name;

    private ChangeCustomerName(String customerID, String givenName, String familyName) {
        this.customerID = ID.build(customerID);
        this.name = PersonName.build(givenName, familyName);
    }

    public static ChangeCustomerName build(String customerID, String givenName, String familyName) {
        return new ChangeCustomerName(customerID, givenName, familyName);
    }
}
