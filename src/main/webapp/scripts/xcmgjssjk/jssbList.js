
$(function () {
    searchFrm();
});



function addJssb() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JSSB/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jssbListGrid) {
                jssbListGrid.reload()
            }
        }
    }, 1000);
}
function jssbEdit(jssbId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&jssbId=" + jssbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jssbListGrid) {
                jssbListGrid.reload()
            }
        }
    }, 1000);
}


function jssbDetail(jssbId,status) {
    var action = "detail";
    var url = jsUseCtxPath + "/Jssb/editPage.do?action=" + action + "&jssbId=" + jssbId+ "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jssbListGrid) {
                jssbListGrid.reload()
            }
        }
    }, 1000);
}

function jssbTask(taskId) {
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
                        if (jssbListGrid) {
                            jssbListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}


function removeJssb(jssbId) {
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/Jssb/deleteJssb.do",
                method: 'POST',
                showMsg:false,
                data: {id: jssbId},
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

function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/Jssb/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}
function sendmail() {
    $.ajax({
        url: jsUseCtxPath + '/Jssb/sendemail.do',
        type: 'post',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.alert(data.message, "提示消息", function (action) {
                    if (action == 'ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });

}
function exportJssb() {
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
