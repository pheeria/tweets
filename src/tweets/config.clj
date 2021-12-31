(ns tweets.config)

(def twitter-bearer-token (str "Bearer " (System/getenv "TWITTER_TOKEN")))
(def port (Integer/parseInt
           (or (System/getenv "PORT") "8000")))
