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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectTestThread.class);

    @Override
    protected void test() throws Exception {
        selectById();
        selectByGid();
        selectByIdList();
        selectByGidList();
        selectOneByBean();
        selectByBean();
        selectAll();
        selectForPage();
        selectForCustomerPage();
        countByBean();
        executeSelectReturnString();
        executeSelectReturnInteger();
        executeSelectReturnLong();
        executeSelectReturnBigDecimal();
        executeSelectReturnStringList();
        executeSelectReturnIntegerList();
        executeSelectReturnLongList();
        executeSelectReturnBigDecimalList();
    }

    private void selectById() throws Exception {
        Test test = TestDao.selectTableById(new Test().setId(1));
        LOGGER.info("Select by id test = {}", test);
    }

    private void selectByGid() throws Exception {
        Test test = TestDao.selectTableById(new Test().setId(2));
        Test testByGid = TestDao.selectTableByGid(new Test().setGid(test.getGid()));
        LOGGER.info("Select by gid test = {}", testByGid);
    }

    private void selectByIdList() throws Exception {
        List<Test> testList = TestDao.selectTableByIdList(List.of(3, 4));
        LOGGER.info("Select by id List test list = {}", testList);
    }

    private void selectByGidList() throws Exception {
        List<Test> testList = TestDao.selectTableByIdList(List.of(5, 6));
        List<String> gidList = new ArrayList<String>(2);
        for (Test test : testList) {
            gidList.add(test.getGid());
        }
        List<Test> testByGidList = TestDao.selectTableByGidList(gidList);
        LOGGER.info("Select by gid List test list = {}", testByGidList);
    }

    private void selectOneByBean() throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        Test test = TestDao.selectOneTableByBean(bean);
        LOGGER.info("Select one by bean test = {}", test);
    }

    private void selectByBean() throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        List<Test> testList = TestDao.selectTableByBean(bean);
        LOGGER.info("Select by bean test list = {}", testList);
    }

    private void selectAll() throws Exception {
        List<Test> testList = TestDao.selectAllTable();
        LOGGER.info("Select all test list = {}", testList);
    }

    private void selectForPage() throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        PageBean<Test> pageBean = TestDao.selectTableForPage(bean, 1, 10);
        LOGGER.info("Select for page pageBean = {}", pageBean);
    }

    private void selectForCustomerPage() throws Exception {
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
        PageBean<Test> pageBean = TestDao.selectTableForCustomPage(bean);
        LOGGER.info("Select for customer page pageBean = {}", pageBean);
    }

    private void countByBean() throws Exception {
        Test bean = new Test();
        bean.setIsValid(1);
        int count = TestDao.countTableByBean(bean);
        LOGGER.info("Count by bean count = {}", count);
    }

    private void executeSelectReturnString() throws Exception {
        String testName = TestDao.selectTestNameById(1);
        LOGGER.info("execute select return String = {}", testName);
    }

    private void executeSelectReturnInteger() throws Exception {
        Integer createTime = TestDao.selectCreateTimeById(1);
        LOGGER.info("execute select return Integer = {}", createTime);
    }

    private void executeSelectReturnLong() throws Exception {
        Long updateTime = TestDao.selectUpdateTimeById(1);
        LOGGER.info("execute select return Long = {}", updateTime);
    }

    private void executeSelectReturnBigDecimal() throws Exception {
        BigDecimal money = TestDao.selectMoneyById(1);
        LOGGER.info("execute select return BigDecimal = {}", money);
    }

    private void executeSelectReturnStringList() throws Exception {
        List<String> testNameList = TestDao.selectTestNameByIdList(List.of(1, 2, 3, 4, 5));
        LOGGER.info("execute select return String list = {}", testNameList);
    }

    private void executeSelectReturnIntegerList() throws Exception {
        List<Integer> createTimeList = TestDao.selectCreateTimeByIdList(List.of(1, 2, 3, 4, 5));
        LOGGER.info("execute select return Integer list = {}", createTimeList);
    }

    private void executeSelectReturnLongList() throws Exception {
        List<Long> updateTimeList = TestDao.selectUpdateTimeByIdList(List.of(1, 2, 3, 4, 5));
        LOGGER.info("execute select return Long list = {}", updateTimeList);
    }

    private void executeSelectReturnBigDecimalList() throws Exception {
        List<BigDecimal> moneyList = TestDao.selectMoneyByIdList(List.of(1, 2, 3, 4, 5));
        LOGGER.info("execute select return BigDecimal list = {}", moneyList);
    }
}
