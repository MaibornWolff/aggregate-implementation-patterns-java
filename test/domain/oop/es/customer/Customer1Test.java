package domain.oop.es.customer;

import domain.oop.es.customer.Customer1;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Customer1Test {
    private ID customerID;
    private EmailAddress emailAddress;
    private EmailAddress changedEmailAddress;
    private Hash confirmationHash;
    private Hash wrongConfirmationHash;
    private Hash changedConfirmationHash;
    private PersonName name;
    private PersonName changedName;

    @BeforeEach
    public void beforeEach() {
        customerID = ID.generate();
        emailAddress = EmailAddress.build("john@doe.com");
        changedEmailAddress = EmailAddress.build("john+changed@doe.com");
        confirmationHash = Hash.generate();
        wrongConfirmationHash = Hash.generate();
        changedConfirmationHash = Hash.generate();
        name = PersonName.build("John", "Doe");
        changedName = PersonName.build("Jayne", "Doe");
    }

    @Test
    public void registerCustomer() {
        // When RegisterCustomer
        RegisterCustomer registerCustomer = RegisterCustomer.build(emailAddress.value, name.givenName, name.familyName);
        Customer1 customer = Customer1.register(registerCustomer);

        // Then CustomerRegistered
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerRegistered.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerRegistered customerRegistered = (CustomerRegistered) recordedEvents.get(0);
        assertTrue(customerRegistered.customerID.equals(registerCustomer.customerID));
        assertTrue(customerRegistered.emailAddress.equals(registerCustomer.emailAddress));
        assertTrue(customerRegistered.confirmationHash.equals(registerCustomer.confirmationHash));
        assertTrue(customerRegistered.name.equals(registerCustomer.name));
    }

    @Test
    public void confirmEmailAddress() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmed
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerEmailAddressConfirmed event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));

        // When the same command is handled again, Then no event should be recorded
        customer.confirmEmailAddress(command);
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void confirmEmailAddress_withWrongConfirmationHash() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmationFailed
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerEmailAddressConfirmationFailed event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
    }

    @Test
    public void confirmEmailAddress_whenItWasAlreadyConfirmed() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, confirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void confirmEmailAddress_withWrongConfirmationHash_whenItWasAlreadyConfirmed() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID)
                )
        );

        // When ConfirmCustomerEmailAddress (with wrong confirmationHash)
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, wrongConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmationFailed
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmationFailed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerEmailAddressConfirmationFailed event = (CustomerEmailAddressConfirmationFailed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
    }

    @Test
    public void changeCustomerEmailAddress() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        ChangeCustomerEmailAddress command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        customer.changeEmailAddress(command);

        // Then CustomerEmailAddressChanged
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerEmailAddressChanged event = (CustomerEmailAddressChanged) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
        assertTrue(event.emailAddress.equals(command.emailAddress));
        assertTrue(event.confirmationHash.equals(command.confirmationHash));

        // When the same command is handled again, Then no event should be recorded
        customer.changeEmailAddress(command);
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void changeCustomerEmailAddress_withUnchangedEmailAddress() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerEmailAddress
        ChangeCustomerEmailAddress command = ChangeCustomerEmailAddress.build(customerID.value, emailAddress.value);
        customer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void changeCustomerEmailAddress_whenItWasAlreadyChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressChanged
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ChangeCustomerEmailAddress
        ChangeCustomerEmailAddress command = ChangeCustomerEmailAddress.build(customerID.value, changedEmailAddress.value);
        customer.changeEmailAddress(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void confirmCustomerEmailAddress_whenItWasPreviouslyConfirmedAndThenChanged() {
        // Given CustomerRegistered
        //   and CustomerEmailAddressConfirmed
        //   and CustomerEmailAddressChanged
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerEmailAddressConfirmed.build(customerID),
                        CustomerEmailAddressChanged.build(customerID, changedEmailAddress, changedConfirmationHash)
                )
        );

        // When ConfirmCustomerEmailAddress
        ConfirmCustomerEmailAddress command = ConfirmCustomerEmailAddress.build(customerID.value, changedConfirmationHash.value);
        customer.confirmEmailAddress(command);

        // Then CustomerEmailAddressConfirmed
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerEmailAddressConfirmed.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerEmailAddressConfirmed event = (CustomerEmailAddressConfirmed) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));

        // When the same command is handled again, Then no event should be recorded
        customer.confirmEmailAddress(command);
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void changeCustomerName() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerName
        ChangeCustomerName command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        customer.changeName(command);

        // Then CustomerNameChanged
        List<Event> recordedEvents = customer.getRecordedEvents();
        assertEquals(1, recordedEvents.size());
        assertEquals(CustomerNameChanged.class, recordedEvents.get(0).getClass());
        assertNotNull(recordedEvents.get(0).getClass());

        //  and the payload should be as expected
        CustomerNameChanged event = (CustomerNameChanged) recordedEvents.get(0);
        assertTrue(event.customerID.equals(command.customerID));
        assertTrue(event.name.equals(command.name));

        // When the same command is handled again, Then no event should be recorded
        customer.changeName(command);
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void changeCustomerName_withUnchangedName() {
        // Given CustomerRegistered
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name)
                )
        );

        // When ChangeCustomerName
        ChangeCustomerName command = ChangeCustomerName.build(customerID.value, name.givenName, name.familyName);
        customer.changeName(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }

    @Test
    public void changeCustomerName_whenItWasAlreadyChanged() {
        // Given CustomerRegistered
        //   and CustomerNameChanged
        Customer1 customer = Customer1.reconstitute(
                List.of(
                        CustomerRegistered.build(customerID, emailAddress, confirmationHash, name),
                        CustomerNameChanged.build(customerID, changedName)
                )
        );

        // When ChangeCustomerName
        ChangeCustomerName command = ChangeCustomerName.build(customerID.value, changedName.givenName, changedName.familyName);
        customer.changeName(command);

        // Then no event
        assertEquals(0, customer.getRecordedEvents().size());
    }
}