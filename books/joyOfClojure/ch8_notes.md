Chapter 8 Macros
============================================

Macros take advantage of the data-is-code-is-data philosophy. Most of their work is done at compile time.

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
