Ch 5 Inheritance
===========================

Javascript's classless nature enables many different patterns of inheritance. Chapter 5 is a look at some of the more common patterns.


Pseudoclassical
---------------------------

In an attempt to ease the anxiety of classical programmers, JS hides it prototypal nature behind a classical veneer. Instead of allowing objects to directly inherit from other objects, there's a level of indirection involving constructor functions that must be used to create new objects.

When a new function object is created, the `Function` constructor gives it a `prototype` property that looks like this:

```
this.prototype = {constructor: this};
```

Where `this` refers to the new function object.

This `prototype` object is where inherited properties need to be placed. Every function gets a `prototype` property, but it's only important for functions that are to be used as constructors.


Object Specifiers
--------------------------

JS doesn't have named parameters, but you can fake it by passing a single object arg with the real paramters as properties. This makes long argument lists more readable, less error prone, and makes optional arguments a possibility. This pattern can also be useful when combined with JSON, because an object represented in JSON could be passed as an object specifier to a function to create a full fledged object with data and methods.


Prototypal
---------------------

Classes do not exist. A new object can inherit the properties of an old object.


Functional
--------------------

The inhertance patterns so far have no sense of privacy. The functional pattern fixes this by applying the module pattern.

We'll create a function that produces objects, without the use of `new`, in 4 steps.

1. It creats an object.
2. It optionally defines private variables and methods.
3. It augments the new object with methods. These will be public methods with privileged access to the private memebers defined in #2.
4. It returns the new object.

If all of the state of an object is private, then the object is tamper-proof. If all of the methods of the object make no use of `this` or `that`, then the object is *durable*. A durable object is a collection of functions that act as *capabilities*. A durable object can't be compromised because its internal state cannot be accessed in any way other than through its methods.


