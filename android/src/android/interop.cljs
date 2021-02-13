(ns android.interop
  (:require
    [clojure.walk :as walk])
  (:refer-clojure :exclude [resolve]))

(def resolutions (atom {}))

(def to-json #(-> % clj->js js/JSON.stringify))
(def from-json #(-> % js/JSON.parse js->clj walk/keywordize-keys))

(set! js/resolve
      (fn [s val]
        ((-> @resolutions (get s) first) val)
        (swap! resolutions dissoc s)))

(set! js/reject
      (fn [s val]
        ((-> @resolutions (get s) second) val)
        (swap! resolutions dissoc s)))

(defn make-promise [s]
  (js/Promise.
    (fn [res rej]
      (swap! resolutions assoc s [res rej]))))
