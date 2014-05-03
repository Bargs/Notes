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

