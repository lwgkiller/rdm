$(function () {
    if (systemCategoryId == "GL") {
        $("#standardAttachFile").hide();
    }
    querySelectInfos();
});

//查询下拉框的内容
function querySelectInfos() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("standardCategoryId").load(data.category);
                mini.get("belongDepId").load(data.belongDep);
                formStandard.setData(standardObj);
                if(type=="修订"){
                    clearUploadFile();
                }
                if (action == 'add') {
                    mini.get("standardStatus").setValue("draft");
                    if(systemCategoryId!='JS') {
                        mini.get("banci").setValue("A0");
                    }
                } else if (action == 'detail') {
                    formStandard.setEnabled(false);
                    mini.get("saveStandard").hide();
                    mini.get("uploadFileBtn").setEnabled(false);
                    mini.get("clearFileBtn").setEnabled(false);
                    mini.get("publishNotice").hide();
                }
                if(systemCategoryId!='JS') {
                    $("#banciTr").show();
                }
                if(isPointManager){
                    $("#updateNote").show();
                }
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

    if (targetNode && selectSystemTree.isLeaf(targetNode)) {
        mini.get("systemTreeSelectId").setText(targetNode.systemName);
        mini.get("systemTreeSelectId").setValue(targetNode.id);
        var standardCategoryText=mini.get("standardCategoryId").getText();
        if(systemCategoryId=='GL'&& standardCategoryText=='企业标准'){
            // 新增企业标准自动生成编号
            if(!standardObj.id){
                // 标准编号生成规则：
                var standardNumber = generateStandardNumber(targetNode.id);
                mini.get("standardNumber").setValue(standardNumber);
            }
        }
        selectSystemHide();
    } else {
        mini.alert(standardEdit_name1);
    }

}

// 生成管理标注编号
function generateStandardNumber(systemId) {
    let newSeqNum;
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/autoGenerateStandardNum.do?systemId=' + systemId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (result) {
            if (result) {
                var message="";
                if (result.success) {
                    newSeqNum = result.data;
                } else {
                    message = result.message+"自动生成编号失败，请手动编写！";
                    mini.alert(message);
                }
            }
        }
    });
    return newSeqNum;
}
function selectSystemHide() {
    selectSystemWindow.hide();

}

//查找体系节点
function filterSystemTree() {
    var key = mini.get("filterNameId").getValue();
    if (key == "") {
        selectSystemTree.clearFilter();
    } else {
        key = key.toLowerCase();
        var nodes = selectSystemTree.findNodes(function (node) {
            var systemName = node.systemName ? node.systemName.toLowerCase() : "";
            if (systemName.indexOf(key) != -1) {
                return true;
            }
        });
        //展开所有找到的节点
        for (var i = 0, l = nodes.length; i < l; i++) {
            var node = nodes[i];
            selectSystemTree.expandPath(node);
        }

        //第一个节点选中并滚动到视图
        var firstNode = nodes[0];
        if (firstNode) {
            // systemTree.selectNode(firstNode);
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
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert(standardEdit_name2);
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//新增或者更新的保存
function saveStandard() {
    mini.get("systemCategoryId").setValue(systemCategoryId);
    var formData = formStandard.getData();
    formData.categoryName=mini.get("standardCategoryId").getText();
    formData.belongDepName=mini.get("belongDepId").getText();
    formData.systemName=mini.get("systemTreeSelectId").getText();
    formData.replaceNumber=mini.get("replaceNumberId").getText();
    formData.beReplaceNumber=mini.get("beReplaceNumberId").getText();
    formData.publisherName=mini.get("publisher").getText();
    formData.stoperName=mini.get("stoper").getText();
    formData.belongFieldNames=mini.get("belongFieldIds").getText();

    var checkResult=checkStandardEditRequired(formData);
    if(!checkResult) {
       return;
    }
    if(standardTaskId) {
        formData.standardTaskId=standardTaskId;
    }
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
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
                        mini.alert(message,standardEdit_name14,function (action) {
                            // if(action=='ok') {
                            //     CloseWindow();
                            // }
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
        xhr.open('POST', jsUseCtxPath + '/standardManager/core/standard/save.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        if (formData) {
            for (key in formData) {
                if(key=='publishTime' || key=='stopTime') {
                    fd.append(key, getYMDHmsString(formData[key]));
                } else {
                    fd.append(key, formData[key]);
                }
            }
        }
        fd.append('standardFile', file);
        xhr.send(fd);
    }
}


function stopAndNew() {
    mini.confirm(standardEdit_name3,standardEdit_name14,function(action){;
        if (action != 'ok') {
            return;
        } else {
            mini.get("systemCategoryId").setValue(systemCategoryId);
            var formData = formStandard.getData();
            var checkResult=checkStandardEditRequired(formData);
            if(!checkResult) {
                return;
            }
            if(standardTaskId) {
                formData.standardTaskId=standardTaskId;
            }
            var file = null;
            var fileList = $("#inputFile")[0].files;
            if (fileList && fileList.length > 0) {
                file = fileList[0];
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
                                mini.alert(message,standardEdit_name14,function (action) {
                                    // if(action=='ok') {
                                    //     CloseWindow();
                                    // }
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
                xhr.open('POST', jsUseCtxPath + '/standardManager/core/standard/stopAndNew.do', false);
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                var fd = new FormData();
                if (formData) {
                    for (key in formData) {
                        if(key=='publishTime' || key=='stopTime') {
                            fd.append(key, getYMDHmsString(formData[key]));
                        } else {
                            fd.append(key, formData[key]);
                        }
                    }
                }
                fd.append('standardFile', file);
                xhr.send(fd);
            }

        }
    });

}

function publishNotice() {
    var standardId=mini.get("id").getValue();
    if(!standardId) {
        mini.alert(standardEdit_name4);
        return;
    }
    if(!standardId) {
        standardId='';
    }
    mini.open({
        title: standardEdit_name5,
        url: jsUseCtxPath + "/standardManager/core/standardMessage/edit.do?standardId="+standardId+"&canEditStandard=false",
        width: 850,
        height: 550,
        showModal:true,
        allowResize: true,
        ondestroy: function (action) {
        }
    });
}


function checkStandardEditRequired(formData) {
    if(!formData) {
        mini.alert(standardEdit_name6);
        return false;
    }
    if(!$.trim(formData.standardNumber)) {
        mini.alert(standardEdit_name7);
        return false;
    }
    if(!$.trim(formData.standardName)) {
        mini.alert(standardEdit_name8);
        return false;
    }
    if(!$.trim(formData.standardCategoryId)) {
        mini.alert(standardEdit_name9);
        return false;
    }
    // if(!$.trim(formData.belongDepId)) {
    //     mini.alert('请选择归口部门！');
    //     return false;
    // }
    if(!$.trim(formData.systemId)) {
        mini.alert(standardEdit_name10);
        return false;
    }
    //对于子管理员判断是否能选择这个体系
    if(!isPointManager) {
        //子管理员是否有该体系的权限
        var pointSubManager=whetherIsPointSubManager(systemCategoryId,formData.systemId,currentUserSubManager);
        if(!pointSubManager) {
            mini.alert(standardEdit_name11);
            return false;
        }
    }

    return true;
}

function setPublishOrStopTimeDefault() {
    var status=mini.get("standardStatus").getValue();
    if(status=='draft') {
        mini.get("publishTimeId").setValue('');

        mini.get("stoper").setValue('');
        mini.get("stoper").setText('');
        mini.get("stopTimeId").setValue('');

    } else if (status=='enable') {
        //如果启用时间为空则设置为当前
        if(!mini.get("publishTimeId").getValue()) {
            mini.get("publishTimeId").setValue(getYMDHmsString(new Date()));
        }

        //废止时间和废止人设置为空
        mini.get("stopTimeId").setValue('');
        mini.get("stoper").setValue('');
        mini.get("stoper").setText('');

    } else if (status=='disable') {
        //如果废止时间为空则设置为当前
        if(!mini.get("stopTimeId").getValue()) {
            mini.get("stopTimeId").setValue(getYMDHmsString(new Date()));
        }
        //如果废止人为空则设置为当前
        if(!mini.get("stoper").getValue()) {
            mini.get("stoper").setValue(currentUserId);
            mini.get("stoper").setText(currentUserName);
        }
    }

}

/**
 * 选择替代标准和被替代标准
 */
function selectStandard(inputScene){
    $("#parentInputScene").val(inputScene);
    selectStandardWindow.show();
    searchStandard();
}
//查询标准
function searchStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("filterStandardNameId").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    queryParam.push({name: "systemCategoryId", value: systemCategoryId});
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardListGrid.getPageIndex();
    data.pageSize = standardListGrid.getPageSize();
    data.sortField = standardListGrid.getSortField();
    data.sortOrder = standardListGrid.getSortOrder();
    //查询
    standardListGrid.load(data);
}

function onRowDblClick() {
    selectStandardOK();
}

function selectStandardOK() {
    var selectRow = standardListGrid.getSelected();
    if (selectRow) {
        var parentInputScene=$("#parentInputScene").val();
        if(parentInputScene=='replace') {
            mini.get("replaceNumberId").setValue(selectRow.id);
            mini.get("replaceNumberId").setText(selectRow.standardNumber);
        } else if(parentInputScene=='beReplace') {
            mini.get("beReplaceNumberId").setValue(selectRow.id);
            mini.get("beReplaceNumberId").setText(selectRow.standardNumber);
        }
    }
    selectStandardHide();
}

function selectStandardHide() {
    $("#parentInputScene").val('');
    selectStandardWindow.hide();
    mini.get("filterStandardNumberId").setValue('');
    mini.get("filterStandardNameId").setValue('');
}

function onSelectStandardCloseClick(inputScene) {
    if(inputScene=='replace') {
        mini.get("replaceNumberId").setValue('');
        mini.get("replaceNumberId").setText('');
    } else if(inputScene=='beReplace') {
        mini.get("beReplaceNumberId").setValue('');
        mini.get("beReplaceNumberId").setText('');
    }
}

function selectBelongField() {
    selectFieldWindow.show();
    searchStandardBelongField();
}

function searchStandardBelongField() {
    var params=[];
    params.push({name:'systemCategoryId',value:systemCategoryId});
    var filterFieldName=mini.get("filterFieldName").getValue();
    if(filterFieldName) {
        params.push({name:'fieldName',value:filterFieldName});
    }
    var data={};
    data.filter=mini.encode(params);
    fieldListGrid.load(data);
}

function selectCurrentField() {
    var belongFieldIds=mini.get("belongFieldIds").getValue();
    if(belongFieldIds) {
        var fieldIds=belongFieldIds.split(',');
        var rows = fieldListGrid.findRows(function (row) {
            if (fieldIds.indexOf(row.fieldId) != -1) return true;
            else return false
        });
        fieldListGrid.selects(rows);
    }
}

function selectFieldOK() {
    var selectRow = fieldListGrid.getSelecteds();
    if (selectRow) {
        var fieldIds="";
        var fieldNames="";
        for(var index=0;index<selectRow.length;index++) {
            fieldIds+=selectRow[index].fieldId+",";
            fieldNames+=selectRow[index].fieldName+",";
        }
        fieldIds=fieldIds.substring(0,fieldIds.length-1);
        fieldNames=fieldNames.substring(0,fieldNames.length-1);
       mini.get("belongFieldIds").setValue(fieldIds);
       mini.get("belongFieldIds").setText(fieldNames);
    }
    selectFieldHide();
}

function selectFieldHide() {
    selectFieldWindow.hide();
    mini.get("filterFieldName").setValue('');
}
//展示证明材料窗口
function showAttachDialog() {
    var standardId = mini.get('id').getValue();
    if(!standardId){
        mini.alert(standardEdit_name12);
        return;
    }
    var canOperateFile = false;
    if (action=='edit'||action=='add') {
        canOperateFile = true;
    }
    mini.open({
        title: standardEdit_name13,
        url: jsUseCtxPath + "/standardManager/core/standardFileInfos/attachFileListWindow.do?standardId=" + standardId + "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent,
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

function autoNumber() {
    var standardCategoryText=mini.get("standardCategoryId").getText();
    var systemId=mini.get("systemTreeSelectId").getValue();
    // 编辑标准
    if(!standardObj.id) {
        if (standardCategoryText == '企业标准') {
            if (systemCategoryId == 'GL') {
                // 管理标准下企业标准新增过程中标准编号根据管理标准体系自动生成
                if (systemId) {
                    var standardNumber = generateStandardNumber(systemId);
                    mini.get("standardNumber").setValue(standardNumber);
                }
            } else {
                // 新增技术类自动生成前缀
                mini.get("standardNumber").setValue('Q/XGWJ ');
            }
        } else {
            mini.get("standardNumber").setValue('');
        }
    }
}
