
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
