;; 12-17-2013
;;
;; Remove Duplicates From A List
;;
;; We have today another exercise from our infinite supply of interview questions:
;;
;; Return a list containing only one copy of any duplicates in an input list,
;; with items in the output in the same order as their first appearance in the input.
;;
;; Your task is to answer the interview question given above;
;; you must provide at least two different solutions. When you are finished,
;; you are welcome to read or run a suggested solution, or to post your own
;; solution or discuss the exercise in the comments below.


(def testData '(1 2 3 2 10 1 5))
(def emptyList '())
(def noDupes '(1 2 3 4 5 6 7 8 9 10))
(def allDupes '(1 1 1 1 1 1 1 1 1 1))


;; Solution using lists

(defn deDupe [items]
  (if (not (seq items))
    '()
    (cons (first items) (deDupe (remove #(= (first items) %) items)))))

(deDupe testData)
(deDupe emptyList)
(deDupe noDupes)
(deDupe allDupes)

