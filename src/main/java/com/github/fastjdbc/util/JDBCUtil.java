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

package com.github.fastjdbc.util;

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.ConnectionPool;
import com.github.fastjdbc.common.BaseBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Util for execute sql by connection, sql and parameters.
 *
 * @since 1.0
 */
public class JDBCUtil {

    /**
     * Execute update type sql.
     *
     * @param connection    ConnectionBean object
     * @param sql           sql to execute
     * @param parameterList parameter list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    protected static int executeUpdate(ConnectionBean connection, String sql, List<?> parameterList) throws SQLException {
        if (connection != null) {
            Connection writeConnection = connection.getWriteConnection();
            if (writeConnection != null && !writeConnection.isClosed()) {
                try (PreparedStatement stmt = writeConnection.prepareStatement(sql)) {
                    if (connection.isPrintSql()) {
                        System.out.println(makeLogSql(sql, parameterList));
                    }
                    setParameters(stmt, parameterList);
                    return stmt.executeUpdate();
                } catch (SQLException e) {
                    printError(sql, parameterList);
                    throw e;
                }
            }
        }
        return 0;
    }

    /**
     * Execute update type sql, only for insert sql and return id.
     *
     * @param connection    ConnectionBean object
     * @param sql           sql to execute
     * @param parameterList parameter list
     * @return success rows count
     * @throws SQLException exception when execute sql
     * @see com.github.fastjdbc.common.BaseDao#insertIntoTableReturnId(ConnectionBean, BaseBean)
     * @since 1.0
     */
    protected static Long executeUpdateReturnId(ConnectionBean connection, String sql, List<?> parameterList) throws SQLException {
        if (connection != null) {
            Connection writeConnection = connection.getWriteConnection();
            if (writeConnection != null && !writeConnection.isClosed()) {
                try (PreparedStatement stmt = writeConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    if (connection.isPrintSql()) {
                        System.out.println(makeLogSql(sql, parameterList));
                    }
                    setParameters(stmt, parameterList);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        return rs.next() ? rs.getLong(1) : 0L;
                    }
                } catch (SQLException e) {
                    printError(sql, parameterList);
                    throw e;
                }
            }
        }
        return 0L;
    }

    /**
     * Execute a select sql and return a child bean of {@link BaseBean}.
     *
     * @param connection    ConnectionBean object
     * @param sql           sql to execute
     * @param parameterList parameter list
     * @param bean          bean object which type is same as the return one
     * @param <T>           class which implement {@link BaseBean}
     * @return child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> T executeSelectReturnBean(ConnectionBean connection, String sql, List<?> parameterList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(connection, sql, parameterList);
            return rs.next() ? (T) bean.beanFromResultSet(rs) : null;
        } finally {
            ConnectionPool.close(rs);
        }
    }

    /**
     * Execute a select sql and return a child bean list of {@link BaseBean}.
     *
     * @param connection    ConnectionBean object
     * @param sql           sql to execute
     * @param parameterList parameter list
     * @param bean          bean object which type is same as the return one
     * @param <T>           class which implement {@link BaseBean}
     * @return list of child bean object of {@link BaseBean}
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> List<T> executeSelectReturnList(ConnectionBean connection, String sql, List<?> parameterList, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeSelectReturnResultSet(connection, sql, parameterList);
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
     * @param connection    ConnectionBean object
     * @param sql           sql to execute
     * @param parameterList parameter list
     * @return {@link ResultSet} object
     * @throws SQLException exception when execute sql
     * @since 1.0
     */
    protected static ResultSet executeSelectReturnResultSet(ConnectionBean connection, String sql, List<?> parameterList) throws SQLException {
        if (connection != null) {
            Connection queryConnection = connection.getReadConnection();
            if (queryConnection != null && !queryConnection.isClosed()) {
                try {
                    if (connection.isPrintSql()) {
                        System.out.println(makeLogSql(sql, parameterList));
                    }
                    PreparedStatement stmt = queryConnection.prepareStatement(sql);
                    setParameters(stmt, parameterList);
                    return stmt.executeQuery();
                } catch (SQLException e) {
                    printError(sql, parameterList);
                    throw e;
                }
            }
        }
        return null;
    }

    /**
     * Set paramters for the prepared statement.
     *
     * @param stmt          the prepared statement
     * @param parameterList parameter list
     * @throws SQLException exception when set paramter
     * @since 1.0
     */
    private static void setParameters(PreparedStatement stmt, List<?> parameterList) throws SQLException {
        if (parameterList != null) {
            int offset = 1;
            for (Object param : parameterList) {
                stmt.setObject(offset, param);
                offset++;
            }
        }
    }

    /**
     * Join a complete sql for print to console.
     *
     * @param sql           the original sql
     * @param parameterList parameter list
     * @since 1.0
     */
    private static void printError(String sql, List<?> parameterList) {
        System.err.println("===============================JDBC Error Start==============================");
        System.err.println(makeLogSql(sql, parameterList));
        System.err.println("===============================JDBC Error End================================");
    }

    /**
     * Join a complete sql for print or log.
     *
     * @param sql           the original sql
     * @param parameterList parameter list
     * @return the complete sql
     * @since 1.0
     */
    private static String makeLogSql(String sql, List<?> parameterList) {
        if (parameterList != null) {
            for (Object o : parameterList) {
                sql = sql.replaceFirst("\\?", "'" + o.toString() + "'");
            }
        }
        return sql;
    }

}
