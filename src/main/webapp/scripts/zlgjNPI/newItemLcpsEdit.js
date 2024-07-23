var isFqBianZhi="";
var isShBianZhi="";
var isHqBianZhi="";
var isSzsh="";

$(function () {
    initForm();
    //明细入口
    if (action == 'detail') {
        detailActionProcess();
    } else if(action=='task') {
        taskActionProcess();
        if(isFqBianZhi == 'yes'){
            mini.get("sffgsp").setEnabled(false);
        }else if(isShBianZhi == 'yes'){
            edit1SHProcess();
        }else if(isHqBianZhi == 'yes'){
            edit1SHProcess();
        }if (isSzsh == 'yes'){
            edit1SZSHProcess();
        } else {
            edit1SHProcess();
        }
    }else if(action=='edit'){
        mini.get("sffgsp").setEnabled(false);
    }
});

//保存草稿
function saveXplc(e) {
    var cpzgId=$.trim(mini.get("cpzgId").getValue())
    if (!cpzgId) {
        mini.alert("请选择产品主管");
        return;
    }
    window.parent.saveDraft(e);
}
//启动流程
function startXplcProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}
//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var cpzgId=$.trim(mini.get("cpzgId").getValue());
    if(!cpzgId) {
        return {"result": false, "message": "请选择产品主管"};
    }
    var fileData =fileListGrid.getData();
    if(fileData.length <= 0){
        return {"result": false, "message": "请添加附件"};
    }
    var xplcxxData =grid_xplcps.getData();
    if(xplcxxData.length <= 0){
        return {"result": false, "message": "请添加新品量产评审-信息"};
    }
    return {"result": true};
}

//流程中的审批或者下一步
function xplcApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isFqBianZhi == 'yes') {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (isShBianZhi == 'yes') {
        var datas= grid_xplcps.getData();
        if (datas.length > 0){
            for (var i =0, l = datas.length; i < l; i++){
                if(!datas[i].bmId){
                    mini.alert("请为问题清单'"+datas[i].wtqd+"'添加责任人");
                    return;
                }
            }
        }
    }
    if (isHqBianZhi == 'yes') {
        var datas= grid_xplcps.getData();
        if (datas.length > 0){
            for (var i =0, l = datas.length; i < l; i++){
                if(!datas[i].yhcs && datas[i].zrrId == currentUserId){
                    mini.alert("请为问题清单'"+datas[i].wtqd+"'添加后续优化措施和完成时间");
                    return;
                }
            }
        }
    }
    //检查通过
    window.parent.approve();
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formXplc");
    if (formData.SUB_treegridFileInfo) {
        delete formData.SUB_treegridFileInfo;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[];
    return formData;
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

function detailActionProcess() {
    formXplc.setEnabled(false);
    mini.get("addFile").setEnabled(false);
    mini.get("operateAdd").setEnabled(false);
    $("#detailToolBar").show();
    //非草稿放开流程信息查看按钮
    if(status!='DRAFTED') {
        $("#processInfo").show();
    }
}

function toggleFieldSet(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

var processOption = {
    animation:false,
    tooltip:{
        formatter:function (params) {
            if(params.dataType=='node') {
                return '计划结束时间：'+params.value;
            }
        },
        position: 'inside'
    },
    series : [
        {
            type: 'graph',
            layout: 'none',
            symbolSize: 16,
            roam: false,
            left:0,
            edgeSymbol: ['circle', 'circle'],
            edgeSymbolSize: [0, 0],
            edgeLabel: {
                normal: {
                    textStyle: {
                        fontSize: 16
                    }
                }
            },
            data: [],
            links: [],
            lineStyle: {
                normal: {
                    color:'target',
                    opacity: 1,
                    width: 2,
                    curveness: 0
                }
            }
        }
    ]
};


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();


}

function initForm() {
    $.ajaxSettings.async = false;
    if (xplcId){
        var url = jsUseCtxPath + "/zhgl/core/lcps/getXplcDetail.do";
        $.post(
            url,
            {xplcId: xplcId},
            function (json) {
                formXplc.setData(json);
            });
    }
    var nodeVarsObj = getProcessNodeVars();
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isFqBianZhi') {
            isFqBianZhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isShBianZhi'){
            isShBianZhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isHqBianZhi'){
            isHqBianZhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isSzsh'){
            isSzsh = nodeVars[i].DEF_VAL_;
        }
    }
    $.ajaxSettings.async = true;

}

function edit1SHProcess() {
    formXplc.setEnabled(false);
    mini.get("addFile").setEnabled(false);
    mini.get("operateAdd").setEnabled(false);

}


function getProcessNodeVars() {
    var nodeVarsObj = {};
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        nodeVarsObj[nodeVars[i].KEY_] = nodeVars[i].DEF_VAL_;
    }

    return nodeVarsObj;
}
function addXplcFile() {
    var xplcId=mini.get("xplcId").getValue();
    if(!xplcId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/lcps/fileUploadWindow.do?xplcId="+xplcId,
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
function returnXplcPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/lcps/xplcPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/lcps/xplcOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/lcps/xplcImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}
//下载文档
function downXplcLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/lcps/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.xplcId);
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

//打开标准的编辑页面（新增、编辑、明细）
function openXplcxxEditWindow(id, systemId, action, xplcId) {
    if (!xplcId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "用戶编辑",
        url: jsUseCtxPath + "/zhgl/core/lcps/editXplcxx.do?xplcId=" + xplcId + '&action=' + action + '&isFqBianZhi=' + isFqBianZhi+ '&isShBianZhi=' + isShBianZhi+'&isHqBianZhi='+ isHqBianZhi+'&id='+ id,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            var data = {};
            //查询
            // messageListGrid.load();
            grid_xplcps.load();
        }
    });
}
function edit1SZSHProcess() {
    formXplc.setEnabled(false);
    mini.get("sffgsp").setEnabled(true);
    var sffgsp=mini.get("sffgsp").getValue();
    if(!sffgsp) {
        mini.get("sffgsp").setValue('NO');
    }
    mini.get("addFile").setEnabled(false);
    mini.get("operateAdd").setEnabled(false);

}


