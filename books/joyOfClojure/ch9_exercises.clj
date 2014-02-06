(in-ns 'joy.ns)

(def authors ["Chouser"])

(in-ns 'your.ns)

;; Pull in definitions from previous namespace
(clojure.core/refer 'joy.ns)

joy.ns/authors

(in-ns 'joy.ns)
(def authors ["Chouser" "Fogus"])

(in-ns 'your.ns)

;; Note that the value returned is the current of the var bound
;; to the authors symbol, not the value that was present when
;; the var was "referred".
joy.ns/authors


;; Examples of using create-ns

;; create-ns imports java classes but not clojure.core
(def b (create-ns 'bonobo))
b
((ns-map b) 'String)


;; You can add bindings with `intern`
(intern b 'x 9)

bonobo/x

;; This works for any symbol, including those bound in clojure.core
(intern b 'reduce clojure.core/reduce)

(intern b '+ clojure.core/+)

(in-ns 'bonobo)
(reduce + [1 2 3 4 5])


;; We can remove mappings using `ns-unmap`

(in-ns 'user)
(get (ns-map 'bonobo) 'reduce)

(ns-unmap 'bonobo 'reduce)

(get (ns-map 'bonobo) 'reduce)

;; And we can remove the entire namespace with `remove-ns`

(remove-ns 'bonobo)

(all-ns)
