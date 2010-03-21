
(ns retwis.view
  ;(:require )
  (:use compojure )
  (:use retwis.timeline)
  ;(:import )
  )

(defn login-controller [session params]
  (if
    (valid-login? (params :name) (params :password))
   [
      (session-assoc :login true :uid (get-uid-for-username  (params :name)))
      (redirect-to "/view/")
    ]
    (redirect-to "/login/")))


(defn login-view [session]
  (dosync
    (html
      [:form {:method "post"}
       "User name: "
       [:input {:name "name", :type "text"}]
       [:br]
       "Password: "
       [:input {:name "password", :type "password"}]
       [:br]
       [:input {:type "submit" :value "Log in"}]])))


(defn create-list-recur [alist result]
  (if (empty? alist)
    result
    (create-list-recur
      (rest alist)
      (str result
        (html [:li (first alist)])))))

(defn create-list [alist]
  (create-list-recur alist "")
  )

(defn list-tweets [session]
  (println "session" session)
  (println "uid" (:uid session))
  (html
    [:body
      [:p "Welcomme: " (get-username (:uid session))]
     [:p "Followers: " (create-list (map get-username (get-followers (:uid session))))]
     [:p "Following: "  (create-list (map get-username (get-following (:uid session))))]
     [:form {:method "post"}
      "Post update:"
      [:input {:name "message", :type "text"}]
      [:br]
      [:input {:type "submit" :value "Post message"}]]
     [:p "Timeline: "]
     [:ul (create-list (get-timeline (:uid session)))]
     ]
    ))

(defn  add-controller [session params]
  (println "post" params)
  (post-update (:uid session) (params :message))
  (redirect-to "/view/"))

(defn follow-controller [session params]
  (if (not (nil? (:uid session)))
  (do
    (follow (:uid session) (:uid params))
    (html
      [:body
        [:p "you are now following" (get-username (:uid params))]]))
    (html
      [:body
       [:p "you are not logged in"]])))

(defn logged-in? [session]
  (not (nil? (:uid session))))


(defn unfollow-controller [session params]
  (if (logged-in? session)
  (do
    (unfollow (:uid session) (:uid params))
    (html
      [:body
        [:p "you are no longer following" (get-username (:uid params))]]))
    (html
      [:body
       [:p "you are not logged in"]])))


(defn user-link [ me user]
  (html
    [:p
     [:b (get-username user)]
     [:br]
     [:em
      (if (following? me user)
        (link-to (str "/unfollow/" user) "unfollow")
        (link-to (str "/follow/" user) "follow"))
        ]]))

(defn generate-user-link [uid]
                (fn [user] (user-link uid user)))


(defn user-list [uid]
  (create-list (map (generate-user-link uid) (all-user-ids) )))

(defroutes webservice
  (GET "/view/" (list-tweets session))
  (POST "/view/" (add-controller session params))
  (GET "/login/" (login-view session))
  (POST "/login/" (login-controller session params))
  (GET "/logout/" (session-dissoc :uid))
  (GET "/follow/:uid" (follow-controller session params))
  (GET "/unfollow/:uid" (unfollow-controller session params))

  (GET "/list/" (user-list (:uid session)))
  )

(decorate webservice
  (with-session :memory))

(defn -main [& args]
  (run-server {:port 8888}
    "/*" (servlet webservice)))

