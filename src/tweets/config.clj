(ns tweets.config)

(def twitter-bearer-token (str "Bearer " (System/getenv "TWITTER_TOKEN")))
