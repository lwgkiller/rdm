package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ProductManageDao {
    List<Map<String, Object>> queryProductManageList(Map<String, Object> params);

    JSONObject queryProductManageById(String formId);

    int countProductManageLists(Map<String, Object> param);

    void deleteStandardById(String applyId);

    List<Map<String, Object>> queryTaskAll(Map<String, Object> param);

    List<JSONObject> isJSGLB(Map<String, Object> param);

    void deleteProductManage(Map<String, Object> param);

    /**
     * 产品技术管控需求下拉框
     * 
     * @param params
     * @return
     */
    List<Map<String, Object>> getProductExploitRelation(Map<String, Object> params);

    void addProduct(Map<String, Object> params);

    void updateProduct(Map<String, Object> params);

    void addProductManage(Map<String, Object> params);

    void updateProductManage(Map<String, Object> params);

    List<JSONObject> getItemList(JSONObject jsonObject);

    void delProductManageById(String manageId);

    /**
     * 查询该型号是否存在
     *
     * @param designType
     * @return
     */
    String selectproductTypeNum(@Param("designType") String designType, @Param("manageId") String manageId);

    List<Map<String, Object>> queryProductManDataList(Map<String, Object> params);

}
