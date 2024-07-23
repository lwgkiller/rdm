
package com.redxun.xcmgProjectManager.report.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.dao.IDao;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.entity.JfzbDepsumscore;
import com.redxun.xcmgProjectManager.report.entity.JfzbStatics;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class JfzbDepsumscoreManager extends MybatisBaseManager<JfzbDepsumscore> {
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @SuppressWarnings("rawtypes")
    @Override
    protected IDao getDao() {
        return null;
    }

    public List<JfzbDepsumscore> selectList(JSONObject postDataJson) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
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
        // 查询项目的大级别
        List<String> bigLevelNames = new ArrayList<>();
        List<Map<String, String>> mapLevel = xcmgProjectOtherDao.queryProjectLevel(params);
        if (mapLevel != null && !mapLevel.isEmpty()) {
            for (Map<String, String> oneLevel : mapLevel) {
                bigLevelNames.add(oneLevel.get("levelName"));
            }
        }

        // 查询立项时间满足条件的项目信息
        if (StringUtils.isNotBlank(postDataJson.getString("buildTimeFrom"))) {
            params.put("buildTimeFrom",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString("buildTimeFrom")), -8)));
        }
        if (StringUtils.isNotBlank(postDataJson.getString("buildTimeTo"))) {
            params.put("buildTimeTo",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString("buildTimeTo")), 16)));
        }
        List<Map<String, Object>> projectInfos = xcmgProjectReportDao.countJfzb(params);
        Map<String, JfzbDepsumscore> depName2Obj = new HashMap<>();
        for (Map<String, Object> projectInfo : projectInfos) {
            if (projectInfo.get("pointStandardScore") != null
                && StringUtils.isNotBlank(projectInfo.get("pointStandardScore").toString())) {
                projectInfo.put("score", (int)(Double.parseDouble(projectInfo.get("pointStandardScore").toString())));
            }
            // 组织部门的数据
            if (projectInfo.get("depName") == null) {
                continue;
            }
            String depName = projectInfo.get("depName").toString();
            if (!depName2Obj.containsKey(depName)) {
                depName2Obj.put(depName, new JfzbDepsumscore(depName));
            }
            JfzbDepsumscore obj = depName2Obj.get(depName);
            obj.setDepsumscore(obj.getDepsumscore() + (Integer)projectInfo.get("score"));
            // 组织级别的数据
            String levelName = projectInfo.get("levelName").toString();
            Map<String, JfzbStatics> levelName2Statics = obj.getLevelName2Statics();
            if (!levelName2Statics.containsKey(levelName)) {
                levelName2Statics.put(levelName, new JfzbStatics(levelName));
            }
            JfzbStatics levelObj = levelName2Statics.get(levelName);
            levelObj.setLevelsum(levelObj.getLevelsum() + (Integer)projectInfo.get("score"));
        }
        // 补充level数据
        for (JfzbDepsumscore obj : depName2Obj.values()) {
            Map<String, JfzbStatics> levelData = obj.getLevelName2Statics();
            for (String levelName : bigLevelNames) {
                if (!levelData.containsKey(levelName)) {
                    levelData.put(levelName, new JfzbStatics(levelName));
                }
            }
        }
        List<JfzbDepsumscore> result = new ArrayList<>();
        result.addAll(depName2Obj.values());

        // 从大到小排序
        Collections.sort(result, new Comparator<JfzbDepsumscore>() {
            @Override
            public int compare(JfzbDepsumscore o1, JfzbDepsumscore o2) {
                return o2.getDepsumscore() - o1.getDepsumscore();
            }
        });
//        if (result.size() > ConstantUtil.SHOW_NUM) {
//            result = result.subList(0, ConstantUtil.SHOW_NUM);
//        }
        // 对每一个JfzbDepsumscore中的levelName2Statics进行排序
        for (JfzbDepsumscore oneResult : result) {
            List<JfzbStatics> oneResultStatics = new ArrayList<JfzbStatics>(oneResult.getLevelName2Statics().values());
            Collections.sort(oneResultStatics, new Comparator<JfzbStatics>() {
                @Override
                public int compare(JfzbStatics o1, JfzbStatics o2) {
                    return o1.getLevelname().compareTo(o2.getLevelname());
                }
            });
            oneResult.getJfzbStaticsList().addAll(oneResultStatics);
        }
        return result;
    }

}
