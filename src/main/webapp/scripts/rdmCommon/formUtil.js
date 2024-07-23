//表单中上一步任务的处理情况等提示信息处理
function bpmPreTaskTipInForm() {
    var preNodeHandler = "---";
    var preNodeResult = '<span>---</span>';
    var preNodeName="---";
    var preNodeRemark="---";
    if (window.parent.actInstId) {
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getPreHandleResult.do?actInstId=' + window.parent.actInstId,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.handler) {
                        preNodeHandler = data.handler;
                        if(data.owner) {
                            preNodeHandler+=" 代（"+data.owner+"）";
                        }
                    }
                    if (data.resultText) {
                        preNodeResult = getPreHandleResult(data.result, data.resultText);
                    }
                    if (data.remark) {
                        preNodeRemark = data.remark;
                    }
                    if (data.nodeName) {
                        preNodeName = data.nodeName;
                    }
                }
            }
        });
    }
    $("#preNodeName", window.parent.document).html(preNodeName);
    $("#preNodeHandler", window.parent.document).html(preNodeHandler);
    $("#preNodeResult", window.parent.document).html(preNodeResult);
    $("#preNodeRemark", window.parent.document).html(preNodeRemark);
}
function getPreHandleResult(handleResult, handleResultText) {
    var arr = [{'key': 'BACK', 'value': handleResultText, 'css': 'red'},
        {'key': 'BACK_TO_STARTOR', 'value': handleResultText, 'css': 'red'},
        {'key': 'BACK_SPEC', 'value': handleResultText, 'css': 'red'},
        {'key': 'AGREE', 'value': handleResultText, 'css': 'green'},
        {'key': 'TRANSFER', 'value': handleResultText, 'css': 'orange'}
    ];

    return $.formatItemValue(arr, handleResult);
}
function whetherIsProjectManager(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='技术中心项目管理人员') {
                return true;
            }
        }
    }
    return false;
}

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    if(!instId) {
        return;
    }
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}