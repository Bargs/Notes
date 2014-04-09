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


Invocation patterns and `this`
---------------------------------

When a function is invoked, it receives two special parameters:

1. `this`
2. `arguments`

The value of `this` depends on the *invocation pattern*. There are 4 of these patterns in JS.

> Methods that get their object context from `this` are called public methods.

### Method invocation pattern

A *method* is a function stored as a property on an object. When a function is invoked as a method (that is, accessing it via refinement on an object), `this` is bound to the object.

### Function invocation pattern

When a function is not invoked as a property of an object, `this` is bound to the global object. Crockford calls this a mistake in the language. A consequence is that a method cannot call a standalone helper function that uses `this`, because the inner function's `this` will point to the global object instead of the outer method's object. A workaround is to assign the value of `this` to a different var in the method, which the inner function could then access. A convention is to use the word `that` for the var.

### Constructor invocation pattern

The `new` prefix was added to JS to make it look more like classical languages, but it makes things a mess.

When a function is invoked with `new`, a new object is created and `this` is bound to that new object. The new object is also given a hidden link to the value of the function's `prototype` property (we took advantage of this in `ch3_exercises`).

Functions that are intended to be invoked as constructors (with `new`) are capitalized by convention. If a constructor is invoked without `new`, bad things can happen since `this` won't be bound to what you expect.

Use of `new` is not recommended, better alternatives are discussed in ch 5.

### Apply invocation pattern

Since functions are objects, they can have methods. All JS functions have an `apply` method. `apply` takes two args, a value to bind to `this` and an array of parameters. See example in `ch4_exercises.js`.


Arguments
------------------------

The special `arguments` parameter contains an array of all of the arguments that a function was called with, including any exess arguments. That allows you to create functions that take a variable number of args.

**Note**: `arguments` isn't really an array, but an array-like object. It has a length property but no array methods.


Return
-----------------------

Functions always return a value. The value can be specified with `return`, which will stop the execution of the function and return. If `return` isn't used, the function returns `unefined`, unless `new` was used to invoke the funciton, in which case `this` is returned as long as the `return` value isn't an object.


Exceptions
-----------------------

JS has pretty typical try/catch exceptions. Exceptions are just regular objects, that should have `name` and `message` properties (I think this is just convention). Exceptions thrown in a try block will be caught by the corresponding catch block.

Augmenting Types
---------------------

You can add new methods to JS types by editing their prototype properties.


Scope
---------------------

JavaScript does not have block scope. It has function scope.

> That means that the parameters and variables defined in a function are not visible outside of the function, and that a variable defined anywhere within a function is visible everywhere within the function.

As a result, all variables in a function should be declared at the top of the function body. Unless, of course, they're being declared in another nested function, in which case they should be declared at the top of the nested function's body.
