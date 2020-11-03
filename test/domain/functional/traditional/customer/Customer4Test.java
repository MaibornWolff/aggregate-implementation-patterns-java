package domain.functional.traditional.customer;

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

class Customer4Test {
    // assigned via givenARegisteredCustomer()
    private CustomerState registeredCustomer;
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

    @Test
    public void registerCustomer() {
        // When
        var command = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customer = Customer4.register(command);

        // Then
        assertNotNull(customer);

        // and it should expose the expected state
        assertEquals(command.customerID, customer.id);
        assertEquals(command.name, customer.name);
        assertEquals(command.emailAddress, customer.emailAddress);
        assertEquals(command.confirmationHash, customer.confirmationHash);
        assertFalse(customer.isConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When / Then
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var changedCustomer = assertDoesNotThrow(() -> Customer4.confirmEmailAddress(registeredCustomer, command));

        // and the emailAddress of the changed Customer should be confirmed
        assertTrue(changedCustomer.isConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress_whenEmailAddressWasChanged() {
        // Given
        givenARegisteredCustomer();
        givenCustomerEmailAddressWasChanged();

        // When / Then
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        var changedCustomer = assertDoesNotThrow(() -> Customer4.confirmEmailAddress(registeredCustomer, command));

        // and the emailAddress of the changed Customer should be confirmed
        assertTrue(changedCustomer.isConfirmed);
    }

    @Test
    public void confirmCustomerEmailAddress_withWrongConfirmationHash() {
        // Given
        givenARegisteredCustomer();

        // When / Then
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertThrows(Exception.class, () -> Customer4.confirmEmailAddress(registeredCustomer, command));
    }

    @Test
    public void confirmCustomerEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertDoesNotThrow(() -> Customer4.confirmEmailAddress(registeredCustomer, command));
    }

    @Test
    public void changeCustomerEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When / Then
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var changedCustomer = Customer4.changeEmailAddress(registeredCustomer, command);

        // and the emailAddress and confirmationHash should be changed and the emailAddress should be unconfirmed
        assertEquals(command.emailAddress, changedCustomer.emailAddress);
        assertEquals(command.confirmationHash, changedCustomer.confirmationHash);
        assertFalse(changedCustomer.isConfirmed);
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
        assertThrows(Exception.class, () -> Customer4.confirmEmailAddress(registeredCustomer, command));
    }

    @Test
    public void changeCustomerName() {
        // Given
        givenARegisteredCustomer();

        // When
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        var changedCustomer = Customer4.changeName(registeredCustomer, command);

        // and it should expose the expected state
        assertTrue(command.name.equals(changedCustomer.name));
    }

    /**
     * Helper methods to set up the Given state
     */
    private void givenARegisteredCustomer() {
        var register = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerID = register.customerID;
        confirmationHash = register.confirmationHash;
        registeredCustomer = Customer4.register(register);
    }

    private void givenEmailAddressWasConfirmed() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);

        try {
            registeredCustomer = Customer4.confirmEmailAddress(registeredCustomer, command);
        } catch (Exception e) {
            fail("unexpected error in givenEmailAddressWasConfirmed: " + e.getMessage());
        }
    }

    private void givenCustomerEmailAddressWasChanged() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        changedConfirmationHash = command.confirmationHash;
        registeredCustomer = Customer4.changeEmailAddress(registeredCustomer, command);
    }

    private void givenCustomerNameWasChanged() {
        var command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        registeredCustomer = Customer4.changeName(registeredCustomer, command);
    }
}