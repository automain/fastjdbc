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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A connection pool class.</p>
 * <p>You should call {@link #init(DataSource, Map)} method to init the global connection pool
 * on system start only one time.</p>
 *
 * @since 1.0
 */
public class ConnectionPool {

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
     *
     * @param masterPool   master datasource
     * @param slavePoolMap map of slave datasource, key is slave pool name, value is datasource object
     * @since 1.0
     */
    public static synchronized void init(DataSource masterPool, Map<String, DataSource> slavePoolMap) {
        if (MASTER_POOL == null || DEFAULT_SLAVE_POOL == null) {
            if (masterPool == null) {
                throw new RuntimeException("master pool must not null");
            }
            MASTER_POOL = masterPool;
            if (slavePoolMap != null && !slavePoolMap.isEmpty()) {
                DEFAULT_SLAVE_POOL = slavePoolMap.values().iterator().next();
                POOL_MAP.putAll(slavePoolMap);
            } else {
                DEFAULT_SLAVE_POOL = masterPool;
            }
        }
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
     * <p>Get {@link Connection} object by the given slave pool name.</p>
     * <p>When the slave pool name is null, the {@link Connection} object is from master pool.</p>
     * <p>When the slave pool name is not null, but not found in {@link #POOL_MAP}, the {@link #DEFAULT_SLAVE_POOL} will be used.</p>
     *
     * @param slavePoolName slave pool name
     * @return an object of {@link Connection}
     * @throws SQLException exception when get connection failed
     * @since 1.0
     */
    public static Connection getConnection(String slavePoolName) throws SQLException {
        Connection connection = null;
        if (slavePoolName == null) {
            connection = MASTER_POOL.getConnection();
            connection.setAutoCommit(false);
            connection.setReadOnly(false);
        } else {
            connection = getSlaveDataSource(slavePoolName).getConnection();
            connection.setReadOnly(true);
        }
        return connection;
    }

    /**
     * Roll back the write connection when exception occurred and close both connection without commit.
     *
     * @param connection the connection to roll back
     * @throws SQLException exception when roll back failed
     * @since 1.0
     */
    public static void rollback(Connection connection) throws SQLException {
        if (connection != null && !connection.isReadOnly()) {
            try {
                connection.rollback();
            } finally {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * Close the write connection with commit and close the read connection without commit.
     *
     * @param connection the {@link Connection} object to close
     * @throws SQLException exception when close failed
     * @since 2.2
     */
    public static void close(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                if (!connection.isReadOnly()) {
                    connection.commit();
                }
            } finally {
                connection.close();
                connection = null;
            }
        }
    }

    /**
     * <p>Close the {@link ResultSet} object.</p>
     * <p>When {@code com.github.fastjdbc.BaseDao#executeSelectReturnResultSet(Connection, String, List)} called,
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
