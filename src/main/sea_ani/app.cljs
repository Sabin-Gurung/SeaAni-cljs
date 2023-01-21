(ns sea-ani.app
  (:require
   [reagent.core :as r]
   [sea-ani.anime-api :refer [anime-by-season]]))

; ========================= model app states ======================================

(defonce app-state
  (r/atom {:search-params {}
           :search-results nil}))

(defn load-anime! [year season page]
  (anime-by-season year season page
                   #(-> @app-state
                        (assoc :search-results %)
                        (assoc :search-params {:year year :season season})
                        (->> (reset! app-state)))))

(defn load-more! []
  (let [{:keys [has_next_page current_page]} (get-in @app-state [:search-results :pagination])
        {:keys [season year]} (:search-params @app-state)]
    (when has_next_page
      (anime-by-season
        year season (inc current_page)
        #(-> @app-state
             (update-in [:search-results :data] concat (:data %))
             (assoc-in [:search-results :pagination] (:pagination %))
             (->> (reset! app-state)))))))

; ========================= views ui ======================================
(defn current-year [] (.. (js/Date.) getFullYear))
(def years (range (current-year) 1963 -1))
(def seasons ["summer" "fall" "winter" "spring"])

(defn header []
  [:<>
   [:nav.container-fluid
    [:ul [:li>strong "SeaAni"]]]
   [:header.container
    [:hgroup
     [:h2 "SeaAni"]
     [:h3 "Search your seasonal anime shows"]]]])

(defn search []
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (r/with-let [year (r/atom (first years))
               season (r/atom (first seasons))]
    [:div.container
     [:article
      [:div.grid
       [:label "Year"
        [:select
         {:on-change #(reset! year (.. % -target -value))}
         (for [i years] ^{:key i}[:option {:value i} i])]]
       [:label "Season"
        [:select
         {:on-change #(reset! season (.. % -target -value))}
         (for [i seasons] ^{:key i}[:option {:value i} i])]]]
      [:button {:on-click #(load-anime! @year @season 1)} "Search"]]]))

(defn anime-list [{:keys [pagination data]}]
  [:div.container-fluid
   [:div.anime-list
    (for [anime data]
      ^{:key (:mal_id anime)}
      [:article.anime
       [:figure
        [:img {:src (:images anime)}]
        (when-let [score (:score anime)]
          [:figcaption [:kbd (:score anime)]])
        (:title_english anime) ]])]
   (when (:has_next_page pagination)
     [:button {:on-click #(load-more!)} "Load More"])])

(defn app []
  [:div
   [header]
   [search]
   [anime-list (:search-results @app-state)]])

(comment
  (js/alert "hello")
  years
  seasons
  (anime-by-season 2021 "spring" 1 #(def a %))
  (load-anime! 2021 "fall" 1)
  (-> a
      (update :data (partial take 10))
      )
  )

