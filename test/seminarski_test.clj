(ns seminarski_test
  (:require [midje.sweet :refer :all]
            [seminarski :refer :all]))

  (facts "Provera poteškoca polaznica"

         (fact "Polaznica sa 3 poteškoće ima izražene poteškoće"
               (has-difficulties? {:difficulties 3}) => true)

         (fact "Polaznica sa 1 poteškoćom nema izražene poteškoće"
               (has-difficulties? {:difficulties 1}) => false)

         (fact "Nizak napredak se prepoznaje"
               (has-low-progress? {:progress 1}) => true)

         (fact "Dobar napredak se ne smatra niskim"
               (has-low-progress? {:progress 3}) => false)

         (fact "Tri izostanka predstavljaju veći broj izostanaka"
               (has-many-absences? {:absences 3}) => true)

         (fact "Jedan izostanak nije veliki broj izostanaka"
               (has-many-absences? {:absences 1}) => false))

  (facts "Provera potrebe za dodatnom podrškom"

         (fact "Poteškoće i slab napredak znače potrebu za podrškom"
               (needs-support?
                 {:difficulties 3 :progress 1 :absences 1}) => true)

         (fact "Poteškoće i mnogo izostanaka znače potrebu za podrškom"
               (needs-support?
                 {:difficulties 3 :progress 2 :absences 4}) => true)

         (fact "Bez izraženih poteškoća nema potrebe za dodatnom podrškom"
               (needs-support?
                 {:difficulties 1 :progress 3 :absences 0}) => false))

  (facts "Provera analize polaznice"

         (fact "Analiza dodaje informaciju o potrebi za podrškom"
               (:needs-support?
                 (analyze-dancer
                   {:name "Ana"
                    :difficulties 3
                    :progress 1
                    :absences 1})) => true))