Chapter 4 On Scalars
=======================

4.4 Symbolic Resolution
-----------------------

Symbols are primarily used to provide a name for a given value. However, symbols are objects in their own right and they can be referred to directly using the `symbol` or `quote` functions or the ' special operator.

Even if two symbols have the exact same name, they may be discrete objects. This is because metadata can be attached to symbol objects, so two symbols with the same name may have different metadata. Symbol equality is based only on name (ignoring metadata and identity), but if you were to compare two symbols with the same name using the `identical?` function it would return false.
