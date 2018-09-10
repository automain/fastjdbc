Overview
---
#### *fastjdbc* is a lightweight jdbc frame. It's designed around fast development, fast working and fast maintenance.
#### **The following constraints must be accepted to use this frame:**
* Java EE 8 or later is necessary.
* [HikariCP](https://github.com/brettwooldridge/HikariCP) connection pool is necessary.
* [Servlet](https://javaee.github.io/servlet-spec/) is necessary.
* The primary key in the database table should be the __bigint__ type in mysql (__java.lang.Long__ type in java), or same level type in other database.
* The primary key in the database table should be __auto increment__.
* Logical deletion(soft delete) are recommended and the delete flag column should be named by '__is_delete__' and __tinyint__ type in mysql(__java.lang.Integer__ type in java) or same level type in other database.
'__0__' means the row is effective and '__1__' means the row is deleted.

version history
---
### 1.5(2018-09-11):
* add __PageParameterBean__ as parameter of __selectTableForPage__ function in __BaseDao__
* bug fix: when execute __selectTableForPage__ function, the count sql should not be auto generated, so there is a property in __PageParameterBean__ named __countSql__ for customization
* bug fix: when count result is zero in __selectTableForPage__, the query sql will not executed and return an empty list
* bug fix: __executeSelectReturnResultSet__ function in __JDBCUtil__ can be return null, and the null result should be handled
### 1.4(2018-05-13):
* replace __notNullColumnMap()__ function in __BaseBean__ by __columnMap(boolean all)__
* add __batchInsertIntoTable__ function
* add __all__ param in __updateTable__/__updateTableByIdList__ function to distinguish whether update all columns or the not null columns
### 1.3(2018-03-24):
* add __deleteTableById__ function
* add __deleteTableByIdList__ function
### 1.2(2018-03-13):
* add __pageFromRequest__ function to get current page from request, default: __1__
* add __limitFromRequest__ function to get page limit from request, default: __10__
### 1.1(2018-03-07):
* __pom.xml__ and __README.md__ adjust
### 1.0(2018-03-06):
* init commit(see __javadoc__)
* [generator](https://github.com/automain/generator) release to generate __Bean__,__Dao__,__Service__
* maven dependence
    ```
    <dependency>
        <groupId>com.github.automain</groupId>
        <artifactId>fastjdbc</artifactId>
        <version>1.0</version>
    </dependency>
    ```
license
---
```
Copyright 2018 fastjdbc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```