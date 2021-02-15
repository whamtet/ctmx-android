(ns android.interop)

(defmacro android-promise [f args]
  `(let [s# (str (gensym))
         p# (make-promise s#)]
     (~(symbol (str "js/" f ".invoke")) s# (to-json ~args))
     p#))
