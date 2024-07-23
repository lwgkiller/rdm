$(function () {
    if (singleId) {
        $.ajax({
            url:jsUseCtxPath+"/drbfm/single/getSingleBaseDetail.do?id="+singleId,
            method:'get',
            success:function (json) {
                formProject.setData(json);
            }
        });
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
    var tab1=systemTab.getTab("baseInfo");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleBaseInfoPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab1,{url:newUrl,loadedUrl:newUrl});

    var tab11=systemTab.getTab("expInfo");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleExpInfoPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab11,{url:newUrl,loadedUrl:newUrl});


    var tab2=systemTab.getTab("deptDemand");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleDeptDemandPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab2,{url:newUrl,loadedUrl:newUrl});

    var tab21=systemTab.getTab("changeInfo");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleChangeInfoPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab21,{url:newUrl,loadedUrl:newUrl});

    var tab3=systemTab.getTab("functionAndRequest");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleFunctionRequestPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab3,{url:newUrl,loadedUrl:newUrl});

    var tab31=systemTab.getTab("functionNet");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleFunctionNetPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action+"&startOrEnd="+startOrEnd;
    systemTab.updateTab(tab31,{url:newUrl,loadedUrl:newUrl});

    var tab32=systemTab.getTab("riskAnalysis");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleFunctionRiskAnalysisPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab32,{url:newUrl,loadedUrl:newUrl});

    var tab4=systemTab.getTab("quotaDecompose");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleQuotaDecomposePage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab4,{url:newUrl,loadedUrl:newUrl});

    var tab5=systemTab.getTab("testTask");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleTestTaskPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab5,{url:newUrl,loadedUrl:newUrl});

    var tab6=systemTab.getTab("quotaEvaluate");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleQuotaEvaluatePage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab6,{url:newUrl,loadedUrl:newUrl});

    var tab7=systemTab.getTab("finalProcess");
    var newUrl=jsUseCtxPath+"/drbfm/single/singleFinalProcessPage.do?singleId="+singleId+"&stageName="+stageName+"&action="+action;
    systemTab.updateTab(tab7,{url:newUrl,loadedUrl:newUrl});
    // 激活
    systemTab.activeTab(systemTab.getTab(0));
    //加载内容
    systemTab.reloadTab(systemTab.getTab(0));
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formProject");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
/*    formData.vars = [{key: 'deptName', val: '测试'}, {key: 'yearMonth', val: '2022-10'}];*/
    return formData;
}

var checkResult = {"result": true};
//检查风险分析分解内容是否填写完成
function checkRiskDecompose() {
    $.ajax({
        url: jsUseCtxPath + '/drbfm/single/checkRiskDecompose.do?singleId=' + singleId ,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                checkResult = {"result": false, "message": data.message};
            } else {
                checkResult = {"result": true};
            }
        }
    });
}

var interfaceResult = {"result": false};
//检查风险分析分解内容是否填写完成
function checkSingleInterface() {
    $.ajax({
        url: jsUseCtxPath + '/drbfm/single/checkSingleInterface.do?singleId=' + singleId ,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            interfaceResult = {"result": data.success};
        }
    });
}

//检查改进后处理是否完成
function checkFinalProcess() {
    $.ajax({
        url: jsUseCtxPath + '/drbfm/single/checkFinalProcess.do?singleId=' + singleId ,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                checkResult = {"result": false, "message": data.message};
            } else {
                checkResult = {"result": true};
            }
        }
    });
}


var checkNetResult = {"result": true};
//检查风险分析分解内容是否填写完成
function checkFunctionNetStatus() {
    $.ajax({
        url: jsUseCtxPath + '/drbfm/single/checkFunctionNetStatus.do?partId=' + singleId +"&startOrEnd="+startOrEnd,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                checkNetResult = {"result": false, "message": data.message};
            } else {
                checkNetResult = {"result": true};
            }
        }
    });
}



//流程任务办理时，审批或者下一步按钮的处理
function singleApprove() {
    if(action=='task' && stageName=='bjfzrfxfx') {
        checkFunctionNetStatus();
        if (!checkNetResult.result) {
            mini.alert(checkNetResult.message);
            return;
        }
        checkRiskDecompose();
        checkSingleInterface();
        if (!interfaceResult.result){
            mini.alert("当前存在不是“已完成”或“作废”状态的接口收集流程，请跟进流程处理后重试！");
            return;
        }
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        } else {
            window.parent.approve();
        }
    } else if(action=='task' && stageName=='processFinal') {
        //每一个要求都要有改进后风险评估
        checkFinalProcess();
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        } else {
            window.parent.approve();
        }
    } else if(action=='task' && stageName=='testTaskCreateAndResult') {
        // 风险验证任务创建和指标综合评价
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/checkTestTaskAndResult.do?singleId=' + singleId ,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if(data.result=='false') {
                        mini.alert(data.message);
                        return;
                    } else if(data.result=='confirm') {
                        mini.confirm(data.message, "确定？",
                            function (action) {
                                if (action == "ok") {
                                    window.parent.approve();
                                } else {
                                    return;
                                }
                            }
                        );
                    } else if(data.result=='true') {
                        window.parent.approve();
                    }
                }
            }
        });
    } else {
        window.parent.approve();
    }
}
function singleMessageCopy(){
    copySelectedWindow.show();
    searchSingleGridData();
}
function searchSingleGridData(){
    var formData=$("#searchForm").serializeArray();
    var form = Array.from(formData);
    var data = {};
    data.filter = mini.encode(form);
    data.pageIndex = singleListGrid.getPageIndex();
    data.pageSize = singleListGrid.getPageSize();
    data.sortField = singleListGrid.getSortField();
    data.sortOrder = singleListGrid.getSortOrder();
    singleListGrid.load(data);
}
function clearSingleGridData(){
    searchFormProject.clear();
    singleListGrid.load();
}

//..
function copySingleProcess() {
    var rows = singleListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("是否快速引入选中流程中的“功能&要求描述”和“指标分解”的内容？(部件接口收集流程添加内容不引入)", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                    rowIds.push(r.id);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/drbfm/single/copySingleProcess.do",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(','), singleId: singleId},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        copySelectedWindow.hide();
                        clearSingleGridData();
                        initTabs();
                    }
                }
            });
        }
    });
}

function closeSingleGridData(){
    copySelectedWindow.hide();
    searchFormProject.clear();
    singleListGrid.load();
}
