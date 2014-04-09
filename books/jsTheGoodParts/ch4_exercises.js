var add = function (a, b) {
  return a + b;
};

// The Method invocation pattern

var myObject = {
  value: 0,
  increment: function (inc) {
    this.value += typeof inc === 'number' ? inc : 1;
  }
};

myObject.increment();
myObject.value;

myObject.increment(2);
myObject.value;


// The Function invocation pattern

// Using an inner helper method with the `that` convention
myObject.double = function () {
  var that = this;

  var helper = function () {
    that.value = add(that.value, that.value);
  };

  helper();
};

myObject.double();
myObject.value;


// The Constructor invocation pattern

// This is a constructor function (capitalized by convention)
var Quo = function (string) {
  this.status = string;
}

// Giving all Quos a public method by editing Quo's prototype property.
// All objects created by invoking Quo with `new` will get a hidden link
// to Quo.prototype. This becomes the prototype chain for the new object,
// where properties will be searched for if they're not found directly on the object.
Quo.prototype.get_status = function () {
  return this.status;
};

// By using new, a new object is created and Quo is
// invoked with `this` bound to the new object. The new object
// is given a hidden link to Quo's prototype property, which started out as
// an object with a `constructor` property whose value is the function Quo. We
// edited that Quo.prototype object above, adding the get_status method.
var myQuo = new Quo("confused");

myQuo.get_status();


// The Apply invocation pattern

// Using `apply` to pass params as an array.
var params = [3, 4];
add.apply(null, params);

var statusObject = {
  status: 'A-OK'
};

// Using `apply` to bind `this` to a given object.
// This pattern allows us to invoke an existing method on an object
// that doesn't directly contain or inherit the method.
// That reminds me a bit of Clojure's ability to extend protocols to existing classes.
Quo.prototype.get_status.apply(statusObject);


// Accepting a variable number of args by using `arguments`
var sum = function () {
  var i, sum = 0;

  for (i = 0; i < arguments.length; i += 1) {
    sum += arguments[i];
  }

  return sum;
}

sum(4, 8, 15, 16, 23, 42);


// JS Exceptions

var add = function (a, b) {
  if (typeof a !== 'number' || typeof b !== 'number') {
    throw {
      name: 'TypeError',
      message: 'add needs numbers'
    };
  }
  return a + b;
}

var try_it = function () {
  try {
    add("seven");
  } catch (e) {
    document.writeln(e.name + ': ' + e.message);
  }
}

try_it();


// Scope

var foo = function () {

  var a = 3, b = 5;

  var bar = function () {

    var b = 7, c = 11;

    // a is 3, b is 7, c is 11

    a += b + c;

    // a is 21, b is 7, c is 11

  };

  // a is 3, b is 5, c is not defined

  bar();

  // a is 21, b is 5

}
