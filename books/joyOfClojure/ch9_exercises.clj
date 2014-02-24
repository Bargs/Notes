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


;; A new multimethod with which we'll demonstrate ad hoc inheritance.
(defmulti home :os)
(defmethod home ::unix [m] (get m :home))

(home unix)

;; As is, this will thrown an error because the multimethod doesn't
;; support ::osx
(home osx)

;; We'll tell Clojure that "::osx is a ::unix".
(derive ::osx ::unix)

;; And now our multimethod supports ::osx using the function for ::unix
(home osx)

;; We can see the derivation hierarchy using the `parents`, `ancestors`,
;; `descendants` and `isa?` functions.
(parents ::osx)
(ancestors ::osx)
(descendants ::unix)
(isa? ::osx ::unix)
(isa? ::unix ::osx)


;; If a value has two ancestors and they're both used as dispatch values
;; for the same multimethod, you can run into conflicts with multiple
;; inheritance.
(derive ::osx ::bsd)
(defmethod home ::bsd [m] "/home")

;; Will thrown an error because the ::unix and ::bsd methods both match
(home osx)

;; We can fix this by defining a preferred method
(prefer-method home ::unix ::bsd)

(home osx)


;; Clojure maintains derivation relationships in a global structure. If you
;; want to avoid dealing with global state, you can define your own
;; derivation hierarchy. Once you have your own hierarchy you can pass it
;; to defmulti to specify the derivation context it should use instead of the
;; global one.
(def isBSD (-> (make-hierarchy)
               (derive ::osx ::bsd)))

(def isUnix (-> (make-hierarchy)
               (derive ::osx ::unix)))

(defmulti bsdHome :os :hierarchy #'isBSD)
(defmethod bsdHome ::unix [m] (get m :home))
(defmethod bsdHome ::bsd [m] "/home")

(defmulti unixHome :os :hierarchy #'isUnix)
(defmethod unixHome ::unix [m] (get m :home))
(defmethod unixHome ::bsd [m] "/home")

(bsdHome osx)
(unixHome osx)

;; Multimethods can use arbitrary dispatch functions, not just keywords
(defmulti compile-cmd (juxt :os compiler))

(defmethod compile-cmd [::osx "clang"] [m]
  (str "/usr/bin/" (get m :llvm-compiler)))

(defmethod compile-cmd :default [m]
  (str "Unsure where to locate " (get m :c-compiler)))

(compile-cmd osx)

(compile-cmd unix)


;; Types, Protocols, and Records

;; Records

;; Building a persistent binary tree from records instead of maps

(defrecord TreeNode [val l r])

(TreeNode. 5 nil nil)

;; Our tree builder is changed only slightly to use the record's constructor
;; instead of creating regular maps
(defn xconj [t v]
  (cond
   (nil? t) (TreeNode. v nil nil)
   (< v (:val t)) (TreeNode. (:val t) (xconj (:l t) v) (:r t))
   :else (TreeNode. (:val t) (:l t) (xconj (:r t) v))))

;; Notice that the implementation of xseq has not changed at all since keywords
;; can look themselves up in records just as they do with maps
(defn xseq [t]
  (when t
    (concat (xseq (:l t)) [(:val t)] (xseq (:r t)))))

(def sample-tree (reduce xconj nil [3 5 2 4 6]))
(xseq sample-tree)


;; Protocols

;; Our protocol for First In X Out (objects like stacks and queues)
(defprotocol FIXO
  (fixo-push [fixo value])
  (fixo-pop [fixo])
  (fixo-peek [fixo]))


;; extend-type takes a class or interface to be extended and then
;; one or more blocks that start with the name of a protocol to be extended.
;; Each block contains one or more functions from that protocol to implement
;; Here we're extending TreeNode to implement the fixo-push function from the
;; FIXO protocol.
(extend-type TreeNode
  FIXO
  (fixo-push [node value]
             (xconj node value)))

(xseq (fixo-push sample-tree 5/2))


;; Protocols are useful because they allow for polymorphism. We already
;; implemented fixo-push for TreeNode, but we could also implement it with
;; vectors.
(extend-type clojure.lang.IPersistentVector
  FIXO
  (fixo-push [vector value]
             (conj vector value)))

(fixo-push [2 3 4 5 6] 5/2)

;; Note that IPersistentVector is an interface, not a concrete class like
;; TreeNode. This means that fixo-push can be used with any class
;; that implements IPersistentVector. Also, fixo-push is now polymorphic,
;; choosing the correct implmenetation depending on whether the first
;; parameter (the "target object" in OOP terms) is a vector or a TreeNode.


;; You can even extend a protocol to nil

;; We get an error trying to use fixo-push on nil right now
(reduce fixo-push nil [3 5 2 4 6 0])

;; But if we extend the protocol to nil, we can have it use the TreeNode implementation
(extend-type nil
  FIXO
  (fixo-push [t v]
             (TreeNode. v nil nil)))

(xseq (reduce fixo-push nil [3 5 2 4 6 0]))


;; Here's a complete implementation of FIXO for TreeNodes and vectors

;; TreeNode's FIXO is a little strange, "first in" doesn't really mean anything.
;; peek and pop just work on the smallest value in the tree.
(extend-type TreeNode
  FIXO
  (fixo-push [node value]
             (xconj node value))
  (fixo-peek [node]
             (if (:l node)
               (recur (:l node))
               (:val node)))
  (fixo-pop [node]
            (if (:l node)
              (TreeNode. (:val node) (fixo-pop (:l node)) (:r node))
              (:r node))))

;; Vector's FIXO works like a stack, FILO
(extend-type clojure.lang.IPersistentVector
  FIXO
  (fixo-push [vector value]
             (conj vector value))
  (fixo-peek [vector]
             (peek vector))
  (fixo-pop [vector]
            (pop vector)))

(fixo-peek (fixo-pop (reduce fixo-push nil [3 5 2 4 6])))
(fixo-peek (fixo-pop (reduce fixo-push [] [3 5 2 4 6])))


;; reify combines the power of protocols and closures
(defn fixed-fixo
  ([limit] (fixed-fixo limit []))
  ([limit vector]
   (reify FIXO
     (fixo-push [this value]
                (if (< (count vector) limit)
                  (fixed-fixo limit (conj vector value))
                  this))
     (fixo-peek [_]
                (peek vector))
     (fixo-pop [_]
               (pop vector)))))

;; With a limit of three, fixo-peek returns the third item we push onto the stack
(fixo-peek (reduce fixo-push (fixed-fixo 3) [1 2 3]))

;; If we try to push a fourth item, it doesn't get added
(fixo-peek (reduce fixo-push (fixed-fixo 3) [1 2 3 4]))

;; An extra item can only be added if another is popped first
(fixo-peek (fixo-push (fixo-pop (reduce fixo-push (fixed-fixo 3) [1 2 3])) 4))


;; Implementing protocol methods directly in the defrecord form

(defrecord TreeNode [val l r]
  FIXO
  (fixo-push [t v]
             (if (< v val)
               (TreeNode. val (fixo-push l v) r)
               (TreeNode. val l (fixo-push r v))))
  (fixo-peek [t]
             (if l
               (fixo-peek l)
               val))
  (fixo-pop [t]
            (if l
              (TreeNode. val (fixo-pop l) r)
              r)))


(def sample-tree2 (reduce fixo-push (TreeNode. 3 nil nil) [5 2 4 6]))

(xseq sample-tree2)


;; Defining our FIXO TreeNode with deftype so that we can override
;; the methods that Clojure normally implements automatically for records

(deftype TreeNode [val l r]
  FIXO
  (fixo-push [_ v]
             (if (< v val)
               (TreeNode. val (fixo-push l v) r)
               (TreeNode. val l (fixo-push r v))))
  (fixo-peek [_]
             (if l
               (fixo-peek l)
               val))
  (fixo-pop [_]
            (if l
              (TreeNode. val (fixo-pop l) r)
              r))

  clojure.lang.IPersistentStack
  (cons [this v] (fixo-push this v))
  (peek [this] (fixo-peek this))
  (pop [this] (fixo-pop this))

  clojure.lang.Seqable
  (seq [t]
       (concat (seq l) [val] (seq r))))

(extend-type nil
  FIXO
  (fixo-push [t v]
             (TreeNode. v nil nil)))

(def sample-tree2 (into (TreeNode. 3 nil nil) [5 2 4 6]))
(seq sample-tree2)
