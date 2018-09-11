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
import com.github.fastjdbc.test.common.BaseTestThread;
import com.github.fastjdbc.test.service.TbUserService;

import java.util.Arrays;

public class DeleteTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TbUserService service) throws Exception {
        softDeleteById(connection, service);
        softDeleteByIdList(connection, service);
        deleteById(connection, service);
        deleteByIdList(connection, service);
    }

    private void softDeleteById(ConnectionBean connection, TbUserService service) throws Exception {
        service.softDeleteTableById(connection, 9L);
    }

    private void softDeleteByIdList(ConnectionBean connection, TbUserService service) throws Exception {
        service.softDeleteTableByIdList(connection, Arrays.asList(7L, 8L));
    }

    private void deleteById(ConnectionBean connection, TbUserService service) throws Exception {
        service.deleteTableById(connection, 10L);
    }

    private void deleteByIdList(ConnectionBean connection, TbUserService service) throws Exception {
        service.deleteTableByIdList(connection, Arrays.asList(11L, 12L));
    }
}
