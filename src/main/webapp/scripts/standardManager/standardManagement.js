$(function () {
    if (isPointManager) {
        $("#operateStandard").show();
        $("#importId").show();
    } else if (isSubManager) {
        $("#operateStandard").show();
        $("#importId").show();
    }
    //是否是技术标准管理员
    var JSSystemStandardManager = whetherIsPointStandardManager('JS', currentUserRoles);
    //是否是管理标准附件审批人员
    var GLBZFBSPRYSystemStandardManager = whetherIsPointStandardManager('GLBZFBSPRY', currentUserRoles);
    if (tabName == 'JS' && !JSSystemStandardManager) {
        mini.get("exportBtn").hide();
    }
    if (tabName == 'JS' && (JSSystemStandardManager||currentUserNo=='admin')) {
        mini.get("doCheckStartFlow").show();
    }
    // 是否管理标准管理员
    debugger;
    if (tabName == 'GL'&&GLBZFBSPRYSystemStandardManager) {
        mini.get("standardAttachesManagement").show();
    }

    querySelectInfos();
    mini.get("standardNumber").setValue(standardNumberFilter);
    mini.get("standardName").setValue(standardNameFilter);
    mini.get("category").setValue(standardCategoryFilter);
    mini.get("publishTimeFrom").setValue(publishTimeFromFilter);
    mini.get("publishTimeTo").setValue(publishTimeToFilter);
    mini.get("status").setValue(standardStatusFilter);
    mini.get("standardField").setValue(fieldId);
    mini.get("status").setValue("enable");
    searchStandard();
    //初始隐藏一些列，以列的name去查找column
    var sendSupplierCol = standardListGrid.getColumn('sendSupplier');
    standardListGrid.hideColumn(sendSupplierCol);
    var systemNameCol = standardListGrid.getColumn('systemName');
    standardListGrid.hideColumn(systemNameCol);
    var cbbhCol = standardListGrid.getColumn('cbbh');
    standardListGrid.hideColumn(cbbhCol);
    var yzxcdCol = standardListGrid.getColumn('yzxcd');
    standardListGrid.hideColumn(yzxcdCol);
    var creatorCol = standardListGrid.getColumn('creator');
    standardListGrid.hideColumn(creatorCol);

    if (tabName == 'JS') {
        var banciCol = standardListGrid.getColumn('banci');
        standardListGrid.hideColumn(banciCol);
        $("#banciFilter").hide();
    }
    if (tabName != 'JS') {
        var yearDoCheckStatus = standardListGrid.getColumn('yearDoCheckStatus');
        standardListGrid.hideColumn(yearDoCheckStatus);
        $("#yearDoCheckStatusFilter").hide();
    }
});

//查询下拉框的内容
function querySelectInfos() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do?systemCategoryId=' + tabName,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("category").load(data.category);
                mini.get("belongDep").load(data.belongDep);
                mini.get("standardField").load(data.fields);
            }
        }
    });
}

//查询标准
function searchStandard() {
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

    var standardField = $.trim(mini.get("standardField").getValue());
    if (standardField) {
        queryParam.push({name: "fieldId", value: standardField});
    }
    var banciField = $.trim(mini.get("banci").getValue());
    if (banciField) {
        queryParam.push({name: "banci", value: banciField});
    }
    var yearDoCheckStatusField = $.trim(mini.get("yearDoCheckStatus").getValue());
    if (yearDoCheckStatusField) {
        queryParam.push({name: "yearDoCheckStatus", value: yearDoCheckStatusField});
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
    systemTree.selectNode(null);
    systemTree.collapseAll();
    mini.get("filterNameId").setValue("");
    mini.get("filterNumberId").setValue("");

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
    mini.get("standardField").setValue("");
    mini.get("banci").setValue("");
    mini.get("yearDoCheckStatus").setValue("");
    searchStandard();
}

//打开标准的编辑页面（新增、编辑、明细）
function openStandardEditWindow(standardId, systemId, action) {

    //编辑时对于子管理员判断体系的权限
    if (action == 'edit') {
        if (!isPointManager) {
            //子管理员是否有该体系的权限
            var pointSubManager = whetherIsPointSubManager(tabName, systemId, currentUserSubManager);
            if (!pointSubManager) {
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
            var data = {
                systemTreeData: systemTree.getData(), isPointManager: isPointManager,
                tabName: tabName, currentUserSubManager: currentUserSubManager
            };  //传递上传参数
            iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {
            searchStandard();
        }
    });
}

//删除标准
function removeStandard(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = standardListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var existCannotDelete = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (isPointManager) {
                    ids.push(r.id);
                } else {
                    var pointSubManager = whetherIsPointSubManager(tabName, r.systemId, currentUserSubManager);
                    if (pointSubManager) {
                        ids.push(r.id);
                    } else {
                        existCannotDelete = true;
                    }
                }
            }
            if (existCannotDelete) {
                mini.alert('仅能删除有相应操作权限体系下的标准！');
            }
            if (ids.length <= 0) {
                return;
            }
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standard/delete.do",
                type: 'POST',
                data: mini.encode({ids: ids.join(',')}),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchStandard();
                    }
                }
            });
        }
    });
}

//判断预览是否需要申请。返回值：0--不需要申请，1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请
function judgePreviewNeedApply(standardId, categoryName) {
    if (categoryName != '企业标准' && categoryName != '集团标准') {
        return {result: '0'};
    }
    var applyCategoryId = 'preview';
    //标准管理领导不需要申请
    var isLeader = whetherIsLeader(currentUserRoles);
    if (isLeader) {
        return {result: '0'};
    }
    //技术标准管理人员不需要申请
    var JSSystemStandardManager = whetherIsPointStandardManager('JS', currentUserRoles);
    if (JSSystemStandardManager) {
        return {result: '0'};
    }
    //非管理职级的人员预览也不需要
    var isGLMan = whetherIsGLMan(currentUserZJ);
    if (!isGLMan) {
        return {result: '0'};
    }
    //管理职级的人员预览非技术类的不需要
    if (isGLMan && tabName != 'JS') {
        return {result: '0'};
    }
    //其他场景都需要判断是否已经有申请单
    return checkOperateApply(standardId, applyCategoryId, currentUserId);
}
//判断下载是否需要申请。返回值：0--不需要申请，1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请
function judgeDownloadNeedApply(standardId, categoryName) {
    var applyCategoryId = 'download';
    //如果技术标准的类型不是企业标准、集团标准，则允许下载
    if (tabName == 'JS') {
        if ((categoryName == '企业标准' || categoryName == '集团标准') && !isHw) {
            // 判断是否需要申请
            return checkOperateApply(standardId, applyCategoryId, currentUserId);
        } else {
            // 其他场景不需要申请
            return { result: '0' };
        }
    } else {
        //标准管理领导不需要申请
        var isLeader = whetherIsLeader(currentUserRoles);
        if (isLeader) {
            return {result: '0'};
        }
        //本体系类别的标准管理人员下载不需要申请
        var selfSystemStandardManager = whetherIsPointStandardManager(tabName, currentUserRoles);
        if (selfSystemStandardManager) {
            return {result: '0'};
        }
        //其他场景都需要判断是否已经有申请单
        return checkOperateApply(standardId, applyCategoryId, currentUserId);
    }
}

function checkOperateApply(standardId, applyCategoryId, currentUserId) {
    var resultCodeId = {result: '3'};
    $.ajax({
        url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
        type: 'POST',
        data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                resultCodeId.result = data.result;
                resultCodeId.applyId = data.applyId;
            }
        }
    });
    return resultCodeId;
    }

//预览标准全文
function previewStandard(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
    if (status == 'disable') {
        mini.confirm("该标准已废止，确定预览吗？", "提示", function (action) {
            if (action == "ok") {
                previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
            }
        });
    } else {
        previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
    }

}

function previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
    var resultCodeId = judgePreviewNeedApply(standardId, categoryName);
    if (resultCodeId.result == '0' || resultCodeId.result == '1') {
        if (resultCodeId.result == '1') {
            changeApplyUseStatus(resultCodeId.applyId, 'yes');
        }
        //记录预览情况
        recordStandardOperate('preview', standardId);
        var previewUrl = jsUseCtxPath + "/standardManager/core/standard/preview.do?standardId=" + standardId;
        if (systemCategoryId == 'JS') {
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&plate=bz&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
        } else {
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
        }

    } else if (resultCodeId.result == '2') {
        mini.alert('请在“标准预览申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
    } else if (resultCodeId.result == '3') {
        //跳转到新增预览申请界面
        mini.confirm("当前操作需要提交预览申请，确定继续？", "权限不足", function (action) {
            if (action == "ok") {
                addApply('preview', standardId, standardName);
            }
        });
    }
}

//下载标准全文
function downloadStandard(standardId, standardName, standardNumber, categoryName, status) {
    if (status == 'disable') {
        mini.confirm("该标准已废止，确定下载吗？", "提示", function (action) {
            if (action == "ok") {
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
        mini.confirm("当前操作会创建一个下载申请单，审批完成后可在此处或“标准流程管理”--“标准下载申请”页面点击“下载”按钮下载标准，确定继续？", "权限不足", function (action) {
            if (action == "ok") {
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

//导入标准
function openImportStandard() {
    var pointManager = whetherIsPointStandardManager(tabName, currentUserRoles);
    if (!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    importWindow.show();
}

function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchStandard();
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
    var standardField = $.trim(mini.get("standardField").getValue());
    if (standardField) {
        queryParam.push({name: "fieldId", value: standardField});
    }
    var banciField = $.trim(mini.get("banci").getValue());
    if (banciField) {
        queryParam.push({name: "banci", value: banciField});
    }
    var yearDoCheckStatusField = $.trim(mini.get("yearDoCheckStatus").getValue());
    if (yearDoCheckStatusField) {
        queryParam.push({name: "yearDoCheckStatus", value: yearDoCheckStatusField});
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

//导入模板下载
function standardImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/standardManager/core/standard/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传xls文件！');
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importStandard() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert('请选择文件！');
        return;
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            message = returnObj.message;
                        }
                        mini.alert(message);
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/standardManager/core/standard/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('standardImportFile', file);
        xhr.send(fd);
    }
}

function stopOldPublishNew(standardId, systemId) {
    if (!isPointManager) {
        //子管理员是否有该体系的权限
        var pointSubManager = whetherIsPointSubManager(tabName, systemId, currentUserSubManager);
        if (!pointSubManager) {
            mini.alert('没有该体系下标准的操作权限！');
            return;
        }
    }
    mini.confirm("该操作会废止当前标准，生成&启用新标准。确定继续?", "提示信息", function (action) {
        ;
        if (action == "ok") {
            $.ajax({
                url: jsUseCtxPath + '/standardManager/core/standard/stopOldPublishNew.do?standardId=' + standardId,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchStandard();
                    }
                }
            });
        }
    });
}

function borrow(standardId, systemId) {
    if (!isPointManager) {
        //子管理员是否有该体系的权限
        var pointSubManager = whetherIsPointSubManager(tabName, systemId, currentUserSubManager);
        if (!pointSubManager) {
            mini.alert('没有该体系下标准的操作权限！');
            return;
        }
    }
    mini.open({
        title: "标准借用",
        url: jsUseCtxPath + "/standardManager/core/standard/borrowPage.do?standardFromId=" + standardId,
        width: 800,
        height: 500,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchStandard();
        }
    });
}
//马天宇
function linkStandard() {
    var rows = standardListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    var ids = [];
    for (var i = 0, l = rows.length; i < l; i++) {
        var r = rows[i];
        ids.push(r.id);
    }
    _SubmitJson({
        url: jsUseCtxPath + "/jsbz/saveJsbz.do?type=" + type,
        method: 'POST',
        showMsg: false,
        data: {ids: ids.join(',')},
        success: function (data) {
            if (data) {
                mini.alert(data.message);
                searchStandard();
            }
        }
    });
}
//@lwgkiller改:将type改为业务标志，businessId代表业务id。
function linkStandard2() {
    var url = "";
    var rows = standardListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    var ids = [];
    for (var i = 0, l = rows.length; i < l; i++) {
        var r = rows[i];
        ids.push(r.id);
    }
    if (type == "keyDesign") {
        url = jsUseCtxPath + "/jsbz/saveJsbz.do?type=" + businessId;
    } else if (type == "componentTest") {
        url = jsUseCtxPath + "/componentTest/core/kanban/bindingStandard.do?mainId=" + businessId;
    }
    _SubmitJson({
        url: url,
        method: 'POST',
        showMsg: false,
        data: {ids: ids.join(',')},
        success: function (data) {
            if (data) {
                mini.alert(data.message);
                searchStandard();
            }
        }
    });
}
//@lwgkiller改-end


//@mh 标准动态跳转到详情
function jumpToDynamic(standardId) {
    // mini.alert("standardId"+standardId)
    if(standardId)
    {
        var url = jsUseCtxPath + "/standardManager/core/standardManagement/jsStandardManagementEdit.do?action=detail" +
            "&standardId="+standardId+
            "&status=RUNNING";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (standardListGrid) {
                    standardListGrid.reload()
                }
                ;
            }
        }, 1000);
    }

}

function doCheckStartFlow() {
    var selectStandards=standardListGrid.getSelecteds();
    if(!selectStandards || selectStandards.length==0) {
        mini.alert("请至少选择一条标准！");
        return;
    }
    mini.confirm("确定要对选择的"+selectStandards.length+"条标准发起自查流程吗？<br>" +
        "流程创建后，请在首页待办或“标准流程管理”--“标准执行性自查”页面进行流程确认并提交", "提醒",
        function (action) {
            if (action == "ok") {
                showLoading();
                var standardIds=[];
                for(var index=0;index<selectStandards.length;index++) {
                    standardIds.push(selectStandards[index].id);
                }
                $.ajax({
                    url: jsUseCtxPath + '/standard/core/doCheck/startDoCheckFlow.do?standardIds=' + standardIds.join(","),
                    type: 'get',
                    contentType: 'application/json',
                    success: function (result) {
                        if (result) {
                            mini.alert(result.message);
                        }
                    },
                    complete: function () {
                        hideLoading();
                    }
                });
            }
        }
    );
}
//管理标准附件审批人员查看所有申请汇总界面用于审核
function standardAttachesManagement() {
    var action = "review";
    mini.open({
        title: standardEdit_name13,
        url: jsUseCtxPath + "/standardManager/core/standardFileInfos/attachFileListWindow.do?action="+action+"&standardType="+tabName+"&coverContent=" + coverContent,
        width: 1600,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}