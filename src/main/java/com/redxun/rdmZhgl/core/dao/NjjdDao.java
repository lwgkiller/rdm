package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

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
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Repository
public interface NjjdDao {
    List<Map<String, Object>> queryNjjdListByStatus(Map<String, Object> param);

    String countJsmmList(Map<String, Object> param);

    int countJsmmLists(Map<String, Object> param);

    void updateNumber(Map<String, Object> param);

    void insertNjjd(JSONObject param);

    void updateNjjd(JSONObject param);

    JSONObject queryNjjdById(String njjdId);

    List<JSONObject> queryNjjdFileList(Map<String, Object> param);

    List<JSONObject> queryNjjdFileTypes(String njfjDl);

    void addNjjdFileInfos(JSONObject param);

    void updateNjjdFileInfos(JSONObject param);

    void deleteNjjdFile(Map<String, Object> param);

    void deleteNjjd(Map<String, Object> param);

    List<JSONObject> getUserList(String njjdId);

    void saveUserList(JSONObject param);

    void updateUserList(JSONObject param);

    void delUserListByIds(Map<String, Object> params);

    void delUserListById(Map<String, Object> param);

    void delFjById(Map<String, Object> param);

    void delZsById(Map<String, Object> param);

    @Select("SELECT fjId FROM nj_userinfo WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);

    @Select("SELECT fjId FROM nj_userinfo WHERE id = #{id} ")
    String selectFjIdByNjBaseId(@Param("id") String id);

    @Select("SELECT njBaseId from nj_fileInfo  WHERE id = #{id} ")
    String selectNjjdByFjId(@Param("id") String id);

    Map<String, Object> getUser(@Param("standardId") String standardId);

    void saveParamInfo(JSONObject param);

    void updateParamInfo(JSONObject param);

    void updateSaleButton(Map<String, Object> param);

    void updateSfxzButton(Map<String, Object> param);

    /**
     * 查询该型号是否有在售类型
     *
     * @param productType
     * @return
     */
    @Select("SELECT  count(*) num from nj_baseInfo where productType = #{productType}  and saleStatus = '1'")
    String selectproductTypeNum(@Param("productType") String productType, @Param("njjdId") String njjdId);

    @Select("SELECT  count(*) num from nj_baseInfo where productType = #{productType}  and saleStatus = '1' and id != #{njjdId} ")
    String selectproductTypeNumById(@Param("productType") String productType, @Param("njjdId") String njjdId);

    /**
     * 查询用户条数
     *
     * @param njjdId
     * @return
     */
    @Select("    SELECT\n" + "    count(*) count\n" + "    FROM\n" + "    nj_userInfo\n"
        + "    left join   nj_fileInfo  on nj_userInfo.fjId = nj_fileInfo.id\n" + "    WHERE\n"
        + "    nj_userInfo.njBaseId =  #{njjdId}")
    String selectUserNum(@Param("njjdId") String njjdId);

    @Select("SELECT\n" + "count(distinct njfjXlName) count\n" + "\n" + "FROM (\n"
        + "SELECT * from nj_filetypeinfo WHERE njfjDl = 'js' "
        + ") t1 left join nj_fileinfo t2 on t1.njfjXlId = t2.njfjXlId \n" + "\n" + "where   t2.njBaseId =   #{njjdId}")
    String selectPicNum(@Param("njjdId") String njjdId);

    @Select("SELECT\n" + "count(distinct njfjXlName) count\n" + "\n" + "FROM (\n"
        + "SELECT * from nj_filetypeinfo WHERE njfjDl = 'zs' "
        + ") t1 left join nj_fileinfo t2 on t1.njfjXlId = t2.njfjXlId \n" + "\n" + "where   t2.njBaseId =   #{njjdId}")
    String selectZsNum(@Param("njjdId") String njjdId);

    List<Map<String, Object>> queryUserOperateApply(Map<String, Object> params);

    void insertNjjdxzpz(JSONObject param);

    void updateNjjdxzpz(JSONObject param);

    void deleteNjjdxzpz(Map<String, Object> param);

    List<JSONObject> getXzpzList(JSONObject jsonObject);

    List<Map<String, Object>> queryNjjdcpgg(Map<String, Object> param);

    List<Map<String, Object>> queryNjjdxzpz(Map<String, Object> param);

    JSONObject queryNjjdFileById(String id);

    void deleteNjjdparam(Map<String, Object> param);

    void deleteNjjduser(Map<String, Object> param);

    void updateNjSubsidy(JSONObject param);
}