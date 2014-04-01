;; Chapter 11 exercises

(time (let [x (future (do (Thread/sleep 5000) (+ 41 1)))]
        [@x @x]))


;; Creating a Twitter feed parser using futures

(ns joy.futures
  (:require [clojure [xml :as xml]]
           [clojure [zip :as zip]])
  (:import (java.util.regex Pattern)))

;; Parse the xml and convert it into a zipper data structure
(defn feed->zipper [uri-str]
  (->> (xml/parse uri-str)
       zip/xml-zip))

;; normalize the feeds so they look the same regardless of whether it's rss or atom
(defn normalize [feed]
  (if (= :feed (:tag (first feed)))
    feed
    (zip/down feed)))

(defn feed-children [uri-str]
  (->> uri-str
       feed->zipper
       normalize
       zip/children
       (filter (comp #{:item :entry} :tag))))

;; get the title text
(defn title [entry]
  (some->> entry
           :content
           (some #(when (= :title (:tag %)) %))
           :content
           first))

;; Count the occurances of txt in result of extractor's application to feed
(defn count-text-task [extractor txt feed]
  (let [items (feed-children feed)
        re    (Pattern/compile (str "(?i)" txt))]
    (->> items
         (map extractor)
         (mapcat #(re-seq re %))
         count)))

;; Let 'er rip
(count-text-task
 title
 "Erlang"
 "http://feeds.feedburner.com/ElixirLang")

(count-text-task
 title
 "Elixir"
 "http://feeds.feedburner.com/ElixirLang")

(def feeds #{"http://feeds.feedburner.com/ElixirLang"
             "http://blog.fogus.me/feed/"})

;; Looks good, but what if we want to process multiple feeds? We don't want to wait
;; for each feed to process before starting the next one. We'll use futures to run
;; the processing of each feed in parallel.
(let [results (for [feed feeds]
                (future
                  (count-text-task title "Elixir" feed)))]
  (reduce + (map deref results)))


(defmacro as-futures [[a args] & body]
  (let [parts (partition-by #{'=>} body)
        [acts _ [res]] (partition-by #{:as} (first parts))
        [_ _ task] parts]
    `(let [~res (for [~a ~args] (future ~@acts))]
       ~@task)))

(defn occurrences [extractor tag & feeds]
  (as-futures [feed feeds]
              (count-text-task extractor tag feed)
              :as results
              =>
              (reduce + (map deref results))))

(occurrences title "released"
             "http://blog.fogus.me/feed/"
             "http://feeds.feedburner.com/ElixirLang")


;; Promises

;; Defining a utility that will let us throw a bunch of
;; threads at a given function
(ns joy.mutation
  (:import java.util.concurrent.Executors))

(def thread-pool
  (Executors/newFixedThreadPool
   (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(defn dothreads!
  [f & {thread-count :threads
        exec-count :times
        :or {thread-count 1 exec-count 1}}]
  (dotimes
    [t thread-count]
    (.submit thread-pool #(dotimes [_ exec-count] (f)))))


(def x (promise))
(def y (promise))
(def z (promise))

(dothreads! #(deliver z (+ @x @y)))

(dothreads!
 #(do (Thread/sleep 2000) (deliver x 52)))

(dothreads!
 #(do (Thread/sleep 4000) (deliver y 86)))

(time @z)


;; Creating a macro like as-futures for promises
;;
;; Creates a promise for each task passed in and delivers
;; the value for each promise by evaluating the task with
;; dothreads!. Executes body with the collection of promises
;; bound to the symbol passed as the 4th arg in the vector.
(defmacro with-promises [[n tasks _ as] & body]
  (when as
    `(let [tasks# ~tasks
           n# (count tasks#)
           promises# (take n# (repeatedly promise))]
       (dotimes [i# n#]
         (dothreads!
          (fn []
            (deliver (nth promises# i#)
                     ((nth tasks# i#))))))
       (let [~n tasks#
             ~as promises#]
         ~@body))))


;; Use our new macro in a simple test runner
(defrecord TestRun [run passed failed])

(defn pass [] true)

(defn fail [] false)

(defn run-tests [& all-tests]
  (with-promises
    [tests all-tests :as results]
    (into (TestRun. 0 0 0)
          (reduce #(merge-with + %1 %2) {}
                  (for [r results]
                    (if @r
                      {:run 1 :passed 1}
                      {:run 1 :failed 1}))))))

(run-tests pass fail fail fail pass)
