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

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TestService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdateTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TestService service) throws Exception {
        updateByIdForNotNullColumn(connection, service);
        updateByGidForNotNullColumn(connection, service);
        updateByIdForAllColumn(connection, service);
        updateByGidForAllColumn(connection, service);
        updateByParamOne(connection, service);
        updateByParamMulti(connection, service);
        updateByParamInsertWhenNotExist(connection, service);
        updateByIdList(connection, service);
        updateByGidList(connection, service);
    }

    private void updateByIdForNotNullColumn(ConnectionBean connection, TestService service) throws Exception {
        service.updateTableById(connection, new Test().setId(1).setRemark("updateByIdForNotNullColumn remark"), false);
    }

    private void updateByGidForNotNullColumn(ConnectionBean connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(2));
        service.updateTableByGid(connection, new Test().setGid(test.getGid()).setRemark("updateByGidForNotNullColumn remark"), false);
    }

    private void updateByIdForAllColumn(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test()
                .setId(3)
                .setRemark("updateByIdForAllColumn remark")
                .setTestName("updateByIdForAllColumn testName")
                .setMoney(BigDecimal.ONE)
                .setIsValid(1)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000));
        service.updateTableById(connection, bean, true);
    }

    private void updateByGidForAllColumn(ConnectionBean connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(4));
        Test bean = new Test()
                .setId(4)
                .setRemark("updateByGidForAllColumn remark")
                .setTestName("updateByGidForAllColumn testName")
                .setMoney(BigDecimal.ONE)
                .setIsValid(1)
                .setGid(test.getGid())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000));
        service.updateTableByGid(connection, bean, true);
    }

    private void updateByParamOne(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("updateByParamOne remark");
        service.updateTable(connection, param, bean, false, false, false);
    }

    private void updateByParamMulti(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("updateByParamMulti remark");
        service.updateTable(connection, param, bean, false, true, false);
    }

    private void updateByParamInsertWhenNotExist(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("updateByParamInsertWhenNotExist remark");
        Test bean = new Test()
                .setRemark("updateByParamInsertWhenNotExist remark")
                .setTestName("updateByParamInsertWhenNotExist testName")
                .setMoney(BigDecimal.ZERO)
                .setIsValid(0)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000));
        service.updateTable(connection, param, bean, true, false, false);
    }

    private void updateByIdList(ConnectionBean connection, TestService service) throws Exception {
        service.updateTableByIdList(connection, new Test().setRemark("updateByIdList remark"), List.of(5, 6, 7), false);
    }

    private void updateByGidList(ConnectionBean connection, TestService service) throws Exception {
        List<Test> tests = service.selectTableByIdList(connection, List.of(8, 9, 10));
        List<String> gidList = new ArrayList<String>(3);
        for (Test test : tests) {
            gidList.add(test.getGid());
        }
        service.updateTableByGidList(connection, new Test().setRemark("updateByGidList remark"), gidList, false);
    }

}
