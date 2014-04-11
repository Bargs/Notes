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


// Closures
// The following function returns and object with two functions that are closures
// They have access to the value var defined in the constructor function, but it's hidden
// from everyone else. This is a way to create private members in JS.
var myObject = (function () {
  var value = 0;

  return {
    increment: function (inc) {
      value += typeof inc === 'number' ? inc : 1;
    },
    getValue: function () {
      return value;
    }
  };
}());


// Creating a better quo, with a private status data member
var quo = function (status) {
  return {
    get_status: function () {
    return status;
    }
  };
};

quo("amazed").get_status();


// A more useful example using closures
// Sets a DOM node to yellow then fades to white
var fade = function (node) {
  var level = 1;
  var step = function () {
    var hex = level.toString(16);
    node.style.backgroundColor = '#FFFF' + hex + hex;
    if (level < 15) {
      level += 1;
      setTimeout(step, 100);
    }
  };
  setTimeout(step, 100);
};

fade(document.body);


// Note that closures have access to the original parameters from the environment they
// were created in, NOT copies. If you expect a closure to contain a copy of the parameter
// from the time the closure was created, you'll run into erros like the following:

// The following code is supposed to create event handlers for onClick for an array of nodes.
// Each event handler should pop up an alert box with the node's index in the array. Instead
// each alert box will always display the array's length, because the code mistakenly assumes
// that each closure gets a copy of the var i, instead of the original which is getting updated
// with each iteration of the for loop.

var add_the_handlers = function (nodes) {
  var i;
  for (i = 0; i < nodes.length; i += 1) {
    nodes[i].onclick = function (e) {
      alert(i);
    };
  }
};


// This is a better example, that correctly implements the desired functionality.
// It works because passing `i` to `helper` binds the value of the outer `i` local var
// to helper's `i` param, which is a new, different var inside helper. The function
// returned by `helper` closes over the `i` param, instead of the original `i` var.
var add_the_handlers = function (nodes) {
  var helper = function (i) {
    return function (e) {
      alert(i);
    };
  };

  var i;
  for (i = 0; i < nodes.length; i += 1) {
    modes[i].onclick = helper(i);
  }
};
