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
