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

import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.BaseTestThread;

import java.sql.Connection;
import java.util.List;

public class DeleteTestThread extends BaseTestThread {

    @Override
    protected void test(Connection connection) throws Exception {
        TEST_DAO.softDeleteTableById(connection, new Test().setId(1));
        Test test2 = TEST_DAO.selectTableById(connection, new Test().setId(2));
        TEST_DAO.softDeleteTableByGid(connection, new Test().setGid(test2.getGid()));
        TEST_DAO.softDeleteTableByIdList(connection, List.of(3, 4));
        Test test5 = TEST_DAO.selectTableById(connection, new Test().setId(5));
        Test test6 = TEST_DAO.selectTableById(connection, new Test().setId(6));
        TEST_DAO.softDeleteTableByGidList(connection, List.of(test5.getGid(), test6.getGid()));
        TEST_DAO.deleteTableById(connection, new Test().setId(7));
        Test test8 = TEST_DAO.selectTableById(connection, new Test().setId(8));
        TEST_DAO.deleteTableByGid(connection, new Test().setGid(test8.getGid()));
        TEST_DAO.deleteTableByIdList(connection, List.of(9, 10));
        Test test11 = TEST_DAO.selectTableById(connection, new Test().setId(11));
        Test test12 = TEST_DAO.selectTableById(connection, new Test().setId(12));
        TEST_DAO.deleteTableByGidList(connection, List.of(test11.getGid(), test12.getGid()));
    }

}
