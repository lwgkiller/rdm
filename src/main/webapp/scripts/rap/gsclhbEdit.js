var first="";
var second="";
var third="";
var forth="";
var fifth="";

$(function () {
    if (cxId) {
        var url = jsUseCtxPath + "/environment/core/Cx/getCxDetail.do";
        $.post(
            url,
            {cxId: cxId},
            function (json) {
                formCx.setData(json);
            });
    }else {
        if(!cxId&&type == "old" && oldCxId){
            var url = jsUseCtxPath + "/environment/core/Cx/getOldCxDetail.do";
            $.post(
                url,
                {cxId: oldCxId},
                function (json) {
                    formCx.setData(json);
                    mini.get("apply").setValue(currentUserId);
                    mini.get("apply").setText(currentUserName);
                    mini.get("dept").setValue(currentUserMainDepId);
                    mini.get("dept").setText(deptName);
                    mini.get("type").setValue(type);
                    mini.get("oldCxId").setValue(oldCxId);
                });
        }else {
            mini.get("sb").setValue("徐工牌(XCMG)");
            mini.get("jxdl").setValue("工程机械");
            mini.get("jxxl").setValue("挖掘机械");
            mini.get("zzsmc").setValue("徐州徐工挖掘机械有限公司");
            mini.get("scdz").setValue("江苏省徐州经济技术开发区高新路39号");
            mini.get("zlname").setValue("工作质量");
            mini.get("hbdmwz").setValue("PIN码在整机铭牌上");
            mini.get("jxmc").setValue("液压挖掘机");
            mini.get("rllx").setValue("柴油");
            mini.get("rlgg").setValue("GB19147 车用柴油（国六）");
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("dept").setValue(currentUserMainDepId);
            mini.get("dept").setText(deptName);
            mini.get("type").setValue(type);
        }
    }
    formCx.setEnabled(false);
    mini.get("addDqFile").setEnabled(false);
    mini.get("addDlFile").setEnabled(false);
    mini.get("addHbFile").setEnabled(false);
    mini.get("downloadGsClhb").setEnabled(false);
    mini.get("downloadGsClhbdj").setEnabled(false);
    mini.get("dqId").setEnabled(true);
    mini.get("dlId").setEnabled(true);
    mini.get("nbbh").setEnabled(true);
    mini.get("zjsjxh").setEnabled(true);
    mini.get("cpjxxh").setEnabled(true);
    mini.get("zjwlh").setEnabled(true);
    // mini.get("sfgz").setEnabled(true);
    mini.get("cyjedgl").setEnabled(true);
    mini.get("fldm").setEnabled(true);
    mini.get("upvecc").setEnabled(true);
    mini.get("xxfwjc").setEnabled(true);
    mini.get("jxmc").setEnabled(true);
    mini.get("zlname").setEnabled(true);
    mini.get("zl").setEnabled(true);
    mini.get("wxcc").setEnabled(true);
    mini.get("jxsbff").setEnabled(true);
    mini.get("hbdmwz").setEnabled(true);
    mini.get("hbbqwz").setEnabled(true);
    mini.get("zzl").setEnabled(true);
    mini.get("zdcs").setEnabled(true);
    mini.get("edgzcs").setEnabled(true);
    mini.get("dzyx").setEnabled(true);
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formCx.setEnabled(false);
        mini.get("addCpFile").setEnabled(false);
        mini.get("downloadGsClhb").setEnabled(false);
        mini.get("downloadGsClhbdj").setEnabled(false);
        mini.get("downImportGsClhbpf").setEnabled(false);
        mini.get("downImportGsClhbsx").setEnabled(false);
        mini.get("downImportGsClhbcz").setEnabled(false);
        mini.get("downImportGsClhbwj").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    } else if(action == "change")
    {
        formCx.setEnabled(true);
        mini.get("downloadGsClhb").setEnabled(true);
        mini.get("downloadGsClhbdj").setEnabled(true);
        mini.get("addDqFile").setEnabled(true);
        mini.get("addDlFile").setEnabled(true);
        mini.get("addHbFile").setEnabled(true);
        mini.get("jxxzmc").setEnabled(false);
        mini.get("num").setEnabled(false);
        mini.get("dqId").setEnabled(false);
        mini.get("dlId").setEnabled(false);
        mini.get("apply").setEnabled(false);
        mini.get("dept").setEnabled(false);
        $("#changeToolBar").show();
    }

    if(first=='yes'||status=='DRAFTED'){
        var t=document.getElementById("checkboxCpInfo");
        t.checked = 'true';
        bztoggleFieldSet(t, 'cpzg');
    }
    if(second=='yes'){
        var t=document.getElementById("checkboxDqInfo");
        t.checked = 'true';
        bztoggleFieldSet(t, 'dqgcs');
    }
    if(third=='yes'){
        var t=document.getElementById("checkboxDlInfo");
        t.checked = 'true';
        bztoggleFieldSet(t, 'dlgcs');
    }
    if(fifth=='yes'){
        var t=document.getElementById("checkboxHbInfo");
        t.checked = 'true';
        bztoggleFieldSet(t, 'hbzy');
    }
});
function getData() {
    var formData = _GetFormJsonMini("formCx");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function savegsClhb(e) {
    // var formValid = validCpzg();
    // if (!formValid.result) {
    //     mini.alert(formValid.message);
    //     return;
    // }
    window.parent.saveDraft(e);
}
function saveChange(){
    var formData = _GetFormJsonMini("formCx");
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Cx/saveCx.do',
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
function startgsClhbProcess(e) {
    var formValid = validCpzg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}
function gsClhbCpzgApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (first == 'yes') {
        var formValid = validCpzg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }else if (second == 'yes') {
        var formValid = validDqgcs();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }else if (third== 'yes') {
        var formValid = validDlgcs();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }else if (forth == 'yes') {
        var formValid = validBzh();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function gsClhbPfzyApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (fifth == 'yes') {
        var formValid = validPfzy();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function validCpzg() {
    var type = $.trim(mini.get("type").getValue());
    var dlId = $.trim(mini.get("dlId").getValue())
    if (!dlId&&type!="old") {
        return {"result": false, "message": "请选择动力工程师"};
    }
    var nbbh = $.trim(mini.get("nbbh").getValue())
    if (!nbbh) {
        return {"result": false, "message": "请填写内部编号"};
    }
    var zjsjxh = $.trim(mini.get("zjsjxh").getValue())
    if (!zjsjxh) {
        return {"result": false, "message": "请填写整机设计型号"};
    }
    var cpjxxh = $.trim(mini.get("cpjxxh").getValue())
    if (!cpjxxh) {
        return {"result": false, "message": "请填写产品机械型号"};
    }
    var zjwlh = $.trim(mini.get("zjwlh").getValue())
    if (!zjwlh) {
        return {"result": false, "message": "请填写整机物料号"};
    }
    var jxmc = $.trim(mini.get("jxmc").getValue())
    if (!jxmc) {
        return {"result": false, "message": "请填写机械名称"};
    }
    var zlname = $.trim(mini.get("zlname").getValue())
    if (!zlname) {
        return {"result": false, "message": "请填写质量名称"};
    }
    var zl = $.trim(mini.get("zl").getValue())
    if (!zl) {
        return {"result": false, "message": "请填写质量（KG）"};
    }
    var wxcc = $.trim(mini.get("wxcc").getValue())
    if (!wxcc) {
        return {"result": false, "message": "请填写外形尺寸"};
    }
    var jxsbff = $.trim(mini.get("jxsbff").getValue())
    if (!jxsbff) {
        return {"result": false, "message": "请填写机械的识别方法和位置"};
    }
    var hbdmwz = $.trim(mini.get("hbdmwz").getValue())
    if (!hbdmwz) {
        return {"result": false, "message": "请填写机械环保代码在机体上的打刻位置"};
    }
    var hbbqwz = $.trim(mini.get("hbbqwz").getValue())
    if (!hbbqwz) {
        return {"result": false, "message": "请填写环保信息标签位置"};
    }
    var zzl = $.trim(mini.get("zzl").getValue())
    if (!zzl) {
        return {"result": false, "message": "请填写最大设计总质量"};
    }
    var fldm = $.trim(mini.get("fldm").getValue())
    if (!fldm) {
        return {"result": false, "message": "请填写机械分类代码"};
    }
    // var sfgz = $.trim(mini.get("sfgz").getValue())
    // if (!sfgz) {
    //     return {"result": false, "message": "请填写是否改装"};
    // }
    var upvecc = $.trim(mini.get("upvecc").getValue())
    if (!upvecc) {
        return {"result": false, "message": "请填写车载终端数据是否上传VECC官网"};
    }
    var xxfwjc = $.trim(mini.get("xxfwjc").getValue())
    if (!xxfwjc) {
        return {"result": false, "message": "请填写诊断口是否下线访问检查"};
    }
    var cyjedgl = $.trim(mini.get("cyjedgl").getValue())
    if (!cyjedgl) {
        return {"result": false, "message": "请填写额定功率(kW)"};
    }
    var dzyx = $.trim(mini.get("dzyx").getValue())
    if (!dzyx) {
        return {"result": false, "message": "请选择是否有多种运行模式"};
    }
    if(cyjedgl=='37kW及以上'&&type!="old"){
        var dqId = $.trim(mini.get("dqId").getValue())
        if (!dqId) {
            return {"result": false, "message": "请选择电气工程师"};
        }
    }
    var cpFileList=cpFileListGrid.getData();
    if(cpFileList.length<6&&type!="old") {
        return {"result": false, "message": "请上传所需的6种附件"};
    }
    return {"result": true};
}

function validDqgcs() {
    var pfkzwz = $.trim(mini.get("pfkzwz").getValue())
    if (!pfkzwz) {
        return {"result": false, "message": "请填写排放控制诊断系统（NCD/PCD/NCD+PCD系统）接口位置"};
    }
    var pfkztdwz = $.trim(mini.get("pfkztdwz").getValue())
    if (!pfkztdwz) {
        return {"result": false, "message": "请填写排放控制诊断系统（NCD/PCD/NCD+PCD系统）接口替代位置"};
    }
    var czzdxh = $.trim(mini.get("czzdxh").getValue())
    if (!czzdxh) {
        return {"result": false, "message": "请填写远程车载终端型号"};
    }
    var czzdscc = $.trim(mini.get("czzdscc").getValue())
    if (!czzdscc) {
        return {"result": false, "message": "请填写远程车载终端生产厂"};
    }
    var czzdwz = $.trim(mini.get("czzdwz").getValue())
    if (!czzdwz) {
        return {"result": false, "message": "请填写远程车载终端安装位置"};
    }
    return {"result": true};
}
function validDlgcs() {
    var rlgg = $.trim(mini.get("rlgg").getValue())
    if (!rlgg) {
        return {"result": false, "message": "请填写燃料规格"};
    }
    var fsqd = $.trim(mini.get("fsqd").getValue())
    if (!fsqd) {
        return {"result": false, "message": "请填写冷却风扇驱动方式"};
    }
    var rllx = $.trim(mini.get("rllx").getValue())
    if (!rllx) {
        return {"result": false, "message": "请填写燃料类型"};
    }
    var rlxsl = $.trim(mini.get("rlxsl").getValue())
    if (!rlxsl) {
        return {"result": false, "message": "请填写燃料箱数量"};
    }
    var rlxrj = $.trim(mini.get("rlxrj").getValue())
    if (!rlxrj) {
        return {"result": false, "message": "请填写燃油箱各油箱容积（L）"};
    }
    var ryxzrj = $.trim(mini.get("ryxzrj").getValue())
    if (!ryxzrj) {
        return {"result": false, "message": "请填写燃油箱总容积(L)"};
    }
    var ryxcl = $.trim(mini.get("ryxcl").getValue())
    if (!ryxcl) {
        return {"result": false, "message": "请填写燃油箱材料"};
    }
    var ryxwz = $.trim(mini.get("ryxwz").getValue())
    if (!ryxwz) {
        return {"result": false, "message": "请填写燃油箱安装位置"};
    }
    // var nsxsl = $.trim(mini.get("nsxsl").getValue())
    // if (!nsxsl) {
    //     return {"result": false, "message": "请填写尿素箱数量"};
    // }
    // var nsxrj = $.trim(mini.get("nsxrj").getValue())
    // if (!nsxrj) {
    //     return {"result": false, "message": "请填写尿素箱容积（L）"};
    // }
    var fdjzcwlh = $.trim(mini.get("fdjzcwlh").getValue())
    if (!fdjzcwlh) {
        return {"result": false, "message": "请填写发动机总成物料号"};
    }
    var fdjgkbh = $.trim(mini.get("fdjgkbh").getValue())
    if (!fdjgkbh) {
        return {"result": false, "message": "请填写发动机环保信息公开编号"};
    }
    var zdjznj = $.trim(mini.get("zdjznj").getValue())
    if (!zdjznj) {
        return {"result": false, "message": "请填写最大基准扭矩"};
    }
    var fyjccgrj = $.trim(mini.get("fyjccgrj").getValue())
    if (!fyjccgrj) {
        return {"result": false, "message": "请填写反应剂存储罐容积"};
    }
    var cyjxs = $.trim(mini.get("cyjxs").getValue())
    if (!cyjxs) {
        return {"result": false, "message": "请填写发动机系族"};
    }
    var fdjxh = $.trim(mini.get("fdjxh").getValue())
    if (!fdjxh) {
        return {"result": false, "message": "请填写发动机型号"};
    }
    var fdjzzs = $.trim(mini.get("fdjzzs").getValue())
    if (!fdjzzs) {
        return {"result": false, "message": "请填写发动机品牌"};
    }
    var fdjsb = $.trim(mini.get("fdjsb").getValue())
    if (!fdjsb) {
        return {"result": false, "message": "请填写发动机商标"};
    }
    var fdjscdz = $.trim(mini.get("fdjscdz").getValue())
    if (!fdjscdz) {
        return {"result": false, "message": "请填写发动机生产厂地址"};
    }
    // var fyjgyw = $.trim(mini.get("fyjgyw").getValue())
    // if (!fyjgyw) {
    //     return {"result": false, "message": "请填写反应剂罐有无"};
    // }
    // if(fyjgyw=='有'){
    //     var fyjgcl = $.trim(mini.get("fyjgcl").getValue())
    //     if (!fyjgcl) {
    //         return {"result": false, "message": "请填写反应剂罐材料"};
    //     }
    //     var fyjsm = $.trim(mini.get("fyjsm").getValue())
    //     if (!fyjsm) {
    //         return {"result": false, "message": "请填写有无反应剂使用说明标签"};
    //     }
    //     var fyjfd = $.trim(mini.get("fyjfd").getValue())
    //     if (!fyjfd) {
    //         return {"result": false, "message": "请填写有无反应剂防冻系统"};
    //     }
    //     var fyjps = $.trim(mini.get("fyjps").getValue())
    //     if (!fyjps) {
    //         return {"result": false, "message": "请填写有无反应剂喷射系统防冻系统"};
    //     }
    // }
    var dlFileList=dlFileListGrid.getData();
    if(dlFileList.length<4) {
        return {"result": false, "message": "请上传所需的4种附件"};
    }
    return {"result": true};
}
function validBzh() {
    var sb = $.trim(mini.get("sb").getValue())
    if (!sb) {
        return {"result": false, "message": "请填写商标"};
    }
    var jxdl = $.trim(mini.get("jxdl").getValue())
    if (!jxdl) {
        return {"result": false, "message": "请填写机械分类-大类"};
    }
    var jxxl = $.trim(mini.get("jxxl").getValue())
    if (!jxxl) {
        return {"result": false, "message": "请填写机械分类-小类"};
    }
    var zzsmc = $.trim(mini.get("zzsmc").getValue())
    if (!zzsmc) {
        return {"result": false, "message": "请填写制造商名称"};
    }
    var scdz = $.trim(mini.get("scdz").getValue())
    if (!scdz) {
        return {"result": false, "message": "请填写生产地址"};
    }
    return {"result": true};
}
function validPfzy() {
    var zjgkh = $.trim(mini.get("zjgkh").getValue());
    if (!zjgkh) {
        return {"result": false, "message": "请填写整机环保信息公开号"};
    }
    var hbFileList=hbFileListGrid.getData();
    if(hbFileList.length<4) {
        return {"result": false, "message": "请上传所需的4种附件"};
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



function downLoadCxFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Cx/cxPdfPreview.do?action=download');
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputstandardId = $("<input>");
    inputstandardId.attr("type", "hidden");
    inputstandardId.attr("name", "formId");
    inputstandardId.attr("value", formId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputstandardId);
    form.append(inputFileId);
    form.submit();
    form.remove();
}

function returnCxPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/environment/core/Cx/cxPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/environment/core/Cx/cxOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/environment/core/Cx/cxImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
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
        if (nodeVars[i].KEY_ == 'first') {
            first = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'second') {
            second = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'third') {
            third = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'forth') {
            forth = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'fifth') {
            fifth = nodeVars[i].DEF_VAL_;
        }

    }
    formCx.setEnabled(false);
    mini.get("addCpFile").setEnabled(false);
    mini.get("addDqFile").setEnabled(false);
    mini.get("addDlFile").setEnabled(false);
    mini.get("addHbFile").setEnabled(false);
    mini.get("downloadGsClhb").setEnabled(false);
    mini.get("downloadGsClhbdj").setEnabled(false);
    mini.get("downImportGsClhbcz").setEnabled(false);
    mini.get("downImportGsClhbwj").setEnabled(false);
    mini.get("downImportGsClhbpf").setEnabled(false);
    mini.get("downImportGsClhbsx").setEnabled(false);
    if (first == 'yes') {
        mini.get("dqId").setEnabled(true);
        mini.get("dlId").setEnabled(true);
        mini.get("nbbh").setEnabled(true);
        mini.get("zjsjxh").setEnabled(true);
        mini.get("cpjxxh").setEnabled(true);
        mini.get("zjwlh").setEnabled(true);
        mini.get("jxmc").setEnabled(true);
        mini.get("zlname").setEnabled(true);
        mini.get("zl").setEnabled(true);
        mini.get("wxcc").setEnabled(true);
        mini.get("jxsbff").setEnabled(true);
        mini.get("hbdmwz").setEnabled(true);
        mini.get("hbbqwz").setEnabled(true);
        mini.get("zzl").setEnabled(true);
        mini.get("zdcs").setEnabled(true);
        mini.get("edgzcs").setEnabled(true);
        mini.get("dzyx").setEnabled(true);
        mini.get("addCpFile").setEnabled(true);
        // mini.get("sfgz").setEnabled(true);
        mini.get("cyjedgl").setEnabled(true);
        mini.get("upvecc").setEnabled(true);
        mini.get("xxfwjc").setEnabled(true);
        mini.get("fldm").setEnabled(true);
        mini.get("downImportGsClhbcz").setEnabled(true);
        mini.get("downImportGsClhbwj").setEnabled(true);
    }

    if (second == 'yes') {
        mini.get("pfkzwz").setEnabled(true);
        mini.get("pfkztdwz").setEnabled(true);
        mini.get("czzdxh").setEnabled(true);
        mini.get("czzdscc").setEnabled(true);
        mini.get("czzdwz").setEnabled(true);
        mini.get("addDqFile").setEnabled(true);
    }
    if (third == 'yes') {
        mini.get("rlgg").setEnabled(true);
        mini.get("fsqd").setEnabled(true);
        mini.get("rllx").setEnabled(true);
        mini.get("rlxsl").setEnabled(true);
        mini.get("rlxrj").setEnabled(true);
        // mini.get("nsxsl").setEnabled(true);
        // mini.get("nsxrj").setEnabled(true);
        mini.get("fdjxh").setEnabled(true);
        mini.get("fdjzzs").setEnabled(true);
        mini.get("fdjscdz").setEnabled(true);
        mini.get("fdjsb").setEnabled(true);
        mini.get("cyjxs").setEnabled(true);
        mini.get("ryxzrj").setEnabled(true);
        mini.get("ryxcl").setEnabled(true);
        mini.get("ryxqt").setEnabled(true);
        mini.get("ryxwz").setEnabled(true);
        // mini.get("fyjgyw").setEnabled(true);
        // mini.get("fyjgcl").setEnabled(true);
        // mini.get("fyjgqt").setEnabled(true);
        // mini.get("fyjsm").setEnabled(true);
        // mini.get("fyjfd").setEnabled(true);
        // mini.get("fyjps").setEnabled(true);
        mini.get("fdjzcwlh").setEnabled(true);
        mini.get("fdjgkbh").setEnabled(true);
        mini.get("zdjznj").setEnabled(true);
        mini.get("fyjccgrj").setEnabled(true);
        mini.get("addDlFile").setEnabled(true);
        mini.get("downImportGsClhbpf").setEnabled(true);
        mini.get("downImportGsClhbsx").setEnabled(true);
    }
    if (fifth == 'yes') {
        mini.get("zjgkh").setEnabled(true);
        mini.get("downloadGsClhb").setEnabled(true);
        mini.get("downloadGsClhbdj").setEnabled(true);
        mini.get("addHbFile").setEnabled(true);
    }
}


function downloadPic() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/downloadPic.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhb() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importGsClhbDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhbdj() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importDjxxDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhbcz() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importCzDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhbwj() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importWjDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhbpf() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importPfDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function downImportGsClhbsx() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Cx/importSxDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}



function saveClhbInProcess() {
    var formData = _GetFormJsonMini("formCx");
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Cx/saveCx.do',
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
