(ns grocerylist.events.list
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.spec.db :as spec.db]
    [grocerylist.spec.list :as spec.list]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]
    [grocerylist.fx :as fx]))

(defn add-item [db [_ item-name item-location]]
  (let [list-id (:current-list-id db)
        id (get-in db [:lists list-id :items.next-id])]
    (-> db
        (assoc-in [:lists list-id :items id] {:name item-name
                                              :location item-location
                                              :checked? false
                                              :id id})
        (update-in [:lists list-id :items.next-id] inc))))
(reg-event-persistent-db
  ::add-item
  add-item)

(defn delete-item [db [_ id]]
  (update db :items dissoc id))
(reg-event-persistent-db
  ::delete-item
  [select-list]
  delete-item)

(defn check-item [db [_ id]]
  (update-in db [:items id :checked?] not))
(reg-event-persistent-db
  ::check-item
  [select-list]
  check-item)

(defn update-item-name [db [_ id new-name]]
  (assoc-in db [:items id :name] new-name))
(reg-event-persistent-db
  ::update-item-name
  [select-list]
  update-item-name)

(defn update-item-location [db [_ id new-location]]
  (if (> (.indexOf (:locations db) new-location) -1)
    (assoc-in db [:items id :location] new-location)
    db))
(reg-event-persistent-db
  ::update-item-location
  [select-list]
  update-item-location)

(defn toggle-sort-method [db [_ sort-method]]
  (if (contains? u/sorting-method-map sort-method)
    (if (= sort-method (:sort-method db))
      (update db :sort-reversed? not)
      (assoc db :sort-method sort-method
                :sort-reversed? false))
    db))
(re-frame/reg-event-db
  ::toggle-sort-method
  toggle-sort-method)

(defn update-list-name [db [_ new-name]]
  (assoc db :name new-name))
(reg-event-persistent-db
  ::update-list-name
  [select-list]
  update-list-name)

(re-frame/reg-event-fx
  ::edit-name-start
  [select-list]
  (fn [{db :db}]
    {:db (assoc-in db [:edits :list :name] (:name db))
     :fx [[::fx/focus-element "edit-list-name"]]}))

(re-frame/reg-event-db
  ::edit-name
  (fn [db [_ new-name]]
    (assoc-in db [:edits :list :name] new-name)))

(re-frame/reg-event-fx
  ::edit-name-submit
  (fn [{db :db}]
    (let [new-name (get-in db [:edits :list :name])
          new-db (assoc-in db [:lists (:current-list-id db) :name] new-name)]
      (if (s/valid? ::spec.db/db new-db)
        {:db (-> db
                 (update-in [:edits :list] dissoc :name)
                 (update-in [:errors :forms] dissoc :list))
         :fx [[:dispatch [::update-list-name new-name]]]}
        {:db (assoc-in db [:errors :forms :list] (s/explain-data ::spec.db/db new-db))
         :fx [[::fx/focus-element "edit-list-name"]]}))))

(re-frame/reg-event-fx
  ::edit-item-name-start
  [select-list]
  (fn [{db :db} [_ id]]
    {:db (assoc-in db [:edits :items id :name] (get-in db [:items id :name]))
     :fx [[::fx/focus-element (str "edit-item-name-" id)]]}))

(re-frame/reg-event-db
  ::edit-item-name
  (fn [db [_ id new-name]]
    (assoc-in db [:edits :items id :name] new-name)))

(re-frame/reg-event-fx
  ::edit-item-name-submit
  (fn [{db :db} [_ id]]
    (let [new-name (get-in db [:edits :items id :name])
          list (get-in db [:lists (:current-list-id db)])
          new-list (assoc-in list [:items id :name] new-name)]
      (if (s/valid? ::spec.list/list new-list)
        {:db (-> db
                 (update-in [:edits :items] dissoc id)
                 (update-in [:errors :forms] dissoc :item))
         :fx [[:dispatch [::update-item-name id new-name]]]}
        {:db (assoc-in db [:errors :forms :item] (s/explain-data ::spec.list/list new-list))
         :fx [[::fx/focus-element (str "edit-item-name-" id)]]}))))