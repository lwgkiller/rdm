var isChuangjian = "";
var isChuliren = "";
var isZZRXZ = "";


$(function () {
    if (gyfkId) {
        var url = jsUseCtxPath + "/qualityfxgj/core/Gyfk/getGyfk.do";
        $.post(
            url,
            {gyfkId: gyfkId},
            function (json) {
                formGyfk.setData(json);
            });
    }
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formGyfk.setEnabled(false);
        mini.get("addGyfkDetail").setEnabled(false);
        mini.get("editGyfkDetail").setEnabled(false);
        mini.get("removeGyfkDetail").setEnabled(false);
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
    }
});

function getData() {
    var formData = _GetFormJsonMini("formGyfk");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveGyfk(e) {
    var formValid = validGyfkFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

function addGyfkDetail() {
    var gyfkId = mini.get("gyfkId").getValue();
    if (!gyfkId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    }
    mini.open({
        title: "计划明细",
        url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/editGyfkDetail.do?gyfkId=" + gyfkId + "&action=add",
        width: 1050,
        height: 850,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (detailListGrid) {
                detailListGrid.load();
            }
        }
    });
}

function editGyfkDetail() {
    var gyfkId = mini.get("gyfkId").getValue();
    if (!gyfkId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    }
    var row = detailListGrid.getSelected();
    if (row) {
        var gyxjId = row.gyxjId;
        var resId = row.resId;
        if(resId==currentUserId||isChuangjian||isZZRXZ||status=="DRAFTED"){
            mini.open({
                title: "计划明细",
                url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/editGyfkDetail.do?gyxjId=" + gyxjId + "&action=edit&gyfkId=" + gyfkId
                    + "&isChuangjian=" + isChuangjian + "&isZZRXZ=" + isZZRXZ,
                width: 1050,
                height: 850,
                showModal: true,
                allowResize: true,
                onload: function () {
                },
                ondestroy: function (action) {
                    if (detailListGrid) {
                        detailListGrid.load();
                    }
                }
            });
        }else {
            mini.alert("请处理责任人是当前用户的问题清单！");
        }
    } else {
        mini.alert("请选中一条记录");
    }
}

function removeGyfkDetail() {
    var gyfkId = mini.get("gyfkId").getValue();
    if (!gyfkId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    }
    var row = detailListGrid.getSelected();
    if (row) {
        var gyxjId = row.gyxjId;
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/deleteGyfkDetail.do?gyfkId=" + gyfkId,
                    method: 'POST',
                    showMsg: false,
                    data: {gyxjId: gyxjId},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            detailListGrid.reload();
                        }
                    }
                });
            }
        });
    } else {
        mini.alert("请选中一条记录");
    }
}

function gyfkFile(gyxjId) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/fileList.do?&gyxjId=" + gyxjId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
        }
    });
}

function startGyfkProcess(e) {
    var formValid = validGyfkFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var gridData = detailListGrid.getData();
    if (gridData.length<1) {
        mini.alert("请填写问题清单！");
        return;
    }
    window.parent.startProcess(e);
}

function validGyfkFirst() {
    var repPerson = $.trim(mini.get("repPerson").getText())
    if (!repPerson) {
        return {"result": false, "message": "请选择产品主管"};
    }
    var repDep = $.trim(mini.get("repDep").getText())
    if (!repDep) {
        return {"result": false, "message": "请选择责任部门"};
    }
    var checkZrName = $.trim(mini.get("checkZrName").getText())
    if (!checkZrName) {
        return {"result": false, "message": "请选择审核室主任"};
    }
    return {"result": true};
}



function gyfkApprove() {
    var gridData = detailListGrid.getData();
    if (isChuliren == "yes") {
        for (var i = 0; i < gridData.length; i++) {
            if(gridData[i].resId==currentUserId){
                if (gridData[i].method == undefined || gridData[i].completion == undefined || gridData[i].finishTime == undefined) {
                    mini.alert("请处理责任人是当前用户的问题清单！");
                    return;
                }
            }
        }
    }
    if (isZZRXZ == "yes") {
        for (var i = 0; i < gridData.length; i++) {
            if (gridData[i].res == undefined||gridData[i].res == "") {
                mini.alert("请选择责任人！");
                return;
            }
        }
    }
    //编制阶段的下一步需要校验表单必填字段
    var formValid = validGyfkFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查通过
    window.parent.approve();
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


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isChuangjian') {
            isChuangjian = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isChuliren') {
            isChuliren = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZZRXZ') {
            isZZRXZ = nodeVars[i].DEF_VAL_;
        }
    }
    formGyfk.setEnabled(false);
    mini.get("addGyfkDetail").setEnabled(false);
    mini.get("editGyfkDetail").setEnabled(false);
    mini.get("removeGyfkDetail").setEnabled(false);

    if (isChuangjian == 'yes') {
        formGyfk.setEnabled(true);
        mini.get("addGyfkDetail").setEnabled(true);
        mini.get("editGyfkDetail").setEnabled(true);
        mini.get("removeGyfkDetail").setEnabled(true);
    }
    if (isChuliren == 'yes') {
        mini.get("editGyfkDetail").setEnabled(true);
    }
    if (isZZRXZ == 'yes') {
        mini.get("editGyfkDetail").setEnabled(true);
    }
}




