(ns android.core
  (:require
    [android.interop :as interop]
    [android.ui.emails :as emails]
    [android.util :as util]
    cljs.reader
    [ctmx.intercept :as intercept]
    ctmx.rt
    [frontend.core :as frontend]
    hiccups.runtime)
  (:require-macros
    [ctmx.core :as ctmx]
    [hiccups.core :as hiccups]))

(enable-console-print!)

(defn hidden [name value]
  [:input {:type "hidden" :name name :value value}])

(defn- state-panel [id username password email-data content]
  [:form.mt-2 {:id id :hx-target "this"}
   (hidden "username" username)
   (hidden "password" password)
   (hidden "email-data" (pr-str email-data))
   content])

(ctmx/defcomponent ^:endpoint panel [req username password ^:edn email-data ^:int i]
  (ctmx/with-req req
    (case request-method
      :post
      (-> (interop/android-json "emails" {:username username :password password})
          (.then (fn [emails]
                   (state-panel
                     id
                     username
                     password
                     emails
                     (emails/email-panel emails)))))
      :put
      (-> (interop/android-json "emails" {:start (count email-data)})
          (.then (fn [emails]
                   (let [emails (vec (concat email-data emails))]
                     (state-panel
                       id
                       username
                       password
                       emails
                       (emails/email-panel emails))))))
      :patch
      (state-panel
        id
        username
        password
        email-data
        (emails/individual-email email-data i))
      :delete
      (state-panel
        id
        username
        password
        email-data
        (emails/email-panel email-data))
      [:form.mt-4 {:id id :hx-post "panel" :hx-indicator "#spinner"}
       [:div.input-group.mb-2
        [:input.form-control
         {:type "text" :placeholder "Username" :name "username" :value username :required true}]
        [:span.input-group-text "@gmail.com"]]
       [:div.input-group.mb-2
        [:input.form-control
         {:type "password" :placeholder "Password" :name "password" :value password :required true}]]
       [:div.input-group
        [:input.form-control {:type "submit" :value "Login"}]]
       util/loading])))

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
(set! (.-includeIndicatorStyles js/htmx.config) false)
(main)
(frontend/main)

