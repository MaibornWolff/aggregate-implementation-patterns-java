package domain.functional.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.CustomerEmailAddressChanged;
import domain.shared.event.CustomerEmailAddressConfirmationFailed;
import domain.shared.event.CustomerEmailAddressConfirmed;
import domain.shared.event.CustomerRegistered;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Customer5Test {
    private ID customerID;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash confirmationHash;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName name;
    private PersonName changedName;

    @BeforeEach
    void beforeEach() {
        customerID = ID.generate();
        emailAddress = EmailAddress.build("john@doe.com");
        changedEmailAddress = EmailAddress.build("john+changed@doe.com");
        confirmationHash = Hash.generate();
        wrongConfirmationHash = Hash.generate();
        changedConfirmationHash = Hash.generate();
        name = PersonName.build("John", "Doe");
        changedName = PersonName.build("Jayne", "Doe");
    }

    @Test
    void registerCustomer() {
        // When RegisterCustomer
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customerRegistered = Customer5.register(registerCustomer);

        // Then CustomerRegistered
        assertNotNull(customerRegistered);

        //  and the payload should be as expected
        assertEquals(registerCustomer.customerID, customerRegistered.customerID);
        assertEquals(registerCustomer.emailAddress, customerRegistered.emailAddress);
        assertEquals(registerCustomer.confirmationHash, customerRegistered.confirmationHash);
        assertEquals(registerCustomer.name, customerRegistered.name);
    }

    @Test
    void confirmEmailAddress() {
        // Given CustomerRegistered
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var recordedEvents = Customer5.confirmEmailAddress(currentState, command);

        // Then CustomerEmailAddressConfirmed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash() {
        // Given CustomerRegistered
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        var recordedEvents = Customer5.confirmEmailAddress(currentState, command);

        // Then CustomerEmailAddressConfirmationFailed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var recordedEvents = Customer5.confirmEmailAddress(currentState, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        var recordedEvents = Customer5.confirmEmailAddress(currentState, command);

        // Then CustomerEmailAddressConfirmationFailed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void changeEmailAddress() {
        // Given CustomerRegistered
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var recordedEvents = Customer5.changeEmailAddress(currentState, command);

        // Then CustomerEmailAddressChanged
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
        assertEquals(command.emailAddress, event.emailAddress);
        assertEquals(command.confirmationHash, event.confirmationHash);
    }

    @Test
    void changeEmailAddress_withUnchangedEmailAddress() {
        // Given CustomerRegistered
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        var recordedEvents = Customer5.changeEmailAddress(currentState, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeEmailAddress_whenItWasAlreadyChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressChanged
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var recordedEvents = Customer5.changeEmailAddress(currentState, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void confirmEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        //   and CustomerEmailAddressChanged
        var currentState = CustomerState.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        var recordedEvents = Customer5.confirmEmailAddress(currentState, command);

        // Then CustomerEmailAddressConfirmed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }
}
