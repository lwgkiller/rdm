package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ExportPartsAtlasDao {
    List<JSONObject> queryDemandList(JSONObject param);

    List<JSONObject> queryDemandListByDetailNo(JSONObject param);

    List<JSONObject> queryAllDemandInfos();

    List<JSONObject> queryDemandDetailInfos(JSONObject param);

    List<JSONObject> queryAllMachineTasks(JSONObject param);

    int queryDemandListTotal(JSONObject param);

    void insertDemand(JSONObject param);

    void updateDemand(JSONObject param);

    void insertDemandDetail(JSONObject param);

    void batchInsertDemandDetails(List<JSONObject> params);

    void batchInsertDispatchDetails(List<JSONObject> params);

    void insertDetailConfig(JSONObject param);

    void batchInsertDetailConfigs(List<JSONObject> params);

    List<JSONObject> queryDetailConfigs(JSONObject param);

    void batchInsertMachineTasks(List<JSONObject> params);

    JSONObject getDemandInfoById(String id);

    JSONObject getDemandInfoByNo(String no);

    List<JSONObject> getDemandDetailList(String demandId);

    List<JSONObject> getMachineList(JSONObject param);

    List<JSONObject> queryAtlasTaskList(JSONObject param);

    int countAtlasTaskList(JSONObject param);

    void batchInsertTaskStatusHis(List<JSONObject> params);

    List<JSONObject> queryStatusHisList(JSONObject param);

    void updateTaskStatus(JSONObject param);

    void updateTaskStatusApi(JSONObject param);

    // 机型制作相关
    List<JSONObject> queryModelMadeList(JSONObject param);

    int countQueryModelMadeList(JSONObject param);

    // 机型待制作相关
    List<JSONObject> queryModelMadeWaitList(JSONObject param);

    int countQueryModelMadeWaitList(JSONObject param);

    JSONObject getModelMadeDetail(String id);

    void insertModelMade(JSONObject param);

    void updateModelMade(JSONObject param);

    void deleteModelMade(JSONObject param);

    List<JSONObject> queryDispatchList(JSONObject param);

    int queryDispatchListTotal(JSONObject param);

    List<JSONObject> getDispatchDetailList(String dispatchId);

    JSONObject getDispatchInfoById(String id);

    void insertDispatch(JSONObject param);

    void insertDispatchApi(JSONObject param);

    void updateDispatch(JSONObject param);

    void updateDispatchApi(JSONObject param);

    void insertDispatchDetail(JSONObject param);

    void updateDispatchDetail(JSONObject param);

    void updateDispatchDetailApi(JSONObject param);

    void updateDispatchDetailConfig(JSONObject param);

    void delConfigDetail(JSONObject param);

    JSONObject getDispatchDetailById(String id);

    List<JSONObject> getAllDemandDetailList(JSONObject param);

    int countAllDemandDetailList(JSONObject param);

    void delDispatchDetail(JSONObject param);

    void delDispatchDetailByDispatchId(JSONObject param);

    void updateMachineTaskByDispatch(JSONObject param);

    List<JSONObject> queryAbnormalList(JSONObject param);

    int countAbnormalList(JSONObject param);

    void insertTaskAbnormal(JSONObject param);

    void delTaskAbnormal(JSONObject param);

    void delStatusHis(JSONObject param);

    void updateTaskAbnormal(JSONObject param);

    void updateModelMadeNum(JSONObject param);

    void insertChangeNotice(JSONObject param);

    void updateDemandDetail(JSONObject param);

    void updateChangeNoticeConfirm(JSONObject param);

    List<JSONObject> queryChangeNoticeList(JSONObject param);

    int queryChangeNoticeTotal(JSONObject param);

    void updateTask2Zf(JSONObject param);

    void updateTask2UnBind(JSONObject param);

    List<JSONObject> queryDispatchDetailsByDetailNo(JSONObject param);

    void updateReDispatchTimeApi(JSONObject param);

    List<JSONObject> exportDemandMessage(Map<String, Object> param);

    Integer batchAtlasDispatchTime(List<JSONObject> addList);

    JSONObject getCarIsExist(String machineCode);

    /**
     * @lwgkiller:以下成品库功能相关
     */
    // ..
    List<JSONObject> queryDispatchFPWList(JSONObject param);

    // ..
    int queryDispatchFPWListTotal(JSONObject param);

    // ..
    void deleteDispatchFPW(JSONObject param);

    // ..
    void insertDispatchFPW(JSONObject formData);

    // ..
    JSONObject getDispatchFWPDetailById(String id);

    // ..
    void updateMachineTaskByDispatchFWP(JSONObject formData);

    // ..
    void insertMachineTaskByDispatchFWP(JSONObject formData);

    // ..
    void batchUpdateMachineTaskWhichDispatchFWPCreate(List<JSONObject> params);

    void delDemandDetail(JSONObject param);

    // @mh 2023年3月31日11:45:16 用五个条件从成品发运库匹配操保手册发运/需求申请
    List<JSONObject> queryManualStatusList(JSONObject param);

    // @mh 2023年4月14日16:44:26 专门用于更新操保手册状态描述一个字段
    void updateStatusDesc(JSONObject param);

    /**
     * @mh:以下内容为机型制作子表相关
     **/


    // 机型制做_变动关系梳理
    void insertChangeRel(JSONObject param);

    void updateChangeRel(JSONObject param);

    void deleteChangeRel(JSONObject param);

    List<JSONObject> queryChangeRelList(JSONObject param);

    //..
    void batchInsertChangeRel(List<JSONObject> params);

    //机型制做_外购件图册资料
    void insertWgjtczl(JSONObject param);

    void updateWgjtczl(JSONObject param);

    void deleteWgjtczl(JSONObject param);

    List<JSONObject> queryWgjtczlList(JSONObject param);

    //..
    void batchInsertWgjtczl(List<JSONObject> params);

    //机型制做_维修专用件

    void insertWxzyj(JSONObject param);

    void updateWxzyj(JSONObject param);

    void deleteWxzyj(JSONObject param);

    List<JSONObject> queryWxzyjList(JSONObject param);

    //..
    void batchInsertWxzyj(List<JSONObject> params);

    //机型制做_工艺合件明细

    void insertGyhjmx(JSONObject param);

    void updateGyhjmx(JSONObject param);

    void deleteGyhjmx(JSONObject param);

    List<JSONObject> queryGyhjmxList(JSONObject param);

    //..
    void batchInsertGyhjmx(List<JSONObject> params);

}
