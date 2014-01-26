;; Section 8.1.1

;; An eval that allows you to pass in a local context
;; Uses syntax-quote (`), unquote (~), and unquote-splicing (~@)
;; Builds a let with the bindings specified in the ctx map.
(defn contextual-eval [ctx expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
      ~expr)))

(contextual-eval '{a 1, b 2} '(+ a b))

(contextual-eval '{a 1, b 2} '(let [b 1000] (+ a b)))

;; playing around a bit to see how the bindings work
(mapcat (fn [[k v]] [k `'~v]) '{a 1, b 2})

`(let [~@(mapcat (fn [[k v]] [k `'~v]) '{a 1, b 2})])
;; == (clojure.core/let [a (quote 1) b (quote 2)])

;; So the let in the syntax-quoted list passed to eval binds the keys
;; of the ctx map to quoted values for those keys. Quoting of the values
;; is accomplished by the `'~v (which is equivalent to `(quote ~v)).
;; I'm not totally sure why it's necessary to quote the value.
;; The book simply says:
;;      "The bindings created use the interesting `'~v pattern
;;       to garner the value of the built bindings at runtime."
;; Could use some more explanation.



;; An example of dealing with nested syntax quotes. Note the
;; one to one relationship between syntax quotes and unquotes.
(let [x 9, y '(- x)]
  (println `y)
  (println ``y)
  (println ``~y)
  (println ``~~y)
  (contextual-eval {'x 36} ``~~y))
