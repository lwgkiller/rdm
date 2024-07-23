var step = "";
var taskStatus = "";
var fznl = "";
var isLeaf = false;
var sqlcsfjs = "";
var createById = "";
var isSendToSDM = "";
$(function () {
    if (fzsjId) {
        var url = jsUseCtxPath + "/fzsj/core/fzsj/getFzsjDetail.do?fzsjId=" + fzsjId;
        $.ajax({
            url: url,
            method: 'get',
            async: false,
            success: function (json) {
                fzsjForm.setData(json);
                taskStatus = json.taskStatus;
                //申请流程是否结束
                if (json.sqlcsfjs) {
                    sqlcsfjs = json.sqlcsfjs;
                }
                if (json.CREATE_BY_) {
                    createById = json.CREATE_BY_;
                }
                if (json.currentAblityLevel) {
                    fznl = json.currentAblityLevel;
                }
            }
        });
    } else {
        mini.get('taskType').setValue("independ_process");
    }
    fileListGrid.load();
    fzzxGrid.load();
    //明细入口CREATE_BY_

    //查找当前登录人负责或者参与的运行中的科技项目
    var queryProjectUserId = currentUserId;
    if (createById) {
        queryProjectUserId = createById;
    }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryParticipateProject.do?queryProjectUserId=' + queryProjectUserId,
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectId").load(data);
            }
        }
    });

    //明细入口
    if (action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (taskStatus && taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        fzsjForm.setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
        mini.get("addFile").setEnabled(false);
    } else if (action == 'task') {
        taskActionProcess();
    } else if (action == 'edit') {
        var createBy = mini.get("CREATE_BY_").getValue();
        if (!createBy) {
            mini.get("CREATE_BY_").setValue(currentUserId);
            mini.get("CREATE_BY_").setText(currentUserName);
        }
        var departmentId = mini.get("departmentId").getValue();
        if (!departmentId) {
            mini.get("departmentId").setValue(mainGroupId);
            mini.get("departmentId").setText(mainGroupName);
        }
        mini.get("addFzzx").setEnabled(false);
        mini.get("gjyj").setEnabled(false);
        mini.get("predictFinishTime").setEnabled(false);
        mini.get("gjyy").setEnabled(false);
        mini.get("gjhxntsfk").setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    mini.get("CREATE_BY_").setEnabled(false);
    mini.get("departmentId").setEnabled(false);
    mini.get("demandData").setEnabled(false);
    mini.get("applyTime").setEnabled(false);
    mini.get("fzdx").setEnabled(false);
    mini.get("isSendToSDM").setEnabled(false);
    //判断关联的科技项目是否能编辑
    var canEditProject = false;
    if (!fzsjId) {
        canEditProject = true;
    }
    if (fzsjId && sqlcsfjs != 'yes' && currentUserId == createById) {
        canEditProject = true;
    }
    if (fzsjId && sqlcsfjs == 'yes' && whetherIsProjectManager(currentUserRoles)) {
        canEditProject = true;
    }
    if (canEditProject) {
        mini.get("projectId").setEnabled(true);
        mini.get("projectId").show();
        mini.get("projectName").hide();
        mini.get("saveProjectInfo").show();
    } else {
        mini.get("projectId").setEnabled(false);
        mini.get("projectId").hide();
        mini.get("projectName").show();
        mini.get("saveProjectInfo").hide();
    }
    if (isSendToSDM != '') {
        mini.get("isSendToSDM").setEnabled(true);
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

        if (nodeVars[i].KEY_ == 'step') {
            step = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isSendToSDM') {
            isSendToSDM = nodeVars[i].DEF_VAL_;
        }
    }
    if (step == 'bianZhi') {
        mini.get("gjyj").setEnabled(false);
        mini.get("predictFinishTime").setEnabled(false);
        mini.get("gjyy").setEnabled(false);
        mini.get("gjhxntsfk").setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
        mini.get("addFile").setEnabled(false);
    } else if (step == 'jd') {
        fzsjForm.setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
        mini.get("addFile").setEnabled(false);
    } else if (step == 'fzszrshApply' || step == 'fzszrshApprove' || step == 'fzrwfp') {//..增加仿真任务分配节点
        fzsjForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    } //else if (step == 'bmfzrqr') {
    else if (step == 'fqrsh') {//由bmfzrqr转移到fqrsh
        fzsjForm.setEnabled(false);
        mini.get("gjyj").setEnabled(true);
        mini.get("predictFinishTime").setEnabled(true);
        mini.get("gjyy").setEnabled(true);
        mini.get("addFile").setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
    } else if (step == 'fqrqr') {
        fzsjForm.setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
        mini.get("gjhxntsfk").setEnabled(true);
    } else {
        fzsjForm.setEnabled(false);
        mini.get("addFzzx").setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("isSendToSDM").setEnabled(false);
    }
    if (isSendToSDM != "") {
        mini.get("isSendToSDM").setEnabled(true);
    } else {
        mini.get("isSendToSDM").setEnabled(false);
    }
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("fzsjForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'materialCode', val: formData.materialCode}];
    return formData;
}

//保存草稿
function saveFzsj(e) {
    window.parent.saveDraft(e);
}

//启动流程
function startFzsjProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    if (mini.get("idUrgent").getValue() == '是') {
        mini.confirm("紧急任务将由研究院副院长直接分配，确认继续吗？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                window.parent.startProcess(e);
            }
        })
    } else {
        window.parent.startProcess(e);
    }
}

//检验表单是否必填(编制)
function validBianZhi() {
    var checkResult = {};
    var questName = $.trim(mini.get("questName").getValue());
    if (!questName) {
        return {"result": false, "message": '请输入任务名称'};
    }
    var applicationType = $.trim(mini.get("applicationType").getValue());
    /*if (!applicationType) {
     return {"result": false, "message": '请输入应用机型'};
     }*/
    var taskResource = mini.get("taskResource").getValue();
    if (!taskResource) {
        return {"result": false, "message": '请选择任务来源'};
    }
    var prototypeState = mini.get("prototypeState").getValue();
    if (!prototypeState) {
        return {"result": false, "message": '请选择样机状态'};
    }
    var cpzgOrSzrId = mini.get("cpzgOrSzrId").getValue();
    if (!cpzgOrSzrId) {
        return {"result": false, "message": '请选择产品主管或室主任'};
    }
    var fzszrId = mini.get("fzszrId").getValue();
    if (!fzszrId) {
        return {"result": false, "message": '请选择仿真室主任'};
    }
    var fzlbId = mini.get("fzlbId").getValue();
    if (!fzlbId) {
        return {"result": false, "message": '请选择仿真分析项'};
    }
    var fzmd = $.trim(mini.get("fzmd").getValue());
    if (!fzmd) {
        return {"result": false, "message": '请输入仿真目的'};
    }
    if (!fznl || fznl < 2) {
        return {"result": false, "message": '能力等级大于等于2级的才允许申请!'};
    }
    checkResult.result = true;
    return checkResult;
}

//仿真室主任审核(申请流程)校验
function validFzszrsh() {
    var valid = true;
    $.ajax({
        url: jsUseCtxPath + "/fzsj/core/fzsj/fzszrshValid.do?fzsjId=" + fzsjId,
        async: false,
        success: function (res) {
            if (!res.success) {
                valid = false;
            }
        }
    });
    return valid;
}

//流程中暂存信息（如编制阶段）
function saveFzsjInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("fzsjForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/fzsj/core/fzsj/saveFzsj.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = "数据保存成功";
                } else {
                    message = "数据保存失败";
                }
                mini.alert(message, "提示信息", function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function fzsjApprove() {
    var valid = true;
    if (isSendToSDM != "") {
        var isSendToSDMValue = mini.get("isSendToSDM").getValue();
        if (!isSendToSDMValue) {
            mini.alert("请选择是否推送到SDM系统！");
            return;
        }
    }
    if (step == 'bianZhi') {//编制情况单独处理，需要询问
        valid = validBianZhi();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
        if (mini.get("idUrgent").getValue() == '是') {
            mini.confirm("紧急任务将由研究院副院长直接分配，确认继续吗？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    //检查通过
                    window.parent.approve();
                }
            })
        } else {
            //检查通过
            window.parent.approve();
        }
    } else {//非编制情况统一处理
        if (step == 'fzszrshApply' || step == 'fzszrshApprove' || step == 'fzrwfp') {//..增加仿真任务分配节点
            valid = validFzszrsh();
            if (!valid) {
                mini.alert("请新增仿真执行");
                return;
            }
        } else if (step == 'fzrwzx') {
            var lastFzzx = fzzxGrid.data[fzzxGrid.data.length - 1];
            if (!lastFzzx.fzjgjjy) {
                mini.alert("请输入仿真结果及建议");
                return;
            }
        } else if (step == 'fqrsh') {
            var lastFzzx = fzzxGrid.data[fzzxGrid.data.length - 1];
            if (!lastFzzx.confirmResult) {
                mini.alert("请选择确认结果");
                return;
            }
            if (!lastFzzx.star) {
                mini.alert("请评分！");
                return;
            }
            //..由bmfzrqr转移到fqrsh
            var gjyj = mini.get("gjyj").getValue();
            if (!gjyj) {
                mini.alert("请选择改进意见");
                return;
            }
            if (gjyj != 'btygj') {
                var predictFinishTime = mini.get("predictFinishTime").getValue();
                if (!predictFinishTime) {
                    mini.alert("请选择预计完成改进时间");
                    return;
                }
                var gjyy = mini.get("gjyy").getValue();
                if (!gjyy && gjyj == 'bfgj') {
                    mini.alert("请输入部分改进/不改进理由");
                    return;
                }
            } else if (gjyj == 'btygj') {
                var gjyy = mini.get("gjyy").getValue();
                if (!gjyy) {
                    mini.alert("请输入部分改进/不改进理由");
                    return;
                }
            }
        } else if (step == 'bmfzrqr') {
            // var gjyj = mini.get("gjyj").getValue();
            // if (!gjyj) {
            //     mini.alert("请选择改进意见");
            //     return;
            // }
            // if (gjyj != 'btygj') {
            //     var predictFinishTime = mini.get("predictFinishTime").getValue();
            //     if (!predictFinishTime) {
            //         mini.alert("请选择预计完成改进时间");
            //         return;
            //     }
            //     var gjyy = mini.get("gjyy").getValue();
            //     if (!gjyy) {
            //         if (!gjyy) {
            //             mini.alert("请输入部分改进/不改进理由");
            //             return;
            //         }
            //     }
            // }
        } else if (step == 'fqrqr') {
            var gjyj = mini.get("gjyj").getValue();
            if (gjyj != 'btygj') {
                var gjhxntsfk = mini.get("gjhxntsfk").getValue();
                var taskResource = mini.get("taskResource").getValue();
                var prototypeState = mini.get("prototypeState").getValue();
                if (taskResource == 'scgj') {
                    if (fileListGrid.totalCount == 0) {
                        mini.alert("请添加改进后附件");
                        return;
                    }
                } else if (taskResource == 'xpyf') {
                    if (prototypeState == 'yyj') {
                        if (fileListGrid.totalCount == 0) {
                            mini.alert("请添加改进后附件");
                            return;
                        }
                    } else {
                        if (fileListGrid.totalCount == 0 && !gjhxntsfk) {
                            mini.alert("请输入改进后反馈");
                            return;
                        }
                    }
                } else {
                    if (fileListGrid.totalCount == 0 && !gjhxntsfk) {
                        mini.alert("请输入改进后反馈");
                        return;
                    }
                }
            }
        }
        //检查通过
        window.parent.approve();
    }
}

function fzsjProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}


function addFzsjFile() {
    var id = mini.get("id").getValue();
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/fzsj/core/fzsj/openFzsjUploadWindow.do?belongDetailId=" + id,
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

function returnFzsjPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/fzsj/core/fzsj/fzsjFileDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/fzsj/core/fzsj/fzsjFileDownload.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/fzsj/core/fzsj/fzsjOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/fzsj/core/fzsj/fzsjImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    if (downloadPermission == 'true') {
        s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">下载</span>';
    }
    return s;
}

function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnFzsjPreviewSpan(record.fileName, record.id, record.belongDetailId, coverContent);
    if (step == 'fqrqr') {
        var deleteUrl = "/fzsj/core/fzsj/delFzsjFile.do";
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongDetailId + '\',\'' + deleteUrl + '\')">删除</span>';
    }
    return cellHtml;
}

function openFzlbWindow(e) {
    var btnEdit = e.sender;
    mini.open({
        url: jsUseCtxPath + "/fzsj/core/fzsj/openFzlbDialog.do",
        title: "仿真分析项",
        width: 1000,
        height: 600,
        ondestroy: function (action) {
            if (action == "ok") {
                var iframe = this.getIFrameEl();
                var data = iframe.contentWindow.getFzlbList();
                data = mini.clone(data);
                if (data) {
                    btnEdit.setValue(data.id);
                    btnEdit.setText(data.fzfxx);
                    mini.get("demandData").setValue(data.demandData);
                    mini.get("fzdx").setValue(data.fzdx);
                    fznl = data.currentAblityLevel;
                }
            }
        }
    });
}

function fzzxGridOptionRender(e) {
    var record = e.record;
    var s = '<span  title="明细" onclick="fzzxDetail(\'' + record.id + '\')">明细</span>';
    //..增加仿真任务分配节点
    if (((step == 'fzszrshApply' || step == 'fzszrshApprove' || step == 'fzrwfp') && !record.fzjgjjy) || ((step == 'fzrwzx' || step == 'fqrsh') && (e.rowIndex == (fzzxGrid.data.length - 1)))) {
        s += '<span  title="编辑" onclick="editFzzx(\'' + record.id + '\')">编辑</span>';
    }
    if ((step == 'fzszrshApply' || step == 'fzszrshApprove' || step == 'fzrwfp') && !record.fzjgjjy) {
        s += '<span  title="删除" onclick="delFzzx(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
    }
    return s;
}

function fzzxGridFileOptionRender(e) {
    var record = e.record;
    var detailType = 'ck';
    var s = '<span  title="查看" onclick="fzzxDetail(\'' + record.id + '\',\'' + detailType + '\')">查看</span>';
    return s;
}


function addFzzx() {
    var valid = validAddFzzx();
    if (!valid) {
        mini.alert("已存在可编辑的数据!");
        return;
    }
    var fzzxAction = "add";
    openFzzxDialog(fzzxAction, '');
}

function validAddFzzx() {
    var valid = '';
    $.ajax({
        url: jsUseCtxPath + '/fzsj/core/fzsj/fzzxAddValid.do?fzsjId=' + fzsjId,
        async: false,
        success: function (res) {
            valid = res.success;
        }
    })
    return valid;
}

function delFzzx(row) {
    mini.confirm("确定删除所选记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            $.ajax({
                url: jsUseCtxPath + '/fzsj/core/fzsj/deleteFzzx.do?fzzxId=' + row.id,
                async: false,
                success: function (res) {
                    mini.alert("删除成功");
                    fzzxGrid.load();
                }
            });
        }
    })

}

function fzzxDetail(fzzxId, detailType) {
    var fzzxAction = "detail";
    openFzzxDialog(fzzxAction, fzzxId, detailType);
}

function editFzzx(fzzxId) {
    var fzzxAction = "edit";
    openFzzxDialog(fzzxAction, fzzxId);
}
function openFzzxDialog(fzzxAction, fzzxId, detailType) {
    var id = mini.get("id").getValue();
    mini.open({
        url: jsUseCtxPath + "/fzsj/core/fzsj/openFzzxDialog.do?fzsjId=" + id + "&fzzxId=" + fzzxId + "&fzzxAction=" + fzzxAction +
        "&step=" + step + "&downloadPermission=" + downloadPermission + '&detailType=' + detailType,
        title: "仿真执行",
        width: 1200,
        height: 800,
        ondestroy: function (action) {
            fzzxGrid.load();
        }
    });
}

function confirmResultRenderer(e) {
    var record = e.record;
    var arr = [{key: 'ty', value: '同意'}, {key: 'bty', value: '不同意'}];
    return $.formatItemValue(arr, record.confirmResult);
}

function projectChange() {
    var projectName = mini.get("projectId").getText();
    mini.get("projectName").setValue(projectName);
}
function setTreeKey(e) {
    if (!isLeaf) {
        return
    }
    var tree = mini.get("applicationType");
    var node = e.node;
    var parentnode = tree.tree.getAncestors(node)
    var text = '';
    for (var i = 0; i < parentnode.length; i++) {
        text += parentnode[i].name + '-';
    }
    text += node.name;
    e.sender.setValue(text);
    e.sender.setText(text);
}
function onBeforeNodeSelect(e) {
    if (!e.isLeaf) {
        e.cancel = true;
        isLeaf = false;
    } else {
        isLeaf = true;
    }
}

//..作废特殊定制，作废完给历史审批者发个通知 todo:!!!!!!!!!!!
function discardInst() {
    if (!confirm('流程作废后不可恢复，确定要继续吗?')) {
        return;
    }
    ////////////////
    _SubmitJson({
        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
        data: {
            instId: mini.get("instId").getValue()
        },
        method: 'POST',
        success: function () {
            CloseWindow();
            window.opener.grid.reload();
            window.opener.mini.showTips({
                content: "<b>成功</b> <br/>流程实例已作废",
                state: 'warning',
                x: 'center',
                y: 'center',
                timeout: 3000
            });
            _SubmitJson({
                url: jsUseCtxPath + "/fzsj/core/fzsj/sendMessageAfterDiscard.do",
                method: 'POST',
                data: {
                    taskId: window.parent.taskId,
                    businessId: mini.get("id").getValue()
                },
                postJson: true,
                showMsg: false,
            });
        }
    })
}