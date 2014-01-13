Chapter 6 Immutability and Laziness
===================================

> * Needs more detail on how `lazy-seq` works
> * Needs more detail on the distinction between immutability and persistence

Immutability
-----------------------------------

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


Laziness
-----------------------------------

Clojure is partially lazy. This is another way Clojure combats memory utilization, but it's also handy for lots of other reasons. Laziness can especially save your butt when you're working with large collections.

It can be useful to make your own functions lazy. If you want to do that, you should follow this recipe:

1. Use the `lazy-seq` macro at the outermost level of your lazy sequence producing expression(s). `lazy-seq` returns a seq that realizes its values only as needed. Need to look into this more... book doesn't give a lot of detail on how this works.

2. If you happen to be consuming another sequence during your operations, then use `rest` instead of `next`. `next` will eagerly "realize" the first element of its result to see if the seq is empty. `rest` doesn't need to do this check.

3. Prefer higher-order functions when processing sequences.

4. Don’t hold onto your head. Clojure can GC portions of your seq that are no longer reachable. This can help you avoid out of memory errors when working with especially large data sets. However, if you hold onto the head of your seq (usually bound to a local) Clojure can't GC any part of the seq. If you perform a calculation that needs a value at the end of the seq, you'll end up holding the entire data structure in memory. This defeats the main purpose of laziness, which is "to prevent the full realization of iterim results during a calculation".


Lazy sequences allow you to work with infinite sequences. These guys are nice because they allow you define a sequence once, and reuse it in all sorts of scenarios. Unlike a non-lazy sequence that necessairly has a max value, you'll never find yourself needing more values and having to recalculate the sequence. Infinite sequences can give you any value you need.


Clojure sequences are lazy but Clojure in general is not. However, you can explicitly delay the evaluation of an expression using the `delay` macro and later invoke the evaluation with the `force` function. You could achieve mostly the same result by passing a thunk, but `delay` also caches the result of its expression and enables the use of `delay?` to check whether the evaluation of an express has been explicitly delayed.

See `ch6_exercise.clj` for an example of a lazy quick sort. The implementation demostrates the power of laziness when it's fully embraced. This quick sort will only do as much work as needed to obtain the requested result. When you only need the beginning of a very large unsorted collection, this can save a lot on time and memory.
