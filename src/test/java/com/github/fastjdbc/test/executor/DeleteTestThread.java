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
import com.github.fastjdbc.test.dao.TestDao;

import java.util.List;

public class DeleteTestThread extends BaseTestThread {

    @Override
    protected void test() throws Exception {
        TestDao.softDeleteTableById(new Test().setId(1));
        Test test2 = TestDao.selectTableById(new Test().setId(2));
        TestDao.softDeleteTableByGid(new Test().setGid(test2.getGid()));
        TestDao.softDeleteTableByIdList(List.of(3, 4));
        Test test5 = TestDao.selectTableById(new Test().setId(5));
        Test test6 = TestDao.selectTableById(new Test().setId(6));
        TestDao.softDeleteTableByGidList(List.of(test5.getGid(), test6.getGid()));
        TestDao.deleteTableById(new Test().setId(7));
        Test test8 = TestDao.selectTableById(new Test().setId(8));
        TestDao.deleteTableByGid(new Test().setGid(test8.getGid()));
        TestDao.deleteTableByIdList(List.of(9, 10));
        Test test11 = TestDao.selectTableById(new Test().setId(11));
        Test test12 = TestDao.selectTableById(new Test().setId(12));
        TestDao.deleteTableByGidList(List.of(test11.getGid(), test12.getGid()));
    }

}
