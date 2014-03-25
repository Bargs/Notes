;; Chapter 11 exercises

(time (let [x (future (do (Thread/sleep 5000) (+ 41 1)))]
        [@x @x]))


;; Creating a Twitter feed parser using futures

(ns joy.futures
  (:require [clojure [xml :as xml]]
           [clojure [zip :as zip]])
  (:import (java.util.regex Pattern)))

(defn feed->zipper [uri-str]
  (->> (xml/parse uri-str)
       zip/xml-zip))
