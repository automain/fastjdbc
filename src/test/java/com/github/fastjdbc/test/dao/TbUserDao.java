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

package com.github.fastjdbc.test.dao;

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.bean.PageParameterBean;
import com.github.fastjdbc.common.BaseDao;
import com.github.fastjdbc.test.bean.TbUser;

import java.util.ArrayList;
import java.util.List;

public class TbUserDao extends BaseDao<TbUser> {

    @SuppressWarnings("unchecked")
    public PageBean<TbUser> selectTableForCustomPage(ConnectionBean connection, TbUser bean, int page, int limit) throws Exception {
        List<Object> countParameterList = new ArrayList<Object>();
        List<Object> parameterList = new ArrayList<Object>();
        String countSql = setSearchCondition(bean, countParameterList, true);
        String sql = setSearchCondition(bean, parameterList, false);
        PageParameterBean pageParameterBean = new PageParameterBean();
        pageParameterBean.setConnection(connection);
        pageParameterBean.setBean(bean);
        pageParameterBean.setCountSql(countSql);
        pageParameterBean.setCountParameterList(countParameterList);
        pageParameterBean.setSql(sql);
        pageParameterBean.setParameterList(parameterList);
        pageParameterBean.setPage(page);
        pageParameterBean.setLimit(limit);
        return selectTableForPage(pageParameterBean);
    }

    private String setSearchCondition(TbUser bean, List<Object> parameterList, boolean isCountSql) {
        StringBuilder sql = new StringBuilder("SELECT ");
        if (isCountSql) {
            sql.append("COUNT(1)");
        } else {
            sql.append("*");
        }
        sql.append(" FROM tb_user WHERE is_delete = 0 ");
        if (bean.getCellphone() != null) {
            sql.append(" AND cellphone = ?");
            parameterList.add(bean.getCellphone());
        }
        if (bean.getUserName() != null) {
            sql.append(" AND user_name LIKE ?");
            parameterList.add(bean.getUserName() + "%");
        }
        if (bean.getCreateTimeRange() != null) {
            sql.append(" AND create_time >= ? AND create_time <= ?");
            setTimeRange(bean.getCreateTimeRange(), parameterList);
        }
        if (bean.getEmail() != null) {
            sql.append(" AND email = ?");
            parameterList.add(bean.getEmail());
        }
        return sql.toString();
    }
}