var isBianzhi="";
var isjscsBianzhi="";
var isjsbzBianzhi="";
var isbmscBianzhi="";
var checkNewConfirm="";
$(function () {
    $.ajaxSettings.async = false;
    if(jsjdsId) {
        var url = jsUseCtxPath + "/zhgl/core/jsjds/getJsjdsDetail.do";
        $.post(
            url,
            {jsjdsId: jsjdsId},
            function (json) {
                formZljsjds.setData(json);
                mini.get("jsfz").setText(mini.get("jsfz").getValue());
            });
    }
    sflxFun();
    //明细入口
    if (action == 'detail') {
        formZljsjds.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#ifbianjizl").css("background-color","#F2F2F2");
        $("#ifbianjixy").css("background-color","#F2F2F2");
        $("#ifbianjizyd").css("background-color","#F2F2F2");
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
        ifDisplay();
    } else if(action=='task') {
        $("#ifbianjizl").css("background-color","#F2F2F2");
        $("#ifbianjixy").css("background-color","#F2F2F2");
        $("#ifbianjizyd").css("background-color","#F2F2F2");
        taskActionProcess();
        ifDisplay();
    }else if(action=='edit'){
        edit1Process();
        ifDisplay();
        $("#ifbianjixy").css("background-color","#F2F2F2");
        $("#ifbianjizyd").css("background-color","#F2F2F2");
    }else if(action=='change'){
        formZljsjds.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("zllx").setEnabled(true);
        $("#ifbianjixy").css("background-color","#F2F2F2");
        $("#ifbianjizyd").css("background-color","#F2F2F2");
        $("#ifbianjizl").css("background-color","white");
        ifDisplay();
        $("#changeToolBar").show();
        mini.get("sflx").setEnabled(true);
        mini.get("project").setEnabled(true);
        mini.get("plan").setEnabled(true);

    }

    $.ajaxSettings.async = true;
});
function ifDisplay() {
    var ifgj=$.trim(mini.get("ifgj").getValue())
    if(ifgj && 'yes' ==ifgj){
        $("#ifgjxs").show();
    }
    var zllx=$.trim(mini.get("zllx").getValue())
    if(zllx && 'WGSJ' !=zllx){
        $("#ifFmsy1").show();
        $("#ifFmsy2").show();
        $("#ifFmsy3").show();
    }
}
//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formZljsjds");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'zlName',val:formData.zlName}];
    return formData;
}

//保存草稿
function saveJsjds(e) {
    var zlName=$.trim(mini.get("zlName").getValue())
    if (!zlName) {
        mini.alert("请填写提案名称！");
        return;
    }
    window.parent.saveDraft(e);
}

//检验表单是否必填
function validJsjds() {
    var sflx=$.trim(mini.get("sflx").getValue());
    var zyType=$.trim(mini.get("zyType").getValue())
    if(!zyType) {
        return {"result": false, "message": "请选择专业类别"};
    }
    var zlName=$.trim(mini.get("zlName").getValue())
    if(!zlName) {
        return {"result": false, "message": "请填写提案名称"};
    }
    var zllx=$.trim(mini.get("zllx").getValue())
    if(!zllx) {
        return {"result": false, "message": "请选择专利类型"};
    }else if ("WGSJ" != zllx){
        var jswt=$.trim(mini.get("jswt").getValue())
        if(!jswt) {
            return {"result": false, "message": "请填写现有技术及其存在的问题"};
        }
        var jjwt=$.trim(mini.get("jjwt").getValue())
        if(!jjwt) {
            return {"result": false, "message": "请填写解决技术问题采用的方案"};
        }
        var jsxg=$.trim(mini.get("jsxg").getValue())
        if(!jsxg) {
            return {"result": false, "message": "请填写技术效果"};
        }
    }
    var jsfz=$.trim(mini.get("jsfz").getValue())
    if(!jsfz) {
        return {"result": false, "message": "请选择技术分支"};
    }
    var ifgj=$.trim(mini.get("ifgj").getValue())
    if(!ifgj) {
        return {"result": false, "message": "请选择是否为国际申请"};
    }else if("yes" ==ifgj){
        var sqgj=$.trim(mini.get("sqgj").getValue())
        if(!sqgj) {
            return {"result": false, "message": "请填写申请国家"};
        }
        var sqgjyy=$.trim(mini.get("sqgjyy").getValue())
        if(!sqgjyy) {
            return {"result": false, "message": "请填写申请该国家原因"};
        }
    }
    var fmsjr=$.trim(mini.get("fmsjr").getValue())
    if(!fmsjr) {
        return {"result": false, "message": "请填写发明(设计)人"};
    }
    var myfmsjId=$.trim(mini.get("myfmsjId").getValue())
    if(!myfmsjId) {
        return {"result": false, "message": "请选择我司发明(设计)人"};
    }
    var zlsqr = $.trim(mini.get("zlsqr").getValue())
    if (!zlsqr) {
        return {"result": false, "message": '请填写专利申请人'};
    }
    var hqrId=$.trim(mini.get("hqrId").getValue())
    if(!hqrId&&sflx=='是') {
        return {"result": false, "message": "请选择项目负责人(会签)"};
    }
    var zlbhd=$.trim(mini.get("zlbhd").getValue())
    if(!zlbhd) {
        return {"result": false, "message": "请填写专利技术保护要点"};
    }
    if(!sflx) {
        return {"result": false, "message": "请选择是否关联项目"};
    }
    var projectId=$.trim(mini.get("project").getValue());
    var plan=$.trim(mini.get("plan").getValue());
    if(sflx=='是'&&!projectId) {
        return {"result": false, "message": "请选择关联项目"};
    }
    if(sflx=='是'&&projectId&&!plan){
        return {"result": false, "message": "请选择成果计划"};
    }
    //上传附件
    var fileListGridData = fileListGrid.getData();
    if (fileListGridData.length==0) {
        return {"result": false, "message": "请上传技术交底书"};
    } else {
        var hasBook = false;
        for (var i = 0; i < fileListGridData.length; i++) {
            if (fileListGridData[i].fileType =='技术交底书') {
                hasBook=true;
            }
        }
        if(!hasBook){
            return {"result": false, "message": "请上传技术交底书"};
        }
        if(needReport == 'true'){
            var hasReport = false;
            for (var i = 0; i < fileListGridData.length; i++) {
                if (fileListGridData[i].fileType =='检索报告') {
                    hasReport=true;
                }
            }
            if(!hasReport){
                return {"result": false, "message": "请上传检索报告"};
            }
        }
    }
    return {"result": true};
}
//检验表单是否必填
function validJsjdsBmsc() {
    var xycz=$.trim(mini.get("xycz").getValue())
    if(!xycz) {
        return {"result": false, "message": "请选择新颖性和创造性"};
    }
    var zlzyd=$.trim(mini.get("zlzyd").getValue())
    if(!zlzyd) {
        return {"result": false, "message": "请选择专利技术重要度"};
    }
    return {"result": true};
}
function validJsjdsJscsyj() {
    var jsyj=$.trim(mini.get("jsyj").getValue())
    if(!jsyj) {
        return {"result": false, "message": "请填写技术管理部初审意见"};
    }
    return {"result": true};
}
function validJsjdsJsbzyj() {
    var jsbyj=$.trim(mini.get("jsbyj").getValue())
    if(!jsbyj) {
        return {"result": false, "message": "请填写技术管理部部长审核意见"};
    }
    return {"result": true};
}

function validChange() {
    var sflx=$.trim(mini.get("sflx").getValue());
    var projectId=$.trim(mini.get("project").getValue());
    var plan=$.trim(mini.get("plan").getValue());
    if(sflx=='是'&&!projectId) {
        return {"result": false, "message": "请选择关联项目"};
    }
    if(sflx=='是'&&projectId&&!plan){
        return {"result": false, "message": "请选择成果计划"};
    }
    return {"result": true};
}



//启动流程
function startJsjdsProcess(e) {
    var formValid = validJsjds();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveJsjdsInProcess() {
    var formValid = validJsjds();
    if (!formValid.result&&action!="change") {
        mini.alert(formValid.message);
        return;
    }
    var changeValid = validChange();
    if (!changeValid.result&&action=="change") {
        mini.alert(changeValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formZljsjds");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jsjds/saveJsjds.do',
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
function jsjdsApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validJsjds();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if(isbmscBianzhi == 'yes'){
        var formValid = validJsjdsBmsc();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if(isjscsBianzhi == 'yes'){
        var formValid = validJsjdsJscsyj();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if(isjsbzBianzhi == 'yes'){
        var formValid = validJsjdsJsbzyj();
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
    if(instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }
}

function addJsjdsFile() {
    var jsjdsId=mini.get("jsjdsId").getValue();
    if(!jsjdsId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/jsjds/fileUploadWindow.do?jsjdsId="+jsjdsId,
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
        if(nodeVars[i].KEY_ == 'isbmscBianzhi'){
            isbmscBianzhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isjscsBianzhi'){
            isjscsBianzhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isjsbzBianzhi'){
            isjsbzBianzhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'checkNewConfirm'){
            checkNewConfirm = nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi!= 'yes') {
        formZljsjds.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if(isbmscBianzhi == 'yes'){
        mini.get("xycz").setEnabled(true);
        mini.get("zlzyd").setEnabled(true);
        $("#ifbianjixy").css("background-color","white");
        $("#ifbianjizyd").css("background-color","white");
    }
    if(isjscsBianzhi == 'yes'){
        mini.get("jsyj").setEnabled(true);
    }
    if(isjsbzBianzhi == 'yes'){
        mini.get("jsbyj").setEnabled(true);
    }
    if(checkNewConfirm == 'yes'){
        mini.get("zllx").setEnabled(true);
        $("#ifbianjizl").css("background-color","white");
    }
}



function returnJsjdsPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/jsjds/jsjdsPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/jsjds/jsjdsOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/jsjds/jsjdsImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }

    return s;
}
//下载文档
function downJsjdsLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/jsjds/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.jsjdsId);
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
function edit1Process() {
    mini.get("xycz").setEnabled(false);
    mini.get("zlzyd").setEnabled(false);
    mini.get("jsyj").setEnabled(false);
    mini.get("jsbyj").setEnabled(false);

}
