(ns tweets.core
  (:require [org.httpkit.client :as http]
            [tweets.utils :as utils]
            [org.httpkit.sni-client :as sni-client]))

;; Change default client for your whole application:
(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(def locations {:de "23424829"
                :world "1"})

(def options {:timeout 1000
              :keepalive 30000
              :headers {"Authorization" (str "Bearer " (System/getenv "TWITTER_TOKEN"))}})

(defn get-trends-for [location]
  (let [url (str "https://api.twitter.com/1.1/trends/place.json?id=" location)]
    (http/get url options utils/str->json)))

(defn search-tweets [query]
  (http/get
   "https://api.twitter.com/2/tweets/search/recent"
   (assoc options :query-params {"query" (str query " -is:retweet")
                                 "max_results" 100
                                 "tweet.fields" "public_metrics"})
   utils/str->json))

(defn filter-top-5 [data]
  (->> data
       first
       :trends
       (map :name)
       distinct
       (take 5)))

(defn prepare-trends []
  (let [futures (map get-trends-for (vals locations))
        responses (map deref futures)
        filtered (map filter-top-5 responses)]
    (apply concat filtered)))

(defn filter-tweets [data]
  (->> data
       :data
       (map #(assoc % :metrics (apply + (vals (:public_metrics %)))))
       (sort-by :metrics >)
       (map (fn [tweet] {:url (str "https://twitter.com/i/web/status/" (:id tweet))
                         :text (:text tweet)}))
       (take 3)))

(defn prepare-tweets [queries]
  (let [futures (map search-tweets queries)
        responses (map deref futures)
        tweets (map filter-tweets responses)]
    tweets))

(defn -main []
  (let [queries (prepare-trends)
        tweets (prepare-tweets queries)]
    (apply concat tweets)))

(-main)

