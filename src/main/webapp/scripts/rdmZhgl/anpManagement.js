$(function () {
    // console.log("测试数据",anpObj)
    formStandard.setData(anpObj);
    if (action == 'detail') {
        formStandard.setEnabled(false);
        mini.get("saveStandard").hide();
        mini.get("uploadFileBtn").setEnabled(false);
        mini.get("clearFileBtn").setEnabled(false);
        mini.get("publishNotice").hide();
    }
});

//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        mini.get("fileName").setValue(fileList[0].name);

    }
}
//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}
//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}


//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        mini.get("fileName").setValue(fileList[0].name);

    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

/**
 * 检验表单是否必填
 *
 * @returns {*}
 */
function validAnpMessage() {
    var awardType = $.trim(mini.get("awardType").getValue())
    if (!awardType) {
        return {"result": false, "message": "请选择获奖类别"};
    }

    var certificateNumber = $.trim(mini.get("certificateNumber").getValue())
    if (!certificateNumber) {
        return {"result": false, "message": "请填写证书编号"};
    }

    var honor = $.trim(mini.get("honor").getValue())
    if (!honor) {
        return {"result": false, "message": "请填写荣誉"};
    }
    // var portrayalPointPersonId = $.trim(mini.get("portrayalPointPersonId").getValue())
    // if (!portrayalPointPersonId) {
    //     return {"result": false, "message": "请填写画像积分人员"};
    // }

    return {"result": true};
}

/**
 * 新增或者更新的保存
 */
function saveAnp(awardId) {

    var formData = formStandard.getData();

    var formValid = validAnpMessage();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if(xhr.responseText) {
                        var returnObj=JSON.parse(xhr.responseText);
                        var message='';
                        if(returnObj.message) {
                            message=returnObj.message;
                        }
                        mini.alert(message,'提示信息',function (action) {
                            if(action=='ok') {
                                CloseWindow();
                            }
                            if(returnObj.success) {
                                mini.get("id").setValue(returnObj.id);
                            }
                            CloseWindow();

                        });
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/zhgl/core/awardNewProduct/saveAnp.do?awardId='+awardId, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        if (formData) {
            for (key in formData) {
                if(key=='publishTime' || key=='stopTime') {
                    fd.append(key, getYMDHmsString(formData[key]));
                } else {
                    fd.append(key, formData[key]);
                }
            }
        }
        fd.append('standardFile', file);
        xhr.send(fd);
    }



}



function publishNotice() {
    var standardId=mini.get("id").getValue();
    if(!standardId) {
        mini.alert('请先进行标准的保存！');
        return;
    }
    if(!standardId) {
        standardId='';
    }
    mini.open({
        title: "发布宣贯",
        url: jsUseCtxPath + "/standardManager/core/standardMessage/edit.do?standardId="+standardId+"&canEditStandard=false&scene=public",
        width: 850,
        height: 550,
        showModal:true,
        allowResize: true,
        ondestroy: function (action) {
        }
    });
}

function checkStandardEditRequired(formData) {
    if(!formData) {
        mini.alert('请填写必填项！');
        return false;
    }
    if(!$.trim(formData.companyName)) {
        mini.alert('请填写企业名称！');
        return false;
    }
    if(!$.trim(formData.standardNumber)) {
        mini.alert('请填写标准编号！');
        return false;
    }
    if(!$.trim(formData.standardName)) {
        mini.alert('请填写标准名称！');
        return false;
    }
    if(!$.trim(formData.standardStatus)) {
        mini.alert('请选择标准状态！');
        return false;
    }
    if(!$.trim(formData.publishTime)) {
        mini.alert('请选择发布时间！');
        return false;
    }

    return true;
}
