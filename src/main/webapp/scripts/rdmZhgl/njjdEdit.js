var isBianzhi = "";
var isShenHe = "";
var isJsWeiHu = "";
var isScUser = "";
$(function () {
    initForm();
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isBianzhi') {
            isBianzhi = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isShenHe') {
            isShenHe = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isJsWeiHu') {
            isJsWeiHu = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isScUser') {
            isScUser = nodeVars[i].DEF_VAL_;
        }
    }

    if (njjdId) {
        var url = jsUseCtxPath + "/zhgl/core/njjd/getNjjdDetail.do";
        $.post(
            url,
            {njjdId: njjdId},
            function (json) {
                formJsmm.setData(json);
            });
    }

    // 技术材料附件对挖掘机械研究院可见
    var dom = document.getElementById("show");
    if (isJSZX || isJsWeiHu || action=="edit") {
        dom.className = "";
    } else {
        dom.className = "conceal";
    }

    // 用户信息对营销公司市场部和农机鉴定专员可见
    var userDom = document.getElementById("user");
    if (isUser || isScUser) {
        userDom.className = "";
    } else {
        userDom.className = "conceal";
    }
    //明细入口
    if (action == 'detail') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        if(isNjzy == false) {
            mini.get("addFiles").setEnabled(false);
        }
        mini.get("operateAdd").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        detailXzpzProcess();
        $("#ifDisplay").show();
        if(isNjzy) {
            mini.get("haveSubsidy").setEnabled(true);
            mini.get("subsidyEndDate").setEnabled(true);
            mini.get("saveSubsidy").show();
        }
    } else if (action == 'task') {
        taskActionProcess();
    }else if(action == 'edit'){
        mini.get("addFiles").setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
        mini.get("haveSubsidy").setEnabled(false);
        mini.get("subsidyEndDate").setEnabled(false);
    }

});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formJsmm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }

    formData.changeXzpzData=grid_njjd_xzpzInfo.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'jsmmName', val: formData.jsmmName}];
    return formData;
}

//保存草稿
function saveJsmm(e) {
    var productType = $.trim(mini.get("productType").getValue())
    if (!productType) {
        mini.alert("请添加产品型号");
        return;
    }
    if(grid_njjd_xzpzInfo.getChanges().length > 0){
        var xzpzFormValid =draXzpz();
        if (!xzpzFormValid.result) {
            mini.alert(xzpzFormValid.message);
            return;
        }
    }
    //查询该型号是否有停售状态，有则不允许提交
    var productType = $.trim(mini.get("productType").getValue())
    var postData = {productType: productType, njjdId: njjdId};
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/njjd/selectproductTypeNum.do',
        type: 'post',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            console.log("查询该型号是否有停售状态", data)
            if (parseInt(data.num) > 0) {
                mini.alert('已有该类型在售产品，请勿重复提交哦！');
                return;
            } else {
                window.parent.saveDraft(e);
            }
        }
    });

}

//检验表单是否必填
function validNjjd() {
    var productType = $.trim(mini.get("productType").getValue())
    if (!productType) {
        return {"result": false, "message": "请填写产品型号"};
    }
    var salesArea = $.trim(mini.get("salesArea").getValue())
    if (!salesArea) {
        return {"result": false, "message": "请填写主销区域"};
    }
    var njcl = $.trim(mini.get("njcl").getValue())
    if (!njcl) {
        return {"result": false, "message": "请填写该型号产品生产量"};
    }
    var njxs = $.trim(mini.get("njxs").getValue())
    if (!njxs) {
        return {"result": false, "message": '请填写该型号产品销售量'};
    }
    var projectName = $.trim(mini.get("projectName").getValue())
    if (!projectName) {
        return {"result": false, "message": "请填写所属项目名称"};
    }
    var projectNumber = $.trim(mini.get("projectNumber").getValue())
    if (!projectNumber) {
        return {"result": false, "message": "请填写所属项目编号"};
    }

    var ztXh = $.trim(mini.get("ztXh").getValue())
    if (!ztXh) {
        return {"result": false, "message": "请填写整机参数型号"};
    }
    var ztJgxs = $.trim(mini.get("ztJgxs").getValue())
    if (!ztJgxs) {
        return {"result": false, "message": "请填写整机参数结构型式"};
    }
    var ztGzzl = $.trim(mini.get("ztGzzl").getValue())
    if (!ztGzzl) {
        return {"result": false, "message": "请填写整机参数工作质量"};
    }
    var ztCdrl = $.trim(mini.get("ztCdrl").getValue())
    if (!ztCdrl) {
        return {"result": false, "message": "请填写整机参数铲斗容量"};
    }
    var ztWxcc = $.trim(mini.get("ztWxcc").getValue())
    if (!ztWxcc) {
        return {"result": false, "message": "请填写整机参数外形尺寸和运输状态"};
    }
    var ztZj = $.trim(mini.get("ztZj").getValue())
    if (!ztZj) {
        return {"result": false, "message": "请填写整机参数轴距"};
    }
    var ztLsLj = $.trim(mini.get("ztLsLj").getValue())
    if (!ztLsLj) {
        return {"result": false, "message": "请填写整机参数轮式轮距"};
    }
    var ztLsLtgg = $.trim(mini.get("ztLsLtgg").getValue())
    if (!ztLsLtgg) {
        return {"result": false, "message": "请填写整机参数轮式轮胎规格"};
    }
    var ztLsLtQy = $.trim(mini.get("ztLsLtQy").getValue())
    if (!ztLsLtQy) {
        return {"result": false, "message": "请填写整机参数轮式轮胎气压"};
    }
    var ztLdGj = $.trim(mini.get("ztLdGj").getValue())
    if (!ztLdGj) {
        return {"result": false, "message": "请填写整机参数履带式履带轨距"};
    }
    var ztLdCd = $.trim(mini.get("ztLdCd").getValue())
    if (!ztLdCd) {
        return {"result": false, "message": "请填写整机参数履带接地长度"};
    }
    var ztLdKd = $.trim(mini.get("ztLdKd").getValue())
    if (!ztLdKd) {
        return {"result": false, "message": "请填写整机参数履带板宽度"};
    }
    var ztLdcz = $.trim(mini.get("ztLdcz").getValue())
    if (!ztLdcz) {
        return {"result": false, "message": "请填写整机参数履带式履带材质"};
    }
    var ztHdhzBj = $.trim(mini.get("ztHdhzBj").getValue())
    if (!ztHdhzBj) {
        return {"result": false, "message": "请填写整机参数后端回转半径"};
    }
    var ztLdJx = $.trim(mini.get("ztLdJx").getValue())
    if (!ztLdJx) {
        return {"result": false, "message": "请填写整机参数离地间隙"};
    }
    var ztZtldGd = $.trim(mini.get("ztZtldGd").getValue())
    if (!ztZtldGd) {
        return {"result": false, "message": "请填写整机参数转台离地高度"};
    }
    var ztZtKd = $.trim(mini.get("ztZtKd").getValue())
    if (!ztZtKd) {
        return {"result": false, "message": "请填写整机参数转台总宽度"};
    }
    var ztZtwdCd = $.trim(mini.get("ztZtwdCd").getValue())
    if (!ztZtwdCd) {
        return {"result": false, "message": "请填写整机参数转台尾端长度"};
    }
    var ztZxj = $.trim(mini.get("ztZxj").getValue())
    if (!ztZxj) {
        return {"result": false, "message": "请填写整机参数回转中心至驱动轮中心距"};
    }
    var ztJdby = $.trim(mini.get("ztJdby").getValue())
    if (!ztJdby) {
        return {"result": false, "message": "请填写整机参数平均接地比压"};
    }
    var ztDws = $.trim(mini.get("ztDws").getValue())
    if (!ztDws) {
        return {"result": false, "message": "请填写整机参数档位数"};
    }
    var ztSdQj = $.trim(mini.get("ztSdQj").getValue())
    if (!ztSdQj) {
        return {"result": false, "message": "请填写整机参数理论速度前进"};
    }
    var ztSdDt = $.trim(mini.get("ztSdDt").getValue())
    if (!ztSdDt) {
        return {"result": false, "message": "请填写整机参数理论速度倒退"};
    }
    var ztPp = $.trim(mini.get("ztPp").getValue())
    if (!ztPp) {
        return {"result": false, "message": "请填写整机参数爬坡能力"};
    }
    var fdjXh = $.trim(mini.get("fdjXh").getValue())
    if (!fdjXh) {
        return {"result": false, "message": "请填写发动机型号"};
    }
    var fdjXs = $.trim(mini.get("fdjXs").getValue())
    if (!fdjXs) {
        return {"result": false, "message": "请填写发动机型式"};
    }
    var fdjFactory = $.trim(mini.get("fdjFactory").getValue())
    if (!fdjFactory) {
        return {"result": false, "message": "请填写发动机生产厂"};
    }
    var fdjBdGl = $.trim(mini.get("fdjBdGl").getValue())
    if (!fdjBdGl) {
        return {"result": false, "message": "请填写发动机标定功率"};
    }
    var fdjEdGl = $.trim(mini.get("fdjEdGl").getValue())
    if (!fdjEdGl) {
        return {"result": false, "message": "请填写发动机额定净功率"};
    }
    var fdjBdZs = $.trim(mini.get("fdjBdZs").getValue())
    if (!fdjBdZs) {
        return {"result": false, "message": "请填写发动机标定转速"};
    }
    var jssXh = $.trim(mini.get("jssXh").getValue())
    if (!jssXh) {
        return {"result": false, "message": "请填写驾驶室型号"};
    }
    var jssXs = $.trim(mini.get("jssXs").getValue())
    if (!jssXs) {
        return {"result": false, "message": "请填写驾驶室型式"};
    }
    var jssFactory = $.trim(mini.get("jssFactory").getValue())
    if (!jssFactory) {
        return {"result": false, "message": "请填写驾驶室生产厂"};
    }
    var zyccZdWjlCd = $.trim(mini.get("zyccZdWjlCd").getValue())
    if (!zyccZdWjlCd) {
        return {"result": false, "message": "请填写作业参数最大挖掘力铲斗"};
    }
    var zycsZdwjlDg = $.trim(mini.get("zycsZdwjlDg").getValue())
    if (!zycsZdwjlDg) {
        return {"result": false, "message": "请填写作业参数最大挖掘力斗杆"};
    }
    var zycsZdwjBj = $.trim(mini.get("zycsZdwjBj").getValue())
    if (!zycsZdwjBj) {
        return {"result": false, "message": "请填写作业参数最大挖掘半径"};
    }
    var zycsZdwjSd = $.trim(mini.get("zycsZdwjSd").getValue())
    if (!zycsZdwjSd) {
        return {"result": false, "message": "请填写作业参数最大挖掘深度"};
    }
    var zycsZdCzwjSd = $.trim(mini.get("zycsZdCzwjSd").getValue())
    if (!zycsZdCzwjSd) {
        return {"result": false, "message": "请填写作业参数最大垂直挖掘深度"};
    }
    var zycsZdwjGd = $.trim(mini.get("zycsZdwjGd").getValue())
    if (!zycsZdwjGd) {
        return {"result": false, "message": "请填写作业参数最大挖掘高度"};
    }
    var zycsZdxzGd = $.trim(mini.get("zycsZdxzGd").getValue())
    if (!zycsZdxzGd) {
        return {"result": false, "message": "请填写作业参数最大卸载高度"};
    }
    var gzzzDbCd = $.trim(mini.get("gzzzDbCd").getValue())
    if (!gzzzDbCd) {
        return {"result": false, "message": "请填写工作装置动臂长度"};
    }
    var gzzzDgCd = $.trim(mini.get("gzzzDgCd").getValue())
    if (!gzzzDgCd) {
        return {"result": false, "message": "请填写工作装置斗杆长度"};
    }
    var yybXh = $.trim(mini.get("yybXh").getValue())
    if (!yybXh) {
        return {"result": false, "message": "请填写液压系统主液压泵型号"};
    }
    var yybLl = $.trim(mini.get("yybLl").getValue())
    if (!yybLl) {
        return {"result": false, "message": "请填写液压系统主液压泵流量"};
    }
    var gzyl = $.trim(mini.get("gzyl").getValue())
    if (!gzyl) {
        return {"result": false, "message": "请填写液压系统设定工作压力"};
    }
    if(grid_njjd_xzpzInfo.getChanges().length > 0){
        var xzpzFormValid =draXzpz();
        if (!xzpzFormValid.result) {
            mini.alert(xzpzFormValid.message);
            return;
        }
    }

    return {"result": true};
}

//启动流程
function startJsmmProcess(e) {

    // 校验
    var formValid = validNjjd();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    //查询该型号是否有停售状态，有则不允许提交
    var productType = $.trim(mini.get("productType").getValue())
    var postData = {productType: productType, njjdId: njjdId};
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/njjd/selectproductTypeNum.do',
        type: 'post',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            console.log("查询该型号是否有停售状态", data)
            if (parseInt(data.num) > 0) {
                mini.alert('已有该类型在售产品，请勿重复提交哦！');
                return;
            } else {
                //判断用户条数

                //技术证件上传完整方可提交

                    var productType = $.trim(mini.get("productType").getValue())
                    var postData = {productType: productType, njjdId: njjdId};
                    $.ajax({
                        url: jsUseCtxPath + '/zhgl/core/njjd/selectPicNum.do',
                        type: 'post',
                        data: mini.encode(postData),
                        contentType: 'application/json',
                        success: function (data) {
                            if (parseInt(data.count) < 8) {
                                mini.alert("技术证件添加不全，请添加齐全后再提交哦！");
                                return;
                            } else {
                                //检查通过
                                window.parent.startProcess(e);
                            }
                        }
                    });

            }
        }
    });


}

//流程中暂存信息（如编制阶段）
function saveSubsidy() {
    var formData ={};
    formData.id=njjdId;
    formData.haveSubsidy = mini.get("haveSubsidy").getValue();
    formData.subsidyEndDate = mini.get("subsidyEndDate").getText();
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/njjd/saveSubsidy.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = "数据保存成功";
                } else {
                    message = "数据保存失败" + data.message;
                }

                mini.alert(message, "提示信息", function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function jsmmApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validNjjd();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }

    //用户添加阶段，用户提交条数不能低于5条
    if (isScUser == 'yes') {
        //查询用户条数
        var productType = $.trim(mini.get("productType").getValue())
        var postData = {productType: productType, njjdId: njjdId};
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/njjd/selectproductTypeNum.do',
            type: 'post',
            data: mini.encode(postData),
            contentType: 'application/json',
            success: function (data) {
                if (parseInt(data.count) <= 4) {
                    mini.alert("用户信息条数不能低于5条，请添加足够条数后再提交哦！");
                    return;
                } else {
                    //检查通过
                    window.parent.approve();
                }
            }
        });
    } else {
        //检查通过
        window.parent.approve();
    }

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

function addNjjdFile() {
    var njjdId = mini.get("njjdId").getValue();

    if (!njjdId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/njjd/fileUploadWindow.do?njjdId=" + njjdId + "&njfjDl=js",
        width: 750,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
            fileListGrid.load();
        },
        ondestroy: function (action) {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}


function addNjjdFiles() {
    var njjdId = mini.get("njjdId").getValue();

    if (!njjdId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/njjd/fileUploadWindow.do?njjdId=" + njjdId + "&njfjDl=zs",
        width: 750,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (fileListGrids) {
                fileListGrids.load();
            }
        }
    });
}


function addNjjdFJ() {
    var njjdId = mini.get("njjdId").getValue();

    if (!njjdId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/njjd/fileUploadWindow.do?njjdId=" + njjdId + "&njfjDl=yh",
        width: 750,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (grid) {
                grid.load();
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
        if (nodeVars[i].KEY_ == 'isShenHe') {
            isShenHe = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isJsWeiHu') {
            isJsWeiHu = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isScUser') {
            isScUser = nodeVars[i].DEF_VAL_;
        }
    }
    if (isBianzhi == 'yes') {
        // formJsmm.setEnabled(false);
        // mini.get("addFile").setEnabled(false);
        mini.get("addFiles").setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
    }

    if (isShenHe == 'yes') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addFiles").setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
        // mini.get("isOperation").setEnabled(false);
        detailXzpzProcess();
    }

    if (isJsWeiHu == 'yes') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
        mini.get("haveSubsidy").setEnabled(true);
        mini.get("subsidyEndDate").setEnabled(true);
        detailXzpzProcess();
    }

    if (isScUser == 'yes') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addFiles").setEnabled(false);
        // mini.get("operateAdd").setEnabled(false);
        detailXzpzProcess();
    }
}



function returnJsmmPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/zhgl/core/njjd/njjdPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/zhgl/core/njjd/njjdOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/zhgl/core/njjd/njjdImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}

//下载文档
function downLoadZsFile(fileName, fileId, njjdId) {

    //分管领导下载不作限制
    if (isFGLD) {
        downLoadJsjlFile(fileName, fileId, njjdId);
    } else {
        //判断 0-已有审批完成的未使用的申请(直接下载)/1-已有草稿或需要增加新申请/2-运行中的申请
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/njjd/checkOperateApply.do",
            type: 'POST',
            data: mini.encode({fileId: fileId, njjdId: njjdId, applyUserId: currentUserId}),
            contentType: 'application/json',
            async: false,
            success: function (data) {
                if (data) {
                    var result = data.result;
                    var applyId = data.applyId;
                    var instId = data.instId;
                    // 0-已有审批完成的未使用的申请(直接下载)
                    if (result == 0) {
                        downLoadJsjlFile(fileName, fileId, njjdId);
                        //修改下载状态
                        var postData = {saleStatus: "1", applyId: applyId};
                        $.ajax({
                            url: jsUseCtxPath + '/zhgl/core/njjd/updateSfxzButton.do',
                            type: 'post',
                            data: mini.encode(postData),
                            contentType: 'application/json',
                            success: function (data) {
                                fileListGrids.load();
                            }
                        });

                    } else if (result == 1) {
                        // 1-已有草稿或需要增加新申请
                        if (instId != "") {
                            editApplyRow(applyId, instId);
                        }else {
                            var applyCategoryId = "download";

                            title = "新增下载申请";

                            var width = getWindowSize().width;
                            var height = getWindowSize().height;
                            _OpenWindow({
                                url: jsUseCtxPath + "/bpm/core/bpmInst/NJJDXZSQ/start.do?standardApplyCategoryId=" + applyCategoryId,
                                title: title,
                                width: width,
                                height: height,
                                showMaxButton: true,
                                showModal: true,
                                allowResize: true,
                                ondestroy: function () {
                                    if (applyListGrid) {
                                        applyListGrid.reload();
                                    }
                                }
                            });
                        }

                    } else {
                        //2-运行中的申请
                        mini.alert('下载正在审批，请审批结束后查看');
                        return;
                    }
                }
            }
        });
    }

}


//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId, instId) {
    var title = "编辑下载申请";
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    var applyCategoryId = "download";
    _OpenWindow({
        url: jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&standardApplyCategoryId=" + applyCategoryId,
        title: title,
        width: width,
        height: height,
        showMaxButton: true,
        showModal: true,
        allowResize: true,
        ondestroy: function (action) {
            if (applyListGrid) {
                applyListGrid.reload();
            }
        }
    });
}

function downLoadJsjlFile(fileName, fileId, jsjlId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/njjd/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "njjdId");
    inputJsjlId.attr("value", jsjlId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputJsjlId);
    form.append(inputFileId);
    form.submit();
    form.remove();

}
function detailXzpzProcess() {
    mini.get("addNjjdxzpz").setEnabled(false);
    mini.get("removeNjjdxzpz").setEnabled(false);
    grid_njjd_xzpzInfo.setAllowCellEdit(false);
    $("#xzpzButtons").hide();
}
function addNjjd_xzpzInfoRow() {
    var row={};
    grid_njjd_xzpzInfo.addRow(row);
}
//移除
function removeNjjd_xzpzInfoRow() {
    var selecteds = grid_njjd_xzpzInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_njjd_xzpzInfo.removeRows(deleteArr);
}
function initForm() {
    $.ajaxSettings.async = false;
    if (njjdId){
        var url = jsUseCtxPath + "/zhgl/core/njjd/getXzpzList.do";
        $.post(
            url,
            {njjdId: njjdId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_njjd_xzpzInfo.setData(json);
                }
            });

    }
    $.ajaxSettings.async = true;
}
function exportCpgg() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
function draXzpz() {
    var checkMemberMust=checkMemberRequired(grid_njjd_xzpzInfo.getData(),false);
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
        if (!row.pzmc) {
            return {result: false, message: '请填写选装配置名称'};
        }
        if (!row.pzsjz) {
            return {result: false, message: '请填写选装配置设计值'};
        }
    }
    return {result: true};
}