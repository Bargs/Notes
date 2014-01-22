;; Creating a function that dynamically creates other functions

(defn fnth [n]
  (apply comp
         (cons first
               (take (dec n) (repeat rest)))))

((fnth 5) '[a b c d e])

((partial + 5) 100 200)


;; Creating a higher level function from existing parts by passing
;; functions as argments

(def plays [{:band "Burial", :plays 979, :loved 9}
            {:band "Eno", :plays 2333, :loved 15}
            {:band "Bill Evans", :plays 979, :loved 9}
            {:band "Magma", :plays 2665, :loved 31}])

(def sort-by-loved-ratio (partial sort-by #(/ (:plays %) (:loved %))))

(sort-by-loved-ratio plays)


;; An example of a naturally recursive problem and solution
;; convert is a general purpose unit conversion function thanks
;; to the combo of recusive data and a recursive function

(def simple-metric {:meter 1,
                    :km 1000,
                    :cm 1/100,
                    :mm [1/10 :cm]})

(defn convert [context descriptor]
  (reduce (fn [result [mag unit]]
            (+ result
               (let [val (get context unit)]
                 (if (vector? val)
                   (* mag (convert context val))
                   (* mag val)))))
          0
          (partition 2 descriptor)))


;; testing to see how partition works
(partition 2 [3 :km 1 :meter 80 :cm])

(float (convert simple-metric [3 :km 10 :meter 80 :cm 10 :mm]))



;; The trampoline

(defn elevator [commands]
  (letfn
    [(ff-open [[_ & r]]
      "When the elevator is open on the 1st floor
       it can either close or be done."
      #(case _
         :close (ff-closed r)
         :done true
         false))
     (ff-closed [[_ & r]]
       "When the elevator is closed on the 1st floor
        it can either open or go up."
       #(case _
          :open (ff-open r)
          :up (sf-closed r)
          false))
     (sf-closed [[_ & r]]
       "When the elevator is closed on the 2nd floor
        it can either go down or open."
       #(case _
          :down (ff-closed r)
          :open (sf-open r)
          false))
     (sf-open [[_ & r]]
       "When the elevator is open on the 2nd floor
        it can either close or be done"
       #(case _
          :close (sf-closed r)
          :done true
          false))]
    (trampoline ff-open commands)))

(elevator [:close :open :close :up :open :open :done])

(elevator [:close :up :open :close :down :open :done])


;; Continuation Passing Style


;; Factorial in CPS
(defn fac-cps [n k]
  (letfn [(cont [v] (k (* v n)))]
    (if (zero? n)
      (k 1)
      (recur (dec n) cont))))

(defn fac [n]
  (fac-cps n identity))

(fac 10)



(defn mk-cps [accept? kend kont]
  (fn [n]
    ((fn [n k]
       (let [cont (fn [v]
                    (k ((partial kont v) n)))]
         (if (accept? n)
           (k 1)
           (recur (dec n) cont))))
     n kend)))


(def fac
  (mk-cps zero?
          identity
          #(* %1 %2)))

(fac 10)

(def tri
  (mk-cps #(== 1 %)
          identity
          #(+ %1 %2)))

(tri 10)


;; A* pathfinding

;; Our 2d world
(def world [[  1   1   1   1   1]
            [999 999 999 999   1]
            [  1   1   1   1   1]
            [  1 999 999 999 999]
            [  1   1   1   1   1]])


;; Neighbors function from chapter 5.
;; Takes a grid size and coordinates. Calculates
;; the coordinates of positions N, S, E, and W of
;; the current position. Filters out all invalid
;; coordinates (< 0 or >= size). Returns a vector
;; of coordinates for all valid neighbors.
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

(neighbors 5 [0 0])


;; Our estimated cost function
(defn estimate-cost [step-cost-est size y x]
  (* step-cost-est
     (- (+ size size) y x 2)))

(estimate-cost 900 5 0 0)

(estimate-cost 900 5 4 4)


;; Our "cost so far" function
(defn path-cost [node-cost cheapest-nbr]
  (+ node-cost
     (or (:cost cheapest-nbr) 0)))

(path-cost 900 {:cost 1 })


;; Our total cost function
(defn total-cost [newcost step-cost-est size y x]
  (+ newcost
     (estimate-cost step-cost-est size y x)))

(total-cost 0 900 5 0 0)

(total-cost 1000 900 5 3 4)

(total-cost (path-cost 900 {:cost 1}) 900 5 3 4)


;; A helper function for determining the minimum value
;; in a collection based on some function
(defn min-by [f coll]
  (when (seq coll)
    (reduce (fn [min other]
              (if (> (f min) (f other))
                other
                min))
            coll)))

(min-by :cost [{:cost 100} {:cost 36} {:cost 9}])


;; The main A* algorithm
(defn astar [start-yx step-est cell-costs]
  (let [size (count cell-costs)]
    (loop [steps 0
           routes (vec (replicate size (vec (replicate size nil))))
           work-todo (sorted-set [0 start-yx])]
      (if (empty? work-todo)
        [(peek (peek routes)) :steps steps]
        (let [[_ yx :as work-item] (first work-todo)
              rest-work-todo (disj work-todo work-item)
              nbr-yxs (neighbors size yx)
              cheapest-nbr (min-by :cost
                                   (keep #(get-in routes %)
                                         nbr-yxs))
              newcost (path-cost (get-in cell-costs yx)
                                 cheapest-nbr)
              oldcost (:cost (get-in routes yx))]
          (if (and oldcost (>= newcost oldcost))
            (recur (inc steps) routes rest-work-todo)
            (recur (inc steps)
                   (assoc-in routes yx
                             {:cost newcost
                              :yxs (conj (:yxs cheapest-nbr [])
                                         yx)})
                   (into rest-work-todo
                         (map
                          (fn [w]
                            (let [[y x] w]
                              [(total-cost newcost step-est size y x) w]))
                          nbr-yxs)))))))))