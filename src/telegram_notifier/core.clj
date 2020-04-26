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
(def help-text (slurp "resources/help.md"))

(h/defhandler handler

  (h/command-fn "start"
                (fn [{{id :id :as chat} :chat}]
                  (println "Bot joined new chat: " chat)
                  (t/send-text token id "Member Notification Bot started!")))

  (h/command-fn "help"
                (fn [{{id :id :as chat} :chat}]
                  (t/send-text token id {:parse_mode "MarkdownV2"} help-text)))

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
