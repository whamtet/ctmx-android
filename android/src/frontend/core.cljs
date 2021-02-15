(ns frontend.core
  (:require
    [android.interop :as interop]
    [frontend.press :as press])
  (:refer-clojure :exclude [delay]))

(defn activate-images []
  (println "activating images")
  (doseq [img (js/document.getElementsByTagName "img")]
    (press/onpress img #(interop/android-promise "share" (.-src img)))))

(defn after-settle []
  (when (js/document.getElementById "individual-email")
    (activate-images)))

(defn main []
  (js/htmx.on "htmx:afterSettle" after-settle))
