package com.redxun.drbfm.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lcj
 */
@Repository
public interface DrbfmReportDao {
    List<JSONObject> queryQuotaReportList(Map<String, Object> params);

    int countQuotaReport(Map<String, Object> params);
}
