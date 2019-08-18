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

import java.util.List;

public class DeleteTestThread extends BaseTestThread {

    @Override
    protected void test(ConnectionBean connection, TestService service) throws Exception {
        service.softDeleteTableById(connection, new Test().setId(1));
        Test test2 = service.selectTableById(connection, new Test().setId(2));
        service.softDeleteTableByGid(connection, new Test().setGid(test2.getGid()));
        service.softDeleteTableByIdList(connection, List.of(3, 4));
        Test test5 = service.selectTableById(connection, new Test().setId(5));
        Test test6 = service.selectTableById(connection, new Test().setId(6));
        service.softDeleteTableByGidList(connection, List.of(test5.getGid(), test6.getGid()));
        service.deleteTableById(connection, new Test().setId(7));
        Test test8 = service.selectTableById(connection, new Test().setId(8));
        service.deleteTableByGid(connection, new Test().setGid(test8.getGid()));
        service.deleteTableByIdList(connection, List.of(9, 10));
        Test test11 = service.selectTableById(connection, new Test().setId(11));
        Test test12 = service.selectTableById(connection, new Test().setId(12));
        service.deleteTableByGidList(connection, List.of(test11.getGid(), test12.getGid()));
    }

}
