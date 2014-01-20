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