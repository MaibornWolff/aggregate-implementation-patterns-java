package domain.functional.es.customer;

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

import static org.junit.jupiter.api.Assertions.*;

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
        WHEN_ConfirmEmailAddress_With(confirmationHash);
        THEN_EmailAddressConfirmed();
    }

    @Test
    @Order(3)
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmEmailAddress_With(wrongConfirmationHash);
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(4)
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_With(confirmationHash);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(5)
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        WHEN_ConfirmEmailAddress_With(wrongConfirmationHash);
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(6)
    void changeCustomerEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_EmailAddressChanged();
    }

    @Test
    @Order(7)
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_With(emailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(8)
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasChanged();
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(9)
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasConfirmed();
        __and_EmailAddressWasChanged();

        WHEN_ConfirmEmailAddress_With(changedConfirmationHash);
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

    private void WHEN_ConfirmEmailAddress_With(Hash confirmationHash) {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        try {
            recordedEvents = Customer7.confirmEmailAddress(eventStream, command);
        } catch (NullPointerException e) {
            fail("PROBLEM: The confirmationHash is null!\n" +
                    "HINT: Maybe you didn't apply the previous events properly!?\n");
        }
    }

    private void WHEN_ChangeEmailAddress_With(EmailAddress emailAddress) {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        try {
            recordedEvents = Customer7.changeEmailAddress(eventStream, command);
            changedConfirmationHash = command.confirmationHash;
        } catch (NullPointerException e) {
            fail("PROBLEM: The emailAddress is null!\n" +
                    "HINT: Maybe you didn't apply the previous events properly!?\n");
        }
    }

    /**
     * Methods for THEN
     */

    private void THEN_CustomerRegistered() {
        String failMsg;

        failMsg = "PROBLEM in register(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n\n";
        assertNotNull(customerRegistered, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n\n";
        assertEquals(customerID, customerRegistered.customerID, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong emailAddress!\n" +
                "HINT: The emailAddress in the event should be taken from the command!\n\n";
        assertEquals(emailAddress, customerRegistered.emailAddress, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong confirmationHash!\n" +
                "HINT: The confirmationHash in the event should be taken from the command!\n\n";
        assertEquals(confirmationHash, customerRegistered.confirmationHash, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong name!\n" +
                "HINT: The name in the event should be taken from the command!\n\n";
        assertEquals(name, customerRegistered.name, failMsg);
    }

    private void THEN_EmailAddressConfirmed() {
        String failMsg;

        failMsg = "PROBLEM in confirmEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressConfirmed event and return it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You returned an event of type: " + getClassnameOfFirst(recordedEvents) + "\n\n";
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);

        failMsg = "PROBLEM in confirmEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n\n";
        assertEquals(customerID, event.customerID, failMsg);
    }

    private void THEN_EmailAddressConfirmationFailed() {
        String failMsg;

        failMsg = "PROBLEM in confirmEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressConfirmationFailed event and return it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You returned an event of type: " + getClassnameOfFirst(recordedEvents) + "\n\n";
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);

        failMsg = "PROBLEM in confirmEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n\n";
        assertEquals(customerID, event.customerID, failMsg);
    }

    private void THEN_EmailAddressChanged() {
        String failMsg;

        failMsg = "PROBLEM in changeEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressChanged event and return it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You returned an event of type: " + getClassnameOfFirst(recordedEvents) + "\n\n";
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);

        failMsg = "PROBLEM in changeEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n\n";
        assertEquals(customerID, event.customerID, failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): The event contains a wrong emailAddress!\n" +
                "HINT: The emailAddress in the event should be taken from the command!\n\n";
        assertEquals(changedEmailAddress, event.emailAddress, failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): The event contains a wrong confirmationHash!\n" +
                "HINT: The confirmationHash in the event should be taken from the command!\n\n";
        assertEquals(changedConfirmationHash, event.confirmationHash, failMsg);
    }

    private void THEN_NothingShouldHappen() {
        var failMsg = "PROBLEM: No event should have been recorded!\n" +
                "HINTS: Check your business logic - this command should be ignored (idempotency)!\n" +
                "       Did you apply all previous events properly?\n" +
                "       The returned event is of type: " + getClassnameOfFirst(recordedEvents) + "\n\n";
        assertEquals(0, recordedEvents.size(), failMsg);
    }

    /**
     * Helper methods
     */

    private String getClassnameOfFirst(List<Event> recordedEvents) {
        if (recordedEvents.size() == 0) {
            return "???";
        }

        return recordedEvents.get(0).getClass().toString();
    }
}
