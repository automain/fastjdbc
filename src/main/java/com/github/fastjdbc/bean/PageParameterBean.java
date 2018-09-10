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

package com.github.fastjdbc.bean;

import com.github.fastjdbc.common.BaseBean;

import java.util.List;

/**
 * A bean for set the parameter of query for page.
 *
 * @param <T> an object which implement {@link BaseBean}
 * @since 1.5
 */
public class PageParameterBean<T extends BaseBean> {

    /**
     * {@link ConnectionBean} object.
     *
     * @since 1.5
     */
    private ConnectionBean connection;

    /**
     * Bean object witch type is same with the query result.
     *
     * @since 1.5
     */
    private T bean;

    /**
     * Sql to count the total of result we want. This sql must return one column of int type.
     * When the query sql is complex, the count sql usually can simpler than the query sql.
     *
     * @since 1.5
     */
    private String countSql;

    /**
     * Parameter list for the count sql.
     *
     * @since 1.5
     */
    private List<Object> countParameterList;


    /**
     * Sql to query the result.
     *
     * @since 1.5
     */
    private String sql;

    /**
     * Parameter list for the query sql.
     *
     * @since 1.5
     */
    private List<Object> parameterList;

    /**
     * Page number.
     *
     * @since 1.5
     */
    private int page;

    /**
     * Count of data displayed on each page.
     *
     * @since 1.5
     */
    private int limit;

    public ConnectionBean getConnection() {
        return connection;
    }

    public void setConnection(ConnectionBean connection) {
        this.connection = connection;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public List<Object> getCountParameterList() {
        return countParameterList;
    }

    public void setCountParameterList(List<Object> countParameterList) {
        this.countParameterList = countParameterList;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Object> parameterList) {
        this.parameterList = parameterList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
