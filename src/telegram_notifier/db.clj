(ns telegram-notifier.db
  (:require [jdbc.pool.c3p0 :as pool]))

;; create db conn
(def spec
  (pool/make-datasource-spec {:subprotocol "postgresql"
                              :subname "//localhost:5432/telegram_notifier"
                              :user "admin"
                              :password ""}))