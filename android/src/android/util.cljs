(ns android.util)

(defn inner-text [html]
  (-> (js/DOMParser.)
      (.parseFromString html "text/html")
      .-body
      .-textContent))
