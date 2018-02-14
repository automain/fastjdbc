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

import java.sql.SQLException;
import java.util.List;

public interface CommonDao<T extends BaseBean> {

    int insertIntoTable(ConnectionBean connection, T bean) throws SQLException;

    Long insertIntoTableReturnId(ConnectionBean connection, T bean) throws SQLException;

    int updateTable(ConnectionBean connection, T bean) throws SQLException;

    int updateTable(ConnectionBean connection, T paramBean, T newBean, boolean insertWhenNotExist, boolean updateMulti) throws SQLException;

    int softDeleteTableById(ConnectionBean connection, Long id) throws SQLException;

    int softDeleteTableByIdList(ConnectionBean connection, List<Long> idList) throws SQLException;

    T selectTableById(ConnectionBean connection, Long id) throws SQLException;

    List<T> selectTableByIdList(ConnectionBean connection, List<Long> idList) throws SQLException;

    T selectOneTableByBean(ConnectionBean connection, T bean) throws SQLException;

    List<T> selectTableByBean(ConnectionBean connection, T bean) throws SQLException;

    List<T> selectAllTable(ConnectionBean connection) throws SQLException;
}
