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


;; Multimethods and the universal design pattern (udp)

;;We're going to need a get function, so exclude clojure.core's
(ns joy.udp
  (:refer-clojure :exclude [get]))

;; The udp is built on five funtions: beget, get, put, has?, and forget.
;; For our subset we'll just need the first three.

;; Takes a map and associates its prototype reference to
;; another map. Ensures the map has a properly formatted
;; prototype entry as specified by the UDP.
(defn beget [this proto]
  (assoc this ::prototype proto))

;; Note, double colons just fully qualify a keyword with your
;; current namespace. So ::prototype is :joy.udp/prototype


;; get will search through a map and its prototypes for a key.
;; Returns nil if the key is not found.
(defn get [m k]
  (when m
    (if-let [[_ v] (find m k)]
      v
      (recur (::prototype m) k))))

(get (beget {:sub 0} {:super 1})
     :super)

;; put adds a key and value to a map, overwriting a matching one
;; if it exists. Works the same as clojure's assoc, so this is easy.
(def put assoc)


;; A simple example usage of our new methods
(def cat {:likes-dogs true, :ocd-bathing true})
(def morris (beget {:likes-9lives true} cat))
(def post-traumatic-morris (beget {:likes-dogs nil} morris))

(get cat :likes-dogs)

(get morris :likes-dogs)

(get post-traumatic-morris :likes-dogs)


;; A multimethod. Will dispatch the call to the function based
;; on the value of the :os key in the map argument passed in
(defmulti compiler :os)
(defmethod compiler ::unix [m] (get m :c-compiler))
(defmethod compiler ::osx [m] (get m :llvm-compiler))


;; Usage of our multimethod
(def clone (partial beget {}))
(def unix {:os ::unix, :c-compiler "cc", :home "/home", :dev "/dev"})
(def osx (->
          (clone unix)
          (put :os ::osx)
          (put :llvm-compiler "clang")
          (put :home "/Users")))

(compiler unix)

(compiler osx)
