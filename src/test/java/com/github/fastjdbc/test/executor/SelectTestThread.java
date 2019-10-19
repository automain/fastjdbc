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

import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TestService;
import com.github.fastjdbc.test.vo.TestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectTestThread.class);

    @Override
    protected void test(Connection connection, TestService service) throws Exception {
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

    private void selectById(Connection connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(1));
        LOGGER.info("Select by id test = {}", test);
    }

    private void selectByGid(Connection connection, TestService service) throws Exception {
        Test test = service.selectTableById(connection, new Test().setId(2));
        Test testByGid = service.selectTableByGid(connection, new Test().setGid(test.getGid()));
        LOGGER.info("Select by gid test = {}", testByGid);
    }

    private void selectByIdList(Connection connection, TestService service) throws Exception {
        List<Test> testList = service.selectTableByIdList(connection, List.of(3, 4));
        LOGGER.info("Select by id List test list = {}", testList);
    }

    private void selectByGidList(Connection connection, TestService service) throws Exception {
        List<Test> testList = service.selectTableByIdList(connection, List.of(5, 6));
        List<String> gidList = new ArrayList<String>(2);
        for (Test test : testList) {
            gidList.add(test.getGid());
        }
        List<Test> testByGidList = service.selectTableByGidList(connection, gidList);
        LOGGER.info("Select by gid List test list = {}", testByGidList);
    }

    private void selectOneByBean(Connection connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        Test test = service.selectOneTableByBean(connection, bean);
        LOGGER.info("Select one by bean test = {}", test);
    }

    private void selectByBean(Connection connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        List<Test> testList = service.selectTableByBean(connection, bean);
        LOGGER.info("Select by bean test list = {}", testList);
    }

    private void selectAll(Connection connection, TestService service) throws Exception {
        List<Test> testList = service.selectAllTable(connection);
        LOGGER.info("Select all test list = {}", testList);
    }

    private void selectForPage(Connection connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        PageBean<Test> pageBean = service.selectTableForPage(connection, bean, 1, 10);
        LOGGER.info("Select for page pageBean = {}", pageBean);
    }

    private void selectForCustomerPage(Connection connection, TestService service) throws Exception {
        int dayStart = (int) LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().getEpochSecond();
        int dayEnd = dayStart + 86400;
        TestVO bean = new TestVO();
        bean.setCreateTime(dayStart);
        bean.setCreateTimeEnd(dayEnd);
        bean.setPage(1);
        bean.setSize(10);
        bean.setTestDictionaryList(List.of(0, 1));
        bean.setSortLabel("create_time");
        bean.setSortOrder("asc");
        PageBean<Test> pageBean = service.selectTableForCustomPage(connection, bean);
        LOGGER.info("Select for customer page pageBean = {}", pageBean);
    }

    private void countByBean(Connection connection, TestService service) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        int count = service.countTableByBean(connection, bean);
        LOGGER.info("Count by bean count = {}", count);
    }

}
