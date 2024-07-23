$(function () {
    querySelectInfos();
    if(recordId) {
        $.ajax({
            url: jsUseCtxPath + '/xcmgNpi/core/npiFile/queryNpiFileById.do?id='+recordId,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    formStandard.setData(data);
                }
            }
        });
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

function selectSystem() {
    selectSystemWindow.show();
    mini.get("filterNameId").setValue('');
}

function selectSystemOK() {
    var targetNode = selectSystemTree.getSelectedNode();
    if (!targetNode) {
        mini.alert("请选择一个最底层的体系类别！");
    } else if (targetNode && selectSystemTree.isLeaf(targetNode)) {
        var text = targetNode.systemName;
        if (targetNode.systemNameEn) {
            text+="【"+targetNode.systemNameEn+"】";
        }

        mini.get("systemTreeSelectId").setText(text);
        mini.get("systemTreeSelectId").setValue(targetNode.id);
        selectSystemHide();
    } else {
        mini.alert("请选择最底层的体系类别！");
    }

}

function selectSystemHide() {
    selectSystemWindow.hide();

}
//查找体系节点
function filterSystemTree() {
    selectSystemTree.selectNode(null);
    selectSystemTree.clearFilter();
    selectSystemTree.collapseAll();
    var nameFilter = $.trim(mini.get("filterNameId").getValue());
    if (nameFilter) {
        nameFilter = nameFilter.toLowerCase();
        var nodes = selectSystemTree.findNodes(function (node) {
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
            selectSystemTree.expandPath(node);
        }

        //第一个节点选中并滚动到视图
        var firstNode = nodes[0];
        if (firstNode) {
            selectSystemTree.selectNode(firstNode);
            selectSystemTree.scrollIntoView(firstNode);
        }
    }
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
        if (fileNameSuffix == 'pdf') {
            mini.get("fileObjName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert("请上传pdf文件！");
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileObjName").setValue('');
}

//新增或者更新的保存
function saveNpiFile() {
    var formData = formStandard.getData();
    if (!$.trim(formData.fileName)) {
        mini.alert("请填写活动名称（中文）！");
        return;
    }
    if (!$.trim(formData.stageDicId)) {
        mini.alert("请选择流程阶段！");
        return;
    }
    if(!$.trim(formData.systemName)) {
        mini.alert("请选择活动类型！");
        return;
    }
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    } else if (!$.trim(formData.fileObjName)) {
        mini.alert("请上传pdf附件！");
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
                    if(xhr.responseText) {
                        var returnObj=JSON.parse(xhr.responseText);
                        var message='';
                        if(returnObj.message) {
                            message=returnObj.message;
                        }
                        mini.alert(message,"提示信息",function (action) {
                            if(returnObj.success) {
                                mini.get("id").setValue(returnObj.id);
                            }
                            CloseWindow();
                        });
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/xcmgNpi/core/npiFile/save.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        if (formData) {
            for (key in formData) {
                fd.append(key, formData[key]);
            }
        }
        fd.append('npiFile', file);
        xhr.send(fd);
    }
}
