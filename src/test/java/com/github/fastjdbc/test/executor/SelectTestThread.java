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
import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TestService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TestService service) throws Exception {
        selectById(connection, service);
        selectByIdList(connection, service);
        selectOneByBean(connection, service);
        selectByBean(connection, service);
        selectAll(connection, service);
        selectForPage(connection, service);
        selectForCustomerPage(connection, service);
        countByBean(connection, service);
    }

    private void selectById(ConnectionBean connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, 1);
        if (test != null) {
            System.out.println("=====Select by id testName is " + test.getTestName());
        }
    }

    private void selectByIdList(ConnectionBean connection, TestService service) throws Exception {
        List<Test> userList = service.selectTableByIdList(connection, List.of(2, 3));
        if (!userList.isEmpty()) {
            for (Test test : userList) {
                System.out.println("=====Select by id List testName is " + test.getTestName());
            }
        }
    }

    private void selectOneByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        Test test = service.selectOneTableByBean(connection, bean);
        if (test != null) {
            System.out.println("=====Select one by bean testName is " + test.getTestName());
        }
    }

    private void selectByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        List<Test> testList = service.selectTableByBean(connection, bean);
        if (!testList.isEmpty()) {
            for (Test test : testList) {
                System.out.println("=====Select by bean testName is " + test.getTestName());
            }
        }
    }

    private void selectAll(ConnectionBean connection, TestService service) throws Exception {
        List<Test> testList = service.selectAllTable(connection);
        if (!testList.isEmpty()) {
            for (Test test : testList) {
                System.out.println("=====Select all testName is " + test.getTestName());
            }
        }
    }

    private void selectForPage(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        PageBean<Test> pageBean = service.selectTableForPage(connection, bean, 1, 10);
        System.out.println("=====Select for page total is " + pageBean.getTotal());
        System.out.println("=====Select for page page is " + pageBean.getPage());
        List<Test> testList = pageBean.getData();
        if (testList != null && !testList.isEmpty()) {
            for (Test test : testList) {
                System.out.println("=====Select for page testName is " + test.getTestName());
            }
        }
    }

    private void selectForCustomerPage(ConnectionBean connection, TestService service) throws Exception {
        int dayStart = (int) LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().getEpochSecond();
        int dayEnd = dayStart + 86400;
        Test bean = new Test();
        bean.setCreateTime(dayStart);
        bean.setCreateTimeEnd(dayEnd);
        PageBean<Test> pageBean = service.selectTableForCustomPage(connection, bean, 1, 10);
        System.out.println("=====Select for customer page total is " + pageBean.getTotal());
        System.out.println("=====Select for customer page page is " + pageBean.getPage());
        List<Test> testList = pageBean.getData();
        if (testList != null && !testList.isEmpty()) {
            for (Test test : testList) {
                System.out.println("=====Select for customer page testName is " + test.getTestName());
            }
        }
    }

    private void countByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        int count = service.countTableByBean(connection, bean);
        System.out.println("=====Count by bean count is " + count);
    }

}
