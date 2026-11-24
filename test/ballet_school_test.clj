(ns ballet-school-test
  (:require [midje.sweet :refer :all]
            [ballet-school :as app]))

(fact "Checks high difficulties"
      (app/has-high-difficulties? {:difficulties 3}) => true
      (app/has-high-difficulties? {:difficulties 1}) => false)

(fact "Checks low progress"
      (app/has-low-progress? {:progress 0}) => true
      (app/has-low-progress? {:progress 3}) => false)

(fact "Checks frequent absences"
      (app/has-frequent-absences? {:absences 4}) => true
      (app/has-frequent-absences? {:absences 1}) => false)

(fact "Collects observations"
      (app/collect-observations
        {:difficulties 3
         :progress 0
         :absences 4})
      =>
      ["High level of difficulties"
       "Limited progress"
       "Frequent absences"])

(fact "Returns empty observations"
      (app/collect-observations
        {:difficulties 1
         :progress 3
         :absences 0})
      =>
      [])
