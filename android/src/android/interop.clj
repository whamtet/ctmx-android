(ns android.interop)

(defmacro android-promise [s & java-constructors]
  `(let [~s (str (gensym))
         p# (make-promise ~s)]
     ~@java-constructors ;; go and trigger your java
     p#))
