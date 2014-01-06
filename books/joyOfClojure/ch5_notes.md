Chapter 5 Collection Types
==========================

Persistence
--------------------------

In Clojure, the term *persistent* has a different meaning than it usually does in other technical contexts these days. It does not refer to the storage of values on a disk. Clojure's use of the word has to do with immutable in-memory collections with specific properties.

> In particular, a persistent collection in Clojure allows you to preserve historical versions (Okasaki 1999) of its state, and promises that all versions will have the same update and lookup complexity guarantees. The specific guarantees depend on the collection type, and we’ll cover those details along with each kind of collection.

In otherwords, persistent collections are immutable and when you perform an operation that appears to "change" the collection, it's actually creating a new collection that reflects the change without touching the original. This seems similar to Scheme lists and Java strings.

Sequence Abstraction
--------------------------

First, some vocab.

### Terms

Clojure has a set of very similar sounding terms that each have distinct, precise definitions.

* Collection - A composite data type
* Sequential - Ordered series of values
* Sequence - A sequential collection that may or may not exist yet
* seq - Simple API for navigating collections (first, rest, nil and ())
* `clojure.core/seq` - A function that returns an object implementing the seq API

So, *sequential* is a braod category of collection types. A *sequence* is one kind of sequential. And *a seq* is any object that implements the *seq API*. 

When the `seq` function is called on collection-like objects, it returns a seq or `nil` if the collection is empty. Seq will return the original object if it implements the seq API, otherwise it returns a new seq object based on the collection.

Functions that promise to return *seqs* will always return `nil` on empty collections, just like the `seq` function. Functions that promise to return *sequences* (like `rest`, `map`, `filter`) will never return `nil`, only empty sequences.

This distinction doesn't make sense to me yet. A later section states the following:

> All an object needs to do to be a sequence is to support the two core functions: `first` and `rest`.

Which is basically the definition for *a seq* laid out earlier. So I still need some clarification on their distincition between a *seq* and a *sequence*.

From furthur research, *seq* vs *sequence* doesn't seem to be a normal distinction in Clojure. This [clojure.org doc][http://clojure.org/sequences] uses the terms interchangably.

### Equality Partitions

Colletions are placed into three main categories (or "partitions"): sequentials, maps, and sets. These categories affect equality. Two collections will never be equal if they belong to different partitions. Within a partition, equality is defined more loosely. For instance, two sequential collections with the same values in the same order will return true when compared with `=`, even if they are different types of objects (ex. a vector and a list).


### The Abstraction

Instead of building heavily on cons cells like other Lisps, Clojure has taken the conceptual interface they provide and turned it into the *sequence* abstraction. Clojure provides a vast library of functions that operate on this abstraction. To leverage these functions, all an object needs to do is support the `first` and `rest` functions. You can get a seq representation of any Clojure collection by using the `seq` function. Some collections have multiple seq representations that you can get using other functions.


Vectors
--------------------------

Most frequently used collection in Clojure. 

`vec` is a function that takes a collection and returns a vector.

*primitive vectors* can be created with the `vector-of` function. These are just like any other vector but they store their values as primitives and attempt to coerce any additions to their given type.

As they get larger, vectors and lists perform differently on some operations. Vectors are good at three things compared to lists:

1. Adding or removing things from the right end of the collection
2. Accessing or changing items in the interior of the collection by numeric index
3. Walking in reverse order

Accessing or changing an item by index can be done in "essentially" constant time.

You can get an item at an index using `nth`, `get`, or by using the vector as a function (e.g. (vector-name 4)). Values can be changed with `assoc`. Items in nested vectors can be reached with `assoc-in`, `get-in`, and `update-in`.


