Chapter 6 Immutability and Laziness
===================================

Clojure supports immutability directly as a language feature instead of relying on convention. It has mutable references but immutable data structures.

What's so great about immutability?

* Invariants are easier to enforce when you only have to worry about them at construction time.
* It's easier to reason about objects with a smaller state space.
* Equality is more meaningful when you know it won't change on you.
* Immutable objects can be shared freely (even across threads).
