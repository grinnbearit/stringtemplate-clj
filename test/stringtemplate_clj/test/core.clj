(ns stringtemplate-clj.test.core
  (:use [stringtemplate-clj.core] :reload)
  (:use [clojure.test])
  (:use [clojure.contrib.mock])
  (:require [fs.core :as fs]))

(defn run-search-template [dir-path filename paths]
  (binding [search-paths (ref [])]
    (apply add-path paths)
    (expect [fs/exists? (returns true)]
      (is (= dir-path (search-template filename))))))

(deftest search-template-one-path
  (let [dir_path "/stubbed/"
        filename "myfile"
        file_path (clojure.string/join [dir_path filename ".st"])]
    (run-search-template dir_path filename [dir_path])))

(deftest search-template-many-paths
  (let [expected-path "/stubbed/"
        filename "myfile"
        file_path (clojure.string/join [expected-path filename ".st"])]
    (run-search-template expected-path filename [expected-path "second-path" "another path"])))

(deftest add-one-path
  (binding [search-paths (ref [])]
    (add-path "mypath")
    (is (= ["mypath"] @search-paths))))

(deftest add-many-paths
  (binding [search-paths (ref [])]
    (add-path "first" "second")
    (is (= ["first" "second"] @search-paths))))