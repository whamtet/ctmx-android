(ns android.interop
  (:require
    [android.util :as util]))

(def resolutions (atom {}))

(set! js/resolve
      (fn [cb]
        ((-> @resolutions (get cb) first) (util/from-json (js/androidBridge.getResult cb)))
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
        (js/androidBridge.invoke fName cb data)))))

(defn android-json [fName data]
  (android-promise fName (util/to-json data)))
