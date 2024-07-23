$(function () {
    searchFrm();
//    只对申请人角色放开“新增申请”和“删除”操作
    if(opRoleName!='SQRKC') {
        mini.get("addApply").hide();
        mini.get("removeApply").hide();
    }else{
        mini.get("exportApplyMat").hide();
        mini.get("importApplyMat").hide();
    }
    if(currentUserNo=='admin'){
        mini.get("addApply").show();
        mini.get("removeApply").show();
        mini.get("exportApplyMat").show();
        mini.get("importApplyMat").show();
    }
});

//物料扩充申请的窗口
function editOrViewApply(applyNo,action) {
    var title="物料扩充申请";
    var url=jsUseCtxPath+"/materielExtend/apply/editPage.do?applyNo="+applyNo+"&action="+action;
    //新增场景判断该申请人是否错误的都处理过了
    if(!applyNo && action=='edit') {
        $.ajax({
            url:jsUseCtxPath+"/materielExtend/apply/checkSuccessConfirm.do",
            async:false,
            success:function (data) {
                if(data.result) {
                    //测试是否能解决浏览器弹窗拦截
                    var winObj=window.open('_blank',title);
                    winObj.location=url;
                    var loop = setInterval(function() {
                        if(winObj.closed) {
                            clearInterval(loop);
                            if(applyListGrid){
                                applyListGrid.reload();
                            };
                        }
                    }, 1000);
                } else {
                    mini.alert('存在尚未处理“流程结束（有错误物料）”的申请单，请点击“错误确认”处理！')
                }
            }
        });
    } else {
        var winObj=window.open(url,title);
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                if(applyListGrid){
                    applyListGrid.reload();
                };
            }
        }, 1000);
    }
}

function failConfirm(applyNo) {
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/failConfirm.do?applyNo="+applyNo,
        success:function (data) {
            mini.alert(data.message);
            applyListGrid.reload();
        }
    });
}

//删除记录
function removeApply(record) {
    var rows =[];
    if(record) {
        rows.push(record);
    } else {
        rows = applyListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //对于非草稿状态的申请不能删除
            var ids = [];
            var existCannotDelete=false;
            for (var i = 0; i < rows.length; i++) {
                var r = rows[i];
                if(r.applyStatus=='draft') {
                    ids.push(r.applyNo);
                } else {
                    existCannotDelete=true;
                }
            }
            if(existCannotDelete) {
                mini.alert("非草稿状态的数据不会被删除！");
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/materielExtend/apply/applyDel.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    contentType:'application/json',
                    success: function (text) {
                        if(applyListGrid) {
                            applyListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

//查询申请单各个节点的处理状态
function processStatusQuery(applyNo){
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/applyDetail.do?applyNo="+applyNo,
        success:function (data) {
            if(data) {
                //赋值
                processStatusForm.setData(data);
                processStatusForm.setEnabled(false);
                processStatus();
            }
            processStatusWindow.show();
        }
    });
}

//处理几个处理节点状态的中文
function processStatus() {
    toProcessNameColor("gyStatus",'gyCommitUserName','gyCommitTime');
    toProcessNameColor("gfStatus",'gfCommitUserName','gfCommitTime');
    toProcessNameColor("cgStatus",'cgCommitUserName','cgCommitTime');
    toProcessNameColor("cwStatus",'cwCommitUserName','cwCommitTime');
    toProcessNameColor("wlStatus",'wlCommitUserName','wlCommitTime');
    toProcessNameColor("zzStatus",'zzCommitUserName','zzCommitTime');
}

function toProcessNameColor(statusInputName,userInputName,timeInputName) {
    var statusCode=mini.get(statusInputName).getValue();
    var nameAndColor={};
    switch (statusCode) {
        case '00':
            nameAndColor.name='未开始';
            nameAndColor.color='#8080809e';
            break;
        case '01':
            nameAndColor.name='进行中';
            nameAndColor.color='#ddf3149e';
            break;
        case '02':
            nameAndColor.name='已完成';
            nameAndColor.color='#5ede1a9e';
            break;
    }
    mini.get(statusInputName).setValue(nameAndColor.name)
    $("input[name="+statusInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+userInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+timeInputName+"]").parent().css("background-color",nameAndColor.color);
}
function exportApplyMateriels() {

    var rows =[];
    rows = applyListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    var ids = [];
    for (var i = 0; i < rows.length; i++) {
        var r = rows[i];
        ids.push(r.applyNo);
    }
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/materielExtend/apply/exportApplyMaterials.do?applyNos="+ids.join(','));
    $("body").append(form);
    form.submit();
    form.remove();
}
function batchImportOpen() {
    importWindow.show();
}

//批量导入
function importApplyMateriel() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert('请选择文件！');
        return;
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
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            for(var index=0;index<returnObj.message.length;index++) {
                                message += returnObj.message[index]+'<br/>';
                            }
                        }
                        message='<div style="text-align: left">'+message+'</div>';
                        mini.alert(message,'提醒',function (action) {
                            applyListGrid.reload();
                            closeImportWindow();
                        });
                    }
                }
            }
        };
        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/materielExtend/apply/importApplyMateriel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('materielImportFile', file);
        xhr.send(fd);
    }
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
        if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传xls或xlsx文件！');
        }
    }
}
//关闭导入物料的窗口
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    applyListGrid.reload();
}
//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}
