;; Creating a function that dynamically creates other functions

(defn fnth [n]
  (apply comp
         (cons first
               (take (dec n) (repeat rest)))))

((fnth 5) '[a b c d e])

