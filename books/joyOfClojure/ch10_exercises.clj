
;; Defining a utility that will let us throw a bunch of
;; threads at a given function
(ns joy.mutation
  (:import java.util.concurrent.Executors))

(def thread-pool
  (Executors/newFixedThreadPool
   (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(defn dothreads!
  [f & {thread-count :threads
        exec-count :times
        :or {thread-count 1 exec-count 1}}]
  (dotimes
    [t thread-count]
    (.submit thread-pool #(dotimes [_ exec-count] (f)))))


;; Creating a mutable game board with refs

(def
  initial-board
  [[:- :k :-]
   [:- :- :-]
   [:- :K :-]])

(defn board-map [f board]
  (vec (map #(vec (for [s %] (f s)))
            board)))

(defn reset-board!
  "Resets the board state. Generally these types
  of functions are a bad idea, but matters of page
  count force our hand."
  []
  (def board (board-map ref initial-board))
  (def to-move (ref [[:K [2 1]] [:k [0 1]]]))
  (def num-moves (ref 0)))

;; Neighbors function from chapter 5.
(defn neighbors
  ([size yx]
   (neighbors [[-1 0] [1 0] [0 -1] [0 1]]
              size
              yx))
  ([deltas size yx]
   (filter (fn [new-yx]
             (every? #(< -1 % size) new-yx))
           (map #(vec (map + yx %))
                deltas))))


(def king-moves
  (partial neighbors
           [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]] 3))

(defn good-move?
  [to enemy-sq]
  (when (not= to enemy-sq)
    to))

(defn choose-move
  "Randomly choose a legal move"
  [[[mover mpos] [_ enemy-pos]]]
  [mover (some #(good-move? % enemy-pos)
               (shuffle (king-moves mpos)))])


(defn place [from to] to)

(defn move-piece [[piece dest] [[_ src] _]]
  (alter (get-in board dest) place piece)
  (alter (get-in board src ) place :-_)
  (alter num-moves inc))

(defn update-to-move [move]
  (alter to-move #(vector (second %) move)))

;; Here there be dragons
;; With two separate `dosync` forms, our updates are spread across more
;; than one transaction. This will cause troubles, as we'll see below.
(defn make-move []
  (let [move (choose-move @to-move)]
    (dosync (move-piece move @to-move))
    (dosync (update-to-move move))))


(reset-board!)

(make-move)

(board-map deref board)

(make-move)

(board-map deref board)

;; Things look ok so far...
;; Let's see what happens when we run it in
;; a concurrent execution environment
(dothreads! make-move :threads 100 :times 100)

;; In all likelyhood, this shows us a broken board.
(board-map deref board)


;; An improved make-move with only one transaction.
(defn make-move-v2 []
  (dosync
   (let [move (choose-move @to-move)]
     (move-piece move @to-move)
     (update-to-move move))))

(reset-board!)

(make-move)

(board-map deref board)

@num-moves

(dothreads! make-move-v2 :threads 100 :times 100)
(board-map #(dosync (deref %)) board)

@to-move

@num-moves


;; Stressed refs

;; Runs a slow transaction in a separate thread while a loop with short transactions
;; quickly modifies the ref the slow transaction is trying to read
(defn stress-ref [r]
  (let [slow-tries (atom 0)]
    (future
      (dosync
       (swap! slow-tries inc)
       (Thread/sleep 200)
       @r)
      (println (format "r is: %s, history: %d, after: %d tries"
                       @r (.getHistoryCount r) @slow-tries)))
    (dotimes [i 500]
      (Thread/sleep 10)
      (dosync (alter r inc)))
    :done))

;; The long running transaction probably won't finish until the short
;; transactions are finished
(stress-ref (ref 0))

;; We can increase the max history for the ref if mixing long and short
;; transactions is inevitable in our application.
;; The long transaction manages to finish first here, but it still has
;; to retry a good number of times.
(stress-ref (ref 0 :max-history 30))

;; If we're doing real work in the long running transaction, retrying so many
;; times could get very wasteful. If we know the history is going to have to
;; be fairly long, we can start it out at a larger min length instead of increasing
;; it each try starting at 0
(stress-ref (ref 0 :min-history 15 :max-history 30))


;; Agents

;; Creating an agent and sending an action to it
(def joy (agent []))
(send joy conj "First edition")

@joy

;; Agent actions are asyncronous
(defn slow-conj [coll item]
  (Thread/sleep 1000)
  (conj coll item))

;; The return value will still show the old value of the agent since
;; the action is still running asyncronously.
(send joy slow-conj "Second Edition")

;; Execute this a little later, than the new value has taken over
@joy

