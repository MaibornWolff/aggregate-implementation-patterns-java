# Aggregate Implementation Patterns
There are a lot of different possibilities when implementing an aggregate. Many of them are not generally better than
others, hence it's crucial to take the use case into account when deciding for one model. We want to have a look at a few
different patterns together and discuss their pros and cons. In the end, you should have more options to choose from for
your next project. 

## Implementation Characteristics
While this is certainly a very simplified approach, we'll focus on the following two dimensions:
* event-sourced vs. traditional
* functional vs. object-oriented

This leads to the following comprehensive representation in four quadrants. In the hands-on part, we'll follow the 
implied order A -> B -> C -> D.

|         | ES                 | TRAD                 |
| ------- | :----------------- | -------------------: |
| **OOP** | A <br/> *OOP & ES* | B <br/> *OOP & TRAD* |
| **FP**  | *FP & ES* <br/> D  | *FP & TRAD* <br/> C  |

## Our Aggregate Example
**To Do**

## General Instructions
For each model, you can find a class *CustomerX* containing production code as well as a corresponding test class
*CustomerXTest*. Most of the time, only production code snippets are missing but sometimes also tests wait for your
implementation.

Enable the disabled test cases (remove the @Disabled annotation) in *CustomerXTest* one by one and make them all green!
The first test case (RegisterCustomer) is already enabled for you to start.

## Our Models

### Customer1
This version of a Customer Aggregate is OOP-style, event-sourced, and records events that have happened, the client has to request those recorded events.

*Bonus challenge:*  
What needs to be changed so that the Aggregate keeps its own state up-to-date, e.g. to be able to handle multiple
Commands within one request from the outside?

*Hint:*   
To test this, you can extend the test cases so that they handle the same command again, resulting in "no changes".

### Customer2
This version of a Customer Aggregate is OOP-style, event-sourced, and directly returns the events that have happened.

*Bonus challenge:*  
What needs to be changed so that the Aggregate keeps its own state up-to-date, e.g. to be able to handle multiple
Commands within one request from the outside?
*Hint:*  
To test this, you can extend the test cases so that they handle the same command again, resulting in "no changes".

### Customer3
**To Do**

### Customer4
**To Do**

### Customer5
**To Do**

### Customer6
**To Do**

### Customer7
**To Do**
