(ns android.storage
  (:require [alandipert.storage-atom :refer [local-storage]]))

(def password (local-storage (atom "") :password))

(defn get-password []
  @password)

(defn set-password [p]
  (reset! password p))
