Chapter 7 Functional Programming
============================================

Functions are first class in Clojure. What makes something first class?

* It can be created on demand.
* It can be stored in a data structure.
* It can be passed as an argument to a function.
* It can be returned as the value of a function.

**Note**
> What does `apply` do? It calls the function passed as its first argument with the collection of arguments passed as its second argument. Essentially it allows you to pass a variable number of arguments if you don't know how many will be passed at compile time.

*Composition* is one way to create new functions on demand by smooshing together existing functions. Functions can be composed easily using the `comp` function which takes an arbitrary number of function arguments and returns a new function that applies each to the return value of the next function in line.