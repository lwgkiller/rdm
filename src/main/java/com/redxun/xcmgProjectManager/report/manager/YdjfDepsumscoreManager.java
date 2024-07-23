
package com.redxun.xcmgProjectManager.report.manager;

import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmCommon.core.util.RdmConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redxun.core.dao.IDao;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.entity.YdjfDepsumscore;

@Service
public class YdjfDepsumscoreManager extends MybatisBaseManager<YdjfDepsumscore> {
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Resource
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    @SuppressWarnings("rawtypes")
    @Override
    protected IDao getDao() {
        return null;
    }

    public YdjfDepsumscore getYdjfDepsumscore(String uId) {
        YdjfDepsumscore ydjfDepsumscore = get(uId);
        return ydjfDepsumscore;
    }

    @Override
    public void create(YdjfDepsumscore entity) {
        entity.setId(IdUtil.getId());
        super.create(entity);

    }

    @Override
    public void update(YdjfDepsumscore entity) {
        super.update(entity);

    }

    public List<Map<String, Object>> selectList(YdjfDepsumscore ydjfDepsumscoreParam) {
        // 数据库中存储的时间是UTC时间，需要将查询条件转为UTC时间
        String queryTime = ydjfDepsumscoreParam.getTime() + "-01T00:00:00";
        String queryTimeFrom = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(queryTime), -8));
        String queryTimeTo =
            DateUtil.formatDate(DateUtil.addHour(DateUtil.add(DateUtil.parseDate(queryTime), Calendar.MONTH, 1), -8));
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("timeFrom", queryTimeFrom);
        params.put("timeTo", queryTimeTo);
        // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
        boolean isJSZXProjManagUsers =
            commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
        if (isJSZXProjManagUsers) {
            Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
            //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
            boolean isGYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isGYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.GYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
            //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
            boolean isYYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isYYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.YYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
        }
        List<Map<String, Object>> queryDepScores = xcmgProjectReportDao.countYdjf(params);
        if (!queryDepScores.isEmpty()) {
            Iterator iterator = queryDepScores.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> ydjfDepsumscore = (Map<String, Object>)iterator.next();
                String deptId = ydjfDepsumscore.get("depid").toString();
                String deptName = ydjfDepsumscore.get("depname").toString();
                if (!deptName.equalsIgnoreCase(RdmConst.GYJSB_NAME) && !deptName.equalsIgnoreCase(RdmConst.XXHGLB_NAME)
                    && !loginRecordManager.judgeIsJSZX(deptId, deptName).getBooleanValue("isJSZX")) {
                    iterator.remove();
                }
            }
        }
//        if (queryDepScores.size() > ConstantUtil.SHOW_NUM) {
//            queryDepScores = queryDepScores.subList(0, ConstantUtil.SHOW_NUM);
//        }
        return queryDepScores;
    }
}
