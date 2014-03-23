Chapter 11 Parallelism
==============================

> Concurrency is about the design of a system, parallelism is about the execution model.

Clojure offers two reference types for parallelism, futures and promises.


Futures
------------------------------

Futures offer a simple mechanism for partitioning an operation into discrete parts that can be processed in parallel. Futures wrap an expression that is executed the first time the future is dereferenced, and only the first time.
