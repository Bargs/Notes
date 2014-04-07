Ch 4 Functions
==================

Functions is JS are objects. While objects created from object literals have a hidden link to `Object.prototype`, function objects are linked to `Function.prototype` (which is linked to `Object.prototype`).

Every function has two additional hidden properties:

1. The function's context
2. The code that implements the function's behavior

Every function also has a `prototype` property, which is different from the hidden link to a prototype object. The `prototype` property contains an object with a `constructor` property whose value is the function. The reason for this structure is discussed in chapter 5.

JS has first class functions, they can be passed as args, assigned to vars, returned from functions, and created dynamically. Because functions are objects, they can also have methods.

What really sets functions apart from regular objects is that they can be invoked.

Function literals look something like this:

```
var add = function (a, b) {
  return a + b;
};
```

A function can optionally be given a name, after the `function` keyword in the literal. If no name is given, it is said to be *anonymous*.

A function literal can appear anywhere an expression can appear. When it appears inside another function, it has access to the outer function's parameters and vars. The inner function object maintains a link to the outer context even if it's returned from the outer function, which creates a closure.

