Chapter 7 Functional Programming
============================================

Functions are first class in Clojure. What makes something first class?

* It can be created on demand.
* It can be stored in a data structure.
* It can be passed as an argument to a function.
* It can be returned as the value of a function.

## Creating functions on demand

**Note**
> What does `apply` do? It calls the function passed as its first argument with the collection of arguments passed as its second argument. Essentially it allows you to pass a variable number of arguments if you don't know how many will be passed at compile time.

*Composition* is one way to create new functions on demand by smooshing together existing functions. Functions can be composed easily using the `comp` function which takes an arbitrary number of function arguments and returns a new function that applies each to the return value of the next function in line.

*Partial* functions can be created with the `partial` function. It takes a function and a list of arguments and returns a function that, when passed additional arguments, will apply the original function to the args passed to `partial` in addition to the new arguments.

```
((partial + 5) 100 200)
;=> 305
```

You can build the *complement* of a function using `complement`. This guy simply takes a function that returns a truthy value and builds a function that always returns the opposite truthy value for any given input.


## Functions as arguments

Being able to pass functions as arguments is much better than having to rely on callback objects like you do in Java.

`sort-by` is a good example of the use of function args. In addition to a list to sort it can take a function argument that will be used to pre-process each list item. The sort will occur on the result of the application of this function to each list item. This can help when dealing with nested lists or heterogeneous lists of non-mutually comparable types.

The ability to pass functions as arguments allows you to build your logic on top of existing parts. This let's you focus on your application *instead of worrying about re-implementing core functionality*.