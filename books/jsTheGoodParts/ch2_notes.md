# Grammar

## Whitespace

Whitespace is formatting characters or comments.

JS comments take two forms, blocks `/* */' and line-ending comments `//`

Avoid using block comments, because the `/* */` can also be found in regex literals, which can cause a syntax error if nested in a block comment.

## Names

A letter followed by 0 or more letters, digits, or underbars. Must avoid reserved words.

## Numbers

JS only has one type of number, a 64-bit floating point. 1 and 1.0 are the same value.

## String

Can use double or single quotes. Backslash can escape characters.

Strings are immutable. Equality is based on the string value, not object equality. So no String.equals() nonsense like in Java.


## Statements

### Var statement

vars created outside of functions are thrown into the global namespace. Inside a function the var statement defines a private variable local to the function.

### Other Statements

expression statement, disruptive statement, try, if, switch, while, for, do

A block is a set of statements between curly braces. New blocks don't create new scope, so define variables at the top of functions.

Falsey values (all other values are true):

* false
* null
* undefined
* empty string
* the number 0
* NaN

Switch statements can only use numbers or strings. `break` statements should be used to prevent fallthrough from one case clause to the next.


For loops come in two flavors, the standard `for (init; condition; increment) {}` and `for (var in object) {}`.

For var in object enumerates the property names of an object, not the values of an array. This will include properties in the prototype chain, so you'll usually want to use `object.hasOwnProperty(variable)` to make sure you're only looping through properties of the object in question.


An expression statement can either assign values to one or more variables or members, invoke a method, delete a property from an object. The = operator is used for assignment.


## Expressions

The `typeof` operator produces the values 'number', 'string', 'boolean', 'undefined', 'function', and 'object'. null and arrays return 'object'

Using `.` or `[]` to specify a property or element of an object or array is called *refinement*.

Object property names in object literals must be known at compile time because they are treated as literal names, not variable names.

Function literals can optionally include a name that the function can use to call itself recursively.
