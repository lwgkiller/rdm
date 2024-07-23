$(function () {
    var year = new Date().getFullYear();
    var month = new Date().getMonth();
    var nowDate = year+"-"+month;
    mini.get('yearMonthPick').setValue(nowDate);
    searchFrm();
})

function addRow() {
    let url = jsUseCtxPath + "/portrait/attendance/attendanceEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}
//查看
function viewForm(id,action) {
    var url= jsUseCtxPath +"/portrait/attendance/attendanceEditPage.do?action="+action+"&id="+id;
    var title = "修改";
    if(action=='view'){
        title = "查看";
    }
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}
//删除记录
function removeRow() {
    var rows = [];
    rows = attendanceListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定取消选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/portrait/attendance/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (attendanceListGrid) {
                            attendanceListGrid.reload();
                        }
                    }
                });
            }

        }
    });
}
function openEditWindow() {
    monthSelectedWindow.show();
}
function closeEditWindow() {
    monthSelectedWindow.hide();
}
function asyncData() {
    monthForm.validate();
    if (!monthForm.isValid()) {
        mini.alert("月份必须选择");
        return;
    }
    var formData = monthForm.getData();
    mini.confirm("确定同步考勤数据？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var config = {
                url: jsUseCtxPath + "/portrait/attendance/asyncAttendance.do",
                method: 'POST',
                data: formData,
                success: function (result) {
                    //如果存在自定义的函数，则回调
                    var result = mini.decode(result);
                    if (result.success) {
                        closeEditWindow();
                        searchFrm();
                    } else {
                    }
                    ;
                }
            }
            _SubmitJson(config);
        }
    });
}
