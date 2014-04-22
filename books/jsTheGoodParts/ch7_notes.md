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


Elements
-------------

A *regexp choice* contains one or more *regexp sequences* separated by `|`. A choices matches if any of the sequences match. When more than one sequence is present, they're attempted in the given order.

A *regexp sequence* is made up of *regexp factors*, each of which are followed by optional quantifiers. If no quantifier is given, the factor will be matched once.

A *regexp factor* is a character, a parenthesized group, a character class, or an escape sequence.

There are 4 kinds of groups:

1. Capturing - a regex choice wrapped in parentheses. "Captures" the matching text which is given a number in the result corresponding to the group's position.
2. Noncapturing - Has a `?:` prefix. Slightly faster than capturing, doesn't provide the matched text in the result.
3. Positive lookahead - Has a `?=` prefix. Once matched, text is rewound to where the group started. "This is not a good part".
4. Negative lookahead - Has a `?!` prefix. Like postivie lookahead, but it only matches if it fails to match. "This is not a good part".

A *regexp class* specifies one of a set of characters. Ranges of characters can be specified using `-`. You can match the *opposite* of a character class by starting the class with `^`. Note, the rules of escapement within a character class are slightly different than those for a regexp factor.

A *regexp quantifier* specifies how many times a *regexp factor* should match. Numbers in curly braces can specify a count or range (`{3}` means match 3 times, `{3,6}` means match 3, 4, 5, or 6 times, `{3,}` matches 3 or more times).

`?` is the same as `{0,1}`. `*` is the same as `{0,}`. `+` is the same as `{1,}`.

Matching is usually greedy, matching as many repetitions as possible up to the limit. If the quantifier has an extra `?` suffix, matching tends to be lazy, matching as few repetitions as possible.

