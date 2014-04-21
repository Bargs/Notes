Ch 7 Regular Expressions
============================

> Regular expressions usually have a significant performance advantage over equivalent string operations in JavaScript.

> The part of the JS language that is least portable is the implementation of regular expressions. Regular expressions that are very complicated or convoluted are more likely to have portability problems.

It's usually better to use non-capturing groups `(?:...)` instead of capturing groups `(...)` because capturing has a performance penalty.

There are two ways to create a regular expression:

1. A regular expression literal
2. The RegExp constructor

Literals are enclosed with slashes `/.../` followed by three optional flags.

* `g` = global (match multiple times; meaning varies with the method)
* `i` = case insensitive
* `m` = multiline (^ and $ can match line-ending characters)

The constructor takes a string and compiles it into a RegExp object. This is more complicated because backslash is used differently in string literals than it is in Regex literals. It's only really useful if you need to generate a regex at runtime using material that is not available at compile time.
