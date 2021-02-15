(ns android.core
  (:require
    android.interop
    [ctmx.intercept :as intercept]
    ctmx.rt
    hiccups.runtime)
  (:require-macros
    [android.interop :as interop]
    [ctmx.core :as ctmx]
    [hiccups.core :as hiccups]))

(enable-console-print!)

(defn- truncate [text]
  text)

(defn- email-div [i {:keys [from subject text]}]
  [:div.row
   [:div.col from]
   [:div.col subject]
   [:div.col (truncate text)]])

(ctmx/defcomponent email-panel [req emails]
  [:div {:id id}
   (map-indexed email-div emails)])

(ctmx/defcomponent ^:endpoint panel [req username password]
  (ctmx/with-req req
    (if post?
      (-> (interop/android-promise emails {:username username :password password})
          (.then (fn [emails]
                   [:div {:id id}
                    (email-panel req emails)])))
      [:form.mt-4 {:id id :hx-post "panel"}
       [:div.input-group.mb-2
        [:input.form-control
         {:type "text" :placeholder "Username" :name "username" :value username :required true}]
        [:span.input-group-text "@gmail.com"]]
       [:div.input-group.mb-2
        [:input.form-control
         {:type "password" :placeholder "Password" :name "password" :value password :required true}]]
       [:div.input-group
        [:input.form-control {:type "submit" :value "Login"}]]])))

(def req {:params {}})

(ctmx/defstatic main []
  (set! js/document.body.innerHTML
        (hiccups/html
          [:div.container {:hx-ext "intercept"}
           (panel req)])))

(intercept/set-responses!
  (ctmx/metas main))

(set! (.-defaultSettleDelay js/htmx.config) 0)
(set! (.-defaultSwapStyle js/htmx.config) "outerHTML")
(main)
