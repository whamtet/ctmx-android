(ns frontend.core)

(defn activate-images []
  (doseq [img (js/document.getElementsByTagName "img")]
    (js/console.log img)))
