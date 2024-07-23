$(function () {
});
//打开标准的编辑页面（新增、编辑、明细）
function openStandardEditWindow(standardId,systemId, action) {

    mini.open({
        title: "标准编辑",
        url: jsUseCtxPath + "/standardManager/core/standard/edit.do?standardId=" + standardId + '&action=' + action + '&systemCategoryId=JS',
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var data = {systemTreeData: systemTree.getData(),isPointManager:isPointManager,
                tabName:tabName,currentUserSubManager:currentUserSubManager};  //传递上传参数
            iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {
            searchStandard();
        }
    });
}
function statusRenderer(e) {
    var record = e.record;
    var status = record.standardStatus;

    var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
        {'key': 'enable', 'value': '有效', 'css': 'green'},
        {'key': 'disable', 'value': '已废止', 'css': 'red'}
    ];

    return $.formatItemValue(arr, status);
}
function addItems() {
    var action = "add";
    let url = jsUseCtxPath + "/wwrz/core/standardMng/editPage.do?action=" + action+"&standardType="+standardType;
    var title = "新增";
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//查看
function editForm(id, action) {
    var url = jsUseCtxPath + "/wwrz/core/standardMng/editPage.do?action=" + action + "&id=" + id;
    var title = "修改";
    if (action == 'view') {
        title = "查看";
    }
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//删除记录
function removeRow() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定取消选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.mainId);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/standardMng/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }

        }
    });
}


function exportExcel() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}


function fileRenderer(e) {
    var record = e.record;
    var standardId = record.id;
    var existFile = record.existFile;
    var standardName = record.standardName;
    var standardNumber = record.standardNumber;
    var status = record.standardStatus;
    var categoryName=record.categoryName;
    var systemCategoryId = record.systemCategoryId;
    if (existFile) {
        var s = '<span title="预览"  onclick="previewStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber+ '\',\'' + categoryName + '\',\'' + status + '\',\'' + coverContent + '\',\'' + systemCategoryId + '\')">预览</span>';
        s += '<span title="下载" onclick="downloadStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\')">下载</span>';
        return s;
    } else {
        var s = '<span title="预览"  style="color: silver">预览</span>';
        s += '<span title="下载" style="color: silver">下载</span>';
        return s;
    }
}
//预览标准全文
function previewStandard(standardId, standardName, standardNumber, categoryName, status,coverConent,systemCategoryId) {
    if(status=='disable') {
        mini.confirm("该标准已废止，确定预览吗？","提示",function (action) {
            if(action=="ok") {
                previewStandardDo(standardId, standardName, standardNumber, categoryName, status,coverConent,systemCategoryId);
            }
        });
    } else {
        previewStandardDo(standardId, standardName, standardNumber, categoryName, status,coverConent,systemCategoryId);
    }

}

function previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent,systemCategoryId) {
    var resultCodeId = judgePreviewNeedApply(standardId, categoryName);
    if (resultCodeId.result == '0' || resultCodeId.result == '1') {
        if (resultCodeId.result == '1') {
            changeApplyUseStatus(resultCodeId.applyId, 'yes');
        }
        //记录预览情况
        recordStandardOperate('preview', standardId);
        var previewUrl = jsUseCtxPath + "/standardManager/core/standard/preview.do?standardId=" + standardId;
        if(systemCategoryId=='JS'){
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&plate=bz&path="+jsUseCtxPath+"&recordId="+standardId+"&file=" + encodeURIComponent(previewUrl));
        }else{
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&path="+jsUseCtxPath+"&recordId="+standardId+"&file=" + encodeURIComponent(previewUrl));
        }

    } else if (resultCodeId.result == '2') {
        mini.alert('请在“标准预览申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
    } else if (resultCodeId.result == '3') {
        //跳转到新增预览申请界面
        mini.confirm("当前操作需要提交预览申请，确定继续？","权限不足",function (action) {
            if(action=="ok") {
                addApply('preview', standardId, standardName);
            }
        });
    }
}

//下载标准全文
function downloadStandard(standardId, standardName, standardNumber, categoryName, status) {
    if(status=='disable') {
        mini.confirm("该标准已废止，确定下载吗？","提示",function (action) {
            if(action=="ok") {
                downloadStandardDo(standardId, standardName, standardNumber, categoryName, status);
            }
        });
    }
    else {
        downloadStandardDo(standardId, standardName, standardNumber, categoryName, status);
    }
}

function downloadStandardDo(standardId, standardName, standardNumber, categoryName, status) {
    var resultCodeId = judgeDownloadNeedApply(standardId, categoryName);
    if (resultCodeId.result == '0' || resultCodeId.result == '1') {
        if (resultCodeId.result == '1') {
            changeApplyUseStatus(resultCodeId.applyId, 'yes');
        }
        //记录下载情况
        recordStandardOperate('download', standardId);
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/standardManager/core/standard/download.do");
        var standardIdAttr = $("<input>");
        standardIdAttr.attr("type", "hidden");
        standardIdAttr.attr("name", "standardId");
        standardIdAttr.attr("value", standardId);
        var standardNameAttr = $("<input>");
        standardNameAttr.attr("type", "hidden");
        standardNameAttr.attr("name", "standardName");
        standardNameAttr.attr("value", standardName);
        var standardNumberAttr = $("<input>");
        standardNumberAttr.attr("type", "hidden");
        standardNumberAttr.attr("name", "standardNumber");
        standardNumberAttr.attr("value", standardNumber);
        $("body").append(form);
        form.append(standardIdAttr);
        form.append(standardNameAttr);
        form.append(standardNumberAttr);
        form.submit();
        form.remove();
    } else if (resultCodeId.result == '2') {
        mini.alert('已存在审批中的申请，请在“标准流程管理”--“标准下载申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
    } else if (resultCodeId.result == '3') {
        //跳转到新增下载申请界面
        mini.confirm("当前操作会创建一个下载申请单，审批完成后可在此处或“标准流程管理”--“标准下载申请”页面点击“下载”按钮下载标准，确定继续？","权限不足",function (action) {
            if(action=="ok") {
                addApply('download', standardId, standardName);
            }
        });
    }
}
//判断预览是否需要申请。返回值：0--不需要申请，1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请
function judgePreviewNeedApply(standardId,categoryName) {
    debugger
    if(categoryName!='企业标准') {
        return {result: '0'};
    }
    var applyCategoryId='preview';
    //标准管理领导不需要申请
    var isLeader = whetherIsLeader(currentUserRoles);
    if (isLeader) {
        return {result: '0'};
    }
    //技术标准管理人员不需要申请
    var JSSystemStandardManager=whetherIsPointStandardManager('JS',currentUserRoles);
    if(JSSystemStandardManager) {
        return {result: '0'};
    }
    //非管理职级的人员预览也不需要
    var isGLMan = whetherIsGLMan(currentUserZJ);
    if(!isGLMan) {
        return {result: '0'};
    }
    //管理职级的人员预览非技术类的不需要
    if(isGLMan&&tabName!='JS') {
        return {result: '0'};
    }
    //其他场景都需要判断是否已经有申请单
    var resultCodeId = {result: '3'};
    $.ajax({
        url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
        type: 'POST',
        data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
        contentType: 'application/json',
        async: false,
        success: function (data) {
            if (data) {
                resultCodeId.result = data.result;
                resultCodeId.applyId = data.applyId;
            }
        }
    });
    return resultCodeId;
}
//判断下载是否需要申请。返回值：0--不需要申请，1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请
function judgeDownloadNeedApply(standardId,categoryName) {
    var applyCategoryId='download';
    //如果技术标准的类型不是企业标准，则允许下载
    if(tabName=='JS') {
        if(categoryName!='企业标准') {
            return {result: '0'};
        } else {
            //其他场景都需要判断是否已经有申请单
            var resultCodeId = {result: '3'};
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
                type: 'POST',
                data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    if (data) {
                        resultCodeId.result = data.result;
                        resultCodeId.applyId = data.applyId;
                    }
                }
            });
            return resultCodeId;
        }
    } else {
        //标准管理领导不需要申请
        var isLeader = whetherIsLeader(currentUserRoles);
        if (isLeader) {
            return {result: '0'};
        }
        //本体系类别的标准管理人员下载不需要申请
        var selfSystemStandardManager=whetherIsPointStandardManager(tabName,currentUserRoles);
        if(selfSystemStandardManager) {
            return {result: '0'};
        }
        //其他场景都需要判断是否已经有申请单
        var resultCodeId = {result: '3'};
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
            type: 'POST',
            data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
            contentType: 'application/json',
            async: false,
            success: function (data) {
                if (data) {
                    resultCodeId.result = data.result;
                    resultCodeId.applyId = data.applyId;
                }
            }
        });
        return resultCodeId;
    }
}
function recordStandardOperate(action, standardId) {
    $.ajax({
        url: jsUseCtxPath + "/standardManager/core/standard/record.do?standardId=" + standardId + "&action=" + action,
        method: 'GET',
        success: function (data) {

        }
    });
}
function changeApplyUseStatus(applyId, useStatus) {
    $.ajax({
        url: jsUseCtxPath + "/standardManager/core/standardApply/changeUseStatus.do?applyId=" + applyId + "&useStatus=" + useStatus,
        method: 'GET',
        success: function () {

        }
    });
}
//新增标准申请
function addApply(applyCategoryId, standardId, standardName) {
    var title = "新增预览申请";
    if (applyCategoryId == 'download') {
        title = "新增下载申请";
    }
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath + "/bpm/core/bpmInst/BZGLSQ/start.do?standardApplyCategoryId=" + applyCategoryId + "&standardId=" + standardId,
        title: title,
        width: width,
        height: height,
        showMaxButton: true,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (standardListGrid) {
                standardListGrid.reload();
            }
        }
    });
}
