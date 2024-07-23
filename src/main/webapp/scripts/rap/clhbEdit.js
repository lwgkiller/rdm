var isCPZG="";
var isDLGCS="";
var isBZH="";
var isPFZY="";

$(function () {
    if (wjId) {
        var url = jsUseCtxPath + "/environment/core/Wj/getWjDetail.do";
        $.post(
            url,
            {wjId: wjId},
            function (json) {
                formWj.setData(json);
            });
    }else {
        if(type == "old" && oldWjId){
            var url = jsUseCtxPath + "/environment/core/Wj/getOldWjDetail.do";
            $.post(
                url,
                {wjId: oldWjId},
                function (json) {
                    formWj.setData(json);
                    mini.get("apply").setValue(currentUserId);
                    mini.get("apply").setText(currentUserName);
                    mini.get("dept").setValue(currentUserMainDepId);
                    mini.get("dept").setText(deptName);
                    mini.get("type").setValue(type);
                    mini.get("oldWjId").setValue(oldWjId);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("dept").setValue(currentUserMainDepId);
            mini.get("dept").setText(deptName);
            mini.get("type").setValue(type);
        }
    }
    mini.get("wjBrand").setValue("徐工牌(XCMG)");
    mini.get("classA").setValue("工程机械");
    mini.get("classB").setValue("挖掘机械");
    mini.get("producter").setValue("徐州徐工挖掘机械有限公司");
    mini.get("adress").setValue("江苏省徐州经济技术开发区高新路39号");
    mini.get("wjEmission").setValue("第3阶段");
    mini.get("carType").setValue("非道路移动机械");
    mini.get("nationStandard").setValue("GB 20891-2014和GB 36886-2018烟度标准");
    mini.get("zlName").setValue("工作质量");
    mini.get("dmLocation").setValue("PIN码在整机铭牌上");
    mini.get("wjName").setValue("液压挖掘机");
    mini.get("fuelType").setValue("柴油");
    mini.get("fuelSpecies").setValue("GB19147 车用柴油");
    formWj.setEnabled(false);
    mini.get("addFile2").setEnabled(false);
    mini.get("downloadClhb").setEnabled(false);
    mini.get("downloadGsClhbdj").setEnabled(false);
    mini.get("dlId").setEnabled(true);
    mini.get("wjNo").setEnabled(true);
    mini.get("wjEmission").setEnabled(true);
    mini.get("wjModel").setEnabled(true);
    mini.get("wjName").setEnabled(true);
    mini.get("wjWeight").setEnabled(true);
    mini.get("ratedPower").setEnabled(true);
    mini.get("wjSize").setEnabled(true);
    mini.get("carType").setEnabled(true);
    mini.get("nationStandard").setEnabled(true);
    mini.get("zlName").setEnabled(true);
    mini.get("bsLocation").setEnabled(true);
    mini.get("dmLocation").setEnabled(true);
    mini.get("bqLocation").setEnabled(true);
    mini.get("parameter").setEnabled(true);
    mini.get("zjwlh").setEnabled(true);
    mini.get("addFile").setEnabled(true);
    mini.get("designXh").setEnabled(true);
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formWj.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }else if(action == "change")
    {
        mini.get("addFile").setEnabled(true);
        mini.get("addFile2").setEnabled(true);
        mini.get("downloadClhb").setEnabled(true);
        mini.get("downloadGsClhbdj").setEnabled(true);
        formWj.setEnabled(true);
        mini.get("num").setEnabled(false);
        mini.get("CREATE_TIME_").setEnabled(false);
        mini.get("apply").setEnabled(false);
        mini.get("dept").setEnabled(false);
        mini.get("dlId").setEnabled(false);
        $("#changeToolBar").show();
    }
});
function getData() {
    var formData = _GetFormJsonMini("formWj");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // fo   rmData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveClhb(e) {
    window.parent.saveDraft(e);
}
function startClhbProcess(e) {
    var formValid = validCpzg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}
function clhbCpzgApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isCPZG == 'yes') {
        var formValid = validCpzg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function clhbDlgcsApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isDLGCS == 'yes') {
        var formValid = validDlgcs();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function clhbBzhApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBZH == 'yes') {
        var formValid = validBzh();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function clhbPfzyApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isPFZY == 'yes') {
        var formValid = validPfzy();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function validDlgcs() {
    var fuelType = $.trim(mini.get("fuelType").getValue());
    if (!fuelType) {
        return {"result": false, "message": "请填写燃料类型"};
    }
    var fuelSpecies = $.trim(mini.get("fuelSpecies").getValue());
    if (!fuelSpecies) {
        return {"result": false, "message": "请填写燃料规格"};
    }
    var grMeasures = $.trim(mini.get("grMeasures").getValue());
    if (!grMeasures) {
        return {"result": false, "message": "请填写隔热措施"};
    }
    var grMeasuresMs = $.trim(mini.get("grMeasuresMs").getValue());
    if (!grMeasuresMs) {
        return {"result": false, "message": "请填写隔热措施描述"};
    }
    var hclLocation = $.trim(mini.get("hclLocation").getValue());
    if (!hclLocation) {
        return {"result": false, "message": "请填写后处理具体安装位置"};
    }
    var jqSystem = $.trim(mini.get("jqSystem").getValue());
    if (!jqSystem) {
        return {"result": false, "message": "请填写进气系统特征"};
    }
    var resistance = $.trim(mini.get("resistance").getValue());
    if (!resistance) {
        return {"result": false, "message": "请填写机械进气系统阻力"};
    }
    var coldType = $.trim(mini.get("coldType").getValue());
    if (!coldType) {
        return {"result": false, "message": "请填写中冷器形式"};
    }
    var pqPressure = $.trim(mini.get("pqPressure").getValue());
    if (!pqPressure) {
        return {"result": false, "message": "请填写发动机额定转速和100%负荷下,排气系统背压（kPa）"};
    }
    var fdjxh = $.trim(mini.get("fdjxh").getValue());
    if (!fdjxh) {
        return {"result": false, "message": "请填写发动机型号"};
    }
    var fdjsb = $.trim(mini.get("fdjsb").getValue())
    if (!fdjsb) {
        return {"result": false, "message": "请填写发动机商标"};
    }
    var fdjzzs = $.trim(mini.get("fdjzzs").getValue());
    if (!fdjzzs) {
        return {"result": false, "message": "请填写发动机品牌"};
    }
    var fdjzcwlh = $.trim(mini.get("fdjzcwlh").getValue());
    if (!fdjzcwlh) {
        return {"result": false, "message": "请填写发动机总成物料号"};
    }
    var fdjscdz = $.trim(mini.get("fdjscdz").getValue());
    if (!fdjscdz) {
        return {"result": false, "message": "请填写发动机生产厂地址"};
    }
    var fdjgkbh = $.trim(mini.get("fdjgkbh").getValue());
    if (!fdjgkbh) {
        return {"result": false, "message": "请填写发动机环保信息公开编号"};
    }
    return {"result": true};
}

function validCpzg() {
    var dlId = $.trim(mini.get("dlId").getValue());
    var type = $.trim(mini.get("type").getValue());
    if (!dlId&&type!="old") {
        return {"result": false, "message": "请选择动力工程师"};
    }
    var wjNo = $.trim(mini.get("wjNo").getValue());
    if (!wjNo) {
        return {"result": false, "message": "请填写内部编号"};
    }
    var wjEmission = $.trim(mini.get("wjEmission").getValue());
    if (!wjEmission) {
        return {"result": false, "message": "请填写排放标准"};
    }
    var carType = $.trim(mini.get("carType").getValue());
    if (!carType) {
        return {"result": false, "message": "请选择车辆类别"};
    }
    var nationStandard = $.trim(mini.get("nationStandard").getValue());
    if (!nationStandard) {
        return {"result": false, "message": "请选择国家标准"};
    }
    var designXh = $.trim(mini.get("designXh").getValue());
    if (!designXh) {
        return {"result": false, "message": "请填写整机设计型号"};
    }
    var wjModel = $.trim(mini.get("wjModel").getValue());
    if (!wjModel) {
        return {"result": false, "message": "请填写机械型号"};
    }
    var wjName = $.trim(mini.get("wjName").getValue());
    if (!wjName) {
        return {"result": false, "message": "请填写机械名称"};
    }
    var wjWeight = $.trim(mini.get("wjWeight").getValue());
    if (!wjWeight) {
        return {"result": false, "message": "请填写质量"};
    }
    var ratedPower = $.trim(mini.get("ratedPower").getValue());
    if (!ratedPower) {
        return {"result": false, "message": "请填写额定功率"};
    }
    var zlName = $.trim(mini.get("zlName").getValue());
    if (!zlName) {
        return {"result": false, "message": "请选择质量名称"};
    }
    var wjSize = $.trim(mini.get("wjSize").getValue());
    if (!wjSize) {
        return {"result": false, "message": "请填写外形尺寸"};
    }
    var bsLocation = $.trim(mini.get("bsLocation").getValue());
    if (!bsLocation) {
        return {"result": false, "message": "请选择机械的标识方法和位置"};
    }
    var dmLocation = $.trim(mini.get("dmLocation").getValue());
    if (!dmLocation) {
        return {"result": false, "message": "请选择机械环保代码在机体上的打刻位置"};
    }
    var bqLocation = $.trim(mini.get("bqLocation").getValue());
    if (!bqLocation) {
        return {"result": false, "message": "请选择环保信息标签的位置"};
    }
    var zjwlh = $.trim(mini.get("zjwlh").getValue());
    if (!zjwlh) {
        return {"result": false, "message": "请填写整机物料号"};
    }
    return {"result": true};
}
function validBzh() {
    var wjBrand = $.trim(mini.get("wjBrand").getValue());
    if (!wjBrand) {
        return {"result": false, "message": "请填写商标"};
    }
    var classA = $.trim(mini.get("classA").getValue());
    if (!classA) {
        return {"result": false, "message": "请选择机械分类-大类"};
    }
    var classB = $.trim(mini.get("classB").getValue());
    if (!classB) {
        return {"result": false, "message": "请选择机械分类-小类"};
    }
    var producter = $.trim(mini.get("producter").getValue());
    if (!producter) {
        return {"result": false, "message": "请选择制造商名称"};
    }
    var adress = $.trim(mini.get("adress").getValue());
    if (!adress) {
        return {"result": false, "message": "请选择生产厂地址"};
    }
    return {"result": true};
}
function validPfzy() {
    var zjgkh = $.trim(mini.get("zjgkh").getValue());
    if (!zjgkh) {
        return {"result": false, "message": "请填写整机环保信息公开号"};
    }
    var fileListGridHb=fileListGrid2.getData();
    if(fileListGridHb.length<3) {
        return {"result": false, "message": "请上传整机环保信息公开表等附件"};
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

function fileuploadtupian() {
    var wjId = mini.get("wjId").getValue();
    if (!wjId) {
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Wj/openUploadWindow.do?fileType=tupian&wjId=" + wjId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}

function fileuploadbiaoge() {
    var wjId = mini.get("wjId").getValue();
    if (!wjId) {
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Wj/openUploadWindow.do?fileType=biaoge&wjId=" + wjId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (fileListGrid2) {
                fileListGrid2.load();
            }
        }
    });
}

function downLoadWjFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Wj/wjPdfPreview.do?action=download');
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

function returnWjPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/environment/core/Wj/wjPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/environment/core/Wj/wjOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/environment/core/Wj/wjImagePreview';
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
        if (nodeVars[i].KEY_ == 'isCPZG') {
            isCPZG = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isDLGCS') {
            isDLGCS = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isPFZY') {
            isPFZY = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isBZH') {
            isBZH = nodeVars[i].DEF_VAL_;
        }

    }
    formWj.setEnabled(false);
    mini.get("addFile").setEnabled(false);
    mini.get("addFile2").setEnabled(false);
    mini.get("downloadClhb").setEnabled(false);
    mini.get("downloadGsClhbdj").setEnabled(false);
    if (isCPZG == 'yes') {
        mini.get("dlId").setEnabled(true);
        mini.get("designXh").setEnabled(true);
        mini.get("wjNo").setEnabled(true);
        mini.get("wjEmission").setEnabled(true);
        mini.get("wjModel").setEnabled(true);
        mini.get("wjName").setEnabled(true);
        mini.get("wjWeight").setEnabled(true);
        mini.get("wjSize").setEnabled(true);
        mini.get("carType").setEnabled(true);
        mini.get("nationStandard").setEnabled(true);
        mini.get("zlName").setEnabled(true);
        mini.get("bsLocation").setEnabled(true);
        mini.get("dmLocation").setEnabled(true);
        mini.get("bqLocation").setEnabled(true);
        mini.get("parameter").setEnabled(true);
        mini.get("zjwlh").setEnabled(true);
        mini.get("addFile").setEnabled(true);
        mini.get("ratedPower").setEnabled(true);
    }

    if (isDLGCS == 'yes') {
        mini.get("fuelType").setEnabled(true);
        mini.get("fuelSpecies").setEnabled(true);
        mini.get("grMeasures").setEnabled(true);
        mini.get("grMeasuresMs").setEnabled(true);
        mini.get("hclLocation").setEnabled(true);
        mini.get("jqSystem").setEnabled(true);
        mini.get("resistance").setEnabled(true);
        mini.get("coldType").setEnabled(true);
        mini.get("pqPressure").setEnabled(true);
        mini.get("fdjxh").setEnabled(true);
        mini.get("fdjsb").setEnabled(true);
        mini.get("fdjzzs").setEnabled(true);
        mini.get("fdjzcwlh").setEnabled(true);
        mini.get("fdjscdz").setEnabled(true);
        mini.get("fdjgkbh").setEnabled(true);
    }
    if (isBZH == 'yes') {
        mini.get("wjBrand").setEnabled(true);
        mini.get("classA").setEnabled(true);
        mini.get("classB").setEnabled(true);
        mini.get("producter").setEnabled(true);
        mini.get("adress").setEnabled(true);
    }
    if (isPFZY == 'yes') {
        mini.get("zjgkh").setEnabled(true);
        mini.get("addFile2").setEnabled(true);
        mini.get("downloadClhb").setEnabled(true);
        mini.get("downloadGsClhbdj").setEnabled(true);
    }

}

function saveClhbInProcess() {
    var formData = _GetFormJsonMini("formWj");
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Wj/saveWj.do',
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
function saveChange(){
    saveClhbInProcess();
}

function downImportClhb() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Wj/importClhbDownload.do");
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