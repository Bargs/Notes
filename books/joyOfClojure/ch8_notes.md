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

Macros take data and return data. One task macros can accomplish is to take data, transform it somehow, and return the results. In Clojure, code is data and data is code. So, macros can take code in one form and return the code in another form. This can be useful for a number of things. For one, it can be helpful to mold a programming language to your specific problem domain. Usually this means creating some sort of DSL. While a DSL makes programming in your domain easier, it makes it harder to leverage third party libraries that know nothing about your DSL. It would be nice to write our code using a DSL, but have it transformed into a more common format behind the scenes. Macros can do this for us. See section 8.4 of `ch8_exercises.clj` for an in depth example.


Using Macros to Control Symbolic Resolution Time
------------------------------------------------

In Clojure, the use of syntax-quote will attempt to resolve symbols at macro-expansion time. This helps avoid *name capture* issues where a name generated at compile time clashes with a name available at run-time.

Howerver, it is possible to defer symbolic resolution. You can selectively capture a symbolic name in a macro body by unquoting the quoted symbol inside a syntax-quote (e.g. `\`(+ 2 ~'x)`  the x here will not be qualified with a namespace). The unquote is necessary because the syntax-quote would otherwise try to resolve the namespace of x. The quote is necessary because the unquote would try to resolve the value of x at compile time without it, resulting in a `CompilerException`. An example of this is shown in `ch8_exercises.clj` by implementing a macro that uses an anaphora (in spoken language, a term used in a sentence that refers back to a previously identified subject or object). Note that anaphoras aren't often used in Clojure since they don't nest.

Selective name capturing can be useful when you're using another macro or function that provides an anaphoric symbol. In order to use that symbol in your own macro, you may have to selectively unquote it using the `~'a-symbol` pattern.

A macro that doesn't cause name capturing is considered *hygienic*. Clojure prefers hygienic macros, so if you want to name capture you must do so explicitly using the `~'a-symbol` pattern.


Using Macros to Manage Resources
------------------------------------------------

Macros can be used to automatically handle scarce resources in situations where Java programmers would typically have to rely on the try/catch/finally idiom. For instance, Clojure provides a `with-open` macro that will, when given a "closeable" object, automatically call that object's `.close` method in a finally block. If a resource you're working with doesn't have a `.close` method, you can easily create a generic version of this macro (as seen in `ch8_exercises.clj`).

