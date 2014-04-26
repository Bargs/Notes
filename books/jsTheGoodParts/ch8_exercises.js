// Ch 8 Methods


// Some helpers for later on

// A helper function for creating objects with a given prototype
if (typeof Object.create !== 'function') {
  Object.create = function (o) {
    var F = function () {};
    F.prototype = o;
    return new F();
  };
}

// replaces html special chars with their html entities
if (!String.prototype.entityify) {
    String.prototype.entityify = function () {
        return this.replace(/&/g, "&amp;").replace(/</g,
            "&lt;").replace(/>/g, "&gt;");
    };
}

// Clears all html from the browser
var clearDoc = function () {
  document.body.innerHTML = "";
}


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


// array.reverse()
// Modifies the array by reversing the elements, returning the array.
var a = ['a', 'b', 'c'];
a.reverse();


// array.shift()
// Like pop but it removes the first element. Much slower than pop.
var a = ['a', 'b', 'c'];
a.shift();
a


// array.slice(start, end)
// Returns a copy of the array starting at array[start] and ending at array[end - 1].
// By default `end` is array.length if an `end` isn't provided.
var a = ['a', 'b', 'c'];
a.slice(0, 1);
a.slice(1);
a.slice(1, 2);


// array.sort(comparefn)
// Sorts an array in place. The default comparison function assumes the elements are strings, so
// it sorts numbers incorrectly. You can provide your own compareison function, which should take
// two parameters, returning 0 if they're equal, a negative number if the first is less, and a
// positive number if the second parameter is less.
var n = [4, 8, 15, 16, 23, 42];
n.sort();

n.sort(function (a, b) {
  return a - b;
});

// we can also create a function that sorts strings an numbers
var m = ['aa', 'bb', 'a', 4, 8, 15, 16, 23, 42];
m.sort(function (a, b) {
  if (a === b) {
    return 0;
  }
  if (typeof a === typeof b) {
    return a < b ? -1 : 1;
  }
  return typeof a < typeof b ? -1 : 1;
});


// array.splice(start, deleteCount, item...)
// removes `deleteCount` number of items from the array starting at `start`, replacing them with
// any `item`s passed in at the end of the args list. Unlike, `slice`, `splice` modifies the
// original array. `splice` returns the deleted elements.
var a = ['a', 'b', 'c'];
a.splice(1, 1, 'ache', 'bug');
a


// array.unshift(item...)
// Like `push` but `item`s are added to the front of the array. Returns the arrays new length.
var a = ['a', 'b', 'c'];
a.unshift('?', '@');
a



// function

// function.apply(thisarg, argArray)
// Invokes a function with and object to be bound to `this` and an optional array of args


// Number

// number.toExponential(fractionDigits)
// Converts a number to a string in exponential form. `fractionDigits` controls the number of
// decimal plcaes
var tenThousand = 10000;
tenThousand.toExponential(2);
Math.PI.toExponential(0);
Math.PI.toExponential(2);
Math.PI.toExponential(7);
Math.PI.toExponential();


// number.toFixed(fractionDigits)
// converts a number to a string in decimal form. `fractionDigits` is the number of decimals places
// default is 0, should be between 0 and 20

Math.PI.toFixed(0);
Math.PI.toFixed(2);
Math.PI.toFixed(7);
Math.PI.toFixed(16);
Math.PI.toFixed();


// number.toPrecision(precision)
// Converts number to a string in decimal form with `precision` digits of precision.
Math.PI.toPrecision(2);
Math.PI.toPrecision(7);
Math.PI.toPrecision(16);
Math.PI.toPrecision();


// number.toString(radix)
// Converts number to a string. `radix` controls the base of the number, between 2 and 36.
// Default is base 10.
Math.PI.toString(2);
Math.PI.toString(7);
Math.PI.toString(16);
Math.PI.toString();



// Object methods


// object.hasOwnProperty(name)
// Returns true if the object contains a property having the name. Prototype chain is not checked.
var a = {member: true};
var b = Object.create(a);
a.hasOwnProperty('member');
b.hasOwnProperty('member');
b.member



// RegExp methods

// regexp.exec(string)
// Most powerful and slowest of the regexp methods. Returns an array, with 0 being the
// matched text, 1 being capture group 1, 2 being group 2, etc. If the match fails null is returned.
// If the regexp has a `g` flag, searching begins at `regexp.lastIndex` instead of position 0.
// After a match `regexp.lastIndex` is set to the character after the match. An unsuccessful match
// sets `regexp.lastIndex` back to 0. This allows you to search for multiple matches by executing
// `regexp.exec(string)` in a loop.

var text = '<html><body bgcolor=linen><p>' + 'This is <b>bold<\/b>!<\/p><\/body><\/html>';
var tags = /[^<>]+|<(\/?)([A-Za-z]+)([^<>]*)>/g;
var a, i;

while ((a = tags.exec(text))) {
  for (i = 0; i < a.length; i += 1) {
    document.writeln(('// [' + i + '] ' + a[i]).entityify());
    document.writeln('<br \/>');
  }
  document.writeln('<br \/><br \/>');
}


// regexp.test(string)
// The simplest and fastest of the regexp methods. If there's a match, it returns true, otherwise
// false. Don't use the `g` flag.

var b = /&.+;/.test('frank &amp; beans');
b


