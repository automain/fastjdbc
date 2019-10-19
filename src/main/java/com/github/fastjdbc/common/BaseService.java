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

import com.github.fastjdbc.bean.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>A common template of Service layer, all the classes of Service layer should extends this class</p>
 * <p>In business,we usually need to customize the query conditions and return the {@link PageBean} object,
 * a new method is needed to be added to the Service class in this condition.</p>
 *
 * @param <T> an object which implement {@link BaseBean}
 * @param <D> an object which implement {@link BaseDao}
 * @since 1.0
 */
public class BaseService<T extends BaseBean, D extends BaseDao<T>> {

    /**
     * Dao object.
     *
     * @since 1.0
     */
    private D dao;

    /**
     * Bean object.
     */
    private T bean;

    /**
     * Constructor with dao object.
     *
     * @param bean bean object
     * @param dao  dao object
     * @since 1.0
     */
    public BaseService(T bean, D dao) {
        this.bean = bean;
        this.dao = dao;
    }

    public D getDao() {
        return dao;
    }

    public T getBean() {
        return bean;
    }

    /**
     * Insert the not null properties of bean.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to insert
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    public int insertIntoTable(Connection connection, T bean) throws SQLException {
        return dao.insertIntoTable(connection, bean);
    }

    /**
     * Insert the not null properties of bean and return the generated primary key.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to insert
     * @return generated primary key
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    public Integer insertIntoTableReturnId(Connection connection, T bean) throws SQLException {
        return dao.insertIntoTableReturnId(connection, bean);
    }

    /**
     * Batch insert the properties of bean list.
     *
     * @param connection {@link Connection} object
     * @param list       list of bean to insert
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    public int batchInsertIntoTable(Connection connection, List<T> list) throws SQLException {
        return dao.batchInsertIntoTable(connection, list);
    }

    /**
     * Update the properties of bean by the primary key of bean.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to update
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    public int updateTableById(Connection connection, T bean, boolean all) throws SQLException {
        return dao.updateTableById(connection, bean, all);
    }

    /**
     * Update the properties of bean by the gid column of bean.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to update
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 2.1
     */
    public int updateTableByGid(Connection connection, T bean, boolean all) throws SQLException {
        return dao.updateTableByGid(connection, bean, all);
    }

    /**
     * Update the properties of bean by the given id list.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to update
     * @param idList     a list id of the beans which will be updated
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    public int updateTableByIdList(Connection connection, T bean, List<Integer> idList, boolean all) throws SQLException {
        return dao.updateTableByIdList(connection, bean, idList, all);
    }

    /**
     * Update the properties of bean by the given gid list.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to update
     * @param gidList    a list gid of the beans which will be updated
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 2.1
     */
    public int updateTableByGidList(Connection connection, T bean, List<String> gidList, boolean all) throws SQLException {
        return dao.updateTableByGidList(connection, bean, gidList, all);
    }

    /**
     * Update the properties of bean by the query result of param bean.
     *
     * @param connection         {@link Connection} object
     * @param paramBean          param bean to query the rows to update
     * @param newBean            bean to update
     * @param insertWhenNotExist whether or not to insert when the query returns nothing
     * @param updateMulti        whether or not to update multi result when the query returns more than one result
     * @param all                true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    public int updateTable(Connection connection, T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti, boolean all) throws SQLException {
        return dao.updateTable(connection, paramBean, newBean, insertWhenNotExist, updateMulti, all);
    }

    /**
     * Soft delete a bean by the given id.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to soft delete, the primary key must not null
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    public int softDeleteTableById(Connection connection, T bean) throws SQLException {
        return dao.softDeleteTableById(connection, bean);
    }

    /**
     * Soft delete a bean by the given gid.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to soft delete, the gid column must not null
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 2.1
     */
    public int softDeleteTableByGid(Connection connection, T bean) throws SQLException {
        return dao.softDeleteTableByGid(connection, bean);
    }

    /**
     * Soft delete a bean by the given id list.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection {@link Connection} object
     * @param idList     a list id of the beans which will be soft deleted
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    public int softDeleteTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return dao.softDeleteTableByIdList(connection, bean, idList);
    }

    /**
     * Soft delete a bean by the given gid list.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is deleted,
     * {@code 0} represent the row is effective.
     *
     * @param connection {@link Connection} object
     * @param gidList    a list gid of the beans which will be soft deleted
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 2.1
     */
    public int softDeleteTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return dao.softDeleteTableByGidList(connection, bean, gidList);
    }

    /**
     * Delete a bean by the given id.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to delete, the primary key must not null
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    public int deleteTableById(Connection connection, T bean) throws SQLException {
        return dao.deleteTableById(connection, bean);
    }

    /**
     * Delete a bean by the given gid.
     *
     * @param connection {@link Connection} object
     * @param bean       bean to delete, the gid column must not null
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 2.1
     */
    public int deleteTableByGid(Connection connection, T bean) throws SQLException {
        return dao.deleteTableByGid(connection, bean);
    }

    /**
     * Delete a bean by the given id list.
     *
     * @param connection {@link Connection} object
     * @param idList     a list id of the beans which will be deleted
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    public int deleteTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return dao.deleteTableByIdList(connection, bean, idList);
    }

    /**
     * Delete a bean by the given gid list.
     *
     * @param connection {@link Connection} object
     * @param gidList    a list gid of the beans which will be deleted
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 2.1
     */
    public int deleteTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return dao.deleteTableByGidList(connection, bean, gidList);
    }

    /**
     * @param connection {@link Connection} object
     * @param bean       the param bean
     * @return amount of rows which match the param bean
     * @throws SQLException exception when query
     * @since 1.7
     */
    public int countTableByBean(Connection connection, T bean) throws SQLException {
        return dao.countTableByBean(connection, bean);
    }

    /**
     * Query a bean by the given id.
     *
     * @param connection {@link Connection} object
     * @param bean       a bean with not null primary key
     * @return the bean of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    public T selectTableById(Connection connection, T bean) throws SQLException {
        return dao.selectTableById(connection, bean);
    }

    /**
     * Query a bean by the given gid.
     *
     * @param connection {@link Connection} object
     * @param bean       a bean with not null gid column
     * @return the bean of query result
     * @throws SQLException exception when query
     * @since 2.1
     */
    public T selectTableByGid(Connection connection, T bean) throws SQLException {
        return dao.selectTableByGid(connection, bean);
    }

    /**
     * Query a bean by the given id list.
     *
     * @param connection {@link Connection} object
     * @param idList     a list id of the beans to query
     * @return the bean list of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    public List<T> selectTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return dao.selectTableByIdList(connection, bean, idList);
    }

    /**
     * Query a bean by the given gid list.
     *
     * @param connection {@link Connection} object
     * @param gidList    a list gid of the beans to query
     * @return the bean list of query result
     * @throws SQLException exception when query
     * @since 2.1
     */
    public List<T> selectTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return dao.selectTableByGidList(connection, bean, gidList);
    }

    /**
     * Query a bean by the param bean, match all the not null properties equals.
     * When multi rows match the condition, return the first one.
     *
     * @param connection {@link Connection} object
     * @param bean       the param bean
     * @return the first of query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    public T selectOneTableByBean(Connection connection, T bean) throws SQLException {
        return dao.selectOneTableByBean(connection, bean);
    }

    /**
     * Query list of beans by the param bean, match all the not null properties equals.
     *
     * @param connection {@link Connection} object
     * @param bean       the param bean
     * @return all query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    public List<T> selectTableByBean(Connection connection, T bean) throws SQLException {
        return dao.selectTableByBean(connection, bean);
    }

    /**
     * Query all rows.
     *
     * @param connection {@link Connection} object
     * @return all rows
     * @throws SQLException exception when query
     * @since 1.0
     */
    public List<T> selectAllTable(Connection connection) throws SQLException {
        return dao.selectAllTable(connection, bean);
    }

    /**
     * Query list of beans by the param bean for page, match all the not null properties equals.
     *
     * @param connection {@link Connection} object
     * @param bean       the param bean
     * @param page       page number
     * @param size       the count of data displayed on each page
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @see PageBean
     * @since 1.0
     */
    public PageBean<T> selectTableForPage(Connection connection, T bean, int page, int size) throws Exception {
        return dao.selectTableForPage(connection, bean, page, size);
    }

}
