package com.redxun.drbfm.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTotalDao;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class DrbfmTotalService {
    private Logger logger = LoggerFactory.getLogger(DrbfmTotalService.class);
    @Autowired
    private DrbfmTotalDao drbfmTotalDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;

    public JsonPageResult<?> getTotalList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_total_baseinfo.CREATE_TIME_", "desc");
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // TODO 增加角色过滤的条件
        // addJsmmRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> totalList = drbfmTotalDao.queryTotalList(params);
        for (Map<String, Object> oneData : totalList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(totalList);
        int countTotalListDataList = drbfmTotalDao.countTotalList(params);
        result.setTotal(countTotalListDataList);
        return result;
    }

    /**
     * 需要判断是否有关联生成的单一项目，有的话不允许删除。没有的话，需要将分解的结构表信息一起删除
     * 
     * @param ids
     * @return
     */
    public JsonResult deleteTotal(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (ids == null || ids.length == 0) {
            return result;
        }
        // 判断有无生成的单一项目
        List<String> idList = new ArrayList<>(Arrays.asList(ids));
        Map<String, Object> param = new HashMap<>();
        param.put("totalIds", idList);
        List<Map<String, Object>> singleList = drbfmSingleDao.querySingleList(param);
        Set<String> totalIdSet = new HashSet<>();
        for (Map<String, Object> oneSingle : singleList) {
            totalIdSet.add(oneSingle.get("totalId").toString());
        }
        idList.removeAll(totalIdSet);
        if (idList.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("已创建部件分析项目的总项目不能删除！");
            return result;
        }

        // 删除基本信息表和分解结构表
        drbfmTotalDao.deleteTotalStructByParam(param);
        drbfmTotalDao.deleteTotalBaseInfo(param);

        return result;
    }

    public JSONObject getTotalDetail(String totalId) {
        return drbfmTotalDao.getTotalDetail(totalId);
    }

    public void saveTotal(JsonResult result, JSONObject formJSON, String gridDataStr) {
        String formId = formJSON.getString("id");
        if (StringUtils.isBlank(formId)) {
            // 新增基本信息
            formId = IdUtil.getId();
            formJSON.put("id", formId);
            formJSON.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formJSON.put("CREATE_TIME_", new Date());
            drbfmTotalDao.createTotal(formJSON);
            // 默认创建根节点
            JSONObject rootObj = new JSONObject();
            rootObj.put("id", IdUtil.getId());
            rootObj.put("belongTotalId", formId);
            rootObj.put("structName", formJSON.getString("analyseName"));
            rootObj.put("structNumber", formJSON.getString("jixing"));
            rootObj.put("judgeIsInterface", "否");
            rootObj.put("judgeNeedAnalyse", "是");
            rootObj.put("parentId", "0");
            rootObj.put("pathIds", "0." + rootObj.getString("id") + ".");
            rootObj.put("sn", 1);
            rootObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            rootObj.put("CREATE_TIME_", new Date());
            drbfmTotalDao.createStruct(rootObj);
        } else {
            // 更新基本信息
            drbfmTotalDao.updateTotal(formJSON);
            // 实现结构树的保存
            if (StringUtils.isNotBlank(gridDataStr)) {
                JSONArray gridArray = JSONArray.parseArray(gridDataStr);
                if (gridArray.size() > 0) {
                    genStructsData(gridDataStr, null, formId);
                }
            }
        }
        result.setData(formId);
    }

    // 复制总项及struct
    public void copyTotal(JsonResult result, JSONObject formJSON, String gridDataStr, String newFromId,
        HashMap<String, String> structIdMap) {
        String formId = formJSON.getString("id");
        // 肯定是有formId

        // 复制基本信息
        formJSON.put("id", newFromId);
        formJSON.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formJSON.put("CREATE_TIME_", new Date());
        formJSON.put("femaType", "product");
        drbfmTotalDao.createTotal(formJSON);

        // 实现结构树的保存
        if (StringUtils.isNotBlank(gridDataStr)) {
            JSONArray gridArray = JSONArray.parseArray(gridDataStr);
            if (gridArray.size() > 0) {
                copyStructsData(gridDataStr, null, newFromId, structIdMap);
            }
        }

        result.setData(newFromId);
    }

    /**
     * 查询数据库中当前没有关联单一项目，且需要风险验证的结构，创建单一项目
     *
     * @param result
     * @param totalId
     */
    public void createSingleFlow(JsonResult result, String totalId, String jixing) {
        if (StringUtils.isBlank(totalId)) {
            result.setSuccess(false);
            result.setMessage("总项目ID为空，操作失败！");
            return;
        }
        JSONObject param = new JSONObject();
        param.put("belongTotalId", totalId);
        param.put("scene", "createSingle");
        List<JSONObject> structList = drbfmTotalDao.queryStructByParam(param);
        if (structList == null || structList.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("未找到需创建的部件，操作失败！");
            return;
        }
        createAndStartSingleFlow(result, structList, jixing);
    }

    /**
     * 查询数据库中原有流程，及需要的创建单一项目
     *
     * @param result
     * @param totalId
     */
    public void copySingleFlow(JsonResult result, String totalId, String jixing) {
        if (StringUtils.isBlank(totalId)) {
            result.setSuccess(false);
            result.setMessage("总项目ID为空，操作失败！");
            return;
        }
        JSONObject param = new JSONObject();
        param.put("belongTotalId", totalId);
        param.put("scene", "copy");
        List<JSONObject> structList = drbfmTotalDao.queryStructByParam(param);
        if (structList == null || structList.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("未找到需创建的部件，操作失败！");
            return;
        }
        createAndStartSingleFlow(result, structList, jixing);
    }

    /**
     * copy原有流程，
     *
     * @param result
     * @param newFromId
     * @param structIdMap
     */
    public void copyFlowData(JsonResult result, String newFromId, HashMap<String, String> structIdMap,
        List<JSONObject> structList) {
        if (StringUtils.isBlank(newFromId)) {
            result.setSuccess(false);
            result.setMessage("总项目ID为空，数据复制操作失败！");
            return;
        }

        // 查询 drbfm_single_baseinfo 的Id映射
        HashMap<String, String> baseInfoIdMap = new HashMap<>();

        // 构造所有失效模式的对应映射
        HashMap<String, String> sxmsMap = new HashMap<>();
        List<JSONObject> singleIdList = new ArrayList<>();

        for (JSONObject oneObj : structList) {
            String structId = oneObj.getString("id");
            String oldStructId = structIdMap.get(structId);
            String sffxfx = oneObj.getString("judgeNeedAnalyse");
            if ("否".equalsIgnoreCase(sffxfx)) {
                continue;
            }
            if (StringUtils.isNotBlank(oldStructId)) {
                JSONObject res = drbfmSingleDao.querySingleBaseByStructId(structId);
                JSONObject oldRes = drbfmSingleDao.querySingleBaseByStructId(oldStructId);
                // 查不到原structId对应的baseId就跳过不需要复制
                if (oldRes == null || oldRes.size() == 0) {
                    continue;
                }
                String singleId = res.getString("id");
                String oldSingleId = oldRes.getString("id");
                JSONObject oneSingleId = new JSONObject();
                oneSingleId.put("singleId", singleId);
                oneSingleId.put("oldSingleId", oldSingleId);
                singleIdList.add(oneSingleId);

                baseInfoIdMap.put(res.getString("id"), oldRes.getString("id"));
                // id,singleNumber,structId,CREATE_TIME_,CREATE_BY_替换成新的 或者是将其他字段赋过来

                // 基础表的复制
                oldRes.put("id", res.getString("id"));
                oldRes.put("singleNumber", res.getString("singleNumber"));
                oldRes.put("structId", res.getString("structId"));
                oldRes.put("CREATE_TIME_", res.getString("CREATE_TIME_"));
                oldRes.put("CREATE_BY_", res.getString("CREATE_BY_"));
                oldRes.put("structType", "复制测试吧");
                drbfmSingleDao.updateSingleBase(oldRes);

                // 涉及子表的复制

                // 1.功能特性复制

                String[] ids = {oldSingleId};
                List<String> businessIds = Arrays.asList(ids);
                Map<String, Object> queryparam = new HashMap<>();
                queryparam.put("businessIds", businessIds);

                HashMap<String, String> funcMap = new HashMap<>();
                Map<String, Object> funParam = new HashMap<>();
                funParam.put("belongSingleId", oldSingleId);

                // 单一流程表单查询
                List<JSONObject> funRes = drbfmSingleDao.getFunctionList(funParam);
                // 要将id和belongSingleId替换
                // 需要存功能id前后对应的映射
                if (funRes.size() > 0) {
                    for (JSONObject oneFun : funRes) {
                        String oldId = oneFun.getString("id");
                        String newId = IdUtil.getId();
                        // funcMap需要从old->new
                        funcMap.put(oldId, newId);
                        oneFun.put("id", newId);
                        oneFun.put("belongSingleId", singleId);

                        oneFun.put("relDeptDemandId", null);
                        // 4.创建时间创建人重置，更新时间清空
                        oneFun.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneFun.put("CREATE_TIME_", new Date());
                        oneFun.put("UPDATE_BY_", null);
                        oneFun.put("UPDATE_TIME_", null);
                    }

                    Map<String, Object> paramFunctionList = new HashMap<>();
                    paramFunctionList.put("list", funRes);
                    drbfmSingleDao.insertFunctionArray(paramFunctionList);
                }

                // 2.要求复制

                // 需求分解处理

                HashMap<String, String> reqMap = new HashMap<>();

                List<JSONObject> requestList = drbfmSingleDao.getRequestListByIds(queryparam);
                if (requestList.size() > 0) {
                    for (JSONObject requestLine : requestList) {
                        // 1.主键重新生成
                        String oldId = requestLine.getString("id");
                        String newId = IdUtil.getId();
                        reqMap.put(oldId, newId);
                        requestLine.put("id", newId);

                        // 2.belongSingleId重置
                        requestLine.put("belongSingleId", singleId);
                        // 3.创建时间创建人重置，更新时间清空
                        requestLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        requestLine.put("CREATE_TIME_", new Date());
                        requestLine.put("UPDATE_BY_", null);
                        requestLine.put("UPDATE_TIME_", null);
                        // 4. relFunctionId这个字段要去字典中查
                        String relFunctionId = funcMap.get(requestLine.getString("relFunctionId"));
                        if (StringUtils.isNotBlank(relFunctionId)) {
                            requestLine.put("relFunctionId", relFunctionId);
                        } else {
                            requestLine.put("relFunctionId", null);
                        }

                    }
                    Map<String, Object> paraRequestList = new HashMap<>();
                    paraRequestList.put("list", requestList);
                    drbfmSingleDao.insertRequestArray(paraRequestList);
                }

                // 3.功能失效网复制 这个可能引用其他部件，如果有未映射到的，需要暂时保存，后续再处理

                JSONObject sxmsParam = new JSONObject();
                sxmsParam.put("belongSingleId", oldSingleId);
                List<JSONObject> sxmsList = drbfmSingleDao.querySxmsListBySingleId(sxmsParam);

                if (sxmsList.size() > 0) {
                    for (JSONObject oneSxms : sxmsList) {
                        // 1.主键重新生成
                        String oldId = oneSxms.getString("id");
                        String newId = IdUtil.getId();
                        // 这个也需要从old->new
                        sxmsMap.put(oldId, newId);
                        oneSxms.put("id", newId);
                        oneSxms.put("oldId", oldId);
                        // 2.belongSingleId重置
                        oneSxms.put("partId", singleId);
                        // 3.创建时间创建人重置，更新时间清空
                        oneSxms.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneSxms.put("CREATE_TIME_", new Date());
                        oneSxms.put("UPDATE_BY_", null);
                        oneSxms.put("UPDATE_TIME_", null);
                        // 4. yqId这个字段要去字典中查
                        if (StringUtils.isNotBlank(oneSxms.getString("yqId"))) {
                            String yqId = reqMap.get(oneSxms.getString("yqId"));
                            if (StringUtils.isNotBlank(yqId)) {
                                oneSxms.put("yqId", yqId);
                            } else {
                                oneSxms.put("yqId", null);
                            }
                        } else {
                            oneSxms.put("yqId", null);
                        }
                    }
                    Map<String, Object> paraSxmsList = new HashMap<>();
                    paraSxmsList.put("list", sxmsList);
                    drbfmSingleDao.insertSxmsArray(paraSxmsList);
                }

                //
                // 指标分解处理
                List<JSONObject> quotaList = drbfmSingleDao.getQuotaListByIds(queryparam);
                if (quotaList.size() > 0) {
                    for (JSONObject quotaLine : quotaList) {
                        // 1.主键重新生成
                        quotaLine.put("id", IdUtil.getId());
                        // 2.belongSingleId重置
                        quotaLine.put("belongSingleId", singleId);
                        // 3.创建时间创建人重置，更新时间清空
                        quotaLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        quotaLine.put("CREATE_TIME_", new Date());
                        quotaLine.put("UPDATE_BY_", null);
                        quotaLine.put("UPDATE_TIME_", null);
                        // todo 4.这个relRequestId 需要映射
                        String relRequestId = quotaLine.getString("relRequestId");
                        if (StringUtils.isNotBlank(relRequestId)) {
                            if (StringUtils.isNotBlank(reqMap.get(relRequestId))) {
                                quotaLine.put("relRequestId", reqMap.get(relRequestId));
                            } else {
                                quotaLine.put("relRequestId", null);
                            }
                        } else {
                            quotaLine.put("relRequestId", null);
                        }
                    }
                    Map<String, Object> paraQuotaList = new HashMap<>();
                    paraQuotaList.put("list", quotaList);
                    drbfmSingleDao.insertQuotaArray(paraQuotaList);
                }

            }

        }
        // 在上个循环中将失效模式都复制完成后，再进行失效关系网和风险分析的赋值

        // 4.失效模式网复制
        // 遍历这个map baseInfoIdMap 或者 SingleList
        // todo 需要重新获取一下失效模式的map
        // 构建一个sxms->singleId 的映射

        if (!singleIdList.isEmpty()) {
            // 查询所有复制过来的失效模式，增加到映射中
            HashMap<String, String> sxms2singleIdMap = new HashMap<>();
            JSONObject cpsxmsParam = new JSONObject();
            cpsxmsParam.put("totalId", newFromId);
            List<JSONObject> sxmsMapList = drbfmSingleDao.getCopiedSxmsList(cpsxmsParam);
            for (JSONObject oneSxms : sxmsMapList) {
                String oldId = oneSxms.getString("oldId");
                String newId = oneSxms.getString("id");
                String singleId = oneSxms.getString("partId");
                if (StringUtils.isNotBlank(oldId) && StringUtils.isNotBlank(newId)) {
                    sxmsMap.put(oldId, newId);
                }
                if (StringUtils.isNotBlank(oldId) && StringUtils.isNotBlank(singleId)) {
                    sxms2singleIdMap.put(oldId, singleId);
                }
            }

            // 查询singleId 中所有的relation 这里查了单一项目的所有关系
            /**
             * @mh 这部分比较复杂,每个关系有自己从属单一项目，默认是向下关联， 复制时，根据singleId复制可以复制所有向下关联的关系，
             **/

            for (JSONObject oneSingleId : singleIdList) {
                String singleId = oneSingleId.getString("singleId");
                String oldSingleId = oneSingleId.getString("oldSingleId");
                JSONObject sxmsRelParam = new JSONObject();
                sxmsRelParam.put("belongSingleId", oldSingleId);
                List<JSONObject> sxmsRelList = drbfmSingleDao.querySxmsRelListBySingleId(sxmsRelParam);
                // todo这个用迭代器，遍历，并且将映射不到的删除

                if (sxmsRelList.size() > 0) {
                    Iterator<JSONObject> iterator = sxmsRelList.iterator();

                    while (iterator.hasNext()) {
                        JSONObject oneSxmsRel = iterator.next();
                        String oldId = oneSxmsRel.getString("id");
                        String newId = IdUtil.getId();
                        oneSxmsRel.put("id", newId);

                        // 2.belongSingleId重置
                        oneSxmsRel.put("partId", singleId);
                        // 3.创建时间创建人重置
                        oneSxmsRel.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneSxmsRel.put("CREATE_TIME_", new Date());

                        // 4. baseId和relId这个兩字段要去字典中查
                        if (StringUtils.isNotBlank(oneSxmsRel.getString("baseId"))) {
                            String baseId = sxmsMap.get(oneSxmsRel.getString("baseId"));
                            if (StringUtils.isNotBlank(baseId)) {
                                oneSxmsRel.put("baseId", baseId);
                            } else {
                                // 如果没找到baseId说明是从原单一流程向上关联到其他部件【单一流程】的还使用该baseId即可
                                logger.info("失效关系网未找到原失效模式映射，不复制该条数据" + oldId);
                                iterator.remove();
                                continue;

                            }
                        }
                        if (StringUtils.isNotBlank(oneSxmsRel.getString("relId"))) {
                            String relId = sxmsMap.get(oneSxmsRel.getString("relId"));
                            if (StringUtils.isNotBlank(relId)) {
                                oneSxmsRel.put("relId", relId);
                            } else {
                                // 如果没找到baseId说明是从原单一流程向下关联到其他部件【单一流程】的还使用该relId即可
                                logger.info("失效关系网未找到原失效模式映射，不复制该条数据" + oldId);
                                iterator.remove();
                                continue;
                            }
                        }

                    }
                    Map<String, Object> paraSxmsRelList = new HashMap<>();
                    paraSxmsRelList.put("list", sxmsRelList);
                    drbfmSingleDao.insertSxmsRelArray(paraSxmsRelList);

                }

//                

                // 5.失效风险分析复制
                JSONObject fxfxParam = new JSONObject();
                fxfxParam.put("partId", oldSingleId);
                List<JSONObject> fxfxList = drbfmSingleDao.queryFxpgListByPartId(fxfxParam);
                if (fxfxList.size() > 0) {
                    for (JSONObject oneFxfx : fxfxList) {
                        // 1.主键重新生成
                        String oldId = oneFxfx.getString("id");
                        String newId = IdUtil.getId();
                        oneFxfx.put("id", newId);

                        // 2.belongSingleId重置
                        oneFxfx.put("partId", singleId);
                        // 3.创建时间创建人重置，更新时间清空
                        oneFxfx.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneFxfx.put("CREATE_TIME_", new Date());
                        // 4. sxmsId 和 sxyyId 要重新映射

                        if (StringUtils.isNotBlank(oneFxfx.getString("sxmsId"))) {
                            String sxmsId = sxmsMap.get(oneFxfx.getString("sxmsId"));
                            if (StringUtils.isNotBlank(sxmsId)) {
                                oneFxfx.put("sxmsId", sxmsId);
                            } else {
                                logger.info("失效风险分析未找到原失效模式映射，跳过该条数据");
                            }
                        }
                        if (StringUtils.isNotBlank(oneFxfx.getString("sxyyId"))) {
                            String sxyyId = sxmsMap.get(oneFxfx.getString("sxyyId"));
                            if (StringUtils.isNotBlank(sxyyId)) {
                                oneFxfx.put("sxyyId", sxyyId);
                            } else {
                                logger.info("失效风险分析未找到原失效模式映射，跳过该条数据");
                            }
                        }
                        // 5.所有字段为空的要补null
                    }
                    Map<String, Object> paraFxfxList = new HashMap<>();
                    paraFxfxList.put("list", fxfxList);
                    drbfmSingleDao.batchCreateRiskAnalysis(paraFxfxList);

                }

            }


            for (JSONObject oneSingleId : singleIdList) {
                String singleId = oneSingleId.getString("singleId");
                String oldSingleId = oneSingleId.getString("oldSingleId");
                // 4.2 重新创建向上关联
                // 当前singleId部件如果之前有上级需要关联到此部件需要在此步骤重新创建

                // 查询所有由否转是当前节点向上关联的
                // 下面是为了给先点否再点是的时候创建关联使用的， 平时是冗余操作
                JSONObject upParam = new JSONObject();
                upParam.put("singleId", oldSingleId);
                List<JSONObject> upSxmsRelList = drbfmSingleDao.getUpSxmsRelList(upParam);
                if (upSxmsRelList.size() > 0) {
                    Iterator<JSONObject> iteratorRel = upSxmsRelList.iterator();

                    while (iteratorRel.hasNext()) {
                        JSONObject oneSxmsRel = iteratorRel.next();;
                        String oldBaseId = oneSxmsRel.getString("baseId");
                        String oldRelId = oneSxmsRel.getString("relId");
                        if (StringUtils.isBlank(oldBaseId) || StringUtils.isBlank(oldRelId)) {
                            iteratorRel.remove();
                            continue;
                        }
                        String newBaseId = sxmsMap.get(oldBaseId);
                        String newRelId = sxmsMap.get(oldRelId);

                        if (StringUtils.isBlank(newBaseId) || StringUtils.isBlank(newRelId)) {
                            iteratorRel.remove();
                            continue;
                        }
                        // 在这要查 该关联是否存在，如果存在 就不用再创建了
                        JSONObject checkParam = new JSONObject();
                        checkParam.put("baseId", newBaseId);
                        checkParam.put("relId", newRelId);
                        List<JSONObject> checkSxmsRelList = drbfmSingleDao.querySxmsRelList(checkParam);
                        if (!checkSxmsRelList.isEmpty()) {
                            iteratorRel.remove();
                            continue;
                        }

                        String newId = IdUtil.getId();
                        oneSxmsRel.put("id", newId);

                        // 2.belongSingleId重置
                        oneSxmsRel.put("partId", singleId);

                        String newSingleId = sxms2singleIdMap.get(oldBaseId);
                        if (StringUtils.isNotBlank(newSingleId)) {
                            oneSxmsRel.put("partId", newSingleId);
                        }

                        // 3.创建时间创建人重置
                        oneSxmsRel.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneSxmsRel.put("CREATE_TIME_", new Date());
                        oneSxmsRel.put("baseId", sxmsMap.get(oldBaseId));
                        oneSxmsRel.put("relId", sxmsMap.get(oldRelId));
                    }
                    if (upSxmsRelList.size() > 0) {
                        Map<String, Object> upRelList = new HashMap<>();
                        upRelList.put("list", upSxmsRelList);
                        drbfmSingleDao.insertSxmsRelArray(upRelList);
                    }
                }
            }

        }

    }

    /**
     * 生成需要保存的数据，并新增或者更新
     * 
     * @param parentObj
     */

    public void genStructsData(String gridJson, JSONObject parentObj, String formId) {
        JSONArray jsonArray = JSONArray.parseArray(gridJson);
        if (parentObj != null) {
            parentObj.put("childs", jsonArray.size());
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String id = jsonObj.getString("id");
            JSONObject addOrUpdateObj = null;
            // 是否为创建
            boolean isCreated = false;
            if (StringUtils.isBlank(id)) {
                addOrUpdateObj = new JSONObject();
                addOrUpdateObj.put("id", IdUtil.getId());
                isCreated = true;
                addOrUpdateObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                addOrUpdateObj.put("CREATE_TIME_", new Date());
                addOrUpdateObj.put("belongTotalId", formId);
            } else {
                JSONObject param = new JSONObject();
                param.put("id", id);
                List<JSONObject> queryResults = drbfmTotalDao.queryStructByParam(param);
                if (queryResults != null && !queryResults.isEmpty()) {
                    addOrUpdateObj = queryResults.get(0);
                    addOrUpdateObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    addOrUpdateObj.put("UPDATE_TIME_", new Date());
                } else {
                    addOrUpdateObj = new JSONObject();
                    addOrUpdateObj.put("id", IdUtil.getId());
                    isCreated = true;
                    addOrUpdateObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    addOrUpdateObj.put("CREATE_TIME_", new Date());
                    addOrUpdateObj.put("belongTotalId", formId);
                }
            }
            addOrUpdateObj.put("structNumber", jsonObj.getString("structNumber"));
            addOrUpdateObj.put("structName", jsonObj.getString("structName").replace("\n", ""));
            addOrUpdateObj.put("judgeIsInterface", jsonObj.getString("judgeIsInterface"));
            addOrUpdateObj.put("interfaceAName", jsonObj.getString("interfaceAName"));
            addOrUpdateObj.put("interfaceBName", jsonObj.getString("interfaceBName"));
            addOrUpdateObj.put("judgeNeedAnalyse", jsonObj.getString("judgeNeedAnalyse"));
            addOrUpdateObj.put("analyseUserId", jsonObj.getString("analyseUserId"));
            addOrUpdateObj.put("analyseUserName", jsonObj.getString("analyseUserName"));
            addOrUpdateObj.put("sn", jsonObj.getIntValue("sn"));

            if (parentObj == null) {
                addOrUpdateObj.put("parentId", "0");
                addOrUpdateObj.put("pathIds", "0." + addOrUpdateObj.getString("id") + ".");
            } else {
                addOrUpdateObj.put("parentId", parentObj.getString("id"));
                addOrUpdateObj.put("pathIds", parentObj.getString("pathIds") + addOrUpdateObj.getString("id") + ".");
            }

            String children = jsonObj.getString("children");
            if (StringUtils.isNotBlank(children)) {
                genStructsData(children, addOrUpdateObj, formId);

            } else {
                JSONObject param = new JSONObject();
                param.put("parentId", addOrUpdateObj.getString("id"));
                List<JSONObject> subGroup = drbfmTotalDao.queryStructByParam(param);
                if (subGroup != null && !subGroup.isEmpty()) {
                    genStructsData(JSONObject.toJSONString(subGroup), addOrUpdateObj, formId);
                }
            }
            if (isCreated) {
                createStruct(addOrUpdateObj);
            } else {
                updateStruct(addOrUpdateObj);
            }
        }
    }

    /**
     * 拷贝台账结构
     *
     * @param parentObj
     */

    public void copyStructsData(String gridJson, JSONObject parentObj, String formId,
        HashMap<String, String> structIdMap) {
        JSONArray jsonArray = JSONArray.parseArray(gridJson);
        if (parentObj != null) {
            parentObj.put("childs", jsonArray.size());
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String id = jsonObj.getString("id");
            JSONObject addOrUpdateObj = null;
            // 是否为创建
            boolean isCreated = true;
            addOrUpdateObj = new JSONObject();
            String newId = IdUtil.getId();
            structIdMap.put(newId, id);
            addOrUpdateObj.put("id", newId);
            addOrUpdateObj.put("oldId", id);
            addOrUpdateObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            addOrUpdateObj.put("CREATE_TIME_", new Date());
            addOrUpdateObj.put("belongTotalId", formId);
            addOrUpdateObj.put("structNumber", jsonObj.getString("structNumber"));
            addOrUpdateObj.put("structName", jsonObj.getString("structName"));
            addOrUpdateObj.put("judgeIsInterface", jsonObj.getString("judgeIsInterface"));
            addOrUpdateObj.put("interfaceAName", jsonObj.getString("interfaceAName"));
            addOrUpdateObj.put("interfaceBName", jsonObj.getString("interfaceBName"));
            addOrUpdateObj.put("judgeNeedAnalyse", "否");
            addOrUpdateObj.put("analyseUserId", "");
            addOrUpdateObj.put("analyseUserName", "");
            addOrUpdateObj.put("sn", jsonObj.getIntValue("sn"));

            if (parentObj == null) {
                addOrUpdateObj.put("parentId", "0");
                addOrUpdateObj.put("pathIds", "0." + addOrUpdateObj.getString("id") + ".");
            } else {
                addOrUpdateObj.put("parentId", parentObj.getString("id"));
                addOrUpdateObj.put("pathIds", parentObj.getString("pathIds") + addOrUpdateObj.getString("id") + ".");
            }

            String children = jsonObj.getString("children");
            if (StringUtils.isNotBlank(children)) {
                copyStructsData(children, addOrUpdateObj, formId, structIdMap);

            } else {
                JSONObject param = new JSONObject();
                param.put("parentId", addOrUpdateObj.getString("id"));
                List<JSONObject> subGroup = drbfmTotalDao.queryStructByParam(param);
                if (subGroup != null && !subGroup.isEmpty()) {
                    copyStructsData(JSONObject.toJSONString(subGroup), addOrUpdateObj, formId, structIdMap);
                }
            }
            if (isCreated) {
                createStruct(addOrUpdateObj);
            } else {
                updateStruct(addOrUpdateObj);
            }
        }
    }

    public void createStruct(JSONObject addOrUpdateObj) {
        drbfmTotalDao.createStruct(addOrUpdateObj);
        String parentId = addOrUpdateObj.getString("parentId");
        if (StringUtils.isNotBlank(parentId) && (!"0".equals(parentId))) {
            JSONObject parentObj = null;
            JSONObject param = new JSONObject();
            param.put("id", parentId);
            List<JSONObject> parentResults = drbfmTotalDao.queryStructByParam(param);
            if (parentResults != null && !parentResults.isEmpty()) {
                parentObj = parentResults.get(0);
            } else {
                return;
            }
            int childs = drbfmTotalDao.countStructByParentId(parentId);
            parentObj.put("childs", childs);
            drbfmTotalDao.updateStructChilds(parentObj);
        }
    }

    public void updateStruct(JSONObject addOrUpdateObj) {
        drbfmTotalDao.updateStruct(addOrUpdateObj);
    }

    public JsonResult createAndStartSingleFlow(JsonResult result, List<JSONObject> structList, String jixing) {
        IUser user = userService.getByUserId(ContextUtil.getCurrentUserId());
        ContextUtil.setCurUser(user);
        // 查找solution
        BpmSolution bpmSol = bpmSolutionManager.getByKey("drbfmSingle", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        }
        for (JSONObject oneStruct : structList) {
            // 加上处理的消息提示
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                JSONObject formData = new JSONObject();
                formData.put("structId", oneStruct.getString("id"));
                JSONArray varsArr = new JSONArray();
                JSONObject jixingObj = new JSONObject();
                jixingObj.put("key", "jixing");
                jixingObj.put("val", jixing);
                varsArr.add(jixingObj);
                JSONObject structNameObj = new JSONObject();
                structNameObj.put("key", "structName");
                structNameObj.put("val", oneStruct.getString("structName"));
                varsArr.add(structNameObj);
                formData.put("vars", varsArr);
                formData.put("bos", new JSONArray());
                String singleNumber =
                    "S-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
                formData.put("singleNumber", singleNumber);
                startCmd.setJsonData(formData.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
                if (handleMessage.getErrorMsges().size() == 0) {
                    handleMessage.addErrorMsg(ex.getMessage());
                }
            } finally {
                // 在处理过程中，是否有错误的消息抛出
                if (handleMessage.getErrorMsges().size() > 0) {
                    result.setSuccess(false);
                    result.setMessage("启动流程失败!");
                    result.setData(handleMessage.getErrorMsges());
                } else {
                    result.setSuccess(true);
                    result.setMessage("部件分析项目创建成功，请在相应页面查看！");
                }
                ProcessHandleHelper.clearProcessCmd();
                ProcessHandleHelper.clearProcessMessage();
            }
        }
        return result;
    }

    /**
     * 删除这个节点及其子节点，更新这个节点的childs数量
     * 
     * @param delObj
     */
    public void delAndUpdateParentChilds(JSONObject delObj) {
        if (delObj == null) {
            return;
        }
        // 直接父节点的childs数量减1
        String parentId = delObj.getString("parentId");
        if (StringUtils.isNotBlank(parentId) && !"0".equals(parentId)) {
            JSONObject param = new JSONObject();
            param.put("parentId", parentId);
            drbfmTotalDao.updateParentChildsMinus1(param);
        }
        // 删除这个节点本身及所有子节点
        JSONObject param = new JSONObject();
        param.put("pathIds", delObj.getString("id"));
        drbfmTotalDao.deleteTotalStructByParam(param);
    }

    public List<JSONObject> yanzhongxingPage(HttpServletRequest request) {
        List<JSONObject> list = drbfmTotalDao.getYanzhongxingPage();
        return list;
    }

    public List<JSONObject> chuangxinxingPage(HttpServletRequest request) {
        List<JSONObject> list = drbfmTotalDao.getChuangxinxingPage();
        return list;
    }

    public static void main(String[] args) {
        for (int index = 0; index < 13; index++) {
            System.out
                .println("S-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100));
        }
    }
}
