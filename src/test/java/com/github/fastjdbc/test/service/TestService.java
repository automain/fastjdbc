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

package com.github.fastjdbc.test.service;

import com.github.fastjdbc.PageBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.common.ServiceDaoContainer;
import com.github.fastjdbc.test.vo.TestVO;

import java.sql.Connection;

public class TestService implements ServiceDaoContainer {

    public PageBean<Test> selectTableForCustomPage(Connection connection, TestVO bean) throws Exception {
        return TEST_DAO.selectTableForCustomPage(connection, bean, bean.getPage(), bean.getSize());
    }

}