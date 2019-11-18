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
import com.github.fastjdbc.test.dao.TestDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdateTestThread extends BaseTestThread {

    @Override
    protected void test(Connection connection) throws Exception {
        updateByIdForNotNullColumn(connection);
        updateByGidForNotNullColumn(connection);
        updateByIdForAllColumn(connection);
        updateByGidForAllColumn(connection);
        updateByParamOne(connection);
        updateByParamMulti(connection);
        updateByParamInsertWhenNotExist(connection);
        updateByIdList(connection);
        updateByGidList(connection);
    }

    private void updateByIdForNotNullColumn(Connection connection) throws Exception {
        TestDao.updateTableById(connection, new Test().setId(1).setRemark("updateByIdForNotNullColumn remark"), false);
    }

    private void updateByGidForNotNullColumn(Connection connection) throws Exception {
        Test test = TestDao.selectTableById(connection, new Test().setId(2));
        TestDao.updateTableByGid(connection, new Test().setGid(test.getGid()).setRemark("updateByGidForNotNullColumn remark"), false);
    }

    private void updateByIdForAllColumn(Connection connection) throws Exception {
        Test bean = new Test()
                .setId(3)
                .setRemark("updateByIdForAllColumn remark")
                .setTestName("updateByIdForAllColumn testName")
                .setMoney(BigDecimal.ONE)
                .setIsValid(1)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000))
                .setTestDictionary(1);
        TestDao.updateTableById(connection, bean, true);
    }

    private void updateByGidForAllColumn(Connection connection) throws Exception {
        Test test = TestDao.selectTableById(connection, new Test().setId(4));
        Test bean = new Test()
                .setId(4)
                .setRemark("updateByGidForAllColumn remark")
                .setTestName("updateByGidForAllColumn testName")
                .setMoney(BigDecimal.ONE)
                .setIsValid(1)
                .setGid(test.getGid())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000))
                .setTestDictionary(1);
        TestDao.updateTableByGid(connection, bean, true);
    }

    private void updateByParamOne(Connection connection) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("updateByParamOne remark");
        TestDao.updateTable(connection, param, bean, false, false, false);
    }

    private void updateByParamMulti(Connection connection) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("updateByParamMulti remark");
        TestDao.updateTable(connection, param, bean, false, true, false);
    }

    private void updateByParamInsertWhenNotExist(Connection connection) throws Exception {
        Test param = new Test().setRemark("updateByParamInsertWhenNotExist remark");
        Test bean = new Test()
                .setRemark("updateByParamInsertWhenNotExist remark")
                .setTestName("updateByParamInsertWhenNotExist testName")
                .setMoney(BigDecimal.ZERO)
                .setIsValid(0)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000))
                .setTestDictionary(0);
        TestDao.updateTable(connection, param, bean, true, false, false);
    }

    private void updateByIdList(Connection connection) throws Exception {
        TestDao.updateTableByIdList(connection, new Test().setRemark("updateByIdList remark").setTestDictionary(0), List.of(5, 6, 7), false);
    }

    private void updateByGidList(Connection connection) throws Exception {
        List<Test> tests = TestDao.selectTableByIdList(connection, List.of(8, 9, 10));
        List<String> gidList = new ArrayList<String>(3);
        for (Test test : tests) {
            gidList.add(test.getGid());
        }
        TestDao.updateTableByGidList(connection, new Test().setRemark("updateByGidList remark").setTestDictionary(1), gidList, false);
    }

}
