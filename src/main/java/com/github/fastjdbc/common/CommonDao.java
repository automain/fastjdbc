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

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.PageBean;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>A common template of DAO layer, all the classes of DAO layer should extends {@link BaseDao}</p>
 * <p>Considering performance and large amount of data support,
 * the primary key of table should be long type(bigint in mysql) and auto increment.</p>
 * <p>Data permissions should be considered when design the table in database.</p>
 * <p>In business,we usually need to customize the query conditions and return the {@link PageBean} object,
 * a new method is needed to be added to the DAO class in this condition.</p>
 *
 * @see BaseDao
 * @since 1.0
 */
public interface CommonDao {

    /**
     * Insert the not null properties of bean.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to insert
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#notNullColumnMap()
     * @since 1.0
     */
    int insertIntoTable(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Insert the not null properties of bean and return the generated primary key.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to insert
     * @return generated primary key
     * @throws SQLException exception when insert failed
     * @see BaseBean#notNullColumnMap()
     * @since 1.0
     */
    Long insertIntoTableReturnId(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Update the not null properties of bean by the primary key of bean, the primary key
     * property should not null otherwise nothing will be updated.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to update
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#notNullColumnMap()
     * @since 1.0
     */
    int updateTable(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Update the not null properties of bean by the given id list.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to update
     * @param idList     a list id of the beans which will be updated
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#notNullColumnMap()
     * @since 1.0
     */
    int updateTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException;

    /**
     * Update the not null properties of bean by the query result of param bean.
     *
     * @param connection         ConnectionBean object
     * @param paramBean          param bean to query the rows to update
     * @param newBean            bean to update
     * @param insertWhenNotExist whether or not to insert when the query returns nothing
     * @param updateMulti        whether or not to update multi result when the query returns more than one result
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#notNullColumnMap()
     * @since 1.0
     */
    int updateTable(ConnectionBean connection, BaseBean paramBean, BaseBean newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException;

    /**
     * Soft delete a bean by the given id.
     * The column of delete mark should named {@code is_delete} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param id         id of the bean
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    int softDeleteTableById(ConnectionBean connection, BaseBean bean, Long id) throws SQLException;

    /**
     * Soft delete a bean by the given id list.
     * The column of delete mark should named {@code is_delete} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param idList     a list id of the beans which will be soft deleted
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    int softDeleteTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException;

    /**
     * Query a bean by the given id.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param id         id of the bean
     * @return the bean of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    BaseBean selectTableById(ConnectionBean connection, BaseBean bean, Long id) throws SQLException;

    /**
     * Query a bean by the given id list.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param idList     a list id of the beans to query
     * @return the bean list of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    List<BaseBean> selectTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException;

    /**
     * Query a bean by the param bean, match all the not null properties equals.
     * When multi rows match the condition, return the first one.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @return the first of query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    BaseBean selectOneTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Query list of beans by the param bean, match all the not null properties equals.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @return all query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    List<BaseBean> selectTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Query all rows.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @return all rows
     * @throws SQLException exception when query
     * @since 1.0
     */
    List<BaseBean> selectAllTable(ConnectionBean connection, BaseBean bean) throws SQLException;

    /**
     * Query list of beans by the param bean for page, match all the not null properties equals.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @param page       page number
     * @param limit      the count of data displayed on each page
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @see PageBean
     * @since 1.0
     */
    PageBean<BaseBean> selectTableForPage(ConnectionBean connection, BaseBean bean, int page, int limit) throws Exception;
}
