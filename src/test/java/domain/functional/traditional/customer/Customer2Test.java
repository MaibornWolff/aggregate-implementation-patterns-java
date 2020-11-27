package domain.functional.traditional.customer;

import domain.shared.command.ChangeCustomerEmailAddress;
import domain.shared.command.ConfirmCustomerEmailAddress;
import domain.shared.command.RegisterCustomer;
import domain.shared.exception.WrongConfirmationHashException;
import domain.shared.value.EmailAddress;
import domain.shared.value.Hash;
import domain.shared.value.ID;
import domain.shared.value.PersonName;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Customer2Test {
    private ID customerID;
    private Hash confirmationHash;
    private PersonName name;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName changedName;
    private CustomerState registeredCustomer;

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
    @Order(1)
    void registerCustomer() {
        // When
        var command = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        var customer = Customer2.register(command);

        // Then it should succeed
        // and it should expose the expected state
        assertNotNull(customer);
        assertEquals(command.customerID, customer.id);
        assertEquals(command.name, customer.name);
        assertEquals(command.emailAddress, customer.emailAddress);
        assertEquals(command.confirmationHash, customer.confirmationHash);
        assertFalse(customer.isEmailAddressConfirmed);
    }

    @Test
    @Order(2)
    void confirmEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When confirmCustomerEmailAddress
        // Then it should succeed
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        var changedCustomer = assertDoesNotThrow(() -> Customer2.confirmEmailAddress(registeredCustomer, command));

        // and the emailAddress of the changed Customer should be confirmed
        assertTrue(changedCustomer.isEmailAddressConfirmed);
    }

    @Test
    @Order(3)
    void confirmEmailAddress_withWrongConfirmationHash() {
        // Given
        givenARegisteredCustomer();

        // When confirmCustomerEmailAddress
        // Then it should throw WrongConfirmationHashException
        var command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        assertThrows(WrongConfirmationHashException.class, () -> Customer2.confirmEmailAddress(registeredCustomer, command));
    }

    @Test
    @Order(6)
    void changeEmailAddress() {
        // Given
        givenARegisteredCustomer();

        // When changeCustomerEmailAddress
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        var changedCustomer = Customer2.changeEmailAddress(registeredCustomer, command);

        // Then the emailAddress and confirmationHash should be changed and the emailAddress should be unconfirmed
        assertEquals(command.emailAddress, changedCustomer.emailAddress);
        assertEquals(command.confirmationHash, changedCustomer.confirmationHash);
        assertFalse(changedCustomer.isEmailAddressConfirmed);
    }

    @Test
    @Order(9)
    void confirmEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given
        givenARegisteredCustomer();
        givenEmailAddressWasConfirmed();
        givenEmailAddressWasChanged();

        // When confirmEmailAddress
        // Then it should throw WrongConfirmationHashException
        var command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        var changedCustomer = assertDoesNotThrow(() -> Customer2.confirmEmailAddress(registeredCustomer, command));

        // and the emailAddress of the changed Customer should be confirmed
        assertTrue(changedCustomer.isEmailAddressConfirmed);
    }

    /**
     * Helper methods to set up the Given state
     */
    private void givenARegisteredCustomer() {
        var register = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        customerID = register.customerID;
        confirmationHash = register.confirmationHash;
        registeredCustomer = Customer2.register(register);
    }

    private void givenEmailAddressWasConfirmed() {
        var command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);

        try {
            registeredCustomer = Customer2.confirmEmailAddress(registeredCustomer, command);
        } catch (WrongConfirmationHashException e) {
            fail("unexpected error in givenEmailAddressWasConfirmed: " + e.getMessage());
        }
    }

    private void givenEmailAddressWasChanged() {
        var command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        changedConfirmationHash = command.confirmationHash;
        registeredCustomer = Customer2.changeEmailAddress(registeredCustomer, command);
    }
}
