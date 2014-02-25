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
