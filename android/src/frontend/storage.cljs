(ns frontend.storage)

(def data (atom []))

(defn email-data []
  @data)
(defn set-email-data [e]
  (reset! data e))
