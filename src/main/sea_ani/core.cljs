(ns sea-ani.core
  (:require
   [goog.dom :as gdom]
   [reagent.dom :as rdom]
   [sea-ani.app :refer [app]]))

(defn mount-app []
  (when-let [el (gdom/getElement "app")]
    (rdom/render [app] el)))

(defn init []
  (println "----------Initialize App---------------")
  (mount-app))

(defn ^:dev/before-load stop []
  ; (js/console.log "stop")
  )
(defn ^:dev/after-load start []
  ; (js/console.log "start")
  (mount-app))

(comment
  (js/alert "hello")
  js/document
  )

