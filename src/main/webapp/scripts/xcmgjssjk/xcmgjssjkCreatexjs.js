$(function () {
    if(jssjkObj) {
        formJssjk.setData(jssjkObj);
    }
    //明细入口
    if (action == 'detail') {
        formJssjk.setEnabled(false);
        // mini.get("saveNewjsDraft").setEnabled(false);
        mini.get("saveNewjsDraft").hide();
    }
    //新增给项目负责人默认值
    if(!action){
        mini.get("XMFZR_NAME").setValue(xmfzrid);
        mini.get("XMFZR_NAME").setText(xmfzrName);
    }
    if(action){
        mini.get("XMFZR_NAME").setText(jssjkObj.XMFZR_NAME);
    }

});
//创建新技术保存
function saveNewjsDraft() {
    var postData = {};
    postData.jssjkId =mini.get("jssjkId").getValue();
    postData.xmbh =mini.get("XMBH").getValue();
    postData.xmmc =mini.get("XMMC").getValue();
    if(!action){
        postData.xmfzrId =mini.get("XMFZR_NAME").getValue();
        postData.xmfzrName =mini.get("XMFZR_NAME").getText();
    }else {
        postData.xmfzrName =mini.get("XMFZR_NAME").getValue();
        postData.xmfzrId =mini.get("XMFZR_ID").getValue();
    }
    postData.lxfs =mini.get("LXDH").getValue();
    postData.kfzt =mini.get("KFZT").getValue();
    postData.xmlb =mini.get("XMLB").getValue();
    postData.npibhxx =mini.get("NPIBHXX").getValue();
    postData.jsms =mini.get("JSMS").getValue();
    postData.tdhmc =mini.get("TDHMC").getValue();
    postData.jyjx =mini.get("JYJX").getValue();
    postData.recordStatus = '草稿';
    //检查必填项
    var checkResult = commitValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }
    $.ajax({
        url: jsUseCtxPath + "/jssj/core/config/saveNewjsDataDraft.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        // var url = jsUseCtxPath + "/jssj/core/config/cjxjsConfigPage.do?jssjkId=" + returnData.data + "&action=edit";
                        // window.location.href = url;
                        window.close();
                    }
                });
            }
        }
    });
}
//实施计划附件
function addNjsFile() {
    var fjlx="ssjh";
    if (jssjkObj){
        var meetingId = jssjkObj.UUID;
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
            url: jsUseCtxPath + "/jssj/core/config/jsjkUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
//项目章程附件
function addNjsFile2() {
    var fjlx="xmzc";
    if (jssjkObj){
        var meetingId = jssjkObj.UUID;
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
            url: jsUseCtxPath + "/jssj/core/config/jsjkUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
//应用案例附件
function addNjsFile3() {
    var fjlx="yyal";
    if (jssjkObj){
        var meetingId = jssjkObj.UUID;
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
            url: jsUseCtxPath + "/jssj/core/config/jsjkUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
function commitValidCheck(postData) {
    var checkResult = {};
    if (!postData.xmbh) {
        checkResult.success = false;
        checkResult.reason = '请填写项目编码！';
        return checkResult;
    }
    if (!postData.xmmc) {
        checkResult.success = false;
        checkResult.reason = '请填写项目名称！';
        return checkResult;
    }
    if (!postData.xmfzrId) {
        checkResult.success = false;
        checkResult.reason = '请填写项目负责人！';
        return checkResult;
    }
    if (!postData.lxfs) {
        checkResult.success = false;
        checkResult.reason = '请填写联系方式！';
        return checkResult;
    }
    if (!postData.kfzt) {
        checkResult.success = false;
        checkResult.reason = '请选择开发状态！';
        return checkResult;
    }
    if (!postData.xmlb) {
        checkResult.success = false;
        checkResult.reason = '请选择项目类别！';
        return checkResult;
    }
    if (!postData.npibhxx) {
        checkResult.success = false;
        checkResult.reason = '请填写npi编号信息！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}