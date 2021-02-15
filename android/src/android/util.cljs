(ns android.util
  (:require
    [clojure.walk :as walk]))

(def to-json #(-> % clj->js js/JSON.stringify))
(def from-json #(-> % js/JSON.parse js->clj walk/keywordize-keys))

(defn inner-text [html]
  (-> (js/DOMParser.)
      (.parseFromString html "text/html")
      .-body
      .-textContent))

(def loading
  [:img#spinner {:src "loading.gif"}])
