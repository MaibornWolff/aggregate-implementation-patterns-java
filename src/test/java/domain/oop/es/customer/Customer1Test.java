package domain.oop.es.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.event.*;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void registerCustomer() {
        WHEN_RegisterCustomer();
        THEN_CustomerRegistered();
    }

    @Test
    @Order(2)
    void confirmEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmCustomerEmailAddress_With(confirmationHash);
        THEN_CustomerEmailAddressConfirmed();
    }

    @Test
    @Order(3)
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN_CustomerRegistered();
        WHEN_ConfirmCustomerEmailAddress_With(wrongConfirmationHash);
        THEN_CustomerEmailAddressConfirmationFailed();
    }

    @Test
    @Order(4)
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();
        WHEN_ConfirmCustomerEmailAddress_With(confirmationHash);
        THEN_NoEvent();
    }

    @Test
    @Order(5)
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();
        WHEN_ConfirmCustomerEmailAddress_With(wrongConfirmationHash);
        THEN_CustomerEmailAddressConfirmationFailed();
    }

    @Test
    @Order(6)
    void changeCustomerEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();
        WHEN_ChangeCustomerEmailAddress_With(changedEmailAddress);
        THEN_CustomerEmailAddressChanged();
    }

    @Test
    @Order(7)
    void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeCustomerEmailAddress_With(emailAddress);
        THEN_NoEvent();
    }

    @Test
    @Order(8)
    void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasChanged();
        WHEN_ChangeCustomerEmailAddress_With(changedEmailAddress);
        THEN_NoEvent();
    }

    @Test
    @Order(9)
    void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        GIVEN_CustomerRegistered();
        __and_CustomerEmailAddressWasConfirmed();
        __and_CustomerEmailAddressWasChanged();
        WHEN_ConfirmCustomerEmailAddress_With(changedConfirmationHash);
        THEN_CustomerEmailAddressConfirmed();
    }

    /**
     * Methods for GIVEN
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

    private void __and_CustomerEmailAddressWasChanged() {
        registeredCustomer.apply(
                CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
        );
    }

    /**
     * Methods for WHEN
     */

    private void WHEN_RegisterCustomer() {
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        registeredCustomer = Customer1.register(registerCustomer);
        customerID = registerCustomer.customerID;
        confirmationHash = registerCustomer.confirmationHash;
    }

    private void WHEN_ConfirmCustomerEmailAddress_With(Hash confirmationHash) {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        try {
            registeredCustomer.confirmEmailAddress(command);
        } catch (NullPointerException e) {
            fail("PROBLEM: The confirmationHash is null!\n" +
                    "HINT: Maybe you didn't apply the previous events properly!?\n");
        }
    }

    private void WHEN_ChangeCustomerEmailAddress_With(EmailAddress emailAddress) {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        try {
            registeredCustomer.changeEmailAddress(command);
        } catch (NullPointerException e) {
            fail("PROBLEM: The emailAddress is null!\n" +
                    "HINT: Maybe you didn't apply the previous events properly!?\n");
        }
    }

    /**
     * Methods for THEN
     */

    void THEN_CustomerRegistered() {
        String failMsg;

        var recordedEvents = registeredCustomer.getRecordedEvents();

        failMsg = "PROBLEM in register(): No event was recorded!\n" +
                "HINT: Build a CustomerRegistered event and use recordThat() to record it!\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in register(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in register(): An event of the wrong type was recorded!\n" +
                "HINT: You recorded an event of type: " + getClassnameOfFirst(recordedEvents) + "\n";
        assertEquals(CustomerRegistered.class, recordedEvents.get(0).getClass(), failMsg);

        var customerRegistered = (CustomerRegistered) recordedEvents.get(0);

        failMsg = "PROBLEM in register(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n";
        assertEquals(customerID, customerRegistered.customerID, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong emailAddress!\n" +
                "HINT: The emailAddress in the event should be taken from the command!\n";
        assertEquals(emailAddress, customerRegistered.emailAddress, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong confirmationHash!\n" +
                "HINT: The confirmationHash in the event should be taken from the command!\n";
        assertEquals(confirmationHash, customerRegistered.confirmationHash, failMsg);

        failMsg = "PROBLEM in register(): The event contains a wrong name!\n" +
                "HINT: The name in the event should be taken from the command!\n";
        assertEquals(name, customerRegistered.name, failMsg);
    }

    void THEN_CustomerEmailAddressConfirmed() {
        String failMsg;

        var recordedEvents = registeredCustomer.getRecordedEvents();

        failMsg = "PROBLEM in confirmEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressConfirmed event and use recordThat() to record it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You recorded an event of type: " + getClassnameOfFirst(recordedEvents) + "\n";
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);

        failMsg = "PROBLEM in confirmEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n";
        assertEquals(customerID, event.customerID, failMsg);
    }

    void THEN_CustomerEmailAddressConfirmationFailed() {
        String failMsg;

        var recordedEvents = registeredCustomer.getRecordedEvents();

        failMsg = "PROBLEM in confirmEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressConfirmed event and use recordThat() to record it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in confirmEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You recorded an event of type: " + getClassnameOfFirst(recordedEvents) + "\n";
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);

        failMsg = "PROBLEM in confirmEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n";
        assertEquals(customerID, event.customerID, failMsg);
    }

    private void THEN_CustomerEmailAddressChanged() {
        String failMsg;

        var recordedEvents = registeredCustomer.getRecordedEvents();

        failMsg = "PROBLEM in changeEmailAddress(): No event was recorded!\n" +
                "HINTS: Build a CustomerEmailAddressChanged event and use recordThat() to record it!\n" +
                "       Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n";
        assertEquals(1, recordedEvents.size(), failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): The recorded event is NULL!\n" +
                "HINT: There must be some weird code ;-)\n";
        assertNotNull(recordedEvents.get(0), failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): An event of the wrong type was recorded!\n" +
                "HINTS: Did you apply all previous events properly?\n" +
                "       Check your business logic :-)!\n" +
                "       You recorded an event of type: " + getClassnameOfFirst(recordedEvents) + "\n";
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass(), failMsg);

        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);

        failMsg = "PROBLEM in changeEmailAddress(): The event contains a wrong customerID!\n" +
                "HINT: The customerID in the event should be taken from the command!\n";
        assertEquals(customerID, event.customerID, failMsg);

        failMsg = "PROBLEM in changeEmailAddress(): The event contains a wrong emailAddress!\n" +
                "HINT: The customerID in the event should be taken from the command!\n";
        assertEquals(changedEmailAddress, event.emailAddress, failMsg);
    }

    void THEN_NoEvent() {
        var recordedEvents = registeredCustomer.getRecordedEvents();

        var failMsg = "PROBLEM: No event should have been recorded!\n" +
                "HINTS: Check your business logic - this command should be ignored (idempotency)!\n" +
                "       Did you apply all previous events properly?\n" +
                "       The recorded event is of type: " + getClassnameOfFirst(recordedEvents) + "\n";
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
