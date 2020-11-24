package domain.functional.es.customer;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// copy of Customer6Test since the method signatures are the same
class Customer7Test {
    private ID customerID;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash confirmationHash;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName name;
    private PersonName changedName;
    private List<Event> eventStream;

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
        eventStream = new ArrayList<>();
    }

    @Test
    void registerCustomer() {
        // When RegisterCustomer
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customerRegistered = Customer7.register(registerCustomer);

        // Then CustomerRegistered
        assertNotNull(customerRegistered);

        //  and the payload should be as expected
        assertTrue(customerRegistered.customerID.equals(registerCustomer.customerID));
        assertTrue(customerRegistered.emailAddress.equals(registerCustomer.emailAddress));
        assertTrue(customerRegistered.confirmationHash.equals(registerCustomer.confirmationHash));
        assertTrue(customerRegistered.name.equals(registerCustomer.name));
    }

    @Test
    void confirmEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var recordedEvents = Customer7.confirmEmailAddress(eventStream, command);

        // Then CustomerEmailAddressConfirmed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));

        // When the same command is handled again, Then no event should be recorded
        eventStream.add(event);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash() {
        // Given
        givenARegisteredCustomer();

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        var recordedEvents = Customer7.confirmEmailAddress(eventStream, command);

        // Then CustomerEmailAddressConfirmationFailed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        CustomerEmailAddressConfirmationFailed event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
    }

    @Test
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var recordedEvents = Customer7.confirmEmailAddress(eventStream, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        var recordedEvents = Customer7.confirmEmailAddress(eventStream, command);

        // Then CustomerEmailAddressConfirmationFailed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        CustomerEmailAddressConfirmationFailed event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
    }

    @Test
    void changeCustomerEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var recordedEvents = Customer7.changeEmailAddress(eventStream, command);

        // Then CustomerEmailAddressChanged
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        CustomerEmailAddressChanged event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
        assertTrue(event.emailAddress.equals(command.emailAddress));
        assertTrue(event.confirmationHash.equals(command.confirmationHash));

        // When the same command is handled again, Then no event should be recorded
        eventStream.add(event);
        recordedEvents = Customer7.changeEmailAddress(eventStream, command);
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        var recordedEvents = Customer7.changeEmailAddress(eventStream, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasChanged();

        // When ChangeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var recordedEvents = Customer7.changeEmailAddress(eventStream, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();
        givenEmailAddressWasChanged();

        // When ConfirmCustomerEmailAddress
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        var recordedEvents = Customer7.confirmEmailAddress(eventStream, command);

        // Then CustomerEmailAddressConfirmed
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        CustomerEmailAddressConfirmed event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));

        // When the same command is handled again, Then no event should be recorded
        eventStream.add(event);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeCustomerName() {
        // Given
        givenARegisteredCustomer();

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        var recordedEvents = Customer7.changeName(eventStream, command);

        // Then CustomerNameChanged
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerNameChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0));

        //  and the payload should be as expected
        var event = (CustomerNameChanged) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
        assertTrue(event.name.equals(command.name));

        // When the same command is handled again, Then no event should be recorded
        eventStream.add(event);
        recordedEvents = Customer7.changeName(eventStream, command);
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeCustomerName_withUnchangedName() {
        // Given
        givenARegisteredCustomer();

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, name.givenName, name.familyName);
        var recordedEvents = Customer7.changeName(eventStream, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    @Test
    void changeCustomerName_whenItWasAlreadyChanged() {
        // Given
        givenARegisteredCustomer();
        givenNameWasChanged();

        // When ChangeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        var recordedEvents = Customer7.changeName(eventStream, command);

        // Then no event
        assertEquals(0, recordedEvents.size());
    }

    /**
     * Helper methods to set up the Given state
     */
    private void givenARegisteredCustomer() {
        eventStream.add(CustomerRegistered.build(customerID, emailAddress, confirmationHash, name));
    }

    private void givenEmailAddressWasConfirmed() {
        eventStream.add(CustomerEmailAddressConfirmed.build(customerID));
    }

    private void givenEmailAddressWasChanged() {
        eventStream.add(CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash));
    }

    private void givenNameWasChanged() {
        eventStream.add(CustomerNameChanged.build(customerID, changedName));
    }
}
