var bianzhi = "";
var iscpzg = "";
var isdlgcs = "";
$(function () {
    if (projectId) {
        var url = jsUseCtxPath + "/environment/core/Zjhm/getZjhmDetail.do";
        $.post(
            url,
            {projectId: projectId},
            function (json) {
                formProject.setData(json);
            });
    }
    // formProject.setEnabled(false);
    // mini.get("addFile").setEnabled(false);

    //变更入口
    if (action == 'task') {
        taskActionProcess();
    }else if (action == "detail") {
        formProject.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();

        }
    }else if(action == "edit"){
        formProject.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("jxxh").setEnabled(true);
        mini.get("cpzgId").setEnabled(true);
        mini.get("needFile").setEnabled(true);
    }
});

function getData() {
    var formData = _GetFormJsonMini("formProject");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveDraft(e) {
    var formValid = validZjhm();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}


function startProcess(e) {
    var formValid = validZjhm();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function projectApprove() {
    //编制阶段的下一步需要校验表单必填字段
    var formValid = validZjhm();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查通过
    window.parent.approve();
}

function validZjhm() {
    var jxxh = $.trim(mini.get("jxxh").getValue());
    if (!jxxh) {
        return {"result": false, "message": "请填写机械型号"};
    }
    var cpzgId = $.trim(mini.get("cpzgId").getValue());
    if (!cpzgId) {
        return {"result": false, "message": "请选择产品主管"};
    }
    return {"result": true};
}

function projectApproveCpzg() {
    //编制阶段的下一步需要校验表单必填字段
    var formValid = validCpzg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查通过
    window.parent.approve();
}

function validCpzg() {
    var dlgcsId = $.trim(mini.get("dlgcsId").getValue());
    if (!dlgcsId) {
        return {"result": false, "message": "请选择动力工程师"};
    }
    return {"result": true};
}

function projectApproveDlgcs() {
    //编制阶段的下一步需要校验表单必填字段
    var formValid = validDlgcs();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查文件

    //检查通过
    window.parent.approve();
}

function validDlgcs() {
    var fdjgkbh = $.trim(mini.get("fdjgkbh").getValue());
    if (!fdjgkbh) {
        return {"result": false, "message": "请填写发动机信息公开编号"};
    }
    var fdjzzs = $.trim(mini.get("fdjzzs").getValue());
    if (!fdjzzs) {
        return {"result": false, "message": "请填写发动机制造商"};
    }
    var fdjxh = $.trim(mini.get("fdjxh").getValue());
    if (!fdjxh) {
        return {"result": false, "message": "请填写发动机型号"};
    }
    var needFile = mini.get("needFile").getValue();
    var data = fileListGrid.getData().length;
    if ((needFile == "true") && (data == '0')){
        return {"result": false, "message": "请上传发动机相关环保认证证书"};
    }
    return {"result": true};
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


function fileupload() {
//     var rjbgId = mini.get("rjbgId").getValue();
    if (!projectId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/environment/core/Zjhm/openUploadWindow.do?projectId=" + projectId,
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

function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'bianzhi') {
            bianzhi = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'iscpzg') {
            iscpzg = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isdlgcs') {
            isdlgcs = nodeVars[i].DEF_VAL_;
        }
    }
    formProject.setEnabled(false);
    if (bianzhi == '1') {
        formProject.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("jxxh").setEnabled(true);
        mini.get("cpzgId").setEnabled(true);
        mini.get("needFile").setEnabled(true);
    }
    if (iscpzg == '1') {
        formProject.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("dlgcsId").setEnabled(true);
    }
    if (isdlgcs == '1') {
        formProject.setEnabled(false);
        mini.get("addFile").setEnabled(true);
        mini.get("fdjgkbh").setEnabled(true);
        mini.get("fdjzzs").setEnabled(true);
        mini.get("fdjxh").setEnabled(true);
    }
}

function deleteFile(fileName,fileId,belongId,urlValue) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + urlValue;
                var data = {
                    fileName: fileName,
                    id: fileId,
                    projectId: belongId
                };
                $.ajax({
                    url:url,
                    method:'post',
                    contentType: 'application/json',
                    data:mini.encode(data),
                    success:function (json) {
                        if(fileListGrid) {
                            fileListGrid.load();
                        }
                    }
                });
            }
        }
    );
}

