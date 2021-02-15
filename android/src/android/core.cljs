(ns android.core
  (:require
    android.interop
    cljs.reader
    [ctmx.intercept :as intercept]
    ctmx.rt
    hiccups.runtime)
  (:require-macros
    [android.interop :as interop]
    [ctmx.core :as ctmx]
    [hiccups.core :as hiccups]))

(enable-console-print!)

(def ^:private limit 60)
(defn- truncate [text]
  (if (-> text .-length (> limit))
    (str (.substring text 0 (- limit 3)) "...")
    text))

(defn- email-div [i {:keys [from subject text]}]
  [:div
   [:div from]
   [:div subject]
   [:div
    [:a {:hx-patch "panel" :href "javascript: void(0)"}
     (truncate text)]]
   [:hr]])

(ctmx/defcomponent email-panel [req emails]
  [:div {:id id}
   [:h5 "Emails"]
   (map-indexed email-div emails)])

(defn hidden [name value]
  [:input {:type "hidden" :name name :value value}])

(ctmx/defcomponent ^:endpoint panel [req username password ^:edn email-data]
  (ctmx/with-req req
    (case request-method
      :post
      (-> (interop/android-promise emails {:username username :password password})
          (.then (fn [emails]
                   [:form {:id id :hx-target "this"}
                    (hidden "username" username)
                    (hidden "password" password)
                    (hidden "email-data" (pr-str emails))
                    (email-panel req emails)])))
      :patch
      [:div (pr-str email-data)]
      [:div
       [:form.mt-4 {:id id :hx-post "panel"}
        [:div.input-group.mb-2
         [:input.form-control
          {:type "text" :placeholder "Username" :name "username" :value username :required true}]
         [:span.input-group-text "@gmail.com"]]
        [:div.input-group.mb-2
         [:input.form-control
          {:type "password" :placeholder "Password" :name "password" :value password :required true}]]
        [:div.input-group
         [:input.form-control {:type "submit" :value "Login"}]]]])))

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
