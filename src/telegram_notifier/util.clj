(ns telegram-notifier.util
  (:require [clj-http.client :as http])
  (:gen-class))

(def base-url "https://api.telegram.org/bot")

(defn get-chat [token chat-id]
  "Get up to date information about the chat"
  (let [url (str base-url token "/getChat")
        form [{:part-name "chat_id" :content (str chat-id)}]
        resp (http/post url {:as :json :multipart form})]
    (-> resp :body)))

(defn get-pinned-msg
  "Get pinned msg id"
  [chat-info]
  (if (contains? (chat-info :result) :pinned_message)
    (int (((chat-info :result) :pinned_message) :message_id))))

(defn user-mention-str
  "Creates a Markdown formatted string to mention user by id"
  [name id]
  (str "[" name "](tg://user?id=" id ")"))

(defn get-chat-admins [token chat-id]
  "Get up to date information about the chat admins"
  (let [url (str base-url token "/getChatAdministrators")
        form [{:part-name "chat_id" :content (str chat-id)}]
        resp (http/post url {:as :json :multipart form})]
    (-> resp :body)))

(defn pin-chat-msg
  "Pin a message in a group, a supergroup, or a channel"
  ([token chat-id msg-id] (pin-chat-msg token chat-id msg-id false))
  ([token chat-id msg-id disable-notif]
   (let [url (str base-url token "/pinChatMessage")
         body {:chat_id chat-id
               :message_id msg-id
               :disable_notification disable-notif}]
     (http/post url {:content-type :json
                     :as :json
                     :form-params body}))))

(defn unpin-chat-msg
  "Unpin a message in a group, a supergroup, or a channel"
  ([token chat-id]
   (let [url (str base-url token "/unpinChatMessage")
         body {:chat_id chat-id}]
     (http/post url {:content-type :json
                     :as :json
                     :form-params body}))))