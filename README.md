# Aggregate Implementation Patterns
There are a lot of different possibilities when implementing an aggregate. Many of them are not generally better than
others, hence it's crucial to take the use case into account when deciding for one model. We want to have a look at a few
different patterns together and discuss their pros and cons. In the end, you should have more options to choose from for
your next project. 

## Implementation Characteristics
While this is certainly a very simplified approach, we'll focus on the following two dimensions:
* event-sourced vs. traditional (i.e. full state gets persisted)
* functional vs. object-oriented

This leads to the following comprehensive representation in four quadrants.

|         | ES         | TRAD         |
| ------- | :--------- | -----------: |
| **OOP** | *OOP & ES* | *OOP & TRAD* |
| **FP**  | *FP & ES*  | *FP & TRAD*  |

## Our Aggregate Example
In this workshop, we want to focus on a Customer aggregate supporting three simple use cases. The details that you can
find below could have been collected in an event storming workshop beforehand, for example.

### Customer Registration
Command: **RegisterCustomer** with properties
* customerID
* emailAddress
* confirmationHash
* personName

Event: **CustomerRegistered** with properties
* customerID
* emailAddress
* confirmationHash
* personName

Rules & Policies:
* new email addresses are unconfirmed
* new email addresses must get a new hash

### Email Confirmation
Command: **ConfirmCustomerEmailAddress** with properties
* customerID
* confirmationHash

Event: **CustomerEmailAddressConfirmed** or **CustomerEmailAddressConfirmationFailed**, both with property
* customerID

Rules & Policies:
* email addresses can only be confirmed with matching hash

### Email Change 
Command: **ChangeCustomerEmailAddress** with properties
* customerID
* emailAddress
* confirmationHash

Event: **CustomerEmailAddressChanged** with properties
* customerID
* emailAddress
* confirmationHash

Rules & Policies:
* new email addresses are unconfirmed
* new email addresses must get a new hash

## General Instructions
For each model, you can find a class *CustomerX* containing production code as well as a corresponding test class *CustomerXTest*.  
Most of the time, only production code snippets are missing but sometimes also tests wait for your
implementation.

Enable the disabled test cases (remove the @Disabled annotation) in *CustomerXTest* one by one and make them all green!  
The first test case (RegisterCustomer) is already enabled for you to start.

## Exercises - Implement the following variants

### OOP & Event-Sourced

#### Customer1
* records the events that have happened
* the client has to request those events

#### Customer2
* directly returns the events that have happened

### OOP & Traditional

#### Customer3
* state and behavior is the same object
* directly modifies the state

*Challenge:*  
How can we test this?  
Most behavior methods don't return anything.  
Implement the missing assertions in the tests first.

### Functional & Traditional

#### Customer4
* state and behavior are different objects
* directly modifies the state

*Challenge:*  
How can we test this?  
Implement the missing assertions in the tests first!

### Functional & Event-Sourced

#### Customer5
* uses a state object (*CustomerState*)
* this is reconstituted outside and given as input to the behavior functions

#### Customer6
* uses a state object (*CustomerState*)
* this is reconstituted inside of the behavior functions from the events that are given as input

#### Customer7
* internal state per function (variables)
* those variables get reconstituted inside of the behavior functions from the events that are given as input
