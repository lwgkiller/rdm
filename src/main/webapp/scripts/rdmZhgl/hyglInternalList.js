//..
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
//..
function addMeeting() {
    var url = jsUseCtxPath + "/zhgl/core/hyglInternal/editPage.do?meetingId=&action=add&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (meetingListGrid) {
                meetingListGrid.reload();
            }
        }
    }, 1000);
}
//..
function detailMeeting(meetingId) {
    var url = jsUseCtxPath + "/zhgl/core/hyglInternal/editPage.do?meetingId=" + meetingId + "&action=detail&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (meetingListGrid) {
                meetingListGrid.reload();
            }
        }
    }, 1000);
}
//..
function editMeeting(meetingId) {
    var url = jsUseCtxPath + "/zhgl/core/hyglInternal/editPage.do?meetingId=" + meetingId + "&action=edit&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (meetingListGrid) {
                meetingListGrid.reload();
            }
        }
    }, 1000);
}
//..
function feedBackMeeting(meetingId) {
    var url = jsUseCtxPath + "/zhgl/core/hyglInternal/editPage.do?meetingId=" + meetingId + "&action=feedback&scene="+scene;
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (meetingListGrid) {
                meetingListGrid.reload();
            }
        }
    }, 1000);
}


//..
function removeMeeting(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = meetingListGrid.getSelecteds();
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
                url: jsUseCtxPath + "/zhgl/core/hyglInternal/deleteData.do",
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