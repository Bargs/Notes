Appendix C JSLint
======================

JSLint defines a subset of JS that helps you avoid the bad parts.

What are some things it looks for?

Undefined variables and functions. JSLint assumes all vars are declared before being used, which allows it to find accidentally implied globals.

Reports on the names of members to help the programmer find misspellings.

Allows a option object or option specification in source to control which JSLint rules you want to use.

Looks for missing semicolons by assuming that almost every statement should end with one.

Requires blocks for `if` statements and loops.

`for in` statements that don't use `hasOwnProperty` to filter out properties from the prototype chain.

Forbids switch statement fallthrough.

Forbids use of `with` statement.

Forbids use of `==` and `!=`

Looks for unreachable code.

Forbids use of ++ and --

Forbids bitwise operators

Forbids `eval`.

Forbids the `void` operator.

Looks for qualities in regexps that may cause portability problems.

Flags uncapitalized functions used with `new` and capitalized functions used without `new`, and forbids `new` usage with Number, String, Boolean, Object, and Array.

Can parse HTML docs with <script> tags or inline event handlers to look for issues in the HTML that might five the JS compiler problems.

Ensures JSON structures are well formed.

