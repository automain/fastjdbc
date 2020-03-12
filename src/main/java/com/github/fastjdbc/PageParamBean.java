/*
 * Copyright 2019 fastjdbc
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

package com.github.fastjdbc;

import java.util.List;

/**
 * A bean for set the param of query for page.
 *
 * @param <T> an object which implement {@link BaseBean}
 * @since 1.5
 */
public class PageParamBean<T extends BaseBean> {

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
     * Param list for the count sql.
     *
     * @since 1.5
     */
    private List<Object> countParamList;


    /**
     * Sql to query the result.
     *
     * @since 1.5
     */
    private String sql;

    /**
     * Param list for the query sql.
     *
     * @since 1.5
     */
    private List<Object> paramList;

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
    private int size;

    public T getBean() {
        return bean;
    }

    public PageParamBean<T> setBean(T bean) {
        this.bean = bean;
        return this;
    }

    public String getCountSql() {
        return countSql;
    }

    public PageParamBean<T> setCountSql(String countSql) {
        this.countSql = countSql;
        return this;
    }

    public List<Object> getCountParamList() {
        return countParamList;
    }

    public PageParamBean<T> setCountParamList(List<Object> countParamList) {
        this.countParamList = countParamList;
        return this;
    }

    public String getSql() {
        return sql;
    }

    public PageParamBean<T> setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    public PageParamBean<T> setParamList(List<Object> paramList) {
        this.paramList = paramList;
        return this;
    }

    public int getPage() {
        return page;
    }

    public PageParamBean<T> setPage(int page) {
        this.page = page;
        return this;
    }

    public int getSize() {
        return size;
    }

    public PageParamBean<T> setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        return "PageParamBean{" +
                "bean=" + bean +
                ", countSql='" + countSql + '\'' +
                ", countParamList=" + countParamList +
                ", sql='" + sql + '\'' +
                ", paramList=" + paramList +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
