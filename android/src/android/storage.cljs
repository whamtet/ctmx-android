(ns android.storage
  (:require [alandipert.storage-atom :refer [local-storage]]))

(def passwords (local-storage (atom {}) :passwords))
(def last-email (local-storage (atom "") :last-email))

(defn get-password [k]
  (reset! last-email k)
  (@passwords k))
(defn get-init []
  (let [last @last-email]
    [last (@passwords last "")]))

(defn set-password [k p]
  (swap! passwords assoc k p))
