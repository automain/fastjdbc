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

import com.github.fastjdbc.bean.PageBean;
import com.github.fastjdbc.bean.PageParamBean;
import com.github.fastjdbc.common.BaseDao;
import com.github.fastjdbc.test.bean.Test;
import com.github.fastjdbc.test.vo.TestVO;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class TestDao extends BaseDao<Test> {

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