(ns grocerylist.events.forms.item
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.list :as list]
    [grocerylist.spec.list :as spec.list]))

(defn update-name [db [_ name]]
  (assoc-in db [:forms :item :name] name))
(re-frame/reg-event-db
  ::update-name
  update-name)

(defn update-location [db [_ location]]
  (assoc-in db [:forms :item :location] location))
(re-frame/reg-event-db
  ::update-location
  update-location)
(defn submit [{db :db}]
  (let [item-name (get-in db [:forms :item :name] "")
        location (get-in db [:forms :item :location] (first (:locations db)))
        item {:name item-name
              :location location
              :checked? false}
        list (get-in db [:lists (:current-list-id db)] {})
        list-to-check (-> list
                          (assoc-in [:items (:items.next-id list)] item)
                          (update :items.next-id inc))]
    (if (s/valid? ::spec.list/list list-to-check)
      {:fx [[:dispatch [::list/add-item (:name item) (:location item)]]
            [:dispatch [::update-name ""]]]
       :db (assoc-in db [:errors :forms :item] nil)}
      {:db (assoc-in db [:errors :forms :item] (s/explain-data ::spec.list/list list-to-check))})))
(re-frame/reg-event-fx
  ::submit
  [select-list]
  submit)

(defn reset [{db :db} _]
  {:fx [[:dispatch [::update-name ""]]
        [:dispatch [::update-location (first (:locations db))]]]})
(re-frame/reg-event-fx
  ::reset
  [select-list]
  reset)