package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface XcmgProjectDeliveryApprovalDao {
    List<Map<String, Object>> queryDeliveryApproval(Map<String, Object> params);
}
