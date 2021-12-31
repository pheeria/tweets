(ns tweets.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [org.httpkit.server :refer [run-server]]
            [tweets.twitter :as twitter]
            [tweets.config :as config]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
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

(defn telegram-updates [req]
  (with-open [r (io/reader (:body req) :encoding "UTF-8")]
    (let [url (str "https://api.telegram.org/bot" config/telegram-token "/sendMessage")
          input (:message (json/read-str (slurp r) :key-fn keyword))
          message {:chat_id (get input [:from :id]) :text (str "You said ->> " (:text input))}
          response @(http/post url {:body (json/write-str message)
                                    :headers {"Content-Type" "application/json"}})]
      (println response))))

(defn app [req]
  (let [uri (:uri req)
        telegram (str "/" config/telegram-token)
        handler (condp = uri
                  "/meaning" meaning
                  "/tweets" tweets
                  telegram telegram-updates
                  not-found)]
    (handler req)))

(defn stop-server []
  (when (some? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (let [url (str "https://api.telegram.org/bot" config/telegram-token "/setWebhook")
        query {:url (str "https://clj-tweets.herokuapp.com/" config/telegram-token)}]
    (http/post url {:body (json/write-str query) :headers {"Content-Type" "application/json"}})
    (reset! server (run-server #'app {:port config/port}))))

