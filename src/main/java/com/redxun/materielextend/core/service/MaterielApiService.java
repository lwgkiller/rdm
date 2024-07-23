package com.redxun.materielextend.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.materielextend.core.dao.MaterielDao;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:07
 */
@Service
public class MaterielApiService {
    private static final Logger logger = LoggerFactory.getLogger(MaterielApiService.class);

    @Autowired
    private MaterielService materielService;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private MaterielDao materielDao;

    /**
     * MPM平台调用接口进行物料扩充申请单的创建
     *
     * @param postBody
     * @return
     */
    public void mpmCreateApply(JSONObject result, String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            result.put("success", false);
            result.getJSONArray("messages").add("传输的消息内容为空！");
            return;
        }
        // 查询到当前的操作人
        String sfzh = postBodyObj.getString("sfzh");
        if (StringUtils.isBlank(sfzh)) {
            result.put("success", false);
            result.getJSONArray("messages").add("请传输当前登录用户的身份证号信息！");
            return;
        }
        List<JSONObject> sqUserInfos = materielApplyDao.queryUserInfoByCertNo(sfzh);
        if (sqUserInfos == null || sqUserInfos.isEmpty()) {
            result.put("success", false);
            result.getJSONArray("messages").add("当前登录用户的身份证号在RDM中找不到！");
            return;
        }
        JSONObject sqUserInfo = sqUserInfos.get(0);
        // 抽取并检查要扩充的物料信息，将有问题的筛除并输出原因
        JSONArray materielArray = postBodyObj.getJSONArray("materiels");
        if (materielArray == null || materielArray.isEmpty()) {
            result.put("success", false);
            result.getJSONArray("messages").add("要扩充的物料数组为空！");
            return;
        }
        List<JSONObject> dataInsert = new ArrayList<>();
        List<String> failReasons = new ArrayList<>();
        for (int index = 0; index < materielArray.size(); index++) {
            getOneMatInfoFromObject(dataInsert, failReasons, materielArray.getJSONObject(index));
        }
        // 将已经扩充成功、正在进行中的过滤掉
        filterOKorDoing(dataInsert, failReasons);
        if (!failReasons.isEmpty()) {
            result.put("success", false);
            result.getJSONArray("messages").addAll(failReasons);
        }
        // 如果全都是有问题的、已经在进行中的、扩充成功的，则不再进行创建申请单
        if (dataInsert.isEmpty()) {
            return;
        }
        // 创建申请单
        String applyNo = mpmCreateAndCommitApply(sqUserInfo);
        // 批量新增物料
        List<JSONObject> tempAddList = new ArrayList<>();
        for (int i = 0; i < dataInsert.size(); i++) {
            JSONObject oneAddMaterielObj = dataInsert.get(i);
            materielService.convert2Upper(oneAddMaterielObj);
            oneAddMaterielObj.put("applyNo", applyNo);
            tempAddList.add(oneAddMaterielObj);
            logger.info("MPM接口（add）" + "单号：" + applyNo + "更新人员:" + ContextUtil.getCurrentUserId() +"采购类型："+oneAddMaterielObj.get("cglx") +"采购存储地点：" + oneAddMaterielObj.get("cgccdd"));
            if (tempAddList.size() % 20 == 0) {
                materielDao.batchAddMateriels(tempAddList);
                tempAddList.clear();
            }
        }
        if (!tempAddList.isEmpty()) {
            materielDao.batchAddMateriels(tempAddList);
            tempAddList.clear();
        }
    }

    /**
     * 过滤掉进行中的或者已经扩充成功的
     * 
     * @param dataInsert
     * @param failReasons
     */
    private void filterOKorDoing(List<JSONObject> dataInsert, List<String> failReasons) {
        if (dataInsert == null || dataInsert.isEmpty()) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        Set<String> wlhmSet = new HashSet<>();
        for (JSONObject oneData : dataInsert) {
            wlhmSet.add(oneData.getString("wlhm"));
        }
        param.put("wlhmList", new ArrayList<String>(wlhmSet));
        List<JSONObject> okOrDoingMats = materielDao.queryOkOrDoingMatByIds(param);
        Set<String> okOrDoingWlhms = new HashSet<>();
        for (JSONObject oneMat : okOrDoingMats) {
            okOrDoingWlhms.add(oneMat.getString("wlhm"));
        }
        Iterator<JSONObject> iterator = dataInsert.iterator();
        while (iterator.hasNext()) {
            JSONObject oneData = iterator.next();
            String oneDataWlhm = oneData.getString("wlhm");
            if (okOrDoingWlhms.contains(oneDataWlhm)) {
                iterator.remove();
                failReasons.add(oneDataWlhm + "：物料正在扩充或已扩充成功");
            }
        }
    }

    /**
     * 从接口传递的数据中提取物料扩充信息，写死简单处理，不再用之前复杂的配置
     * 
     * @param dataInsert
     * @param failReasons
     * @param originalMat
     */
    private void getOneMatInfoFromObject(List<JSONObject> dataInsert, List<String> failReasons,
        JSONObject originalMat) {
        JSONObject resultMat = new JSONObject();
        String wlhm = originalMat.getString("wlhm");
        if (StringUtils.isBlank(wlhm)) {
            failReasons.add("：物料号码为空");
            return;
        }
        String wllx = originalMat.getString("wllx");
        if (StringUtils.isBlank(wllx)) {
            failReasons.add(wlhm + "：物料类型为空");
            return;
        }
        String wlms = originalMat.getString("wlms");
        if (StringUtils.isBlank(wlms)) {
            failReasons.add(wlhm + "：物料描述为空");
            return;
        }
        String dw = originalMat.getString("dw");
        if (StringUtils.isBlank(dw)) {
            failReasons.add(wlhm + "：单位为空");
            return;
        }
        String wlz = originalMat.getString("wlz");
        if (StringUtils.isBlank(wlz)) {
            failReasons.add(wlhm + "：物料组为空");
            return;
        }
        resultMat.put("id", IdUtil.getId());
        resultMat.put("wlhm", wlhm);
        resultMat.put("wllx", wllx);
        resultMat.put("gc", "2080");
        resultMat.put("xszz", "2080");
        resultMat.put("fxqd", "10");
        resultMat.put("wlms", wlms);
        resultMat.put("dw", dw);
        resultMat.put("wlz", wlz);
        resultMat.put("cpz", "FERT".equalsIgnoreCase(wllx) ? "16" : "10");
        resultMat.put("jhgc", "2080");
        resultMat.put("kmszz", "FERT".equalsIgnoreCase(wllx) ? "02" : "01");
        resultMat.put("jg", originalMat.getString("jg") == null ? "" : originalMat.getString("jg"));
        resultMat.put("zl", originalMat.getString("zl") == null ? "" : originalMat.getString("zl"));
        resultMat.put("gys", originalMat.getString("gys") == null ? "" : originalMat.getString("gys"));
        resultMat.put("bzcz", originalMat.getString("bzcz") == null ? "" : originalMat.getString("bzcz"));
        resultMat.put("jkgc", "");
        resultMat.put("cgz", "");
        resultMat.put("mrplx", "PD");
        resultMat.put("mrpkzz", "");
        resultMat.put("kyxjc", "FERT".equalsIgnoreCase(wllx) ? "KP" : "02");
        String cglx = originalMat.getString("cglx") == null ? "" : originalMat.getString("cglx");
        if (StringUtils.isNotBlank(cglx) && !"E".equalsIgnoreCase(cglx) && !"F".equalsIgnoreCase(cglx)) {
            failReasons.add(wlhm + "：采购类型应该为E或F");
            return;
        }
        resultMat.put("cglx", cglx);
        String pldx = "";
        String dljz = "";
        if ("E".equalsIgnoreCase(cglx)) {
            pldx = "EX";
            dljz = "1";
        }
        if ("F".equalsIgnoreCase(cglx)) {
            pldx = "WB";
            dljz = "2";
        }
        resultMat.put("pldx", pldx);
        resultMat.put("dljz", dljz);
        resultMat.put("tscgl", "");
        resultMat.put("fc", "");
        resultMat.put("scccdd", "");
        resultMat.put("kcdd1", "");
        resultMat.put("kcdd2", "");
        resultMat.put("kcdd3", "");
        resultMat.put("pslx", "");
        resultMat.put("zzscsj", "");
        resultMat.put("jhjhsj", "");
        resultMat.put("xlhcswj", "");
        resultMat.put("lrzx", "208001");
        resultMat.put("pgl", "");
        resultMat.put("jgdw", "1");
        resultMat.put("wlzt", "");
        resultMat.put("markError", "");
        resultMat.put("markErrorReason", "");
        resultMat.put("markErrorUserId", "");
        resultMat.put("markErrorTime", "");
        resultMat.put("wlzt", MaterielConstant.WLZT);
        resultMat.put("markError", MaterielConstant.MARK_ERROR_NO);
        resultMat.put("jclx", "");
        resultMat.put("codeId", "");
        resultMat.put("codeName", "");
        resultMat.put("sfwg", "");
        dataInsert.add(resultMat);
    }

    // 创建并提交申请单
    private String mpmCreateAndCommitApply(JSONObject currentUser) {
        Map<String, String> param = new HashMap<>();
        List<JSONObject> roles = materielApplyDao.queryMaterielOpRoles(param);
        Map<String, String> roleKey2UserId = new HashMap<>();
        for (JSONObject oneRole : roles) {
            roleKey2UserId.put(oneRole.getString("KEY_"), oneRole.getString("userId"));
        }
        String applyNo = CommonUtil.toGetApplyNo();
        Map<String, Object> params = new HashMap<>();
        params.put("applyNo", applyNo);
        params.put("sqUserDepId", currentUser.getString("GROUP_ID_"));
        params.put("sqUserId", currentUser.getString("USER_ID_"));
        params.put("sqCommitTime", "");
        params.put("gyStatus", MaterielConstant.KCSTATUS_NO);
        params.put("gyCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_GYKC));
        params.put("cgStatus", MaterielConstant.KCSTATUS_NO);
        params.put("cgCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_CGKC));
        params.put("gfStatus", MaterielConstant.KCSTATUS_NO);
        params.put("gfCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_GFKC));
        params.put("cwStatus", MaterielConstant.KCSTATUS_NO);
        params.put("cwCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_CWKC));
        params.put("wlStatus", MaterielConstant.KCSTATUS_NO);
        params.put("wlCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_WLKC));
        params.put("zzStatus", MaterielConstant.KCSTATUS_NO);
        params.put("zzCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_ZZKC));
        params.put("applyStatus", MaterielConstant.APPLYSTATUS_DRAFT);
        params.put("urgent", "no");
        materielApplyDao.addMaterielApply(params);
        return applyNo;
    }

}
