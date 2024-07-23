$(function () {
    let progressListGrid = mini.get("progressListGrid");
    progressListGrid.load();
})

/**项目进度情况*/
function jumpToDetail(e) {
    var record = e.record;
    var projectId = record.projectId;
    var s = '<a href="#" style="color:#f75509;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId + '\',\'' + record.status + '\')">' + record.projectName + '</a>';
    return s;
}

function onStatusRenderer(e) {
    var record = e.record;
    var status = record.status;
    var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];
    return $.formatItemValue(arr, status);
}

function onProgressRenderer(e) {
    var record = e.record;
    var projectId = record.projectId;
    var s = '<div class="mini-progressbar" id="p1" style="border-width: 0px;color: #0a7ac6"><div class="mini-progressbar-border"><div class="mini-progressbar-bar" style="width: ' + record.progressNum + '%;"></div><div class="mini-progressbar-text" id="p1$text">' + record.progressNum + '%</div></div></div>';
    return s;
}

function onRiskRenderer(e) {
    var record = e.record;
    var hasRisk = record.hasRisk;

    var color = '#32CD32';
    var title = xcmgProjectDeskHome_name;
    if (hasRisk == 1) {
        color = '#EEEE00';
        title = xcmgProjectDeskHome_name1;
    } else if (hasRisk == 2) {
        color = '#fb0808';
        title = xcmgProjectDeskHome_name2;
    } else if (hasRisk == 3) {
        color = '#cccccc';
        title = xcmgProjectDeskHome_name3;
    } else if (hasRisk == 4) {
        color = '#9B00FCFF';
        title = xcmgProjectDeskHome_name4;
    }
    var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
    return s;
}
//明细
function detailProjectRow(projectId,status) {
    var action = "detail";
    window.open(jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status="+status);
}

