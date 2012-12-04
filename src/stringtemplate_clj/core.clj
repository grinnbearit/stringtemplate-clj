(ns stringtemplate-clj.core
  (:import [org.antlr.stringtemplate StringTemplate StringTemplateGroup])
  (:require [fs.core :as fs]))


(def search-paths (ref []))
(def templates (ref {}))

(defn add-path 
  ([path] (dosync (alter search-paths conj path)))
  ([path & xp]
    (loop [head path tail xp]
      (dosync (alter search-paths conj head))
      (if tail
        (recur (first tail) (next tail))
        @search-paths))))

(defn search-template [file]
  (first (filter #(fs/exists? (clojure.string/join [% file ".st"])) @search-paths)))

(defn load-template [directory name]
  (.. (StringTemplateGroup. "" directory) (getInstanceOf name)))

(defn update-template [template data]
  (let [new-template  (.. template getInstanceOf)]
    (doseq [[k v] (concat (seq (.. template getAttributes)) data)]
      (.. new-template (setAttribute k v)))
    new-template))

(defn add-template [name]
  (let [template (load-template (search-template name) name)]
    (dosync
      (alter templates assoc (keyword name) template))))

(defn find-template [name]
  (let [template (@templates (keyword name))]
    (if (nil? template)
      (get (add-template name) (keyword name))
      template)))

(defn render-template [template-name, data]
  (let [template (find-template template-name)]
    (str (update-template template data))))
