package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.newProductAssembly.core.service.NewproductAssemblyKanbanService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.FileOperateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.zlgjNPI.core.dao.GjllDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZlgjHandler
        implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ZlgjHandler.class);
    @Autowired
    private ZlgjWTService zlgjWTService;
    @Autowired
    private GjllDao gjllTDao;
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private NewproductAssemblyKanbanService newproductAssemblyKanbanService;
    @Autowired
    private BpmTaskManager bpmTaskManager;


    //整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String wtId = createOrUpdateZlgjByFormData(processStartCmd);
        if (StringUtils.isNotBlank(wtId)) {
            processStartCmd.setBusinessKey(wtId);
        }
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray jsonArray = new JSONArray();
        JSONObject designModel = new JSONObject();
        JSONObject bigWtlx = new JSONObject();
        designModel.put("key", "smallJiXing");
        bigWtlx.put("key", "bigWtlx");
        if(!"LBJSY".equalsIgnoreCase(formDataJson.getString("wtlx"))){
            designModel.put("val", formDataJson.getString("smallJiXing"));
            jsonArray.add(designModel);
        }else {
            designModel.put("val", formDataJson.getString("componentModel"));
            jsonArray.add(designModel);
        }
        if("XPSZ".equalsIgnoreCase(formDataJson.getString("wtlx"))||
                "XPZDSY".equalsIgnoreCase(formDataJson.getString("wtlx"))||
                "XPLS".equalsIgnoreCase(formDataJson.getString("wtlx"))||
                "WXBLX".equalsIgnoreCase(formDataJson.getString("wtlx"))||
                "LBJSY".equalsIgnoreCase(formDataJson.getString("wtlx"))){
            bigWtlx.put("val", "新产品导入");
            jsonArray.add(bigWtlx);
        }else {
            bigWtlx.put("val", "分析改进");
            jsonArray.add(bigWtlx);
        }
        processStartCmd.getJsonDataObject().put("vars", jsonArray);
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String wtId = processStartCmd.getBusinessKey();
        String zlgjNumber = toGetZlgjNumber(processStartCmd.getJsonData());
        if (StringUtils.isNotBlank(zlgjNumber)) {
            Map<String, Object> param = new HashMap<>();
            param.put("wtId", wtId);
            param.put("zlgjNumber", zlgjNumber);
            param.put("rounds", "1");
            zlgjWTDao.updateNumber(param);
        }
        return null;
    }

    private String toGetZlgjNumber(String formDataStr) {
        String zlgjNumber = "";
        if (StringUtils.isBlank(formDataStr)) {
            return zlgjNumber;
        }
        JSONObject formDataObj = JSONObject.parseObject(formDataStr);
        String wtlx = formDataObj.getString("wtlx");
        if (StringUtils.isBlank(wtlx)) {
            return zlgjNumber;
        }
        String typeCode = "";
        switch (wtlx) {
            case ZlgjConstant.CNWT:
                typeCode = "CN";
                break;
            case ZlgjConstant.SCWT:
                typeCode = "SC";
                break;
            case ZlgjConstant.HWWT:
                typeCode = "HW";
                break;
            case ZlgjConstant.XPSZ:
                typeCode = "SZ";
                break;
            case ZlgjConstant.XPZDSY:
                typeCode = "ZJ";
                break;
            case ZlgjConstant.XPLS:
                typeCode = "LS";
                break;
            case ZlgjConstant.WXBLX:
                typeCode = "WX";
                break;
            case ZlgjConstant.LBJSY:
                typeCode = "LBJ";
                break;
        }
        String nowYearStart = DateFormatUtil.getNowByString("yyyy");
        // 查询创建人的部门代号
        String CREATE_BY_ = formDataObj.getString("CREATE_BY_");
        if (StringUtils.isBlank(CREATE_BY_)) {
            CREATE_BY_ = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptList = zlgjWTDao.queryMainDeptByUserId(CREATE_BY_);
        String deptCode = deptList.get(0).getString("DEPT_CODE_");
        if (deptList == null || deptList.isEmpty() || StringUtils.isBlank(deptCode)) {
            return zlgjNumber;
        }
        String queryNumberParam = typeCode + "-" + deptCode + "-" + nowYearStart + "-";
        // 查询当前条件下最大的编号
        List<JSONObject> maxNumberList = zlgjWTDao.queryMaxZlgjNumber(queryNumberParam);
        if (maxNumberList == null || maxNumberList.isEmpty()) {
            zlgjNumber = queryNumberParam + "0001";
            return zlgjNumber;
        }

        int existNumber = Integer.parseInt(maxNumberList.get(0).getString("zlgjNumber").split("-", -1)[3]);
        int thisNumber = existNumber + 1;
        DecimalFormat df = new DecimalFormat("0000");
        zlgjNumber = queryNumberParam + df.format(thisNumber);
        return zlgjNumber;
    }

    //任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateZlgjByFormData(processNextCmd);
    }

    //驳回场景cmd中没有表单数据
    private String createOrUpdateZlgjByFormData(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        if (StringUtils.isBlank(formDataJson.getString("wtId"))) {
            /***********定位问题*********/
            if (StringUtils.isBlank(formDataJson.getString("wtlx"))) {
                logger.error("!!!!问题类型为空");
                return null;
            }
            zlgjWTService.createZlgj(formDataJson);
        } else {
            /***********定位问题*********/
            if (StringUtils.isBlank(formDataJson.getString("wtlx"))) {
                logger.error("!!!!问题类型为空");
                return null;
            }
            zlgjWTService.updateZlgj(formDataJson);
            //@lwgkiller:切入NewproductAssemblyKanbanService，传formDataJson处理信息的更新
            newproductAssemblyKanbanService.doSynZlgjToUpdateException(formDataJson);
        }
        return formDataJson.getString("wtId");
    }

    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            // 如果问题改进，则生成质量改进履历
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            boolean ifgj = false;
            if (formDataJson != null && "yes".equalsIgnoreCase(formDataJson.getString("ifgj"))) {
                ifgj = true;
            }
            String wtId = bpmInst.getBusKey();
            if (ifgj) {
                String fileId = "";
                JSONObject gjllJsonAll = new JSONObject();
                Map<String, Object> param = new HashMap<>();
                param.put("wtId", wtId);
                List<JSONObject> gjllJsonList = gjllTDao.queryLlInfoById(param);
                for (JSONObject gjllJson : gjllJsonList) {
                    String wtlx = gjllJson.getString("wtlx");
                    if(!"LBJSY".equalsIgnoreCase(wtlx)){
                        String jiXingName = ZlgjWTService.toGetJixingNameByCode(gjllJson.getString("jiXing"));
                        gjllJsonAll.put("jiXing", jiXingName);
                        gjllJsonAll.put("smallJiXing", gjllJson.getString("smallJiXing"));
                        gjllJsonAll.put("gzlj", gjllJson.getString("gzlj"));
                        gjllJsonAll.put("wtms", gjllJson.getString("wtms"));
                    }else {
                        gjllJsonAll.put("smallJiXing", gjllJson.getString("machineModel"));
                        gjllJsonAll.put("gzlj", gjllJson.getString("componentName"));
                        gjllJsonAll.put("wtms", "零部件试验问题");
                    }
                    gjllJsonAll.put("wtId", gjllJson.getString("wtId"));
                    gjllJsonAll.put("reason", gjllJson.getString("reason"));
                    gjllJsonAll.put("cqcs", gjllJson.getString("cqcs"));
                    gjllJsonAll.put("tzdh", gjllJson.getString("tzdh"));
                    gjllJsonAll.put("yjqhch", gjllJson.getString("yjqhch"));
                    gjllJsonAll.put("zlgjNumber", gjllJson.getString("zlgjNumber"));
                    gjllJsonAll.put("zrrId", gjllJson.getString("zrrId"));
                    gjllJsonAll.put("zrrName", gjllJson.getString("zrrName"));
                    gjllJsonAll.put("ssbmId", gjllJson.getString("ssbmId"));
                    gjllJsonAll.put("ssbmName", gjllJson.getString("ssbmName"));
                    gjllJsonAll.put("CREATE_BY_", gjllJson.getString("zrrId"));
                    gjllJsonAll.put("wtlx", gjllJson.getString("wtlx"));
                    gjllJsonAll.put("lbjgys", gjllJson.getString("lbjgys"));
                    gjllJsonAll.put("qhTime", gjllJson.getString("wcTime"));
                }
                gjllJsonAll.put("gjId", IdUtil.getId());
                gjllJsonAll.put("CREATE_TIME_", new Date());
                gjllTDao.autoCreateLl(gjllJsonAll);
                // 拷贝质量问题的附件
                List<JSONObject> fileJsonList = gjllTDao.queryLlFileInfoById(param);
                String zlgjFilePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
                String gjllFilePathBase = WebAppUtil.getProperty("gjllFilePathBase");
                StringBuilder sourcePathSb = new StringBuilder(zlgjFilePathBase);
                sourcePathSb.append(File.separator).append(wtId).append(File.separator);
                StringBuilder targetPathSb = new StringBuilder(gjllFilePathBase);
                targetPathSb.append(File.separator).append(gjllJsonAll.get("gjId").toString()).append(File.separator);
                for (JSONObject fileJson : fileJsonList) {
                    fileId = IdUtil.getId();
                    fileJson.put("belongId", gjllJsonAll.getString("gjId"));
                    fileJson.put("fileId", fileId);
                    gjllTDao.autoCreateLlFile(fileJson);
                    String[] sourceFileNameAndSuffix =
                            FileOperateUtil.toGetFileNameAndSuffix(fileJson.getString("fileName"));
                    String sourceFile =
                            sourcePathSb.toString() + fileJson.getString("id") + "." + sourceFileNameAndSuffix[1];
                    String targetDir = targetPathSb.toString();
                    String targetFileName = fileId + "." + sourceFileNameAndSuffix[1];
                    FileOperateUtil.copyFileFromSourceToTarget(sourceFile, targetDir, targetFileName);
                }
            }

            // 流程结束发送通知到检验经理及检验技术角色信息
            Map<String, Object> params = new HashMap<>();
            params.put("userId", "检验经理及检验技术");
            List<Map<String, Object>> currentUserRoles = zlgjWTDao.queryJyjlUserRoles(params);
            StringBuilder strUser = new StringBuilder();
            for (int i = 0; i < currentUserRoles.size(); i++) {
                strUser.append(currentUserRoles.get(i).get("USER_ID_")).append(",");
            }
            // 查询解决方案中的通知单号
            JSONArray reasonDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_fanganjj"));
            StringBuilder strBul = new StringBuilder();
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                if (StringUtils.isNotBlank(oneObject.getString("tzdh"))) {
                    strBul.append(oneObject.getString("tzdh")).append(";");
                }
            }
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【质量改进】");
            stringBuilder.append(formDataJson.getString("wtms")).append(":");
            if (StringUtils.isNotBlank(strBul.toString())) {
                stringBuilder.append(strBul.toString());
            } else {
                stringBuilder.append("无通知单号");
            }
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(strUser.deleteCharAt(strUser.length() - 1).toString(), noticeObj);

            // 如果问题改进，则回调外部系统（如果存在）
            if (ifgj) {
                JsonResult callBackOutResult = zlgjWTService.zlgjWtCallBack(wtId);
                logger.info("回调外部系统" + (callBackOutResult.getSuccess() ? "成功" : "失败，原因：" + callBackOutResult.getMessage())
                                + "，wtId=" + wtId);
            }
            //@lwgkiller:切入NewproductAssemblyKanbanService，传formDataJson处理闭环标记
            newproductAssemblyKanbanService.doSynZlgjToClearException(formDataJson);
        }
    }
}
