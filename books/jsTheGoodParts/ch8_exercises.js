// Ch 8 Methods

// Lots of examples of the standard methods on the standard types

// Array

// array.concat(item...)
// Appends an item or array of items to the end of a shallow copy of the target array.
var a = ['a', 'b', 'c'];
var b = ['x', 'y', 'z'];
a.concat(b, true);


// array.join(separator)
// Creates a string from all of the array items, separated by the given separator.
var a = ['a', 'b', 'c'];
a.push('d');
a.join('');


// array.pop()
// Removes and returns the last element of the array, or undefined if it's empty.
var a = ['a', 'b', 'c'];
a.pop();
a


// array.push(item...)
// Appends items to the end of an array. Unlike concat, the original array is modified and
// array items are appended whole instead of splicing them in. Returns the new `length`.
var a = ['a', 'b', 'c'];
var b = ['x', 'y', 'z'];
a.push(b, true);
a
