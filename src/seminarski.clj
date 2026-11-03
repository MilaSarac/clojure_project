(ns seminarski)

(def dancers
  [{:name "Ana" :difficulties 3 :progress 1 :absences 1}
   {:name "Mina" :difficulties 2 :progress 0 :absences 4}
   {:name "Sara" :difficulties 1 :progress 3 :absences 2}
   {:name "Jovana" :difficulties 3 :progress 2 :absences 3}
   {:name "Lena" :difficulties 1 :progress 2 :absences 0}])


(defn has-difficulties? [dancer]
  (>= (:difficulties dancer) 2))

(defn has-low-progress? [dancer]
  (<= (:progress dancer) 1))

(defn has-many-absences? [dancer]
  (>= (:absences dancer) 3))


(defn needs-support? [dancer]
  (and (has-difficulties? dancer)
       (or (has-low-progress? dancer)
           (has-many-absences? dancer))))

(defn analyze-dancer [dancer]
  (let [initial-observations
        (if (has-difficulties? dancer)
          ["Izrazene poteskoce"]
          [])

        progress-observations
        (if (has-low-progress? dancer)
          (conj initial-observations "Slab napredak")
          initial-observations)

        all-observations
        (if (has-many-absences? dancer)
          (conj progress-observations "Vise izostanaka")
          progress-observations)]

    (assoc dancer
      :observations all-observations
      :needs-support? (needs-support? dancer))))


(def analyzed-dancers
  (map analyze-dancer dancers))

(def dancers-needing-support
  (filter :needs-support? analyzed-dancers))


(println "Analiza polaznica:" analyzed-dancers)

(println "Polaznice kojima je potrebna dodatna podrska:"
         (map :name dancers-needing-support))

(println "Broj polaznica za dodatnu analizu:"
         (count dancers-needing-support))