$(function () {
    if (rjId) {
        var url = jsUseCtxPath + "/environment/core/Rjsc/getRjscDetail.do";
        $.post(
            url,
            {rjId: rjId},
            function (json) {
                formRjsc.setData(json);
            });
    }
    if (action == 'detail') {
        formRjsc.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
});



function save() {
    var formData = new mini.Form("formRjsc");
    //var formValid = validRjsc();
    // if (!formValid.result) {
    //     mini.alert(formValid.message);
    //     return;
    // }
    var data = formData.getData();
    var json = mini.encode(data);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rjsc/saveRjsc.do',
        type: 'POST',
        data: json,
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

function validRjsc() {
    // var companyName = $.trim(mini.get("companyName").getValue())
    // if (!companyName) {
    //     return {"result": false, "message": "请填写单位名称名称"};
    // }
    // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    // if(!fileListGrid) {
    //     return {"result": false, "message": "请添加附件"};
    // }
    //return {"result": true};
}


function fileupload() {
    var rjId = mini.get("rjId").getValue();
    if (!rjId) {
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjsc/openUploadWindow.do?rjId=" + rjId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}



