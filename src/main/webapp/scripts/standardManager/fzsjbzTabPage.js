$(function () {
    //是否是技术标准管理员
    var JSSystemStandardManager=whetherIsPointStandardManager('JS',currentUserRoles);
    if(tabName=='JS' && !JSSystemStandardManager) {
        mini.get("exportBtn").hide();
    }

    querySelectInfos();
    mini.get("standardNumber").setValue(standardNumberFilter);
    mini.get("standardName").setValue(standardNameFilter);
    mini.get("category").setValue(standardCategoryFilter);
    mini.get("publishTimeFrom").setValue(publishTimeFromFilter);
    mini.get("publishTimeTo").setValue(publishTimeToFilter);
    mini.get("status").setValue(standardStatusFilter);
    // mini.get("standardField").setValue(fieldId);
    searchStandard();
    //初始隐藏一些列，以列的name去查找column
    var sendSupplierCol=standardListGrid.getColumn('sendSupplier');
    standardListGrid.hideColumn(sendSupplierCol);
    var systemNameCol=standardListGrid.getColumn('systemName');
    standardListGrid.hideColumn(systemNameCol);
    var cbbhCol=standardListGrid.getColumn('cbbh');
    standardListGrid.hideColumn(cbbhCol);
    var yzxcdCol=standardListGrid.getColumn('yzxcd');
    standardListGrid.hideColumn(yzxcdCol);
    var creatorCol=standardListGrid.getColumn('creator');
    standardListGrid.hideColumn(creatorCol);

    if(tabName=='JS') {
        var banciCol=standardListGrid.getColumn('banci');
        standardListGrid.hideColumn(banciCol);
        $("#banciFilter").hide();
    }

});

//查询下拉框的内容
function querySelectInfos() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do?systemCategoryId='+tabName,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("category").load(data.category);
                mini.get("belongDep").load(data.belongDep);
                // mini.get("standardField").load(data.fields);
            }
        }
    });
}

//查询标准
function searchStandard() {
    var queryParam = [];
    //体系Id
    /*var systemIdArr = [];
    var selectNode = systemTree.getSelectedNode();
    if (selectNode) {
        systemIdArr.push(selectNode.id);
        var allChild = systemTree.getAllChildNodes(selectNode);
        for (var i = 0; i < allChild.length; i++) {
            systemIdArr.push(allChild[i].id);
        }
    }
    queryParam.push({name: "systemIds", value: systemIdArr});*/
    //其他筛选条件
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var standardCategoryId = $.trim(mini.get("category").getValue());
    if (standardCategoryId) {
        queryParam.push({name: "standardCategoryId", value: standardCategoryId});
    }
    var standardStatus = $.trim(mini.get("status").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }
    var replaceNumber = $.trim(mini.get("replaceNumber").getValue());
    if (replaceNumber) {
        queryParam.push({name: "replaceNumber", value: replaceNumber});
    }
    var beReplaceNumber = $.trim(mini.get("beReplaceNumber").getValue());
    if (beReplaceNumber) {
        queryParam.push({name: "beReplaceNumber", value: beReplaceNumber});
    }
    var belongDepId = $.trim(mini.get("belongDep").getValue());
    if (belongDepId) {
        queryParam.push({name: "belongDepId", value: belongDepId});
    }
    var publishTimeFrom = $.trim(mini.get("publishTimeFrom").getText());
    if (publishTimeFrom) {
        queryParam.push({name: "publishTimeFrom", value: publishTimeFrom});
    }
    var publishTimeTo = $.trim(mini.get("publishTimeTo").getText());
    if (publishTimeTo) {
        queryParam.push({name: "publishTimeTo", value: publishTimeTo});
    }
    queryParam.push({name: "systemCategoryId", value: tabName});
    var publisherId = $.trim(mini.get("publisher").getValue());
    if (publisherId) {
        queryParam.push({name: "publisherId", value: publisherId});
    }
    var whetherIsBorrow = $.trim(mini.get("whetherIsBorrow").getValue());
    if (whetherIsBorrow) {
        queryParam.push({name: "whetherIsBorrow", value: whetherIsBorrow});
    }
    var sendSupplier = $.trim(mini.get("sendSupplier").getValue());
    if (sendSupplier) {
        queryParam.push({name: "sendSupplier", value: sendSupplier});
    }

    // var standardField=$.trim(mini.get("standardField").getValue());
    // if (standardField) {
    //     queryParam.push({name: "fieldId", value: standardField});
    // }
    var banciField=$.trim(mini.get("banci").getValue());
    if (banciField) {
        queryParam.push({name: "banci", value: banciField});
    }

    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardListGrid.getPageIndex();
    data.pageSize = standardListGrid.getPageSize();
    data.sortField = standardListGrid.getSortField();
    data.sortOrder = standardListGrid.getSortOrder();
    //查询
    standardListGrid.load(data);
}

//清空筛选条件后查询标准
function clearSearchStandard() {
    // systemTree.selectNode(null);
    // systemTree.collapseAll();
    // mini.get("filterNameId").setValue("");
    // mini.get("filterNumberId").setValue("");

    mini.get("standardNumber").setValue("");
    mini.get("standardName").setValue("");
    mini.get("category").setValue("");
    mini.get("status").setValue("");
    mini.get("replaceNumber").setValue("");
    mini.get("beReplaceNumber").setValue("");
    mini.get("belongDep").setValue("");
    mini.get("publishTimeFrom").setValue("");
    mini.get("publishTimeTo").setValue("");
    mini.get("publisher").setValue("");
    mini.get("whetherIsBorrow").setValue("");
    mini.get("sendSupplier").setValue("");
    // mini.get("standardField").setValue("");
    mini.get("banci").setValue("");
    searchStandard();
}

//打开标准的编辑页面（新增、编辑、明细）
function openStandardEditWindow(standardId,systemId, action) {

    //编辑时对于子管理员判断体系的权限
    if(action=='edit') {
        if(!isPointManager) {
            //子管理员是否有该体系的权限
            var pointSubManager=whetherIsPointSubManager(tabName,systemId,currentUserSubManager);
            if(!pointSubManager) {
                mini.alert('没有该体系下标准的操作权限！');
                return;
            }
        }
    }
    mini.open({
        title: "标准编辑",
        url: jsUseCtxPath + "/standardManager/core/standard/edit.do?standardId=" + standardId + '&action=' + action + '&systemCategoryId=' + tabName,
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

//判断预览是否需要申请。返回值：0--不需要申请，1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请
function judgePreviewNeedApply(standardId,categoryName) {
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

//导出标准
function exportStandard() {
    var queryParam = [];
    //体系Id
    var systemIdArr = [];
    var selectNode = systemTree.getSelectedNode();
    if (selectNode) {
        systemIdArr.push(selectNode.id);
        var allChild = systemTree.getAllChildNodes(selectNode);
        for (var i = 0; i < allChild.length; i++) {
            systemIdArr.push(allChild[i].id);
        }
    }
    queryParam.push({name: "systemIds", value: systemIdArr});
    //其他筛选条件
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var standardCategoryId = $.trim(mini.get("category").getValue());
    if (standardCategoryId) {
        queryParam.push({name: "standardCategoryId", value: standardCategoryId});
    }
    var standardStatus = $.trim(mini.get("status").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }
    var replaceNumber = $.trim(mini.get("replaceNumber").getValue());
    if (replaceNumber) {
        queryParam.push({name: "replaceNumber", value: replaceNumber});
    }
    var beReplaceNumber = $.trim(mini.get("beReplaceNumber").getValue());
    if (beReplaceNumber) {
        queryParam.push({name: "beReplaceNumber", value: beReplaceNumber});
    }
    var belongDepId = $.trim(mini.get("belongDep").getValue());
    if (belongDepId) {
        queryParam.push({name: "belongDepId", value: belongDepId});
    }
    var publishTimeFrom = $.trim(mini.get("publishTimeFrom").getText());
    if (publishTimeFrom) {
        queryParam.push({name: "publishTimeFrom", value: publishTimeFrom});
    }
    var publishTimeTo = $.trim(mini.get("publishTimeTo").getText());
    if (publishTimeTo) {
        queryParam.push({name: "publishTimeTo", value: publishTimeTo});
    }
    queryParam.push({name: "systemCategoryId", value: tabName});
    // queryParam.push({name: "systemCategoryName", value: title});
    var publisherId = $.trim(mini.get("publisher").getValue());
    if (publisherId) {
        queryParam.push({name: "publisherId", value: publisherId});
    }
    var whetherIsBorrow = $.trim(mini.get("whetherIsBorrow").getValue());
    if (whetherIsBorrow) {
        queryParam.push({name: "whetherIsBorrow", value: whetherIsBorrow});
    }
    var sendSupplier = $.trim(mini.get("sendSupplier").getValue());
    if (sendSupplier) {
        queryParam.push({name: "sendSupplier", value: sendSupplier});
    }
    var standardField=$.trim(mini.get("standardField").getValue());
    if (standardField) {
        queryParam.push({name: "fieldId", value: standardField});
    }
    var banciField=$.trim(mini.get("banci").getValue());
    if (banciField) {
        queryParam.push({name: "banci", value: banciField});
    }

    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//查找体系节点
function filterSystemTreeInStandard() {
    systemTree.selectNode(null);
    systemTree.clearFilter();
    systemTree.collapseAll();
    var nameFilter = $.trim(mini.get("filterNameId").getValue());
    var numberFilter = $.trim(mini.get("filterNumberId").getValue());
    if (nameFilter || numberFilter) {
        nameFilter = nameFilter.toLowerCase();
        numberFilter = numberFilter.toLowerCase();
        var nodes = systemTree.findNodes(function (node) {
            var systemName = node.systemName ? node.systemName.toLowerCase() : "";
            var systemNumber = node.systemNumber ? node.systemNumber.toLowerCase() : "";
            if (nameFilter && systemName.indexOf(nameFilter) == -1) {
                return false;
            }
            if (numberFilter && systemNumber.indexOf(numberFilter) == -1) {
                return false;
            }
            return true;
        });
        //展开所有找到的节点
        for (var i = 0, l = nodes.length; i < l; i++) {
            var node = nodes[i];
            systemTree.expandPath(node);
        }

        //第一个节点选中并滚动到视图
        var firstNode = nodes[0];
        if (firstNode) {
            systemTree.selectNode(firstNode);
            systemTree.scrollIntoView(firstNode);
        }
    }
}
