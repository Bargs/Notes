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
