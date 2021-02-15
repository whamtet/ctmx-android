(ns frontend.press)

(def pressTimeout)

(defn onpress [elt f]
  (set! (.-onmousedown elt) #(set! pressTimeout (js/setTimeout f 500)))
  (set! (.-onmouseup elt) #(js/clearTimeout pressTimeout)))
