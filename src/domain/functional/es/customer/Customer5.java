package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerName;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.CustomerNameChanged;
import domain.shared.event.CustomerRegistered;
import domain.shared.event.Event;

import java.util.List;

public class Customer5 {

    public static CustomerRegistered register(RegisterCustomer command) {
        return CustomerRegistered.build(
                        command.customerID,
                        command.emailAddress,
                        command.confirmationHash,
                        command.name
                );
    }

    public static List<Event> changeName(CustomerState current, ChangeCustomerName command) {
        if (current.name.equals(command.name)) {
            return List.of();
        }

        return List.of(CustomerNameChanged.build(command.customerID, command.name));
    }
}
