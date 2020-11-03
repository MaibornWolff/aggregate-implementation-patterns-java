package domain.oop.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ChangeCustomerName;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Customer3Test {
    // assigned via givenARegisteredCustomer()
    private Customer3 registeredCustomer;
    private ID customerID;
    private Hash confirmationHash;

    // assigned via beforeEach() + used as input in given....() methods and in test cases
    private PersonName name;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName changedName;

    @BeforeEach
    public void beforeEach() {
        emailAddress = EmailAddress.build("john@doe.com");
        changedEmailAddress = EmailAddress.build("john+changed@doe.com");
        wrongConfirmationHash = Hash.generate();
        changedConfirmationHash = Hash.generate();
        name = PersonName.build("John", "Doe");
        changedName = PersonName.build("Jayne", "Doe");
    }

    // TODO: streamline the "then" comments

    @Test
    public void registerCustomer() {
        // When registerCustomer
        var command = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customer = Customer3.register(command);

        // Then it should succeed
        assertNotNull(customer);

        // and it should have the expected state
        assertTrue(command.customerID.equals(customer.id));
        assertTrue(command.name.equals(customer.name));
        assertTrue(command.emailAddress.equals(customer.emailAddress));
        assertTrue(command.confirmationHash.equals(customer.confirmationHash));
        assertFalse(customer.isEmailAddressConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress() {
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
    public void confirmCustomerEmailAddress_whenEmailAddressWasChanged() {
        // Given
        givenARegisteredCustomer();
        givenCustomerEmailAddressWasChanged();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        assertDoesNotThrow(() -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should be confirmed
        assertTrue(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress_withWrongConfirmationHash() {
        // Given
        givenARegisteredCustomer();

        // When confirmCustomerEmailAddress
        // Then it should throw ConfirmationHashDoesNotMatchException
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertThrows(Exception.class, () -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should not be confirmed
        assertFalse(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertDoesNotThrow(() -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should be confirmed
        assertTrue(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    public void changeCustomerEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When changeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        registeredCustomer.changeEmailAddress(command);

        // Then the emailAddress and confirmationHash should be changed and the emailAddress should be unconfirmed
        assertTrue(command.emailAddress.equals(registeredCustomer.emailAddress));
        assertTrue(command.confirmationHash.equals(registeredCustomer.confirmationHash));
        assertFalse(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress_withWrongConfirmationHash_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();
        givenCustomerEmailAddressWasChanged();

        // When confirmEmailAddress
        // Then it should throw ConfirmationHashDoesNotMatchException
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertThrows(Exception.class, () -> registeredCustomer.confirmEmailAddress(command));

        // and the emailAddress should not be confirmed
        assertFalse(registeredCustomer.isEmailAddressConfirmed);
    }

    @Test
    public void changeCustomerName() {
        // Given
        givenARegisteredCustomer();

        // When changeCustomerName
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        registeredCustomer.changeName(command);

        // and it should have the expected state
        assertTrue(command.name.equals(registeredCustomer.name));
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
        } catch (Exception e) {
            fail("unexpected error in givenEmailAddressWasConfirmed: " + e.getMessage());
        }
    }

    private void givenCustomerEmailAddressWasChanged() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        changedConfirmationHash = command.confirmationHash;
        registeredCustomer.changeEmailAddress(command);
    }

    private void givenCustomerNameWasChanged() {
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        registeredCustomer.changeName(command);
    }
}