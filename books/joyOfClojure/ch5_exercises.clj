;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.

;; We'll create a function to locate the index of an element in a collection

;; The function, named pos , must
;; - Work on any collection type, returning indices corresponding to some value
;; - Return a numerical index for sequential collections or associated key for maps and sets
;; - Otherwise return nil


;; First approach from book

(defn pos [e coll]
  (let [cmp (if (map? coll)
              #(= (second %1) %2)
              #(= %1 %2))]
    (loop [s coll idx 0]
      (when (seq s)
        (if (cmp (first s) e)
          (if (map? coll)
            (first (first s))
            idx)
          (recur (next s) (inc idx)))))))


(pos 3 [:a 1 :b 2 :c 3 :d 4])

(pos :foo [:a 1 :b 2 :c 3 :d 4])

(pos 3 {:a 1 :b 2 :c 3 :d 4})

(pos \3 ":a 1 :b 2 :c 3 :d 4")


;; This first implementation works, but it's messy and it shouldn't
;; have to treat maps and other collections differently.


;; How about we create a helper function that generates a unified format
;; across collection types

(defn index [coll]
  (cond
   (map? coll) (seq coll)
   (set? coll) (map vector coll coll)
   :else (map vector (iterate inc 0) coll)))

;; My first crack at pos

(defn pos [e coll]
  (let [icoll (index coll)]
  (loop [s icoll]
    (when (seq s)
      (if (= (second (first s)) e)
        (first (first s))
        (recur (next s)))))))

(pos 3 [:a 1 :b 2 :c 3 :d 4])

(pos :foo [:a 1 :b 2 :c 3 :d 4])

(pos 3 {:a 1 :b 2 :c 3 :d 4})

(pos \3 ":a 1 :b 2 :c 3 :d 4")

;; The books solution:

(defn pos [e coll]
  (for [[i v] (index coll) :when (= e v)] i))


(pos 3 [:a 1 :b 2 :c 3 :d 4])

(pos :foo [:a 1 :b 2 :c 3 :d 4])

(pos 3 {:a 1 :b 2 :c 3 :d 4})

(pos \3 ":a 1 :b 2 :c 3 :d 4")


;; However, it would be cool if pos could take a predicate:

(defn pos [pred coll]
  (for [[i v] (index coll) :when (pred v)] i))


(pos #{3 4} {:a 1 :b 2 :c 3 :d 4})

(pos even? [2 3 6 7])