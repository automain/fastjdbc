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

/**
 * <p>A connection pool class.</p>
 * <p>You should call {@link #init(String, HikariConfig, List)} method to init the global connection pool
 * on system start, this pool only support {@code HikariCP} data source.</p>
 *
 * @since 1.0
 */
public class ConnectionPool {

    /**
     * Database name.
     *
     * @since 1.0
     */
    private static String DATABASE_NAME;

    /**
     * Master database connection pool.
     *
     * @since 1.0
     */
    private static DataSource MASTER_POOL;

    /**
     * Default slave database connection pool.
     *
     * @since 1.0
     */
    private static DataSource DEFAULT_SLAVE_POOL;

    /**
     * Slave databases connection pool map, key is pool name, value is {@link DataSource} object.
     *
     * @since 1.0
     */
    private static Map<String, DataSource> POOL_MAP = new HashMap<>();

    /**
     * <p>Initialization method for init global connection pool.</p>
     * <p>{@code poolName} should be set for each {@link HikariConfig} and keep distinct.</p>
     *
     * @param databaseName    database name
     * @param masterConfig    the {@link HikariConfig} object of master datasource
     * @param slaveConfigList the {@link HikariConfig} object of slave datasources
     * @since 1.0
     */
    public static void init(String databaseName, HikariConfig masterConfig, List<HikariConfig> slaveConfigList) {
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

    /**
     * Method to change autoCommit to {@code false} and allowPoolSuspension to {@code true}.
     *
     * @param config {@link HikariConfig} object
     * @since 1.0
     */
    private static void initConfig(HikariConfig config) {
        config.setAutoCommit(false);
        config.setAllowPoolSuspension(true);
    }

    /**
     * <p>Get slave datasource object by given pool name.</p>
     * <p>When pool name is {@code null} or not in {@link #POOL_MAP}, {@link #DEFAULT_SLAVE_POOL} will be returned.</p>
     *
     * @param poolName the pool name
     * @return slave pool datasource object
     * @since 1.0
     */
    private static DataSource getSlaveDataSource(String poolName) {
        return POOL_MAP.getOrDefault(poolName, DEFAULT_SLAVE_POOL);
    }

    /**
     * Close the given connection without commit.
     *
     * @param connection the connection to close
     * @throws SQLException exception when close failed
     * @since 1.0
     */
    private static void close(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Commit the connection and then close.
     *
     * @param connection the connection to close
     * @throws SQLException exception when close failed
     * @since 1.0
     */
    private static void commitAndClose(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.commit();
            } finally {
                close(connection);
            }
        }
    }

    /**
     * Get the database name.
     *
     * @return database name
     * @since 1.0
     */
    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    /**
     * <p>Get {@link ConnectionBean} object by the given slave pool name.</p>
     * <p>The write connection is a connection from master datasource.</p>
     * <p>The read connection is a connection from slave datasource which pool name is the given pool name.</p>
     * <p>When slave pool not found, a connection from default slave pool will be used as read connection.</p>
     *
     * @param slavePoolName slave pool name
     * @return an object of {@link ConnectionBean}
     * @throws SQLException exception when get connection failed
     * @since 1.0
     */
    public static ConnectionBean getConnectionBean(String slavePoolName) throws SQLException {
        Connection writeConnection = MASTER_POOL.getConnection();
        writeConnection.setAutoCommit(false);
        return new ConnectionBean(writeConnection, getSlaveDataSource(slavePoolName).getConnection());
    }

    /**
     * Close the write connection with commit and close the read connection without commit.
     *
     * @param connection the connection to close
     * @throws SQLException exception when close failed
     * @since 1.0
     */
    public static void closeConnectionBean(ConnectionBean connection) throws SQLException {
        if (connection != null) {
            commitAndClose(connection.getWriteConnection());
            close(connection.getReadConnection());
        }
    }

    /**
     * Roll back the write connection when exception occurred and close both connection without commit.
     *
     * @param connection the connection to roll back
     * @throws SQLException exception when roll back failed
     * @since 1.0
     */
    public static void rollbackConnectionBean(ConnectionBean connection) throws SQLException {
        if (connection != null && connection.getWriteConnection() != null) {
            try {
                connection.getWriteConnection().rollback();
            } finally {
                close(connection.getWriteConnection());
                close(connection.getReadConnection());
            }
        }
    }

    /**
     * <p>Close the {@link ResultSet} object.</p>
     * <p>When {@link com.github.fastjdbc.util.JDBCUtil#executeSelectReturnResultSet(ConnectionBean, String, List)} called,
     * this method should be called to close {@link ResultSet} at last.</p>
     *
     * @param rs {@link ResultSet} object to close
     * @throws SQLException exception when close failed
     * @since 1.0
     */
    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
            rs = null;
            stmt = null;
        }
    }

}
