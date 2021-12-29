(ns tweets.utils
  (:require
   [clojure.data.json :as json]))

(defn str->json [{:keys [body error]}]
  (if error
    (do
      (println "Failed, exception: " error)
      '())
    (json/read-str body :key-fn keyword)))

(defn debug [whatever]
  (println whatever)
  whatever)
