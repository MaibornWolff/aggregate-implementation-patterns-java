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
**To Do**  
Currently only shown on the Miro board we use for the introduction.

## General Instructions
For each model, you can find a class *CustomerX* containing production code as well as a corresponding test class
*CustomerXTest*. Most of the time, only production code snippets are missing but sometimes also tests wait for your
implementation.

Enable the disabled test cases (remove the @Disabled annotation) in *CustomerXTest* one by one and make them all green!
The first test case (RegisterCustomer) is already enabled for you to start.

## Our variants

### Customer1 (OOP & Event-Sourced)
* records events that have happened
* the client has to request those events

*Bonus challenge:*  
What needs to be changed so that the Aggregate keeps its own state up-to-date, e.g. to be able to handle multiple
Commands within one request from the outside?

*Hint:*   
To test this, you can extend the test cases so that they handle the same command again, resulting in "no changes".


### Customer2 (OOP & Event-Sourced)
* directly returns the events that have happened

*Bonus challenge:*  
What needs to be changed so that the Aggregate keeps its own state up-to-date, e.g. to be able to handle multiple
Commands within one request from the outside?

*Hint:*  
To test this, you can extend the test cases so that they handle the same command again, resulting in "no changes".


### Customer3 (OOP & Traditional)
* state and behavior is the same object
* directly modifies the state

*Challenge:*  
How can we test this?  
Most behavior methods don't return anything.  
Implement the missing assertions in the tests first.


### Customer4 (Functional & Traditional)
* state and behavior are different objects
* directly modifies the state

*Challenge:*  
How can we test this?  
Implement the missing assertions in the tests first!


### Customer5 (Functional & Event-Sourced)
* uses an external state object (*CustomerState*)
* this is reconstituted outside and given as input to the behavior functions


### Customer6 (Functional & Event-Sourced)
* uses an external state object (*CustomerState*)
* this is reconstituted inside of the behavior functions from the events that are given as input


### Customer7 (Functional & Event-Sourced)
* internal state per function (variables)
* those variables get reconstituted inside of the behavior functions from the events that are given as input
