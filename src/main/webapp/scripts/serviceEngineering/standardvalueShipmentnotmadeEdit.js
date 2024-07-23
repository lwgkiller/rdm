$(function () {
    if (zzcsbId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/getStandardvalueShipmentnotmadeDetail.do?zzcsbId="+zzcsbId;
        $.ajax({
            url:url,
            method:'get',
            success:function (json) {
                zzcsbForm.setData(json);
            }
        });
    }
    //    不同场景的处理
    if(action=='detail') {
        zzcsbForm.setEnabled(false);
    } else if(action=='edit' || action=='add') {
        mini.get("submit").show();
        mini.get("betaCompletion").setEnabled(false);
    }
});

function submit() {
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.departmentId = mini.get("departmentId").getValue();
    postData.department = mini.get("departmentId").getText();
    postData.salesModel = $.trim(mini.get("salesModel").getValue());
    postData.materialCode = $.trim(mini.get("materialCode").getValue());
    postData.materialName = $.trim(mini.get("materialName").getValue());
    postData.versionType = mini.get("versionType").getValue();
    postData.pinFour = $.trim(mini.get("pinFour").getValue());
    postData.principalId = mini.get("principalId").getValue();
    postData.principal = mini.get("principalId").getText();
    postData.betaCompletion = mini.get("betaCompletion").getValue();
    postData.responseStatus = mini.get("responseStatus").getValue();
    postData.responseTime = mini.get("responseTime").getValue();
    //检查必填项
    var checkResult=commitValidCheck(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/saveBusiness.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, standardvalueShipmentnotmadeEdit_name, function () {
                    if (returnData.success) {
                        CloseWindow()
                    }
                });
            }
        }
    });
}

function commitValidCheck(postData) {
    var checkResult={};
    if(!postData.departmentId) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name1;
        return checkResult;
    }
    if (!postData.salesModel) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name2;
        return checkResult;
    }
    if (!postData.materialCode) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name3;
        return checkResult;
    }
    if (!postData.materialName) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name4;
        return checkResult;
    }
    if (!postData.versionType) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name5;
        return checkResult;
    }
    if (!postData.pinFour) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name6;
        return checkResult;
    }
    if (!postData.principalId) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name7;
        return checkResult;
    }
    if (!postData.betaCompletion) {
        checkResult.success=false;
        checkResult.reason=standardvalueShipmentnotmadeEdit_name8;
        return checkResult;
    }
    checkResult.success=true;
    return checkResult;
}

//校验是否重复
function validReptition() {
    var valid = true;
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.materialCode = $.trim(mini.get("materialCode").getValue());
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/standardvalue/shipmentnotmade/validRepetition.do',
        type: 'post',
        async: false,
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            if(!data.success) {
                valid = false;
            }
        }
    });
    return valid
}

