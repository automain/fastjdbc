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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A common template of DAO layer, all the classes of DAO layer should extends this class.</p>
 * <p>Considering performance and large amount of data support,
 * the primary key of table should be Integer type(int unsigned in mysql) and auto increment.</p>
 * <p>Data permissions should be considered when design the table in database.</p>
 * <p>In business,we usually need to customize the query conditions and return the {@link PageBean} object,
 * a new method is needed to be added to the DAO class in this condition.</p>
 *
 * @since 1.0
 */
public class BaseDao {

    /**
     * The logger facade.
     *
     * @since 2.2
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

    /**
     * Insert the not null properties of bean.
     *
     * @param bean bean to insert
     * @param <T>  {@link BaseBean} type class
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    public static <T extends BaseBean> int insertIntoTable(T bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        return executeUpdate(sql, paramList);
    }

    /**
     * Insert the not null properties of bean and return the generated primary key.
     *
     * @param bean bean to insert
     * @param <T>  {@link BaseBean} type class
     * @return generated primary key
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.0
     */
    public static <T extends BaseBean> Integer insertIntoTableReturnId(T bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        return executeUpdateReturnId(sql, paramList);
    }

    /**
     * Batch insert the properties of bean list.
     *
     * @param list list of bean to insert
     * @param <T>  {@link BaseBean} type class
     * @return count of insert rows
     * @throws SQLException exception when insert failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    public static <T extends BaseBean> int batchInsertIntoTable(List<T> list) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getBatchInsertSql(list, paramList);
        return executeUpdate(sql, paramList);
    }

    /**
     * Update the properties of bean by the primary key of bean, the primary key
     * property should not null otherwise nothing will be updated, the primary column
     * should be named by id and int type in mysql.
     *
     * @param bean bean to update
     * @param all  true to update all column of bean, false to update not null column of bean
     * @param <T>  {@link BaseBean} type class
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int updateTableById(T bean, boolean all) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(all);
        Object id = columnMap.get("id");
        columnMap.remove("id");
        List<Object> paramList = new ArrayList<Object>(columnMap.size() + 1);
        String columnParamSql = makeColumnParamSql(columnMap, paramList, ", ");
        paramList.add(id);
        return executeUpdate("UPDATE " + bean.tableName() + " SET " + columnParamSql + " WHERE id = ?", paramList);
    }

    /**
     * Update the properties of bean by the gid of bean, the column named gid
     * should be exists and should not null otherwise nothing will be updated.
     *
     * @param bean bean to update
     * @param all  true to update all column of bean, false to update not null column of bean
     * @param <T>  {@link BaseBean} type class
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int updateTableByGid(T bean, boolean all) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(all);
        columnMap.remove("id");
        List<Object> paramList = new ArrayList<Object>(columnMap.size() + 1);
        String columnParamSql = makeColumnParamSql(columnMap, paramList, ", ");
        paramList.add(columnMap.get("gid"));
        return executeUpdate("UPDATE " + bean.tableName() + " SET " + columnParamSql + " WHERE gid = ?", paramList);
    }

    /**
     * Update the properties of bean by the given id list, the primary column
     * should be named by id and int type in mysql.
     *
     * @param bean   bean to update
     * @param idList a list id of the beans which will be updated
     * @param all    true to update all column of bean, false to update not null column of bean
     * @param <T>    {@link BaseBean} type class
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int updateTableByIdList(T bean, List<Integer> idList, boolean all) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(all);
        columnMap.remove("id");
        List<Object> paramList = new ArrayList<Object>(columnMap.size() + idList.size());
        String columnParamSql = makeColumnParamSql(columnMap, paramList, ", ");
        paramList.addAll(idList);
        return executeUpdate("UPDATE " + bean.tableName() + " SET " + columnParamSql + " WHERE id" + makeInStr(idList), paramList);
    }

    /**
     * Update the properties of bean by the given gid list.
     *
     * @param bean    bean to update
     * @param gidList a list gid of the beans which will be updated
     * @param all     true to update all column of bean, false to update not null column of bean
     * @param <T>     {@link BaseBean} type class
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int updateTableByGidList(T bean, List<String> gidList, boolean all) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(all);
        columnMap.remove("id");
        List<Object> paramList = new ArrayList<Object>(columnMap.size() + gidList.size());
        String columnParamSql = makeColumnParamSql(columnMap, paramList, ", ");
        paramList.addAll(gidList);
        return executeUpdate("UPDATE " + bean.tableName() + " SET " + columnParamSql + " WHERE gid" + makeInStr(gidList), paramList);
    }

    /**
     * Update the properties of bean by the query result of param bean.
     *
     * @param paramBean          param bean to query the rows to update by the not null columns
     * @param newBean            bean to update
     * @param insertWhenNotExist whether or not to insert when the query returns nothing
     * @param updateMulti        whether or not to update multi result when the query returns more than one result
     * @param all                true to update all column of bean, false to update not null column of bean
     * @param <T>                {@link BaseBean} type class
     * @return count of updated rows
     * @throws SQLException exception when update failed
     * @see BaseBean#columnMap(boolean)
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int updateTable(T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti, boolean all) throws SQLException {
        String tableName = paramBean.tableName();
        Map<String, Object> newColumnMap = newBean.columnMap(all);
        newColumnMap.remove("id");
        Map<String, Object> paramColumnMap = paramBean.columnMap(false);
        StringBuilder paramBuilder = new StringBuilder();
        List<Object> paramList = null;
        List<Object> newList = new ArrayList<Object>(newColumnMap.size());
        if (!paramColumnMap.isEmpty()) {
            paramList = new ArrayList<Object>(paramColumnMap.size());
            paramBuilder.append(" WHERE ").append(makeColumnParamSql(paramColumnMap, paramList, " AND "));
        }
        if (insertWhenNotExist) {
            ResultSet rs = null;
            try {
                rs = executeSelectReturnResultSet("SELECT 1 FROM " + tableName + paramBuilder.toString() + " LIMIT 1", paramList);
                if (!rs.next()) {
                    return insertIntoTable(newBean);
                }
            } finally {
                ConnectionPool.close(rs);
            }
        }
        StringBuilder updateBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ").append(makeColumnParamSql(newColumnMap, newList, ", ")).append(paramBuilder);
        if (!updateMulti) {
            updateBuilder.append(" LIMIT 1");
        }
        if (paramList != null) {
            newList.addAll(paramList);
        }
        return executeUpdate(updateBuilder.toString(), newList);
    }

    /**
     * Soft delete a bean by the given id.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    public static <T extends BaseBean> int softDeleteTableById(T bean) throws SQLException {
        return executeUpdate("UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE id = ?", List.of(bean.columnMap(false).get("id")));
    }

    /**
     * Soft delete a bean by the given gid.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 2.1
     */
    public static <T extends BaseBean> int softDeleteTableByGid(T bean) throws SQLException {
        return executeUpdate("UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE gid = ?", List.of(bean.columnMap(false).get("gid")));
    }

    /**
     * Soft delete a bean by the given id list.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param bean   bean object
     * @param idList a list id of the beans which will be soft deleted
     * @param <T>    {@link BaseBean} type class
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 1.0
     */
    public static <T extends BaseBean> int softDeleteTableByIdList(T bean, List<Integer> idList) throws SQLException {
        return executeUpdate("UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE id" + makeInStr(idList), idList);
    }

    /**
     * Soft delete a bean by the given gid list.
     * The column of delete mark should named {@code is_valid} with int type(tinyint in mysql) and
     * {@code 1} represent the row is valid,
     * {@code 0} represent the row is invalid.
     *
     * @param bean    bean object
     * @param gidList a list gid of the beans which will be soft deleted
     * @param <T>     {@link BaseBean} type class
     * @return count of soft deleted rows
     * @throws SQLException exception when soft delete
     * @since 2.1
     */
    public static <T extends BaseBean> int softDeleteTableByGidList(T bean, List<String> gidList) throws SQLException {
        return executeUpdate("UPDATE " + bean.tableName() + " SET is_valid = 0 WHERE gid" + makeInStr(gidList), gidList);
    }

    /**
     * Delete a bean by the given id.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    public static <T extends BaseBean> int deleteTableById(T bean) throws SQLException {
        return executeUpdate("DELETE FROM " + bean.tableName() + " WHERE id = ?", List.of(bean.columnMap(false).get("id")));
    }

    /**
     * Delete a bean by the given gid.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 2.1
     */
    public static <T extends BaseBean> int deleteTableByGid(T bean) throws SQLException {
        return executeUpdate("DELETE FROM " + bean.tableName() + " WHERE gid = ?", List.of(bean.columnMap(false).get("gid")));
    }

    /**
     * Delete a bean by the given id list.
     *
     * @param bean   bean object
     * @param idList a list id of the beans which will be deleted
     * @param <T>    {@link BaseBean} type class
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 1.3
     */
    public static <T extends BaseBean> int deleteTableByIdList(T bean, List<Integer> idList) throws SQLException {
        return executeUpdate("DELETE FROM " + bean.tableName() + " WHERE id" + makeInStr(idList), idList);
    }

    /**
     * Delete a bean by the given gid list.
     *
     * @param bean    bean object
     * @param gidList a list gid of the beans which will be deleted
     * @param <T>     {@link BaseBean} type class
     * @return count of deleted rows
     * @throws SQLException exception when delete
     * @since 2.1
     */
    public static <T extends BaseBean> int deleteTableByGidList(T bean, List<String> gidList) throws SQLException {
        return executeUpdate("DELETE FROM " + bean.tableName() + " WHERE gid" + makeInStr(gidList), gidList);
    }

    /**
     * Count the columns by the param bean.
     *
     * @param bean the param bean
     * @param <T>  {@link BaseBean} type class
     * @return amount of rows which match the param bean
     * @throws SQLException exception when query
     * @since 1.7
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> int countTableByBean(T bean) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(false);
        int size = columnMap.size();
        List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
        String sql = makeSelectTableSql(bean, columnMap, paramList, true);
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            ConnectionPool.close(rs);
        }
        return 0;
    }

    /**
     * Query a bean by the given id.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return the bean of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    public static <T extends BaseBean> T selectTableById(T bean) throws SQLException {
        return executeSelectReturnBean("SELECT * FROM " + bean.tableName() + " WHERE id = ?", List.of(bean.columnMap(false).get("id")), bean);
    }

    /**
     * Query a bean by the given gid.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return the bean of query result
     * @throws SQLException exception when query
     * @since 2.1
     */
    public static <T extends BaseBean> T selectTableByGid(T bean) throws SQLException {
        return executeSelectReturnBean("SELECT * FROM " + bean.tableName() + " WHERE gid = ?", List.of(bean.columnMap(false).get("gid")), bean);
    }

    /**
     * Query a bean by the given id list.
     *
     * @param bean   bean object
     * @param idList a list id of the beans to query
     * @param <T>    {@link BaseBean} type class
     * @return the bean list of query result
     * @throws SQLException exception when query
     * @since 1.0
     */
    public static <T extends BaseBean> List<T> selectTableByIdList(T bean, List<Integer> idList) throws SQLException {
        return executeSelectReturnList("SELECT * FROM " + bean.tableName() + " WHERE id" + makeInStr(idList), idList, bean);
    }

    /**
     * Query a bean by the given gid list.
     *
     * @param bean    bean object
     * @param gidList a list gid of the beans to query
     * @param <T>     {@link BaseBean} type class
     * @return the bean list of query result
     * @throws SQLException exception when query
     * @since 2.1
     */
    public static <T extends BaseBean> List<T> selectTableByGidList(T bean, List<String> gidList) throws SQLException {
        return executeSelectReturnList("SELECT * FROM " + bean.tableName() + " WHERE gid" + makeInStr(gidList), gidList, bean);
    }

    /**
     * Query a bean by the param bean, match all the not null properties equals.
     * When multi rows match the condition, return the first one.
     *
     * @param bean the param bean
     * @param <T>  {@link BaseBean} type class
     * @return the first of query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> T selectOneTableByBean(T bean) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(false);
        int size = columnMap.size();
        List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
        String sql = makeSelectTableSql(bean, columnMap, paramList, false) + " LIMIT 1";
        return executeSelectReturnBean(sql, paramList, bean);
    }

    /**
     * Query list of beans by the param bean, match all the not null properties equals.
     *
     * @param bean the param bean
     * @param <T>  {@link BaseBean} type class
     * @return all query results
     * @throws SQLException exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> List<T> selectTableByBean(T bean) throws SQLException {
        Map<String, Object> columnMap = bean.columnMap(false);
        int size = columnMap.size();
        List<Object> paramList = new ArrayList<Object>(size > 0 ? size : 1);
        String sql = makeSelectTableSql(bean, columnMap, paramList, false);
        return executeSelectReturnList(sql, paramList, bean);
    }

    /**
     * Query all rows.
     *
     * @param bean bean object
     * @param <T>  {@link BaseBean} type class
     * @return all rows
     * @throws SQLException exception when query
     * @since 1.0
     */
    public static <T extends BaseBean> List<T> selectAllTable(T bean) throws SQLException {
        return executeSelectReturnList("SELECT * FROM " + bean.tableName(), null, bean);
    }

    /**
     * Query list of beans by the param bean for page, match all the not null properties equals.
     *
     * @param bean the param bean
     * @param page page number
     * @param size the count of data displayed on each page
     * @param <T>  {@link BaseBean} type class
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @see PageBean
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> PageBean<T> selectTableForPage(T bean, int page, int size) throws Exception {
        Map<String, Object> columnMap = bean.columnMap(false);
        int columnSize = columnMap.size();
        List<Object> paramList = new ArrayList<Object>(columnSize > 0 ? columnSize : 1);
        String sql = makeSelectTableSql(bean, columnMap, paramList, false);
        String countSql = "SELECT COUNT(1)" + sql.substring(8);
        PageParamBean pageParamBean = new PageParamBean()
                .setBean(bean)
                .setCountSql(countSql)
                .setCountParamList(paramList)
                .setSql(sql)
                .setParamList(paramList)
                .setPage(page)
                .setSize(size);
        return selectTableForPage(pageParamBean);
    }

    /**
     * Query list of beans by the param bean for page by given sql and param list.
     *
     * @param pageParamBean {@link PageParamBean} object
     * @param <T>           {@link BaseBean} type class
     * @return {@link PageBean} object
     * @throws Exception exception when query
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseBean> PageBean<T> selectTableForPage(PageParamBean<T> pageParamBean) throws Exception {
        PageBean pageBean = new PageBean().setTotal(0).setPage(1);
        T bean = pageParamBean.getBean();
        int size = Math.max(1, pageParamBean.getSize());
        int page = Math.max(1, pageParamBean.getPage());
        List<Object> paramList = pageParamBean.getParamList();
        ResultSet countResult = null;
        ResultSet pageResult = null;
        List<T> data = new ArrayList<T>();
        try {
            int total = 0;
            countResult = executeSelectReturnResultSet(pageParamBean.getCountSql(), pageParamBean.getCountParamList());
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
                pageResult = executeSelectReturnResultSet(pageParamBean.getSql() + " LIMIT ?, ?", paramList);
                while (pageResult.next()) {
                    data.add((T) bean.beanFromResultSet(pageResult));
                }
            }
            pageBean.setTotal(total).setPage(page).setData(data);
        } finally {
            ConnectionPool.close(countResult);
            ConnectionPool.close(pageResult);
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
    public static String makeInStr(List<?> paramList) {
        StringBuilder builder = new StringBuilder(" IN(");
        for (int i = paramList.size(); i > 0; i--) {
            builder.append("?");
            if (i > 1) {
                builder.append(", ");
            }
        }
        return builder.append(")").toString();
    }

    /**
     * Join the sql of insert into table and add param to param list.
     *
     * @param bean      bean object
     * @param paramList param list
     * @param <T>       {@link BaseBean} type class
     * @return sql string for insert into table
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getInsertSql(T bean, List<Object> paramList) {
        Map<String, Object> columnMap = bean.columnMap(false);
        int size = columnMap.size();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(bean.tableName()).append("(");
        StringBuilder valueBuilder = new StringBuilder();
        int offset = 1;
        for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
            sqlBuilder.append(entry.getKey());
            paramList.add(entry.getValue());
            valueBuilder.append("?");
            if (offset < size) {
                sqlBuilder.append(", ");
                valueBuilder.append(", ");
            }
            offset++;
        }
        return sqlBuilder.append(") VALUES (").append(valueBuilder).append(")").toString();
    }

    /**
     * Join the sql of batch insert into table and add param to param list.
     *
     * @param list      list of bean object
     * @param paramList param list
     * @param <T>       {@link BaseBean} type class
     * @return sql string for insert into table
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getBatchInsertSql(List<T> list, List<Object> paramList) {
        T bean = list.get(0);
        List<String> columnList = new ArrayList<String>(bean.columnMap(true).keySet());
        columnList.remove("id");
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(bean.tableName()).append("(");
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
            for (String s : columnList) {
                paramList.add(columnMap.get(s));
            }
        }
        return sqlBuilder.append(paramBuilder).toString();
    }

    /**
     * Join the not null column sql by separator and add param to param list.
     *
     * @param columnMap the column map collection
     * @param paramList param list
     * @param separator the separator to connect sql string
     * @return sql string
     * @since 1.0
     */
    private static String makeColumnParamSql(Map<String, Object> columnMap, List<Object> paramList, String separator) {
        StringBuilder sqlBuilder = new StringBuilder();
        int size = columnMap.size();
        int offset = 1;
        for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
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
     * @param bean       bean object
     * @param columnMap  the column map collection
     * @param paramList  param list
     * @param isCountSql whether the sql is for count
     * @param <T>        class which implement {@link BaseBean}
     * @return sql string
     * @since 1.0
     */
    private static <T extends BaseBean> String makeSelectTableSql(T bean, Map<String, Object> columnMap, List<Object> paramList, boolean isCountSql) {
        String head = isCountSql ? "SELECT COUNT(1)" : "SELECT *";
        return head + " FROM " + bean.tableName() + " WHERE " + makeColumnParamSql(columnMap, paramList, " AND ");
    }

    /**
     * Execute update type sql.
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    protected static int executeUpdate(String sql, List<?> paramList) throws SQLException {
        Connection connection = ConnectionPool.CONNECTION_POOL.get();
        if (connection == null || connection.isClosed() || connection.isReadOnly()) {
            throw new RuntimeException("connection object must not null and not closed and not read only");
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            LOGGER.debug(makeLogSql(sql, paramList));
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
     * @param sql       sql to execute
     * @param paramList param list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @see BaseDao#insertIntoTableReturnId(BaseBean)
     * @since 1.0
     */
    protected static Integer executeUpdateReturnId(String sql, List<?> paramList) throws SQLException {
        Connection connection = ConnectionPool.CONNECTION_POOL.get();
        if (connection == null || connection.isClosed() || connection.isReadOnly()) {
            throw new RuntimeException("connection object must not null and not closed and not read only");
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LOGGER.debug(makeLogSql(sql, paramList));
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
     * @param sql       sql to execute
     * @param paramList param list
     * @param bean      bean object which type is same as the return one
     * @param <T>       {@link BaseBean} type class
     * @return child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> T executeSelectReturnBean(String sql, List<?> paramList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            return rs.next() ? (T) bean.beanFromResultSet(rs) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a String type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return {@link String} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static String executeSelectReturnString(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            return rs.next() ? rs.getString(1) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a Integer type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return {@link Integer} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static Integer executeSelectReturnInteger(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            return rs.next() ? rs.getInt(1) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a Long type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return {@link Long} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static Long executeSelectReturnLong(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            return rs.next() ? rs.getLong(1) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a BigDecimal type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return {@link BigDecimal} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static BigDecimal executeSelectReturnBigDecimal(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
            return rs.next() ? rs.getBigDecimal(1) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a child bean list of {@link BaseBean}.
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @param bean      bean object which type is same as the return one
     * @param <T>       {@link BaseBean} type class
     * @return list of child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> List<T> executeSelectReturnList(String sql, List<?> paramList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(sql, paramList);
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
     * Execute a select sql and return a list of String type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return a list of {@link String} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static List<String> executeSelectReturnStringList(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            List<String> result = new ArrayList<String>();
            rs = executeSelectReturnResultSet(sql, paramList);
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a list of Integer type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return a list of {@link Integer} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static List<Integer> executeSelectReturnIntegerList(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            List<Integer> result = new ArrayList<Integer>();
            rs = executeSelectReturnResultSet(sql, paramList);
            while (rs.next()) {
                result.add(rs.getInt(1));
            }
            return result;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a list of Long type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return a list of {@link Long} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static List<Long> executeSelectReturnLongList(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            List<Long> result = new ArrayList<Long>();
            rs = executeSelectReturnResultSet(sql, paramList);
            while (rs.next()) {
                result.add(rs.getLong(1));
            }
            return result;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a list of BigDecimal type result
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return a list of {@link BigDecimal} type result
     * @throws SQLException exception when execute sql
     * @since 2.5
     */
    protected static List<BigDecimal> executeSelectReturnBigDecimalList(String sql, List<?> paramList) throws SQLException {
        ResultSet rs = null;
        try {
            List<BigDecimal> result = new ArrayList<BigDecimal>();
            rs = executeSelectReturnResultSet(sql, paramList);
            while (rs.next()) {
                result.add(rs.getBigDecimal(1));
            }
            return result;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * <p>Execute a select sql and return a {@link ResultSet} object.</p>
     * <p>Note: When the existing methods do not meet the requirements, you can call this for customer select sql,
     * just as important, you must call {@link ConnectionPool#close(ResultSet)} to close the {@link ResultSet} object at last.</p>
     *
     * @param sql       sql to execute
     * @param paramList param list
     * @return {@link ResultSet} object
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    protected static ResultSet executeSelectReturnResultSet(String sql, List<?> paramList) throws SQLException {
        Connection connection = ConnectionPool.CONNECTION_POOL.get();
        if (connection == null || connection.isClosed()) {
            throw new RuntimeException("connection object must not null and not closed");
        }
        try {
            LOGGER.debug(makeLogSql(sql, paramList));
            PreparedStatement stmt = connection.prepareStatement(sql);
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
        LOGGER.error("JDBC error sql: {}", makeLogSql(sql, paramList));
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
            for (Object o : paramList) {
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
