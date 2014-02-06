Chapter 9 Combining Data and Code
============================================


Namespaces
--------------------------------------------

Namespaces can be thought of as two level look ups. The first part looks up a namespace map, the second is a symbol in that namespace that refers to a var.

There are multiple ways to create a namespace:

### ns

Using `ns` creates a namespace (if it doesn't exist) and switches to it. It includes all classes in `java.lang` and functions, macros, and special forms in `clojure.core`.

### in-ns

Includes `java.lang` but not `clojure.core`. Takes a quoted symbol as the namespace qualifier. Switches to the namespace in addition to creating it.

(in-ns 'gibbon)

> (reduce + [1 2 (Integer. 3)])
; java.lang.Exception: Unable to resolve symbol: reduce in this context

> (clojure.core/refer 'clojure.core)
(reduce + [1 2 (Integer. 3)])
;=> 6

### create-ns

Gives the programmer the most control over the namespace they're creating. Doesn't automatically switch to the new namespace, instead returns it. Imports Java class mappings, but not `clojure.core`.

`create-ns` is meant for advanced techniques. When you have a namespace object you can programmtically modify its map of symbols using `intern` (adds a binding), `ns-unmap` (removes a binding), and `remove-ns` (removes the entire namespace). See `ch9_exercises.clj` for example usages. If you need `clojure.core` functionality, you'll have to `intern` it. Because this manual importing can cause subtle bugs, `create-ns` is best used sparingly.
