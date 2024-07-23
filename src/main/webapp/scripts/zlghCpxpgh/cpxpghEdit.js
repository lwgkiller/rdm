var step = "";
var taskStatus = "";

$(function () {
    var url = jsUseCtxPath + "/strategicplanning/core/cpxpgh/getCpxpghDetail.do?cpxpghId=";
    var cpxsztGridUrl = jsUseCtxPath + "/strategicplanning/core/cpxpgh/queryChilds.do?childType=xszt&cpxpghId=";
    var cppzGridUrl = jsUseCtxPath + "/strategicplanning/core/cpxpgh/queryChilds.do?childType=cppz&cpxpghId=";
    if (cpxpghId) {
        url += cpxpghId;
        cpxsztGridUrl += cpxpghId;
        cppzGridUrl += cpxpghId;
    } else if (changeId) {
        url += changeId;
        cpxsztGridUrl += changeId;
        cppzGridUrl += changeId;
    }
    $.ajax({
        url:url,
        method:'get',
        async: false,
        success:function (json) {
            cpxpghForm.setData(json);
            taskStatus = json.taskStatus;
        }
    });
    if (changeId) {
        mini.get("id").setValue("");
    }
    cpxsztGrid.setUrl(cpxsztGridUrl);
    cppzGrid.setUrl(cppzGridUrl);
    cpxsztGrid.load();
    cppzGrid.load();
    // fileListGrid.load();
    //明细入口
    if (action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(taskStatus!='DRAFTED') {
            $("#processInfo").show();
            cpxsztGrid.setAllowCellEdit(false);
            cppzGrid.setAllowCellEdit(false);
        }
        cpxpghForm.setEnabled(false);
        mini.get("salesAreaButtons").setVisible(false);
        mini.get("cppzButtons").setVisible(false);
        // mini.get("addFile").setVisible(false);

    } else if(action=='task') {
        taskActionProcess();
    } else if (action=='edit') {
        // mini.get("addFile").setVisible(false);
        mini.get("archivedFile").setEnabled(false);
        mini.get("hbxxgk").setEnabled(false);
        mini.get("cpxssyzt").setEnabled(false);
        mini.get("cpckrzzt").setEnabled(false);
        mini.get("cbsc").setEnabled(false);
        mini.get("cptszt").setEnabled(false);
        mini.get("cpcskhzt").setEnabled(false);

    }
    mini.get("departmentId").setEnabled(false);
    if (!cpxpghId && !changeId) {
        mini.get("departmentId").setValue(mainGroupId)
        mini.get("departmentId").setText(mainGroupName)
    }
});

function toggleCpxpghFieldset(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

function addSalesArea() {
    var row={
        cpxpghId: '',
        childType: 'xszt',
        childName: '',
        childValue: ''
    };
    cpxsztGrid.addRow(row);
}

function delSalesArea() {
    var row = cpxsztGrid.getSelected();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    delRowGrid("cpxsztGrid");
}

function onCellValidation(e) {
    if (e.field == "childName") {
        if (!e.value) {
            e.isValid = false;
            e.errorText = "不能为空";
        }
    }
    if (e.field == "childValue") {
        if (!e.value) {
            e.isValid = false;
            e.errorText = "不能为空";
        }
    }
}

function addCppz() {
    var row={
        cpxpghId: '',
        childType: 'cppz',
        childName: '',
        childValue: ''
    };
    cppzGrid.addRow(row);
}

function delCppz() {
    var row = cppzGrid.getSelected();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    delRowGrid("cppzGrid");
}


//保存草稿
function saveCpxpgh(e) {
    if (changeId) {
        mini.get("changeId").setValue(changeId)
    }
    window.parent.saveDraft(e);
}

//启动流程
function startCpxpghProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var checkRunning = checkIsRunning();
    if (!checkRunning) {
        mini.alert("已存在'运行中'的数据");
        return;
    }
    if (changeId) {
        mini.get("changeId").setValue(changeId)
    }
    window.parent.startProcess(e);
}

function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'step') {
            step = nodeVars[i].DEF_VAL_;
        }
    }
    if(step == 'bianZhi') {
        mini.get("archivedFile").setEnabled(false);
        mini.get("hbxxgk").setEnabled(false);
        mini.get("cpxssyzt").setEnabled(false);
        mini.get("cpckrzzt").setEnabled(false);
        mini.get("cbsc").setEnabled(false);
        mini.get("cptszt").setEnabled(false);
        mini.get("cpcskhzt").setEnabled(false);
    } else if(step == 'szsp' || step == 'pz') {
        cpxpghForm.setEnabled(false);
        mini.get("salesAreaButtons").setVisible(false);
        mini.get("cppzButtons").setVisible(false);
        cpxsztGrid.setAllowCellEdit(false);
        cppzGrid.setAllowCellEdit(false);
    } else {
        mini.get("salesAreaButtons").setVisible(false);
        mini.get("cppzButtons").setVisible(false);
        cpxpghForm.setEnabled(false);
        cpxsztGrid.setAllowCellEdit(false);
        cppzGrid.setAllowCellEdit(false);
        if (jsztqrUser) {
            if (jsztqrUser == 'cpxpjsglbcpzg') {
                mini.get("archivedFile").setEnabled(true);
            } else if (jsztqrUser == 'bzrzyjsfzr') {
                mini.get("hbxxgk").setEnabled(true);
                mini.get("cpxssyzt").setEnabled(true);
                mini.get("cpckrzzt").setEnabled(true);
            } else if (jsztqrUser == 'fwgcfzr') {
                mini.get("cbsc").setEnabled(true);
            } else if (jsztqrUser == 'znkzyjsfzr') {
                mini.get("cptszt").setEnabled(true);
            } else {
                mini.get("cpcskhzt").setEnabled(true);
            }
        }
    }
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("cpxpghForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'materialCode',val:formData.materialCode}];
    return formData;
}



//检验表单是否必填(编制)
function validBianZhi() {
    var checkResult={};
    var salesModel = $.trim(mini.get("salesModel").getValue());
    if (!salesModel) {
        return {"result": false, "message": "请输入销售型号"};
    }
    var designModel = $.trim(mini.get("designModel").getValue());
    if (!designModel) {
        return {"result": false, "message": "请输入设计型号"};
    }
    var productStatus = mini.get("productStatus").getValue();
    if (!productStatus) {
        return {"result": false, "message": "请选择产品状态"};
    }
    var salsesYear = $.trim(mini.get("salsesYear").getValue());
    if (!salsesYear) {
        return {"result": false, "message": "请输入可售年份"};
    }
    cpxsztGrid.validate();
    if(!cpxsztGrid.isValid()){
        var error = cpxsztGrid.getCellErrors()[0];
        cpxsztGrid.beginEditCell(error.record, error.column);
        return {"result": false, "message": error.column.header + error.errorText};
    }
    cppzGrid.validate();
    if(!cppzGrid.isValid()){
        var error = cppzGrid.getCellErrors()[0];
        cppzGrid.beginEditCell(error.record, error.column);
        return {"result": false, "message": error.column.header + error.errorText};
    }
    var zjwsCost = $.trim(mini.get("zjwsCost").getValue());
    if (!zjwsCost) {
        return {"result": false, "message": "请输入整机未税成本"};
    }
    var zjSalePrice = $.trim(mini.get("zjSalePrice").getValue());
    if (!zjSalePrice) {
        return {"result": false, "message": "请输入整机销售价格"};
    }
    var zjbjgxRate = $.trim(mini.get("zjbjgxRate").getValue());
    if (!zjbjgxRate) {
        return {"result": false, "message": "请输入整机边际贡献率"};
    }
    checkResult.result=true;
    return checkResult;
}

//根据finalId校验是否有在流程中的
function checkIsRunning() {
    var result = true;
    var finalId = mini.get("finalId").getValue();
    if (changeId) {
        finalId = changeId;
    }
    $.ajax({
        url: jsUseCtxPath + "/strategicplanning/core/cpxpgh/wheterRunning.do?finalId=" + finalId,
        type: "GET",
        async: false,
        success: function (res) {
            if (res > 0) {
                result = false;
            }
        }

    })
    return result;
}

//流程中暂存信息（如编制阶段）
function saveCpxpghInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("cpxpghForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/strategicplanning/core/cpxpgh/saveCpxpgh.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
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
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function cpxpghApprove() {
    //编制阶段的下一步需要校验表单必填字段
    var valid = true;
    if (step == 'bianZhi') {
        valid = validBianZhi();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
    } else if (step == 'jsztqr') {
        if (jsztqrUser == 'cpxpjsglbcpzg') {
            var archivedFile = mini.get("archivedFile").getValue();
            if (!archivedFile) {
                mini.alert("请选择产品归档文件");
                return;
            }
        } else if (jsztqrUser == 'bzrzyjsfzr') {
            var hbxxgk = mini.get("hbxxgk").getValue();
            var cpxssyzt = mini.get("cpxssyzt").getValue();
            var cpckrzzt = mini.get("cpckrzzt").getValue();
            if (!hbxxgk) {
                mini.alert("请选择环保信息公开");
                return;
            }
            if (!cpxssyzt) {
                mini.alert("请选择产品型式试验状态");
                return;
            }
            if (!cpckrzzt) {
                mini.alert("请选择产品出口认证状态");
                return;
            }
        } else if (jsztqrUser == 'fwgcfzr') {
            var cbsc = mini.get("cbsc").getValue();
            if (!cbsc) {
                mini.alert("请选择操保手册/零件图册");
                return;
            }
        } else if (jsztqrUser == 'znkzyjsfzr') {
            var cptszt = mini.get("cptszt").getValue();
            if (!cptszt) {
                mini.alert("请选择产品调试状态");
                return;
            }
        } else {
            var cpcskhzt = mini.get("cpcskhzt").getValue();
            if (!cpcskhzt) {
                mini.alert("请选择产品测试考核状态");
                return;
            }
        }
    }
    //检查通过
    window.parent.approve();
}

function jxbzzbshProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}



function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml=returnCpxpghPreviewSpan(record.fileName,record.id,record.cpxpghId,coverContent);
    //编辑、产品主管填写、编制可以删除
    if(action=='edit' || (action=='task')) {
        var deleteUrl="/strategicplanning/core/cpxpgh/delCpxpghFiles.do";
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.cpxpghId+'\',\''+deleteUrl+'\')">删除</span>';
    }
    return cellHtml;
}

function returnCpxpghPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var downloadUrl =  "/strategicplanning/core/cpxpgh/cpxpghDownload.do";
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/strategicplanning/core/cpxpgh/cpxpghDownload.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+downloadUrl+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/strategicplanning/core/cpxpgh/cpxpghOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/strategicplanning/core/cpxpgh/cpxpghImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    if (action=='edit' || (action=='task' && step == 'bianZhi')) {
        s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\''+fileName+'\',\''+fileId+'\',\''+formId+'\',\''+downloadUrl+'\')">下载</span>';
    }
    return s;
}

function addCpxpghFile() {
    var id=mini.get("id").getValue();
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/strategicplanning/core/cpxpgh/openCpxpghUploadWindow.do?cpxpghId=" + id ,
        width: 800,
        height: 350,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

//作废
function discardCpxpghInst() {
    if (!confirm('流程作废后不可恢复，确定要继续吗?')) {
        return;
    }
    var postData = {};
    postData.finalId = mini.get("finalId").getValue();
    var instId =mini.get("instId").getValue();
    _SubmitJson({
        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
        data: {
            instId: instId
        },
        showMsg: false,
        method: 'POST',
        success: function () {
            if (postData.finalId) {
                $.ajax({
                    url: jsUseCtxPath + '/strategicplanning/core/cpxpgh/discardCpxpghInst.do',
                    type: 'post',
                    async: false,
                    showMsg: false,
                    data:mini.encode(postData),
                    contentType: 'application/json',
                    success: function (data) {
                        if(data.success) {
                            mini.alert('作废成功','提示',function () {
                                window.parent.CloseWindow();
                            })
                        }
                    }
                });
            } else {
                mini.alert('作废成功','提示',function () {
                    window.parent.CloseWindow();
                })
            }

        }
    })

}

function cpxpghProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

