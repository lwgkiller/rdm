$(function () {
    //TODO 是否要限制只能选择自己参与的项目列表
    var queryProjectUrl = jsUseCtxPath + "/drbfm/total/projectList.do";
    $.ajax({
        url:queryProjectUrl,
        async:false,
        success:function (json) {
            mini.get("projectId").setData(json);
        }
    });
    if(totalId) {
        var url = jsUseCtxPath + "/drbfm/total/queryTotalDetail.do?id="+totalId;
        $.ajax({
            url:url,
            method:'get',
            async:false,
            success:function (json) {
                totalBaseInfoForm.setData(json);
            }
        });
    }
    // 根据action处理页面编辑和按钮权限
    mini.get("copyTotalApply").hide();

    if (action == 'detail') {
        totalBaseInfoForm.setEnabled(false);
        groupGrid.setAllowCellEdit(false);
        mini.get("saveTotalApply").setEnabled(false);
        mini.get("commitAndCreateSingle").setEnabled(false);
    } else if (action=="copy"){
        mini.get("saveTotalApply").hide();
        mini.get("commitAndCreateSingle").hide();
        mini.get("copyTotalApply").show();
    }
    if (commitFlag == "false") {
        mini.get("commitAndCreateSingle").hide();
    }
});

function saveTotalApply() {
    var formData = totalBaseInfoForm.getData();
    // 校验表单必填项
    if(!formData.jixing) {
        mini.alert("请填写设计型号！");
        return;
    }
    if(!formData.analyseName) {
        mini.alert("请填写风险分析总项目名称！");
        return;
    }
    //表格检验
    groupGrid.validate();
    if(!groupGrid.isValid()){
        var error = groupGrid.getCellErrors()[0];
        mini.alert(error.errorText);
        return;
    }
    //获得表格的每行值
    var gridData = groupGrid.getData();
    var test1=mini.encode(formData);
    var test2=mini.encode(gridData);
    var postData={
        gridData:mini.encode(gridData),
        formData:mini.encode(formData)
    };
    _SubmitJson({
        url: jsUseCtxPath+"/drbfm/total/saveTotal.do",
        data:postData,
        method:'POST',
        showMsg:false,
        success:function(returnData){
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action=edit&id="+returnData.data;
                        window.location.href = url;
                    }
                });
            }
        }
    });

}

function copyTotalApply() {

    var formData = totalBaseInfoForm.getData();
    // 校验表单必填项
    if(!formData.jixing) {
        mini.alert("请填写设计型号！");
        return;
    }
    if(!formData.analyseName) {
        mini.alert("请填写风险分析总项目名称！");
        return;
    }
    //表格检验
    groupGrid.validate();
    if(!groupGrid.isValid()){
        var error = groupGrid.getCellErrors()[0];
        mini.alert(error.errorText);
        return;
    }
    //获得表格的每行值
    var gridData = groupGrid.getData();
    var test1=mini.encode(formData);
    var test2=mini.encode(gridData);
    var postData={
        gridData:mini.encode(gridData),
        formData:mini.encode(formData),
        action:action
    };
    _SubmitJson({
        url: jsUseCtxPath+"/drbfm/total/copyTotal.do",
        data:postData,
        method:'POST',
        showMsg:false,
        success:function(returnData){
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action=edit&id="+returnData.data;
                        window.location.href = url;
                    }
                });
            }
        }
    });

}

function commitAndCreateSingle() {
    if(!totalId) {
        mini.alert("请先点击“保存”按钮进行表单保存！");
        return;
    }
    var formData = totalBaseInfoForm.getData();
    // 校验表单必填项
    if(!formData.jixing) {
        mini.alert("请填写设计型号！");
        return;
    }
    if(!formData.analyseName) {
        mini.alert("请填写风险分析总项目名称！");
        return;
    }
    if(!formData.femaType) {
        mini.alert("请填写FMEA类型！");
        return;
    }
    if(!formData.department) {
        mini.alert("请选择所属部门！");
        return;
    }
    //表格检验
    groupGrid.validate();
    if(!groupGrid.isValid()){
        var error = groupGrid.getCellErrors()[0];
        mini.alert(error.errorText);
        return;
    }
    if (!confirm("提交后会创建部件对应的分析项目，已关联分析项目的不会重复创建，确定继续？")) {return;}

    //获得表格的每行值
    var gridData = groupGrid.getData();
    var postData={
        gridData:mini.encode(gridData),
        formData:mini.encode(formData)
    };
    showLoading();
    mini.get("commitAndCreateSingle").setEnabled(false);
    mini.get("saveTotalApply").setEnabled(false);
    $.ajax({
        url: jsUseCtxPath+"/drbfm/total/commitAndCreateSingle.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action=edit&id="+returnData.data;
                        window.location.href = url;
                    }
                });
            }
        },
        complete:function(returnData){
            hideLoading();
            mini.get("commitAndCreateSingle").setEnabled(true);
            mini.get("saveTotalApply").setEnabled(true);
        }
    });

}


// function copyAndCreateSingle() {
//     if(!totalId) {
//         mini.alert("请进入已创建好的总项进行复制！");
//         return;
//     }
//     var formData = totalBaseInfoForm.getData();
//     // 校验表单必填项
//     if(!formData.jixing) {
//         mini.alert("请填写设计型号！");
//         return;
//     }
//     if(!formData.analyseName) {
//         mini.alert("请填写风险分析总项目名称！");
//         return;
//     }
//     //表格检验
//     groupGrid.validate();
//     if(!groupGrid.isValid()){
//         var error = groupGrid.getCellErrors()[0];
//         mini.alert(error.errorText);
//         return;
//     }
//     if (!confirm("以此为模板复制，请确认已完成修改，确定继续？")) {return;}
//
//     //获得表格的每行值
//     var gridData = groupGrid.getData();
//     var postData={
//         gridData:mini.encode(gridData),
//         formData:mini.encode(formData),
//         action:action
//     };
//     showLoading();
//     mini.get("commitAndCreateSingle").setEnabled(false);
//     mini.get("copyAndCreateSingle").setEnabled(false);
//     mini.get("saveTotalApply").setEnabled(false);
//     $.ajax({
//         url: jsUseCtxPath+"/drbfm/total/copyAndCreateSingle.do",
//         type: 'POST',
//         contentType: 'application/json',
//         data: mini.encode(postData),
//         success: function (returnData) {
//             if (returnData && returnData.message) {
//                 mini.alert(returnData.message, '提示', function () {
//                     if (returnData.success) {
//                         var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action=edit&id="+returnData.data;
//                         window.location.href = url;
//                     }
//                 });
//             }
//         },
//         complete:function(returnData){
//             hideLoading();
//             mini.get("commitAndCreateSingle").setEnabled(true);
//             mini.get("saveTotalApply").setEnabled(true);
//         }
//     });
//
// }


//可以直接调用groupGrid.validate();
function onCellValidationTotal(e){
    var record = e.record;
    if(e.field=='sn' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='“序号”不能为空！';
    }
    if(e.field=='structName' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='“部件/接口名称”不能为空！';
    }
    if(e.field=='structNumber' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='“部件/接口编号”不能为空！';
    }
    if(e.field=='analyseUserId' && record.judgeNeedAnalyse=='是' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='需要风险分析场景下，“分析责任人”不能为空！';
    }
    if(e.field=='interfaceAName' && record.judgeIsInterface=='是' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='接口情况下，“接口A端要素名称”不能为空！';
    }
    if(e.field=='interfaceBName' && record.judgeIsInterface=='是' && (!e.value||e.value=='')){
        e.isValid = false;
        e.errorText='接口情况下，“接口B端要素名称”不能为空！';
    }

}

//用于展开树形结构时将当前节点的id赋值到parentId中
function onBeforeGridTreeLoadTotal(e){
    var tree = e.sender;    //树控件
    var node = e.node;      //当前节点
    var params = e.params;  //参数对象
    //可以传递自定义的属性
    params.parentId = node.id;
}

function singleDetail(applyId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/drbfm/single/singleFramePage.do?action=" + action + "&singleId=" + applyId + "&status=" + status;
    var winObj = window.open(url);
}

function singleEdit(applyId, status) {
    var action = "edit";
    var url = jsUseCtxPath + "/drbfm/single/singleFramePage.do?action=" + action + "&singleId=" + applyId + "&status=" + status;
    var winObj = window.open(url);
}

//跳转到任务的处理界面
function devTaskDo(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async:false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if(!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (singleListGrid) {
                            singleListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

//新增流程（后台根据配置的表单进行跳转）
function addSingleInterfaceCollectFlow(id) {
    var singleId = getSingleData(id);
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
    var url = jsUseCtxPath + "/bpm/core/bpmInst/drbfmSingleInterfaceCollect/start.do?id=" + id;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
        }
    }, 1000);
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