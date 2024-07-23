var stageName = "";
$(function () {
    var url = jsUseCtxPath + "/world/core/fitnessImprove/getJson.do";
    $.ajaxSettings.async = false;
    $.post(
        url,
        {id: applyId},
        function (json) {
            currentForm.setData(json);
        });
    $.ajaxSettings.async = true;
    if(!applyId){
        mini.get("technical").setValue("No");
    }
    if (action == 'detail') {
        ifChange();
        currentForm.setEnabled(false);
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
    } else if (action == 'task') {
        ifChange();
        taskActionProcess();
    } else if (action == 'edit') {
        currentForm.setEnabled(false);
        setBaseInfoEnable();
    }else if (action == 'change') {
        currentForm.setEnabled(false);
        mini.get("technical").setEnabled(true)
    }
});

function getData() {
    var formData = _GetFormJsonMini("currentForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    if (formData.SUB_demandGrid) {
        delete formData.SUB_demandGrid;
    }
    return formData;
}

//保存草稿
function saveDraft(e) {
    window.parent.saveDraft(e);
    // currentForm.reload();
}

//发起流程
function startProcess(e) {
    var formValid = validBase();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

// 编制表格必填字段验证
function validBase() {
    if (!mini.get("productModel").getValue()) {
        return {"result": false, "message": "please enter product model"};
    }
    if (!mini.get("region").getValue()) {
        return {"result": false, "message": "please enter region "};
    }
    if (!mini.get("improveType").getValue()) {
        return {"result": false, "message": "please choose  Imporvement Characteristics "};
    }
    if (!mini.get("improveSource").getValue()) {
        return {"result": false, "message": "please choose  Imporvement From "};
    }
    if (!mini.get("reqDesc").getValue()) {
        return {"result": false, "message": "please enter  Requirment Description "};
    }
    if (!mini.get("competitorDesc").getValue()) {
        return {"result": false, "message": "please enter  Competitor Design Programme "};
    }
    if (!mini.get("improveDesc").getValue()) {
        return {"result": false, "message": "please enter  Improvement Suggestion  "};
    }
    return {"result": true};
}

function checkFile(checkFileType) {

    $.ajax({
        url: jsUseCtxPath + "/world/core/fitnessImprove/checkFileList.do?fileType="+checkFileType+"&baseInfoId="+id.value,
        type: 'POST',
        contentType: 'application/json',
        async:false,
        data:mini.encode({
            fileType:checkFileType,
            baseInfoId:id.value
        }),
        success: function (data) {
            console.log(data);
            return data;
        }
    });
}

//下一步审批
function applyApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (stageName == 'start') {
        var formValid = validBase();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //海外基地判断
    else if (stageName == 'hwjdpd') {
        if (!mini.get("hwjdJudge").getValue()) {
            mini.alert("please choose Judge Issue Type");
            return;
        }
    }
    // 国际所建议确认
    else if (stageName == 'gjsjyqr') {
        if (!mini.get("productManagerId").getValue()) {
            mini.alert("please choose productManager");
            return;
        }
    }
    // 产品能否改进 如需添加文件要确认已添加文件
    else if (stageName == 'cpnfgj') {
        if (!mini.get("pmJudge").getValue()) {
            mini.alert("please choose Judge Improve Type");
            return;

        } else if (mini.get("pmJudge").getValue() == "yes") {
            if (!mini.get("improveProjcetDesc").getValue()) {
                mini.alert("please enter Improve Project Plan");
                return;
            }
            var res = false;
            var checkFileType = 'pla';
            $.ajax({
                url: jsUseCtxPath + "/world/core/fitnessImprove/checkFileList.do?fileType="+checkFileType+"&baseInfoId="+id.value,
                type: 'POST',
                contentType: 'application/json',
                async:false,
                data:mini.encode({
                    fileType:checkFileType,
                    baseInfoId:id.value
                }),
                success: function (data) {
                    res = data;
                    return
                }
            });
            if (!res) {
                mini.alert("please upload Improve Project Plan");
                return;
            }


        } else if (mini.get("pmJudge").getValue() == "no") {
            if (!mini.get("cantImproveDesc").getValue()) {
                mini.alert("please enter Can Not Improve Reason");
                return;
            }
        }

    }
    //产品方案验证
    else if (stageName == 'cpfayz') {
        var res = false;
        var checkFileType = 'con';
        $.ajax({
            url: jsUseCtxPath + "/world/core/fitnessImprove/checkFileList.do?fileType="+checkFileType+"&baseInfoId="+id.value,
            type: 'POST',
            contentType: 'application/json',
            async:false,
            data:mini.encode({
                fileType:checkFileType,
                baseInfoId:id.value
            }),
            success: function (data) {
                res = data;
                return
            }
        });
        if (!res) {
            mini.alert("please upload Project Verification Document");
            return;
        }

    }
    //国际所实施计划
    else if (stageName == 'gjsssjh') {
        if (!mini.get("planActTime").getValue()) {
            mini.alert("please select Plan Implementation Time");
            return;
        }

    }
    //检查通过
    window.parent.approve();
}


function processInfo1() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function setBaseInfoEnable() {
    mini.get("productModel").setEnabled(true);
    mini.get("region").setEnabled(true);
    mini.get("improveType").setEnabled(true);
    mini.get("improveSource").setEnabled(true);
    mini.get("productModel").setEnabled(true);
    mini.get("reqDesc").setEnabled(true);
    mini.get("reqDescUrl").setEnabled(true);
    mini.get("competitorDesc").setEnabled(true);
    mini.get("competitorUrl").setEnabled(true);
    mini.get("improveDesc").setEnabled(true);
    mini.get("improveDescUrl").setEnabled(true);
    //基础三个文件权限
    baseFileEdit = "true";

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
        if (nodeVars[i].KEY_ == 'stageName') {
            stageName = nodeVars[i].DEF_VAL_;
        }
    }

    currentForm.setEnabled(false);

    if (stageName == 'start') {
        setBaseInfoEnable();


    }
    else if (stageName == 'hwjdpd') {
        mini.get("hwjdJudge").setEnabled(true);
    }
    else if (stageName == 'gjsjyqr') {
        mini.get("productManagerId").setEnabled(true);
    }
    else if (stageName == 'cpnfgj') {
        mini.get("pmJudge").setEnabled(true);
        mini.get("cantImproveDesc").setEnabled(true);
        mini.get("improveProjcetDesc").setEnabled(true);
        projectFileEdit = "true";
    }
    else if (stageName == 'cpfayz') {
        projectVerifyEdit = "true";

    }
    else if (stageName == 'gjsssjh') {
        mini.get("planActTime").setEnabled(true);

    }

}

function addPicFile(fileType, canEdit) {
    // var fjlx="gztp";
    if (!applyId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    // var canOperateFile = false;
    // if ((action=='edit'||action=='') ||(isBianzhi == 'yes')||action=='change') {
    //     canOperateFile = true;
    // }
    debugger;
    mini.open({
        title: "上传图片",
        url: jsUseCtxPath + "/world/core/fitnessImprove/openFileWindow.do?applyId=" + applyId + "&fileType=" + fileType + "&canEdit=" + canEdit,
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

// 根据【能否改进】选项显示表单
function ifChange() {
    var pmJudge = mini.get("pmJudge").getValue();
    if (!pmJudge) {
        return;
    }
    if (pmJudge == "yes") {
        $("#canNotImprove").hide();
        $("#canImprove").show();
    }
    else {
        $("#canImprove").hide();
        $("#canNotImprove").show();
    }

}


