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
import com.github.fastjdbc.test.bean.TbUser;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TbUserService;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class UpdateTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TbUserService service) throws Exception {
        updateByIdForNotNullColumn(connection, service);
        updateByIdForAllColumn(connection, service);
        updateByParamOne(connection, service);
        updateByParamMulti(connection, service);
        updateByParamInsertWhenNotExist(connection, service);
        updateByIdList(connection, service);
    }

    private void updateByIdForNotNullColumn(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setUserId(1L);
        bean.setEmail("updateNotNull@email.com");
        service.updateTable(connection, bean, false);
    }

    private void updateByIdForAllColumn(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setUserId(2L);
        bean.setUserName("updateAllColumn");
        bean.setCellphone("11111111111");
        bean.setCreateTime(new Timestamp(System.currentTimeMillis()));
        bean.setPasswordMd5("e10adc3949ba59abbe56e057f20f883e");
        bean.setIsDelete(1);
        service.updateTable(connection, bean, true);
    }

    private void updateByParamOne(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser param = new TbUser();
        param.setEmail("email@email.com");
        TbUser bean = new TbUser();
        bean.setEmail("updateByParamOne@email.com");
        service.updateTable(connection, param, bean, false, false, false);
    }

    private void updateByParamMulti(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser param = new TbUser();
        param.setIsDelete(0);
        TbUser bean = new TbUser();
        bean.setCellphone("22222222222");
        service.updateTable(connection, param, bean, false, true, false);
    }

    private void updateByParamInsertWhenNotExist(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser param = new TbUser();
        param.setIsDelete(0);
        TbUser bean = new TbUser();
        bean.setUserName("UpdateNE");
        bean.setCellphone("11111111111");
        bean.setCreateTime(new Timestamp(System.currentTimeMillis()));
        bean.setEmail("email@email.com");
        bean.setPasswordMd5("e10adc3949ba59abbe56e057f20f883e");
        bean.setIsDelete(0);
        service.updateTable(connection, param, bean, true, false, false);
    }

    private void updateByIdList(ConnectionBean connection, TbUserService service) throws Exception {
        List<Long> idList = Arrays.asList(4L, 5L, 6L);
        TbUser bean = new TbUser();
        bean.setEmail("updateByIdList@email.com");
        service.updateTableByIdList(connection, bean, idList, false);
    }

}
