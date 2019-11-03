/*
 * Copyright 2019 fastjdbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fastjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p>An object that all the database bean object should implement.</p>
 *
 * @param <T> a class which implement this
 * @since 1.0
 */
public interface BaseBean<T extends BaseBean> {

    /**
     * Return the table name of the child bean in the database.
     *
     * @return the table name of the child bean in the database
     * @since 1.0
     */
    String tableName();

    /**
     * <p>The key of result map is column name in the database,
     * the value of map is this property value of the child bean.</p>
     * eg: if there is a primary key column name {@code id}
     * and an other column name {@code test_name}
     * the child method should like this:
     * <pre> {@code
     * Map<String, Object> map = new HashMap<String, Object>(2);
     * if (all || this.getId() != null) {
     *      map.put("id", this.getId());
     * }
     * if (all || this.getTestName() != null) {
     *      map.put("test_name", this.getTestName());
     * }
     * return map;
     * }</pre>
     *
     * @param all get all or not null column map
     * @return the all or not null column map of the child object
     * @since 1.4
     */
    Map<String, Object> columnMap(boolean all);

    /**
     * <p>Get an object of the child bean from {@link ResultSet}.</p>
     * eg: if there is a primary key column name {@code id}
     * and an other column name {@code test_name}
     * and the child bean name is {@code Test}
     * the child method should like this:
     * <pre>{@code
     *  return new Test()
     *      .setId(rs.getInt("id"))
     *      .setTestName(rs.getString("test_name"));
     * }</pre>
     *
     * @param rs the ResultSet of query
     * @return the object of child
     * @throws SQLException the exception of parse value
     * @since 1.0
     */
    T beanFromResultSet(ResultSet rs) throws SQLException;

}
