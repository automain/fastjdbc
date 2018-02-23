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
 * A common template of DAO layer, all the classes of DAO layer should extends this class.
 *
 * @see CommonDao
 * @since 1.0
 */
public class BaseDao extends JDBCUtil implements CommonDao {

    @Override
    public int insertIntoTable(ConnectionBean connection, BaseBean bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        if (sql != null) {
            return executeUpdate(connection, sql, paramList);
        }
        return 0;
    }

    @Override
    public Long insertIntoTableReturnId(ConnectionBean connection, BaseBean bean) throws SQLException {
        List<Object> paramList = new ArrayList<Object>();
        String sql = getInsertSql(bean, paramList);
        if (sql != null) {
            return executeUpdateReturnId(connection, sql, paramList);
        }
        return 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateTable(ConnectionBean connection, BaseBean bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                StringBuilder sqlBuilder = null;
                List<Object> objectList = null;
                if (!entrySet.isEmpty()) {
                    objectList = new ArrayList<Object>(entrySet.size() + 1);
                    sqlBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                    sqlBuilder.append(makeNotNullColumnParamSql(entrySet, objectList, ", "));
                    sqlBuilder.append(" WHERE ").append(bean.primaryKey()).append(" = ?");
                    objectList.add(bean.primaryValue());
                }
                if (sqlBuilder != null) {
                    return executeUpdate(connection, sqlBuilder.toString(), objectList);
                }
            }
        }
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateTable(ConnectionBean connection, BaseBean paramBean, BaseBean newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException {
        if (paramBean != null && newBean != null) {
            String tableName = paramBean.tableName();
            String tableNameCheck = newBean.tableName();
            if (tableName != null && tableName.equals(tableNameCheck)) {
                Set<Map.Entry<String, Object>> newEntrySet = newBean.notNullColumnMap().entrySet();
                if (!newEntrySet.isEmpty()) {
                    Set<Map.Entry<String, Object>> paramEntrySet = paramBean.notNullColumnMap().entrySet();
                    StringBuilder paramBuilder = new StringBuilder();
                    List<Object> paramList = null;
                    List<Object> newList = new ArrayList<Object>(newEntrySet.size());
                    if (!paramEntrySet.isEmpty()) {
                        paramList = new ArrayList<Object>(paramEntrySet.size());
                        paramBuilder.append(" WHERE ").append(makeNotNullColumnParamSql(paramEntrySet, paramList, " AND "));
                    }
                    if (insertWhenNotExist) {
                        ResultSet resultSet = executeSelectReturnResultSet(connection, "SELECT COUNT(1) FROM " + tableName + paramBuilder, paramList);
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
                    if (paramList != null) {
                        newList.addAll(paramList);
                    }
                    return executeUpdate(connection, updateBuilder.toString(), newList);
                }
            }
        }
        return 0;
    }

    @Override
    public int softDeleteTableById(ConnectionBean connection, BaseBean bean, Long id) throws SQLException {
        String sql = "UPDATE " + bean.tableName() + " SET is_delete = '1' WHERE " + bean.primaryKey() + " = ?";
        return executeUpdate(connection, sql, Collections.singletonList(id));
    }

    @Override
    public BaseBean selectTableById(ConnectionBean connection, BaseBean bean, Long id) throws SQLException {
        return executeSelectReturnBean(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + " = ?", Collections.singletonList(id), bean);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseBean selectOneTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                List<Object> objectList = new ArrayList<Object>(size);
                String sql = makeSelectTableSql(bean, entrySet, objectList) + " LIMIT 1";
                return executeSelectReturnBean(connection, sql, objectList, bean);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List selectTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException {
        if (bean != null) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                List<Object> objectList = new ArrayList<Object>(size);
                String sql = makeSelectTableSql(bean, entrySet, objectList);
                return executeSelectReturnList(connection, sql, objectList, bean);
            }
        }
        return new ArrayList<BaseBean>(1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseBean> selectAllTable(ConnectionBean connection, BaseBean bean) throws SQLException {
        return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName(), null, bean);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageBean<BaseBean> selectTableForPage(ConnectionBean connection, BaseBean bean, int page, int limit) throws Exception {
        PageBean pageBean = new PageBean();
        if (bean != null) {
            String sql = null;
            List<Object> parameters = null;
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                int size = entrySet.size();
                size = size > 0 ? size : 1;
                parameters = new ArrayList<Object>(size);
                sql = makeSelectTableSql(bean, entrySet, parameters);
            }
            if (sql != null) {
                limit = limit < 1 ? 1 : limit;
                page = page < 1 ? 1 : page;
                ResultSet countResult = null;
                ResultSet pageResult = null;
                List<BaseBean> data = new ArrayList<BaseBean>();
                try {
                    countResult = executeSelectReturnResultSet(connection, makeCountSql(sql), parameters);
                    int count = 0;
                    if (countResult.next()) {
                        count = countResult.getInt(1);
                    }
                    pageBean.setCount(count);
                    parameters.add((page - 1) * limit);
                    parameters.add(limit);
                    pageResult = executeSelectReturnResultSet(connection, sql + " LIMIT ?, ?", parameters);
                    while (pageResult.next()) {
                        data.add(bean.pickBeanFromResultSet(pageResult));
                    }
                    pageBean.setCurr(page);
                    pageBean.setData(data);
                } finally {
                    ConnectionPool.close(countResult);
                    ConnectionPool.close(pageResult);
                }
            }
        }
        return pageBean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseBean> selectTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return new ArrayList<BaseBean>(1);
        }
        return executeSelectReturnList(connection, "SELECT * FROM " + bean.tableName() + " WHERE " + bean.primaryKey() + " IN(" + inStr + ")", new ArrayList<Object>(idList), bean);
    }

    @Override
    public int softDeleteTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException {
        String inStr = makeInStr(idList);
        if (inStr == null) {
            return 0;
        }
        String sql = "UPDATE " + bean.tableName() + " SET is_delete = '1' WHERE " + bean.primaryKey() + " IN(" + inStr + ")";
        return executeUpdate(connection, sql, idList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateTableByIdList(ConnectionBean connection, BaseBean bean, List<Long> idList) throws SQLException {
        if (bean != null && idList != null && !idList.isEmpty()) {
            String tableName = bean.tableName();
            if (tableName != null) {
                Set<Map.Entry<String, Object>> entrySet = bean.notNullColumnMap().entrySet();
                StringBuilder sqlBuilder = null;
                List<Object> objectList = null;
                if (!entrySet.isEmpty()) {
                    int idSize = idList.size();
                    String inStr = makeInStr(idList);
                    objectList = new ArrayList<Object>(entrySet.size() + idSize);
                    sqlBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                    sqlBuilder.append(makeNotNullColumnParamSql(entrySet, objectList, ", "));
                    sqlBuilder.append(" WHERE ").append(bean.primaryKey()).append(" IN(").append(inStr).append(")");
                    objectList.addAll(idList);
                }
                if (sqlBuilder != null) {
                    return executeUpdate(connection, sqlBuilder.toString(), objectList);
                }
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseBean> String getInsertSql(T bean, List<Object> paramList) {
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
                }
                return sqlBuilder.toString();
            }
        }
        return null;
    }

    private static String makeNotNullColumnParamSql(Set<Map.Entry<String, Object>> entrySet, List<Object> objectList, String separator) {
        StringBuilder sqlBuilder = new StringBuilder();
        int size = entrySet.size();
        int offset = 1;
        for (Map.Entry<String, Object> entry : entrySet) {
            sqlBuilder.append(entry.getKey()).append(" = ?");
            objectList.add(entry.getValue());
            if (offset < size) {
                sqlBuilder.append(separator);
            }
            offset++;
        }
        return sqlBuilder.toString();
    }

    private static <T extends BaseBean> String makeSelectTableSql(T bean, Set<Map.Entry<String, Object>> entrySet, List<Object> parameterList) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(bean.tableName());
        int size = entrySet.size();
        if (size > 0) {
            sqlBuilder.append(" WHERE ");
            int offset = 1;
            for (Map.Entry<String, Object> entry : entrySet) {
                sqlBuilder.append(entry.getKey()).append(" = ?");
                parameterList.add(entry.getValue());
                if (offset < size) {
                    sqlBuilder.append(" AND ");
                }
                offset++;
            }
        }
        return sqlBuilder.toString();
    }

    private static String makeCountSql(String sql) {
        int firstFrom = sql.indexOf("FROM");
        int beginIndex = sql.indexOf("SELECT") + 6;
        String head = sql.substring(beginIndex, firstFrom);
        int selectCount = head.split("SELECT").length - 1;
        int endIndex = getFromIndex(sql, 0, selectCount);
        sql = sql.replace(sql.substring(beginIndex, endIndex), " COUNT(1) ");
        return sql;
    }

    private static int getFromIndex(String sql, int fromIndex, int count) {
        int nextIndex = sql.indexOf("FROM", fromIndex);
        if (count > 0) {
            return getFromIndex(sql, nextIndex + 4, count - 1);
        } else {
            return nextIndex;
        }
    }

}
