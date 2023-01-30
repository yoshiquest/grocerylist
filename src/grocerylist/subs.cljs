(ns grocerylist.subs
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]))

(re-frame/reg-sub
  ::route
  :-> :route)

(re-frame/reg-sub
  ::current-list-id
  :-> :current-list-id)

(re-frame/reg-sub
  ::lists
  :-> :lists)

(re-frame/reg-sub
  ::current-list
  :<- [::lists]
  :<- [::current-list-id]
  (fn [[lists current-list-id]]
    (get lists current-list-id)))

(re-frame/reg-sub
  ::listname
  :<- [::current-list]
  :-> :listname)

(re-frame/reg-sub
  ::items
  :<- [::current-list]
  :-> :items)

(re-frame/reg-sub
  ::list
  :<- [::items]
  :-> vals)

(re-frame/reg-sub
  ::location-list
  :<- [::current-list]
  :-> :locations)

(re-frame/reg-sub
  ::itemform.name
  (fn [db]
    (get-in db [:itemform :name] "")))

(re-frame/reg-sub
  ::itemform.location
  (fn [db]
    (get-in db [:itemform :location] "")))

;(re-frame/reg-sub
;  ::listitem
;  :<- [::list]
;  (fn [list [_ itemnum]]
;    (get list itemnum)))

(re-frame/reg-sub
  ::location.dragged
  :location.dragged)

(re-frame/reg-sub
  ::location.dragged?
  :<- [::location.dragged]
  (fn [dragnum [_ itemnum]]
    (= itemnum dragnum)))

(re-frame/reg-sub
  ::location-listitem
  :<- [::location-list]
  (fn [list [_ itemnum]]
    (get list itemnum)))

(re-frame/reg-sub
  ::nlocations
  :<- [::location-list]
  :-> count)

;(re-frame/reg-sub
;  ::nlist
;  :<- [::list]
;  :-> count)

(re-frame/reg-sub
  ::locationform.name
  (fn [db]
    (get-in db [:locationform :name])))

(re-frame/reg-sub
  ::sort-method
  :-> :sort-method)

(re-frame/reg-sub
  ::sort-reversed?
  :-> :sort-reversed?)

(re-frame/reg-sub
  ::sorted-list
  :<- [::location-list]
  :<- [::list]
  :<- [::sort-method]
  :<- [::sort-reversed?]
  (fn [[location-list list sort-method sort-reversed?]]
    ((get u/sorting-method-map sort-method) location-list list sort-reversed?)))

(re-frame/reg-sub
  ::sorted-ids
  :<- [::sorted-list]
  (fn [sorted-list]
    (map :id sorted-list)))

(re-frame/reg-sub
  ::item-by-id
  :<- [::items]
  (fn [items [_ id]]
    (get items id)))

(re-frame/reg-sub
  ::lists-list
  :<- [::lists]
  (fn [lists]
    (reduce-kv (fn [coll id list]
                 (conj coll {:id id
                             :listname (:listname list)})) [] lists)))

(re-frame/reg-sub
  ::listform.name
  (fn [db]
    (get-in db [:listform :name] "")))