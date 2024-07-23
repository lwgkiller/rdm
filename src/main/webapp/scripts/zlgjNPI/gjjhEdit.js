$(function () {
    if(id) {
        var url = jsUseCtxPath + "/zhgl/core/zlgj/getGjjhBaseInfo.do";
        $.post(
            url,
            {gjjhId: id},
            function (json) {
                formGjjh.setData(json);
            });
    }
    //明细入口
    if (action == 'detail') {
        formGjjh.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#saveBtn").hide();
    }
});


//检验表单是否必填
function validGjjh() {
    var formName=$.trim(mini.get("formName").getValue())
    if(!formName) {
        return {"result": false, "message": "请填写会议名称"};
    }
/*    var openTime=$.trim(mini.get("openTime").getValue())
    if(!openTime) {
        return {"result": false, "message": "请填写召开时间"};
    }
    var openPlace=$.trim(mini.get("openPlace").getValue())
    if(!openPlace) {
        return {"result": false, "message": "请填写召开地点"};
    }
    var ownerUserIds = $.trim(mini.get("ownerUserIds").getValue())
    if (!ownerUserIds) {
        return {"result": false, "message": '请选择主持人'};
    }*/
    var ownerDeptIds=$.trim(mini.get("ownerDeptIds").getValue())
    if(!ownerDeptIds) {
        return {"result": false, "message": "请选择组织部门"};
    }
    var joinUserNames=$.trim(mini.get("joinUserNames").getValue())
    if(!joinUserNames) {
        return {"result": false, "message": "请填写参会人员"};
    }
/*    var formTitle=$.trim(mini.get("formTitle").getValue())
    if(!formTitle) {
        return {"result": false, "message": "请填写会议议题"};
    }
    var recordUserNames=$.trim(mini.get("recordUserNames").getValue())
    if(!recordUserNames) {
        return {"result": false, "message": "请填写记录人"};
    }*/
    return {"result": true};
}


//保存
function saveGjjh() {
    var formValid = validGjjh();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formGjjh");
    if (formData.SUB_detailListGrid) {
        delete formData.SUB_detailListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/zlgj/saveGjjhBaseInfo.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (returnObj) {
            if (returnObj) {
                var message="";
                if(returnObj.success) {
                    message="数据保存成功";
                    mini.alert(message,"提示信息",function () {
                        window.location.href=jsUseCtxPath + "/zhgl/core/zlgj/gjjhEditPage.do?action=edit&id="+returnObj.data;
                    });
                } else {
                    message="数据保存失败，"+returnObj.message;
                    mini.alert(message,"提示信息");
                }
            }
        }
    });
}

function addGjjhDetail() {
    var formId=mini.get("id").getValue();
    if(!formId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    }
    mini.open({
        title: "计划明细",
        url: jsUseCtxPath + "/zhgl/core/zlgj/gjjhDetailEditPage.do?formId="+formId+"&action=add",
        width: 1050,
        height: 680,
        showModal:true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(detailListGrid) {
                detailListGrid.load();
            }
        }
    });
}

function seeGjjhDetail(id) {
    mini.open({
        title: "计划明细",
        url: jsUseCtxPath + "/zhgl/core/zlgj/gjjhDetailEditPage.do?id="+id+"&action=detail",
        width: 1050,
        height: 680,
        showModal:true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(detailListGrid) {
                detailListGrid.load();
            }
        }
    });
}

function editGjjhDetail(id) {
    mini.open({
        title: "计划明细",
        url: jsUseCtxPath + "/zhgl/core/zlgj/gjjhDetailEditPage.do?id="+id+"&action=edit",
        width: 1050,
        height: 680,
        showModal:true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(detailListGrid) {
                detailListGrid.load();
            }
        }
    });
}

function removeGjjhDetail(detailId,gjjhId) {
    mini.confirm("该记录对应的附件将同时被删除，确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/zlgj/delGjjhDetail.do",
                method: 'POST',
                showMsg:false,
                data: {id: detailId,formId:gjjhId},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        if(detailListGrid) {
                            detailListGrid.load();
                        }
                    }
                }
            });
        }
    });
}

function showDetailFiles(detailId,formId,canEditFile) {
    mini.open({
        title: "改进计划附件",
        url: jsUseCtxPath + "/zhgl/core/zlgj/gjjhFileListWindow.do?detailId=" + detailId + "&formId=" + formId + "&canEditFile=" + canEditFile,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(detailListGrid) {
                detailListGrid.load();
            }
        }
    });
}