Chapter 4 On Scalars
=======================

4.4 Symbolic Resolution
-----------------------

Symbols are primarily used to provide a name for a given value. However, symbols are objects in their own right and they can be referred to directly using the `symbol` or `quote` functions or the ' special operator.

Even if two symbols have the exact same name, they may be discrete objects. This is because metadata can be attached to symbol objects, so two symbols with the same name may have different metadata. Symbol equality is based only on name (ignoring metadata and identity), but if you were to compare two symbols with the same name using the `identical?` function it would return false.

Clojure is a "Lisp-1". This means that it treats symbolic resolution the same regardless of whether the symbol was bound as a function or a value.

4.5 Regular Expressions
-----------------------

Regexs have a literal representation in Clojure. It looks like this:

```
#"an example pattern"
```

Clojure uses Java's regex engine under the hood. The regex literal creates a java.util.regex.Pattern. So, check out the [javadoc][1] for Pattern for details about special characters.

[1]: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

Option flags can be added at the beginning of the pattern in parenthesis starting with a question mark like so:

```
#"(?i)This is case insensitive"
```

`re-seq` is a function that takes a regex and a string and returns all matches in a lazy seq. You'll use this a lot.

Avoid using Java's `Matcher` object. This object is mutable and works in a non-thread-safe manner.