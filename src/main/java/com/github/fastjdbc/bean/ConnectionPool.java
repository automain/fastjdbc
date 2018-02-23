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

package com.github.fastjdbc.bean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionPool {

    private static String DATABASE_NAME;

    private static DataSource MASTER_POOL;

    private static DataSource DEFAULT_SLAVE_POOL;

    private static Map<String, DataSource> POOL_MAP = new HashMap<>();

    private static void init(String databaseName, HikariConfig masterConfig, List<HikariConfig> slaveConfigList) {
        if (DATABASE_NAME == null || MASTER_POOL == null || DEFAULT_SLAVE_POOL == null || POOL_MAP == null) {
            if (databaseName == null || masterConfig == null) {
                throw new RuntimeException("connection pool initialization failure");
            }
            DATABASE_NAME = databaseName;
            initConfig(masterConfig);
            MASTER_POOL = new HikariDataSource(masterConfig);
            POOL_MAP.put(masterConfig.getPoolName(), MASTER_POOL);
            if (slaveConfigList == null || slaveConfigList.isEmpty()) {
                DEFAULT_SLAVE_POOL = MASTER_POOL;
            } else {
                HikariDataSource dataSource = null;
                for (HikariConfig config : slaveConfigList) {
                    initConfig(config);
                    dataSource = new HikariDataSource(config);
                    if (DEFAULT_SLAVE_POOL == null) {
                        DEFAULT_SLAVE_POOL = dataSource;
                    }
                    POOL_MAP.put(config.getPoolName(), dataSource);
                }
            }
        }
    }

    private static void initConfig(HikariConfig config) {
        config.setAutoCommit(false);
        config.setAllowPoolSuspension(true);
    }

    private static DataSource getSlaveDataSource(String poolName) {
        return POOL_MAP.getOrDefault(poolName, DEFAULT_SLAVE_POOL);
    }

    private static void closeConnectionBeanWithOutCommit(ConnectionBean conn) throws SQLException {
        if (conn != null) {
            close(conn.getWriteConnection());
            close(conn.getReadConnection());
        }
    }

    private static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    private static void commitAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                close(conn);
            }
        }
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static ConnectionBean getConnectionBean(String slavePoolName) throws SQLException {
        Connection writeConnection = MASTER_POOL.getConnection();
        writeConnection.setAutoCommit(false);
        return new ConnectionBean(writeConnection, getSlaveDataSource(slavePoolName).getConnection());
    }

    public static void closeConnectionBean(ConnectionBean conn) throws SQLException {
        if (conn != null) {
            commitAndClose(conn.getWriteConnection());
            close(conn.getReadConnection());
        }
    }

    public static void rollbackConnectionBean(ConnectionBean conn) throws SQLException {
        if (conn != null && conn.getWriteConnection() != null) {
            try {
                conn.getWriteConnection().rollback();
            } finally {
                closeConnectionBeanWithOutCommit(conn);
            }
        }
    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            Statement stmt = rs.getStatement();
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
        }
    }

}
