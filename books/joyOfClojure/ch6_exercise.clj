(defn rand-ints [n]
  (take n (repeatedly #(rand-int n))))

(rand-ints 10)


;;
;; A helper function that does the bulk of the work for qsort
;;
(defn sort-parts [work]
  (lazy-seq
   (loop [[part & parts] work]
     (if-let [[pivot & xs] (seq part)]
       (let [smaller? #(< % pivot)]
         (recur (list*
           (filter smaller? xs)
           pivot
           (remove smaller? xs)
           parts)))
       (when-let [[x & parts] parts]
         (cons x (sort-parts parts)))))))


;;
;; The entrypoint for our new quicksort
;;
(defn qsort [xs]
  (sort-parts (list xs)))


(qsort [2 1 4 3])

(qsort (rand-ints 20))