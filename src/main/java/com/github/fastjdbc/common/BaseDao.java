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
import com.github.fastjdbc.bean.ConnectionPool;
import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.util.JDBCUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A common template of DAO layer, all the classes of DAO layer should extends this class</p>
 * <p>Considering performance and large amount of data support,
 * the primary key of table should be long type(bigint in mysql) and auto increment.</p>
 * <p>Data permissions should be considered when design the table in database.</p>
 * <p>In business,we usually need to customize the query conditions and return the {@link PageBean} object,
 * a new method is needed to be added to the DAO class in this condition.</p>
 *
 * @since 1.0
 */
public class BaseDao<T extends BaseBean> extends JDBCUtil {

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
    public int insertIntoTable(ConnectionBean connection, T bean) throws SQLException {
        List<Object> parameterList = new ArrayList<Object>();
        String sql = getInsertSql(bean, parameterList);
        if (sql != null) {
            return executeUpdate(connection, sql, parameterList);
        }
        return 0;
    }

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
    public Long insertIntoTableReturnId(ConnectionBean connection, T bean) throws SQLException {
        List<Object> parameterList = new ArrayList<Object>();
        String sql = getInsertSql(bean, parameterList);
        if (sql != null) {
            return executeUpdateReturnId(connection, sql, parameterList);
        }
        return 0L;
    }

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
    @SuppressWarnings("unchecked")
    public int updateTable(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                StringBuilder sqlBuilder = null;
                List<Object> parameterList = null;
                if (!entrySet.isEmpty()) {
                    parameterList = new ArrayList<Object>(entrySet.size() + 1);
                    sqlBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                    sqlBuilder.append(makeNotNullColumnParamSql(entrySet, parameterList, ", "));
                    sqlBuilder.append(" WHERE ").append(bean.primaryKey()).append(" = ?");
                    parameterList.add(bean.primaryValue());
                }
                if (sqlBuilder != null) {
                    return executeUpdate(connection, sqlBuilder.toString(), parameterList);
                }
            }
        }
        return 0;
    }

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
    @SuppressWarnings("unchecked")
    public int updateTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        if (bean != null && idList != null && !idList.isEmpty()) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                StringBuilder sqlBuilder = null;
                List<Object> parameterList = null;
                if (!entrySet.isEmpty()) {
                    int idSize = idList.size();
                    String inStr = makeInStr(idList);
                    parameterList = new ArrayList<Object>(entrySet.size() + idSize);
                    sqlBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                    sqlBuilder.append(makeNotNullColumnParamSql(entrySet, parameterList, ", "));
                    sqlBuilder.append(" WHERE ").append(bean.primaryKey()).append(inStr);
                    parameterList.addAll(idList);
                }
                if (sqlBuilder != null) {
                    return executeUpdate(connection, sqlBuilder.toString(), parameterList);
                }
            }
        }
        return 0;
    }

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
    @SuppressWarnings("unchecked")
    public int updateTable(ConnectionBean connection, T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException {
        if (paramBean != null && newBean != null) {
            String tableName = paramBean.tableName();
            String tableNameCheck = newBean.tableName();
            if (tableName != null && tableName.equals(tableNameCheck)) {
                Set<Map.Entry<String, Object>> newEntrySet = newBean.notNullColumnMap().entrySet();
                if (!newEntrySet.isEmpty()) {
                    Set<Map.Entry<String, Object>> paramEntrySet = paramBean.notNullColumnMap().entrySet();
                    StringBuilder paramBuilder = new StringBuilder();
                    List<Object> parameterList = null;
                    List<Object> newList = new ArrayList<Object>(newEntrySet.size());
                    if (!paramEntrySet.isEmpty()) {
                        parameterList = new ArrayList<Object>(paramEntrySet.size());
                        paramBuilder.append(" WHERE ").append(makeNotNullColumnParamSql(paramEntrySet, parameterList, " AND "));
                    }
                    if (insertWhenNotExist) {
                        ResultSet resultSet = executeSelectReturnResultSet(connection, "SELECT COUNT(1) FROM " + tableName + paramBuilder.toString(), parameterList);
                        boolean hasRecord = resultSet.next() && resultSet.getInt(1) > 0;
                        ConnectionPool.close(resultSet);
                        if (!hasRecord) {
                            return insertIntoTable(connection, newBean);
                        }
                    }
                    StringBuilder updateBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ").append(makeNotNullColumnParamSql(newEntrySet, newList, ", ")).append(paramBuilder);
                    if (!updateMulti) {
                        updateBuilder.append(" LIMIT 1");
                    }
                    if (parameterList != null) {
                        newList.addAll(parameterList);
                    }
                    return executeUpdate(connection, updateBuilder.toString(), newList);
                }
            }
        }
        return 0;
    }

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
    public int softDeleteTableById(ConnectionBean connection, T bean, Long id) throws SQLException {
        String sql = "UPDATE " + bean.tableName() + " SET is_delete = '1' WHERE " + bean.primaryKey() + " = ?";
        return executeUpdate(connection, sql, Collections.singletonList(id));
    }

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
    public int softDeleteTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return 0;
        }
        String sql = "UPDATE " + bean.tableName() + " SET is_delete = '1' WHERE " + bean.primaryKey() + inStr;
        return executeUpdate(connection, sql, idList);
    }

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
    public T selectTableById(ConnectionBean connection, T bean, Long id) throws SQLException {
        return executeSelectReturnBean(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + " = ?", Collections.singletonList(id), bean);
    }

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
    @SuppressWarnings("unchecked")
    public List<T> selectTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return new ArrayList<T>(1);
        }
        return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + inStr, idList, bean);
    }

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
    @SuppressWarnings("unchecked")
    public T selectOneTableByBean(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                List<Object> parameterList = new ArrayList<Object>(size);
                String sql = makeSelectTableSql(bean, entrySet, parameterList) + " LIMIT 1";
                return executeSelectReturnBean(connection, sql, parameterList, bean);
            }
        }
        return null;
    }

    /**
     * Query list of beans by the param bean, match all the not null properties equals.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @return all query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public List<T> selectTableByBean(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                List<Object> parameterList = new ArrayList<Object>(size);
                String sql = makeSelectTableSql(bean, entrySet, parameterList);
                return executeSelectReturnList(connection, sql, parameterList, bean);
            }
        }
        return new ArrayList<T>(1);
    }

    /**
     * Query all rows.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @return all rows
     * @throws SQLException exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public List<T> selectAllTable(ConnectionBean connection, T bean) throws SQLException {
        return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName(), null, bean);
    }

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
    @SuppressWarnings("unchecked")
    public PageBean<T> selectTableForPage(ConnectionBean connection, T bean, int page, int limit) throws Exception {
        if (bean != null) {
            String sql = null;
            List<Object> parameterList = null;
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                parameterList = new ArrayList<Object>(size);
                sql = makeSelectTableSql(bean, entrySet, parameterList);
            }
            return selectTableForPage(connection, bean, sql, parameterList, page, limit);
        }
        return new PageBean();
    }

    /**
     * Query list of beans by the param bean for page by given sql and parameter list.
     *
     * @param connection    ConnectionBean object
     * @param bean          bean object
     * @param sql           sql
     * @param parameterList parameter list
     * @param page          page number
     * @param limit         the count of data displayed on each page
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public PageBean<T> selectTableForPage(ConnectionBean connection, T bean, String sql, List<Object> parameterList, int page, int limit) throws Exception {
        PageBean pageBean = new PageBean();
        if (bean != null && sql != null) {
            limit = limit < 1 ? 1 : limit;
            page = page < 1 ? 1 : page;
            ResultSet countResult = null;
            ResultSet pageResult = null;
            List<T> data = new ArrayList<T>();
            try {
                countResult = executeSelectReturnResultSet(connection, makeCountSql(sql), parameterList);
                int count = 0;
                if (countResult.next()) {
                    count = countResult.getInt(1);
                }
                pageBean.setCount(count);
                parameterList.add((page - 1) * limit);
                parameterList.add(limit);
                pageResult = executeSelectReturnResultSet(connection, sql + " LIMIT ?, ?", parameterList);
                while (pageResult.next()) {
                    data.add((T) bean.beanFromResultSet(pageResult));
                }
                pageBean.setCurr(page);
                pageBean.setData(data);
            } finally {
                ConnectionPool.close(countResult);
                ConnectionPool.close(pageResult);
            }
        }
        return pageBean;
    }

    /**
     * Join the placeholder by the parameter list size for sql statement.
     *
     * @param parameterList parameter list
     * @return string of sql placeholder
     * @since 1.0
     */
    public static String makeInStr(List<?> parameterList) {
        if (parameterList == null || parameterList.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(" IN(");
            for (int i = parameterList.size(); i > 0; i--) {
                builder.append("?");
                if (i > 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            return builder.toString();
        }
    }

    /**
     * Join the sql of insert into table and add parameter to parameter list.
     *
     * @param bean          bean object
     * @param parameterList parameter list
     * @param <T>           class which implement {@link BaseBean}
     * @return sql string for insert into table
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getInsertSql(T bean, List<Object> parameterList) {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                StringBuilder sqlBuilder = new StringBuilder();
                if (!entrySet.isEmpty()) {
                    int size = entrySet.size();
                    sqlBuilder.append("INSERT INTO ").append(tableName).append("(");
                    StringBuilder valueBuilder = new StringBuilder();
                    int offset = 1;
                    for (Map.Entry<String, Object> entry : entrySet) {
                        sqlBuilder.append(entry.getKey());
                        parameterList.add(entry.getValue());
                        valueBuilder.append("?");
                        if (offset < size) {
                            sqlBuilder.append(", ");
                            valueBuilder.append(", ");
                        }
                        offset++;
                    }
                    sqlBuilder.append(") VALUES (");
                    valueBuilder.append(")");
                    sqlBuilder.append(valueBuilder);
                }
                return sqlBuilder.toString();
            }
        }
        return null;
    }

    /**
     * Join the not null column sql by separator and add parameter to parameter list.
     *
     * @param entrySet      the not null column map collection
     * @param parameterList parameter list
     * @param separator     the separator to connect sql string
     * @return sql string
     * @since 1.0
     */
    private static String makeNotNullColumnParamSql(Set<Map.Entry<String, Object>> entrySet, List<Object> parameterList, String separator) {
        StringBuilder sqlBuilder = new StringBuilder();
        int size = entrySet.size();
        int offset = 1;
        for (Map.Entry<String, Object> entry : entrySet) {
            sqlBuilder.append(entry.getKey()).append(" = ?");
            parameterList.add(entry.getValue());
            if (offset < size) {
                sqlBuilder.append(separator);
            }
            offset++;
        }
        return sqlBuilder.toString();
    }

    /**
     * Join the not null column sql for select and add parameter to parameter list.
     *
     * @param bean          bean object
     * @param entrySet      the not null column map collection
     * @param parameterList parameter list
     * @param <T>           class which implement {@link BaseBean}
     * @return sql string
     * @since 1.0
     */
    private static <T extends BaseBean> String makeSelectTableSql(T bean, Set<Map.Entry<String, Object>> entrySet, List<Object> parameterList) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(bean.tableName());
        int size = entrySet.size();
        if (size > 0) {
            sqlBuilder.append(" WHERE ").append(makeNotNullColumnParamSql(entrySet, parameterList, " AND "));
        }
        return sqlBuilder.toString();
    }

    /**
     * Make a count total rows sql for select table for page.
     *
     * @param sql the select for page sql
     * @return count sql
     * @since 1.0
     */
    private static String makeCountSql(String sql) {
        String upperSql = sql.toUpperCase();
        int beginIndex = upperSql.indexOf("SELECT") + 6;
        int endIndex = getFromIndex(upperSql, beginIndex);
        return sql.replace(sql.substring(beginIndex, endIndex), " COUNT(1) ");
    }

    /**
     * Find outermost {@code FROM} index for make count sql.
     *
     * @param sql        the select for page sql
     * @param beginIndex begin index to find
     * @return outermost {@code FROM} index
     * @since 1.0
     */
    private static int getFromIndex(String sql, int beginIndex) {
        int nextFromIndex = sql.indexOf("FROM", beginIndex);
        int nextSelectIndex = sql.indexOf("SELECT", beginIndex);
        if (nextSelectIndex < nextFromIndex && nextSelectIndex > 0) {
            return getFromIndex(sql, nextFromIndex + 4);
        } else {
            return nextFromIndex;
        }
    }

}
