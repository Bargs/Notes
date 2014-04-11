var clearDoc = function () {
  document.body.innerHTML = "";
}

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


// Modules

// Creating a "deentityify" module for String objects.
// Our method needs a map of html entities to the equivalent strings.
// We could store it in a global var, but that would make our code brittle.
// We could define the map as a literal in the function itself, but then the map
// would get recreated on every method invocation. Instead, we'll only create the map
// once and hide it away where only the method can use it, by using closure.
// Modules follow this general pattern; a function defines some private vars and
// functions, creates some privileged functions that will have access to the privates
// via closure, and returns the privileged functions or stores them an an accessible
// place.
String.prototype.deentityify = (function () {

  var entity = {
    quot: '"',
    lt: '<',
    gt: '>'
  };

  return function () {
    return this.replace(/&([^&;]+);/g,
                         function (a, b) {
                           var r = entity[b];
                           return typeof r === 'string' ? r : a;
                         }
                       );
  };
}());

'&lt;&quot;&gt;'.deentityify();


// Modules are useful for information hiding and encapsulation. They can also be
// used to create secure objects.

// The following function creates objects that can be used to generate serial numbers
// without risk of a third party being able to tamper with the generator's internals.
// Because the seqer functions don't make use of `this`, the `prefix` and `seq`
// aren't stored as properties of the object. Instead they are vars that only the
// original functions created inside of `serial_maker` can access. Even if a third-party
// changed the functions on `seqer`, it still wouldn't grant them access to the private
// vars.
var serial_maker = function () {
  var prefix = '';
  var seq = 0;

  return {
    set_prefix: function (p) {
      prefix = String(p);
    },
    set_seq: function (s) {
      seq = s;
    },
    gensym: function () {
      var result = prefix + seq;
      seq += 1;
      return result;
    }
  };
};

var seqer = serial_maker();
seqer.set_prefix('Q');
seqer.set_seq(1000);
seqer.gensym();
seqer.gensym();


// Currying

// It's easy to implement currying in JS

// We have to use `slice` to turn the "array-like" `arguments` parameter into an
// actual array with will work correctly with the `concat` method.
Function.prototype.curry = function () {
  var slice = Array.prototype.slice,
      args = slice.apply(arguments),
      that = this;

  return function () {
    return that.apply(null, args.concat(slice.apply(arguments)));
  };
};

var add1 = add.curry(1);
add1(6);


// Memoization

// A fibonacci implementation
var fib = (function () {
  var count = 0;

  return {
    fibonacci: function (n) {
      count++;
      return n < 2 ? n : this.fibonacci(n - 1) + this.fibonacci(n - 2);
    },
    getCount: function () {
      return count;
    }
  }
}());

for (var i = 0; i <= 10; i += 1) {
  document.writeln('// ' + i + ': ' + fib.fibonacci(i));
}

fib.getCount();


// That works fine, but the function gets called over and over with the same parameters,
// calculating the answer from scrach each time. It gets called 453 times to be exact.
// We can make fibonacci much more efficient if we memoize it.

var fib2 = (function () {
  var count = 0, memo = [0, 1];

  return {
    fibonacci: function (n) {
      count++;
      var result = memo[n];

      if (typeof result !== 'number') {
        result = this.fibonacci(n - 1) + this.fibonacci(n - 2);
        memo[n] = result;
      }

      return result;
    },
    getCount: function () {
      return count;
    }
  }
}());

for (var i = 0; i <= 10; i += 1) {
  document.writeln('// ' + i + ': ' + fib2.fibonacci(i));
}

// Now the fibonacci function only gets called 29 times.
fib2.getCount();


// We can generalize this to easily gain the benefits of
// memoization with other functions.
var memoizer = function (memo, formula) {
  var recur = function (n) {
    var result = memo[n];
    if (typeof result !== 'number') {
      result = formula(recur, n);
      memo[n] = result;
    }
    return result;
  };
  return recur;
};

// An implementation of fibonacci using our new memoizer
var fibonacci = memoizer([0, 1], function (recur, n) {
  return recur(n - 1) + recur(n - 2);
});

for (var i = 0; i <= 10; i += 1) {
  document.writeln('// ' + i + ': ' + fibonacci(i));
}

// Now we can use memoizer to implment other recursive functions
var factorial = memoizer([1, 1], function (recur, n) {
  return n * recur(n - 1);
});

for (var i = 0; i <= 10; i += 1) {
  document.writeln('// ' + i + ': ' + factorial(i));
}
