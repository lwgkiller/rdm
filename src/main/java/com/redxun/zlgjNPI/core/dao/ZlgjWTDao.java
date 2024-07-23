package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZlgjWTDao {
    int countZlgjList(Map<String, Object> param);//主信息计数

    List<JSONObject> queryZlgjList(Map<String, Object> param);//查询主信息

    void insertZlgj(JSONObject param);//添加主信息

    void updateZlgj(JSONObject param);//更新主信息

    void deleteZlgj(Map<String, Object> param);//删除主信息

    JSONObject queryZlgjById(String jsmmId);//查询单个主信息

    List<JSONObject> queryZlgjListCq(Map<String, Object> param);//查询超期主信息

    int countZlgjListCq(Map<String, Object> param);//超期主信息计数

    List<JSONObject> queryZlgjFileList(Map<String, Object> param);//根据主信息ids获取文件列表

    List<JSONObject> getFileListByTypeId(Map<String, Object> params);//根据主信息id||附件类型||文件名获取文件列表

    List<JSONObject> getFileListByTypeIdh(Map<String, Object> params);//根据主信息id||附件类型||文件名获||解决方案id取文件列表

    void addFileInfos(Map<String, Object> params);//添加附件信息

    void deleteZlgjFile(Map<String, Object> param);//根据主信息ids||文件id删除附件信息

    void deleteFileByIds(Map<String, Object> params);//根据文件ids删除附件信息

    void updateZrbmshTime(JSONObject param);//更新责任部门审核时间

    //..问题原因列表
    List<JSONObject> getReasonList(JSONObject jsonObject);

    //..问题原因列表根据自己的ids查
    List<JSONObject> reasonList(JSONObject jsonObject);

    //添加问题原因
    int addWtyy(Map<String, Object> param);

    //更新问题原因
    int updatWtyy(Map<String, Object> params);

    //单个删除问题原因
    int delWtyy(String id);

    //批删问题原因
    void deleteWtyy(Map<String, Object> param);

    //..临时措施列表
    List<JSONObject> getLscsList(JSONObject jsonObject);

    //添加临时措施
    int addLscs(Map<String, Object> param);

    //更新临时措施
    int updatLscs(Map<String, Object> params);

    //单个删除临时措施
    int delLscs(String id);

    //批删临时措施
    void deleteLscs(Map<String, Object> param);

    //..原因验证列表
    List<JSONObject> getyzList(JSONObject jsonObject);

    //添加原因验证
    int addYyyz(Map<String, Object> param);

    //更新原因验证
    int updatYyyz(Map<String, Object> params);

    //单个删除原因验证
    int delYyyz(String id);

    //批删原因验证
    void deleteYyyz(Map<String, Object> param);

    //..方案验证列表
    List<JSONObject> getfayzList(JSONObject jsonObject);

    //添加方案验证
    int addFayz(Map<String, Object> param);

    //更新方案验证
    int updatFayz(Map<String, Object> params);

    //单个删除方案验证
    int delFayz(String id);

    //批删方案验证
    void deleteFayz(Map<String, Object> param);

    //..永久解决方案列表
    List<JSONObject> getfajjList(JSONObject jsonObject);

    //添加永久解决方案
    int addJjfa(Map<String, Object> param);

    //更新永久解决方案
    int updatJjfa(Map<String, Object> params);

    //单独删除永久解决方案
    int delJjfa(String id);

    //批删永久解决方案
    void deleteJjfa(Map<String, Object> param);

    //..风险排查人员列表
    List<JSONObject> getRiskUserList(JSONObject jsonObject);

    //添加风险排查人员
    int addRiskUser(Map<String, Object> param);

    //更新风险排查人员
    int updateRiskUser(Map<String, Object> params);

    //单独删除风险排查人员
    int delRiskUser(String id);

    //批删风险排查人员
    void deleteRiskUser(Map<String, Object> param);

    //..风险排查及整改列表
    List<JSONObject> getRiskList(JSONObject jsonObject);

    //添加风险排查及整改
    int addRisk(Map<String, Object> param);

    //更新风险排查及整改
    int updateRisk(Map<String, Object> params);

    //单独删除风险排查及整改
    int delRisk(String id);

    //批删风险排查及整改
    void deleteRisk(Map<String, Object> param);


    //..以下杂项
    List<Map<String, String>> queryUserByZlgjgcs(Map<String, Object> params);

    List<Map<String, Object>> queryJyjlUserRoles(Map<String, Object> params);

    List<JSONObject> querySCWTSHRYEnum(Map<String, Object> params);

    List<JSONObject> queryGzxtCodeByName(String gzxtName);

    void updateNumber(Map<String, Object> param);

    List<JSONObject> queryMainDeptByUserId(String userId);

    List<JSONObject> queryMaxZlgjNumber(String zlgjNumber);

    void updateZrcpzgStart(Map<String, Object> param);

    void updateZrcpzgEnd(Map<String, Object> param);

    void updateZrrStart(Map<String, Object> param);

    void updateZrrEnd(Map<String, Object> param);

    List<JSONObject> queryOutSystemByBusKeyId(Map<String, Object> param);

    void updateOutSystemCallResult(JSONObject param);

    List<JSONObject> queryReasonByWtId(Map<String, Object> params);

    List<JSONObject> queryLscsByWtId(Map<String, Object> params);

    List<JSONObject> queryCqcsByWtId(Map<String, Object> params);

    List<JSONObject> queryNodeFirstCreateTime(Map<String, Object> param);

    void insertZlgjOutSystem(JSONObject param);

    void deleteOutSystemByBusKeyId(Map<String, Object> param);

    List<JSONObject> queryApiList(String searchValue);

    List<JSONObject> queryNodeHandler(JSONObject param);

    void updatePdmPrNo(JSONObject param);
}
