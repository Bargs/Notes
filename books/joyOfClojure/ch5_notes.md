Chapter 5 Collection Types
==========================

Persistence
--------------------------

In Clojure, the term *persistent* has a different meaning than it usually does in other technical contexts these days. It does not refer to the storage of values on a disk. Clojure's use of the word has to do with immutable in-memory collections with specific properties.

> In particular, a persistent collection in Clojure allows you to preserve historical versions (Okasaki 1999) of its state, and promises that all versions will have the same update and lookup complexity guarantees. The specific guarantees depend on the collection type, and we’ll cover those details along with each kind of collection.

In otherwords, persistent collections are immutable and when you perform an operation that appears to "change" the collection, it's actually creating a new collection that reflects the change without touching the original. This seems similar to Scheme lists and Java strings.