var isBianzhi="";
var isBianzhiReason="";
var isSfGz="";
var isSffgsp="";
var isFayz = "";
$(function () {
    initForm();
    //明细入口
    if (action == 'detail') {
        detailActionProcess();
    } else if(action=='task') {
        taskActionProcess();
    }else if(action=='edit'){
        editProcess();
    }
});

//保存草稿
function saveZlgj(e) {
    var smallTypeName=$.trim(mini.get("smallTypeId").getText());
    if (!smallTypeName) {
        mini.alert("请选择物料分类");
        return;
    }
    window.parent.saveDraft(e);
}



//流程中暂存信息（如编制阶段）
function saveZlgjInProcess() {
    var smallTypeName=$.trim(mini.get("smallTypeId").getText());
    if (!smallTypeName) {
        mini.alert("请选择物料分类");
        return;
    }
   var formData = getData();
    var json =mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/gyswt/saveZlgj.do',
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败，"+data.message;
                }
                mini.alert(message,"提示",function () {
                    window.location.reload();
                });
            }
        }
    });
}


//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formZlgj");
    if (formData.SUB_zlgj_fanganjj) {
        delete formData.SUB_zlgj_fanganjj;
    }
    if (formData.SUB_zlgj_fanganyz) {
        delete formData.SUB_zlgj_fanganyz;
    }
    if (formData.SUB_zlgj_linshicuoshi) {
        delete formData.SUB_zlgj_linshicuoshi;
    }
    if (formData.SUB_zlgj_reasonInfo) {
        delete formData.SUB_zlgj_reasonInfo;
    }
    if (formData.SUB_zlgj_yanzhengInfo) {
        delete formData.SUB_zlgj_yanzhengInfo;
    }
    formData.changeReasonData=grid_zlgj_reasonInfo.getChanges();
    formData.changeLscsData=grid_zlgj_linshicuoshi.getChanges();
    formData.changeGbyyData=grid_zlgj_yanzhengInfo.getChanges();
    formData.changeFayzData=grid_zlgj_fanganyz.getChanges();
    formData.changeJjfaData=grid_zlgj_fanganjj.getChanges();

    return formData;
}


function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function detailActionProcess() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    $("#reasonButtons").hide();
    $("#lscsButtons").hide();
    $("#gbyyButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    if(action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }
}
function editProcess() {
    mini.get("sfgz").setEnabled(false);
    mini.get("sffgsp").setEnabled(false);
    mini.get("gjxg").setEnabled(false);
    mini.get("gzr").setEnabled(false);
    mini.get("sfyx").setEnabled(false);

    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    $("#reasonButtons").hide();
    $("#lscsButtons").hide();
    $("#gbyyButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();

}

function toggleFieldSet(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}


function addZlgj_reasonInfoRow() {
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    if(!wtId) {
        mini.alert('请先保存草稿');
        return;
    }
    var row={};
    grid_zlgj_reasonInfo.addRow(row);
}

//移除
function removeZlgj_reasonInfoRow() {
    var selecteds = grid_zlgj_reasonInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_reasonInfo.removeRows(deleteArr);
}

function addCuoshi() {
    if(grid_zlgj_reasonInfo.getChanges().length > 0){
        mini.alert("原因数据有变动，暂时不能编辑，请先点击'暂存信息'保存数据");
        return;
    }
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if(!wtId) {
        mini.alert('请先保存草稿,获取主表id');
        return;
    }
    if(reasonData.length ==0){
        mini.alert('请先添加可能原因并保存');
        return
    }
    var row={};
    grid_zlgj_linshicuoshi.addRow(row);
}

function delCuoshi() {
    var selecteds = grid_zlgj_linshicuoshi.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_linshicuoshi.removeRows(deleteArr);
}

function addZlgj_yanzhengInfoRow() {
    if(grid_zlgj_reasonInfo.getChanges().length > 0){
        mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
        return;
    }
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if(!wtId) {
        mini.alert('请先保存草稿');
        return;
    }
    if(reasonData.length ==0){
        mini.alert('请先添加“问题可能原因”并点击“暂存信息”进行保存！');
        return
    }
    var row={};
    grid_zlgj_yanzhengInfo.addRow(row);
}

function removeZlgj_yanzhengInfoRow() {
    var selecteds = grid_zlgj_yanzhengInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_yanzhengInfo.removeRows(deleteArr);
}

function addyzFangan() {
    if(grid_zlgj_reasonInfo.getChanges().length > 0){
        mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
        return;
    }
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if(!wtId) {
        mini.alert('请先保存草稿');
        return;
    }
    if(reasonData.length ==0){
        mini.alert('请先添加“问题可能原因”并点击“暂存信息”进行保存！');
        return
    }
    var row={};
    grid_zlgj_fanganyz.addRow(row);
}

function delyzFangan() {
    var selecteds = grid_zlgj_fanganyz.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_fanganyz.removeRows(deleteArr);
}
function addjjFangan() {
    if(grid_zlgj_reasonInfo.getChanges().length > 0){
        mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
        return;
    }
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if(!wtId) {
        mini.alert('请先保存草稿');
        return;
    }
    if(reasonData.length ==0){
        mini.alert('请先添加“问题可能原因”并点击“暂存信息”进行保存！');
        return
    }
    var row={};
    grid_zlgj_fanganjj.addRow(row);
}

function deljjFangan() {
    var selecteds = grid_zlgj_fanganjj.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_fanganjj.removeRows(deleteArr);
}

//附件
function addPicFile() {
    var fjlx="gztp";
    var wtId=mini.get("wtId").getValue();
    if(!wtId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    var canOperateFile = false;
    if (action=='edit' ||isBianzhi == 'yes') {
        canOperateFile = true;
    }
    mini.open({
        title: "上传图片",
        url: jsUseCtxPath + "/zlgjNPI/core/gyswt/zlgjFileWindow.do?wtId=" + wtId + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
        });
}
//附件
function addGysfxFile(faId,wtId) {
    if(!faId) {
        mini.alert('请先点击“暂存信息”保存本条永久解决方案后上传！');
        return;
    }
    if(!wtId) {
        mini.alert('请先点击“暂存信息”保存申请单！');
        return;
    }
    var canOperateFile = false;
    if (isBianzhiReason == 'yes') {
        canOperateFile = true;
    }
    mini.open({
        title: "上传附件",
        url: jsUseCtxPath + "/zlgjNPI/core/gyswt/zlgjFileWindow.do?wtId=" + wtId+ "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&faId="+faId,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isBianzhi') {
            isBianzhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isBianzhiReason'){
            isBianzhiReason = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isSfGz'){
            isSfGz = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isSffgsp'){
            isSffgsp = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isFayz'){
            isFayz = nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi == 'yes'){
        editProcess();
    }else if(isBianzhiReason =='yes'){
        formZlgj.setEnabled(false);
    }else if(isSfGz == 'yes'){
        detailActionProcess();
        mini.get("sfgz").setEnabled(true);
    }else if(isSffgsp =='yes'){
        detailActionProcess();
        mini.get("sffgsp").setEnabled(true);
    }else if (isFayz =='yes'){
        detailActionProcess();
        mini.get("gjxg").setEnabled(true);
        mini.get("gzr").setEnabled(true);
        mini.get("sfyx").setEnabled(true);
    } else{
        detailActionProcess();
    }
}

function initForm() {
    $.ajaxSettings.async = false;
    mini.get("smallTypeId").setData(smallTypeList);
    if (wtId){
        var url = jsUseCtxPath + "/zlgjNPI/core/gyswt/getZlgjDetail.do";
        $.post(
            url,
            {
                wtId: wtId,
                type: type
            },
            function (json) {
                formZlgj.setData(json.baseInfo);
                grid_zlgj_reasonInfo.setData(json.reason);
                grid_zlgj_linshicuoshi.setData(json.lscs);
                grid_zlgj_yanzhengInfo.setData(json.gbyy);
                grid_zlgj_fanganyz.setData(json.fayz);
                grid_zlgj_fanganjj.setData(json.jjfa);
            });
    }
    $.ajaxSettings.async = true;

}

//启动流程
function startZlgjProcess(e) {
    var formValid = startProcessValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//提交流程时数据是否有效
function startProcessValid() {
    var smallTypeId=$.trim(mini.get("smallTypeId").getValue());
    if (!smallTypeId) {
        return {"result": false, "message": "请选择物料分类"};
    }
    var bllbj=$.trim(mini.get("bllbj").getValue());
    if(!bllbj) {
        return {"result": false, "message": "请填写不良零部件"};
    }
    var lbjThXh=$.trim(mini.get("lbjThXh").getValue());
    if(!lbjThXh) {
        return {"result": false, "message": "请填写零部件图号/型号"};
    }
    var lbjgys=$.trim(mini.get("lbjgys").getValue());
    if(!lbjgys) {
        return {"result": false, "message": "请填写零部件供应商"};
    }
    var blsl=$.trim(mini.get("blsl").getValue());
    if(!blsl) {
        return {"result": false, "message": "请填写不良数量"};
    }
    var fsqy=$.trim(mini.get("fsqy").getValue());
    if (!fsqy) {
        return {"result": false, "message": "请选择问题发生区域"};
    }
    var wtms=$.trim(mini.get("wtms").getValue());
    if(!wtms) {
        return {"result": false, "message": "请填写问题详细描述"};
    }
    var xcczfa=$.trim(mini.get("xcczfa").getValue());
    if(!xcczfa) {
        return {"result": false, "message": "请选择现场处置方法"};
    }
    var defaultResult={"result": true};
    var wtId=mini.get("wtId").getValue();
    //检查有没有不良图片
    $.ajax({
        url: jsUseCtxPath+"/zlgjNPI/core/gyswt/files.do?wtId="+wtId +"&faId=",
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data || data.length==0) {
                defaultResult.result=false;
                defaultResult.message="请上传不良图片！";
            }
        }
    });
    return defaultResult;
}

//流程中的审批或者下一步
function zlgjApprove() {
    if(isBianzhi == 'yes'){
        var formValid = startProcessValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }else if(isBianzhiReason =='yes'){
    //    检查5个子表是否有数据，有数据的每一行必填项
        var checkResult=checkReasons(grid_zlgj_reasonInfo.getData());
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        var checkResult=checkLscss(grid_zlgj_linshicuoshi.getData());
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        var checkResult=checkGbyys(grid_zlgj_yanzhengInfo.getData());
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        var checkResult=checkFayzs(grid_zlgj_fanganyz.getData());
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        var checkResult=checkJjfas(grid_zlgj_fanganjj.getData());
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
    }else if(isSfGz == 'yes'){
        var sfgz = mini.get("sfgz").getValue();
        if(!sfgz) {
            mini.alert("请选择供应商有无故障！");
            return;
        }
    }else if(isSffgsp =='yes'){
        var sffgsp = mini.get("sffgsp").getValue();
        if(!sffgsp) {
            mini.alert("请选择是否分管领导审批！");
            return;
        }
    }else if (isFayz =='yes'){
        var gjxg = mini.get("gjxg").getValue();
        if(!gjxg) {
            mini.alert("请填写改进效果验证！");
            return;
        }
        var gzr = mini.get("gzr").getValue();
        if(!gzr) {
            mini.alert("请填写跟踪人！");
            return;
        }
        var sfyx = mini.get("sfyx").getValue();
        if(!sfyx) {
            mini.alert("请选择是否有效！");
            return;
        }
    }

    //检查通过
    window.parent.approve();
}

function checkJjfas(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加“永久解决方案”，并点击“暂存信息”！'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: '请选择“永久解决方案”中的“原因”字段！'};
        }
        if (!row.cqcs) {
            return {result: false, message: '请填写“永久解决方案”中的“长期措施”字段！'};
        }
        if (!row.ddpch) {
            return {result: false, message: '请填写“永久解决方案”中的“断点批次号”字段！'};
        }
        if (!row.wcTime) {
            return {result: false, message: '请填写“永久解决方案”中的“实际完成时间”字段！'};
        }
    }
    return {result: true};
}

function checkFayzs(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加“方案验证”，并点击“暂存信息”！'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: '请选择“方案验证”中的“原因”字段！'};
        }
        if (!row.gjfa) {
            return {result: false, message: '请填写“方案验证”中的“改进方案”字段！'};
        }
    }
    return {result: true};
}

function checkGbyys(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加“根本原因”，并点击“暂存信息”！'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: '请选择“根本原因”中的“原因”字段！'};
        }
        if (!row.jcjg) {
            return {result: false, message: '请填写“根本原因”中的“检测结果”字段！'};
        }
    }
    return {result: true};
}

function checkLscss(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加“临时措施”，并点击“暂存信息”！'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: '请选择“临时措施”中的“原因”字段！'};
        }
        if (!row.dcfa) {
            return {result: false, message: '请填写“临时措施”中的“对策方案”字段！'};
        }
        if (!row.ddpc) {
            return {result: false, message: '请填写“临时措施”中的“断点批次”字段！'};
        }
    }
    return {result: true};
}

function checkReasons(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加“问题可能原因”，并点击“暂存信息”！'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.reason) {
            return {result: false, message: '请填写“问题可能原因”中的“原因”字段！'};
        }
    }
    return {result: true};
}

