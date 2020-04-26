(defproject telegram-notifier "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]]
  :mirrors {"central" {:name "central"
              :url "https://repo1.maven.org/maven2"}}

  :plugins [[lein-environ "1.1.0"]]

  :main telegram-notifier.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-cljfmt "0.5.7"]]}}
  :repl-options {:init-ns telegram-notifier.core}
  :jar-name "telegram-notifier.jar"
  :uberjar-name "telegram-notifier-standalone.jar"
  )
