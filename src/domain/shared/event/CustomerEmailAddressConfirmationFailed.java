package domain.shared.event;

import domain.shared.value.ID;

public final class CustomerEmailAddressConfirmationFailed implements Event {
    public final ID customerID;

    private CustomerEmailAddressConfirmationFailed(ID customerID) {
        this.customerID = customerID;
    }

    public static CustomerEmailAddressConfirmationFailed build(ID customerID) {
        return new CustomerEmailAddressConfirmationFailed(customerID);
    }
}
