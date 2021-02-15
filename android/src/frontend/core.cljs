(ns frontend.core
  (:refer-clojure :exclude [delay]))

(defn activate-images []
  (println "activating images")
  (doseq [img (js/document.getElementsByTagName "img")]
    (js/console.log img)))

(defn after-settle []
  (when (js/document.getElementById "individual-email")
    (activate-images)))

(defn main []
  (js/htmx.on "htmx:afterSettle" after-settle))
