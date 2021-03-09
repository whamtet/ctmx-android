(ns android.storage
  (:require [alandipert.storage-atom :refer [local-storage]]))

(def passwords (local-storage (atom {}) :passwords))

(defn get-password [k]
  (@passwords k))

(defn set-password [k p]
  (swap! passwords assoc k p))
