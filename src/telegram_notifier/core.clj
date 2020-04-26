(ns telegram-notifier.core
  (:require [telegram-notifier.util :as util]
            [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t])
  (:gen-class))

(def token (env :telegram-token))

(h/defhandler handler
  ;; Main message handler
              ;;{:message_id 44, :from {:id 107956390, :is_bot false, :first_name Daniil, :last_name Petrov, :username DanPetrov, :language_code en}, :chat {:id -233956393, :title TestGroup, :type group, :all_members_are_administrators true}, :date 1587826032, :text test}
              ;;{
              ;; :message_id 47, :from {
              ;; :id 48275381, :is_bot false, :first_name Viktors},
              ;; :chat {:id -233956393,
              ;; :title TestGroup,
              ;; :type group,
              ;; :all_members_are_administrators true},
              ;; :date 1587827119, :text .}

  (h/command-fn "start"
                (fn [{{id :id :as chat} :chat}]
                  (println "Bot joined new chat: " chat)
                  (t/send-text token id "Welcome to telegram-notifier!")))

  (h/command-fn "help"
                (fn [{{id :id :as chat} :chat}]
                  (t/send-text token id "Help is on the way")))

  (h/command-fn "notifyall"
                (fn [{{id :id :as chat} :chat}]
                  (t/send-text token id "Notifying all members")
                  (let [chat-info (util/get-chat token id)
                        pinned-msg (util/get-pinned-msg chat-info)]
                    (do
                      (when pinned-msg (util/unpin-chat-msg token id))
                      (let [notif-msg (t/send-text token id "HERE")]
                        ((util/pin-chat-msg token id ((notif-msg :result) :message_id))
                         (util/unpin-chat-msg token id)
                         (do
                           (when pinned-msg (util/pin-chat-msg token id pinned-msg))))))))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the telegram-notifier")
  (<!! (p/start token handler)))
