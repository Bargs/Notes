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


;; 8.2 Defining Control Structures

;; 8.2.1 Defining structures without syntax-quote

;; Macro that takes a list of expressions (paired) and executes
;; every even expression as long as the odd expression before it
;; is truthy.
;;
;; Works by building up nested lists of whens.
;;
(defmacro do-until [& clauses]
  (when clauses
    (list 'clojure.core/when (first clauses)
          (if (next clauses)
            (second clauses)
            (throw (IllegalArgumentException.
                    "do-until requires an even number of forms")))
          (cons 'do-until (nnext clauses)))))

(do-until
 (even? 2) (println "Even")
 (odd? 3) (println "Odd")
 (zero? 1) (println "You never see me")
 :lollipop (println "Truthy thing"))

(macroexpand-1 '(do-until true (prn 1) false (prn 2)))

(require '[clojure.walk :as walk])

;; do-until recursively expands into a series of when calls
;; which themselves expand into a series of if expressions
;;(because when itself is a macro defined in terms of the built-in if ):
(walk/macroexpand-all '(do-until true (prn 1 ) false (prn 2)))


;; 8.2.2 Defining structures using syntax-quote and unquote

(defmacro unless [condition & body]
  `(if (not ~condition)
     (do ~@body)))

(unless true "nope")

(unless false "yep")

(macroexpand-1 '(unless (even? 3) (odd? 3)))


;; See how things can break if you forget to unquote condition

;; condition is treated as any other symbol in a syntax-quote instead
;; of being evaluated.
(macroexpand `(if (not condition) "got it"))

;; So the macro will return a form that includes the condition symbol
;; instead of its local value from the macro application. When the new
;; form is evaluated if will attempt to find the value of condition.
(eval `(if (not condition) "got it"))

;; This could cause subtle problems if condition is bound elsewhere in
;; the application
(def condition false)
(eval `(if (not condition) "got it"))


;; 8.3 Macros Combining Forms

;; Say you wanted to call a function every time certain vars are rebound to different values
;; This would require the same boilerplate code using the `add-watch` function every time
;; you define a new var. Instead you could use a macro to handle this boilerplate for you.
(defmacro def-watched [name & value]
  `(do
     (def ~name ~@value)
     (add-watch (var ~name)
                :re-bind
                (fn [~'key ~'r old# new#]
                  (println old# " -> " new#)))))

(def-watched x (* 12 12))

;; Now the watch function will be called and print our string when the value of x is rebound
(def x 0)


;; 8.4 Using Macros to Change Forms

;; Sometimes the best way to approach a problem is to write
;; example code assuming you have all the functions and macros you need
;; and then implement the ones that don't exist yet.

;; We'll write some code to describe the "simple domain of the ongoing
;; struggle between humans and monsters."

;; Here we'll describe the things and logical groupings of our domain
;;
;; Man versus monster
;;   People
;;     Men (humans)
;;       Name
;;       Have beards?
;;   Monsters
;;     Chupacabra
;;       Eats goats?


;; The above outline could be expressed as a Clojure form if we assume the
;; existence of some macros and functions that we'll create.
;;
;; (domain man-vs-monster
;;         (grouping people
;;                   (Human "A stock human")
;;
;;                   (Man (isa Human)
;;                        "A man, baby"
;;                        [name]
;;                        [has-beard?]))
;;         (grouping monsters
;;                   (Chupacabra
;;                    "A fierce, yet elusive creature"
;;                    [eats-goats?])))

;; Our goal in this section will be to create macros that will transform this simple
;; representation into one that is more programmatically useful.

;; We could represent the above structure as a tree of generic nodes:

{:tag <node form>,  ;; Domain, grouping, etc
 :attrs {},  ;; e.g. :name people
 :content [<nodes>]} ;; e.g. properties

;; While this format doesn't fit nicely into our problem domain, it does offer advantages
;; in that it's a tree, it's composed of simple types, and it's recognizable to some
;; existing libraries. Using macros, we'll be able to transform our "DSL" into this
;; more program friendly form.
