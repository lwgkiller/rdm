function onRemarkRenderer(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var remark = record.remark;
    if (remark == null) {
        remark = "";
    }
    html += '<span style="white-space:pre-wrap" >' + remark + '</span>';
    html += '</div>'
    return html;
}

function onContractIndicatorsRenderer(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var contractIndicators = record.contractIndicators;
    if (contractIndicators == null) {
        contractIndicators = "";
    }
    html += '<span style="white-space:pre-wrap" >' + contractIndicators + '</span>';
    html += '</div>'
    return html;
}

function onCompletedIndicatorsRenderer(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var completedIndicators = record.completedIndicators;
    if (completedIndicators == null) {
        completedIndicators = "";
    }
    html += '<span style="white-space:pre-wrap" >' + completedIndicators + '</span>';
    html += '</div>'
    return html;
}

function onDelayReasonRenderer(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var delayReason = record.delayReason;
    if (delayReason == null) {
        delayReason = "";
    }
    html += '<span style="white-space:pre-wrap" >' + delayReason + '</span>';
    html += '</div>'
    return html;
}

//渲染是否列
function onIsOkRenderer(e) {
    return e.value == "1" ? '<span class = "myrow">' + "是" + '</span>' : "否";
}

function addCxyProject() {
    var url = jsUseCtxPath + "/zhgl/core/cxy/editPage.do?cxyProjectId=&action=add";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cxyProjectListGrid) {
                cxyProjectListGrid.reload();
            }
        }
    }, 1000);
}

function detailCxyProjectRow(cxyProjectId) {
    var url = jsUseCtxPath + "/zhgl/core/cxy/editPage.do?cxyProjectId=" + cxyProjectId + "&action=detail";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cxyProjectListGrid) {
                cxyProjectListGrid.reload();
            }
        }
    }, 1000);
}

function editCxyProjectRow(cxyProjectId) {
    var url = jsUseCtxPath + "/zhgl/core/cxy/editPage.do?cxyProjectId=" + cxyProjectId + "&action=edit";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cxyProjectListGrid) {
                cxyProjectListGrid.reload();
            }
        }
    }, 1000);
}

function feedBackCxyProjectRow(cxyProjectId) {
    var url = jsUseCtxPath + "/zhgl/core/cxy/editPage.do?cxyProjectId=" + cxyProjectId + "&action=feedback";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cxyProjectListGrid) {
                cxyProjectListGrid.reload();
            }
        }
    }, 1000);
}

function removeCxyProjectRow(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = cxyProjectListGrid.getSelecteds();
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
                if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (r.isSubmit == '0' && currentUserId == r.CREATE_BY_)) {
                    rowIds.push(r.id);
                }
            }
            if (rowIds.length <= 0) {
                mini.alert("仅可删除本人创建的尚未提交的数据！");
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/cxy/deleteData.do",
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

function exportBusiness() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}


