package com.redxun.rdmCommon.core.dao;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.redxun.sys.org.entity.OsInstUsers;
import com.redxun.sys.org.entity.OsRelInst;

/**
 * @author zz
 */
@Repository
public interface ImportUserDao {


    /**
     * 保存用户信息
     *
     * @param jsonObject
     */
    void addUserInfo(JSONObject jsonObject);

    /**
     * 保存用户机构信息
     *
     * @param osInstUsers
     */
    void addInstUser(OsInstUsers osInstUsers);

    /**
     * 保存用户角色信息
     *
     * @param osRelInst
     */
    void addOsRelInst(OsRelInst osRelInst);

    /**
     * 获取组织id
     *
     * @param  jsonObject
     * @return
     */
    JSONObject getGroupId(JSONObject jsonObject);
    /**
     *根据登录号查询用户数据
     *
     * @return json
     * @param userNo
     * */
    JSONObject getUserByNo(String userNo);
}
