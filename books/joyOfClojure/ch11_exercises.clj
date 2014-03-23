;; Chapter 11 exercises

(time (let [x (future (do (Thread/sleep 5000) (+ 41 1)))]
        [@x @x]))
