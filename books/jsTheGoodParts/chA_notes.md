Appendix A. Awful Parts
===========================

Global Variables
-------------------------

Javascript requires the use of global variables because it does not have a linker. There are three ways to create one:

1. Declare a var outside of any function.
2. Add a property directly to the global object (e.g. `window.foo = value`)
3. Use a variable without declaring it. This is an *implied global*


Scope
------------------------

Javascript has function scope not block scope, even though it uses block syntax. A variable declared in a block is visible everywhere in the function containing the block, unlike in other languages where the block would create a new scope that's inaccessible outside the block.


Semicolon Insertion
--------------------------

Javascript tries to correct faulty programs by automatically inserting semicolons. You shouldn't rely on this because it can mask more serious errors.


typeof
--------------------------

Doesn't always return what you'd expect.

typeof null === 'object'

typeof /a/ might return 'object' or 'function'

typeof [] === 'object' instead of 'array'


parseInt
--------------------------

Converts a string into an integer. Stops parsing at the first nondigit without error. Reads numbers as base 8 if they start with 0.

+
----------------------

Can add or concatenate, depending on parameters. If you want it to add, you need to make sure both params are numbers.


Floating Point
-----------------------

Javascript numbers are Binary floating-point numbers and they're inept at handling decimal fractions. The only way to avoid inaccurate results is to scale the decimals and perform your math on them as integers before turning them back into decimals.


NaN
----------------------

typeof NaN === 'number'

It's really hard to test for NaN. NaN === NaN is false.

There's an isNaN function, but it returns true for expressions that are not a number instead of the NaN object itself. (e.g. isNaN('oops') === true)


Phony Arrays
---------------------

Javascript doesn't have real arrays. They're convenient, but sometimes they're very slow.


Falsy Values
---------------------

Javascript has many falsey values: 0, NaN, '', false, null, undefined.


Object
---------------------

Objects are never truly empty because they pick up members from the prototype chain. This matters if you write code that expects an empty object that accidentally has a property you're using in the prototype chain. You can only avoid this by using `hasOwnProperty`. or testing for specific types before interacting with those properties.
