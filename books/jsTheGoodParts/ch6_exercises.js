// Arrays

var empty = [];
var numbers = ['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven',
               'eight', 'nine'];

empty[1]
numbers[1]

empty.length
numbers.length

// Arrays are basically just special objects, their subscripts get converted
// into strings that are used as property names. This object literal
// produces a similar result to the array above.

var numbers_object = {
  '0': 'zero',
  '1': 'one',
  '2': 'two',
  '3': 'three',
  '4': 'four',
  '5': 'five',
  '6': 'six',
  '7': 'seven',
  '8': 'eight',
  '9': 'nine'
};

numbers_object[1]

// both `numbers` and `numbers_object` are objects containing 10 properties,
// each with the exact same name and value. However, there are a couple of
// differences.
//
// 1. `numbers` inherits from Array.prototype instead of Object.prototype,
//    so it has access to a larger number of useful methods.
// 2. `numbers` gets a `length` property and `numbers_object` does not.

// Note that JS arrays can be heterogeneous.

var misc = ['string', 98.6, true, false, null, undefined,
            ['nested', 'array'], {object: true}, NaN, Infinity];

misc.length


// The `length` property is the largest integer property name plus one.
// This IS NOT necessarily the number of properties in the array.
var myArray = [];
myArray.length

myArray[1000000] = true;
myArray.length

// The [] operator converts its express to a string using the expression's
// `toString` method if it has one. That string is used as a property name.
// Assigning a value to an index greather than or equal to length, and less than
// 4,294,967,295, will set `length` to the new subscript plus one.

// `length` can be set explicitly. Making it larger does not allocate more space,
// but making it smaller will cause all properties at >= `length` to be deleted.
numbers.length = 3;
numbers

// Using the length property is a convenient way to append values to the end
// of an array.
numbers[numbers.length] = 'shi';
numbers[numbers.length - 1]

// But you can also use Array.prototype's `push` method:
numbers.push('go');
numbers
