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
import com.github.fastjdbc.bean.PageParamBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A common template of DAO layer, all the classes of DAO layer should extends this class.</p>
 * <p>Considering performance and large amount of data support,
 * the primary key of table should be Integer type(int unsigned in mysql) and auto increment.</p>
 * <p>Data permissions should be considered when design the table in database.</p>
 * <p>In business,we usually need to customize the query conditions and return the {@link PageBean} object,
 * a new method is needed to be added to the DAO class in this condition.</p>
 *
 * @param <T> an object which implement {@link BaseBean}
 * @since 1.0
 */
public class BaseDao<T extends BaseBean> {

    /**
     * Insert the not null properties of bean.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to insert
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    protected int insertIntoTable(ConnectionBean connection, T bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        if (sql != null) {
            return executeUpdate(connection, sql, paramList);
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
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    protected Integer insertIntoTableReturnId(ConnectionBean connection, T bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        if (sql != null) {
            return executeUpdateReturnId(connection, sql, paramList);
        }
        return 0;
    }

    /**
     * Batch insert the properties of bean list.
     *
     * @param connection ConnectionBean object
     * @param list       list of bean to insert
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    protected int batchInsertIntoTable(ConnectionBean connection, List<T> list) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getBatchInsertSql(list, paramList);
        if (sql != null) {
            return executeUpdate(connection, sql, paramList);
        }
        return 0;
    }

    /**
     * Update the properties of bean by the primary key of bean, the primary key
     * property should not null otherwise nothing will be updated.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to update
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    protected int updateTable(ConnectionBean connection, T bean, boolean all) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(all).entrySet();
                if (!entrySet.isEmpty()) {
                    List<Object> paramList = new ArrayList<Object>(entrySet.size() + 1);
                    StringBuilder sqlBuilder = new StringBuilder("UPDATE ")
                            .append(tableName).append(" SET ")
                            .append(makeColumnParamSql(entrySet, paramList, ", "))
                            .append(" WHERE ").append(bean.primaryKey()).append(" = ?");
                    paramList.add(bean.primaryValue());
                    return executeUpdate(connection, sqlBuilder.toString(), paramList);
                }
            }
        }
        return 0;
    }

    /**
     * Update the properties of bean by the given id list.
     *
     * @param connection ConnectionBean object
     * @param bean       bean to update
     * @param idList     a list id of the beans which will be updated
     * @param all        true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    protected int updateTableByIdList(ConnectionBean connection, T bean, List<Integer> idList, boolean all) throws SQLException {
        if (bean != null && idList != null && !idList.isEmpty()) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(all).entrySet();
                if (!entrySet.isEmpty()) {
                    int idSize = idList.size();
                    String inStr = makeInStr(idList);
                    List<Object> paramList = new ArrayList<Object>(entrySet.size() + idSize);
                    StringBuilder sqlBuilder = new StringBuilder("UPDATE ")
                            .append(tableName).append(" SET ")
                            .append(makeColumnParamSql(entrySet, paramList, ", "))
                            .append(" WHERE ").append(bean.primaryKey()).append(inStr);
                    paramList.addAll(idList);
                    return executeUpdate(connection, sqlBuilder.toString(), paramList);
                }
            }
        }
        return 0;
    }

    /**
     * Update the properties of bean by the query result of param bean.
     *
     * @param connection         ConnectionBean object
     * @param paramBean          param bean to query the rows to update by the not null columns
     * @param newBean            bean to update
     * @param insertWhenNotExist whether or not to insert when the query returns nothing
     * @param updateMulti        whether or not to update multi result when the query returns more than one result
     * @param all                true to update all column of bean, false to update not null column of bean
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    protected int updateTable(ConnectionBean connection, T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti, boolean all) throws SQLException {
        if (paramBean != null && newBean != null) {
            String tableName = paramBean.tableName();
            String tableNameCheck = newBean.tableName();
            if (tableName != null && tableName.equals(tableNameCheck)) {
                Set<Map.Entry<String, Object>> newEntrySet = newBean.columnMap(all).entrySet();
                if (!newEntrySet.isEmpty()) {
                    Set<Map.Entry<String, Object>> paramEntrySet = paramBean.columnMap(false).entrySet();
                    StringBuilder paramBuilder = new StringBuilder();
                    List<Object> paramList = null;
                    List<Object> newList = new ArrayList<Object>(newEntrySet.size());
                    if (!paramEntrySet.isEmpty()) {
                        paramList = new ArrayList<Object>(paramEntrySet.size());
                        paramBuilder.append(" WHERE ").append(makeColumnParamSql(paramEntrySet, paramList, " AND "));
                    }
                    if (insertWhenNotExist) {
                        ResultSet rs = null;
                        try {
                            rs = executeSelectReturnResultSet(connection, "SELECT 1 FROM " + tableName + paramBuilder.toString() + " LIMIT 1", paramList);
                            if (!rs.next()) {
                                return insertIntoTable(connection, newBean);
                            }
                        } finally {
                            ConnectionPool.close(rs);
                        }
                    }
                    StringBuilder updateBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ").append(makeColumnParamSql(newEntrySet, newList, ", ")).append(paramBuilder);
                    if (!updateMulti) {
                        updateBuilder.append(" LIMIT 1");
                    }
                    if (paramList != null) {
                        newList.addAll(paramList);
                    }
                    return executeUpdate(connection, updateBuilder.toString(), newList);
                }
            }
        }
        return 0;
    }

    /**
     * Soft delete a bean by the given id.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param id         id of the bean
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    protected int softDeleteTableById(ConnectionBean connection, T bean, Integer id) throws SQLException {
        return executeUpdate(connection, "UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE " + bean.primaryKey() + " = ?", List.of(id));
    }

    /**
     * Soft delete a bean by the given id list.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param idList     a list id of the beans which will be soft deleted
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    protected int softDeleteTableByIdList(ConnectionBean connection, T bean, List<Integer> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return 0;
        } else {
            return executeUpdate(connection, "UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE " + bean.primaryKey() + inStr, idList);
        }
    }

    /**
     * Delete a bean by the given id.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param id         id of the bean
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    protected int deleteTableById(ConnectionBean connection, T bean, Integer id) throws SQLException {
        return executeUpdate(connection, "DELETE FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + " = ?", List.of(id));
    }

    /**
     * Delete a bean by the given id list.
     *
     * @param connection ConnectionBean object
     * @param bean       bean object
     * @param idList     a list id of the beans which will be deleted
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    protected int deleteTableByIdList(ConnectionBean connection, T bean, List<Integer> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return 0;
        } else {
            return executeUpdate(connection, "DELETE FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + inStr, idList);
        }
    }

    /**
     * Count the columns by the param bean.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @return amount of rows which match the param bean
     * @throws SQLException exception when query
     * @since 1.7
     */
    @SuppressWarnings("unchecked")
    protected int countTableByBean(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(false).entrySet();
                int size = entrySet.size();
                List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
                String sql = makeSelectTableSql(bean, entrySet, paramList);
                String countSql = "SELECT COUNT(1)" + sql.substring(8);
                ResultSet rs = null;
                try {
                    rs = executeSelectReturnResultSet(connection, countSql, paramList);
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                } finally {
                    ConnectionPool.close(rs);
                }
            }
        }
        return 0;
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
    protected T selectTableById(ConnectionBean connection, T bean, Integer id) throws SQLException {
        return executeSelectReturnBean(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + " = ?", List.of(id), bean);
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
    protected List<T> selectTableByIdList(ConnectionBean connection, T bean, List<Integer> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return new ArrayList<T>(1);
        } else {
            return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + inStr, idList, bean);
        }
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
    protected T selectOneTableByBean(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(false).entrySet();
                int size = entrySet.size();
                List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
                String sql = makeSelectTableSql(bean, entrySet, paramList) + " LIMIT 1";
                return executeSelectReturnBean(connection, sql, paramList, bean);
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
    protected List<T> selectTableByBean(ConnectionBean connection, T bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(false).entrySet();
                int size = entrySet.size();
                List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
                String sql = makeSelectTableSql(bean, entrySet, paramList);
                return executeSelectReturnList(connection, sql, paramList, bean);
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
    protected List<T> selectAllTable(ConnectionBean connection, T bean) throws SQLException {
        return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName(), null, bean);
    }

    /**
     * Query list of beans by the param bean for page, match all the not null properties equals.
     *
     * @param connection ConnectionBean object
     * @param bean       the param bean
     * @param page       page number
     * @param size       the count of data displayed on each page
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @see PageBean
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected PageBean<T> selectTableForPage(ConnectionBean connection, T bean, int page, int size) throws Exception {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(false).entrySet();
                int entrySize = entrySet.size();
                List<Object> paramList = new ArrayList<Object>(entrySize > 0 ? entrySize : 1);
                String sql = makeSelectTableSql(bean, entrySet, paramList);
                String countSql = "SELECT COUNT(1)" + sql.substring(8);
                PageParamBean pageParamBean = new PageParamBean()
                        .setConnection(connection)
                        .setBean(bean)
                        .setCountSql(countSql)
                        .setCountParamList(paramList)
                        .setSql(sql)
                        .setParamList(paramList)
                        .setPage(page)
                        .setSize(size);
                return selectTableForPage(pageParamBean);
            }
        }
        return new PageBean();
    }

    /**
     * Query list of beans by the param bean for page by given sql and param list.
     *
     * @param pageParamBean {@link PageParamBean} object
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected PageBean<T> selectTableForPage(PageParamBean<T> pageParamBean) throws Exception {
        PageBean pageBean = new PageBean();
        if (pageParamBean != null) {
            T bean = pageParamBean.getBean();
            String countSql = pageParamBean.getCountSql();
            String sql = pageParamBean.getSql();
            if (bean != null && countSql != null && sql != null) {
                int size = Math.max(1, pageParamBean.getSize());
                int page = Math.max(1, pageParamBean.getPage());
                ConnectionBean connection = pageParamBean.getConnection();
                List<Object> paramList = pageParamBean.getParamList();
                ResultSet countResult = null;
                ResultSet pageResult = null;
                List<T> data = new ArrayList<T>();
                try {
                    int total = 0;
                    countResult = executeSelectReturnResultSet(connection, countSql, pageParamBean.getCountParamList());
                    if (countResult.next()) {
                        total = countResult.getInt(1);
                    }
                    if (total == 0) {
                        page = 1;
                    } else {
                        int offset = (page - 1) * size;
                        if (offset > total) {
                            page = total / size + 1;
                        } else if (offset == total) {
                            page = total / size;
                        }
                        paramList.add((page - 1) * size);
                        paramList.add(size);
                        pageResult = executeSelectReturnResultSet(connection, sql + " LIMIT ?, ?", paramList);
                        while (pageResult.next()) {
                            data.add((T) bean.beanFromResultSet(pageResult));
                        }
                    }
                    pageBean.setTotal(total).setPage(page).setData(data);
                } finally {
                    ConnectionPool.close(countResult);
                    ConnectionPool.close(pageResult);
                }
            }
        }
        return pageBean;
    }

    /**
     * Join the placeholder by the param list size for sql statement.
     *
     * @param paramList param list
     * @return string of sql placeholder
     * @since 1.0
     */
    protected static String makeInStr(List<?> paramList) {
        if (paramList == null || paramList.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(" IN(");
            for (int i = paramList.size(); i > 0; i--) {
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
     * Join the sql of insert into table and add param to param list.
     *
     * @param bean      bean object
     * @param paramList param list
     * @param <T>       class which implement {@link BaseBean}
     * @return sql string for insert into table
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getInsertSql(T bean, List<Object> paramList) {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.columnMap(false).entrySet();
                if (!entrySet.isEmpty()) {
                    int size = entrySet.size();
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder.append("INSERT INTO ").append(tableName).append("(");
                    StringBuilder valueBuilder = new StringBuilder();
                    int offset = 1;
                    for (Map.Entry<String, Object> entry : entrySet) {
                        sqlBuilder.append(entry.getKey());
                        paramList.add(entry.getValue());
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
                    return sqlBuilder.toString();
                }
            }
        }
        return null;
    }

    /**
     * Join the sql of batch insert into table and add param to param list.
     *
     * @param list      list of bean object
     * @param paramList param list
     * @param <T>       class which implement {@link BaseBean}
     * @return sql string for insert into table
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getBatchInsertSql(List<T> list, List<Object> paramList) {
        if (list != null && !list.isEmpty()) {
            T bean = list.get(0);
            String tableName = bean.tableName();
            if (tableName != null) {
                List<String> columnList = new ArrayList<String>(bean.columnMap(true).keySet());
                if (!columnList.isEmpty()) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder.append("INSERT INTO ").append(tableName).append("(");
                    StringBuilder valueBuilder = new StringBuilder(" (");
                    for (int i = 0, size = columnList.size(), prev = size - 1; i < size; i++) {
                        sqlBuilder.append(columnList.get(i));
                        valueBuilder.append("?");
                        if (i < prev) {
                            sqlBuilder.append(", ");
                            valueBuilder.append(", ");
                        }
                    }
                    sqlBuilder.append(") VALUES");
                    valueBuilder.append(")");
                    StringBuilder paramBuilder = new StringBuilder();
                    for (int i = 0, size = list.size(), prev = size - 1; i < size; i++) {
                        paramBuilder.append(valueBuilder);
                        if (i < prev) {
                            paramBuilder.append(", ");
                        }
                        Map<String, Object> columnMap = list.get(i).columnMap(true);
                        for (int j = 0, columnSize = columnList.size(); j < columnSize; j++) {
                            paramList.add(columnMap.get(columnList.get(j)));
                        }
                    }
                    sqlBuilder.append(paramBuilder);
                    return sqlBuilder.toString();
                }
            }
        }
        return null;
    }

    /**
     * Join the not null column sql by separator and add param to param list.
     *
     * @param entrySet  the not null column map collection
     * @param paramList param list
     * @param separator the separator to connect sql string
     * @return sql string
     * @since 1.0
     */
    private static String makeColumnParamSql(Set<Map.Entry<String, Object>> entrySet, List<Object> paramList, String separator) {
        StringBuilder sqlBuilder = new StringBuilder();
        int size = entrySet.size();
        int offset = 1;
        for (Map.Entry<String, Object> entry : entrySet) {
            sqlBuilder.append(entry.getKey()).append(" = ?");
            paramList.add(entry.getValue());
            if (offset < size) {
                sqlBuilder.append(separator);
            }
            offset++;
        }
        return sqlBuilder.toString();
    }

    /**
     * Join the not null column sql for select and add param to param list.
     *
     * @param bean      bean object
     * @param entrySet  the not null column map collection
     * @param paramList param list
     * @param <T>       class which implement {@link BaseBean}
     * @return sql string
     * @since 1.0
     */
    private static <T extends BaseBean> String makeSelectTableSql(T bean, Set<Map.Entry<String, Object>> entrySet, List<Object> paramList) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(bean.tableName());
        int size = entrySet.size();
        if (size > 0) {
            sqlBuilder.append(" WHERE ").append(makeColumnParamSql(entrySet, paramList, " AND "));
        }
        return sqlBuilder.toString();
    }

    /**
     * Execute update type sql.
     *
     * @param connection ConnectionBean object
     * @param sql        sql to execute
     * @param paramList  param list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    protected static int executeUpdate(ConnectionBean connection, String sql, List<?> paramList) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("ConnectionBean object must not null");
        }
        Connection writeConnection = connection.getWriteConnection();
        if (writeConnection == null || writeConnection.isClosed()) {
            throw new RuntimeException("write connection must not null and not closed");
        }
        try (PreparedStatement stmt = writeConnection.prepareStatement(sql)) {
            if (connection.isPrintSql() && ConnectionPool.getLogger() != null) {
                ConnectionPool.getLogger().info(makeLogSql(sql, paramList));
            }
            setParams(stmt, paramList);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            printError(sql, paramList);
            throw e;
        }
    }

    /**
     * Execute update type sql, only for insert sql and return id.
     *
     * @param connection ConnectionBean object
     * @param sql        sql to execute
     * @param paramList  param list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @see com.github.fastjdbc.common.BaseDao#insertIntoTableReturnId(ConnectionBean, BaseBean)
     * @since 1.0
     */
    protected static Integer executeUpdateReturnId(ConnectionBean connection, String sql, List<?> paramList) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("ConnectionBean object must not null");
        }
        Connection writeConnection = connection.getWriteConnection();
        if (writeConnection == null || writeConnection.isClosed()) {
            throw new RuntimeException("write connection must not null and not closed");
        }
        try (PreparedStatement stmt = writeConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (connection.isPrintSql() && ConnectionPool.getLogger() != null) {
                ConnectionPool.getLogger().info(makeLogSql(sql, paramList));
            }
            setParams(stmt, paramList);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            printError(sql, paramList);
            throw e;
        }
    }

    /**
     * Execute a select sql and return a child bean of {@link BaseBean}.
     *
     * @param connection ConnectionBean object
     * @param sql        sql to execute
     * @param paramList  param list
     * @param bean       bean object which type is same as the return one
     * @param <T>        class which implement {@link BaseBean}
     * @return child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> T executeSelectReturnBean(ConnectionBean connection, String sql, List<?> paramList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(connection, sql, paramList);
            return rs.next() ? (T) bean.beanFromResultSet(rs) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a child bean list of {@link BaseBean}.
     *
     * @param connection ConnectionBean object
     * @param sql        sql to execute
     * @param paramList  param list
     * @param bean       bean object which type is same as the return one
     * @param <T>        class which implement {@link BaseBean}
     * @return list of child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> List<T> executeSelectReturnList(ConnectionBean connection, String sql, List<?> paramList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(connection, sql, paramList);
            List<T> resultList = new ArrayList<T>();
            while (rs.next()) {
                resultList.add((T) bean.beanFromResultSet(rs));
            }
            return resultList;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * <p>Execute a select sql and return a {@link ResultSet} object.</p>
     * <p>Note: When the existing methods do not meet the requirements, you can call this for customer select sql,
     * just as important, you must call {@link ConnectionPool#close(ResultSet)} to close the {@link ResultSet} object at last.</p>
     *
     * @param connection ConnectionBean object
     * @param sql        sql to execute
     * @param paramList  param list
     * @return {@link ResultSet} object
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    public static ResultSet executeSelectReturnResultSet(ConnectionBean connection, String sql, List<?> paramList) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("ConnectionBean object must not null");
        }
        Connection readConnection = connection.getReadConnection();
        if (readConnection == null || readConnection.isClosed()) {
            throw new RuntimeException("read connection must not null and not closed");
        }
        try {
            if (connection.isPrintSql() && ConnectionPool.getLogger() != null) {
                ConnectionPool.getLogger().info(makeLogSql(sql, paramList));
            }
            PreparedStatement stmt = readConnection.prepareStatement(sql);
            setParams(stmt, paramList);
            return stmt.executeQuery();
        } catch (SQLException e) {
            printError(sql, paramList);
            throw e;
        }
    }

    /**
     * Set paramters for the prepared statement.
     *
     * @param stmt      the prepared statement
     * @param paramList param list
     * @throws SQLException exception when set paramter
     * @since 1.0
     */
    private static void setParams(PreparedStatement stmt, List<?> paramList) throws SQLException {
        if (paramList != null) {
            for (int i = 0, size = paramList.size(); i < size; i++) {
                stmt.setObject(i + 1, paramList.get(i));
            }
        }
    }

    /**
     * Join a complete sql for print to console.
     *
     * @param sql       the original sql
     * @param paramList param list
     * @since 1.0
     */
    private static void printError(String sql, List<?> paramList) {
        ConnectionPool.getLogger().severe("JDBC exception error sql: " + makeLogSql(sql, paramList));
    }

    /**
     * Join a complete sql for print or log.
     *
     * @param sql       the original sql
     * @param paramList param list
     * @return the complete sql
     * @since 1.0
     */
    private static String makeLogSql(String sql, List<?> paramList) {
        if (paramList != null) {
            Object o = null;
            for (int i = 0, size = paramList.size(); i < size; i++) {
                o = paramList.get(i);
                if (o == null) {
                    sql = sql.replaceFirst("\\?", "NULL");
                } else {
                    sql = sql.replaceFirst("\\?", "'" + o.toString() + "'");
                }
            }
        }
        return sql;
    }

}
