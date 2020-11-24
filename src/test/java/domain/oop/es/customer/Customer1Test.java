package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Customer1Test {
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
        var customer = Customer1.register(registerCustomer);

        // Then CustomerRegistered
        var recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerRegistered.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var customerRegistered = (CustomerRegistered) recordedEvents.get(0);
        assertEquals(registerCustomer.customerID, customerRegistered.customerID);
        assertEquals(registerCustomer.emailAddress, customerRegistered.emailAddress);
        assertEquals(registerCustomer.confirmationHash, customerRegistered.confirmationHash);
        assertEquals(registerCustomer.name, customerRegistered.name);
    }

    @Test
    void confirmEmailAddress() {
        // Given CustomerRegistered
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmed
        var recordedEvents = customer.getRecordedEvents();
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
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmationFailed
        var recordedEvents = customer.getRecordedEvents();
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
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmationFailed
        var recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void changeCustomerEmailAddress() {
        // Given CustomerRegistered
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        customer.changeEmailAddress(command);

        // Then CustomerEmailAddressChanged
        var recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
        assertEquals(command.emailAddress, event.emailAddress);
    }

    @Test
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given CustomerRegistered
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        customer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressChanged
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        customer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        //   and CustomerEmailAddressChanged
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmed
        var recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void changeCustomerName() {
        // Given CustomerRegistered
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        customer.changeName(command);

        // Then CustomerNameChanged
        var recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerNameChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerNameChanged) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
        assertEquals(command.name, event.name);
    }

    @Test
    void changeCustomerName_withUnchangedName() {
        // Given CustomerRegistered
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, name.givenName, name.familyName);
        customer.changeName(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    void changeCustomerName_whenItWasAlreadyChanged() {
        // Given CustomerRegistered
        //   and CustomerNameChanged
        var customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerNameChanged.build(customerID, changedName)
                )
        );

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        customer.changeName(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }
}
