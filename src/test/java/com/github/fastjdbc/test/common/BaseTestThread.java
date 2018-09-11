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

package com.github.fastjdbc.test.common;

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.ConnectionPool;
import com.github.fastjdbc.test.bean.TbUser;
import com.github.fastjdbc.test.dao.TbUserDao;
import com.github.fastjdbc.test.service.TbUserService;
import com.zaxxer.hikari.HikariConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class BaseTestThread extends Thread {

    static {
        String path = BaseTestThread.class.getResource("/").getPath() + "db.properties";
        File file = new File(path);
        try (InputStream is = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(is);
            HikariConfig config = new HikariConfig(properties);
            ConnectionPool.init(config, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void test(ConnectionBean connection, TbUserService service) throws Exception {
    }

    @Override
    public void run() {
        ConnectionBean connection = null;
        try {
            connection = ConnectionPool.getConnectionBean(null);
            connection.setPrintSql(true);
            TbUserService service = new TbUserService(new TbUser(), new TbUserDao());
            test(connection, service);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ConnectionPool.closeConnectionBean(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
