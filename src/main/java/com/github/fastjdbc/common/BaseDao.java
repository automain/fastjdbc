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

import java.sql.SQLException;
import java.util.List;

/**
 * A common template of DAO layer, all the classes of DAO layer should extends this class.
 *
 * @see CommonDao
 * @since 1.0
 */
public class BaseDao implements CommonDao {

    @Override
    public int insertIntoTable(ConnectionBean connection, BaseBean bean) throws SQLException {
        return 0;
    }

    @Override
    public Long insertIntoTableReturnId(ConnectionBean connection, BaseBean bean) throws SQLException {
        return null;
    }

    @Override
    public int updateTable(ConnectionBean connection, BaseBean bean) throws SQLException {
        return 0;
    }

    @Override
    public int updateTable(ConnectionBean connection, BaseBean paramBean, BaseBean newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException {
        return 0;
    }

    @Override
    public int softDeleteTableById(ConnectionBean connection, Long id) throws SQLException {
        return 0;
    }

    @Override
    public BaseBean selectTableById(ConnectionBean connection, Long id) throws SQLException {
        return null;
    }

    @Override
    public BaseBean selectOneTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException {
        return null;
    }

    @Override
    public List selectTableByBean(ConnectionBean connection, BaseBean bean) throws SQLException {
        return null;
    }

    @Override
    public List selectAllTable(ConnectionBean connection) throws SQLException {
        return null;
    }

    @Override
    public PageBean selectTableForPage(ConnectionBean connection, BaseBean bean, int page, int limit) throws Exception {
        return null;
    }

    @Override
    public List selectTableByIdList(ConnectionBean connection, List idList) throws SQLException {
        return null;
    }

    @Override
    public int softDeleteTableByIdList(ConnectionBean connection, List idList) throws SQLException {
        return 0;
    }

    @Override
    public int updateTableByIdList(ConnectionBean connection, BaseBean bean, List idList) throws SQLException {
        return 0;
    }
}
