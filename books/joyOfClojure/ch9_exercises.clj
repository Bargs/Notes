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
