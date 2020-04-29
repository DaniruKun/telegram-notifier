(ns telegram-notifier.util-test
  (:require [clojure.test :refer :all]
            [telegram-notifier.util :refer :all]))

(def chat-info-with-pinned-msg {:ok true,
                                :result {:id -233956393,
                                         :title "TestGroup",
                                         :type "group",
                                         :pinned_message {:message_id 387,
                                                          :from {:id 1113754276,
                                                                 :is_bot true,
                                                                 :first_name "Member Notification Bot",
                                                                 :username "NotifyMembersBot"},
                                                          :chat {:id -233956393,
                                                                 :title "TestGroup",
                                                                 :type "group",
                                                                 :all_members_are_administrators true},
                                                          :date 1588102671}}})

(deftest test-get-pinned-msg
  (testing "Test that pinned message id is returned"
    (is (= (get-pinned-msg chat-info-with-pinned-msg) 387))))