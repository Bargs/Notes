Chapter 6 Immutability and Laziness
===================================

Clojure supports immutability directly as a language feature instead of relying on convention. It has mutable references but immutable data structures.

What's so great about immutability?

* Invariants are easier to enforce when you only have to worry about them at construction time.
* It's easier to reason about objects with a smaller state space.
* Equality is more meaningful when you know it won't change on you.
* Immutable objects can be shared freely (even across threads).

**Note**
> Keywords can be used in the function call position and passed a map as an argument. The keyword will look itself up in the map and return the value.

### Structural Sharing

Copying is the major downside to immutability. When objects can't be modified, copies must be created when updates are needed. This can have a negative impact on memory usage and performance. Clojure makes use of *structural sharing* to combat this problem. When a change is made to a data structure, Clojure will only copy the pieces that were changed. When adding an item to a list for instance, the new list's `next` portion points to the original list without copying it. This is a nice performance enhancement that can only be used because of the immutable nature of Clojure's data types.


