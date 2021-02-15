(ns android.ui.emails
  (:require
    [android.util :as util]))

(def ^:private limit 80)
(defn- truncate [text]
  (if (-> text .-length (> limit))
    (str (.substring text 0 (- limit 3)) "...")
    text))

(defn- email-div [i {:keys [from subject html]}]
  [:div
   [:div from]
   [:div subject]
   [:div
    [:a
     {:hx-patch "panel"
      :href "javascript: void(0)"
      :hx-vals (util/to-json {:i i})}
     (-> html util/inner-text .trim truncate)]]
   [:hr]])

(defn email-panel [emails]
  [:div
   [:h5 "Emails"]
   (map-indexed email-div emails)])

(defn individual-email [email-data i]
  [:div
   [:button.btn.btn-primary.mt-3
    {:hx-delete "panel"}
    "Back"]
   [:div
    {:dangerouslySetInnerHTML {:__html (-> i email-data :html)}}]])
