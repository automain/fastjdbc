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

package com.github.fastjdbc.test.bean;

import com.github.fastjdbc.common.BaseBean;
import com.github.fastjdbc.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TbUser extends RequestUtil implements BaseBean<TbUser> {

    // 用户ID
    private Long userId;

    // 用户名
    private String userName;

    // 密码MD5值
    private String passwordMd5;

    // 手机号
    private String cellphone;

    // 创建时间
    private Integer createTime;

    // 邮箱
    private String email;

    // 是否删除(0:否,1:是)
    private Integer isDelete;

    // ========== additional column begin ==========

    // 创建时间
    private String createTimeRange;

    public String getCreateTimeRange() {
        return createTimeRange;
    }

    public void setCreateTimeRange(String createTimeRange) {
        this.createTimeRange = createTimeRange;
    }

    // ========== additional column end ==========

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String tableName() {
        return "tb_user";
    }

    @Override
    public String primaryKey() {
        return "user_id";
    }

    @Override
    public Long primaryValue() {
        return this.getUserId();
    }

    @Override
    public Map<String, Object> columnMap(boolean all) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (all || this.getCellphone() != null) {
            map.put("cellphone", this.getCellphone());
        }
        if (all || this.getCreateTime() != null) {
            map.put("create_time", this.getCreateTime());
        }
        if (all || this.getEmail() != null) {
            map.put("email", this.getEmail());
        }
        if (all || this.getIsDelete() != null) {
            map.put("is_delete", this.getIsDelete());
        }
        if (all || this.getPasswordMd5() != null) {
            map.put("password_md5", this.getPasswordMd5());
        }
        if (all || this.getUserName() != null) {
            map.put("user_name", this.getUserName());
        }
        return map;
    }

    @Override
    public TbUser beanFromResultSet(ResultSet rs) throws SQLException {
        TbUser bean = new TbUser();
        bean.setUserId(rs.getLong("user_id"));
        bean.setUserName(rs.getString("user_name"));
        bean.setPasswordMd5(rs.getString("password_md5"));
        bean.setCellphone(rs.getString("cellphone"));
        bean.setCreateTime(rs.getInt("create_time"));
        bean.setEmail(rs.getString("email"));
        bean.setIsDelete(rs.getInt("is_delete"));
        return bean;
    }

    @Override
    public TbUser beanFromRequest(HttpServletRequest request) {
        TbUser bean = new TbUser();
        bean.setUserId(getLong("userId", request));
        bean.setUserName(getString("userName", request));
        bean.setPasswordMd5(getString("passwordMd5", request));
        bean.setCellphone(getString("cellphone", request));
        bean.setCreateTime(getInt("createTime", request));
        bean.setEmail(getString("email", request));
        bean.setIsDelete(getInt("isDelete", request, 0));
        bean.setCreateTimeRange(getString("createTimeRange", request));
        return bean;
    }
}