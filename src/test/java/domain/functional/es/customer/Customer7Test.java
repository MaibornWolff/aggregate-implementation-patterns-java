package domain.functional.es.customer;

import domain.shared.Hints;
import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

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
        eventStream = new ArrayList<>();
        recordedEvents = new ArrayList<>();
    }

    @Test
    @Order(1)
    void registerCustomer() {
        WHEN_RegisterCustomer();
        THEN_CustomerRegistered();
    }

    @Test
    @Order(2)
    void confirmEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_EmailAddressConfirmed();
    }

    @Test
    @Order(3)
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmEmailAddress_withWrongConfirmationHash();
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(4)
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(5)
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_withWrongConfirmationHash();
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(6)
    void changeCustomerEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress();
        THEN_EmailAddressChanged();
    }

    @Test
    @Order(7)
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_withUnchangedEmailAddress();
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(8)
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasChanged();
        WHEN_ChangeEmailAddress();
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(9)
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        __and_EmailAddressWasChanged();

        WHEN_ConfirmEmailAddress_withMatchingConfirmationHash();
        THEN_EmailAddressConfirmed();
    }

    /**
     * Methods for GIVEN
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

    /**
     * Methods for WHEN
     */

    private void WHEN_RegisterCustomer() {
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerRegistered = Customer7.register(registerCustomer);
        customerID = registerCustomer.customerID;
        confirmationHash = registerCustomer.confirmationHash;
    }

    private void WHEN_ConfirmEmailAddress_withMatchingConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
    }

    private void WHEN_ConfirmEmailAddress_withWrongConfirmationHash() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
    }

    private void WHEN_ChangeEmailAddress() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        recordedEvents = Customer7.changeEmailAddress(eventStream, command);
        confirmationHash = command.confirmationHash;
    }

    private void WHEN_ChangeEmailAddress_withUnchangedEmailAddress() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        recordedEvents = Customer7.changeEmailAddress(eventStream, command);
        confirmationHash = command.confirmationHash;
    }

    /**
     * Methods for THEN
     */

    private void THEN_CustomerRegistered() {
        assertNotNull(customerRegistered, Hints.NULL_EVENT);

        assertEquals(emailAddress, customerRegistered.emailAddress, Hints.WRONG_EMAIL_ADDRESS);
        assertEquals(name, customerRegistered.name, Hints.WRONG_NAME);
        assertEquals(customerID, customerRegistered.customerID, Hints.WRONG_CUSTOMER_ID);
        assertEquals(confirmationHash, customerRegistered.confirmationHash, Hints.WRONG_CONFIRMATION_HASH);
    }

    private void THEN_EmailAddressConfirmed() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
    }

    private void THEN_EmailAddressConfirmationFailed() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
    }

    private void THEN_NothingShouldHappen() {
        assertEquals(0, recordedEvents.size(), Hints.SHOULD_BE_NO_EVENT);
    }

    private void THEN_EmailAddressChanged() {
        assertEquals(1, recordedEvents.size(), Hints.WRONG_NUMBER_OF_EVENTS);
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass(), Hints.WRONG_EVENT);
        assertNotNull(recordedEvents.get(0), Hints.NULL_EVENT);

        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, Hints.WRONG_CUSTOMER_ID);
        assertEquals(changedEmailAddress, event.emailAddress, Hints.WRONG_EMAIL_ADDRESS);
        assertEquals(confirmationHash, event.confirmationHash, Hints.WRONG_CONFIRMATION_HASH);
    }
}
