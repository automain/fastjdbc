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
import com.github.fastjdbc.test.bean.TbUser;
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TbUserService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SelectTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TbUserService service) throws Exception {
        selectById(connection, service);
        selectByIdList(connection, service);
        selectOneByBean(connection, service);
        selectByBean(connection, service);
        selectAll(connection, service);
        selectForPage(connection, service);
        selectForCustomerPage(connection, service);
        countByBean(connection, service);
    }

    private void selectById(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser user = service.selectTableById(connection, 1L);
        if (user != null) {
            System.out.println("=====Select by id username is " + user.getUserName());
        }
    }

    private void selectByIdList(ConnectionBean connection, TbUserService service) throws Exception {
        List<TbUser> userList = service.selectTableByIdList(connection, Arrays.asList(2L, 3L));
        if (!userList.isEmpty()) {
            for (TbUser user : userList) {
                System.out.println("=====Select by id List username is " + user.getUserName());
            }
        }
    }

    private void selectOneByBean(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setIsDelete(0);
        TbUser user = service.selectOneTableByBean(connection, bean);
        if (user != null) {
            System.out.println("=====Select one by bean username is " + user.getUserName());
        }
    }

    private void selectByBean(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setIsDelete(0);
        List<TbUser> userList = service.selectTableByBean(connection, bean);
        if (!userList.isEmpty()) {
            for (TbUser user : userList) {
                System.out.println("=====Select by bean username is " + user.getUserName());
            }
        }
    }

    private void selectAll(ConnectionBean connection, TbUserService service) throws Exception {
        List<TbUser> userList = service.selectAllTable(connection);
        if (!userList.isEmpty()) {
            for (TbUser user : userList) {
                System.out.println("=====Select all username is " + user.getUserName());
            }
        }
    }

    private void selectForPage(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setIsDelete(0);
        PageBean<TbUser> pageBean = service.selectTableForPage(connection, bean, null);
        System.out.println("=====Select for page count is " + pageBean.getCount());
        System.out.println("=====Select for page curr is " + pageBean.getCurr());
        List<TbUser> userList = pageBean.getData();
        if (userList != null && !userList.isEmpty()) {
            for (TbUser user : userList) {
                System.out.println("=====Select for page username is " + user.getUserName());
            }
        }
    }

    private void selectForCustomerPage(ConnectionBean connection, TbUserService service) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        String dayStart = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String dayEnd = sdf.format(c.getTime());
        TbUser bean = new TbUser();
        bean.setCreateTimeRange(dayStart + " - " + dayEnd);
        PageBean<TbUser> pageBean = service.selectTableForCustomPage(connection, bean, null);
        System.out.println("=====Select for customer page count is " + pageBean.getCount());
        System.out.println("=====Select for customer page curr is " + pageBean.getCurr());
        List<TbUser> userList = pageBean.getData();
        if (userList != null && !userList.isEmpty()) {
            for (TbUser user : userList) {
                System.out.println("=====Select for customer page username is " + user.getUserName());
            }
        }
    }

    private void countByBean(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser bean = new TbUser();
        bean.setIsDelete(0);
        int count = service.countTableByBean(connection, bean);
        System.out.println("=====Count by bean count is " + count);
    }

}
