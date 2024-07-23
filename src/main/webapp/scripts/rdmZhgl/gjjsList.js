//明细
function gjjsDetail(pId) {
    var action = "detail";
    var url = gjjsPath + "/zhgl/core/gjjs/gjjsPage.do?pId=" + pId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjjsListGrid) {
                gjjsListGrid.reload()
            }
        }
    }, 1000);
}
//编辑
function gjjsEdit(pId) {
    var action = "edit";
    var url = gjjsPath + "/zhgl/core/gjjs/gjjsPage.do?pId=" + pId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjjsListGrid) {
                gjjsListGrid.reload()
            }
        }
    }, 1000);
}
//新增
function addNewgjjs() {
    var url = gjjsPath + "/zhgl/core/gjjs/gjjsPage.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjjsListGrid) {
                gjjsListGrid.reload()
            }
        }
    }, 1000);
}
function removeGjjs(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = gjjsListGrid.getSelecteds();
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
                rowIds.push(r.pId);
            }

            _SubmitJson({
                url: gjjsPath + "/zhgl/core/gjjs/deleteGjjs.do",
                method: 'POST',
                showMsg:false,
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

