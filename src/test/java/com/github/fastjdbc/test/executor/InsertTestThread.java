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

package com.github.fastjdbc.test.executor;

import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InsertTestThread extends BaseTestThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertTestThread.class);

    @Override
    protected void test(Connection connection, TestService service) throws Exception {
        insertOne(connection, service);
        insertOneReturnId(connection, service);
        batchInsertTable(connection, service);
    }

    private void insertOne(Connection connection, TestService service) throws Exception {
        Test test = initTest().setTestName("insertOne testName").setTestDictionary(0);
        service.insertIntoTable(connection, test);
    }

    private void insertOneReturnId(Connection connection, TestService service) throws Exception {
        Test test = initTest().setTestName("insertOneReturnId testName").setTestDictionary(1);
        Integer id = service.insertIntoTableReturnId(connection, test);
        LOGGER.info("Insert one table return id = {}", id);
    }

    private void batchInsertTable(Connection connection, TestService service) throws Exception {
        Test test = null;
        List<Test> list = new ArrayList<Test>(10);
        for (int i = 0; i < 10; i++) {
            test = initTest().setTestName("batchInsertTable" + i).setTestDictionary(2).setCreateTime((int) (System.currentTimeMillis() / 1000) + (i * 2000));
            list.add(test);
        }
        service.batchInsertIntoTable(connection, list);
    }

    private Test initTest() {
        return new Test()
                .setCreateTime((int) (System.currentTimeMillis() / 1000))
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setGid(UUID.randomUUID().toString())
                .setIsValid(1)
                .setMoney(BigDecimal.TEN)
                .setRemark("test remark");
    }
}
