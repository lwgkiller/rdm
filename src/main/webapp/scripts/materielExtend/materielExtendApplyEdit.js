$(function () {
    materielApplyForm.setEnabled(false);
    mini.get("applyUserMobile").setEnabled(true);
    mini.get("urgent").setEnabled(true);
    //使用表单数据初始化
    materielApplyForm.setData(applyObj);
    if(applyObj.urgent=='yes') {
        mini.get("urgent").setChecked(true);
    } else {
        mini.get("urgent").setChecked(false);
    }
    //处理状态的中文
    processStatus();
    //按照materielExtendEditAction和登录人角色处理页面的可编辑字段
    // if(applyObj.applyStatus!='failEnd') {
    //     mini.get("exportFail").hide();
    // }
    if(materielExtendEditAction=='view') {
        mini.get("applyUserMobile").setEnabled(false);
        mini.get("urgent").setEnabled(false);
        mini.get("batchImport").setEnabled(false);
        mini.get("addOneMateriel").setEnabled(false);
        mini.get("delMateriel").setEnabled(false);
        mini.get("saveTempMaterielApply").hide();
        mini.get("commitMaterielApply").hide();
    }else {
        if(opRoleName!='SQRKC'&&currentUserNo!='admin') {
            mini.get("applyUserMobile").setEnabled(false);
            mini.get("urgent").setEnabled(false);
            mini.get("addOneMateriel").setEnabled(false);
            mini.get("delMateriel").setEnabled(false);
            mini.get("saveTempMaterielApply").hide();
        }
    }
    var sqUserId=mini.get("sqUserId").getValue();
    if(currentUserNo=='admin' || (currentUserId == sqUserId && applyObj.applyStatus=='needSync2Sap')) {
        mini.get("syncMateriels2SAP").show();
    }
    //查询物料列表
    var applyNo=mini.get("applyNo").getValue();
    if(applyNo) {
        var url=jsUseCtxPath+"/materielExtend/apply/materielList.do?applyNo="+applyNo;
        materielGrid.setUrl(url);
        materielGrid.load();
    }
});

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
    mini.get(statusInputName).setValue(nameAndColor.name);
    $("input[name="+statusInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+userInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+timeInputName+"]").parent().css("background-color",nameAndColor.color);
}

//新增物料
function editOrViewMateriel(materielId,action) {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('请先点击“暂存”按钮创建申请单！');
        return;
    }
    var title="物料新增和编辑";
    var url=jsUseCtxPath+"/materielExtend/apply/materielEditPage.do?materielId="+materielId+"&action="+action+"&applyNo="+applyNo+"&sqUserId="+mini.get("sqUserId").getValue();
    mini.open({
        title: title,
        url: url,
        width: 1150,
        height: 720,
        showModal: true,
        allowResize: true,
        onload: function () {

        },
        ondestroy: function (action) {
            var url=jsUseCtxPath+"/materielExtend/apply/materielList.do?applyNo="+applyNo;
            materielGrid.setUrl(url);
            materielGrid.load();
        }
    });
}

//删除物料
function deleteMateriel(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = materielGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
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
                    url: jsUseCtxPath + "/materielExtend/apply/materielEditDel.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        materielGrid.reload();
                    }
                });
            }
        }
    });
}

function refreshMateriel() {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('申请单尚未创建！');
        return;
    }
    materielGrid.load();
}

//打开导入物料的窗口
function batchImportOpen() {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('请先点击“暂存”创建申请单！');
        return;
    }
    importWindow.show();
}


//关闭导入物料的窗口
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    materielGrid.reload();
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
        if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传xls或xlsx文件！');
        }
    }
}

function exportMateriels(scene) {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('申请单尚未创建！');
        return;
    }
    var sqUserId=mini.get("sqUserId").getValue();
    if(scene=='fail') {
        mini.alert('导出的问题物料清单中将不包含“物料主键”、“是否为问题物料”，请创建一个新的扩充申请单后将该清单导入！',"提醒",function (action) {
            var form = $("<form>");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", jsUseCtxPath + "/materielExtend/apply/exportMateriels.do?applyNo="+applyNo+"&scene="+scene+"&action="+materielExtendEditAction+"&sqUserId="+sqUserId);
            $("body").append(form);
            form.submit();
            form.remove();
        })
    } else {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/materielExtend/apply/exportMateriels.do?applyNo="+applyNo+"&scene="+scene+"&action="+materielExtendEditAction+"&sqUserId="+sqUserId);
        $("body").append(form);
        form.submit();
        form.remove();
    }
}


//将申请单中的物料同步到SAP中（防止因SAP系统故障导致无法主动调用SAP）
function syncMateriels2SAP() {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('申请单尚未创建！');
        return;
    }
    mini.confirm("此操作会将本单中扩充失败的物料重新推送SAP【需先将“是否问题物料”改为“否”】，确定继续？（数据量大时请等待响应，不要重复操作）", "提醒",
        function (action) {
            if (action == "ok") {
                showLoading();
                mini.get("syncMateriels2SAP").setEnabled(false);
                $.ajax({
                    url: jsUseCtxPath + "/materielExtend/apply/sync2SAP.do?applyNo=" + applyNo,
                    async: false,
                    success: function (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message,'提醒',function (action) {
                                hideLoading();
                                mini.get("syncMateriels2SAP").setEnabled(true);
                                materielGrid.reload();
                            });
                        }
                    },
                    complete: function () {
                        // hideLoading();
                        // mini.get("syncMateriels2SAP").setEnabled(true);
                    }
                });
            }
        });
}

//暂存信息(当前只对申请人开放)
function saveTempMaterielApply() {
    //如果是申请人节点，则检查手机号必填
    var mobile=$.trim(mini.get("applyUserMobile").getValue());
    if(!mobile) {
        mini.alert('请填写联系电话');
        return;
    }
    var urgent=mini.get("urgent").checked?"yes":"no";

    var postData={};
    postData.urgent=urgent;
    postData.applyUserMobile=mobile;
    postData.applyNo=mini.get("applyNo").getValue();
    postData.opRoleName=opRoleName;
    postData.scene="save";
    $.ajax({
       url:jsUseCtxPath+"/materielExtend/apply/applySaveOrCommit.do",
       type:'POST',
       contentType:'application/json',
        async:false,
       data:mini.encode(postData),
       success:function (returnData) {
           if(returnData&&returnData.message) {
               mini.alert(returnData.message,'提示',function () {
                   if(returnData.success) {
                       var url=jsUseCtxPath+"/materielExtend/apply/editPage.do?applyNo="+returnData.data+"&action=edit";
                       window.location.href=url;
                   }
               });
           }
       } 
    });
}

//提交信息
function commitMaterielApply() {
    var nowDate=new Date();
    if(nowDate.getDate()=='1') {
        mini.alert("每月1号SAP ERP锁定，请稍后再试！");
        return;
    }
    var materiels=materielGrid.getData();
    //如果是申请人节点，则检查手机号必填，检查是否存在物料，
    if(opRoleName=='SQRKC') {
        var mobile=$.trim(mini.get("applyUserMobile").getValue());
        if(!mobile) {
            mini.alert('请填写联系电话');
            return;
        }
    //    是否有物料
        if(!materiels || materiels.length==0) {
            mini.alert('请添加或者导入要扩充的物料');
            return;
        }
    }
    // checkOneMaterielRequired无法对多限制条件必填项进行检查。
    // 制造节点，填写过程中的必填项约束条件
    if (opRoleName == "ZZKC") {
        //采购类型为E同时特殊采购类不是50，要求自制件生产时间、生产仓储地点必填
        for (let i = 0; i < materiels.length; i++) {
            var rowData = materiels[i];
            if (rowData.cglx == 'E' && rowData.tscgl != '50') {
                var zzscsj = rowData.zzscsj;
                if (!zzscsj) {
                    mini.alert("物料号码为"+rowData.wlhm+'，采购类型为E同时特殊采购类不是50，自制生产时间必填！');
                    return;
                }
                var scccdd = rowData.scccdd;
                if (!scccdd) {
                    mini.alert("物料号码为"+rowData.wlhm+'，采购类型为E同时特殊采购类不是50，生产仓储地点必填！');
                    return;
                }
            }
        }
    }
    // 检查所有这个角色填写的非问题物料的必填项是否完整
    for(var index=0;index<materiels.length;index++) {
        var oneMateriel=materiels[index];
        var requiredCheck=checkOneMaterielRequired(respProperties,oneMateriel,opRoleName);
        if(!requiredCheck.success) {
            mini.alert('物料“'+oneMateriel.wlhm+'”信息错误，'+requiredCheck.message);
            return;
        }
    }
    //提交
    var urgent=mini.get("urgent").checked?"yes":"no";
    var postData={};
    postData.urgent=urgent;
    postData.applyUserMobile=mobile;
    postData.applyNo=mini.get("applyNo").getValue();
    postData.opRoleName=opRoleName;
    postData.scene="commit";
    showLoading();
    mini.get("commitMaterielApply").setEnabled(false);
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/applySaveOrCommit.do",
        type:'POST',
        contentType:'application/json',
        async:false,
        data:mini.encode(postData),
        success:function (returnData) {
            if(returnData&&returnData.message) {
                mini.alert(returnData.message,'提示',function (action) {
                    hideLoading();
                    mini.get("commitMaterielApply").setEnabled(true);
                    if(returnData.success) {
                        CloseWindow();
                    }
                });
            }
        },
        complete: function () {
            // hideLoading();
            // mini.get("commitMaterielApply").setEnabled(true);
        }
    });
}

//模板下载
function materielTemplateDownload() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/materielExtend/apply/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//批量导入
function importMateriel() {
    var applyNo=mini.get("applyNo").getValue();
    if(!applyNo) {
        mini.alert('请先点击“暂存”创建申请单！');
        return;
    }
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
                            materielGrid.reload();
                            closeImportWindow();
                        });
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/materielExtend/apply/importMateriel.do?applyNo='+applyNo, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('materielImportFile', file);
        xhr.send(fd);
    }
}
