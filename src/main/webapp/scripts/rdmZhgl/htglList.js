//渲染是否列
function onIsOkRenderer(e) {
    return e.value == "1" ? '<span class = "myrow">' + "是" + '</span>' : "否";
}
//..
function addContract() {
    var url = jsUseCtxPath + "/zhgl/core/htgl/editPage.do?contractId=&action=add";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (contractListGrid) {
                contractListGrid.reload();
            }
        }
    }, 1000);
}
//..
function detailContractRow(contractId) {
    var url = jsUseCtxPath + "/zhgl/core/htgl/editPage.do?contractId=" + contractId + "&action=detail";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (contractListGrid) {
                contractListGrid.reload();
            }
        }
    }, 1000);
}
//..
function editContractRow(contractId) {
    var url = jsUseCtxPath + "/zhgl/core/htgl/editPage.do?contractId=" + contractId + "&action=edit";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (contractListGrid) {
                contractListGrid.reload();
            }
        }
    }, 1000);
}
//..
function removeContractRow(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = contractListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(htglList_name2);
        return;
    }
    mini.confirm(htglList_name3, htglList_name4, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (currentUserNo == 'admin' || currentUserNo == 'zhujia') {
                    rowIds.push(r.id);
                }
            }
            if (rowIds.length <= 0) {
                mini.alert(htglList_name5);
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/htgl/deleteData.do",
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
//..
function discardContract(){
    var rows = contractListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(htglList_name2);
        return;
    }
    mini.confirm('确定作废选中记录', '提示', function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (currentUserNo == 'admin' || currentUserNo == 'zhujia') {
                    rowIds.push(r.id);
                }
            }
            if (rowIds.length <= 0) {
                mini.alert('仅可管理员可以作废数据！');
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/htgl/discardData.do",
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
//..
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
//..
function copyContract(){
    var row = contractListGrid.getSelected();
    if (!row) {
        mini.alert(htglList_name6);
        return;
    }
    var url = jsUseCtxPath + "/zhgl/core/htgl/editPage.do?contractId=" + row.id + "&action=copy";
    var winObj = window.open(url, '');
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (contractListGrid) {
                contractListGrid.reload();
            }
        }
    }, 1000);
}


