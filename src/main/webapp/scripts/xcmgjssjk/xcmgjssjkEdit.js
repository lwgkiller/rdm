var isBianzhi="";
$(function () {
    initForm();
    if(jssjkId) {
        var url = jsUseCtxPath + "/jssj/core/config/getJssjkDetail.do";
        $.post(
            url,
            {jssjkId: jssjkId},
            function (json) {
                formJssjk.setData(json);
                if(action=='edit'){
                        if(json.delReason){
                            formJssjk.setEnabled(false);
                            mini.get("addFile").setEnabled(false);
                            mini.get("tdmcButtons").hide();
                            mini.get("tdmcwbButtons").hide();
                            $("#ifDisplay").show();
                            mini.get("delReason").setEnabled(true);
                        }
                }

            });

    }
    $.ajaxSettings.async = false;
    //明细入口
    if (action == 'detail') {
        formJssjk.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status && status!='DRAFTED') {
            $("#processInfo").show();
        }
        if(sptype == "delete"){
            $("#ifDisplay").show();
        }
        detailTdmcProcess();
    }else if(action=='task') {
        taskActionProcess();
    }else if(action=='edit'){
        if(sptype == "delete") {
            formJssjk.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#ifDisplay").show();
            mini.get("delReason").setEnabled(true);
        }else if(sptype == "new"){
            mini.get("jsfzrId").setValue(currentUserId);
            mini.get("jsfzrId").setText(currentUserName);
            mini.get("splx").setValue(sptype);
        }
    }
    $.ajaxSettings.async = true;
});

//检验表单是否必填
function validJssjk() {
    var jsName=$.trim(mini.get("jsName").getValue())
    if(!jsName) {
        return {"result": false, "message": "请填写技术名称"};
    }
    /*var jsNum=$.trim(mini.get("jsNum").getValue())
    if(!jsNum) {
        return {"result": false, "message": "请填写技术编号"};
    }*/
    var lxfs=$.trim(mini.get("lxfs").getValue())
    if(!lxfs) {
        return {"result": false, "message": "请填写联系方式"};
    }
    var ytxm=$.trim(mini.get("ytxm").getValue())
    if(!ytxm) {
        return {"result": false, "message": "请填写依托项目"};
    }
    var xmNum=$.trim(mini.get("xmNum").getValue())
    if(!xmNum) {
        return {"result": false, "message": "请填写项目编号"};
    }
    var jdTime = $.trim(mini.get("jdTime").getValue())
    if (!jdTime) {
        return {"result": false, "message": '请选择鉴定日期'};
    }
    var jslb=$.trim(mini.get("jslb").getValue())
    if(!jslb) {
        return {"result": false, "message": "请选择技术类别"};
    }
    var jsfx=$.trim(mini.get("jsfx").getValue())
    if(!jsfx) {
        return {"result": false, "message": "请选择专业方向"};
    }
    var yffx=$.trim(mini.get("yffx").getValue())
    if(!yffx) {
        return {"result": false, "message": "请选择研发方向"};
    }
    var jsms=$.trim(mini.get("jsms").getValue())
    if(!jsms) {
        return {"result": false, "message": "请填写技术描述"};
    }
    var jsly=$.trim(mini.get("jsly").getValue())
    if(!jsly) {
        return {"result": false, "message": "请选择技术来源"};
    }
    var fileData =fileListGrid.getData();
    if(fileData.length <= 0){
        return {"result": false, "message": "请添加附件"};
    }
    if(sptype == "delete"){
        var delReason=$.trim(mini.get("delReason").getValue())
        if (!delReason) {
            return {"result": false, "message": "请填写删除原因"};
        }
    }
    if(grid_jssjk_tdmcInfo.getChanges().length > 0){
        var tdmcFormValid =draTdmc();
        if (!tdmcFormValid.result) {
            mini.alert(tdmcFormValid.message);
            return;
        }
    }
    if(grid_jssjk_tdmcwbInfo.getChanges().length > 0){
        var tdmcFormValid =draTdmcwb();
        if (!tdmcFormValid.result) {
            mini.alert(tdmcFormValid.message);
            return;
        }
    }
    return {"result": true};
}
//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formJssjk");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    formData.changeData=grid_jssjk_tdmcInfo.getChanges();
    formData.wbChangeData=grid_jssjk_tdmcwbInfo.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jsName',val:formData.jsName}];
    return formData;
}
//保存草稿
function saveJssjk(e) {
    var jsName=$.trim(mini.get("jsName").getValue())
    if (!jsName) {
        mini.alert("请填写技术名称！");
        return;
    }
    // window.parent.saveDraft(e);
    var formData = _GetFormJsonMini("formJssjk");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    formData.changeData=grid_jssjk_tdmcInfo.getChanges();
    formData.wbChangeData=grid_jssjk_tdmcwbInfo.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jsName',val:formData.jsName}];
    debugger
    $.ajax({
        url: jsUseCtxPath + "/jssj/core/config/saveJssjk.do",
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (returnObj) {
            if (returnObj) {
               debugger
                var message = "";
                if (returnObj.success) {
                    message = "数据保存成功，" + returnObj.message;
                    mini.alert(message, '提示', function (){
                        if(returnObj.success){
                            var url = jsUseCtxPath + "/jssj/core/config/editPage.do?jssjkId=" + returnObj.data + "&action=edit";
                            window.location.href = url;
                        }
                    });


                } else {
                    message = "数据保存失败，" + returnObj.message;
                    mini.alert(message);
                }

            }
        }
    });
}
//关闭（修改和删除时如果没有实例关闭即同时删除数据）
function handClose(e) {
    var jssjkId=$.trim(mini.get("jssjkId").getValue());
    var instId=$.trim(mini.get("instId").getValue());
    if(jssjkId && instId == ""){
        $.ajax({
            url: jsUseCtxPath + "/jssj/core/config/deleteJssjk.do",
            method: 'POST',
            async: false,
            data: {ids: jssjkId, instIds: instId},
            success: function (data) {
                if (data) {
                    mini.alert("关闭，放弃修改或删除");
                }
            }
        });
    }
    window.parent.close();
}
//启动流程
function startJssjkProcess(e) {
    var formValid = validJssjk();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    var formData = _GetFormJsonMini("formJssjk");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    formData.changeData=grid_jssjk_tdmcInfo.getChanges();
    formData.wbChangeData=grid_jssjk_tdmcwbInfo.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jsName',val:formData.jsName}];
    $.ajax({
        url: jsUseCtxPath + '/jssj/core/config/startJssjk.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="流程启动成功";

                } else {
                    message="流程启动失败"+data.message;
                }

                mini.alert(message,"提示信息",function () {
                    window.parent.close();
                });
            }
        }
    });
    // window.parent.startProcess(e);
}
//流程中暂存信息（如编制阶段）
function saveJssjkInProcess() {

    var formValid = validJssjk();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    var formData = _GetFormJsonMini("formJssjk");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    formData.changeData=grid_jssjk_tdmcInfo.getChanges();
    formData.wbChangeData=grid_jssjk_tdmcwbInfo.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jsName',val:formData.jsName}];
    $.ajax({
        url: jsUseCtxPath + '/jssj/core/config/saveJssjk.do',
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
function jssjkApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validJssjk();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function processInfo() {
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
function addJssjkFile() {
    var jssjkId=mini.get("jssjkId").getValue();
    if(!jssjkId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/jssj/core/config/fileUploadWindow.do?jssjkId="+jssjkId,
        width: 750,
        height: 450,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(fileListGrid) {
                fileListGrid.load();
            }
        }
    });
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
        if (nodeVars[i].KEY_ == 'isBianzhi') {
            isBianzhi = nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi!= 'yes') {
        formJssjk.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        if(sptype == "delete"){
            $("#ifDisplay").show();
        }
        detailTdmcProcess();
    }
}


function returnJssjkPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/jssj/core/config/jssjkPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/jssj/core/config/jssjkOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/jssj/core/config/jssjkImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }

    return s;
}
//下载文档
function downJssjLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/jssj/core/config/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.jssjkId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(standardId);
    form.append(fileId);
    form.submit();
    form.remove();
}
//是否是分管领导
function whetherIfFgld(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('分管领导')!=-1||userRoles[i].NAME_.indexOf('新技术分管主任')!=-1) {
                return true;
            }
        }
    }
    return false;
}
function addJssjk_tdmcInfoRow() {
    var row={};
    grid_jssjk_tdmcInfo.addRow(row);
}
//移除
function removeJssjk_tdmcInfoRow() {
    var selecteds = grid_jssjk_tdmcInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_jssjk_tdmcInfo.removeRows(deleteArr);
}
function initForm() {
    $.ajaxSettings.async = false;
    if (jssjkId){
        var url = jsUseCtxPath + "/jssj/core/config/getTdmcList.do";
        $.post(
            url,
            {jssjkId: jssjkId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_jssjk_tdmcInfo.setData(json);
                }
            });
        var url2 = jsUseCtxPath + "/jssj/core/config/getTdmcwbList.do";
        $.post(
            url2,
            {jssjkId: jssjkId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_jssjk_tdmcwbInfo.setData(json);
                }
            });
    }
    $.ajaxSettings.async = true;
}
function detailTdmcProcess() {
    // mini.get("addNjjdxzpz").setEnabled(false);
    // mini.get("removeNjjdxzpz").setEnabled(false);
    grid_jssjk_tdmcInfo.setAllowCellEdit(false);
    $("#tdmcButtons").hide();
    grid_jssjk_tdmcwbInfo.setAllowCellEdit(false);
    $("#tdmcwbButtons").hide();
}
function draTdmc() {
    var checkMemberMust=checkMemberRequired(grid_jssjk_tdmcInfo.getData(),false);
    if(!checkMemberMust.result) {
        return {"result": false, "message": checkMemberMust.message};
    }
    return {"result": true};
}
function checkMemberRequired(rowData,checkAll) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: ''};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.gznr) {
            return {result: false, message: '请填写团队花名册(内部人员)列表中的工作内容'};
        }
        if (!row.lxfs) {
            return {result: false, message: '请填写团队花名册(内部人员)列表中的联系方式'};
        }
        if (!row.nbIds){
            return {result: false, message: '请选择团队花名册(内部人员)列表中的公司内部人员'};
        }
    }
    return {result: true};
}
function addJssjk_tdmcwbInfoRow() {
    var row={};
    grid_jssjk_tdmcwbInfo.addRow(row);
}
//移除
function removeJssjk_tdmcwbInfoRow() {
    var selecteds = grid_jssjk_tdmcwbInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_jssjk_tdmcwbInfo.removeRows(deleteArr);
}
function draTdmcwb() {
    var checkMemberMust=checkMemberRequiredwb(grid_jssjk_tdmcwbInfo.getData(),false);
    if(!checkMemberMust.result) {
        return {"result": false, "message": checkMemberMust.message};
    }
    return {"result": true};
}
function checkMemberRequiredwb(rowData,checkAll) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: ''};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.gznr) {
            return {result: false, message: '请填写团队花名册(外部人员)列表中的工作内容'};
        }
        if (!row.lxfs) {
            return {result: false, message: '请填写团队花名册(外部人员)列表中的联系方式'};
        }
        if (!row.wbdw){
            return {result: false, message: '请填写团队花名册(外部人员)列表中的外部单位'};
        }
        if (!row.wbName){
            return {result: false, message: '请填写团队花名册(外部人员)列表中的外部单位人员'};
        }
    }
    return {result: true};
}
