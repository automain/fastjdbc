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

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.common.BaseService;
import com.github.fastjdbc.test.bean.TbUser;
import com.github.fastjdbc.test.dao.TbUserDao;

import javax.servlet.http.HttpServletRequest;

public class TbUserService extends BaseService<TbUser, TbUserDao> {

    public TbUserService(TbUser bean, TbUserDao dao) {
        super(bean, dao);
    }

    public PageBean<TbUser> selectTableForCustomPage(ConnectionBean connection, TbUser bean, HttpServletRequest request) throws Exception {
        return getDao().selectTableForCustomPage(connection, bean, pageFromRequest(request), limitFromRequest(request));
    }

}