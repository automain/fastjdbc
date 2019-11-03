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

import com.github.fastjdbc.PageBean;
import com.github.fastjdbc.PageParamBean;
import com.github.fastjdbc.BaseDao;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.vo.TestVO;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDao extends BaseDao<Test> {

    private static final Test DEFAULT_BEAN = new Test();

    public int insertIntoTable(Connection connection, Test bean) throws SQLException {
        return super.insertIntoTable(connection, bean);
    }

    public Integer insertIntoTableReturnId(Connection connection, Test bean) throws SQLException {
        return super.insertIntoTableReturnId(connection, bean);
    }

    public int batchInsertIntoTable(Connection connection, List<Test> list) throws SQLException {
        return super.batchInsertIntoTable(connection, list);
    }

    public int updateTableById(Connection connection, Test bean, boolean all) throws SQLException {
        return super.updateTableById(connection, bean, all);
    }

    public int updateTableByGid(Connection connection, Test bean, boolean all) throws SQLException {
        return super.updateTableByGid(connection, bean, all);
    }

    public int updateTableByIdList(Connection connection, Test bean, List<Integer> idList, boolean all) throws SQLException {
        return super.updateTableByIdList(connection, bean, idList, all);
    }

    public int updateTableByGidList(Connection connection, Test bean, List<String> gidList, boolean all) throws SQLException {
        return super.updateTableByGidList(connection, bean, gidList, all);
    }

    public int updateTable(Connection connection, Test paramBean, Test newBean, boolean insertWhenNotExist, boolean updateMulti, boolean all) throws SQLException {
        return super.updateTable(connection, paramBean, newBean, insertWhenNotExist, updateMulti, all);
    }

    public int softDeleteTableById(Connection connection, Test bean) throws SQLException {
        return super.softDeleteTableById(connection, bean);
    }

    public int softDeleteTableByGid(Connection connection, Test bean) throws SQLException {
        return super.softDeleteTableByGid(connection, bean);
    }

    public int softDeleteTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return super.softDeleteTableByIdList(connection, DEFAULT_BEAN, idList);
    }

    public int softDeleteTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return super.softDeleteTableByGidList(connection, DEFAULT_BEAN, gidList);
    }

    public int deleteTableById(Connection connection, Test bean) throws SQLException {
        return super.deleteTableById(connection, bean);
    }

    public int deleteTableByGid(Connection connection, Test bean) throws SQLException {
        return super.deleteTableByGid(connection, bean);
    }

    public int deleteTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return super.deleteTableByIdList(connection, DEFAULT_BEAN, idList);
    }

    public int deleteTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return super.deleteTableByGidList(connection, DEFAULT_BEAN, gidList);
    }

    public int countTableByBean(Connection connection, Test bean) throws SQLException {
        return super.countTableByBean(connection, bean);
    }

    public Test selectTableById(Connection connection, Test bean) throws SQLException {
        return super.selectTableById(connection, bean);
    }

    public Test selectTableByGid(Connection connection, Test bean) throws SQLException {
        return super.selectTableByGid(connection, bean);
    }

    public List<Test> selectTableByIdList(Connection connection, List<Integer> idList) throws SQLException {
        return super.selectTableByIdList(connection, DEFAULT_BEAN, idList);
    }

    public List<Test> selectTableByGidList(Connection connection, List<String> gidList) throws SQLException {
        return super.selectTableByGidList(connection, DEFAULT_BEAN, gidList);
    }

    public Test selectOneTableByBean(Connection connection, Test bean) throws SQLException {
        return super.selectOneTableByBean(connection, bean);
    }

    public List<Test> selectTableByBean(Connection connection, Test bean) throws SQLException {
        return super.selectTableByBean(connection, bean);
    }

    public List<Test> selectAllTable(Connection connection) throws SQLException {
        return super.selectAllTable(connection, DEFAULT_BEAN);
    }

    public PageBean<Test> selectTableForPage(Connection connection, Test bean, int page, int size) throws Exception {
        return super.selectTableForPage(connection, bean, page, size);
    }

    @SuppressWarnings("unchecked")
    public PageBean<Test> selectTableForCustomPage(Connection connection, TestVO bean, int page, int size) throws Exception {
        List<Object> countParamList = new ArrayList<Object>();
        List<Object> paramList = new ArrayList<Object>();
        String countSql = setSearchCondition(bean, countParamList, true);
        String sql = setSearchCondition(bean, paramList, false);
        PageParamBean pageParamBean = new PageParamBean()
                .setConnection(connection)
                .setBean(bean)
                .setCountSql(countSql)
                .setCountParamList(countParamList)
                .setSql(sql)
                .setParamList(paramList)
                .setPage(page)
                .setSize(size);
        return selectTableForPage(pageParamBean);
    }

    private String setSearchCondition(TestVO bean, List<Object> paramList, boolean isCountSql) {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(isCountSql ? "COUNT(1)" : "*").append(" FROM test WHERE is_valid = 1");
        if (bean.getGid() != null) {
            sql.append(" AND gid = ?");
            paramList.add(bean.getGid());
        }
        if (bean.getCreateTime() != null) {
            sql.append(" AND create_time >= ?");
            paramList.add(bean.getCreateTime());
        }
        if (bean.getCreateTimeEnd() != null) {
            sql.append(" AND create_time < ?");
            paramList.add(bean.getCreateTimeEnd());
        }
        if (bean.getTestName() != null) {
            sql.append(" AND test_name LIKE ?");
            paramList.add(bean.getTestName() + "%");
        }
        if (CollectionUtils.isNotEmpty(bean.getTestDictionaryList())) {
            sql.append(" AND test_dictionary").append(makeInStr(bean.getTestDictionaryList()));
            paramList.addAll(bean.getTestDictionaryList());
        }
        if (!isCountSql && bean.getSortLabel() != null && bean.getSortOrder() != null && bean.columnMap(true).containsKey(bean.getSortLabel())) {
            sql.append(" ORDER BY ").append(bean.getSortLabel()).append("asc".equalsIgnoreCase(bean.getSortOrder()) ? " ASC" : " DESC");
        }
        return sql.toString();
    }
}