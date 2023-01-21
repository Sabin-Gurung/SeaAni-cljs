(ns sea-ani.anime-api
  (:require
   [sea-ani.http :refer [http]]))

(defn anime-by-season [year season page cb]
  (let [map-anime (comp
                    (map #(select-keys % [:title_english :score :images :mal_id]))
                    (map #(update % :images (comp :image_url :jpg)))
                    (filter :title_english))]
    (http :get (str "https://api.jikan.moe/v4/seasons/" year "/" season "?page=" page)
          :cb #(cb (update % :data (fn [c] (sequence map-anime c)))))))

