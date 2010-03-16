
(ns retwis.redis
  (require redis, redis.internal
))
(def REDIS-SPEC { :host "localhost" :port 6379 :db 3 })

(defmacro def-redis-fn [fname args & body]
  `(defn ~fname ~args
     (redis/with-server REDIS-SPEC
       (do ~@body))))

(def-redis-fn set-key [akey aval]
  (redis/set akey aval))

(def-redis-fn get-key [akey]
  (redis/get akey))

(def-redis-fn increment-and-get [akey]
  (redis/incr akey))

(def-redis-fn show-set [akey]
  (redis/smembers akey))

(def-redis-fn add-to-set [akey aval]
  (redis/sadd akey aval))

(def-redis-fn push [akey aval]
  (redis/lpush akey aval))

(def-redis-fn show-list [akey]
  (redis/lrange akey 0 200))


(def-redis-fn delete [akey]
  (redis/del akey))
(def-redis-fn typeof [akey]
  (redis/type akey))

(def-redis-fn get-keys [pattern]
  (redis/keys pattern))