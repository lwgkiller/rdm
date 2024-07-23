var achieveTypeList = "";
var projectId ="";
$(function () {
    $.ajaxSettings.async = false;
    if(zgzlObj) {
        formZgzl.setData(zgzlObj);
        mini.get("jsfz").setText(mini.get("jsfz").getValue());
    }
    queryDimension();
    queryDimension2();
    queryDimension3();
    queryDimension4();
    if(!action){
        mini.get("departmentId").setValue(currentUserMainGroupId);
        mini.get("departmentId").setText(currentUserMainGroupName);
    }
    if(action == 'detail'){
        formZgzl.setEnabled(false);
        mini.get("saveNewZgzl").hide();
        mini.get("addJiaoFei").hide();
        mini.get("saveJiaoFei").hide();
        mini.get("deleteMJiaoFei").hide();
    }
    if(action == 'change'){
        formZgzl.setEnabled(false);
        mini.get("model").setEnabled(true);
        mini.get("addJiaoFei").hide();
        mini.get("saveJiaoFei").hide();
        mini.get("deleteMJiaoFei").hide();
    }

    if(action){
        initForm();
    }
    if(showJiaofei) {
        $("#jiaofeiTr").show();
    }
    $.ajaxSettings.async = true;
    projectId = mini.get("project").getValue();
    if(projectId){
        mini.get("project").setEnabled(false);
        mini.get("plan").setEnabled(false);
    }

});
//专利类型下拉数据
function queryDimension() {
    var type="专利类型";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#zllxId").load(data);
            }
        }
    });
}
//案件状态下拉数据
function queryDimension2() {
    var type="案件状态";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#gnztId").load(data);
            }
        }
    });
}
//是否
function queryDimension3() {
    var type="yes/no";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#permissionOthers").load(data);
                mini.get("#whetherPledge").load(data);
            }
        }
    });
}
//缴费类别下拉数据
function queryDimension4() {
    var type="缴费类别";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                achieveTypeList=data;
            }
        }
    });
}
//创建保存中国专利
function saveNewZgzl() {
    var postData = {};
    postData.zgzlId =mini.get("zgzlId").getValue();
    postData.departmentId =mini.get("departmentId").getValue();
    postData.departmentName =mini.get("departmentId").getText();
    postData.billNo =mini.get("billNo").getValue();
    postData.reportName =mini.get("reportName").getValue();
    postData.patentName =mini.get("patentName").getValue();
    postData.zllxId =mini.get("zllxId").getValue();
    postData.zllxName =mini.get("zllxId").getText();
    postData.applicationNumber =mini.get("applicationNumber").getValue();
    postData.theInventors =mini.get("theInventors").getValue();
    postData.thepatentee =mini.get("thepatentee").getValue();
    postData.filingdate =mini.get("filingdate").getText();
    postData.byCase =mini.get("byCase").getText();
    postData.examinationApproval =mini.get("examinationApproval").getText();
    postData.authorizationDate =mini.get("authorizationDate").getText();
    postData.authionization =mini.get("authionization").getText();
    postData.personPermitted =mini.get("personPermitted").getValue();
    postData.permissionOthers =mini.get("permissionOthers").getValue();
    postData.whetherPledge =mini.get("whetherPledge").getValue();
    postData.transferredCompany =mini.get("transferredCompany").getValue();
    postData.claimsNumber =mini.get("claimsNumber").getValue();
    postData.gnztId =mini.get("gnztId").getValue();
    postData.patentDate =mini.get("patentDate").getText();
    postData.expiryDate =mini.get("expiryDate").getText();
    postData.failureReason =mini.get("failureReason").getValue();
    postData.agencyName =mini.get("agencyName").getValue();
    postData.agentThe =mini.get("agentThe").getValue();
    postData.companyBelongs =mini.get("companyBelongs").getValue();
    postData.authorizedReward =mini.get("authorizedReward").getValue();
    postData.authirizedTime =mini.get("authirizedTime").getText();
    postData.specifyCountry =mini.get("specifyCountry").getValue();
    postData.myCompanyUserIds =mini.get("myCompanyUserIds").getValue();
    postData.myCompanyUserNames =mini.get("myCompanyUserIds").getText();
    postData.zhuanYe =mini.get("zhuanYe").getValue();
    postData.jllb =mini.get("jllb").getValue();
    postData.beiZhu =mini.get("beiZhu").getValue();
    postData.ipcFlh =mini.get("ipcFlh").getValue();
    postData.ipcZflh =mini.get("ipcZflh").getValue();
    postData.jsfz = mini.get("jsfz").getText();
    postData.projectId = mini.get("project").getValue();
    postData.projectName = mini.get("project").getText();
    postData.planId = mini.get("plan").getValue();
    postData.planName = mini.get("plan").getText();
    postData.jsjdsId = mini.get("jsjdsId").getValue();
    postData.modelId = mini.get("model").getValue();
    postData.modelName = mini.get("model").getText();
    // 检查必填项
    if(action!='change'){
        var checkResult = commitValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
    }
    $.ajax({
        url: zlUseCtxPath + "/zhgl/core/zlgl/saveNewZgzlData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        // window.close();
                        var url=zlUseCtxPath+"/zhgl/core/zlgl/zgzlPage.do?zgzlId="+returnData.data+"&action="+action;
                        window.location.href=url;
                    }
                });
            }
        }
    });
}
//专利证书附件
function addZhengShuFile() {
    var fjlx="zsfj";
    if (zgzlObj){
        var meetingId = zgzlObj.zgzlId;
        if (!meetingId) {
            mini.alert("请先点击‘保存’进行表单创建！")
            return;
        }
        var canOperateFile = false;
        if (action=='edit'||action=='') {
            canOperateFile = true;
        }
        mini.open({
            title: "文件上传",
            url: zlUseCtxPath + "/zhgl/core/zlgl/zgzlUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
}
//缴费附件
function addJiaoFeiFile(fyid,zlId) {
    var fjlx="jffj";
    var meetingId = fyid;
    if (!meetingId) {
        mini.alert("请先点击‘保存’进行表单创建！")
        return;
    }
    var canOperateFile = false;
    if (action=='edit'||action=='') {
        canOperateFile = true;
    }
    mini.open({
        title: "文件上传",
        url: zlUseCtxPath + "/zhgl/core/zlgl/zgzlUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
// function addJiaoFei() {
//     var newRow = {}
//     newRow.id = "";
//     newRow.meetingId = mini.get("zgzlId").getValue();
//     newRow.wjflbId = "";
//     newRow.paymenAmount = "";
//     newRow.paymentTime = "";
//     newRow.meetingPlanCompletion = "";
//     addRowGrid("jiaoFeiListGrid", newRow);
// }
function addZGJiaoFei() {
    var meetingId = mini.get("zgzlId").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存’！");
        return;
    }else {
        var row={};
        jiaoFeiListGrid.addRow(row);
    }
}
function saveZGJiaoFei() {
    var meetingId = mini.get("zgzlId").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存’！");
        return;
    }
    var postData = jiaoFeiListGrid.data;
    var postDataJson = mini.encode(postData);
    $.ajax({
        url: zlUseCtxPath + "/zhgl/core/zlgl/saveJiaoFei.do?meetingId="+meetingId,
        type: 'POST',
        contentType: 'application/json',
        data: postDataJson,
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        // jiaoFeiListGrid.reload();
                        var zlid=returnData.data;
                        if (zlid){
                            var url = zlUseCtxPath + "/zhgl/core/zlgl/getJiaoFeiList.do";
                            $.post(
                                url,
                                {ids: zlid},
                                function (json) {
                                    if (json != null && json.length > 0) {
                                        jiaoFeiListGrid.setData(json);
                                    }
                                });
                        }
                    }
                });
            }
        }
    });
}
function deleteMJiaoFei() {
    var row = jiaoFeiListGrid.getSelected();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    if (row.id == "") {
        delRowGrid("jiaoFeiListGrid");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var id = row.fyId;
            _SubmitJson({
                url: zlUseCtxPath + "/zhgl/core/zlgl/deleteOneJiaofeiData.do",
                method: 'POST',
                data: {id: id},
                success: function (returnData) {
                    if (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    jiaoFeiListGrid.reload();
                                }
                            });
                        }
                    }
                }
            });
        }
    });
}
function commitValidCheck(postData) {
    var checkResult = {};
    if (!postData.billNo) {
        checkResult.success = false;
        checkResult.reason = '请填写提案号！';
        return checkResult;
    }
    if (!postData.reportName) {
        checkResult.success = false;
        checkResult.reason = '请填写提案名称！';
        return checkResult;
    }
    if(postData.projectId){
        if (!postData.planId) {
            checkResult.success = false;
            checkResult.reason = '请选择成果计划！';
            return checkResult;
        }
    }
    if(!projectId&&postData.projectId&&postData.planId){
        var zgzlId = mini.get("zgzlId").getValue();
        $.ajaxSettings.async = false;
        var url = zlUseCtxPath + "/zhgl/core/zlgl/checkProject.do?zgzlId="+zgzlId;
        $.get(
            url,
            function (json) {
                checkResult = json;
            });
        $.ajaxSettings.async = true;
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
    }
    checkResult.success = true;
    return checkResult;
}
function initForm() {
    var meetingId = zgzlObj.zgzlId;
    if (meetingId){
        var url = zlUseCtxPath + "/zhgl/core/zlgl/getJiaoFeiList.do";
        $.post(
            url,
            {ids: meetingId},
            function (json) {
                if (json != null && json.length > 0) {
                    jiaoFeiListGrid.setData(json);
                }
            });
    }

}

function onJsfzCloseClick(){
    mini.get("jsfz").setValue('');
    mini.get("jsfz").setText('');
}

function selectJsfz(){
    mini.open({
        title: "技术分支选择",
        url: zlUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch/jszhTree.do",
        width: 1300,
        height: 700,
        showModal: true,
        allowResize: true,
        onload: function () {

        },
        ondestroy: function (returnData) {
            if(returnData) {
                mini.get("jsfz").setValue(returnData.description);
                mini.get("jsfz").setText(returnData.description);
            } else {
                mini.alert("未选择技术分支！");
            }
        }
    });
}