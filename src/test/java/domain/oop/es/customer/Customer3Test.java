package domain.oop.es.customer;

import domain.THelper;
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
class Customer3Test {
    private ID customerID;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash confirmationHash;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName name;
    private CustomerRegistered customerRegistered;
    private List<Event> recordedEvents;
    private Customer3 registeredCustomer;

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
        GIVEN(CustomerIsRegistered());
        WHEN_ConfirmEmailAddress_With(confirmationHash);
        THEN_EmailAddressConfirmed();
    }

    @Test
    @Order(3)
    void confirmEmailAddress_withWrongConfirmationHash() {
        GIVEN(CustomerIsRegistered());
        WHEN_ConfirmEmailAddress_With(wrongConfirmationHash);
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(4)
    void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        GIVEN(CustomerIsRegistered(),
                __and_EmailAddressWasConfirmed());
        WHEN_ConfirmEmailAddress_With(confirmationHash);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(5)
    void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        GIVEN(CustomerIsRegistered(),
                __and_EmailAddressWasConfirmed());
        WHEN_ConfirmEmailAddress_With(wrongConfirmationHash);
        THEN_EmailAddressConfirmationFailed();
    }

    @Test
    @Order(6)
    void changeEmailAddress() {
        GIVEN(CustomerIsRegistered());
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_EmailAddressChanged();
    }

    @Test
    @Order(7)
    void changeEmailAddress_withUnchangedEmailAddress() {
        GIVEN(CustomerIsRegistered());
        WHEN_ChangeEmailAddress_With(emailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(8)
    void changeEmailAddress_whenItWasAlreadyChanged() {
        GIVEN(CustomerIsRegistered(),
                __and_EmailAddressWasChanged());
        WHEN_ChangeEmailAddress_With(changedEmailAddress);
        THEN_NothingShouldHappen();
    }

    @Test
    @Order(9)
    void confirmEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        GIVEN(CustomerIsRegistered(),
                __and_EmailAddressWasConfirmed(),
                __and_EmailAddressWasChanged());
        WHEN_ConfirmEmailAddress_With(changedConfirmationHash);
        THEN_EmailAddressConfirmed();
    }

    /**
     * Methods for GIVEN
     */

    private void GIVEN(Event... events) {
        registeredCustomer = Customer3.reconstitute(List.of(events));
    }

    private CustomerRegistered CustomerIsRegistered() {
        return CustomerRegistered.build(customerID, emailAddress, confirmationHash, name);
    }

    private CustomerEmailAddressConfirmed __and_EmailAddressWasConfirmed() {
        return CustomerEmailAddressConfirmed.build(customerID);
    }

    private CustomerEmailAddressChanged __and_EmailAddressWasChanged() {
        return CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash);
    }

    /**
     * Methods for WHEN
     */

    private void WHEN_RegisterCustomer() {
        var registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerRegistered = Customer3.register(registerCustomer);
        customerID = registerCustomer.customerID;
        confirmationHash = registerCustomer.confirmationHash;
    }

    private void WHEN_ConfirmEmailAddress_With(Hash confirmationHash) {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        try {
            recordedEvents = registeredCustomer.confirmEmailAddress(command);
        } catch (NullPointerException e) {
            fail(THelper.propertyIsNull("confirmationHash"));
        }
    }

    private void WHEN_ChangeEmailAddress_With(EmailAddress emailAddress) {
        var command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        try {
            recordedEvents = registeredCustomer.changeEmailAddress(command);
        } catch (NullPointerException e) {
            fail(THelper.propertyIsNull("emailAddress"));
        }
    }

    /**
     * Methods for THEN
     */

    private void THEN_CustomerRegistered() {
        var method = "register";
        var eventName = "CustomerRegistered";
        assertNotNull(customerRegistered, THelper.eventIsNull("register", eventName));
        assertEquals(customerID, customerRegistered.customerID, THelper.propertyIsWrong(method, "customerID"));
        assertEquals(emailAddress, customerRegistered.emailAddress, THelper.propertyIsWrong(method, "emailAddress"));
        assertEquals(confirmationHash, customerRegistered.confirmationHash, THelper.propertyIsWrong(method, "confirmationHash"));
        assertEquals(name, customerRegistered.name, THelper.propertyIsWrong(method, "name"));
    }

    private void THEN_EmailAddressConfirmed() {
        var method = "confirmEmailAddress";
        var eventName = "CustomerEmailAddressConfirmed";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, eventName));
        var event = recordedEvents.get(0);
        assertNotNull(event, THelper.eventIsNull(method, eventName));
        assertEquals(CustomerEmailAddressConfirmed.class, event.getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        assertEquals(customerID, ((CustomerEmailAddressConfirmed) event).customerID, THelper.propertyIsWrong(method, "customerID"));
    }

    private void THEN_EmailAddressConfirmationFailed() {
        var method = "confirmEmailAddress";
        var eventName = "CustomerEmailAddressConfirmationFailed";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, eventName));
        var event = recordedEvents.get(0);
        assertNotNull(event, THelper.eventIsNull(method, eventName));
        assertEquals(CustomerEmailAddressConfirmationFailed.class, event.getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        assertEquals(customerID, ((CustomerEmailAddressConfirmationFailed) event).customerID, THelper.propertyIsWrong(method, "customerID"));
    }

    private void THEN_EmailAddressChanged() {
        var method = "changeEmailAddress";
        var eventName = "CustomerEmailAddressChanged";
        assertEquals(1, recordedEvents.size(), THelper.noEventWasRecorded(method, eventName));
        var event = recordedEvents.get(0);
        assertNotNull(event, THelper.eventIsNull(method, eventName));
        assertEquals(CustomerEmailAddressChanged.class, event.getClass(), THelper.eventOfWrongTypeWasRecorded(method));
        assertEquals(customerID, ((CustomerEmailAddressChanged) event).customerID, THelper.propertyIsWrong(method, "customerID"));
        assertEquals(changedEmailAddress, ((CustomerEmailAddressChanged) event).emailAddress, THelper.propertyIsWrong(method, "emailAddress"));
    }

    private void THEN_NothingShouldHappen() {
        assertEquals(0, recordedEvents.size(), THelper.noEventShouldHaveBeenRecorded(THelper.typeOfFirst(recordedEvents)));
    }
}
