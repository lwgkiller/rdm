var isCpkfjh = "";
var isZjcsy = "";
var isCszd = "";
var isZjcse = "";
var isLbjsjy = "";
var isLbjcszd = "";
var isLbjsje = "";
var isJssh = "";
var isGysh = "";
var isStlbjyz = "";
var isZjyz = "";
var isStsh = "";
var isXplctx = "";
var isXplcsh = "";
var isZs = "";

$(function () {
    performznceGrid.hideColumn("CREATE_BY_")
    productGrid.hideColumn("CREATE_BY_")
    assemblyGrid.hideColumn("CREATE_BY_")
    verificationGrid.hideColumn("CREATE_BY_")
    batchGrid.hideColumn("CREATE_BY_")

    messageHide();
    formProductManage.setData(productManageObj);
    performznceGrid.load();
    productGrid.load();
    assemblyGrid.load();
    verificationGrid.load();
    batchGrid.load();


});

function messageHide() {
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isCpkfjh') {
            isCpkfjh = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZjcsy') {
            isZjcsy = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isCszd') {
            isCszd = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZjcse') {
            isZjcse = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isLbjsjy') {
            isLbjsjy = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isLbjcszd') {
            isLbjcszd = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isLbjsje') {
            isLbjsje = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isJssh') {
            isJssh = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isGysh') {
            isGysh = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isStlbjyz') {
            isStlbjyz = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZjyz') {
            isZjyz = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isStsh') {
            isStsh = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isXplctx') {
            isXplctx = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isXplcsh') {
            isXplcsh = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZs') {
            isZs = nodeVars[i].DEF_VAL_;
        }

    }

    //明细入口
    if (action == 'detail') {
        hideAll();
        $("#detailToolBar").show();

        //非草稿放开流程信息查看按钮
        if (formStatus != 'DRAFTED') {
            $("#processInfo").show();
        }

    } else if (action == 'task') {

        if (isCpkfjh == 'yes') {
            //负责人隐藏
            // mini.get("productSupervisorId").setEnabled(false);
            mini.get("tractorParameterId").setEnabled(false);
            mini.get("craftAssemblyId").setEnabled(false);
            mini.get("partsTechnologyId").setEnabled(false);

            //整机设计隐藏
            mini.get("productGrid").setEnabled(false);
            mini.get("addZjButton").setEnabled(false);
            mini.get("removeZjButton").setEnabled(false);
            //部品设计隐藏
            mini.get("assemblyGrid").setEnabled(false);
            mini.get("addBpButton").setEnabled(false);
            mini.get("removeBpButton").setEnabled(false);
            //验证首台隐藏
            mini.get("verificationGrid").setEnabled(false);
            mini.get("addYzButton").setEnabled(false);
            mini.get("removeYzButton").setEnabled(false);
            //小批量产隐藏
            mini.get("batchGrid").setEnabled(false);
            mini.get("addXpButton").setEnabled(false);
            mini.get("removeXpButton").setEnabled(false);
        }

        if (isZjcsy == 'yes') {
            hideAll();
            mini.get("partsTechnologyId").setEnabled(true);
            mini.get("productGrid").setEnabled(true);
            mini.get("addZjButton").setEnabled(true);
            mini.get("removeZjButton").setEnabled(true);
        }

        if (isCszd == 'yes') {
            hideAll();
            mini.get("tractorParameterId").setEnabled(true);
        }

        if (isZjcse == 'yes') {
            hideAll();
            mini.get("productGrid").setEnabled(true);
            mini.get("addZjButton").setEnabled(true);
            mini.get("removeZjButton").setEnabled(true);
        }

        if (isLbjsjy == 'yes') {
            hideAll();
            mini.get("assemblyGrid").setEnabled(true);
            mini.get("addBpButton").setEnabled(true);
            mini.get("removeBpButton").setEnabled(true);
        }

        if (isLbjcszd == 'yes') {
            hideAll();
            mini.get("craftAssemblyId").setEnabled(true);
        }

        if (isLbjsje == 'yes') {
            hideAll();
            mini.get("assemblyGrid").setEnabled(true);
            mini.get("addBpButton").setEnabled(true);
            mini.get("removeBpButton").setEnabled(true);
        }

        if (isJssh == 'yes') {
            hideAll();
        }

        if (isGysh == 'yes') {
            hideAll();
        }

        if (isStlbjyz == 'yes') {
            hideAll();
            mini.get("verificationGrid").setEnabled(true);
            mini.get("addYzButton").setEnabled(true);
            mini.get("removeYzButton").setEnabled(true);
        }

        if (isZjyz == 'yes') {
            hideAll();
            mini.get("verificationGrid").setEnabled(true);
            mini.get("addYzButton").setEnabled(true);
            mini.get("removeYzButton").setEnabled(true);
        }

        if (isStsh == 'yes') {
            hideAll();
        }

        if (isXplctx == 'yes') {
            hideAll();
            mini.get("batchGrid").setEnabled(true);
            mini.get("addXpButton").setEnabled(true);
            mini.get("removeXpButton").setEnabled(true);
        }

        if (isXplcsh == 'yes') {
            hideAll();
        }

        if (isZs == 'yes') {
            hideAll();
        }
    } else if (action == 'edit') {
        //负责人隐藏
        // mini.get("productSupervisorId").setEnabled(false);
        mini.get("tractorParameterId").setEnabled(false);
        mini.get("craftAssemblyId").setEnabled(false);
        mini.get("partsTechnologyId").setEnabled(false);
        //整机设计隐藏
        mini.get("productGrid").setEnabled(false);
        mini.get("addZjButton").setEnabled(false);
        mini.get("removeZjButton").setEnabled(false);
        //部品设计隐藏
        mini.get("assemblyGrid").setEnabled(false);
        mini.get("addBpButton").setEnabled(false);
        mini.get("removeBpButton").setEnabled(false);
        //验证首台隐藏
        mini.get("verificationGrid").setEnabled(false);
        mini.get("addYzButton").setEnabled(false);
        mini.get("removeYzButton").setEnabled(false);
        //小批量产隐藏
        mini.get("batchGrid").setEnabled(false);
        mini.get("addXpButton").setEnabled(false);
        mini.get("removeXpButton").setEnabled(false);
    }
}

function hideAll() {
    formProductManage.setEnabled(false);
    //产品性能需求隐藏
    mini.get("performznceGrid").setEnabled(false);
    mini.get("addXnButton").setEnabled(false);
    mini.get("removeButton").setEnabled(false);
    //整机设计隐藏
    mini.get("productGrid").setEnabled(false);
    mini.get("addZjButton").setEnabled(false);
    mini.get("removeZjButton").setEnabled(false);
    //部品设计隐藏
    mini.get("assemblyGrid").setEnabled(false);
    mini.get("addBpButton").setEnabled(false);
    mini.get("removeBpButton").setEnabled(false);
    //验证首台隐藏
    mini.get("verificationGrid").setEnabled(false);
    mini.get("addYzButton").setEnabled(false);
    mini.get("removeYzButton").setEnabled(false);
    //小批量产隐藏
    mini.get("batchGrid").setEnabled(false);
    mini.get("addXpButton").setEnabled(false);
    mini.get("removeXpButton").setEnabled(false);

    mini.get("performznceGrid").setEnabled(false);
    mini.get("productGrid").setEnabled(false);
    mini.get("assemblyGrid").setEnabled(false);
    mini.get("verificationGrid").setEnabled(false);
    mini.get("batchGrid").setEnabled(false);

}


function addProductMessage(name) {
    var newRow = {name: "New Row"};
    if (name == 'xn') {
        performznceGrid.addRow(newRow, 0);
        performznceGrid.beginEditCell(newRow, "workContent");
    } else if (name == 'zj') {
        productGrid.addRow(newRow, 0);
        productGrid.beginEditCell(newRow, "workContent");
    } else if (name == 'bp') {
        assemblyGrid.addRow(newRow, 0);
        assemblyGrid.beginEditCell(newRow, "workContent");
    } else if (name == 'yz') {
        verificationGrid.addRow(newRow, 0);
        verificationGrid.beginEditCell(newRow, "workContent");
    } else if (name == 'xp') {
        batchGrid.addRow(newRow, 0);
        batchGrid.beginEditCell(newRow, "workContent");
    }

}

function removeProductMessage(name) {

    if (name == 'xn') {
        var rows = performznceGrid.getSelecteds();
    } else if (name == 'zj') {
        var rows = productGrid.getSelecteds();
    } else if (name == 'bp') {
        var rows = assemblyGrid.getSelecteds();
    } else if (name == 'yz') {
        var rows = verificationGrid.getSelecteds();
    } else if (name == 'xp') {
        var rows = batchGrid.getSelecteds();
    }
    if (rows.length > 0) {
        for (var i = 0; i < rows.length; i++) {
            if(rows[i].CREATE_BY_ != null && rows[i].CREATE_BY_ != currentUserId){
                mini.alert("非自己创建的不能删除");
                return;
            }
        }
        mini.showMessageBox({
            title: "提示信息！",
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: "是否确定删除！",
            callback: function (action) {
                if (action == "ok") {
                    if (name == 'xn') {
                        performznceGrid.removeRows(rows, false);
                    } else if (name == 'zj') {
                        productGrid.removeRows(rows, false);
                    } else if (name == 'bp') {
                        assemblyGrid.removeRows(rows, false);
                    } else if (name == 'yz') {
                        verificationGrid.removeRows(rows, false);
                    } else if (name == 'xp') {
                        batchGrid.removeRows(rows, false);
                    }

                }
            }
        });
    } else {
        mini.alert("请至少选中一条记录");
        return;
    }
}


function getProcessNodeVars() {
    var nodeVarsObj = {};
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        nodeVarsObj[nodeVars[i].KEY_] = nodeVars[i].DEF_VAL_;
    }

    return nodeVarsObj;
}



//保存草稿
function saveManage(e) {

    // 校验
    var formValid = validProductManage();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    //查询该型号是否存在，有则不允许提交
    var designType = $.trim(mini.get("designType").getValue())
    var postData = {designType: designType, manageId: manageId};
    $.ajax({
        url: jsUseCtxPath + '/xcpdr/core/cpkfgk/selectproductTypeNum.do',
        type: 'post',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {

            if (parseInt(data.num) > 0) {
                mini.alert('已有该设计型号，请勿重复提交哦！');
                return;
            } else {
                window.parent.saveDraft(e);

            }
        }
    });


}


//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formProductManage");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var standardName = $.trim(mini.get("standardName").getValue())
    if (!standardName) {
        return {"result": false, "message": "请填写标准名称"};
    }

    return {"result": true};
}

//下一步时数据是否有效
function approveValid() {
    var nodeVarsObj = getProcessNodeVars();
    if (nodeVarsObj.nodeTask == '编制') {
        return draftOrStartValid();
    } else if (nodeVarsObj.nodeTask == '反馈') {
        var standardUserOpinion = $.trim(mini.get("standardUserOpinion").getValue());
        if (!standardUserOpinion || standardUserOpinion.length > 800) {
            return {"result": false, "message": "请填写800字以内的‘标准化意见’！"};
        }
    }

    return {"result": true};
}

//启动流程
function startManageProcess(e) {

    // 校验
    var formValid = validProductManage();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    //查询该型号是否存在，有则不允许提交
    var designType = $.trim(mini.get("designType").getValue())
    var postData = {designType: designType, manageId: manageId};
    $.ajax({
        url: jsUseCtxPath + '/xcpdr/core/cpkfgk/selectproductTypeNum.do',
        type: 'post',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {

            if (parseInt(data.num) > 0) {
                mini.alert('已有该设计型号，请勿重复提交哦！');
                return;
            } else {
                window.parent.startProcess(e);

            }
        }
    });

}

//审批或者下一步
function manageApprove() {

    // 校验
    var formValid = validProductManage();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

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

//检验表单是否必填
function validProductManage() {
    if (action == 'edit' || isCpkfjh == "yes") {
        var designType = $.trim(mini.get("designType").getValue())
        if (!designType) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var performznceStartTime = $.trim(mini.get("performznceStartTime").getValue())
        if (!performznceStartTime) {
            return {"result": false, "message": "请填写性能需求调研开始时间"};
        }
        var performznceEndTime = $.trim(mini.get("performznceEndTime").getValue())
        if (!performznceEndTime) {
            return {"result": false, "message": "请填写性能需求调研结束时间"};
        }
        var productStartTime = $.trim(mini.get("productStartTime").getValue())
        if (!productStartTime) {
            return {"result": false, "message": "请填写产品设计开始时间"};
        }
        var productEndTime = $.trim(mini.get("productEndTime").getValue())
        if (!productEndTime) {
            return {"result": false, "message": "请填写产品设计结束时间"};
        }
        var assemblyStartTime = $.trim(mini.get("assemblyStartTime").getValue())
        if (!assemblyStartTime) {
            return {"result": false, "message": "请填写部件设计开始时间"};
        }
        var assemblyEndTime = $.trim(mini.get("assemblyEndTime").getValue())
        if (!assemblyEndTime) {
            return {"result": false, "message": "请填写部件设计结束时间"};
        }
        var verificationStartTime = $.trim(mini.get("verificationStartTime").getValue())
        if (!verificationStartTime) {
            return {"result": false, "message": "请填写首台验证开始时间"};
        }
        var verificationEndTime = $.trim(mini.get("verificationEndTime").getValue())
        if (!verificationEndTime) {
            return {"result": false, "message": "请填写首台验证结束时间"};
        }
        var batchStartTime = $.trim(mini.get("batchStartTime").getValue())
        if (!batchStartTime) {
            return {"result": false, "message": "请填写小批量产开始时间"};
        }
        var batchEndTime = $.trim(mini.get("batchEndTime").getValue())
        if (!batchEndTime) {
            return {"result": false, "message": "请填写小批量产结束时间"};
        }

        var productSupervisorId = $.trim(mini.get("productSupervisorId").getValue())
        if (!productSupervisorId) {
            return {"result": false, "message": "请填写技术参数人员名称"};
        }

    }

    if ( isCszd == "yes") {
        var tractorParameterId = $.trim(mini.get("tractorParameterId").getValue())
        if (!tractorParameterId) {
            return {"result": false, "message": "请填写整机工艺参数人员名称"};
        }
    }

    if(isZjcsy == "yes"){
        var partsTechnologyId = $.trim(mini.get("partsTechnologyId").getValue())
        if (!partsTechnologyId) {
            return {"result": false, "message": "请填写零部件技术参数人员"};
        }
    }

    if ( isLbjcszd == "yes") {
        var craftAssemblyId = $.trim(mini.get("craftAssemblyId").getValue())
        if (!craftAssemblyId) {
            return {"result": false, "message": "请填写零部件工艺参数人员"};
        }
    }
    return {"result": true};

}



