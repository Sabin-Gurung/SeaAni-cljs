(ns sea-ani.anime-api
  (:require
   [sea-ani.http :refer [http]]))

(defn- api-season-anime [year season page]
  (str "https://api.jikan.moe/v4/seasons/" year "/" season "?page=" page))
(defn- api-anime-full [mal_id]
  (str "https://api.jikan.moe/v4/anime/" mal_id "/full"))

(defn anime-by-season [year season page cb]
  (let [map-anime (comp
                    (map #(select-keys % [:title_english :score :images :mal_id]))
                    (map #(update % :images (comp :image_url :jpg)))
                    (filter :title_english))]
    (http :get (api-season-anime year season page)
          :cb #(cb (update % :data (fn [c] (sequence map-anime c)))))))

(defn anime-full [mal_id cb]
  (http :get (api-anime-full mal_id)
        :cb #(-> (:data %)
                 (select-keys [:title_english :popularity :trailer
                               :synopsis :images :trailer])
                 (update :trailer :embed_url)
                 (update :images (comp :large_image_url :jpg))
                 cb)))


