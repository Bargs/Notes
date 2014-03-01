Mutation and Concurrency
===============================

Clojure takes a different approach than Java to mutability and concurrent programming. Instead of relying on shared state with fine grained locks, Clojure tries to reduce mutability as much as possible.

When mutation is needed, Clojure provides 4 major mutable references: refs, agents, atoms and vars.


Refs and Clojure's Software Transactional Memory (STM)
------------------------------------------------------

> STM is a non-blocking way to coordinate concurrent updates between related mutable value cells.

Three important terms that form the foundation for Clojure's model of state management and mutation:

* Time - The relative moments when events occur
* State - A snapshot of an entity's properties at a moment in time
* Identity - The logical entity identified by a common stream of states occuring over time.

When dealing with identities in Clojure's model, you're receiving a snapshot of its properties at a moment in time, not necessarily the most recent.

Clojure's STM works with transactions demarked by the `dosync` form. A transaction builds a set of changeable data cells that should all change together. Like a database transaction, a Clojure transaction is all or nothing.

Clojure's four reference types are good at different things. The following table describes their features:


              Ref  Agent  Atom  Var
Coordinated    x
Asynchronous         x
Retriable      x           x
Thread-local                     x


* Coordinated - Reads and writes to multiple refs can be made in a way that guarantees no race conditions
* Asynchronous - The request to update is queued to happen in another thread some time later, while the thread that made the request continues immediately
* Retriable - Indicates that the work done to update a reference's value is speculative and may have to be repeated
* Thread-local - Thread safety is achieved by isolating changes to state to a single thread


The value for each reference type is accessed in the same way, using the `@` reader feature or the `deref` function. The write mechanism for each type is unique. All reference types provide *consistency* by allowing the association of a validator function via `setvalidator`.

`ch10_exercises.clj` delves into the details of refs with a mutable game board example.

### Transactions

Clojure's STM doesn't rely on locking mechanisms like Java's `synchronized` so it can't cause deadlocks.

Behind the scenes it uses *multiversion concurrency control (MVCC)* to ensure *snapshot isolation*.

Snapshot isolation means each transaction gets its own view of the data. The snapshot is made up of reference values that only that transaction has access to. Once the transaction completes, the values of the in transaction refs are compared with the target refs. If no conflicts are found, the changes are committed. If there are conflicts the transaction may be retried.
