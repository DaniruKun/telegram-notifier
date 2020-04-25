(ns telegram-notifier.util)

(defn user-mention-str
  "Creates a Markdown formatted string to mention user by id"
  [name id]
  (str "[" name "](tg://user?id=" id ")"))
