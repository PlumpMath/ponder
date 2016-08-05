(ns ponder.db
  (:require [schema.core :as s :include-macros true]))



(def NavigationRoute
  {:key s/Keyword
   :title s/Str
   })

(def NavigationState
  {
   :index s/Int
  :routes [NavigationRoute]
  :key s/Keyword
  })
(def schema {:greeting s/Str
             :encouraging-message s/Str
             :nav {

                   :index s/Int
                   :routes [{:key s/Keyword
                            :title s/Str}]
             }})

;; initial state of app-db
(def app-db {:greeting "Hello Rhiannon!"
             :encouraging-message "I hot-reload and compile to iOS and Android!"

             :nav {
                   :index    0
                   :routes [{:key :greeting-route
                             :title "First room"}]}
             })
