(ns frontend.core
  (:require
    [frontend.press :as press])
  (:refer-clojure :exclude [delay]))

(defn activate-images []
  (println "activating images")
  (doseq [img (js/document.getElementsByTagName "img")]
    (press/onpress img #(println "pressed"))))

(defn after-settle []
  (when (js/document.getElementById "individual-email")
    (activate-images)))

(defn main []
  (js/htmx.on "htmx:afterSettle" after-settle))
