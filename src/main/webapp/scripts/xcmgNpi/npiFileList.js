$(function () {
    querySelectInfos();
    searchNpiFiles();
    if (isOperator=="true") {
        $("#opBtns").show();
    }
});

//查询下拉框的内容
function querySelectInfos() {
    $.ajax({
        url: jsUseCtxPath + '/xcmgNpi/core/npiFile/getDicInfos.do',
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("stageId").load(data);
            }
        }
    });
}

//查询
function searchNpiFiles() {
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
    var stageDicId = $.trim(mini.get("stageId").getValue());
    if (stageDicId) {
        queryParam.push({name: "stageDicId", value: stageDicId});
    }
    var fileName = $.trim(mini.get("fileName").getValue());
    if (fileName) {
        queryParam.push({name: "fileName", value: fileName});
    }

    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = npiFileListGrid.getPageIndex();
    data.pageSize = npiFileListGrid.getPageSize();
    data.sortField = npiFileListGrid.getSortField();
    data.sortOrder = npiFileListGrid.getSortOrder();
    //查询
    npiFileListGrid.load(data);
}

//清空筛选条件后查询
function clearSearchNpiFiles() {
    systemTree.selectNode(null);
    systemTree.collapseAll();
    mini.get("filterNameId").setValue("");

    mini.get("stageId").setValue("");
    mini.get("fileName").setValue("");
    searchNpiFiles();
}

//打开编辑页面（新增、编辑）
function openEditWindow(recordId, action) {
    mini.open({
        title: "编辑",
        url: jsUseCtxPath + "/xcmgNpi/core/npiFile/editPage.do?recordId=" + recordId + '&action=' + action,
        width: 900,
        height: 500,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchNpiFiles();
        }
    });
}

//删除标准
function removeNpiFile(recordId) {
    var rowIdArr = [];
    if (recordId) {
        rowIdArr.push(recordId);
    } else {
        var rows = npiFileListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        for (var i = 0; i < rows.length; i++) {
            rowIdArr.push(rows[i].id);
        }
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            $.ajax({
                url: jsUseCtxPath + "/xcmgNpi/core/npiFile/delete.do",
                type: 'POST',
                data: mini.encode({ids: rowIdArr.join(',')}),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchNpiFiles();
                    }
                }
            });
        }
    });
}

//导出
function exportNpiFile() {
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
    var stageDicId = $.trim(mini.get("stageId").getValue());
    if (stageDicId) {
        queryParam.push({name: "stageDicId", value: stageDicId});
    }
    var fileName = $.trim(mini.get("fileName").getValue());
    if (fileName) {
        queryParam.push({name: "fileName", value: fileName});
    }

    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//查找体系节点
function filterSystemTree() {
    systemTree.selectNode(null);
    systemTree.clearFilter();
    systemTree.collapseAll();
    var nameFilter = $.trim(mini.get("filterNameId").getValue());
    if (nameFilter) {
        nameFilter = nameFilter.toLowerCase();
        var nodes = systemTree.findNodes(function (node) {
            var systemName = node.systemName ? node.systemName.toLowerCase() : "";
            var systemNameEn = node.systemNameEn ? node.systemNameEn.toLowerCase() : "";
            if (nameFilter && systemName.indexOf(nameFilter) == -1 && systemNameEn.indexOf(nameFilter) == -1) {
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


function returnNpiFilePreview(fileName, fileId, coverContent) {
    var url = '/xcmgNpi/core/npiFile/pdfPreviewOrDownload.do';
    var s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    return s;
}
function downNpiFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/xcmgNpi/core/npiFile/pdfPreviewOrDownload.do");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileObjName);
    var mainId = $("<input>");
    mainId.attr("type", "hidden");
    mainId.attr("name", "applyId");
    mainId.attr("value", "");
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(mainId);
    form.append(fileId);
    form.submit();
    form.remove();
}