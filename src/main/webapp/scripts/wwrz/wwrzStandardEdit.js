$(function () {
    if(action!='add'){
        infoForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        grid_sector.setData(applyObj.detailList);
    }
    if (action == 'view') {
        infoForm.setEnabled(false);
        $('#save').hide();
        mini.get('addSectorButton').setEnabled(false);
        mini.get('delProductButton').setEnabled(false);
        grid_sector.setAllowCellEdit(false);
    }
})
function saveData() {
    infoForm.validate();
    if (!infoForm.isValid()) {
        return;
    }
    var formData = _GetFormJsonMini("infoForm");
    formData.sectorData = grid_sector.getChanges();
    var json =mini.encode(formData);
    $.ajax({
        url:  jsUseCtxPath+"/wwrz/core/standard/save.do",
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败"+data.message;
                }
                mini.alert(message,"提示信息",function () {
                    mini.get('id').setValue(data.data.id);
                    CloseWindow();
                    editForm(data.data.id,'edit')
                });
            }
        }
    });
}
//修改
function editForm(id,action) {
    var url = jsUseCtxPath + "/wwrz/core/standard/getEditPage.do?action="+action+"&&id=" + id;
    var title = "编辑";
    if(action=='view'){
        title = '查看';
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
function addSector() {
    var row={};
    grid_sector.addRow(row);
}
function delSector() {
    var selecteds = grid_sector.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_sector.removeRows(deleteArr);
}
function sectorAttach(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    var editable = true;
    if (detailId == '' || detailId == 'undefined' || detailId == undefined) {
        s += '<span  title="附件上传" style="color: grey"">章节附件</span>';
    } else {
        s += '<span  title="附件上传" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'sectorAttach\',\'' + editable + '\')">章节附件</span>';
    }
    return s;
}
function showFilePage(detailId, fileType, editable) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/wwrz/core/file/fileWindow.do?detailId=" + detailId + "&fileType=" + fileType + "&editable=" + editable,
        width: 1000,
        height: 500,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
