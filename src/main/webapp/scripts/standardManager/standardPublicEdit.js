$(function () {
    formStandard.setData(standardObj);
    if (action == 'detail') {
        formStandard.setEnabled(false);
        mini.get("saveStandard").hide();
        mini.get("uploadFileBtn").setEnabled(false);
        mini.get("clearFileBtn").setEnabled(false);
        mini.get("publishNotice").hide();
    }
});


//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'pdf') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert(standardPublicEdit_name);
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//新增或者更新的保存
function saveStandard() {
    var formData = formStandard.getData();
    var checkResult=checkStandardEditRequired(formData);
    if(!checkResult) {
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
                        mini.alert(message,standardPublicEdit_name1,function (action) {
                            // if(action=='ok') {
                            //     CloseWindow();
                            // }
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
        xhr.open('POST', jsUseCtxPath + '/standardManager/core/standard/savePublic.do', false);
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
        mini.alert(standardPublicEdit_name2);
        return;
    }
    if(!standardId) {
        standardId='';
    }
    mini.open({
        title: standardPublicEdit_name3,
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
        mini.alert(standardPublicEdit_name4);
        return false;
    }
    if(!$.trim(formData.companyName)) {
        mini.alert(standardPublicEdit_name5);
        return false;
    }
    if(!$.trim(formData.standardNumber)) {
        mini.alert(standardPublicEdit_name6);
        return false;
    }
    if(!$.trim(formData.standardName)) {
        mini.alert(standardPublicEdit_name7);
        return false;
    }
    if(!$.trim(formData.standardStatus)) {
        mini.alert(standardPublicEdit_name8);
        return false;
    }
    if(!$.trim(formData.publishTime)) {
        mini.alert(standardPublicEdit_name9);
        return false;
    }

    return true;
}
