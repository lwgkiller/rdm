$(function () {
    if (jxxqxfId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/jxxqxf/getJxxqxfDetail.do?jxxqxfId="+jxxqxfId;
        $.ajax({
            url:url,
            method:'get',
            success:function (json) {
                jxxqxfForm.setData(json);
            }
        });
    }
    //    不同场景的处理
    if(action=='detail') {
        jxxqxfForm.setEnabled(false);
    } else if(action=='edit' || action=='add') {
        mini.get("submit").show();
    }
});

function submit() {
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.issueDepartmentId = mini.get("issueDepartmentId").getValue();
    postData.issueDepartment = mini.get("issueDepartmentId").getText();
    postData.productDepartmentId = mini.get("productDepartmentId").getValue();
    postData.productDepartment = mini.get("productDepartmentId").getText();
    postData.versionType = mini.get("versionType").getValue();
    postData.productType = mini.get("productType").getValue();
    postData.materialCode = $.trim(mini.get("materialCode").getValue());
    postData.salesModel = $.trim(mini.get("salesModel").getValue());
    postData.designModel = $.trim(mini.get("designModel").getValue());
    postData.pinCode = $.trim(mini.get("pinCode").getValue());
    postData.priority = mini.get("priority").getValue();
    postData.passBack = mini.get("passBack").getValue();
    postData.passBackNum = mini.get("passBackNum").getValue();
    postData.remark = $.trim(mini.get("remark").getValue());
    //检查必填项
    var checkResult=commitValidCheck(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/serviceEngineering/core/jxxqxf/saveJxxqxf.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, jxxqxfEdit_name, function () {
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
    if(!postData.issueDepartmentId) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name1;
        return checkResult;
    }
    if(!postData.productDepartmentId) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name2;
        return checkResult;
    }
    if (!postData.versionType) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name3;
        return checkResult;
    }
    if (!postData.productType) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name4;
        return checkResult;
    }
    if (!postData.materialCode) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name5;
        return checkResult;
    }
    if (!postData.salesModel) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name6;
        return checkResult;
    }
    if (!postData.designModel) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name7;
        return checkResult;
    }
    if (!postData.pinCode) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name8;
        return checkResult;
    }
    if (!postData.priority) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name9;
        return checkResult;
    }
    if (!postData.passBack) {
        checkResult.success=false;
        checkResult.reason=jxxqxfEdit_name10;
        return checkResult;
    }
    checkResult.success=true;
    return checkResult;
}

