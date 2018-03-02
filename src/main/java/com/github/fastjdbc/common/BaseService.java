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

package com.github.fastjdbc.common;

import com.github.fastjdbc.bean.ConnectionBean;
import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public class BaseService<T extends BaseBean, D extends BaseDao<T>> extends RequestUtil {

    private D dao;

    public BaseService(D dao) {
        this.dao = dao;
    }

    public int insertIntoTable(ConnectionBean connection, T bean) throws SQLException {
        return dao.insertIntoTable(connection, bean);
    }

    public Long insertIntoTableReturnId(ConnectionBean connection, T bean) throws SQLException {
        return dao.insertIntoTableReturnId(connection, bean);
    }

    public int updateTable(ConnectionBean connection, T bean) throws SQLException {
        return dao.updateTable(connection, bean);
    }

    public int updateTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        return dao.updateTableByIdList(connection, bean, idList);
    }

    public int updateTable(ConnectionBean connection, T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException {
        return dao.updateTable(connection, paramBean, newBean, insertWhenNotExist, updateMulti);
    }

    public int softDeleteTableById(ConnectionBean connection, T bean, Long id) throws SQLException {
        return dao.softDeleteTableById(connection, bean, id);
    }

    public int softDeleteTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        return dao.softDeleteTableByIdList(connection, bean, idList);
    }

    public T selectTableById(ConnectionBean connection, T bean, Long id) throws SQLException {
        return dao.selectTableById(connection, bean, id);
    }

    public List<T> selectTableByIdList(ConnectionBean connection, T bean, List<Long> idList) throws SQLException {
        return dao.selectTableByIdList(connection, bean, idList);
    }

    public T selectOneTableByBean(ConnectionBean connection, T bean) throws SQLException {
        return dao.selectOneTableByBean(connection, bean);
    }

    public List<T> selectTableByBean(ConnectionBean connection, T bean) throws SQLException {
        return dao.selectTableByBean(connection, bean);
    }

    public List<T> selectAllTable(ConnectionBean connection, T bean) throws SQLException {
        return dao.selectAllTable(connection, bean);
    }

    public PageBean<T> selectTableForPage(ConnectionBean connection, T bean, HttpServletRequest request) throws Exception {
        int page = getInt("page", request, 1);
        int limit = getInt("limit", request, 1);
        return dao.selectTableForPage(connection, bean, page, limit);
    }

}
