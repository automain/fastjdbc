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
 * <p>This is one of the basic compositions of fastjdbc, all transmission operations will be around this.</p>
 * <p>You can write a code generator to generate the implement classes,
 * and then if database changed, you only need to maintain the implement class with less changed.</p>
 *
 * @param <T> an object which implement this
 * @apiNote the child of this should extends {@link com.github.fastjdbc.util.RequestUtil}
 * for override {@link #pickBeanFromRequest(HttpServletRequest)}
 * @since 1.0
 */
public interface BaseBean<T extends BaseBean> {

    /**
     * Return the table name of the clild bean in the database.
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
    Object primaryValue();

    /**
     * <p>The key of result map is column name in the database,
     * the value of map is this property value of the child bean.</p>
     * eg: if there is a primary key column name <tt>test_id</tt>
     * and an other column name <tt>test_name</tt>
     * the child method should like this:
     * <pre> {@code
     * Map&lt;String, Object&gt; map = new HashMap&lt;String, Object&gt;();
     * if (this.getTestName() != null) {
     *      map.put("test_name", this.getTestName());
     * }
     * return map;
     * }</pre>
     *
     * @return the notNullColumnMap of the child object
     * @apiNote the primary key value pair should not in this map!
     * @since 1.0
     */
    Map<String, Object> notNullColumnMap();

    /**
     * <p>Get an object of the child bean from {@link ResultSet}.</p>
     * eg: if there is a primary key column name <tt>test_id</tt>
     * and an other column name <tt>test_name</tt>
     * and the child bean name is <tt>TbTest</tt>
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
     * eg: if there is a primary key column name <tt>test_id</tt>
     * and an other column name <tt>test_name</tt>
     * and the child bean name is <tt>TbTest</tt>
     * the child method should like this:
     * <pre>{@code
     *  TbTest bean = new TbTest();
     *  bean.setTestId(getInt("testId", request));
     *  bean.setTestName(getString("testName", request));
     *  return bean;
     * }</pre>
     *
     * @param request the request object
     * @return the object of child
     * @apiNote the mothod <tt>getInt</tt> and <tt>getString</tt> are from {@link com.github.fastjdbc.util.RequestUtil}
     * @since 1.0
     */
    T pickBeanFromRequest(HttpServletRequest request);
}
