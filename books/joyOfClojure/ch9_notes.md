Chapter 9 Combining Data and Code
============================================


Namespaces
--------------------------------------------

Namespaces can be thought of as two level look ups. The first part looks up a namespace map, the second is a symbol in that namespace that refers to a var.

### Creating namespaces

There are multiple ways to create a namespace:

#### ns

Using `ns` creates a namespace (if it doesn't exist) and switches to it. It includes all classes in `java.lang` and functions, macros, and special forms in `clojure.core`.

#### in-ns

Includes `java.lang` but not `clojure.core`. Takes a quoted symbol as the namespace qualifier. Switches to the namespace in addition to creating it.

```
(in-ns 'gibbon)

(reduce + [1 2 (Integer. 3)])
; java.lang.Exception: Unable to resolve symbol: reduce in this context

(clojure.core/refer 'clojure.core)
(reduce + [1 2 (Integer. 3)])
;=> 6
```

#### create-ns

Gives the programmer the most control over the namespace they're creating. Doesn't automatically switch to the new namespace, instead returns it. Imports Java class mappings, but not `clojure.core`.

`create-ns` is meant for advanced techniques. When you have a namespace object you can programmtically modify its map of symbols using `intern` (adds a binding), `ns-unmap` (removes a binding), and `remove-ns` (removes the entire namespace). See `ch9_exercises.clj` for example usages. If you need `clojure.core` functionality, you'll have to `intern` it. Because this manual importing can cause subtle bugs, `create-ns` is best used sparingly.


### Hiding your privates

Members of a namespace can be made private by attaching metadata to their var. That would look something like this:

```
(ns hider.ns)
(defn ^{:private true} answer [] 42)
```

For functions, the same can be accomplished with the shorthand macro `defn-`. For `def` and `defmacro`, however, the private metadata must be added manually.

### Inclusions and exclusions

Clojure gives you fine grained declarative control over imports in your namespace. These are provided as directives to the `ns` macro.

A namespace definition might look like this:

```
(ns joy.ns-ex
  (:refer-clojure :exclude [defstruct])
  (:use (clojure set xml))
  (:use [clojure.test :only (are is)])
  (:require (clojure [zip :as z]))
  (:import (java.util Date)
         (java.io File)))
```

`use` will load everything in the specified namespace so that its contents can be used without namespace qualification. This can lead to name conflicts, so it should almost always be used with `only` to only pull in what you need.

`require` will also load everything in a namespace, but its contents will need to be qualified when used. The qualification can be shortened by aliasing the namespace with `as`.

`import` is used to import Java classes and interfaces

`refer-clojure` is the same as `(refer 'clojure.core)`. It can be used in conjunction with the `exclude` filter to remove bindings to vars in clojure.core in your namespace to avoid conflicts if your lib needs to use those names.

`load` will load clojure code on the classpath, given a directory path.


Multimethods and the UDP
--------------------------------------------

Multimethods provide function polymorphism based on the result of an arbitrary dispatch function. Most of the details and examples are in the exercises file for this chapter. Multimethods and ad hoc hierarchies are extremely flexible tools but they can make things complex. Clojure provides higher level, more simple tools for creating abstractions and polymorphism which will be in the next section.


Types, Protocols, and Records
--------------------------------------------

### Records

Records are like slightly less flexible maps with much better performance. Records are defined using the `defrecord` form which looks something like this:

```
(defrecord TreeNode [val l r])

(TreeNode. 5 nil nil)
```

`defrecord` creates a new Java class with a constructor with parameters for each of the fields listed. This class is auto imported into the namespace where the record is declared, but it must done manually in other namespaces using the `:import` directive to the `ns` function.

Why should you use records?

1. They're a simple way of defining what fields your object should have instead of using a structure-less map.
2. They're created more quickly, consume less memory, and look up keys faster than regular maps.
3. They can store primitive values isntead of boxed primitives which take up more memory.

**Note**
> Records don't serve as functions like maps do. Also, records are never equal to maps with the same key/avlue mappings. Also note that records are similar to structs that you might see in older code, and have largely replaced them.

See an example of the persistent binary tree from previous chapters built from records instead of maps in `ch9_exercises.clj`.


### Protocols

A *protocol* is a set of function signatures (each with at least one parameter), not unlike an interface in Java. When one of the functions is called, the correct implementation is found based on the type of the first parameter. This is like type based polymorphism in Java if you think of the first parameter as the target object that would be left of the dot in Java.

Protocols are implemented using the `extend`, `extend-type`, and `extend-protocol` forms. All of these do the same thing, `extend-type` and `extend-protocol` are just convenience macros for specific scenarios.

**Note**
> Protocol extension is at the granularity of the entire protocol and not on a per-function basis. In order to extend all of a protocol's functions for a given type, they must all be extended in the same `extend-type` form.

Clojure polymorphism lives in the protocol functions, not in the classes. This is unlike most OO languages, where you would have to use wrappers, adapters, or monkey patching to dynamically extend functionality to a type the way a Clojure protocol can. This makes it easier to tie together third party libraries that know nothing about each other.


#### Sharing Method Implementations

Protocols and `extend` don't support inheritance of implementation. You can't build a base implementation of a protocol, and then build on top of it for a given type the way you might with an abstract base class and concrete class in Java. Instead, there are a couple other ways to achieve code reuse, keeping things DRY.

1. Write a regular function that builds on the protocol's methods. Instead of making the function part of the protocol itself, simply build it using only protocol methods when manipulating the arguments. This way the function will work with any type that the protocol's methods have been extended to.

Ex.
```
(defn fixo-into [c1 c2]
(reduce fixo-push c1 c2))

(xseq (fixo-into (TreeNode. 5 nil nil) [2 4 6 7]))
;=> (2 4 5 6 7)

(seq (fixo-into [5] [2 4 6 7]))
;=> (5 2 4 6 7)
```

2. Use `extend` instead of `extend-type` and `extend-protocol`.  `extend` takes a map of protocol implementations keyed by the functions' names. This map can be defined separately from the `extend` form. Once you've defined the map for one type, it can be used (in whole or in parts) by other types that wish to share the implementation.


#### Reify

Reify can combine the power of closures and protocols. It's a lot like an anonymous class in Java. The result of a `reify` expression is an object that implements a protocol/interface without having a concrete class definition laying around elsewhere. When this new object is passed as the first parameter to one of the methods of the protocol, the matching method implementation that was passed to `reify` will be called.

Part of what makes `reify` interesting is that the implementation methods can be closures. Like the "bot" example from chapter 7, these closures can share an environment so that they feel like methods on a traditional object, using shared state. Unlike the bot example, the closures we're using with `reify` provide an implementation for a protocol, giving clients of the object a stable abstraction to code against. See an example in `ch9_exercises.clj`.

#### Namespaces and protocol methods

Protocol methods live in the same namespace as the protocol itself. Regardless of the type/record that a protocol has been extended to, the methods are always in the same namespace. This means that you could extend multiple protocols from different namespaces to a single type without coflict, even if all of their method names are exactly the same. This makes it easier to manage third party code outside of your control. It also means that two protocols in the same namespace can't have methods with the same names, but that is less of a problem since the same person usually has control over all of the code in a single namespace.

#### Implementing protocol methods in defrecord

Instead of using `extend` you can also implement a protocol or interface directly in `defrecord`. This can be more convenient, and calling methods implemented this way can also be several times faster than their `extend` bretheren. The record keys can also be used directly as local variables in these method implementations. See an example in `ch9_exercises.clj`.

A couple of notes:

1. This style of implementation allows you to implement Java interfaces and extend java.lang.Object, which the `extend` forms do not.

2. When using `recur`, there's no need to pass the target object, it will be passed automatically (just like with `reify`).
