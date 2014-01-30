Chapter 8 Macros
============================================

Macros take advantage of the data-is-code-is-data philosophy. Most of their work is done at compile time. Macros are expanded to the form that they return at compile time. Note that as a result, macros cannot be called with a list of arguments. The number of arguments must be known at compile time so that the expansion can be done.

What are macros good for? Some things: transforming one expression into another form (e.g. the `->` macro), combining forms, changing forms, controlling evaluation and resolution of arguments, managing resources, and building functions.


Data is Code is Data
--------------------------------------------

Clojure programs, like that of other Lisps, are made entirely out of data. Clojure code is comprised of simple data structures, unlike other languages where there is a sharp divide between code and data.


### Syntax-quote `\`, unquote `~`, and unquote-splicing `~@`

From the Clojure docs:

> For all forms other than Symbols, Lists, Vectors, Sets and Maps, `x is the same as 'x.
>
> For Symbols, syntax-quote resolves the symbol in the current context, yielding a fully-qualified symbol (i.e. namespace/name or fully.qualified.Classname). If a symbol is non-namespace-qualified and ends with '#', it is resolved to a generated symbol with the same name to which '_' and a unique id have been appended. e.g. x# will resolve to x_123. All references to that symbol within a syntax-quoted expression resolve to the same generated symbol.
>
> For Lists/Vectors/Sets/Maps, syntax-quote establishes a template of the corresponding data structure. Within the template, unqualified forms behave as if recursively syntax-quoted, but forms can be exempted from such recursive quoting by qualifying them with unquote or unquote-splicing, in which case they will be treated as expressions and be replaced in the template by their value, or sequence of values, respectively.

Quoting and unquoting can be used outside of macros to manipulate code at runtime (see section 8.1.1 in `ch8_exercises.clj` for an example). However, you'll most often see syntax quoting inside the body of macros.

**Note**
> Things can get interesting if you have nested syntax-quotes. See some examples in `ch8_exercises.clj`.


### Macro rules of thumb

Things to think about when writing your own macros:

* Don’t write a macro if a function will do. Reserve macros to provide syntactic abstractions or create binding forms.
* Write an example usage.
* Expand your example usage by hand.
* Use macroexpand , macroexpand-1 , and clojure.walk/macroexpand-all 4 liberally to understand how your implementation works.
* Experiment at the REPL.
* Break complicated macros into smaller functions whenever possible.


Defining Control Structures
--------------------------------------------

Most macros in Clojure are defined using macros. Other languages like Haskell use higher order functions instead of macros. One advantage of macros is that they manipulate compile-time forms.


### Defining control structures without syntax-quote

See example in `ch8_exercises.clj`


### Defining control structures with syntax-quote and unquoting

syntax-quote is useful when you want to selectively evaluate parts of a quoted expression. See `ch8_exercises.clj`.


Macros Combining Forms
--------------------------------------------

Macros can help you mold the language to your problem domain. Tasks can be combined to simplify your API, or in other words provide a *domain specific language* for your problems.

An interesting quote:
> In Lisp the distinction between DSL and API is thin to the point of transparency.

Using macros to combine forms can reduce the amount of boilerplate a client must endure when working with your API. See `ch8_exercises.clj` to see an example.


Using Macros to Change Forms
--------------------------------------------
