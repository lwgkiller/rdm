$(function () {
    if(gjjsObj) {
        formGjjs.setData(gjjsObj);
        jiShuListGrid.reload();
    }
    $.ajaxSettings.async = false;
    if(action == 'detail'){
        formGjjs.setEnabled(false);
        mini.get("saveNewGjjs").hide();
        mini.get("addJishu").hide();
        mini.get("saveJishu").hide();
        mini.get("deleteJishu").hide();
    }

    $.ajaxSettings.async = true;
});

//创建保存
function saveNewGjjs() {
    var postData = {};
    postData.pId =mini.get("pId").getValue();
    postData.jdTime =mini.get("jdTime").getText();
    postData.proName =mini.get("proName").getValue();
    postData.proRespUserName =mini.get("proRespUserId").getText();
    postData.proRespUserId =mini.get("proRespUserId").getValue();
    // 检查必填项
    var checkResult = commitValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }
    //保存技术
    var postDataJishu = jiShuListGrid.data;
    if(postDataJishu && postDataJishu.length!=0) {
        for(var index=0;index<postDataJishu.length;index++) {
            var oneJishu=postDataJishu[index];
            if(!$.trim(oneJishu.jsName) || !$.trim(oneJishu.jssp)) {
                mini.alert("请填写第"+(index+1)+"条技术中的必填项！");
                return;
            }
        }
        postData.jishuArr=jiShuListGrid.data;
    }

    $.ajax({
        url: zlUseCtxPath + "/zhgl/core/gjjs/saveNewGjjsData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        // window.close();
                        var url=zlUseCtxPath+"/zhgl/core/gjjs/gjjsPage.do?pId="+returnData.data+"&action=edit";
                        window.location.href=url;
                    }
                });
            }
        }
    });
}
//查新报告附件
function addBaoGaoFile() {
    var fjlx="cxbg";
    if (gjjsObj){
        var meetingId = gjjsObj.pId;
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
            url: zlUseCtxPath + "/zhgl/core/gjjs/gjjsUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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
//鉴定证书附件
function addZhengShuFile() {
    var fjlx="jdzs";
    if (gjjsObj){
        var meetingId = gjjsObj.pId;
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
            url: zlUseCtxPath + "/zhgl/core/gjjs/gjjsUploadWindow.do?meetingId=" + meetingId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent+"&fjlx="+fjlx,
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

function addJishu() {
    var meetingId = mini.get("pId").getValue();
    if (!meetingId) {
        mini.alert("请先点击上方‘保存’按钮进行项目保存！");
        return;
    }else {
        var row={};
        // jiShuListGrid.addRow(row);
        addRowGrid("jiShuListGrid", row);
    }
}
function saveJishu() {
    var meetingId = mini.get("pId").getValue();
    if (!meetingId) {
        mini.alert("请先点击上方‘保存’按钮进行项目保存！");
        return;
    }
    var postData = jiShuListGrid.data;
    if(!postData || postData.length==0) {
        mini.alert("保存成功！");
        return;
    }
    for(var index=0;index<postData.length;index++) {
        var oneJishu=postData[index];
        if(!$.trim(oneJishu.jsName) || !$.trim(oneJishu.jssp)) {
            mini.alert("请填写第"+(index+1)+"条技术中的必填项！");
            return;
        }
    }
    var postDataJson = mini.encode(postData);
    $.ajax({
        url: zlUseCtxPath + "/zhgl/core/gjjs/saveJishu.do?meetingId="+meetingId,
        type: 'POST',
        contentType: 'application/json',
        async:false,
        data: postDataJson,
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        jiShuListGrid.reload();
                    }
                });
            }
        }
    });
}
function deleteJishu() {
    var row = jiShuListGrid.getSelecteds();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    if (row.id == "") {
        delRowGrid("jiShuListGrid");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = row.length; i < l; i++) {
                var r = row[i];
                rowIds.push(r.xId);
            }
            _SubmitJson({
                url: zlUseCtxPath + "/zhgl/core/gjjs/deleteJiShuData.do",
                method: 'POST',
                data: {ids: rowIds.join(',')},
                success: function (returnData) {
                    if (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    jiShuListGrid.reload();
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
    if (!postData.jdTime) {
        checkResult.success = false;
        checkResult.reason = '请填写鉴定时间！';
        return checkResult;
    }
    if (!postData.proName) {
        checkResult.success = false;
        checkResult.reason = '请填写项目名称！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}
