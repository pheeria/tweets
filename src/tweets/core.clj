(ns tweets.core
  (:require org.httpkit.client
            [tweets.twitter :as twitter]
            [org.httpkit.sni-client :as sni-client]))

;; Change default client for your whole application:
(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(defn -main []
  (twitter/fetch-tweets))
