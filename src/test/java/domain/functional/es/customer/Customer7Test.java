package domain.functional.es.customer;

import domain.shared.Hints;
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
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private CustomerRegistered customerRegistered;
    private List<Event> recordedEvents;

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
        recordedEvents = new ArrayList<>();
    }

    @Test
    void registerCustomer() {
        WHEN_RegisterCustomer();
        THEN_CustomerRegistered();
    }

    void WHEN_RegisterCustomer() {
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerRegistered = Customer7.register(registerCustomer);
        customerID = registerCustomer.customerID;
        confirmationHash = registerCustomer.confirmationHash;
    }

    void THEN_CustomerRegistered() {
        assertNotNull(customerRegistered, Hints.NULL_EVENT);

        assertEquals(emailAddress, customerRegistered.emailAddress, Hints.WRONG_EMAIL_ADDRESS);
        assertEquals(name, customerRegistered.name, Hints.WRONG_NAME);
        assertEquals(customerID, customerRegistered.customerID, Hints.WRONG_CUSTOMER_ID);
        assertEquals(confirmationHash, customerRegistered.confirmationHash, Hints.WRONG_CONFIRMATION_HASH);
    }

    @Test
    void confirmEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_EmailAddressConfirmed();
    }

    void WHEN_ConfirmEmailAddress_withMatchingConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
    }

    void THEN_EmailAddressConfirmed() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmEmailAddress_withWrongConfirmationHash();
        THEN_EmailAddressConfirmationFailed();
    }

    void WHEN_ConfirmEmailAddress_withWrongConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
    }

    void THEN_EmailAddressConfirmationFailed() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
    }

    @Test
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_NothingShouldHappen();
    }

    void THEN_NothingShouldHappen() {
        assertEquals(0, recordedEvents.size(), Hints.SHOULD_BE_NO_EVENT);
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_withWrongConfirmationHash();
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    void changeCustomerEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress();
        THEN_EmailAddressChanged();
    }

    void WHEN_ChangeEmailAddress() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        recordedEvents = Customer7.changeEmailAddress(eventStream, command);
        confirmationHash = command.confirmationHash;
    }

    void THEN_EmailAddressChanged() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
        assertEquals(changedEmailAddress, event.emailAddress, Hints.WRONG_EMAIL_ADDRESS);
        assertEquals(confirmationHash, event.confirmationHash, Hints.WRONG_CONFIRMATION_HASH);
    }

    @Test
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_withUnchangedEmailAddress();
        THEN_NothingShouldHappen();
    }

    void WHEN_ChangeEmailAddress_withUnchangedEmailAddress() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        recordedEvents = Customer7.changeEmailAddress(eventStream, command);
        confirmationHash = command.confirmationHash;
    }

    @Test
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasChanged();
        WHEN_ChangeEmailAddress();
        THEN_NothingShouldHappen();
    }

    @Test
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        __and_EmailAddressWasChanged();

        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_EmailAddressConfirmed();
    }

    @Test
    void changeCustomerName() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeName();
        THEN_NameChanged();
    }

    void WHEN_ChangeName() {
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        recordedEvents = Customer7.changeName(eventStream, command);
        name = changedName;
    }

    void THEN_NameChanged() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerNameChanged.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerNameChanged) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
        assertEquals(name, event.name, Hints.WRONG_NAME);
    }

    @Test
    void changeCustomerName_withUnchangedName() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeName_withUnchangedName();
        THEN_NothingShouldHappen();
    }

    void WHEN_ChangeName_withUnchangedName() {
        var command = ChangeCustomerName.build(customerID.value, name.givenName, name.familyName);
        recordedEvents = Customer7.changeName(eventStream, command);
    }

    @Test
    void changeCustomerName_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        GIVEN_NameWasChanged();
        WHEN_ChangeName();
        THEN_NothingShouldHappen();
    }

    /**
     * Helper methods to set up the Given state
     */
    private void GIVEN_CustomerRegistered() {
        eventStream.add(CustomerRegistered.build(customerID, emailAddress, confirmationHash, name));
    }

    private void __and_EmailAddressWasConfirmed() {
        eventStream.add(CustomerEmailAddressConfirmed.build(customerID));
    }

    private void __and_EmailAddressWasChanged() {
        eventStream.add(CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash));
        emailAddress = changedEmailAddress;
        confirmationHash = changedConfirmationHash;
    }

    private void GIVEN_NameWasChanged() {
        eventStream.add(CustomerNameChanged.build(customerID, changedName));
        name = changedName;
    }
}
