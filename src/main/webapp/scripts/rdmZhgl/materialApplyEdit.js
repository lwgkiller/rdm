var editForm = '';
var isFirstNode = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
            setButtonStatus(false);
        } else {
            setCellEnabled(ApplyObj.applyType);
        }
        applyForm.setData(ApplyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
        setButtonStatus(false);
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
        setCellEnabled(ApplyObj.applyType);
    }
    setLedgerAccountShow(ApplyObj.applyType);
    grid_detail.setData(ApplyObj.detailList);
});

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function setLedgerAccount(type) {
    if (type != 'Z55-chy' && type != 'Z53-chy') {
        return;
    }
    var lederAccount = mini.get("ledgerAccount");
    //总账科目可选项
    if (type == 'Z55-chy') {
        // 研发支出-费用化研发支出-新产品试制开发费
        lederAccount.setValue("5300000019");
    } else if (type=='Z53-chy') {
        // 研发支出-资本化研发支出-新产品试制开发费
        lederAccount.setValue("1799000016");
    }
}
function applyTypeChange(e) {
    var type = e.value;
    setCellEnabled(type);
    setLedgerAccountShow(type);
    mini.get("ledgerAccount").setValue("");
    setLedgerAccount(type);
}
function setButtonStatus(flag) {
    mini.get('addDetailButton').setEnabled(flag);
    mini.get('delDetailButton').setEnabled(flag);
    grid_detail.setAllowCellEdit(flag);
}

function setLedgerAccountShow(type) {
    $("#ledgerAccountTitle").css("display","none");
    if (type == 'Z53-chy' || type == 'Z55-chy') {
        $("#ledgerAccountTitle").css("display","table-row");
    }
}
function setCellEnabled(type) {
    if(type == 'Z53-chy' || type == 'Z55-chy'||type == 'Z53-rcy' || type == 'Z55-rcy'||type=='Z56'){
        mini.get('costCenter').setEnabled(true);
        mini.get('orderCode').setEnabled(true);
        mini.get('storageLocation').setEnabled(false);
    }else if(type=='Z27'){
        mini.get('costCenter').setEnabled(true);
        mini.get('orderCode').setEnabled(false);
        mini.get('storageLocation').setEnabled(false);
    }else if(type=='311'){
        mini.get('costCenter').setEnabled(false);
        mini.get('orderCode').setEnabled(false);
        mini.get('storageLocation').setEnabled(true);
    }else if(type=='all'){
        mini.get('costCenter').setEnabled(false);
        mini.get('orderCode').setEnabled(false);
        mini.get('storageLocation').setEnabled(false);
    }else{
        mini.get('costCenter').setEnabled(false);
        mini.get('orderCode').setEnabled(false);
        mini.get('storageLocation').setEnabled(false);
    }
}


//获取环境变量
function getProcessNodeVars() {
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'editForm') {
            editForm = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFirstNode') {
            isFirstNode = true;
        }
    }
}

//保存草稿
function saveApplyInfo(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    formData.detailData = grid_detail.getChanges();
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var applyType = $.trim(mini.get("applyType").getValue());
    if (!applyType) {
        return {"result": false, "message": "请选择物料类型"};
    }
    if (applyType.startsWith("Z55") || applyType.startsWith("Z53")) {
        applyType = applyType.slice(0, 3);
    }
    if(applyType=='Z53'||applyType=='Z55'||applyType=='Z56'){
        var costCenter = $.trim(mini.get("costCenter").getValue());
        var orderCode = $.trim(mini.get("orderCode").getValue());
        if (!costCenter) {
            return {"result": false, "message": "请选择成本中心"};
        }
        if (!orderCode) {
            return {"result": false, "message": "请填写财务订单号"};
        }
        $.ajaxSettings.async = false;
        var url = jsUseCtxPath + "/rdmZhgl/core/material/checkOrderCode.do?orderCode=" + orderCode;
        var checkResult = {};
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
    }else if(applyType=='Z27'){
        var costCenter = $.trim(mini.get("costCenter").getValue());
        if (!costCenter) {
            return {"result": false, "message": "请选择成本中心"};
        }
    }else if(applyType=='311'){
        var storageLocation = $.trim(mini.get("storageLocation").getValue());
        if (!storageLocation) {
            return {"result": false, "message": "请填写收货存储地点"};
        }
    }
    var reason = $.trim(mini.get("reason").getValue());
    if (!reason) {
        return {"result": false, "message": "请填写领用事由！"};
    }
    var detailList = grid_detail.getData();
    for (var i = 0; i < detailList.length; i++) {
        if (!detailList[i].itemCode) {
            return {"result": false, "message": "请填写物料号！"};
            break
        }
        if (!detailList[i].itemName) {
            return {"result": false, "message": "请填写物料描述！"};
            break
        }
        if (!detailList[i].totalNum) {
            return {"result": false, "message": "请填写总数量！"};
            break
        }
        if(applyType=='311'){
            if (!detailList[i].storage) {
                return {"result": false, "message": "请填写存储地点！"};
                break
            }
        }
    }

    return {"result": true};
}

//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function applyApprove() {
    if(currentDay=='1'){
        mini.alert("每月1号SAP账号锁定，不允许操作！");
        return;
    }
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var detailList = grid_detail.getData();
    if(detailList.length==0){
        mini.alert("请添加物料信息！");
        return;
    }
    window.parent.approve();
}

function addDetail() {
    var row = {};
    var data = [{}, {}, {}];
    // grid_detail.setData(data);
    grid_detail.addRow(row);
}

function delDetail() {
    var selecteds = grid_detail.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_detail.removeRows(deleteArr);
}


function saveDetailData() {
    var data = grid_detail.getChanges();
    var needReload = true;
    var applyId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].applyId = applyId;
            if (data[i]._state == 'removed') {
                continue;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/rdmZhgl/core/material/dealDetailData.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    if (data && data.success) {
                        window.location.reload();
                    } else {
                        mini.alert(data.message);
                    }
                }
            });
        }
    }
}


