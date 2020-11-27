package domain.oop.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.exception.WrongConfirmationHashException;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Customer3Test {
    private ID customerID;
    private Hash confirmationHash;
    private PersonName name;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName changedName;
    private Customer3 registeredCustomer;

    @BeforeEach
    void beforeEach() {
        emailAddress = EmailAddress.build("john@doe.com");
        changedEmailAddress = EmailAddress.build("john+changed@doe.com");
        wrongConfirmationHash = Hash.generate();
        changedConfirmationHash = Hash.generate();
        name = PersonName.build("John", "Doe");
        changedName = PersonName.build("Jayne", "Doe");
    }

    @Test
    void registerCustomer() {
        // When registerCustomer
        var command = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customer = Customer3.register(command);

        // Then it should succeed
        // and should have the expected state
        assertNotNull(customer);
        assertEquals(customer.id, command.customerID);
        assertEquals(customer.name, command.name);
        assertEquals(customer.emailAddress, command.emailAddress);
        assertEquals(customer.confirmationHash, command.confirmationHash);
        assertFalse(customer.isEmailAddressConfirmed);
    }

    @Test
    void confirmEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        assertDoesNotThrow(() -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should be confirmed
        assertTrue(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    void confirmEmailAddress_withWrongConfirmationHash() {
        // Given
        givenARegisteredCustomer();

        // When confirmCustomerEmailAddress
        // Then it should throw WrongConfirmationHashException
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertThrows(WrongConfirmationHashException.class, () -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should not be confirmed
        assertFalse(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    void changeEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When changeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        registeredCustomer.changeEmailAddress(command);

        // Then the emailAddress and confirmationHash should be changed and the emailAddress should be unconfirmed
        assertEquals(registeredCustomer.emailAddress, command.emailAddress);
        assertEquals(registeredCustomer.confirmationHash, command.confirmationHash);
        assertFalse(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    void confirmEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();
        givenEmailAddressWasChanged();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        assertDoesNotThrow(() -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should be confirmed
        assertTrue(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    void changeName() {
        // Given
        givenARegisteredCustomer();

        // When changeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        registeredCustomer.changeName(command);

        // Then it should have the expected state
        assertEquals(registeredCustomer.name, command.name);
    }

    /**
     * Helper methods to set up the Given state
     */
    private void givenARegisteredCustomer() {
        var register = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerID = register.customerID;
        confirmationHash = register.confirmationHash;
        registeredCustomer = Customer3.register(register);
    }

    private void givenEmailAddressWasConfirmed() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);

        try {
            registeredCustomer.confirmEmailAddress(command);
        } catch (WrongConfirmationHashException e) {
            fail("unexpected error in givenEmailAddressWasConfirmed: " + e.getMessage());
        }
    }

    private void givenEmailAddressWasChanged() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        changedConfirmationHash = command.confirmationHash;
        registeredCustomer.changeEmailAddress(command);
    }
}
