package domain.oop.es.customer;

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
import org.junit.jupiter.api.Assumptions;
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
    private Customer1 registeredCustomer;

    @BeforeEach
    void beforeEach() {
        customerID = ID.generate();
        emailAddress = EmailAddress.build("john@doe.com");
        changedEmailAddress = EmailAddress.build("john+changed@doe.com");
        confirmationHash = Hash.generate();
        wrongConfirmationHash = Hash.generate();
        changedConfirmationHash = Hash.generate();
        name = PersonName.build("John", "Doe");
    }

    @Test
    void registerCustomer() {
        WHEN_RegisterCustomer();
        THEN_CustomerRegistered();
    }

    void WHEN_RegisterCustomer() {
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        registeredCustomer = Customer1.register(registerCustomer);
        customerID = registerCustomer.customerID;
        confirmationHash = registerCustomer.confirmationHash;
    }

    void THEN_CustomerRegistered() {
        var recordedEvents = registeredCustomer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerRegistered.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        var customerRegistered = (CustomerRegistered) recordedEvents.get(0);
        assertEquals(customerID, customerRegistered.customerID);
        assertEquals(emailAddress, customerRegistered.emailAddress);
        assertEquals(confirmationHash, customerRegistered.confirmationHash);
        assertEquals(name, customerRegistered.name);
    }

    @Test
    void confirmEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmCustomerEmailAddress_withMatchingConfirmationHash();
        THEN_CustomerEmailAddressConfirmed();
    }

    void WHEN_ConfirmCustomerEmailAddress_withMatchingConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        try {
            registeredCustomer.confirmEmailAddress(command);
        } catch (NullPointerException e) {
            fail("HINT: The confirmationHash is null - guess you don't apply the events properly!?");
        }
    }

    void THEN_CustomerEmailAddressConfirmed() {
        var recordedEvents = registeredCustomer.getRecordedEvents();
        assertEquals(1, recordedEvents.size(), "HINT: Check your code in confirmEmailAddress()!");
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), "HINT: Maybe you have the wrong confirmation hash!?");
        assertNotNull(recordedEvents.get(0), "HINT: A null event was recorded?");

        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, "HINT: The recorded event has a wrong customerID");
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmCustomerEmailAddress_withWrongConfirmationHash();
        THEN_CustomerEmailAddressConfirmationFailed();
    }

    void WHEN_ConfirmCustomerEmailAddress_withWrongConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        registeredCustomer.confirmEmailAddress(command);
    }

    void THEN_CustomerEmailAddressConfirmationFailed() {
        var recordedEvents = registeredCustomer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID);
    }

    @Test
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();
        WHEN_ConfirmCustomerEmailAddress();
        THEN_NoEvent();
    }

    void WHEN_ConfirmCustomerEmailAddress() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        registeredCustomer.confirmEmailAddress(command);
    }

    void THEN_NoEvent() {
        assertEquals(0, registeredCustomer.getRecordedEvents().size());
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        registeredCustomer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmationFailed
        var recordedEvents = registeredCustomer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    @Test
    void changeCustomerEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        registeredCustomer.changeEmailAddress(command);

        // Then CustomerEmailAddressChanged
        var recordedEvents = registeredCustomer.getRecordedEvents();
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
        // Given
        GIVEN_CustomerRegistered();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        registeredCustomer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, registeredCustomer.getRecordedEvents().size());
    }

    @Test
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        // Given
        GIVEN_CustomerRegistered();
        andCustomerEmailAddressWasChanged();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        registeredCustomer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, registeredCustomer.getRecordedEvents().size());
    }

    @Test
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();
        andCustomerEmailAddressWasChanged();

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        registeredCustomer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmed
        var recordedEvents = registeredCustomer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(command.customerID, event.customerID);
    }

    /**
     * Helper methods to set up the Given state
     */
    private void GIVEN_CustomerRegistered() {
        registeredCustomer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );
    }

    private void __and_CustomerEmailAddressWasConfirmed() {
        registeredCustomer.apply(
                CustomerEmailAddressConfirmed.build(customerID)
        );
    }

    private void andCustomerEmailAddressWasChanged() {
        registeredCustomer.apply(
                CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
        );
    }
}
