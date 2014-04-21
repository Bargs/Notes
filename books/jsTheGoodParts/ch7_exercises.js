// Beware, RegExp objects made by regular expression literals
// share a single instance
function make_a_matcher() {
  return /a/gi;
}

var x = make_a_matcher();
var y = make_a_matcher();

x.lastIndex = 10;

// y points to the same instance, according to the book. However, running this
// in lighttable produces a value of 0 for `y.lastIndex` still. So perhaps
// this is dependent on the JS implementation.
y.lastIndex
