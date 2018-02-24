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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>A container of {@link Connection},
 * the {@code writeConnection} property means a connection from master data connection pool,
 * the {@code readConnection} property means a connection from slave data connection pool.</p>
 * <p>You should set all properties for initialization, and for safety, there is no setter method for properties.</p>
 * <p>When you want to execute a query sql to query the data which you just update or insert in a same transaction,
 * you should call the {@link #closeReadUseWrite()} method before use the {@link ConnectionBean}</p>
 *
 * @since 1.0
 */
public class ConnectionBean {

    /**
     * The connection from master data connection pool.
     *
     * @since 1.0
     */
    private Connection writeConnection;

    /**
     * The connection from slave data connection pool.
     *
     * @since 1.0
     */
    private Connection readConnection;

    /**
     * Whether or not print sql in this transaction.
     *
     * @since 1.0
     */
    private boolean printSql;

    public Connection getWriteConnection() {
        return writeConnection;
    }

    public Connection getReadConnection() {
        return readConnection;
    }

    public boolean isPrintSql() {
        return printSql;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }

    /**
     * You should set all properties for initialization.
     *
     * @param writeConnection connection from master data connection pool
     * @param readConnection  connection from slave data connection pool
     */
    public ConnectionBean(Connection writeConnection, Connection readConnection) {
        this.writeConnection = writeConnection;
        this.readConnection = readConnection;
    }

    /**
     * Close the original read connection and then set the write connection to read connection
     *
     * @throws SQLException when close failed
     */
    public void closeReadUseWrite() throws SQLException {
        if (this.readConnection != null && this.readConnection != this.writeConnection) {
            this.readConnection.close();
            this.readConnection = this.writeConnection;
        }
    }
}
