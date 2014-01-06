Chapter 2 Drinking from the Clojure Firehose
============================================

2.2 Collections
--------------------------------------------

`()` - List  
`[]` - Vector  
`{}` - Map  
`#{}` - Set  


2.5 Functions
--------------------------------------------

Functions in Clojure are first class.

Anonymous functions can be defined using the `fn` special form. 

**Note** 
> A *special form* is a Clojure expression that’s part of the core language, but not created in terms of functions, types, or macros.

Named functions can be created with `def` and `defn`. `def` simply assigns a symbolic name to any piece of Clojure data. `defn` is a more streamlined macro for naming functions. You can omit the `fn` section and include an optional documentation string like so:

```
(defn make-set 
  "Takes two values and makes a set from them." 
  [x y] 
  (println "Making a set") 
  #{x y})
```

Functions can be defined to accept a variable number of arguments (multiple arities). Each number of arguments can be associated with a unique function body like so:

```
(defn make-set 
  ([x] #{x}) 
  ([x y] #{x y}))
```

A function can be made to accept an unlimited # of arguments by adding `& arg-name` to the end of the regular argument list. Any arguments passed in a function call which exceed the number of explicit arguments will be dumped into a sequence bound to the name following the `&`. 

Anonymous functions can also be defined using a **shorthand notation**: `#()`. This notation allows you to omit the `fn` and the argument list. Implicit arguments can be referred to using `%` followed by the index of the argument. For functions that take only one argument, `%1` can be replaced with just `%`. Examples from the book:

```
(def make-list0 #(list))

(def make-list2 #(list %1 %2))

(def make-list2+ #(list %1 %2 %&)) 
```
