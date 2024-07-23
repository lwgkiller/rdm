$(function () {
    searchFrm();
});


//新增流程（后台根据配置的表单进行跳转）
function addTotal() {
    var action = "add";
    var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action="+action;
    var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url="+encodeURIComponent(url));
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (totalListGrid) {
                totalListGrid.reload();
            }
        }
    }, 1000);
}

//copy复制流程
function copyTotal() {
    var row = totalListGrid.getSelected();
    if (!row) {
        mini.alert("请选择被复制的项目！")
        return;
    }
    var applyId = row.id;
    var action = "copy";
    var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action="+action+"&id="+applyId;
    var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url="+encodeURIComponent(url));
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (totalListGrid) {
                totalListGrid.reload();
            }
        }
    }, 1000);
}

//查看编辑
function totalDecompose(applyId,action,flag) {
    var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do?action="+action+"&id="+applyId+"&flag="+flag;
    var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url="+encodeURIComponent(url));
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (totalListGrid) {
                totalListGrid.reload();
            }
        }
    }, 1000);
}

function removeTotal(applyId) {
    mini.confirm("确定删除选中记录（已创建部件分析项目的，需要将部件项目删除后才允许删除总项目）？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            rowIds.push(applyId);
            _SubmitJson({
                url: jsUseCtxPath + "/drbfm/total/deleteTotal.do",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(',')},
                success: function (returnData) {
                    if (returnData) {
                        mini.alert(returnData.message);
                        searchFrm();
                    }
                }
            });
        }
    });
}