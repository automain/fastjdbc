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
import java.util.List;
import java.util.UUID;

public class UpdateTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TestService service) throws Exception {
        updateByIdForNotNullColumn(connection, service);
        updateByIdForAllColumn(connection, service);
        updateByParamOne(connection, service);
        updateByParamMulti(connection, service);
        updateByParamInsertWhenNotExist(connection, service);
        updateByIdList(connection, service);
    }

    private void updateByIdForNotNullColumn(ConnectionBean connection, TestService service) throws Exception {
        service.updateTable(connection, new Test().setId(1).setRemark("update test remark"), false);
    }

    private void updateByIdForAllColumn(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test()
                .setId(2)
                .setRemark("update test all remark")
                .setTestName("update test all testName")
                .setMoney(BigDecimal.ONE)
                .setIsValid(1)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000));
        service.updateTable(connection, bean, true);
    }

    private void updateByParamOne(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("update one by bean remark");
        service.updateTable(connection, param, bean, false, false, false);
    }

    private void updateByParamMulti(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("test remark");
        Test bean = new Test().setRemark("update all by bean remark");
        service.updateTable(connection, param, bean, false, true, false);
    }

    private void updateByParamInsertWhenNotExist(ConnectionBean connection, TestService service) throws Exception {
        Test param = new Test().setRemark("update test when not exists remark");
        Test bean = new Test()
                .setRemark("update test when not exists remark")
                .setTestName("update test when not exists  testName")
                .setMoney(BigDecimal.ZERO)
                .setIsValid(0)
                .setGid(UUID.randomUUID().toString())
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setCreateTime((int) (System.currentTimeMillis() / 1000));
        service.updateTable(connection, param, bean, true, false, false);
    }

    private void updateByIdList(ConnectionBean connection, TestService service) throws Exception {
        service.updateTableByIdList(connection, new Test().setRemark("update by id List remark"), List.of(4, 5, 6), false);
    }

}
