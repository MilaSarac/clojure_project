(ns ballet-school
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.pprint :as pprint])
  (:import [java.util ArrayList]
           [weka.core Attribute DenseInstance Instances]
           [weka.classifiers.trees J48]))

;;database connection

(def database-config
  {:dbtype "mysql"
   :dbname "ballet_school"
   :host "localhost"
   :port 3308
   :user "root"
   :password (or (System/getenv "DB_PASSWORD") "")})

(def datasource
  (jdbc/get-datasource database-config))

(defn check-database-connection []
  (jdbc/execute-one!
    datasource
    ["SELECT 1 AS connection_ok"]
    {:builder-fn rs/as-unqualified-lower-maps}))


;;loading data from database

(defn load-dancers []
  (jdbc/execute!
    datasource
    ["SELECT
       d.id AS id,
       d.name AS name,
       a.difficulties AS difficulties,
       a.progress AS progress,
       a.absences AS absences
     FROM dancers d
     JOIN assessments a ON d.id = a.dancer_id
     WHERE a.id = (
       SELECT MAX(a2.id)
       FROM assessments a2
       WHERE a2.dancer_id = d.id
     )
     ORDER BY d.id"]
    {:builder-fn rs/as-unqualified-lower-maps}))


(defn load-training-examples []
  (jdbc/execute!
    datasource
    ["SELECT
       a.difficulties AS difficulties,
       a.progress AS progress,
       a.absences AS absences,
       t.needs_support AS needs_support
     FROM training_examples t
     JOIN assessments a ON t.assessment_id = a.id
     ORDER BY t.id"]
    {:builder-fn rs/as-unqualified-lower-maps}))



(defn has-high-difficulties? [dancer]
  (>= (:difficulties dancer) 2))


(defn has-low-progress? [dancer]
  (<= (:progress dancer) 1))


(defn has-frequent-absences? [dancer]
  (>= (:absences dancer) 3))


(defn collect-observations [dancer]
  (cond-> []
          (has-high-difficulties? dancer)
          (conj "High level of difficulties")

          (has-low-progress? dancer)
          (conj "Limited progress")

          (has-frequent-absences? dancer)
          (conj "Frequent absences")))


;;preparing data

(defn create-attributes []
  (let [attributes (ArrayList.)
        class-values (ArrayList.)]

    (.add attributes (Attribute. "difficulties"))
    (.add attributes (Attribute. "progress"))
    (.add attributes (Attribute. "absences"))

    (.add class-values "no")
    (.add class-values "yes")

    (.add attributes
          (Attribute. "needs_support" class-values))

    attributes))


(defn create-training-instance [dataset example]
  (let [instance
        (DenseInstance.
          1.0
          (double-array
            [(double (:difficulties example))
             (double (:progress example))
             (double (:absences example))
             (if (zero? (:needs_support example))
               0.0
               1.0)]))]

    (.setDataset instance dataset)
    instance))


(defn create-training-dataset [examples]
  (let [dataset
        (Instances.
          "ballet-support"
          (create-attributes)
          (count examples))]

    (.setClassIndex dataset 3)

    (doseq [example examples]
      (.add dataset
            (create-training-instance dataset example)))

    dataset))
