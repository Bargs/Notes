// Pseudoclassical

// Creating a constructor function

var Mammal = function (name) {
  this.name = name;
};

// Modify the constructor function's prototype property to give all instances
// of Mammal a `get_name` and `says` method.
Mammal.prototype.get_name = function () {
  return this.name;
};

Mammal.prototype.says = function () {
  return this.saying || '';
}

var myMammal = new Mammal('Herb the Mammal');
myMammal.get_name();


// A "subclass" can be created with a new constructor function with its `prototype`
// property set to an instance of the superclass
var Cat = function (name) {
  this.name = name;
  this.saying = 'meow';
};

Cat.prototype = new Mammal();

// We add methods to the "Cat class" by modifying that new Mammal instance in Cat.prototype
Cat.prototype.purr = function(n) {
  var i, s = '';
  for (i = 0; i < n; i +=1) {
    if (s) {
      s += '-';
    }
    s += 'r';
  }
  return s;
};

// We can "override" the superclass methods by modifying the superclass instance
// in Cat.prototype
Cat.prototype.get_name = function () {
  return this.says() + ' ' + this.name + ' ' + this.says();
};

var myCat = new Cat('Henrietta');
myCat.says();
myCat.purr(5);
myCat.get_name();

// So this is all looking a bit crazy

// Crockford offers some ideas to make the Pseudoclassical pattern a bit more sane

Function.prototype.method = function (name, func) {
  this.prototype[name] = func;
  return this;
};

// An inherits method for function objects would allow us to avoid directly modifying `prototype`.
// `this` is returned so that we can use it in a cascade style
Function.prototype.inherits = function (Parent) {
  this.prototype = new Parent();
  return this;
};

// Now Cat can be defined in one statement.
var Cat = function (name) {
  this.name = name;
  this.saying = 'meow';
}.
  inherits(Mammal).
  method('purr', function (n) {
    var i, s = '';
    for (i = 0; i < n; i +=1) {
      if (s) {
        s += '-';
      }
      s += 'r';
    }
    return s;
  }).
  method('get_name', function () {
    return this.says() + ' ' + this.name + ' ' + this.says();
  });

// That looks a little bit better, but it's still a bit ridiculous. While we have something that
// looks kind of like classical inheritance, it really works nothing like it. We're creating
// complex class hierarchies without a type system. We're following the patterns of a language
// with static type checking, with all the kludge involved with those patterns, without gaining
// any of the benefits that a type system provides. Crockford argues that it's best to avoid `new`
// and Pseudoclassical inheritance altogether. It's a poor attempt to make classical programmers
// feel more at home and JS has much better options for code reuse if you're willing to learn.


// Prototypal inheritance

// One object inherits the properties of another

if (typeof Object.create !== 'function') {
  Object.create = function (o) {
    var F = function () {};
    F.prototype = o;
    return new F();
  };
}

var myMammal = {
  name: 'Herb the Mammal',
  get_name: function () {
    return this.name;
  },
  says: function () {
    return this.saying || '';
  }
};

// Now we can create some new objects that inherit from myMammal.
var myCat = Object.create(myMammal);

// And we can customize/override properties in our new object. This is called
// *differential inheritance* because we're specifying the differences from the
// original object.
myCat.name = 'Henrietta';
myCat.saying = 'meow';
myCat.purr = function (n) {
    var i, s = '';
    for (i = 0; i < n; i +=1) {
      if (s) {
        s += '-';
      }
      s += 'r';
    }
    return s;
}

myCat.name;
myCat.saying;
myCat.purr(5);


// It can also be interesting for data structures to inherit from other data structures
// For instance, we can model the relationship of nested scopes in Javascript with Javascript
// objects.

// The following is just pseudocode. `block` would be called when a left curly brace is encountered.
// `parse` would look up symbols from scope and augment it when new symbols are defined.
var block = function () {
  var oldScope = scope;
  scope = Object.create(scope);
  advance('{');
  parse(scope);
  advance('}');
  scope = oldScope;
}


// Functional pattern

// Pseudocode for the general pattern
// var constructor = function (spec, my) {
//   var that, other private instance variables;
//   my = my || {};

//   Add shared variables and functions to my

//   that = a new object;

//   Add privileged methods to that

//   return that;
// };

// `that` is the object being constructed.
// `spec` is an object spec that contains all of the information that the constructor needs
//   to make an instance. It could also be a single value if a spec object is not needed.
// `my` is a container of secrets that are shared by the constructors in the inheritance chain.
//   For instance, if you call another constrcutor (a super constructor) to create `that`, you could
//   pass `my` to it to share details. Use of `my` is optional.

// A real code example

// All of our data members are private now
var mammal = function (spec) {
  var that = {};

  that.get_name = function () {
    return spec.name;
  };

  that.says = function () {
    return spec.saying || '';
  };

  return that;
};

var myMammal = mammal({name: 'Herb'});

myMammal.get_name();

// Extending mammal
var cat = function (spec) {
  spec.saying = spec.saying || 'meow';
  var that = mammal(spec);
  that.purr = function (n){
    var i, s = '';
    for (i = 0; i < n; i += 1) {
      if (s) {
        s += '-';
      }
      s += 'r';
    }
    return s;
  };
  return that;
};

var myCat = cat({name: 'Henrietta'});
myCat.get_name();
myCat.purr(5);
myCat.says();

// The functional pattern supports super methods as well

// A `superior` method will give us the ability to generate a function that when called will
// invoke a super method of our choice on the given object.
Object.method('superior', function (name) {
  var that = this,
      method = that[name];
  return function () {
    return method.apply(that, arguments);
  };
});

// Now we'll use it to create a coolcat who's get_name method will call its super method.
var coolcat = function (spec) {
  var that = cat(spec),
      super_get_name = that.superior('get_name');
  that.get_name = function (n) {
    return 'like ' + super_get_name() + ' baby';
  };
  return that;
};

var myCoolCat = coolcat({name: 'Bix'});
myCoolCat.get_name();
