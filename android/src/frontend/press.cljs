(ns frontend.press)

(def pressTimeout)

(defn onpress [elt f]
  (.addEventListener elt "touchstart" #(set! pressTimeout (js/setTimeout f 500)))
  (.addEventListener elt "touchend" #(js/clearTimeout pressTimeout)))
