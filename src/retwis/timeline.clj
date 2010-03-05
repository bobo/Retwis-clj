
(ns retwis.timeline
  ;(:require )
  (:use compojure, retwis.redis)
  ;(:import )
  )

(defn username-key [uid]
  (str "uid:" uid ":username"))
(defn password-key [uid]
  (str "uid:" uid ":password"))

(defn set-username [uid username]
  (set-key (username-key uid) username))

(defn get-next-uid []
  (increment-and-get "uid"))

(defn set-password [uid password]
  (set-key (password-key uid) password))

(defn username-uid-key [username]
  (str "username:" username ":uid"))

(defn get-uid-for-username [username]
    (get-key (username-uid-key username)))


(defn set-uid-for-username [uid username]
  (set-key (username-uid-key username) uid))

(defn create-user [username password]
  (let [uid (get-next-uid)]
    (set-username uid username)
    (set-password uid password)
    (set-uid-for-username uid username)
    uid))

(defn get-username [uid]
  (get-key (username-key uid)))
(defn get-password [uid]
  (get-key (password-key uid)))

(defn get-user [uid]
  (str
     "id:" uid
    " username:" (get-username uid)
    " password: "(get-password uid)
))

(defn followers-key [uid]
  (str "uid:" uid ":followers"))
(defn following-key [uid]
  (str "uid:" uid ":following"))


(defn follow [follower following]
  (add-to-set (followers-key follower) following)
  (add-to-set (following-key following) follower))

(defn get-followers [uid]
  (show-set (followers-key uid)))

(defn get-following [uid]
  (show-set (following-key uid)))

(defn exist? [username]
  (not (empty? username)))

(defn valid-login? [username password]
  (let [uid (get-uid-for-username username)]
        (and
          (exist? uid)
          (= (get-password uid) password)
          )))


(defn next-post-id []
  (increment-and-get "global:nextPostId"))

(defn post-update [uid message]
  (let [mid (next-post-id)]
    (set-key (str "mid:" mid) (str (get-username uid) ":" (new java.util.Date) ":" message))
    mid)
  )

