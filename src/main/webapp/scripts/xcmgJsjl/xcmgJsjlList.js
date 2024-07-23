$(function () {
    queryDimension();
});
function queryDimension() {
    $.ajax({
        url: jsUseCtxPath + '/jsjl/core/config/dimensionListQuery.do?scene='+scene,
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#dimension").load(data);
            }
        }
    });
}

function renderMeetingUserNames(e) {
    var record = e.record;
    var html = "<div style='display:table-cell;vertical-align:middle;line-height: 20px;height:150px;overflow: auto;text-align: center' >";
    var meetingUserNames = record.meetingUserNames;
    if (meetingUserNames == null) {
        meetingUserNames = "";
    }
    html += '<span style="white-space:pre-wrap;" >' + meetingUserNames + '</span>';
    html += '</div>'
    return html;
}

function renderContent(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var contentAndConclusion = record.contentAndConclusion;
    if (contentAndConclusion == null) {
        contentAndConclusion = "";
    }
    html += '<span style="white-space:pre-wrap" >' + contentAndConclusion + '</span>';
    html += '</div>'
    return html;
}

function renderPlan(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var planAndResult = record.planAndResult;
    if (planAndResult == null) {
        planAndResult = "";
    }
    html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
    html += '</div>'
    return html;
}

function addJsjl() {
    var url = jsUseCtxPath + "/jsjl/core/editPage.do?jsjlId=&action=add&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jsjlListGrid) {
                jsjlListGrid.reload();
            }
            ;
        }
    }, 1000);
}

function detailJsjlRow(jsjlId) {
    var url = jsUseCtxPath + "/jsjl/core/editPage.do?jsjlId=" + jsjlId + "&action=detail&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jsjlListGrid) {
                jsjlListGrid.reload();
            }
            ;
        }
    }, 1000);
}

function editJsjlRow(jsjlId) {
    var url = jsUseCtxPath + "/jsjl/core/editPage.do?jsjlId=" + jsjlId + "&action=edit&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jsjlListGrid) {
                jsjlListGrid.reload();
            }
            ;
        }
    }, 1000);
}

function feedBackJsjlRow(jsjlId) {
    var url = jsUseCtxPath + "/jsjl/core/editPage.do?jsjlId=" + jsjlId + "&action=feedback&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jsjlListGrid) {
                jsjlListGrid.reload();
            }
            ;
        }
    }, 1000);
}

function removeJsjlRow(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jsjlListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (r.recordStatus == '草稿' && currentUserId == r.CREATE_BY_)) {
                    rowIds.push(r.id);
                }
            }
            if (rowIds.length <= 0) {
                mini.alert("仅可删除本人创建的尚未提交的数据！");
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/jsjl/core/deleteData.do",
                method: 'POST',
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchFrm();
                    }
                }
            });
        }
    });
}