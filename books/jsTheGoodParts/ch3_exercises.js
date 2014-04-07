var stooge = {
  "first-name": "Jerome",
  "last-name": "Howard"
}

// A helper function for creating objects with a given prototype
if (typeof Object.create !== 'function') {
  Object.create = function (o) {
    var F = function () {};
    F.prototype = o;
    return new F();
  };
}

var another_stooge = Object.create(stooge);

// another_stooge doesn't have a first-name or last-name property, so its prototype (stooge) will be checked
document.writeln(another_stooge['first-name']);
document.writeln(another_stooge['last-name']);

// Add those properties to another_stooge, this doesn't change the value in the prototype
another_stooge['first-name'] = 'Harry';
another_stooge['last-name'] = 'Moses';

document.writeln(another_stooge['first-name']);
document.writeln(another_stooge['last-name']);

// The prototype relationship is dynamic
document.writeln(another_stooge.profession);

// Adding a prop to the prototype automatically makes it available to all objects based on it
stooge.profession = 'actor';
document.writeln(another_stooge.profession);


// Object properties can be enumerated with `for in`.
// Usually you'll want to filter out functions and props on the prototype chain.
var name;

for (name in another_stooge) {
    if (typeof another_stooge[name] !== 'function' && another_stooge.hasOwnProperty(name)) {
          document.writeln(name + ': ' + another_stooge[name]);
    }
}

another_stooge.profession = 'programmer';
another_stooge.profession;

// The `delete` operator will remove a property from an object, if it exists. It won't touch the prototype.
delete another_stooge.profession;
delete another_stooge.profession;
another_stooge.profession;

