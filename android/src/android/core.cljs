(ns android.core
  (:require
    [android.interop :as interop]
    [android.storage :as storage]
    [android.ui.emails :as emails]
    [android.util :as util]
    cljs.reader
    [ctmx.intercept :as intercept]
    [ctmx.render :as render]
    ctmx.rt
    [frontend.core :as frontend])
  (:require-macros
    [ctmx.core :as ctmx]))

(enable-console-print!)

(defn hidden [name value]
  [:input {:type "hidden" :name name :value value}])

(defn- state-panel [id username password email-data content]
  [:form.mt-2 {:id id :hx-target "this"}
   (hidden "username" username)
   (hidden "password" password)
   (hidden "email-data" (pr-str email-data))
   content])

(defn login [id username password error?]
  [:form.mt-4 {:id id :hx-post "panel:login" :hx-indicator "#spinner"}
   [:div.input-group.mb-2
    [:input.form-control
     {:type "text" :placeholder "Username" :name "username" :value username :required true}]]
   [:div.input-group.mb-2
    [:input.form-control
     {:type "text" :placeholder "Password" :name "password" :value password :required true}]]
   (when error?
     [:div.error-message.mb-2 "Invalid Credentials"])
   [:div.input-group
    [:input.form-control {:type "submit" :value "Login"}]]
   util/loading])

(ctmx/defcomponent ^:endpoint panel [req username password ^:edn email-data ^:int i command]
  (case command
    "login"
    (-> (interop/android-json "emails" {:username username :password password})
        (.then (fn [emails]
                 (storage/set-password password) ;; store successful password
                 (state-panel
                   id
                   username
                   password
                   emails
                   (emails/email-panel emails)))
               (fn [_]
                 (login id username password true))))
    "next"
    (-> (interop/android-json "emails" {:start (count email-data)})
        (.then (fn [emails]
                 (let [emails (vec (concat email-data emails))]
                   (state-panel
                     id
                     username
                     password
                     emails
                     (emails/email-panel emails))))))
    "detail"
    (state-panel
      id
      username
      password
      email-data
      (emails/individual-email email-data i))
    "back"
    (state-panel
      id
      username
      password
      email-data
      (emails/email-panel email-data))
    (login id "matthew@molloy.link" (storage/get-password) false)))

(def req {:params {}})

(ctmx/defstatic main []
  (set! js/document.body.innerHTML
        (render/html
          [:div.container {:hx-ext "intercept"}
           (panel req)])))

(intercept/set-responses!
  (ctmx/metas main))

(set! (.-defaultSettleDelay js/htmx.config) 0)
(set! (.-defaultSwapStyle js/htmx.config) "outerHTML")
(set! (.-includeIndicatorStyles js/htmx.config) false)
(main)
(frontend/main)

