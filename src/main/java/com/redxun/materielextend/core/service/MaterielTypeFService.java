package com.redxun.materielextend.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielTypeFDao;
import com.redxun.materielextend.core.service.info.IT_TAB;
import com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFLocator;
import com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFPortType;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;
import java.util.*;

@Service
@Slf4j
public class MaterielTypeFService {
    private Logger logger = LogManager.getLogger(MaterielTypeFService.class);
    @Autowired
    private MaterielTypeFDao materielTypeFDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryMaterielTypeF(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        String currentUserId = ContextUtil.getCurrentUserId();
        params.put("currentUserId", currentUserId);
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        // 1、admin和朱佳查看所有，因此不附加条件
        String cxyProjectLookUserNo = WebAppUtil.getProperty("cxyProjectLookUserNo", "");
        boolean isZjl = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "信息部物料扩充专员");
        if (!currentUserNo.equalsIgnoreCase("admin")&& !currentUserNo.equalsIgnoreCase(cxyProjectLookUserNo)&&!isZjl) {
            params.put("currentUserId", currentUserId);
            // 2、分管领导、管理专员、数据特权人员，也能看所有数据（不考虑草稿）。使用“fgld”参数代表
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "分管领导");
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if ((queryRoleResult != null && !queryRoleResult.isEmpty())) {
                params.put("roleName", "fgld");
            } else {
                queryUserParam.clear();
                queryUserParam.put("userId", currentUserId);
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-LEADER");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult != null && !queryDeptResult.isEmpty()) {
                    // 3、分管主任或部门负责人，查看自己负责部门的、自己创建的，使用“fgzr”参数代表（不考虑草稿）
                    params.put("roleName", "fgzr");
                    judgeUserDeptRole(queryDeptResult, params);
                } else {
                    // 4、其他人员，查看自己创建的，使用"ptyg"参数代表（不考虑草稿）
                    params.put("roleName", "ptyg");
                }
            }
        }

        // addMaterielTypeFRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> materielTypeFList = materielTypeFDao.queryMaterielTypeF(params);
        for (JSONObject materielTypeF : materielTypeFList) {
            if (materielTypeF.get("CREATE_TIME_") != null) {
                materielTypeF.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)materielTypeF.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        List<JSONObject> materielTypeFdetailList = materielTypeFDao.queryMaterielTypeFallDetail();
        Set unfinishSet = new HashSet();
        Set errorSet = new HashSet();
        for(JSONObject oneDetail:materielTypeFdetailList){
            String resultCode = oneDetail.getString("result");
            if(StringUtils.isBlank(resultCode)){
                unfinishSet.add(oneDetail.getString("belongId"));
            }else if("E".equalsIgnoreCase(resultCode)){
                errorSet.add(oneDetail.getString("belongId"));
            }
        }
        for(JSONObject oneInfo:materielTypeFList){
            String id = oneInfo.getString("id");
            if(errorSet.contains(id)){
                oneInfo.put("infoStatus","有错误记录");
            }else if(unfinishSet.contains(id)){
                oneInfo.put("infoStatus","记录未提交");
            }else{
                oneInfo.put("infoStatus","记录创建成功");
            }
        }
        result.setData(materielTypeFList);
        int countMaterielTypeFDataList = materielTypeFDao.countMaterielTypeFList(params);
        result.setTotal(countMaterielTypeFDataList);
        return result;
    }

    private void judgeUserDeptRole(List<Map<String, Object>> queryDeptResult, Map<String, Object> params) {
        Set<String> deptIdList = new HashSet<>();
        for (Map<String, Object> oneDept : queryDeptResult) {
            String groupId = oneDept.get("PARTY1_").toString();
            deptIdList.add(groupId);
        }
        params.put("deptIds", deptIdList);
    }

    public void createMaterielTypeF(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        materielTypeFDao.createMaterielTypeF(formData);
    }

    public void updateMaterielTypeF(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        materielTypeFDao.updateMaterielTypeF(formData);
    }

    public JSONObject getMaterielTypeFById(String id) {
        JSONObject detailObj = materielTypeFDao.queryMaterielTypeFById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JSONObject setWlinfo(String id) {
        JSONObject detailObj = materielTypeFDao.setWlinfo(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void createMaterielTypeFDetail(JSONObject formData) {
        formData.put("detailId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        materielTypeFDao.createMaterielTypeFDetail(formData);
    }

    public void updateMaterielTypeFDetail(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        materielTypeFDao.updateMaterielTypeFDetail(formData);
    }

    public JSONObject getMaterielTypeFDetail(String detailId) {
        JSONObject detailObj = materielTypeFDao.queryMaterielTypeFDetailById(detailId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getMaterielTypeFDetailList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> materielTypeFDetailList = materielTypeFDao.queryMaterielTypeFDetail(param);
        for (JSONObject materielTypeF : materielTypeFDetailList) {
            if (materielTypeF.get("CREATE_TIME_") != null) {
                materielTypeF.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)materielTypeF.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return materielTypeFDetailList;
    }

    public JsonResult deleteOneMaterielTypeFDetail(String detailId) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("detailId", detailId);
        materielTypeFDao.deleteMaterielTypeFDetail(param);
        return result;
    }

    public JsonResult deleteMaterielTypeF(String[] idsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String id : idsArr) {
            // 删除基本信息
            param.clear();
            param.put("id", id);
            materielTypeFDao.deleteMaterielTypeF(param);
            // 查询明细
            param.clear();
            param.put("belongId", id);
            List<JSONObject> materielTypeFDetailList = materielTypeFDao.queryMaterielTypeFDetail(param);
            if (materielTypeFDetailList != null && !materielTypeFDetailList.isEmpty()) {
                // 删除明细和附件
                for (JSONObject oneDetail : materielTypeFDetailList) {
                    deleteOneMaterielTypeFDetail(oneDetail.getString("detailId"));
                }
            }
        }
        return result;
    }

    // 对于非问题物料，调用SAP的接口。如果调用失败则返回失败，调用成功则更新sap返回的失败的物料为问题物料
    public void callSAP(List<JSONObject> materielSync2SAP, ResultData resultData) {
        if (materielSync2SAP == null || materielSync2SAP.isEmpty()) {
            return;
        }
        // 调用SAP
        try {
            List<JSONObject> updateErrorMateriels = new ArrayList<>();
            XgwjjSapZWJRDM_SET_INFINFLocator locator = new XgwjjSapZWJRDM_SET_INFINFLocator();
            XgwjjSapZWJRDM_SET_INFINFPortType service = locator.getXgwjjSapZWJRDM_SET_INFINFHttpPort();
            List<IT_TAB> tempDATA = new ArrayList<>();
            Map<String, JSONObject> tempObj = new HashMap<>();
            for (JSONObject oneMat : materielSync2SAP) {
                IT_TAB inData = convertJSON2Data(oneMat);
                long flag = System.currentTimeMillis();
                logger.info(inData.getMATNR()+"supplier:“" + inData.getLIFNR() + "”记录供应商代码，" + flag);
                tempDATA.add(inData);
                tempObj.put(oneMat.getString("wlhm"), oneMat);
                if (tempDATA.size() == 20) {
                    IT_TAB[] tempArr = new IT_TAB[tempDATA.size()];
                    callSAPReal(tempDATA.toArray(tempArr), tempObj, service, resultData, updateErrorMateriels);
                    tempDATA.clear();
                    tempObj.clear();
                    if (!resultData.isSuccess()) {
                        return;
                    }
                }
            }
            if (!tempDATA.isEmpty()) {
                IT_TAB[] tempArr = new IT_TAB[tempDATA.size()];
                callSAPReal(tempDATA.toArray(tempArr), tempObj, service, resultData, updateErrorMateriels);
                tempDATA.clear();
                tempObj.clear();
                if (!resultData.isSuccess()) {
                    return;
                }
            }

            //更新结果信息
            if (!updateErrorMateriels.isEmpty()) {
                for (JSONObject oneMateriel : updateErrorMateriels) {
                    materielTypeFDao.updateMaterielError(oneMateriel);
                }
            }
        } catch (Exception e) {
            logger.error("创建信息记录失败", e);
            resultData.setSuccess(false);
            resultData.setMessage("调用SAP失败，请点击提交重试，或联系管理员处理！");
        }
    }

    // 将物料转为SAP数据
    private IT_TAB convertJSON2Data(JSONObject oneMat) {
        IT_TAB inTab = new IT_TAB();
        inTab.setMATNR(oneMat.getString("wlhm"));
        inTab.setLIFNR(oneMat.getString("supplier"));
        inTab.setEKORG(oneMat.getString("xszz"));
        inTab.setWERKS(oneMat.getString("gc"));
        inTab.setESOKZ(oneMat.getString("materType"));
        inTab.setAPLFZ(oneMat.getString("jhjhsj"));
        inTab.setEKGRP(oneMat.getString("cgz"));
        inTab.setNETPR(oneMat.getString("jg"));
        inTab.setBPRME(oneMat.getString("dw"));
        inTab.setWAERS(oneMat.getString("currency"));
        inTab.setPEINH(oneMat.getString("jgdw"));
        inTab.setMWSKZ(oneMat.getString("taxCode"));
        inTab.setEVERS(oneMat.getString("estimatePrice"));
        inTab.setZRESULT("");
        inTab.setMESSAGE("");
        inTab.setINFNR("");
        inTab.setZMARK("");
        return inTab;
    }

    // 批量调用SAP一次
    private void callSAPReal(IT_TAB[] inData, Map<String, JSONObject> tempObj,
        XgwjjSapZWJRDM_SET_INFINFPortType service, ResultData resultData, List<JSONObject> updateErrorMateriels)
        throws RemoteException {
        IT_TAB[] outData = service.ZWJRDM_SET_INFNR(inData);
        if (outData == null || outData.length == 0) {
            logger.error("return message from SAP is blank");
            resultData.setSuccess(false);
            resultData.setMessage("SAP返回数据为空！请点击提交重试，或联系管理员处理！");
            return;
        }
        for (IT_TAB temp : outData) {
            String returnCode = temp.getZRESULT();
            String returnMessage = temp.getMESSAGE();
            String wlhm = temp.getMATNR().replaceAll("^(0+)", "");;
            String infoNum = temp.getINFNR();
            String toTest = temp.getZMARK();
            if (StringUtils.isBlank(returnMessage)) {
                logger.error("returnMessage from SAP is blank");
                resultData.setSuccess(false);
                resultData.setMessage("SAP调用失败！返回信息为空!请点击提交重试，或联系管理员处理！");
                return;
            }
            JSONObject oneMateriel = tempObj.get(wlhm);
            oneMateriel.put("result", returnCode);
            oneMateriel.put("message", returnMessage);
            oneMateriel.put("infoNum", infoNum);
            oneMateriel.put("toTest", toTest);
            updateErrorMateriels.add(oneMateriel);
        }
    }
}
