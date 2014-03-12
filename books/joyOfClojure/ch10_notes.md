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

### Embedded Transactions

Unlike some systems, Clojure doesn't provide allow nested transactions to limit the scope of a restart. If a nested transaction encounters a conflict, the entire enclosing transaction must be restarted. Clojure only has one transaction at a time per thread.


### Good things about the STM

1. STM provides your application with a consistent view of its data
2. No need for locks
3. Give you the ACI part of ACID

### Bad things about the STM

1. Write skew. Only updated refs are checked for conflicts when a transaction commits. So if two transactions run concurrently, one reading a value and the other writing to it, both can commit without conflict. This is undesirable because the behavior of the first transaction is probably affected by the value of the ref, even though it doesn't write to it. This anomaly is called *write skew*. Sometimes write skew is dealt with by writing a dummy value to the read-only ref, but Clojure has a more efficient way to accomplish this with the function `ensure`. `ensure` will guarantee that a read-only ref isn't updated by another thread during the transaction.

2. Live lock - a set of transactions that repeatedly restart one another. Clojure combats live lock in a couple ways. One, there are transaction restart limits that will raise an error. Two, Clojure implements *barging* in the STM, which means older transactions are allowed to run while younger transactions have to retry.


### Things you shouldn't do in a transaction

1. IO. Transaction retrys could execute the IO operation over and over. When performing IO, it's useful to use the `io!` macro which will throw an error if accidentally used in a transaction.

2. Object mutation. It often isn't idempotent, which will be a problem if the transaction must retry.

3. Large units of work. Get in and get out as quickly as possible.


### Commutative change

If you have an update function that's commutative (order of operands is unimportant) then you can reduce the number of transaction retrys by using the `commute` function instead of `alter`. `commute` will run the update function once on the in-transaction value of the ref and once again on the value of the ref at commit time to determine what value should get committed. This makes your app more concurrent if you can put up with two conditions:

1. The value you see in-transaction may not be the value that gets committed at commit time.
2. The function you give to `commute` will be run at least twice - once to compute the in-tranaction value, and again to compute the commit value. It might be run any number of times.

### Vulgar change

The `ref-set` function can be used to set a ref to a provided value, unlike `alter` and `commute` which take an update function instead of an explicit value.


### Stressed refs

You should avoid having both short and long running transactions working with the same ref. When this happens, the short transactions can frustrate the long transaction by changing the value of its ref more times than the ref's history can handle. This causes the long running transaction retry, increasing the ref's history by 1 on each retry up to a certain limit. Clojure provides some tools (demonstrated in `ch10_exercises.clj`) to lessen the pain if it can't be avoided, but if possible it's best to follow the rule of thumb and sidestep the problem entirely.


Agents
------------------------------------------------------

Each agent has a queue which holds actions that will be performed on its value. These actions are performed one at a time, modifying the agent's value for each subsequent action. Actions are queued using the `send` and `send-off` forms. Agents are created with the `agent` function.

Agents can be useful to serialize access to a resource. With explicit locks there would be a danger of deadlock if multiple locks were in play, and even without multiple locks you'd risk having some threads starve while waiting for the lock. Because agents are asynchronous and require no lock, neither of these are a problem. In other languages you might write a queue to deal with these problems, but agents give you the same behavior for free.

If for some reason you need to block until an action has been performed by an agent, you can use `await` and `await-for` to wait until all the actions the thread has sent to a set of agents have completed. `await-for` is the same as await except you can specify a time out.


### send vs send-off

Using `send-off` will put an action on an agent's queue which gets worked by a single thread assigned to that agent. Every agent gets one of these threads, without limit. In contrast, `send` will put an action on an agent's queue which must be worked by a thread from a shared pool. The size of this pool is based on the number of processors assigned to the JVM.

If you give a blocking action to `send`, it can clog up the pool for all of the agents in the system. So, `send` should be used for actions that don't block, and `send-off` should be used for actions that might block (ex. on I/O), sleep, or generally tie up the thread.

### Failures in an agent action

Since agent actions happen asynchronously, exceptions won't bubble up the call stack in a way that your code can handle. Agents have two different error handling modes to deal with this problem:

1. :fail - new agents default to this mode. When an exception is thrown the agent holds on to it and goes into a *failed* or *stopped* state and stops processing actions. The exception can be manually inspected with the `agent-error` function, and it can be manually restarted with the `restart-agent` function.

2. :continue - any action throwing an exception is skipped and the agent continues processing. This is usually useful when combined with an `:error-handler` function. These functions take the agent and the exception as arguments and can take some sort of action, like logging an error message.

### When not to use agents

Agents shouldn't be used as a simple mechanism for creating new threads. If an agent is being used in a way where its value is unimportant, it's probably being abused. These roles are usually better filled by a pure Java Thread, or an executor or maybe a Clojure future.

You shouldn't try to make agents synchronous by abusing `await`. If you find yourself using `await` frequently, there may be a more suitable reference type to use like atoms which are shared and uncoordinated like agents, but also synchronous by nature.

Atoms
------------------------------------------------------

Atoms are synchronous (like refs) but uncoordinated (like agents).

> Anywhere you might want to atomically compute a value given an exisiting value and swap in the new value, an atom will suffice.

> Atoms are thread-safe and can be used when you require a lightweight mutable reference to be shared across threads.

An update to an atom will retry if the value of the atom was changed by another thread while the current computation was executing. This doesn't happen through the STM though, so you must be careful when using atoms in a transcation. If the transaction is retried, the atom may be updated multiple times, which should be regarded as a side effect. Only put an atom update in a transaction if it is idempotent. An example of such an idempotent action is memoization, which there is an example of in `ch10_exercises.clj`.


Locks
------------------------------------------------------

Despite the plethora of Clojure concurrency tools, sometimes you still have to rely on explicit locks. One such situation is the concurrent modification of arrays. `ch10_exercises.clj` works through this scenario.

> Currently, the only way to safely modify and see consistent values for a mutable object (such as an array or a class instance) across threads in Clojure is through locking. Wrapping a mutable object in a Clojure reference type provides absolutely no guarantees for safe concurrent modification.

When you have to use locks, Clojure's `locking` macro is the way to go. One nice thing about `locking` is that it always releases its lock at the end of the block.

`locking` is simple but if you want more fine grained control (e.g. locks per array slot instead of one for the entire array) you have to dip into Java's locking mechanisms. `java.util.concurrent.locks` provides a set of explicit locks that we'll use in `ch10_exercises.clj`.
