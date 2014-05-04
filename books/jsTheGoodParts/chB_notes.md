Appendix B. Bad Parts
======================

Bad parts are awful parts that can be avoided.

==
---------------

JS has two sets of equality operators:

`===` and `!==`

Two operands are equal only if they're the same type and value.

`==` and `!=`

If the operands are different types, JS attempts to coerce the values using a complicated set of rules. These rules can lead to unexpected results:

'' == 0 // false
0 == '' // true
0 == '0' // true

false == 'false' // false
false == '0'     // true

false == undefined // false
false == null // false
null == undefined // true

Always use `===` and `!==` to avoid unexpected results.


with
----------------

The `with` statement is supposed to be a shorthand for accessing the properties of an object. However, it's results can be unpredictable because you can't tell by looking at the code whether the vars inside it will be resolved as properties of the object, or as vars that are simply in scope.


eval
-------------

eval is often misused to accomplish things the language already provides a safer means of accomlishing. It's slow and unsafe to eval arbitrary strings.

switch fall through
-----------------------

Unintentional fall throughs are a common source of errors. If you make the decision to never intentionally use fall throughs, the unintentional ones become more much easy to spot.

> The worst features of a language aren't the features that are obviously dangerous or useless. Those are easily avoided. The worst features are the atractive nuisances, the features that are both useful and dangerous.


Bitwise operators
---------------------

They're really slow in Javascript, so they're hardly ever used. Usually a single `&` or `|` is just a mistyped `&&` or `||`.


Typed Wrappers
----------------------

Avoid `new Boolean` and `new String` and `new Number`, they're unnecessary and confusing. Also avoid `new Object` and `new Array`, use `{}` and `[]` instead.

new
------------

`new` is dangerous because a constructor function that is accidentally called without `new` will have `this` assigned to the global object, and will start whacking global variables. It's best to avoid `new` altogether.

void
------------

In JS `void` is an operator that takes an operand and returns `undefined`. This is unnecessary and confusing for people coming from traditional languages.
