(ns android.interop
  (:require
    [clojure.walk :as walk])
  (:refer-clojure :exclude [resolve]))

(def resolutions (atom {}))

(def to-json #(-> % clj->js js/JSON.stringify))
(def from-json #(-> % js/JSON.parse js->clj walk/keywordize-keys))

(set! js/resolve
      (fn [cb]
        ((-> @resolutions (get cb) first) (from-json (js/androidBridge.getResult cb)))
        (swap! resolutions dissoc cb)))

(set! js/reject
      (fn [cb]
        ((-> @resolutions (get cb) second) (js/androidBridge.getResult cb))
        (swap! resolutions dissoc cb)))

(defn android-promise [fName data]
  (let [cb (str (gensym))]
    (js/Promise.
      (fn [res rej]
        (swap! resolutions assoc cb [res rej])
        (js/androidBridge.invoke fName cb (to-json data))))))
