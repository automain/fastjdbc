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
 * A container for page query.
 *
 * @param <T> an object which implement {@link BaseBean}
 * @since 1.0
 */
public class PageBean<T extends BaseBean> {

    /**
     * Current page number.
     *
     * @since 1.0
     */
    private Integer page;

    /**
     * Total number of records conforming to query conditions.
     *
     * @since 1.0
     */
    private Integer total;

    /**
     * The record of the current page.
     *
     * @since 1.0
     */
    private List<T> data;

    public Integer getPage() {
        return page;
    }

    public PageBean<T> setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public PageBean<T> setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public PageBean<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "page=" + page +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
