(ns grocerylist.fx
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]
    [grocerylist.routes :as routes]))

(re-frame/reg-fx
  ::confirm-dialog
  (fn [{:keys [message on-confirm on-deny]
        :or {message ""}}]
    (if (js/confirm message)
      (when on-confirm
        (on-confirm))
      (when on-deny
        (on-deny)))))

(re-frame/reg-fx
  ::navigate
  (fn [handler]
    (routes/navigate! handler)))

(re-frame/reg-fx
  ::focus-element
  (fn [id]
    (reagent/after-render #(some-> js/document (.getElementById id) .focus))))