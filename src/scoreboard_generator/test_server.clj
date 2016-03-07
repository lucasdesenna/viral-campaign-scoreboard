(ns scoreboard-generator.test-server
  (:gen-class)
  (:require [cheshire.core :refer :all])
  (:use [compojure.route :only [files not-found]]
        [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server))

(defonce test-server (atom {}))

(defn json-handler 
  "doc-string"
  [request]
  
  (let [echo (-> request :body)]
    
    (with-channel request channel
        (println "Test Server channel Open.\n")
        (on-close channel (fn [status]
                            (println "Test Server channel closed.\n")))
        
        (println "Data received.\n")
        
        (send! channel {:status 200
                        :headers {"Content-Type" "text/plain"}
                        :body echo}))))


(defn show-landing-page 
  "doc-string"
  [request] 
  
  "<h1>Test Server is running...<h1>")

(defroutes all-routes
  (GET "/" [] show-landing-page)
  (GET "/json" [] json-handler))

(defn die-if-running
  "doc-string"
  []
  
  (when-not (nil? @test-server)
    (@test-server :timeout 1000)
    (reset! test-server nil)))

(defn run 
  "doc-string"
  []
  
  (die-if-running)  
  (reset! test-server (run-server #'all-routes {:port 8090})))