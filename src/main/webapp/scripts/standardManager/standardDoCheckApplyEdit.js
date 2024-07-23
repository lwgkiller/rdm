var stageName="";
$(function () {
    if(applyId) {
        var url = jsUseCtxPath + "/standard/core/doCheck/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                doCheckForm.setData(json);
            }
        );
    }
    checkGrid.hideColumn("confirmPlan");
    checkGrid.hideColumn("confirmResult");
    if(action == 'edit') {
        mini.get("szrUserId").setEnabled(false);
        mini.get("checkListToolBar").hide();
        var url = jsUseCtxPath+"/standard/core/doCheck/checkDetailList.do?applyId="+applyId;
        checkGrid.setUrl(url);
        checkGrid.load();
        checkGrid.setAllowCellEdit(false);
    } else if(action == 'detail') {
        doCheckForm.setEnabled(false);
        mini.get("checkListToolBar").hide();
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        var url = jsUseCtxPath+"/standard/core/doCheck/checkDetailList.do?applyId="+applyId;
        checkGrid.setUrl(url);
        checkGrid.load();
        checkGrid.setAllowCellEdit(false);
        if (status == 'SUCCESS_END') {
            checkGrid.showColumn("confirmResult");
        }
    } else if(action=='task') {
        taskActionProcess();
    }
});


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'stageName') {
            stageName = nodeVars[i].DEF_VAL_;
        }
    }
    var url = jsUseCtxPath+"/standard/core/doCheck/checkDetailList.do?applyId="+applyId+"&stageName="+stageName;
    checkGrid.setUrl(url);
    checkGrid.load();

    if(stageName=='start') {
        mini.get("szrUserId").setEnabled(false);
        mini.get("checkListToolBar").hide();
    } else if(stageName=='shqkWrite') {
        //起草人填写审核情况
        doCheckForm.setEnabled(false);
        mini.get("szrUserId").setEnabled(true);
    } else if(stageName=='zgPlanWrite') {
        //责任人填写整改计划
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
    } else if(stageName=='confirmPlan') {
        //标准化对接人确认计划
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
        checkGrid.showColumn("confirmPlan");
    } else if(stageName=='zgPlanWriteAgain') {
        //计划判定不符合，重新填写整改计划
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
    } else if(stageName=='zgResultWrite') {
        //责任人填写问题关闭结果说明
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
    } else if(stageName=='confirmResult') {
        //标准化对接人确认结果
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
        checkGrid.showColumn("confirmResult");
    } else if(stageName=='zgResultWriteAgain') {
        //责任人填写问题关闭结果说明
        mini.get("checkListToolBar").hide();
        doCheckForm.setEnabled(false);
    } else {
        doCheckForm.setEnabled(false);
        mini.get("checkListToolBar").hide();
        checkGrid.setAllowCellEdit(false);
    }

}

function addCheck() {
    var row = {confirmPlan:"符合",confirmResult:"符合"};
    checkGrid.addRow(row);
}

function delCheck() {
    var selecteds = checkGrid.getSelecteds();
    if(selecteds && selecteds.length>0) {
        mini.confirm(standardDoCheckApplyEdit_name4, standardDoCheckApplyEdit_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                checkGrid.removeRows(selecteds);
            }
        });
    }
}

function getData() {
    var formData = _GetFormJsonMini("doCheckForm");
    if(checkGrid.getChanges().length > 0){
        formData.changeCheckGrid=checkGrid.getChanges();
    }
    return formData;
}

//保存草稿
function saveDraft(e) {
    var standardNumber=mini.get("standardId").getText();
    if(!standardNumber) {
        mini.alert(standardDoCheckApplyEdit_name6);
        return;
    }
    window.parent.saveDraft(e);
}

//发起流程
function startProcess(e) {
    var standardNumber=mini.get("standardId").getText();
    if(!standardNumber) {
        mini.alert(standardDoCheckApplyEdit_name6);
        return;
    }
    var firstWriterName=mini.get("firstWriterId").getText();
    if(!firstWriterName) {
        mini.alert(standardDoCheckApplyEdit_name7);
        return;
    }
    var djrUserName=mini.get("djrUserId").getText();
    if(!djrUserName) {
        mini.alert(standardDoCheckApplyEdit_name8);
        return;
    }

    window.parent.startProcess(e);
}

//下一步审批
function applyApprove() {
    //编制阶段
    if(stageName=='start') {
        var standardNumber=mini.get("standardId").getText();
        if(!standardNumber) {
            mini.alert(standardDoCheckApplyEdit_name6);
            return;
        }
        var firstWriterName=mini.get("firstWriterId").getText();
        if(!firstWriterName) {
            mini.alert(standardDoCheckApplyEdit_name7);
            return;
        }
        var djrUserName=mini.get("djrUserId").getText();
        if(!djrUserName) {
            mini.alert(standardDoCheckApplyEdit_name8);
            return;
        }
    }
    var checkGridData = checkGrid.getData();
    //起草人填写审核情况
    if(stageName=='shqkWrite') {
        var szrUserName=mini.get("szrUserId").getText();
        if(!szrUserName) {
            mini.alert(standardDoCheckApplyEdit_name9);
            return;
        }
        if(!checkGridData || checkGridData.length<5) {
            mini.alert(standardDoCheckApplyEdit_name10);
            return;
        }
        for(var index=0;index<checkGridData.length;index++) {
            var oneData= checkGridData[index];
            var indexNum=oneData._uid;
            if(!oneData.fileType) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name12);
                return;
            }
            if(!oneData.fileName) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name13);
                return;
            }
            if(!oneData.filePath) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name14);
                return;
            }
            if(!oneData.useDesc) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name15);
                return;
            }
            if(!oneData.judge) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name16);
                return;
            }
            if(oneData.judge=='否' && !oneData.respUserName) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name17);
                return;
            }
        }
    }

    //责任人填写整改计划
    if(stageName=='zgPlanWrite' || stageName=='zgPlanWriteAgain') {
        for(var index=0;index<checkGridData.length;index++) {
            var oneData= checkGridData[index];
            var indexNum=oneData._uid;
            if(oneData.judge=='否' && !oneData.detailTypes && oneData.respUserId==currentUserId) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name18);
                return;
            }
            if(oneData.judge=='否' && !oneData.modifyMethod && oneData.respUserId==currentUserId) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name19);
                return;
            }
            if(oneData.judge=='否' && !oneData.planFinishTime && oneData.respUserId==currentUserId) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name20);
                return;
            }
            if(oneData.judge=='否' && !oneData.closeRespUserId && oneData.respUserId==currentUserId) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name21);
                return;
            }
        }
    }
    //责任人填写问题关闭结果说明
    if(stageName=='zgResultWrite' || stageName=='zgResultWriteAgain') {
        for(var index=0;index<checkGridData.length;index++) {
            var oneData= checkGridData[index];
            var indexNum=oneData._uid;
            if(oneData.judge=='否' && !oneData.closeDesc && oneData.closeRespUserId==currentUserId) {
                mini.alert(standardDoCheckApplyEdit_name11+indexNum+standardDoCheckApplyEdit_name22);
                return;
            }
        }
    }

    //检查通过
    window.parent.approve();
}

//暂存信息
function saveInProcess() {
    var formData =getData();

    var json =mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/standard/core/doCheck/saveInProcess.do',
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    window.location.reload();
                } else {
                    mini.alert(standardDoCheckApplyEdit_name23+data.message);
                }
            }
        }
    });
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: standardDoCheckApplyEdit_name24,
        width: 800,
        height: 600
    });
}

function standardNumberClick(){
    selectStandardWindow.show();
    mini.get("filterCategoryId").setData(categoryList);

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
    var standardCategoryId = $.trim(mini.get("filterCategoryId").getValue());
    if (standardCategoryId) {
        queryParam.push({name: "standardCategoryId", value: standardCategoryId});
    }

    queryParam.push({name: "systemCategoryId", value: 'JS'});
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
        mini.get("standardId").setValue(selectRow.id);
        mini.get("standardId").setText(selectRow.standardNumber);
        mini.get("standardName").setValue(selectRow.standardName);
        if(selectRow.publisherId && selectRow.publisherName && !mini.get("firstWriterId").getText()) {
            mini.get("firstWriterId").setValue(selectRow.publisherId.split(",")[0]);
            mini.get("firstWriterId").setText(selectRow.publisherName.split(",")[0]);
        }

    }
    selectStandardHide();
}

function selectStandardHide() {
    selectStandardWindow.hide();
    mini.get("filterStandardNumberId").setValue('');
    mini.get("filterStandardNameId").setValue('');
    mini.get("filterCategoryId").setValue('');
}

function standardNumberCloseClick() {
    mini.get("standardId").setValue('');
    mini.get("standardId").setText('');
    mini.get("standardName").setValue("");
}

function closeProblemFileClick(detailId,judge) {
    mini.get("detailId").setValue(detailId);
    var url=jsUseCtxPath+"/standard/core/doCheck/checkDetailFileList.do?detailId="+detailId;
    fileListGrid.setUrl(url);
    fileListGrid.load();
    closeProblemFileWindow.show();
    if(action=='task' && (stageName=='zgResultWrite'||stageName=='zgResultWriteAgain') && judge=='否') {
        mini.get("cxFileUploadBtn").show();
    }
}


function uploadFile() {
    var baseInfoId = mini.get("id").getValue();
    if (!baseInfoId) {
        mini.alert(standardDoCheckApplyEdit_name25);
        return;
    }
    var detailId = mini.get("detailId").getValue();
    if (!detailId) {
        mini.alert(standardDoCheckApplyEdit_name25);
        return;
    }
    mini.open({
        title: standardDoCheckApplyEdit_name26,
        url: jsUseCtxPath + "/standard/core/doCheck/openUploadWindow.do",
        width: 900,
        height: 350,
        showModal:true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var passParams={};
            passParams.baseInfoId = baseInfoId;
            passParams.detailId = detailId;
            var data = { passParams: passParams };  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}
