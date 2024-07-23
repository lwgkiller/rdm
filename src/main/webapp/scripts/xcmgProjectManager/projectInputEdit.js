function saveInput() {
//    校验
    var inputType=$.trim(mini.get("inputType").getValue());
    if(!inputType){
        mini.alert("请选择类型！");
        return;
    }
    var inputName=$.trim(mini.get("referId").getText());
    if(!inputName) {
        mini.alert("请选择输入名称！");
        return;
    }
    var referContent=$.trim(mini.get("referContent").getValue());
    if(!referContent) {
        mini.alert("请输入引用条款（内容）！");
        return;
    }
    var relation=$.trim(mini.get("relation").getValue());
    if(!relation) {
        mini.alert("请输入与本项目的关系！");
        return;
    }
    //保存
    var formData = _GetFormJsonMini("inputForm");
    var json =mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/saveInputEdit.do',
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                if(data.success) {
                    mini.alert("数据保存成功","提示",function () {
                        closeInput();
                    });
                } else {
                    mini.alert("数据保存失败，"+data.message);
                }
            }
        }
    });
}

function closeInput() {
    inputForm.setData({});
    CloseWindow();
}

function inputTypeChange() {
    inputNameCloseClick();
}

function inputNameCloseClick() {
    mini.get("referId").setValue("");
    mini.get("referId").setText("");
    mini.get("inputNumber").setValue("");
}

function inputNameClick() {
    var inputType = mini.get("inputType").getValue();
    if(!inputType) {
        mini.alert("请先选择类型！");
        return;
    }
    if(inputType=='标准') {
        selectStandardWindow.show();
    } else if(inputType=='专利') {
        selectZlWindow.show();
    } else if(inputType=='情报报告') {
        selectQbWindow.show();
    }
    searchInputList();
}


//查询
function searchInputList() {
    var inputType = mini.get("inputType").getValue();
    var queryParam = [];
    if(inputType=='标准') {
        var systemCategoryId = $.trim(mini.get("filterSystemCategory").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
    } else if(inputType=='专利') {
        var applicationNumber = $.trim(mini.get("zl_applicationNumber").getValue());
        if (applicationNumber) {
            queryParam.push({name: "applicationNumber", value: applicationNumber});
        }
        var patentName = $.trim(mini.get("zl_patentName").getValue());
        if (patentName) {
            queryParam.push({name: "patentName", value: patentName});
        }
    } else if(inputType=='情报报告') {
        var companyName = $.trim(mini.get("qb_companyName").getValue());
        if (companyName) {
            queryParam.push({name: "companyName", value: companyName});
        }
        var projectName = $.trim(mini.get("qb_projectName").getValue());
        if (projectName) {
            queryParam.push({name: "projectName", value: projectName});
        }
        var qbgzType = $.trim(mini.get("qb_qbgzType").getValue());
        if (qbgzType) {
            queryParam.push({name: "qbgzType", value: qbgzType});
        }
        var qbName = $.trim(mini.get("qb_qbName").getValue());
        if (qbName) {
            queryParam.push({name: "qbName", value: qbName});
        }
    }

    var inputList='';
    if(inputType=='标准') {
        inputList = standardListGrid;
    } else if(inputType=='专利') {
        inputList = zlListGrid;
    } else if(inputType=='情报报告') {
        inputList = qbListGrid;
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = inputList.getPageIndex();
    data.pageSize = inputList.getPageSize();
    data.sortField = inputList.getSortField();
    data.sortOrder = inputList.getSortOrder();
    //查询
    inputList.load(data);
}

function onRowDblClick() {
    selectInputOK();
}

function selectInputOK() {
    var inputType = mini.get("inputType").getValue();
    var inputList='';
    if(inputType=='标准') {
        inputList = standardListGrid;
    } else if(inputType=='专利') {
        inputList = zlListGrid;
    } else if(inputType=='情报报告') {
        inputList = qbListGrid;
    }
    var selectRow = inputList.getSelected();
    if (selectRow) {
        var referId='';
        var inputName='';
        var inputNumber = '';
        if(inputType=='标准') {
            referId = selectRow.id;
            inputName=selectRow.standardName;
            inputNumber=selectRow.standardNumber;
        } else if(inputType=='专利') {
            referId = selectRow.zgzlId;
            inputName=selectRow.patentName;
            inputNumber=selectRow.applicationNumber;
        } else if(inputType=='情报报告') {
            referId = selectRow.qbgzId;
            inputName=selectRow.qbName;
            inputNumber=selectRow.qbNum;
        }

        mini.get("referId").setValue(referId);
        mini.get("referId").setText(inputName);
        mini.get("inputNumber").setValue(inputNumber);
    } else {
        mini.alert("请选择一条数据！");
        return;
    }
    selectInputHide();
}

function selectInputHide() {
    var inputType = mini.get("inputType").getValue();
    if(inputType=='标准') {
        selectStandardWindow.hide();
        mini.get("filterSystemCategory").setValue('JS');
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
    } else if(inputType=='专利') {
        selectZlWindow.hide();
        mini.get("zl_applicationNumber").setValue('');
        mini.get("zl_patentName").setValue('');
    } else if(inputType=='情报报告') {
        selectQbWindow.hide();
        mini.get("qb_companyName").setValue('');
        mini.get("qb_projectName").setValue('');
        mini.get("qb_qbgzType").setValue('');
        mini.get("qb_qbName").setValue('');
    }
}