Overview
---
#### *fastjdbc* is a lightweight jdbc frame. It's designed around fast development, fast working and fast maintenance.
#### **The following constraints must be accepted to use this frame:**
* Java EE 11 or later is necessary.
* The primary key in the database table should be named by __id__ and __auto increment__ and __not null__ and __int__ type in mysql (__java.lang.Integer__ type in java), or same level type in other database.
* Logical deletion(soft delete) are recommended and the valid flag column should be named by '__is_valid__' and __tinyint__ type in mysql(__java.lang.Integer__ type in java) or same level type in other database.
'__0__' means the row is invalid and '__1__' means the row is valid.
* column named by __gid__ and __char__ type in mysql (__java.lang.String__ type in java) should be used
when the table is designed by partition or save secret message in business.
* A part of demo is in the test directory.

version history
---
### 2.4(2019-11-18):
* change frequently used methods to static in __BaseDao__
* adjust test classes to fix change
### 2.3(2019-11-03):
* remove __BaseService__ class
* move all classes to base dictionary
* Incompatible with older versions and version below 2.3 is deprecated
### 2.2(2019-10-19):
* disign upgrade for connection
* change JDK version to 11(LTS)
* remove __ConectionBean__ and __java.util.logging.Logger__ dependence
* add __slf4j__ as default log and output sql in debug level
* Incompatible with older versions and version below 2.2 is deprecated
### 2.1(2019-08-21):
* add gid(global id for partition table) column support
* change primary column to named __id__ and __int__ type in mysql (__java.lang.Integer__ type in java)
* remove __primaryKey__ and __primaryValue__ function in __BaseBean__
* adjust test method
### 2.0(2019-08-18):
* disign upgrade for front and back end separation
* remove __RequestUtil__ and __Servlet__ dependence
* standardized naming for params and functions
* Incompatible with older versions and version below 2.0 is deprecated
### 1.8(2018-12-08):
* add __getIntValues__ function in __RequestUtil__
* change __setTimeRange__ function in __BaseDao__, TimeStamp type property is deprecated and Integer type property with unix timestamp is 
suggested.
* remove __getTimestamp__ function in __RequestUtil__

### 1.7(2018-09-30):
* add __countTableByBean__ function in __BaseDao__
* other bug fix

### 1.6(2018-09-19):
* change __init__ function in __ConnectionPool__ from HikariCP config to given datasource
* add RuntimeException at necessary position
### 1.5(2018-09-11):
* add __PageParameterBean__ as parameter of __selectTableForPage__ function in __BaseDao__
* add all main function test in test package, see __MainTestThread__ 
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
---
* maven dependence
    ```
    <dependency>
        <groupId>com.github.automain</groupId>
        <artifactId>fastjdbc</artifactId>
        <version>2.4</version>
    </dependency>
    ```
license
---
```
Copyright 2019 fastjdbc

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