(defproject beckon "0.2.0-SNAPSHOT"
  :description "Handle POSIX signals in Clojure with style and grace."
  :url "https://github.com/hyPiRion/beckon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :profiles {:dev {:plugins [[codox "0.6.4"]]
                   :codox {:sources ["src/clojure"]
                           :output-dir "codox"}}})
