(defproject tweets "0.1.0-SNAPSHOT"
  :description "Trying to use Clojure for Tweets"
  :url "https://twitter.com/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "2.4.0"]
                 [http-kit "2.6.0-alpha1"]]
  :main ^:skip-aot tweets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
