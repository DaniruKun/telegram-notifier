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
(def commands [{:command "start"
                :description "start the bot"}
               {:command "help"
                :description "show help printout"}
               {:command "notifyall"
                :description "notify all chat members"}
               {:command "notifyadmins"
                :description "notify all chat/group admins"}])

(h/defhandler handler

  (h/command-fn "start"
                (fn [{{id :id :as chat} :chat}]
                  (println "Bot joined new chat: " chat)
                  (t/send-text token id "Member Notification Bot started!")))

  (h/command-fn "help"
                (fn [{{id :id :as chat} :chat}]
                  (println "Command executed: [help]")
                  (t/send-text token id {:parse_mode "MarkdownV2"} help-text)))

  (h/command-fn "notifyall"
                (fn [{{id :id :as chat} :chat}]
                  (let [chat-info (util/get-chat token id)
                        pinned-msg (util/get-pinned-msg chat-info)]
                    (println "Command executed: [notifyall]")
                    (if (not= "private" ((chat-info :result) :type))
                      (do
                        (if (util/is-bot-admin token id)
                          (do
                            (when pinned-msg
                              (util/unpin-chat-msg token id))
                            (let [notif-msg (t/send-text token id "HERE")]
                              (util/pin-chat-msg token id ((notif-msg :result) :message_id))
                              (util/unpin-chat-msg token id)
                              (when pinned-msg
                                (util/pin-chat-msg token id pinned-msg true))))
                          (t/send-text token id "Not enough rights - add bot to administrators!")))
                      (t/send-text token id "Cannot notify anyone in a private chat,
                           add me to a group/channel first!")))))

  (h/command-fn "notifyadmins"
                (fn [{{id :id :as chat} :chat}]
                  (println "Command executed: [notifyadmins]")
                  (let [admins (util/get-chat-admins token id)
                        notif-text (util/admin-notif-text admins)]
                    (t/send-text token id {:parse_mode "MarkdownV2"} notif-text)))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "TELEGRAM_TOKEN environment variable NOT SET!")
    (System/exit 1))

  (println "Starting the telegram-notifier...")
  (util/set-commands token commands)

  (<!! (p/start token handler)))
