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

import java.util.ArrayList;
import java.util.List;

public class InsertTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TbUserService service) throws Exception {
        insertOne(connection, service);
        insertOneReturnId(connection, service);
        batchInsertTable(connection, service);
    }

    private void insertOne(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser user = new TbUser();
        initUser(user);
        user.setUserName("userInsertOne");
        service.insertIntoTable(connection, user);
    }

    private void insertOneReturnId(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser user = new TbUser();
        initUser(user);
        user.setUserName("userReturnId");
        Long id = service.insertIntoTableReturnId(connection, user);
        System.out.println("=====Insert one table return id is " + id + "=====");
    }

    private void batchInsertTable(ConnectionBean connection, TbUserService service) throws Exception {
        TbUser user = null;
        List<TbUser> list = new ArrayList<TbUser>(10);
        for (int i = 0; i < 10; i++) {
            user = new TbUser();
            initUser(user);
            user.setUserName("user" + i);
            user.setCreateTime(user.getCreateTime() + (i * 2000));
            list.add(user);
        }
        service.batchInsertIntoTable(connection, list);
    }

    private void initUser(TbUser user) {
        user.setCellphone("11111111111");
        user.setCreateTime((int) (System.currentTimeMillis() / 1000));
        user.setEmail("email@email.com");
        user.setPasswordMd5("e10adc3949ba59abbe56e057f20f883e");
        user.setIsDelete(0);
    }
}
