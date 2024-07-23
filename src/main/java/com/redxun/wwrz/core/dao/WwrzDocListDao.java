package com.redxun.wwrz.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface WwrzDocListDao {


    /**
     * 保存
     * @param param
     * @return
     * */
    int addDoc(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateDoc(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delDocById(String id);
    /**
     * 获取文件列表
     *
     * @param mainId
     * @return list
     * */
    List<JSONObject> getDocumentList(String mainId);

}
