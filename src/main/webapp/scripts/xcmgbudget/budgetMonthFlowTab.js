var originalMonth="";
$(function () {
    initForm();
    if (action == 'detail' || action == 'update' || action == 'baoxiao') {
        //明细入口,不允许编辑表单任何信息
        detailActionProcess();
        initBasic();
        changeProjectTrInit();
    }
     else if (action == 'task') {
        formProject.setEnabled(false);
        //任务办理入口，填单页面同edit，其他页面根据节点变量处理
        if (nodeVars.stageName && nodeVars.stageName == 'bianzhi') {
            initBasic();
            initStatueTr();
        }
        if (nodeVars.stageName && nodeVars.stageName == 'shenhe') {
            initBasic();
            initStatueTr();
        }
        taskActionProcess();
    } else if (action == 'edit') {
        initBasic();
        initStatueTr();
        if(isysManager && (applyObj.CREATE_BY_ != currentUserId)){
            formProject.setEnabled(false);
        }
    }
});

function initBasic(){
    mini.get("flowDeptId").setText(applyObj.deptName);
    mini.get("flowDeptId").setValue(applyObj.deptId);
    mini.get("flowYearMonth").setText(applyObj.yearMonth);
    mini.get("userId").setText(applyObj.userName);
    mini.get("userId").setValue(applyObj.CREATE_BY_);
}

function initStatueTr(){
    if (applyObj.budgetType == 'xml'){
        mini.get("budgetType").setText('项目类预算');
    }
    if (applyObj.budgetType == 'fxml'){
        mini.get("budgetType").setText('非项目类预算');
    }
    changeProjectTrInit();
}

function initForm() {
    //初始化表单
    mini.get("flowDeptId").setEnabled(false);
    mini.get("userId").setEnabled(false);
    if(applyObj) {
        formProject.setData(applyObj);
        originalMonth=applyObj.flowYearMonth;
    }
    getRelatedMessage();
    //初始化tab页签
    for(var i=0;i<bigTypeArray.length;i++) {
        var oneBigType=bigTypeArray[i];
        var oneTab={refreshOnClick:true,title:oneBigType.bigTypeName,name:oneBigType.type+'_'+oneBigType.bigTypeId};
        systemTab.addTab(oneTab);
    }
    //    查询第一个tab的数据
    if (bigTypeArray && bigTypeArray.length>0) {
        systemTab.activeTab(systemTab.getTab(0));
    }
}


function detailActionProcess() {
    $("#detailToolBar").show();
    if (status != 'DRAFTED' && status != '') {
        $("#processInfo").show();
    }
    formProject.setEnabled(false);
}

function processInfo() {
    var instId = $("#instId").val();
    var url = ctxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function taskActionProcess() {
    //获取上一环节的结果和处理人
    var preNodeHandler = "---";
    var preNodeResult = '<span>---</span>';
    if (window.parent.actInstId) {
        $.ajax({
            url: ctxPath + '/xcmgProjectManager/core/xcmgProject/getPreHandleResult.do?actInstId=' + window.parent.actInstId,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.handler) {
                        preNodeHandler = data.handler;
                    }
                    if (data.resultText) {
                        preNodeResult = getPreHandleResult(data.result, data.resultText);
                    }
                }
            }
        });
    }
    $("#preNodeHandler", window.parent.document).html(preNodeHandler);
    $("#preNodeResult", window.parent.document).html(preNodeResult);
}

function getPreHandleResult(handleResult, handleResultText) {
    var arr = [{'key': 'BACK', 'value': handleResultText, 'css': 'red'},
        {'key': 'BACK_TO_STARTOR', 'value': handleResultText, 'css': 'red'},
        {'key': 'BACK_SPEC', 'value': handleResultText, 'css': 'red'},
        {'key': 'AGREE', 'value': handleResultText, 'css': 'green'}
    ];

    return $.formatItemValue(arr, handleResult);
}


//保存草稿
function saveBudgetMonthFlowDraft(e) {
    var saveDraftValidResult = saveBudgetMonthDraftValid();
    if (!saveDraftValidResult.result) {
        mini.alert(saveDraftValidResult.message);
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startBudgetMonthFlowProcess(e) {
    // 一次判断，根据日期判断是否能提交（日）
    var date = new Date();
    var day = date.getDate();
    if (!(1 <=day && day < 19)){
        mini.alert("仅当前月份19号之前可以提报下月预算！");
        return;
    }
    //二次判断，判断是否为当前年月（年月）
    var flowYearMonth = mini.get('flowYearMonth').getText();
    var data = new Date()
    var year = data.getFullYear();
    //当月为+1，下个月为+2
    var mon = data.getMonth() + 2;
    //当月份为12时，年+1
    if (mon == 13) {
        year = year + 1;
    }
    //超过12的月份折合
    if (parseInt(mon) > parseInt(12)){
        mon = mon - 12;
    }
    //小于10的月份格式化
    if (parseInt(0) < parseInt(mon) && parseInt(mon) < parseInt(10)){
        mon = '0' + mon;
    }
    //年月拼合
    var yearMonthC = year + '-' + mon;
    //年月校验
    if (flowYearMonth != yearMonthC){
        mini.alert("申报年份或月份不正确，当月只能提报下月预算！");
        return;
    }
    var formValid = saveBudgetMonthApplicationValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//保存草稿时检查
function saveBudgetMonthDraftValid() {
    var flowYearMonth = mini.get('flowYearMonth').getText();
    if (!flowYearMonth) {
        return {"result": false, "message": "请选择预算申报月份！"};
    }
    //检查是否已存在流程
    var resultData={"result": true};
    return resultData;
}

//提交流程时检查
function saveBudgetMonthApplicationValid() {
    var flowYearMonth = mini.get('flowYearMonth').getText();
    var budgetType = mini.get('budgetType').getValue();
    var budgetId = mini.get('budgetId').getValue();
    if (!flowYearMonth) {
        return {"result": false, "message": "请选择预算申报月份！"};
    }
    if (!budgetType) {
        return {"result": false, "message": "请选择项目类型！"};
    }
    if(budgetType == "xml"){
        var projectId = mini.get('projectId').getValue();
        if (!projectId){
            return {"result": false, "message": "请选择关联项目！"};
        }
        var respId = mini.get('respId').getValue();
        if (!respId){
            return {"result": false, "message": "关联项目无项目负责人！"};
        }
        // var cwddh = mini.get('cwddh').getValue();
        // if (!cwddh){
        //     return {"result": false, "message": "关联项目无财务订单号！"};
        // }
    }
    var resultData={"result": true};
    //资金预算和费用预算检查
    $.ajax({
        url: ctxPath + '/xcmgBudget/core/budgetMonth/checkBudgetMoney.do?budgetType='+budgetType+"&budgetId="+budgetId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data.success == false) {
                resultData = {"result": false, "message": data.message};
            }
        }
    });
    return resultData;
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formProject");
    formData.yearMonth = mini.get("flowYearMonth").getText();
    formData.deptId = mini.get("flowDeptId").getValue();
    formData.deptName = mini.get("flowDeptId").getText();
    formData.projectId = mini.get("projectId").getValue();
    formData.budgetType = mini.get("budgetType").getValue();
    return formData;
}

//流程任务办理时，审批或者下一步按钮的处理
function budgetMonthApprove() {
    var formValid = saveBudgetMonthDraftValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查通过
    window.parent.approve();
}
//添加关联项目信息展开窗口
function addRelatedProject(){
    projectWindow.show();
    searchProcessData();
}
//关闭关联项目选址页面
function closeProjectWindow(){
    projectWindow.hide();
    //刷新查询
    getRelatedMessage();
}

function cleanProcessData() {
    mini.get("relatedProjectName").setValue();
    mini.get("projectNumber").setValue();
    searchProcessData();
}

//关联项目查询
function searchProcessData() {
    var projectName=mini.get("relatedProjectName").getValue();
    var number=mini.get("projectNumber").getValue();
    var paramArray = [{name: "projectName", value: projectName},{name: "number", value: number}];
    var data = {};
    data.filter = mini.encode(paramArray);
    projectListGrid.load(data);
}
//关联项目写入
function choseRelatedProject(e){
    var row = projectListGrid.getSelected();
    mini.get("projectId").setValue(row.projectId);
    saveProjectRelated();
    closeProjectWindow();
}

//初始化获取展示关联项目信息
function getRelatedMessage(){
    var projectId = mini.get("projectId").getValue();
    if (projectId == null || projectId ==""){
        return ;
    }
    $.ajax({
        url: ctxPath + '/xcmgBudget/core/budgetMonth/getProjectListById.do?projectId='+projectId,
        contentType: 'application/json',
        type:'get',
        success: function (result) {
            if (!result.success) {
                mini.alert(result.message);
            } else {
                var data = result.data[0];
                mini.get("projectName").setValue(data.projectName);
                mini.get("projectName").setText(data.projectName);
                mini.get("respId").setValue(data.respMan);
                mini.get("cwddh").setValue(data.cwddh);
                mini.get("gbcwddh").setValue(data.gbcwddh);
                activeChanged();
            }
        }
    });
}

//保存项目相关信息（更新项目关联号）
function saveProjectRelated(){
    var projectId = mini.get("projectId").getValue();
    var budgetId = mini.get("budgetId").getValue();
    $.ajax({
        url: ctxPath + '/xcmgBudget/core/budgetMonthFlow/updateMonthProject.do?projectId='+projectId+"&budgetId="+budgetId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {

        }
    });
}

function changeProjectTr(){
    var budgetId = mini.get("budgetId").getValue();
    if (budgetId == null || budgetId == ''){
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        mini.get("budgetType").setValue();
        mini.get("budgetType").setText();
        return ;
    }
    //记录改变后的项目类型
    var budgetType = mini.get("budgetType").getValue();
    $.ajax({
        url: ctxPath + '/xcmgBudget/core/budgetMonthFlow/updateMonthProjectType.do?budgetId='+budgetId+"&budgetType="+budgetType,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {

        }
    });
    //项目类型发生改变，删除全部项目相关的财务数据
    $.ajax({
        url: ctxPath + '/xcmgBudget/core/budgetMonthFlow/deleteBudgetMonth.do?budgetId='+budgetId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {

        }
    });

    changeProjectTrInit();
    mini.get("projectName").setValue();
    mini.get("projectName").setText();
    mini.get("respId").setValue();
    mini.get("respId").setText();
    mini.get("cwddh").setText();
    mini.get("cwddh").setValue();
    mini.get("gbcwddh").setText();
    mini.get("gbcwddh").setValue();

}

function changeProjectTrInit(){
    var flag = mini.get("budgetType").getValue();
    if (flag == 'xml'){
        $("#projectTr").attr("style","display:''")
    }
    if (flag == 'fxml'){
        $("#projectTr").attr("style","display:none;")
    }

    activeChanged();
}

function relatedCloseClick(){
    mini.get("projectName").setValue("");
    mini.get("projectName").setText("");
    mini.get("respId").setValue("");
    mini.get("respId").setText("");
    mini.get("projectId").setValue("");
    // mini.get("projectId").setText("");
    mini.get("cwddh").setValue("");
    mini.get("cwddh").setText("");
    mini.get("gbcwddh").setValue("");
    mini.get("gbcwddh").setText("");
}
