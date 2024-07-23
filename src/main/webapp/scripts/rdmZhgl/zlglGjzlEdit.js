var achieveTypeList = "";
$(function () {
    if(gjzlObj) {
        formGjzl.setData(gjzlObj);
    }
    $.ajaxSettings.async = false;
    queryDimension();
    queryDimension2();
    queryDimension3();
    if(action == 'detail'){
        formGjzl.setEnabled(false);
        mini.get("saveNewGjzl").hide();
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
});

//案件状态下拉数据
function queryDimension() {
    var type="当前状态";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#gjztId").load(data);
            }
        }
    });
}
//是否
function queryDimension2() {
    var type="yes/no";
    $.ajax({
        url: zlUseCtxPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#appliedNational").load(data);
                mini.get("#nationalStage").load(data);
            }
        }
    });
}
//缴费类别下拉数据
function queryDimension3() {
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

//创建保存国际专利
function saveNewGjzl() {
    var postData = {};
    postData.gjzlId =mini.get("gjzlId").getValue();
    postData.pctName =mini.get("pctName").getValue();
    postData.englishName =mini.get("englishName").getValue();
    postData.applictonNumber =mini.get("applictonNumber").getValue();
    postData.gjztId =mini.get("gjztId").getValue();
    postData.zlztName =mini.get("gjztId").getText();
    postData.applictonDay =mini.get("applictonDay").getText();
    postData.openDay =mini.get("openDay").getText();
    postData.openNumber =mini.get("openNumber").getValue();
    postData.theCountry =mini.get("theCountry").getValue();
    postData.nationalNamber =mini.get("nationalNamber").getValue();
    postData.nationOpenDay =mini.get("nationOpenDay").getValue();
    postData.nationOpenNumbei =mini.get("nationOpenNumbei").getText();
    postData.authorizedNumber =mini.get("authorizedNumber").getValue();
    postData.authorizedDay =mini.get("authorizedDay").getText();
    postData.theInventor =mini.get("theInventor").getValue();
    postData.thePatentee_theApplicane =mini.get("thePatentee_theApplicane").getValue();
    postData.theAgency =mini.get("theAgency").getValue();
    postData.appliedNational =mini.get("appliedNational").getValue();
    postData.correspondingNumber =mini.get("correspondingNumber").getValue();
    postData.patentName =mini.get("patentName").getValue();
    postData.nationalStage =mini.get("nationalStage").getValue();
    postData.rewardAmount =mini.get("rewardAmount").getText();
    postData.rewardTime =mini.get("rewardTime").getValue();
    postData.myCompanyUserIds =mini.get("myCompanyUserIds").getValue();
    postData.myCompanyUserNames =mini.get("myCompanyUserIds").getText();
    // 检查必填项
    var checkResult = commitValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }
    $.ajax({
        url: zlUseCtxPath + "/zhgl/core/zlgl/saveNewGjzlData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        // window.close();
                        var url=zlUseCtxPath+"/zhgl/core/zlgl/gjzlPage.do?gjzlId="+returnData.data+"&action=edit";
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
    if (gjzlObj){
        var meetingId = gjzlObj.gjzlId;
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
function addJiaoFeiFile() {
    var fjlx="jffj";
    if (gjzlObj){
        var meetingId = gjzlObj.gjzlId;
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

function addGJJiaoFei() {
    var meetingId = mini.get("gjzlId").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存’！");
        return;
    }else {
        var row={};
        jiaoFeiListGrid.addRow(row);
    }
}
function saveGJJiaoFei() {
    var meetingId = mini.get("gjzlId").getValue();
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
    if (!postData.applictonNumber) {
        checkResult.success = false;
        checkResult.reason = '请填写国际申请号！';
        return checkResult;
    }
    if (!postData.theInventor) {
        checkResult.success = false;
        checkResult.reason = '请填写发明人！';
        return checkResult;
    }
    if (!postData.thePatentee_theApplicane) {
        checkResult.success = false;
        checkResult.reason = '请填写专利权人/申请人！';
        return checkResult;
    }
    if (!postData.patentName) {
        checkResult.success = false;
        checkResult.reason = '请填写专利名称！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}
function initForm() {
    var meetingId = gjzlObj.gjzlId;
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