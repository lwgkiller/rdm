$(function () {
    mini.get("taskName").setData(mini.decode(nodeSetListWithName));
    searchFrm();
});

function taskResourceRenderer(e) {
    var record = e.record;
    var taskResource = record.taskResource;
    var arr = [{key:'xpyf',value:'新品研发'},{key:'zyOrYycp',value:'在研/预研产品'},
        {key:'scgj',value:'市场改进'},{key:'hxjsyj',value:'核心技术研究'}];
    return $.formatItemValue(arr,taskResource);
}

function prototypeStateRenderer(e) {
    var record = e.record;
    var prototypeState = record.prototypeState;
    var arr = [{key:'yyj',value:'有样机'},{key:'wyj',value:'无样机'}];
    return $.formatItemValue(arr,prototypeState);
}

function syfzzxryRenderer(e) {
    var record = e.record;
    var fzsjId = record.id;
    $.ajax({
        url: jsUseCtxPath + "/fzsj/core/fzsj/getAllZxry.do?fzsjId="+fzsjId,
        method:'get',
        success:function (res) {
            console.log(res);
        }
    });
}

function gjztRender(e) {
    var record = e.record;
    var gjyj = record.gjyj;
    var taskStatus = record.taskStatus;
    var gjzt = '';
    var gjztCss = '';
    if (gjyj == 'tygj') {
        gjzt += '同意改进/';
        gjztCss = 'green';
    } else if (gjyj == 'bfgj') {
        gjzt += '部分改进/';
        gjztCss = 'orange';
    } else if (gjyj == 'btygj') {
        gjzt += '不同意改进';
        gjztCss = 'red';
    } else {
        gjzt += '进行中';
        gjztCss = 'blue';
    }
    var wczt = '';
    var wcztCss = '';
    if (gjyj == 'tygj' || gjyj == 'bfgj') {
        if (taskStatus == 'SUCCESS_END') {
            wczt += '完成改进';
            wcztCss = 'green';
        } else {
            wczt += '改进中';
            wcztCss = 'orange';
        }

    }
    var str = "<span class='"+gjztCss+"'>"+gjzt+"</span>" + "<span class='"+wcztCss+"'>"+wczt+"</span>";
    return str;
}

function taskStatusRenderer(e) {
    var record = e.record;
    var taskStatus = record.taskStatus;
    var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];
    return $.formatItemValue(arr,taskStatus);
}

//明细
function toFzrwhzDetail(fzsjId) {
    var action = "detail";
    var url = jsUseCtxPath + "/fzsj/core/fzsj/fzsjEditPage.do?action=" + action + "&fzsjId=" + fzsjId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (fzrwhzGrid) {
                fzrwhzGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function fzrwhzTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async:false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if(!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (fzrwhzGrid) {
                            fzrwhzGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

