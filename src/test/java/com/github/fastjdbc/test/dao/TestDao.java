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

import com.github.fastjdbc.BaseDao;
import com.github.fastjdbc.PageBean;
import com.github.fastjdbc.PageParamBean;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.vo.TestVO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDao extends BaseDao {

    private static final Test DEFAULT_BEAN = new Test();

    public static int softDeleteTableByIdList(List<Integer> idList) throws SQLException {
        return softDeleteTableByIdList(DEFAULT_BEAN, idList);
    }

    public static int softDeleteTableByGidList(List<String> gidList) throws SQLException {
        return softDeleteTableByGidList(DEFAULT_BEAN, gidList);
    }

    public static int deleteTableByIdList(List<Integer> idList) throws SQLException {
        return deleteTableByIdList(DEFAULT_BEAN, idList);
    }

    public static int deleteTableByGidList(List<String> gidList) throws SQLException {
        return deleteTableByGidList(DEFAULT_BEAN, gidList);
    }

    public static List<Test> selectTableByIdList(List<Integer> idList) throws SQLException {
        return selectTableByIdList(DEFAULT_BEAN, idList);
    }

    public static List<Test> selectTableByGidList(List<String> gidList) throws SQLException {
        return selectTableByGidList(DEFAULT_BEAN, gidList);
    }

    public static List<Test> selectAllTable() throws SQLException {
        return selectAllTable(DEFAULT_BEAN);
    }

    @SuppressWarnings("unchecked")
    public static PageBean<Test> selectTableForCustomPage(TestVO bean) throws Exception {
        List<Object> countParamList = new ArrayList<Object>();
        List<Object> paramList = new ArrayList<Object>();
        String countSql = setSearchCondition(bean, countParamList, true);
        String sql = setSearchCondition(bean, paramList, false);
        PageParamBean pageParamBean = new PageParamBean()
                .setBean(bean)
                .setCountSql(countSql)
                .setCountParamList(countParamList)
                .setSql(sql)
                .setParamList(paramList)
                .setPage(bean.getPage())
                .setSize(bean.getSize());
        return selectTableForPage(pageParamBean);
    }

    private static String setSearchCondition(TestVO bean, List<Object> paramList, boolean isCountSql) {
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

    public static String selectTestNameById(Integer id) throws SQLException {
        String sql = "SELECT test_name FROM test WHERE id = ?";
        return executeSelectReturnString(sql, List.of(id));
    }

    public static Integer selectCreateTimeById(Integer id) throws SQLException {
        String sql = "SELECT create_time FROM test WHERE id = ?";
        return executeSelectReturnInteger(sql, List.of(id));
    }

    public static Long selectUpdateTimeById(Integer id) throws SQLException {
        String sql = "SELECT update_time FROM test WHERE id = ?";
        return executeSelectReturnLong(sql, List.of(id));
    }

    public static BigDecimal selectMoneyById(Integer id) throws SQLException {
        String sql = "SELECT money FROM test WHERE id = ?";
        return executeSelectReturnBigDecimal(sql, List.of(id));
    }

    public static List<String> selectTestNameByIdList(List<Integer> idList) throws SQLException {
        String sql = "SELECT test_name FROM test WHERE id" + makeInStr(idList);
        return executeSelectReturnStringList(sql, idList);
    }

    public static List<Integer> selectCreateTimeByIdList(List<Integer> idList) throws SQLException {
        String sql = "SELECT create_time FROM test WHERE id" + makeInStr(idList);
        return executeSelectReturnIntegerList(sql, idList);
    }

    public static List<Long> selectUpdateTimeByIdList(List<Integer> idList) throws SQLException {
        String sql = "SELECT update_time FROM test WHERE id" + makeInStr(idList);
        return executeSelectReturnLongList(sql, idList);
    }

    public static List<BigDecimal> selectMoneyByIdList(List<Integer> idList) throws SQLException {
        String sql = "SELECT money FROM test WHERE id" + makeInStr(idList);
        return executeSelectReturnBigDecimalList(sql, idList);
    }
}