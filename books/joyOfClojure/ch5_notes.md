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

From furthur research, *seq* vs *sequence* doesn't seem to be a normal distinction in Clojure. This [clojure.org doc](http://clojure.org/sequences) uses the terms interchangably.

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

### Vectors as stacks

The Clojure equivalent to push and pop for vectors are `conj` and `pop`. These functions operate on the right side of the vector. `pop` returns a new vector with the last item removed, instead of the popped element. You can get the last element from the vector using `peek`. These operations are also "essentially" constant time.


### Subvectors

You can efficiently grab a slice from a vector using the `subvec` function. It takes a starting (inclusive) and ending (exclusive) index and returns the subvector described by those fence posts. The `subvec` function is efficient because the subvector holds a reference to the original vector and just does some offset math to provide the correct values.


`MapEntry`s are also vectors, which can be useful.

### Things you can't do with vectors

1. Vectors aren't sparse. In other words, you can't have holes at random indexes in your vectors. Any operation that would create such a vector (deleting any index but the rightmost, adding an item to an index past the end) won't work.

2. Vectors cannot be used as queues efficiently (since efficient operations only happen on the end of the vector, not the beginning). Use a `PersistentQueue` if you want that.

3. Vectors should not be used as sets. The `contains?` function only checks for the existence of keys (for a vector, an index) not values.


Lists
--------------------------

Lists are used almost exclusively to represent Clojure code. As a data type, they offer little value over vectors.

In Clojure, the right way to add an element to the front of a list is `conj`, not `cons`. `cons` will work, but `conj` will always pick the most efficient way to add an element to any collection type. Also, `cons` doesn't guarentee that its result will be a list, but only some kind of seq.

**Note**
> All seqs print with rounded parentheses, but this does not mean they’re the same type or will behave the same way.

### Things you shouldn't do with lists

1. Don't look up values by index. Lists aren't optimized for indexed lookups like vectors, so they're always O(n)

2. Don't use them as sets. Same arguments from the vector section apply.

3. Don't use them as queues. You can't even remove items from the right side.


Persistent Queues
--------------------------

Immutable queue objects. You create one by adding to the provided empty queue object: `clojure.lang.PersistentQueue/EMPTY`

Always use the functions designed for use with queues: `peek`, `pop`, and `conj`. Functions like `rest` and `next` will work, but not as expected because they'll return seqs not queues.

Clojure queues are implemented with a seq in the front and a vector in the back to take advantage of each collection's strengths (removal from the left for a seq, addition to the right for a vector). When the seq runs dry the vector is wrapped in a seq and a new empty vector is added to the end.