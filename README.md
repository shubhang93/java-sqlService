# java-sqlService
An easy to use Sql query runner for Kotlin

SQL is a mtaured DSL in itself and offers a very declarative way of writing queries and fetching data.(Heavily inspired by 
the yesql library for clojure).

# What adantages does this library provide?
* Write your SQL files separately with .sql extensions and test them separetely by connecting them to a db console.
* Sql Service will convert all your queries and make the available in your application.
* Simply use the query name and pass named arguments to your query to fetch the results as List of Maps.
* This library enables you to test queries independent of your app.

# Usage
Create a sql file test.sql
``` sql
-- name: product
select * from product where code = :code and brand=:brand
```
```java
 public static void main(String[] args) {
    SqlService sqlService = new SqlService("dir_path_containing_sql_files") 
	  HashMap<String, Object> params = new HashMap<>();
    params.put("code", "p456","brand","A1");
	  List<HashMap<String, Object>> res = sqlService.executeSql("product",params);
 }
```
Declare your query names using the -- name: your-query-name in the .sql file

Supports mutilple sql_files

sql_dir -> sql_file1,
           sql_file2,
           sql_file3



