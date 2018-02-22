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

public class JDBCUtil {


    protected static <T> int executeUpdate(ConnectionBean conn, String sql, List<T> parameters) throws SQLException {
        PreparedStatement stmt = null;
        if (conn != null) {
            Connection writeConnection = conn.getWriteConnection();
            if (writeConnection != null && !writeConnection.isClosed()) {
                try {
//                    if (OPEN_UPDATE_SQL_LOG) {
//                        UPDATE_LOGGER.info(makeLogSql(sql, parameters));
//                    }
//                    if (OPEN_DEBUG_SQL) {
//                        System.out.println(makeLogSql(sql, parameters));
//                    }
                    stmt = writeConnection.prepareStatement(sql);
                    setParameters(stmt, parameters);
                    return stmt.executeUpdate();
                } catch (SQLException e) {
                    printError(sql, parameters);
                    throw e;
                } finally {
                    ConnectionPool.close(stmt);
                }
            }
        }
        return 0;
    }

    private static int executeUpdateReturnId(ConnectionBean conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (conn != null) {
            Connection writeConnection = conn.getWriteConnection();
            if (writeConnection != null && !writeConnection.isClosed()) {
                try {
//                    if (OPEN_UPDATE_SQL_LOG) {
//                        UPDATE_LOGGER.info(makeLogSql(sql, parameters));
//                    }
//                    if (OPEN_DEBUG_SQL) {
//                        System.out.println(makeLogSql(sql, parameters));
//                    }
                    stmt = writeConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    setParameters(stmt, parameters);
                    stmt.executeUpdate();
                    rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        return 0;
                    }
                } catch (SQLException e) {
                    printError(sql, parameters);
                    throw e;
                } finally {
                    ConnectionPool.close(stmt);
                    ConnectionPool.close(rs);
                }
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> T executeQueryReturnBean(ConnectionBean conn, String sql, List<Object> parameters, T bean) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeQueryReturnResultSet(conn, sql, parameters);
            if (rs.next()) {
                return (T) bean.pickBeanFromResultSet(rs);
            }
        } finally {
            ConnectionPool.close(rs);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BaseBean> List<T> executeQueryReturnList(ConnectionBean conn, String sql, List<Object> parameters, T bean) throws SQLException {
        ResultSet rs = null;
        List<T> resultList = new ArrayList<T>();
        try {
            rs = executeQueryReturnResultSet(conn, sql, parameters);
            while (rs.next()) {
                resultList.add((T) bean.pickBeanFromResultSet(rs));
            }
        } finally {
            ConnectionPool.close(rs);
        }
        return resultList;
    }

    public static ResultSet executeQueryReturnResultSet(ConnectionBean conn, String sql, List<Object> parameters) throws SQLException {
        if (conn != null) {
            Connection queryConnection = conn.getReadConnection();
            if (queryConnection != null && !queryConnection.isClosed()) {
                try {
                    PreparedStatement stmt = queryConnection.prepareStatement(sql);
                    setParameters(stmt, parameters);
                    long start = System.nanoTime();
                    ResultSet rs = stmt.executeQuery();
//                    if (OPEN_SLOW_SQL_LOG && (System.nanoTime() - start > SLOW_SQL_STANDARD)) {
//                        SLOW_LOGGER.info(makeLogSql(sql, parameters));
//                    }
//                    if (OPEN_DEBUG_SQL) {
//                        System.out.println(makeLogSql(sql, parameters));
//                    }
                    return rs;
                } catch (SQLException e) {
                    printError(sql, parameters);
                    throw e;
                }
            }

        }
        return null;
    }

    public static <T> String makeInStr(List<T> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = parameters.size(); i > 0; i--) {
                builder.append("?");
                if (i > 1) {
                    builder.append(", ");
                }
            }
            return builder.toString();
        }
    }

    private static <T> void setParameters(PreparedStatement stmt, List<T> parameters) throws SQLException {
        if (parameters != null) {
            int offset = 1;
            for (Object param : parameters) {
                stmt.setObject(offset, param);
                offset++;
            }
        }
    }

    private static <T> void printError(String sql, List<T> parameters) {
        System.err.println("===============================JDBC Error Begin==============================");
        System.err.println(makeLogSql(sql, parameters));
        System.err.println("===============================JDBC Error End================================");
    }

    private static <T> String makeLogSql(String sql, List<T> parameters) {
        String logSql = sql;
        if (parameters != null) {
            for (Object o : parameters) {
                logSql = logSql.replaceFirst("\\?", "'" + o.toString() + "'");
            }
        }
        return logSql;
    }
}
