package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liwenguang
 * @since 2021/2/23 10:43
 */
@Repository
public interface CcbgDao {

    //..
    List<JSONObject> queryFgzrByUserId(String userId);

    //..
    List<JSONObject> querySzrByUserId(String userId);

    //..
    List<Map<String, Object>> queryCcbgList(Map<String, Object> param);

    //..
    int countCcbgList(Map<String, Object> param);

    //..
    void insertCcbg(JSONObject param);

    //..
    void updateCcbg(JSONObject param);

    //..
    JSONObject queryCcbgById(String ccbgId);

    //..
    List<JSONObject> queryCcbgFileList(Map<String, Object> param);

    //..
    void addCcbgFileInfos(JSONObject param);

    //..
    void deleteCcbgFile(Map<String, Object> param);

    //..
    void deleteCcbg(Map<String, Object> param);

    //..
    List<String> getFileNamesListByMainId(Object id);

//    List<JSONObject> userUpdateQuery();

//    void userUpdate(JSONObject param);
}
