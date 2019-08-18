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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Test implements BaseBean<Test> {

    // 主键
    private Integer id;
    // 测试GID
    private String gid;
    // 创建时间
    private Integer createTime;
    // 更新时间
    private Integer updateTime;
    // 是否有效(0:否,1:是)
    private Integer isValid;
    // 金额
    private BigDecimal money;
    // 备注
    private String remark;
    // 测试名称
    private String testName;

    public Integer getId() {
        return id;
    }

    public Test setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getGid() {
        return gid;
    }

    public Test setGid(String gid) {
        this.gid = gid;
        return this;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public Test setCreateTime(Integer createTime) {
        this.createTime = createTime;
        return this;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public Test setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public Test setIsValid(Integer isValid) {
        this.isValid = isValid;
        return this;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public Test setMoney(BigDecimal money) {
        this.money = money;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Test setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTestName() {
        return testName;
    }

    public Test setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    @Override
    public String tableName() {
        return "test";
    }

    @Override
    public String primaryKey() {
        return "id";
    }

    @Override
    public Integer primaryValue() {
        return this.getId();
    }

    @Override
    public Map<String, Object> columnMap(boolean all) {
        Map<String, Object> map = new HashMap<String, Object>(7);
        if (all || this.getGid() != null) {
            map.put("gid", this.getGid());
        }
        if (all || this.getCreateTime() != null) {
            map.put("create_time", this.getCreateTime());
        }
        if (all || this.getUpdateTime() != null) {
            map.put("update_time", this.getUpdateTime());
        }
        if (all || this.getIsValid() != null) {
            map.put("is_valid", this.getIsValid());
        }
        if (all || this.getMoney() != null) {
            map.put("money", this.getMoney());
        }
        if (all || this.getRemark() != null) {
            map.put("remark", this.getRemark());
        }
        if (all || this.getTestName() != null) {
            map.put("test_name", this.getTestName());
        }
        return map;
    }

    @Override
    public Test beanFromResultSet(ResultSet rs) throws SQLException {
        return new Test()
                .setId(rs.getInt("id"))
                .setGid(rs.getString("gid"))
                .setCreateTime(rs.getInt("create_time"))
                .setUpdateTime(rs.getInt("update_time"))
                .setIsValid(rs.getInt("is_valid"))
                .setMoney(rs.getBigDecimal("money"))
                .setRemark(rs.getString("remark"))
                .setTestName(rs.getString("test_name"));
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", gid='" + gid + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isValid=" + isValid +
                ", money=" + money +
                ", remark='" + remark + '\'' +
                ", testName='" + testName + '\'' +
                '}';
    }
}