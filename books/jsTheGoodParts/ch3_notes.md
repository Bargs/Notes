Ch 3 Objects
==================

An object is a container of properties.

Properties can be retrieved with `[]` or `.`

`[]` can always be used, passing it a string expression. If the string is a literal and it's a legal javascript name, then the `.` notation can be used.

Retrieval of non-existent properties returns `undefined`.

Because `||` returns its second operand if the first is falsey, and undefined is falsey, it can be used to provide default values in a var statement:

```
var x = foo["does-not-exist"] || "default value";
```

Objects are always passed by reference. They are never copied.

## Prototype

All objects are linked to a prototype object, from which properties are inherited. Object literals are linked to `Object.prototype`, other objects can be created with your choice of object linked as a prototype. If an object doesn't contain a property that is being requested, its prototype is checked. If the prototype doesn't have it, *its* prototype is checked. This continues on, up the chain, until Object.prototype is reached. This is called *delegation*. If the prop still hasn't been found, `undefined` is returned. Examples in `ch3_exercises.js`.

## Reflection

The properties of an object can be inspected simply by refinement. However, this will include properties on the prototype chain. To filter out those properties, you can use the `hasOwnProperty` method.

## Global Abatement

vars are global by default in JS, which makes your programs error prone. One way to make this a little better is to limit your app to a single global var assigned to an object. That object becomes the container for your app's vars. This reduces the chance that your app will conflict with third party libs in the global namespace.
