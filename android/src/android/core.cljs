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

(println "MyActivity Hello world!")

(ctmx/defstatic main []
  (set! js/document.body.innerHTML
        (hiccups/html
          [:div {:hx-ext "intercept"}
           [:button "hello"]])))

(intercept/set-responses!
  (ctmx/metas main))

(set! (.-defaultSettleDelay js/htmx.config) 0)
(set! (.-defaultSwapStyle js/htmx.config) "outerHTML")
(main)
