//暂存表单数据，只对在线表单可用
function doSaveData() {
    var conf = {
        taskId: taskId,
        formType: formType
    };
    var boResult = getBoFormDataByType(conf.formType, false);
    if (!boResult.result) {
        alert("表单有内容尚未填写");
        return;
    }
    //表单验证处理。
    if (window._selfFormValidate) {
        var result = window._selfFormValidate(boResult.data, false);
        if (!result)  return;
    }
    if (window._asyncFormValidate) {
        _asyncFormValidate(boResult.data);
    }
    else {
        handSaveData(conf);
    }

}

/**
 * 保存数据。
 * @param conf
 * @returns
 */
function handSaveData(conf) {
    OfficeControls.save(function () {
        var boResult = getBoFormDataByType(conf.formType, false);
        var postData = {
            taskId: conf.taskId,
            jsonData: mini.encode(boResult.data)
        };
        _SubmitJson({
            url: __rootPath + '/bpm/core/bpmTask/doSaveData.do',
            data: postData,
            method: 'POST'
        });
    });
}


//作废
function discardInst(needApply) {
    /**
     * 针对作废流程，项目管理特殊处理，作废需要走流程
     * */
        //查询变更流程状态
    let solKey = getBpmSolution();
    if (solKey.indexOf('KJXMGL') != -1 && (!needApply || needApply != 'false')) {
        let result = getAbolishInfo();
        if (!result) {
            return;
        }
    }
    if (!confirm('流程作废后不可恢复，确定要继续吗?')) {
        return;
    }
    _SubmitJson({
        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
        data: {
            instId: instId
        },
        method: 'POST',
        success: function () {
            CloseWindow();
            //处理相关业务在作废后的切入处理
            callbackAop(instId, solKey);
            window.opener.grid.reload();
            window.opener.mini.showTips({
                content: "<b>成功</b> <br/>流程实例已作废",
                state: 'warning',
                x: 'center',
                y: 'center',
                timeout: 3000
            });
        }
    })
}

//处理相关业务在作废后的切入处理
function callbackAop(instId, solKey) {
    if (solKey.indexOf('ZLGJ') != -1) {
        //处新品装配中的异常闭环
        _SubmitJson({
            url: __rootPath + '/newproductAssembly/core/kanban/doSynZlgjToClearException.do',
            data: {
                instId: instId
            },
            method: 'POST',
            success: function () {
            }
        })
    }
}

//作废并删除
function discardInstAndDel(callBackDel) {
    /**
     * 针对作废流程，项目管理特殊处理，作废需要走流程
     * */
        //查询变更流程状态
    let solKey = getBpmSolution();
    if (solKey.indexOf('KJXMGL') != -1) {
        let result = getAbolishInfo();
        if (!result) {
            return;
        }
    }
    if (!confirm('流程作废并删除后不可恢复，确定要继续吗?')) {
        return;
    }
    _SubmitJson({
        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
        data: {
            instId: instId
        },
        method: 'POST',
        success: function () {
            if (callBackDel) {
                callBackDel();
            }
            CloseWindow();
            window.opener.grid.reload();
            window.opener.mini.showTips({
                content: "<b>成功</b> <br/>流程实例已作废",
                state: 'warning',
                x: 'center',
                y: 'center',
                timeout: 3000
            });
        }
    })
}

function getBpmSolution() {
    let solKey = "";
    let postData = {"solId": solId};
    $.ajax({
        async: false,
        url: __rootPath + '/xcmgProjectManager/core/xcmgProjectAbolish/getBpmSolution.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                solKey = returnData.KEY_;
            }
        }
    });
    return solKey;
}

//科技项目管理判断是否已有作废流程
function getAbolishInfo() {
    let taskId = parent.window.taskId;
    let postData = {"taskId_": taskId};
    let flag = false;
    $.ajax({
        async: false,
        url: __rootPath + '/xcmgProjectManager/core/xcmgProjectAbolish/abolishInfo.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (returnData) {
            let boResult = getBoFormDataByType(formType, false);
            if (returnData && returnData.status != undefined) {
                if (returnData.status == 'RUNNING') {
                    mini.alert("作废流程(" + returnData.id + ")正在审批,流程结束后再进行变更操作！");
                } else if (returnData.status == 'DISCARD_END') {
                    startAbolishFlow(boResult.data.projectId, taskId)
                    flag = false
                } else if (returnData.status == 'SUCCESS_END') {
                    flag = true;
                } else if (returnData.status == 'DRAFTED') {
                    window.open(__rootPath + "/bpm/core/bpmInst/start.do?instId=" + returnData.inst_id_ + "&id=" + returnData.id);
                } else {
                    startAbolishFlow(boResult.data.projectId, taskId)
                    flag = false
                }
            } else {
                startAbolishFlow(boResult.data.projectId, taskId)
                flag = false
            }
        }
    });
    return flag;
}
function startAbolishFlow(_projectId, _taskId) {
    mini.confirm("确定发起作废流程？", "确定？",
        function (action) {
            if (action == "ok") {
                let boResult = getBoFormDataByType(formType, false);
                window.open(__rootPath + "/bpm/core/bpmInst/XMZFLCSP/start.do?projectId=" + _projectId + "&taskId_=" + _taskId);
            }
        }
    );
}


function doTransfer() {
    var url = __rootPath + '/bpm/core/bpmTask/transfer.do?taskIds=' + taskId;
    _OpenWindow({
        title: '任务转办',
        url: url,
        height: 350,
        width: 1000,
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}

function addSign() {
    var url = __rootPath + '/bpm/core/bpmTask/addSign.do?taskId=' + taskId;
    _OpenWindow({
        title: '任务加签',
        url: url,
        height: 350,
        width: 800,
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}


function rejectTask(conf) {
    var url = __rootPath + '/bpm/core/bpmTask/reject.do?taskId=' + taskId;
    var boResult = getBoFormDataByType(conf.formType, true);
    var jsonData = mini.encode(boResult.data);
    _OpenWindow({
        title: '任务驳回',
        url: url,
        height: 400,
        width: 800,
        onload: function () {
            var iframe = this.getIFrameEl().contentWindow;
            iframe.setFormDataJson(jsonData);
        },
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}


function communicate() {
    var url = __rootPath + '/bpm/core/bpmTask/commu.do?taskId=' + taskId;
    _OpenWindow({
        title: '任务沟通',
        url: url,
        height: 350,
        width: 800,
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}


function revokeCommunicate() {
    var url = __rootPath + '/bpm/core/bpmTask/revoke.do?taskId=' + taskId;
    _OpenWindow({
        title: '撤销沟通',
        url: url,
        height: 350,
        width: 800,
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}

function replyCommu() {
    var url = __rootPath + '/bpm/core/bpmTask/replyCommu.do?taskId=' + taskId;
    _OpenWindow({
        title: '回复沟通',
        url: url,
        height: 350,
        width: 800,
        ondestroy: function (action) {
            if (action == "ok") {
                CloseWindow('ok');
            }
        }
    });
}


/**
 * 获取用户数据。
 * @param data
 * @returns
 */
function handUsers(data) {
    var destNodeUsers = [];
    var flag = true;
    var destNodeId = $("input[name='destNodeId']:checked").val();

    if (destNodeId) {
        data.destNodeId = destNodeId;
    }
    else {
        var obj = mini.get("destNodeId");
        if (obj) {
            data.destNodeId = obj.getValue();
        }
    }
    $(".checkusers").each(function () {
        var id = $(this).attr('id');
        //获得目标节点选择的人员
        var usrSel = mini.get(id);
        userIds = usrSel.getValue();
        //获得节点Id
        var nodeId = usrSel.nodeId;
        //加入目标节点的配置
        destNodeUsers.push({
            nodeId: nodeId,
            userIds: userIds
        });
        if (!data.destNodeId && !userIds) {
            flag = false;
        } else if (data.destNodeId == nodeId && !userIds) {
            flag = false;
        }
    });

    //加上目标节点的人员配置
    data.destNodeUsers = mini.encode(destNodeUsers);
    return flag;
}


/**
 * 选择用户。
 * @param seqId
 * @returns
 */
function selectUsers(seqId) {
    //为自由选择用户
    var userBox = mini.get('users' + seqId);

    var tempVal = userBox.getValue();
    var checkBoxData = userBox.getData();
    _UserDlg(false, function (users) {
        var ids = [];
        var fullnames = [];
        for (var i = 0; i < users.length; i++) {
            var addOrNot = true;
            //如果之前的数据里有,则不添加
            for (var j = 0; j < checkBoxData.length; j++) {
                if (checkBoxData[j].id == users[i].userId) {
                    addOrNot = false;
                }
            }
            if (addOrNot) {
                checkBoxData.push({id: users[i].userId, text: users[i].fullname});
                tempVal += "," + users[i].userId;
            }

        }
        userBox.loadData(checkBoxData);
        userBox.setValue(tempVal);
    });
}

function exportPDF() {
    window.location.href = __rootPath + '/bpm/form/bpmFormView/exportPdfByTaskId.do?taskId=' + taskId;
}

/**
 * 初始化表单
 * @returns
 */
function initForm() {

    if (formType != "SEL-DEV") {
        //解析表单。
        renderMiniHtml({});
    }
    else {
        mini.parse();
        // autoHeightOnLoad($("#formFrame"));
        mini.layout();
    }
    //当没有权限时禁止按钮
    handBttons();
}

function handBttons() {
    var btnApprove = mini.get("btnApprove");
    var btnReject = mini.get("btnReject");
    var btnTransfer = mini.get("btnTransfer");
    if (btnApprove) {
        btnApprove.setEnabled(allowTask);
    }
    if (btnReject) {
        btnReject.setEnabled(allowTask);
    }
    if (btnTransfer) {
        btnTransfer.setEnabled(allowTask);
    }

}

function formPrint() {
    var conf = {
        taskId: taskId,
    }
    printForm(conf);
}

/**
 * 获取表单内意见
 * @param postData
 * @returns
 */
function handFormOpinion(boResult, postData) {
    var bos = boResult.data.bos;
    if (!bos) return;

    var opinions = $("[plugins='mini-nodeopinion']");
    if (opinions.length > 0) {
        var obj = $(opinions[0]);
        var name = obj.attr("name").replace("FORM_OPINION_", "");
        var val = obj.val();
        postData.opinion = val;
        postData.opinionName = name;
    }
}

/**
 * 审批
 * @returns
 */
function approveTask(conf) {
    var boResult = getBoFormDataByType(conf.formType, true);
    if (!boResult.result) {
        alert("表单有内容尚未填写");
        return;
    }
    //表单验证处理。
    if (window._selfFormValidate) {
        var result = window._selfFormValidate(boResult.data, false);
        if (!result)  return;
    }
    //
    if (window._asyncFormValidate) {
        _asyncFormValidate(boResult.data, conf);
    }
    else {
        handTaskApprove(conf);
    }
}

/**
 * 对任务进行审批
 * @param conf
 * @returns
 */
function handTaskApprove(conf) {
    OfficeControls.save(function () {
        var postData = {
            taskId: conf.taskId,
            token: conf.token
        };
        var boResult = getBoFormDataByType(conf.formType, true);
        //意见获取
        handFormOpinion(boResult, postData);
        postData.jsonData = mini.encode(boResult.data);

        var url = __rootPath + '/bpm/core/bpmTask/approve.do?taskId=' + conf.taskId + '&jumpType=AGREE';
        _OpenWindow({
            title: '任务审批',
            url: url,
            height: 450,
            width: 800,
            onload: function () {
                var iframe = this.getIFrameEl().contentWindow;
                iframe.setPostData(postData);
            },
            ondestroy: function (action) {
                if (action == "ok") {
                    CloseWindow('ok');
                }
            }
        });
    })
}


function openTaskFlowImg() {
    var boResult = getBoFormDataByType(formType, true);
    var jsonData = mini.encode(boResult.data);
    openFlowImgDialog({taskId: taskId, jsonData: jsonData});
}

function openBpmOpinions() {
    openBpmOpinionsDialog(procInstId);
}

function openBpmMessage() {
    openBpmMessageDialog(instId)

}

function onSelectPath(e) {
    var btn = e.sender;
    openSolutionNode(actDefId, {single: true, end: false, start: false}, function (data) {
        var row = data[0];
        btn.setText(row.name);
        btn.setValue(row.activityId);
    })
}

function startSubProcess(alias) {
    var boResult = getBoFormDataByType(formType, true);
    var config = {
        url: __rootPath + "/bpm/core/bpmInst/getSubProcessFormData.do",
        method: 'POST',
        data: {solKey: alias, mainSolId: solId, formData: mini.encode(boResult.data.bos[0].data)},
        showMsg: false,
        success: function (result) {
            var url = __rootPath + "/bpm/core/bpmInst/" + alias + "/start.do?mainSolId=" + solId + "&taskId=" + taskId;

            _OpenWindow({
                title: '发起流程审批',
                width: 800,
                height: 550,
                url: url,
                onload: function () {
                    var iframe = this.getIFrameEl().contentWindow;
                    iframe.setFormData('processForm', result.data.formData);
                    iframe.setConfVars(result.data.vars);
                },
                ondestroy: function (action) {
                }
            });
        }
    }
    _SubmitJson(config);
}


/**
 * 启动流程
 * @param uid
 * @returns
 */
function doStartProcess(solId) {
    var url = __rootPath + '/bpm/core/bpmInst/start.do?solId=' + solId;
    _OpenWindow({
        title: "启动流程",
        max: true,
        url: url,
        onload: function () {
            var boResult = getBoFormDataByType("ONLINE-DESIGN", false);
            var win = this.getIFrameEl().contentWindow;
            var data = boResult.data.bos[0].data;
            win.setData(data, procInstId, taskId);
        },
        ondestroy: function (action) {

        }
    });
}

function transferTaskSelf(orgiUserId, taskId) {
    var users = [];
    _UserDialog({
        single: true, users: users, callback: function (user) {
            var userIds = [];
            for (var i = 0; i < users.length; i++) {
                userIds.push(users[i].userId);
            }
            if (!user || user.userId == orgiUserId) {
                return;
            }
            _SubmitJson({
                url: __rootPath + '/bpm/core/bpmTask/transferTaskSelf.do',
                data: {
                    taskId: taskId,
                    userId: user.userId
                },
                success: function (result) {
                    CloseWindow('ok');
                }
            });
        }
    });
}

