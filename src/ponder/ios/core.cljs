(ns ponder.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [ponder.handlers]
            [ponder.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))


(def Swiper (js/require "react-native-swiper"))
(def swiper (r/adapt-react-class Swiper))

;;; Navigation
(def card-stack (r/adapt-react-class (.-CardStack (.-NavigationExperimental ReactNative))))
(def navigation-header-comp (.-Header (.-NavigationExperimental ReactNative)))
(def navigation-header (r/adapt-react-class navigation-header-comp))
(def header-title (r/adapt-react-class (.-Title (.-Header (.-NavigationExperimental ReactNative)))))

(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))

(def logo-img (js/require "./images/cljs.png"))
(def fundable-img (js/require "./images/fundable-ideas-that-matters-infographic.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defn nav-title [props]
  (.log js/console "props" props)
  [header-title (aget props "scene" "route" "title")])

(defn header
  [props]
  [navigation-header
   (assoc
    (js->clj props)
;    (js->clj "scene")
    :render-title-component #(r/as-element (nav-title %))
    :on-navigate-back #(dispatch [:nav/pop nil]))])


(def style
  {:view        {:flex-direction "column"
                 :margin         40
                 :margin-top     (.-HEIGHT navigation-header-comp)
                 :align-items    "center"}
   :title       {:font-size     30
                 :font-weight   "100"
                 :margin-bottom 20
                 :text-align    "center"}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   :image       {:width         80
                 :height        80
                 :margin-bottom 30}
   :button      {:background-color "#999"
                 :padding          10
                 :margin-bottom    10
                 :border-radius    5}})


(defn swiper-view []
  [swiper {:style {} :shows-buttons true}
   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "skyblue"}}
    [text {:style {:font-size 30 :font-weight "200" :color "black"}} "hello"]]
   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "steelblue"}}
    [text {:style {:font-size 30 :font-weight "200"}} "How are you?"]]
   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "aquamarine"}}
    [image {:source logo-img
            :style  {:width 80 :height 80 :margin-bottom 30}}]    
    [text {:style {:font-size 30 :font-weight "200"}} "Simple and beautiful"]]])

(defn scroller-view []
  [view {:style {:flex-direction "column" :margin 80 :align-items "center"}}
  [scroll-view {:horizontal true}
   [text {:style {:font-size 10 :font-weight "200" :margin-bottom 10 :text-align "center"}} "hi"]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]

   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]

   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]

   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 10}}]

   [text {:style {:font-size 10 :font-weight "200" :margin-bottom 10 :text-align "center"}} "how are you"]]])

(defn color-block-view []
  [view {:flex 1 :flex-direction "column" :margin 80 :align-items "center"}
         [view {:style {:flex 1 :width 50 :height 50 :backgroundColor "powderblue"}}]
         [view {:style {:flex 1 :width 100 :height 100 :backgroundColor "skyblue"}}]
         [view {:style {:flex 1 :width 150 :height 150 :backgroundColor "steelblue"}}]])


(defn popup-view []
  [view {:style {:flex-direction "column" :margin 80 :align-items "center"}  :justify-content "center"}
   [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5  :justify-content "center"}
                         :on-press #(alert "Museum Madness!")}
    [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Press Me"]]])

(defn route-button [key dest-title button-title]
  [touchable-highlight
   {:style    (:button style)
    :on-press #(dispatch [:nav/push {:key   key
                                     :title dest-title}])}
   [text {:style (:button-text style)} button-title]])

(defn greeting-view []
  (let [greeting (subscribe [:get-greeting])
        encouraging-message (subscribe [:get-encouraging-message])
        current-key (subscribe [:nav/current-key])
        ]

    (fn []
      [view {:style {:flex-direction "column" :margin 80 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "200" :margin-bottom 20 :text-align "center"}} @greeting]
       [image {:source fundable-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       [text {:style {:font-size 20 :font-weight "200" :margin-bottom 20 :text-align "center"}} @encouraging-message]

       [route-button :popup-route "Useless Popup Component" "Popup"]
       [route-button :swiper-route "Simple swiper" "Swiper"]
       [route-button :scroller-route "Simple scroller" "Scroller"]
       [route-button :color-block-route "Silly color block" "Yay it's a color!"]
       
       ])))


(defn scene-decider-function [props]
  (let [
;       current-key (subscribe [:nav/current-key])]
       current-key (keyword (aget props "scene" "key"))]

    (case current-key
      :scene_greeting-route [greeting-view]
      :scene_swiper-route [swiper-view]
      :scene_scroller-route [scroller-view]
      :scene_color-block-route [color-block-view]
      :scene_popup-route [popup-view]
      [greeting-view]
)))


(defn app-root
  "This is not undefined."
  []
  (let [nav (subscribe [:nav/state])]
    (fn []
      [card-stack {:on-navigate      #(dispatch [:nav/pop nil])
                   :render-overlay   #(r/as-element (header %))
                   :navigation-state @nav
                   :style            {:flex 1}
                   :render-scene      #(r/as-element
                                        (scene-decider-function %)

                                                     )}])))
(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "ponder" #(r/reactify-component app-root)))
