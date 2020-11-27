package domain.functional.es.customer;

import domain.THelper;
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
    void changeEmailAddress() {
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_EmailAddressChanged();
    }

    @Test
    @Order(7)
    void changeEmailAddress_withUnchangedEmailAddress() {
        // Given
        GIVEN_CustomerRegistered();
        WHEN_ChangeEmailAddress_With(emailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(8)
    void changeEmailAddress_whenItWasAlreadyChanged() {
        GIVEN_CustomerRegistered();
        __and_EmailAddressWasChanged();
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(9)
    void confirmEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
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
            fail(THelper.propertyIsNull("confirmationHash"));
        }
    }

    private void WHEN_ChangeEmailAddress_With(EmailAddress emailAddress) {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        try {
            recordedEvents = Customer7.changeEmailAddress(eventStream, command);
            changedConfirmationHash = command.confirmationHash;
        } catch (NullPointerException e) {
            fail(THelper.propertyIsNull("emailAddress"));
        }
    }

    /**
     * Methods for THEN
     */

    private void THEN_CustomerRegistered() {
        var method = "register";
        assertNotNull(customerRegistered, THelper.eventIsNull(method));
        assertEquals(customerID, customerRegistered.customerID, THelper.propertyIsWrong(method, "customerID"));
        assertEquals(emailAddress, customerRegistered.emailAddress, THelper.propertyIsWrong(method, "emailAddress"));
        assertEquals(confirmationHash, customerRegistered.confirmationHash, THelper.propertyIsWrong(method, "confirmationHash"));
        assertEquals(name, customerRegistered.name, THelper.propertyIsWrong(method, "name"));
    }

    private void THEN_EmailAddressConfirmed() {
        var method = "confirmEmailAddress";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, "CustomerEmailAddressConfirmed"));
        assertNotNull(recordedEvents.get(0), THelper.eventIsNull(method));
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        var event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, THelper.propertyIsWrong(method, "customerID"));
    }

    private void THEN_EmailAddressConfirmationFailed() {
        var method = "confirmEmailAddress";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, "CustomerEmailAddressConfirmationFailed"));
        assertNotNull(recordedEvents.get(0), THelper.eventIsNull(method));
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        var event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, THelper.propertyIsWrong(method, "customerID"));
    }

    private void THEN_EmailAddressChanged() {
        var method = "changeEmailAddress";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, "CustomerEmailAddressChanged"));
        assertNotNull(recordedEvents.get(0), THelper.eventIsNull(method));
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        var event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertEquals(customerID, event.customerID, THelper.propertyIsWrong(method, "customerID"));
        assertEquals(changedEmailAddress, event.emailAddress, THelper.propertyIsWrong(method, "emailAddress"));
        assertEquals(changedConfirmationHash, event.confirmationHash, THelper.propertyIsWrong(method, "confirmationHash"));
    }

    private void THEN_NothingShouldHappen() {
        assertEquals(0, recordedEvents.size(),
                THelper.noEventShouldHaveBeenRecorded(THelper.typeOfFirst(recordedEvents)));
    }
}
