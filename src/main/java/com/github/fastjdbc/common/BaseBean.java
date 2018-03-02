/*
 * Copyright 2018 fastjdbc
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

package com.github.fastjdbc.common;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p>An object that all the database bean object should implement.</p>
 * <p>Note: the implement classes of {@link BaseBean} should extends {@link com.github.fastjdbc.util.RequestUtil}</p>
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
     * Return the primary key column name of the child bean in the database.
     *
     * @return the primary key column name of the child bean in the database
     * @since 1.0
     */
    String primaryKey();

    /**
     * Return the primary value of the child object.
     *
     * @return the primary value of the child object
     * @since 1.0
     */
    Long primaryValue();

    /**
     * <p>The key of result map is column name in the database,
     * the value of map is this property value of the child bean.</p>
     * eg: if there is a primary key column name {@code test_id}
     * and an other column name {@code test_name}
     * the child method should like this:
     * <pre> {@code
     * Map<String, Object> map = new HashMap<String, Object>();
     * if (this.getTestName() != null) {
     *      map.put("test_name", this.getTestName());
     * }
     * return map;
     * }</pre>
     * <p>Note: the primary key value pair should not in this map!</p>
     *
     * @return the notNullColumnMap of the child object
     * @since 1.0
     */
    Map<String, Object> notNullColumnMap();

    /**
     * <p>Get an object of the child bean from {@link ResultSet}.</p>
     * eg: if there is a primary key column name {@code test_id}
     * and an other column name {@code test_name}
     * and the child bean name is {@code TbTest}
     * the child method should like this:
     * <pre>{@code
     *  TbTest bean = new TbTest();
     *  bean.setTestId(rs.getInt("test_id"));
     *  bean.setTestName(rs.getString("test_name"));
     *  return bean;
     * }</pre>
     *
     * @param rs the ResultSet of query
     * @return the object of child
     * @throws SQLException the exception of parse value
     * @since 1.0
     */
    T pickBeanFromResultSet(ResultSet rs) throws SQLException;

    /**
     * <p>Get an object of the child bean from {@link HttpServletRequest}.</p>
     * eg: if there is a primary key column name {@code test_id}
     * and an other column name {@code test_name}
     * and the child bean name is {@code TbTest}
     * the child method should like this:
     * <pre>{@code
     *  TbTest bean = new TbTest();
     *  bean.setTestId(getInt("testId", request));
     *  bean.setTestName(getString("testName", request));
     *  return bean;
     * }</pre>
     * <p>Note: the method {@code getInt} and {@code getString} are from {@link com.github.fastjdbc.util.RequestUtil}</p>
     *
     * @param request the request object
     * @return the object of child
     * @since 1.0
     */
    T pickBeanFromRequest(HttpServletRequest request);
}
