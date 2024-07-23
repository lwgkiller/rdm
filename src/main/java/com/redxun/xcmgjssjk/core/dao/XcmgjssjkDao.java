package com.redxun.xcmgjssjk.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface XcmgjssjkDao {
    void insertJssjk(JSONObject param);

    void updateJssjk(JSONObject param);

    List<Map<String, Object>> queryJssjkList(Map<String, Object> param);

    void addFileInfos(JSONObject param);

    List<JSONObject> queryJssjkFileList(Map<String, Object> param);

    void deleteJssjkFile(Map<String, Object> param);

    void deleteJssjk(Map<String, Object> param);

    int countJssjkList(Map<String, Object> param);

    List<Map<String, Object>> querySpList(Map<String, Object> param);

    int countSpList(Map<String, Object> param);

    JSONObject queryJssjkById(String jssjkId);

    List<JSONObject> queryJssjkByIdCp(String jssjkId);

    List<JSONObject> queryJssjkByIdCpFile(String jssjkId);

    List<JSONObject> getTdmcList(JSONObject jsonObject);

    void insertJssjktdmc(JSONObject param);

    void updateJssjktdmc(JSONObject param);

    void deleteJssjktdmc(Map<String, Object> param);

    JSONObject queryMaxJssjkNum(Map<String, Object> param);

    void updateJssjkNumber(Map<String, Object> param);

    void insertJssjktdmcwb(JSONObject param);

    void updateJssjktdmcwb(JSONObject param);

    void deleteJssjktdmcwb(Map<String, Object> param);

    List<JSONObject> getTdmcwbList(JSONObject jsonObject);
}
