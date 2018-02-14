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
    private Integer curr;

    /**
     * Total number of records conforming to query conditions.
     *
     * @since 1.0
     */
    private Integer count;

    /**
     * The record of the current page.
     *
     * @since 1.0
     */
    private List<T> data;

    public Integer getCurr() {
        return curr;
    }

    public void setCurr(Integer curr) {
        this.curr = curr;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
