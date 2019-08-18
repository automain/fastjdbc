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
import com.github.fastjdbc.test.vo.TestVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TestService service) throws Exception {
        selectById(connection, service);
        selectByGid(connection, service);
        selectByIdList(connection, service);
        selectByGidList(connection, service);
        selectOneByBean(connection, service);
        selectByBean(connection, service);
        selectAll(connection, service);
        selectForPage(connection, service);
        selectForCustomerPage(connection, service);
        countByBean(connection, service);
    }

    private void selectById(ConnectionBean connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(1));
        System.out.println("=====Select by id test is " + test);
    }

    private void selectByGid(ConnectionBean connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(2));
        Test testByGid = service.selectTableByGid(connection, new Test().setGid(test.getGid()));
        System.out.println("=====Select by gid test is " + testByGid);
    }

    private void selectByIdList(ConnectionBean connection, TestService service) throws Exception {
        List<Test> testList = service.selectTableByIdList(connection, List.of(3, 4));
        System.out.println("=====Select by id List test list is " + testList);
    }

    private void selectByGidList(ConnectionBean connection, TestService service) throws Exception {
        List<Test> testList = service.selectTableByIdList(connection, List.of(5, 6));
        List<String> gidList = new ArrayList<String>(2);
        for (Test test : testList) {
            gidList.add(test.getGid());
        }
        List<Test> testByGidList = service.selectTableByGidList(connection, gidList);
        System.out.println("=====Select by gid List test list is " + testByGidList);
    }

    private void selectOneByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        Test test = service.selectOneTableByBean(connection, bean);
        System.out.println("=====Select one by bean test is " + test);
    }

    private void selectByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        List<Test> testList = service.selectTableByBean(connection, bean);
        System.out.println("=====Select by bean test list is " + testList);
    }

    private void selectAll(ConnectionBean connection, TestService service) throws Exception {
        List<Test> testList = service.selectAllTable(connection);
        System.out.println("=====Select all test list is " + testList);
    }

    private void selectForPage(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        PageBean<Test> pageBean = service.selectTableForPage(connection, bean, 1, 10);
        System.out.println("selectForPage pageBean is " + pageBean);
    }

    private void selectForCustomerPage(ConnectionBean connection, TestService service) throws Exception {
        int dayStart = (int) LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().getEpochSecond();
        int dayEnd = dayStart + 86400;
        TestVO bean = new TestVO();
        bean.setCreateTime(dayStart);
        bean.setCreateTimeEnd(dayEnd);
        bean.setPage(1);
        bean.setSize(10);
        PageBean<Test> pageBean = service.selectTableForCustomPage(connection, bean);
        System.out.println("selectForCustomerPage pageBean is " + pageBean);
    }

    private void countByBean(ConnectionBean connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        int count = service.countTableByBean(connection, bean);
        System.out.println("countByBean count is " + count);
    }

}
