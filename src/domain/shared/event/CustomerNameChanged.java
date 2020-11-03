package domain.shared.event;

import domain.shared.value.ID;
import domain.shared.value.PersonName;

public final class CustomerNameChanged implements Event {
    public final ID customerID;
    public final PersonName name;

    private CustomerNameChanged(ID customerID, PersonName name) {
        this.customerID = customerID;
        this.name = name;
    }

    public static CustomerNameChanged build(ID customerID, PersonName name) {
        return new CustomerNameChanged(customerID, name);
    }
}
