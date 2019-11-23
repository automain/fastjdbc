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

import com.github.fastjdbc.PageBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.dao.TestDao;
import com.github.fastjdbc.test.vo.TestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectTestThread.class);

    @Override
    protected void test(Connection connection) throws Exception {
        selectById(connection);
        selectByGid(connection);
        selectByIdList(connection);
        selectByGidList(connection);
        selectOneByBean(connection);
        selectByBean(connection);
        selectAll(connection);
        selectForPage(connection);
        selectForCustomerPage(connection);
        countByBean(connection);
        executeSelectReturnString(connection);
        executeSelectReturnInteger(connection);
        executeSelectReturnLong(connection);
        executeSelectReturnBigDecimal(connection);
        executeSelectReturnStringList(connection);
        executeSelectReturnIntegerList(connection);
        executeSelectReturnLongList(connection);
        executeSelectReturnBigDecimalList(connection);
    }

    private void selectById(Connection connection) throws Exception {
        Test test = TestDao.selectTableById(connection, new Test().setId(1));
        LOGGER.info("Select by id test = {}", test);
    }

    private void selectByGid(Connection connection) throws Exception {
        Test test = TestDao.selectTableById(connection, new Test().setId(2));
        Test testByGid = TestDao.selectTableByGid(connection, new Test().setGid(test.getGid()));
        LOGGER.info("Select by gid test = {}", testByGid);
    }

    private void selectByIdList(Connection connection) throws Exception {
        List<Test> testList = TestDao.selectTableByIdList(connection, List.of(3, 4));
        LOGGER.info("Select by id List test list = {}", testList);
    }

    private void selectByGidList(Connection connection) throws Exception {
        List<Test> testList = TestDao.selectTableByIdList(connection, List.of(5, 6));
        List<String> gidList = new ArrayList<String>(2);
        for (Test test : testList) {
            gidList.add(test.getGid());
        }
        List<Test> testByGidList = TestDao.selectTableByGidList(connection, gidList);
        LOGGER.info("Select by gid List test list = {}", testByGidList);
    }

    private void selectOneByBean(Connection connection) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        Test test = TestDao.selectOneTableByBean(connection, bean);
        LOGGER.info("Select one by bean test = {}", test);
    }

    private void selectByBean(Connection connection) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        List<Test> testList = TestDao.selectTableByBean(connection, bean);
        LOGGER.info("Select by bean test list = {}", testList);
    }

    private void selectAll(Connection connection) throws Exception {
        List<Test> testList = TestDao.selectAllTable(connection);
        LOGGER.info("Select all test list = {}", testList);
    }

    private void selectForPage(Connection connection) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        PageBean<Test> pageBean = TestDao.selectTableForPage(connection, bean, 1, 10);
        LOGGER.info("Select for page pageBean = {}", pageBean);
    }

    private void selectForCustomerPage(Connection connection) throws Exception {
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
        PageBean<Test> pageBean = TestDao.selectTableForCustomPage(connection, bean);
        LOGGER.info("Select for customer page pageBean = {}", pageBean);
    }

    private void countByBean(Connection connection) throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        int count = TestDao.countTableByBean(connection, bean);
        LOGGER.info("Count by bean count = {}", count);
    }

    private void executeSelectReturnString(Connection connection) throws Exception {
        String testName = TestDao.selectTestNameById(connection, 1);
        LOGGER.info("execute select return String = {}", testName);
    }

    private void executeSelectReturnInteger(Connection connection) throws Exception {
        Integer createTime = TestDao.selectCreateTimeById(connection, 1);
        LOGGER.info("execute select return Integer = {}", createTime);
    }

    private void executeSelectReturnLong(Connection connection) throws Exception {
        Long updateTime = TestDao.selectUpdateTimeById(connection, 1);
        LOGGER.info("execute select return Long = {}", updateTime);
    }

    private void executeSelectReturnBigDecimal(Connection connection) throws Exception {
        BigDecimal money = TestDao.selectMoneyById(connection, 1);
        LOGGER.info("execute select return BigDecimal = {}", money);
    }

    private void executeSelectReturnStringList(Connection connection) throws Exception {
        List<String> testNameList = TestDao.selectTestNameByIdList(connection, List.of(1,2,3,4,5));
        LOGGER.info("execute select return String list = {}", testNameList);
    }

    private void executeSelectReturnIntegerList(Connection connection) throws Exception {
        List<Integer> createTimeList = TestDao.selectCreateTimeByIdList(connection, List.of(1,2,3,4,5));
        LOGGER.info("execute select return Integer list = {}", createTimeList);
    }

    private void executeSelectReturnLongList(Connection connection) throws Exception {
        List<Long> updateTimeList = TestDao.selectUpdateTimeByIdList(connection, List.of(1,2,3,4,5));
        LOGGER.info("execute select return Long list = {}", updateTimeList);
    }

    private void executeSelectReturnBigDecimalList(Connection connection) throws Exception {
        List<BigDecimal> moneyList = TestDao.selectMoneyByIdList(connection, List.of(1,2,3,4,5));
        LOGGER.info("execute select return BigDecimal list = {}", moneyList);
    }
}
