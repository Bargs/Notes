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


