(ns tweets.utils-test
  (:require [tweets.utils :as utils]
            [clojure.test :refer [deftest is run-tests]]))

(def ok-response {:error nil
                  :status 200
                  :body "{\"answer\": 42}"})

(def error-response {:error "{\"error\": \"fatal\"}"
                     :status 500
                     :body nil})

(deftest str-to-json
  (is (= (utils/str->json ok-response) {:answer 42}))
  (is (= (utils/str->json error-response) '())))

(deftest print-debug
  (is (= (utils/debug ok-response) ok-response)))

(run-tests)

