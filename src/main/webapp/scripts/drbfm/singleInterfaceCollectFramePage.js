$(function () {
    if (id) {
        $.ajax({
            url:jsUseCtxPath+"/drbfm/single/getTotalStructDetail.do?id="+id,
            method:'get',
            success:function (json) {
                formProject.setData(json);
                structId = json.structId;
            }
        });
    }
    else {
        var json = {"structId": structId};
        formProject.setData(json);
    }
    initTabs();
    if (action == 'detail') {
        $("#detailToolBar").show();
        if (status != 'DRAFTED' && status != '') {
            $("#processInfo").show();
        }
    } else if (action == 'task') {
        bpmPreTaskTipInForm();
    }
});

function initTabs() {
    var tab3=systemTab.getTab("functionAndRequest");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleFunctionRequestCollectPage.do?stageName=" + stageName +"&action="+action +"&collectType="+collectType +"&belongCollectFlowId="+id;
    systemTab.updateTab(tab3,{url:newUrl,loadedUrl:newUrl});

    var tab4=systemTab.getTab("quotaDecompose");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleQuotaDecomposeCollectPage.do?stageName=" + stageName +"&action="+action +"&collectType="+collectType +"&belongCollectFlowId="+id;
    systemTab.updateTab(tab4,{url:newUrl,loadedUrl:newUrl});
    // 激活
    systemTab.reloadTab(systemTab.getTab(0));
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formProject");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿
function saveDraft(e) {
    window.parent.saveDraft(e);
}

//启动流程
function startProcess(e) {
    if(!id){
        mini.alert("“提交”流程前需点击“保存按钮！”");
        return ;
    }
    //structId来自流程初始化，根据接口主键id一一对应关系查询
    var singleId = getSingleData(structId);
    if (singleId == 'end'){
        mini.alert('该部件对应的部件分析项目流程已结束，不能发起需求收集！');
        return ;
    }
    //无单一流程绑定项目，无法发起需求收集
    if (singleId == ''){
        mini.alert('该部件没有对应的部件分析项目流程，不能发起需求收集！');
        return ;
    }
    //判断单一流程节点位置，信息修改后不允许提交,固定节点前才允许发起
    var status = queryStatusBySingleId(singleId);
    if (!status){
        mini.alert('该部件没有对应的部件分析项目流程的状态信息，不能发起需求收集！');
        return;
    }else if((status != '产品主管发起') && (status != '部件负责人风险分析编制')){
        mini.alert('对应部件分析项目流程在“部件负责人风险分析编制”后，不能发起需求收集！');
        return;
    }
    window.parent.startProcess(e);
}

//下一步审批
function projectApprove() {
    //检查通过
    window.parent.approve();
}

//..流程信息浏览
function processCollectInfo() {
    queryInstIdIdInterface(id)
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

//根据belongCollectFlowId获取instId
function queryInstIdIdInterface(belongCollectFlowId){
    $.ajax({
        url: jsUseCtxPath+"/drbfm/single/queryInstIdIdInterface.do?belongCollectFlowId="+belongCollectFlowId,
        method: 'GET',
        async: false,
        success: function (data) {
            instId = data;
        }
    });
}

function getSingleData(structId){
    var rows=[];
    $.ajax({
        url: jsUseCtxPath+"/drbfm/single/querySingleBaseList.do?structIds="+structId,
        method: 'GET',
        async: false,
        success: function (data) {
            rows.push(data.data[0]);
        }
    });
    var id = '';
    if (!rows[0]){
        return id;
    }
    for(var i=0;i<rows.length;i++){
        if (rows[i].instStatus == "SUCCESS_END"){
            return 'end' ;
        }else if (rows[i].instStatus != "DISCARD_END"){
            id = rows[i].id;
        }
    }
    return id;
}


function queryStatusBySingleId(id){
    var rows=[];
    $.ajax({
        url: jsUseCtxPath+"/drbfm/single/queryStatusBySingleId.do?id="+id,
        method: 'GET',
        async: false,
        success: function (data) {
            rows.push(data);
        }
    });
    var status = '';
    if (rows[0]){
        status = rows[0];
    }
    return status;
}