(require '[cljs.build.api :as b])

(def prefix "../app/src/main/assets/www/")

(b/watch "src"
         {:main 'android.core
          :output-to (str prefix "out/android.js")
          :output-dir (str prefix "out")
          :asset-path "out"})
