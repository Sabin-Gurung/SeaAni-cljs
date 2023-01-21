(ns sea-ani.http
  (:require
   [ajax.core :as ajax]))


(defn http [method uri & {:keys [cb ecb format response-format]
                          :or {cb println
                               ecb println
                               format (ajax/json-request-format)
                               response-format (ajax/json-response-format {:keywords? true})}
                          :as config}]
  (-> (dissoc config :cb :ecb)
      (assoc :method method
             :uri uri
             :handler (fn [[success? resp]] (if success? (cb resp) (ecb resp)))
             :format format
             :response-format response-format)
      (ajax/ajax-request)))
