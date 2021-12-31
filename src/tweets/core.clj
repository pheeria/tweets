(ns tweets.core
  (:gen-class)
  (:require org.httpkit.client
            [org.httpkit.server :refer [run-server]]
            [tweets.twitter :as twitter]
            [tweets.config :as config]
            [clojure.data.json :as json]
            [org.httpkit.sni-client :as sni-client]))

;; Change default client for your whole application:
(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(defonce server (atom nil))

(defn not-found [_]
  {:status 404
   :body "Not Found"})

(defn meaning [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "{\"answer\": 42}"})

(defn tweets [_]
  (let [response (twitter/fetch-tweets)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str response)}))

(defn app [req]
  (let [uri (:uri req)
        handler (case uri
                  "/meaning" meaning
                  "/tweets" tweets
                  not-found)]
    (handler req)))

(defn stop-server []
  (when (some? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (reset! server (run-server #'app {:port config/port})))
