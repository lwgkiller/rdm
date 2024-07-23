var isDarft = "";
var isPsg = "";
var isFbg = "";
var isYy = "";
var isZqyj = "";
var isFb = "";
var isRwxf = "";
var isLx = "";
var isDjr = "";
var isBzsc = "";
var isSsqr = "";
var isBz = "";
var isRzs = "";
var isFgld = "";
var isCash = "";
// 任务分配
var isRwfp = "";
//立项审查
var isLxsc = "";
// 模块负责人意见
var isMkfzr = "";
//标准化负责人审核
var isBzfzrsh = "";
//技术标准委员会复议
var isSffy = "";
//复议结果 标准化负责人审核复议
var isFyjg = "";
// 团队组建
var isTdzj = "";
// 复议审核
var isFysh = "";
//团队草案
var isTeamDraft = "";

$(function () {
    initForm();
    if (action == 'detail') {
        standardDemandApply.setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        if (formStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        detailSsyjProcess();
        if (isBzrzs) {
            mini.get("standardId").setEnabled(true);
            mini.get("saveTemp").show();
        }

    } else if (action == 'task') {
        bpmPreTaskTipInForm();
        var nodeVarsObj = getProcessNodeVars();
        var nodeVars = $.parseJSON(nodeVarsStr);
        mini.get("standardId").setEnabled(false);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'isDarft') {
                isDarft = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isPsg') {
                isPsg = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFbg') {
                isFbg = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isYy') {
                isYy = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isZqyj') {
                isZqyj = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFb') {
                isFb = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isRwxf') {
                isRwxf = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isLx') {
                isLx = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isDjr') {
                isDjr = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isBzsc') {
                isBzsc = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isSsqr') {
                isSsqr = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isBz') {
                isBz = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isRzs') {
                isRzs = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFgld') {
                isFgld = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isCash') {
                isCash = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isRwfp') {
                isRwfp = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isLxsc') {
                isLxsc = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isMkfzr') {
                isMkfzr = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isBzfzrsh') {
                isBzfzrsh = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isSffy') {
                isSffy = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFyjg') {
                isFyjg = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isTdzj') {
                isTdzj = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFysh') {
                isFysh = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isTeamDraft') {
                isTeamDraft = nodeVars[i].DEF_VAL_;
            }
        }
        if (isYy == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            detailSsyjProcess();
        } else if (isFbg == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("sffsgys").setEnabled(true);
            detailSsyjProcess();
        } else if (isPsg == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("bzscIds").setEnabled(true);
            mini.get("ssqrIds").setEnabled(true);
            $("#bzscSync").attr("style", "color:#44cef6;cursor: pointer;visibility:visible;");
            // mini.get("psjl").setEnabled(true);
            detailSsyjProcess();
        } else if (isRwfp == 'yes') {
            standardDemandApply.setEnabled(false);
            // mh 对接人
            mini.get("standardPeopleId").setEnabled(true);
            mini.get("modelPeopleId").setEnabled(true);
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            detailSsyjProcess();
        } else if (isLxsc == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(true);
            mini.get("operateAdd").setEnabled(false);
            mini.get("bzhlxyj").setEnabled(true);
            // 标准对接人要填立项意见
            detailSsyjProcess();
        } else if (isMkfzr == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            detailSsyjProcess();
            //模块负责人意见子表要填信息
            mini.get("teamDraftToolBar").show();
            teamDraftGrid.setAllowCellEdit(true);
            //@mh 这里表单如果加参数 autoload 加载不到节点变量，先在这边用流程变量顶一下 如果后续找到解决方案可以删除
            url = jsUseCtxPath + "/standardManager/core/standardManagement/getTeamDraftList.do?applyId=" + applyId + "&isMkfzr=" + isMkfzr;
            teamDraftGrid.setUrl(url);
            teamDraftGrid.load();

        } else if (isBzfzrsh == 'yes') {
            // 标准化负责人审核，填是否复议
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("sffy").setEnabled(true);
            detailSsyjProcess();
        } else if (isSffy == 'yes') {
            // 复议需要上传附件
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(true);
            mini.get("operateAdd").setEnabled(false);
            detailSsyjProcess();
        } else if (isFysh == 'yes') {
            // 需要填复议结果
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("fyjg").setEnabled(true);
            detailSsyjProcess();
        } else if (isTdzj == 'yes') {
            // 团队组建，选团队成员
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            //选成员
            mini.get("mainDraftIds").setEnabled(true);
            //加文件
            mini.get("addFile").setEnabled(true);
            //引用标准
            mini.get("referStandardIds").setEnabled(true);

            detailSsyjProcess();
        } else if (isZqyj == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
            if (isTeamDraft == 'yes') {
                // 团队草案，权限跟征求意见一样，该阶段也是征求意见阶段，顺序不可变
                // 可以添加文件
                mini.get("addFile").setEnabled(true);
            }
        } else if (isDarft == 'yes') {
            mini.get("addFile").setEnabled(true);
            standardDemandApply.setEnabled(false);
            mini.get("referStandardIds").setEnabled(true);
            mini.get("operateAdd").setEnabled(false);
            mini.get("yjUserIds").setEnabled(true);
            detailSsyjProcess();
        } else if (isFb == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
        }
        // @mh 这步流程已经删除了
        else if (isRwxf == 'yes') {
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            standardDemandApply.setEnabled(false);
            mini.get("feedbackId").setEnabled(true);
            mini.get("standardLeaderId").setEnabled(true);
        } else if (isLx == 'yes') {
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("standardId").setEnabled(false);
            mini.get("referStandardIds").setEnabled(false);
            wcTiemDisplay();
            scssUser();
            mini.get("yjUserIds").setEnabled(false);
            // 主要起草人
            mini.get("mainDraftIds").setEnabled(false);
            //标准化立项意见
            mini.get("bzhlxyj").setEnabled(false);
            detailSsyjProcess();

            //2023年2月10日10:50:15 需要上传附件
            mini.get("addFile").setEnabled(true);


        } else if (isDjr == 'yes' && isCash != 'yes') {
            mini.get("addFile").setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            standardDemandApply.setEnabled(false);
            detailSsyjProcess();
        } else if (isBzsc == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
        } else if (isSsqr == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
        } else if (isBz == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
        } else if (isRzs == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
        } else if (isFgld == 'yes') {
            standardDemandApply.setEnabled(false);
            mini.get("operateAdd").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            detailSsyjProcess();
        } else if (isDjr == 'yes' && isCash == 'yes') {
            mini.get("operateAdd").setEnabled(false);
            standardDemandApply.setEnabled(false);
            detailSsyjProcess();
        }
        else {
            mini.get("addFile").setEnabled(false);
        }
    } else if (action == 'edit') {


        // 原编辑权限
        // mini.get("addFile").setEnabled(false);
        // mini.get("operateAdd").setEnabled(false);
        // standardDemandApply.setEnabled(false);
        // mini.get("feedbackId").setEnabled(true);
        // mini.get("standardLeaderId").setEnabled(true);
        // mini.get("rwms").setEnabled(true);
        // detailSsyjProcess();
        // 新编辑权限
        mini.get("addFile").setEnabled(false);
        mini.get("operateAdd").setEnabled(false);
        mini.get("standardId").setEnabled(false);
        // mini.get("standardLeaderId").setEnabled(false);
        mini.get("referStandardIds").setEnabled(false);
        // mini.get("rwms").setEnabled(false);
        wcTiemDisplay();
        scssUser();
        mini.get("yjUserIds").setEnabled(false);
        // 主要起草人
        mini.get("mainDraftIds").setEnabled(false);
        //标准化立项意见
        mini.get("bzhlxyj").setEnabled(false);
        detailSsyjProcess();

        //2023年2月10日10:50:15 需要上传附件
        mini.get("addFile").setEnabled(true);



        // mini.get("referStandardIds").setEnabled(true);
    }


    if (standardDemandObj) {
        standardDemandApply.setData(standardDemandObj);
        if (standardDemandObj.feedbackType == 'problem') {
            $("#standardTd1").show();
            $("#standardTd2").show();
        }
        /*        if (action == "detail" &&finalStandardNumber) {
                    // mini.get("lastStandardInfo").setValue(finalStandardNumber+"("+finalStandardName+")");
                    document.getElementById("lastStandardInfo").innerText=finalStandardNumber+"("+finalStandardName+")" ;
                }*/
    }
    typeChange2();
    lyChange();
});


function returnJsmmPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + jsStandardManagementEdit_name7 + ' style="color: silver" >' + jsStandardManagementEdit_name7 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/standardManager/core/standardManagement/standardPdfPreview.do';
        s = '<span  title=' + jsStandardManagementEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + jsStandardManagementEdit_name7 + '</span>';
    } else if (fileType == 'office') {
        var url = '/standardManager/core/standardManagement/standardOfficePreview.do';
        s = '<span  title=' + jsStandardManagementEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + jsStandardManagementEdit_name7 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/standardManager/core/standardManagement/standardImagePreview.do';
        s = '<span  title=' + jsStandardManagementEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + jsStandardManagementEdit_name7 + '</span>';
    }
    return s;
}

//下载文档
function downLoadJsjlFile(fileName, fileId, jsjlId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/standardManager/core/standardManagement/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "applyId");
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


//草案时间校验
function buildCnBlur() {
    //草案完成时间校验
    var createtime = new Date(),
        oYear = createtime.getFullYear(),
        oMonth = createtime.getMonth() + 1,
        oDay = createtime.getDate(),
        createtime = oYear + '-' + addZero(oMonth) + '-' + addZero(oDay);

    var draftTime = $.trim(mini.get("draftTime").getValue())
    if (!draftTime) {
        // mini.alert("请选择草案完成时间");
        return;
    }
    if (timeVerify(createtime, draftTime, 2)) {
        $.trim(mini.get("draftTime").setValue(""))
        mini.alert(jsStandardManagementEdit_name8);
    }
}

//征求意见校验
function buildZqyjBlur() {
    var draftTime = $.trim(mini.get("draftTime").getValue())

    var solicitopinionsTime = $.trim(mini.get("solicitopinionsTime").getValue())
    if (!solicitopinionsTime) {
        return;
    }
    //征求意见完成日期校验
    if (timeVerify(draftTime, solicitopinionsTime, 1)) {
        $.trim(mini.get("solicitopinionsTime").setValue(""))
        mini.alert(jsStandardManagementEdit_name9);
    }
}

function buildZqyjFocus() {
    var draftTime = $.trim(mini.get("draftTime").getValue())
    if (!draftTime) {
        mini.get("draftTime").setValue(jsStandardManagementEdit_name10);
        mini.get("draftTime").blur();
    }
}

//评审稿校验
function buildPsgBlur() {

    var solicitopinionsTime = $.trim(mini.get("solicitopinionsTime").getValue())

    var reviewTime = $.trim(mini.get("reviewTime").getValue())
    if (!reviewTime) {
        return;
    }
    //评审稿完成日期校验
    if (timeVerify(solicitopinionsTime, reviewTime, 1)) {
        $.trim(mini.get("reviewTime").setValue(""));
        mini.alert(jsStandardManagementEdit_name11);
    }
}

function buildPsgFocus() {
    var solicitopinionsTime = $.trim(mini.get("solicitopinionsTime").getValue())
    if (!solicitopinionsTime) {
        mini.alert(jsStandardManagementEdit_name12);
    }
}

//报批校验
function buildPbBlur() {

    var reviewTime = $.trim(mini.get("reviewTime").getValue())

    var reportforapprovalTime = $.trim(mini.get("reportforapprovalTime").getValue())
    if (!reportforapprovalTime) {
        return;
    }
    //报批完成日期校验
    if (timeVerify(reviewTime, reportforapprovalTime, 1)) {
        $.trim(mini.get("reportforapprovalTime").setValue(""));
        mini.alert(jsStandardManagementEdit_name13);
    }
}

function buildPbFocus() {
    var reviewTime = $.trim(mini.get("reviewTime").getValue())
    if (!reviewTime) {
        mini.alert(jsStandardManagementEdit_name14);
    }
}

//应用校验
function buildYyBlur() {

    var reportforapprovalTime = $.trim(mini.get("reportforapprovalTime").getValue())

    var applicationTime = $.trim(mini.get("applicationTime").getValue())
    if (!applicationTime) {
        return;
    }
    //应用完成日期校验
    if (timeVerify(reportforapprovalTime, applicationTime, 1)) {
        $.trim(mini.get("applicationTime").setValue(""));
        mini.alert(jsStandardManagementEdit_name15);
    }
}

function buildYyFocus() {
    var reportforapprovalTime = $.trim(mini.get("reportforapprovalTime").getValue())
    if (!reportforapprovalTime) {
        mini.alert(jsStandardManagementEdit_name16);
    }
}

//保存草稿
function saveStandardDemand(e) {

    // 保存草稿 需要有牵头人
    var standardLeaderId = $.trim(mini.get("standardLeaderId").getValue())
    if (!standardLeaderId) {
        mini.alert(jsStandardManagementEdit_name17);
        return;
    }
    /*   var feedbackId = $.trim(mini.get("feedbackId").getValue())
       if (!feedbackId) {
           mini.alert("请选择关联标准反馈");
           return;
       }*/

    window.parent.saveDraft(e);
}

//流程中暂存信息（如编制阶段）
function saveStandardInProcess() {
    var formData = _GetFormJsonMini("standardDemandApply");
    if (formData.fileListGrid) {
        delete formData.fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardManagement/saveStandard.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = jsStandardManagementEdit_name18;
                } else {
                    message = jsStandardManagementEdit_name19 + data.message;
                }

                mini.alert(message, jsStandardManagementEdit_name20, function () {
                    window.location.reload();
                });
            }
        }
    });
}

//自动废止修订标准，打开标准新增页面
function standardIssue() {
    var standardId = mini.get("standardId").getValue();
    if (standardId) {
        mini.alert(jsStandardManagementEdit_name21 + '“' + mini.get("finalStandardName").getValue() + '”');
        return;
    }
    var type = mini.get("rwlb").getValue();
    if (type == "修订") {
        mini.open({
            title: jsStandardManagementEdit_name22,
            url: jsUseCtxPath + "/standardManager/core/standard/edit.do?" +
                "standardTaskId=" + mini.get("id").getValue() + "&action=add&systemCategoryId=JS" +
                "&standardId=" + mini.get("oldStandardId").getValue() + "&type=" + type,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var data = {isPointManager: true, tabName: 'JS', currentUserSubManager: {}};  //传递上传参数
                iframe.contentWindow.setData(data);
            },
            ondestroy: function (action) {
                window.location.reload();
            }
        });
    } else {
        //查询技术体系的体系树
        /*    var systemTreeData=[];
            $.ajaxSettings.async = false;
            $(function () {
                $.ajax({
                    url: jsUseCtxPath + '/standardManager/core/standardSystem/treeQuery.do?systemCategoryId=JS',
                    success: function (data) {
                        if (data) {
                            systemTreeData = data;
                        }
                    }
                });
            });
            $.ajaxSettings.async = true;*/
        mini.open({
            title: jsStandardManagementEdit_name22,
            url: jsUseCtxPath + "/standardManager/core/standard/edit.do?" + "standardTaskId=" + mini.get("id").getValue() + "&action=add&systemCategoryId=JS",
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var data = {isPointManager: true, tabName: 'JS', currentUserSubManager: {}};  //传递上传参数
                iframe.contentWindow.setData(data);
            },
            ondestroy: function (action) {
                window.location.reload();
            }
        });
    }

}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("standardDemandApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.changeData = grid_ssyjInfo.getChanges();
    formData.changeDemandGrid = teamDraftGrid.getChanges();
    formData.bos = [];
    formData.vars = [{key: 'standardLeaderName', val: formData.standardLeaderName}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var s = "";

    var rwly = $.trim(mini.get("rwly").getValue())
    if (!rwly) {
        s = s + "<br>请选择任务来源";
    }

    // todo mh 未做关联之前先不必填
    // if (rwly == "技术项目") {
    //     var jsxmId = $.trim(mini.get("jsxmId").getValue())
    //     if (!jsxmId) {
    //         s = s + "<br>请填写关联技术项目";
    //     }
    // }else if (rwly == "标准信息反馈") {
    //     var feedbackId = $.trim(mini.get("feedbackId").getValue())
    //     if (!feedbackId) {
    //         s = s + "<br>请填写关联标准信息反馈";
    //     }
    //
    // }else if (rwly == "技术标准自查") {
    //     var jsbzzcId = $.trim(mini.get("jsbzzcId").getValue())
    //     if (!jsbzzcId) {
    //         s = s + "<br>请填写关联标准标准自查";
    //     }
    // }else if (rwly == "其他") {
    //     var otherId = $.trim(mini.get("otherId").getValue())
    //     if (!otherId) {
    //         s = s + "<br>请填写关联其他";
    //     }
    // }


    var standardLeaderId = $.trim(mini.get("standardLeaderId").getValue());
    if (!standardLeaderId) {
        s = s + "<br>请选择标准牵头人";
    }
    // var rwms = $.trim(mini.get("rwms").getValue())
    // if (!rwms) {
    //     s = s + "<br>请填写任务描述";
    // }

    var rwlb = mini.get("rwlb").getValue();
    if (!rwlb) {
        s = s + "<br>请选择任务类别";
    }
    if (rwlb == '新制定') {
        var standardName = $.trim(mini.get("standardName").getValue())
        if (!standardName) {
            s = s + "<br>请填写标准名称";
        }
    } else {
        var oldStandardId = $.trim(mini.get("oldStandardId").getValue())
        if (!oldStandardId) {
            s = s + "<br>请填写标准名称";
        }
    }

    var bzmd = $.trim(mini.get("bzmd").getValue())
    if (!bzmd) {
        s = s + "<br>请填写标准目的";
    }
    var bzfw = $.trim(mini.get("bzfw").getValue())
    if (!bzfw) {
        s = s + "<br>请填写标准范围";
    }
    var bzxz = $.trim(mini.get("bzxz").getValue())
    if (!bzxz) {
        s = s + "<br>请填写标准现状";
    }
    var scly = $.trim(mini.get("scly").getValue())
    if (!scly) {
        s = s + "<br>请填写素材来源";
    }
    var clsj = $.trim(mini.get("clsj").getValue())
    if (!clsj) {
        s = s + "<br>请填写材料收集";
    }


    /*var standardLeaderId = $.trim(mini.get("standardLeaderId").getValue())
    if (!standardLeaderId) {
        return {"result": false, "message": "请选择标准牵头人"};
    }*/

    // mh
    // var mainDraftIds = $.trim(mini.get("mainDraftIds").getValue())
    // if (!mainDraftIds) {
    //     s=s+"<br>请选择主要起草人";
    // }

    //立项时间
    var createtime = new Date(),
        oYear = createtime.getFullYear(),
        oMonth = createtime.getMonth() + 1,
        oDay = createtime.getDate(),
        createtime = oYear + '-' + addZero(oMonth) + '-' + addZero(oDay);

    var draftTime = $.trim(mini.get("draftTime").getValue())
    if (!draftTime) {
        s = s + "<br>请在计划信息中选择草案完成时间";
    }
    //草案完成时间校验
    if (timeVerify(createtime, draftTime, 2)) {
        s = s + "<br>草案完成时间不得晚于立项时间两个月";
    }
    var solicitopinionsTime = $.trim(mini.get("solicitopinionsTime").getValue())
    if (!solicitopinionsTime) {
        s = s + "<br>请在计划信息中选择征求意见完成日期";
    }
    //征求意见完成日期校验
    if (timeVerify(draftTime, solicitopinionsTime, 1)) {
        s = s + "<br>征求意见完成日期不得晚于草案完成时间一个月";
    }
    var reviewTime = $.trim(mini.get("reviewTime").getValue())
    if (!reviewTime) {
        s = s + "<br>请在计划信息中选择评审稿完成日期";
    }
    //评审稿完成日期校验
    if (timeVerify(solicitopinionsTime, reviewTime, 1)) {
        s = s + "<br>评审稿完成日期不得晚于征求意见完成日期一个月";
    }
    var reportforapprovalTime = $.trim(mini.get("reportforapprovalTime").getValue())
    if (!reportforapprovalTime) {
        s = s + "<br>请在计划信息中选择报批完成日期";
    }
    //报批完成日期校验
    if (timeVerify(reviewTime, reportforapprovalTime, 1)) {
        s = s + "<br>报批完成日期不得晚于评审稿完成日期一个月也不得低于评审稿完成日期";
    }
    var applicationTime = $.trim(mini.get("applicationTime").getValue())
    if (!applicationTime) {
        s = s + "<br>请在计划信息中选择应用完成日期";
    }
    //应用完成日期校验
    if (timeVerify(reportforapprovalTime, applicationTime, 1)) {
        s = s + "<br>应用完成时间不得晚于征求意见完成日期一个月";
    }

    //@mh 需要上传附件
    //
    if (!applyId) {
        s = s + "<br>请上传附件";
    } else {
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=start",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });
        if (result == "false") {
            s = s + "<br>" + message;
        }
    }




    //@mh 标准对接人第一步不需要填
    // var standardPeopleId = $.trim(mini.get("standardPeopleId").getValue())
    // if (!standardPeopleId) {
    //     s=s+"<br>请选择标准对接人";
    // } else {
    //     //判断标准对接人是否是技术标准管理人员
    //     var result = "";
    //     $.ajax({
    //         type: 'get',
    //         async: false,
    //         contentType: 'application/json',
    //         success: function (data) {
    //             result = data.result;
    //         }
    //     });
    //     if (result == "false") {
    //         s=s+"<br>请选择标准对接人是技术标准管理人员";
    //         // mini.alert("请选择标准对接人是术中心项目管理人员");
    //     }
    // }

    /*var feedbackId = $.trim(mini.get("feedbackId").getValue())
    if (!feedbackId) {
        return {"result": false, "message": "请选择关联标准反馈"};
    }*/
    /*var referStandardIds = $.trim(mini.get("referStandardIds").getValue())
    if (!referStandardIds) {
        return {"result": false, "message": "请选择引用标准"};
    }*/
    /*var yjUserIds = $.trim(mini.get("yjUserIds").getValue())
    if (!yjUserIds) {
        return {"result": false, "message": "请选择征求意见人员"};
    }*/
    // var standardAndApply = $.trim(mini.get("standardAndApply").getValue())
    // if (!standardAndApply) {
    //     s = s + "<br>请填写标准的规范事项&适用范围";
    // }
    // if (standardAndApply.length > 500) {
    //     s = s + "<br>标准的规范事项&适用范围超过字数限制";
    // }
    if (s != "") {
        return {"result": false, "message": s};
    }
    return {"result": true};
}

/**
 * 日期校验
 */
function timeVerify(begin, end, num) {

    //校验草案完成时间
    var beginTime = new Date(addTime(getMyDate(begin), num)).getTime();
    var endTime = new Date(getMyDate(end)).getTime();

    if (beginTime < endTime) {
        return true;
    } else {
        return false;
    }

}

function getMyDate(str) {
    if (str == null) {
        return '';
    }
    var oDate = new Date(str),
        oYear = oDate.getFullYear(),
        oMonth = oDate.getMonth() + 1,
        oDay = oDate.getDate(),
        oHour = oDate.getHours(),
        oMin = oDate.getMinutes(),
        oSen = oDate.getSeconds(),
        oTime = oYear + '-' + addZero(oMonth) + '-' + addZero(oDay);
    return oTime;
}

function addZero(num) {
    if (parseInt(num) < 10) {
        num = '0' + num;
    }
    return num;
}

function addTime(str, num) {
    var oDate = new Date(str),
        oYear = oDate.getFullYear(),
        oMonth = oDate.getMonth() + 1 + num,
        oDay = oDate.getDate(),
        oHour = oDate.getHours(),
        oMin = oDate.getMinutes(),
        oSen = oDate.getSeconds(),
        oTime = oYear + '-' + addZero(oMonth) + '-' + addZero(oDay);
    return oTime;
}

//下一步时数据是否有效
function approveValid() {
    var nodeVarsObj = getProcessNodeVars();
    if (nodeVarsObj.nodeTask == '编制') {
        return draftOrStartValid();
    } else if (nodeVarsObj.nodeTask == '反馈') {
        var standardUserOpinion = $.trim(mini.get("standardUserOpinion").getValue());
        if (!standardUserOpinion || standardUserOpinion.length > 800) {
            return {"result": false, "message": jsStandardManagementEdit_name23};
        }
    }

    return {"result": true};
}

//启动流程
function startStandardDemandProcess(e) {
    var s = "";
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    // 提交需要验证
    // var standardLeaderId = $.trim(mini.get("standardLeaderId").getValue())
    // if (!standardLeaderId) {
    //     s=s+"<br>请选择标准牵头人";
    // }
    // var rwms = $.trim(mini.get("rwms").getValue())
    // if (!rwms) {
    //     s=s+"<br>请填写任务描述";
    // }
    // if(s!=""){
    //     mini.alert(s);
    //     return;
    // }
    window.parent.startProcess(e);
}

//审批或者下一步
function standardDemandApprove() {
    var s = "";
    if (isRwxf == "yes") {
        var standardLeaderId = $.trim(mini.get("standardLeaderId").getValue())
        if (!standardLeaderId) {
            s = s + "请选择标准牵头人";
        }
        // var rwms = $.trim(mini.get("rwms").getValue())
        // if (!rwms) {
        //     s = s + "<br>请填写任务描述";
        // }
    } else if (isLx == "yes") {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    } else if (isRwfp == "yes") {

        var standardPeopleId = $.trim(mini.get("standardPeopleId").getValue())
        if (!standardPeopleId) {
            s=s+"<br>请选择标准对接人";
        } else {
            //判断标准对接人是否是技术标准管理人员
            var result = "";
            $.ajax({
                type: 'get',
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    result = data.result;
                }
            });
            if (result == "false") {
                s=s+"<br>请选择标准对接人是技术标准管理人员";
                // mini.alert("请选择标准对接人是术中心项目管理人员");
            }
        }


        var modelPeopleId = $.trim(mini.get("modelPeopleId").getValue())
        if (!modelPeopleId) {
            s = s + "<br>请选择模块负责人";
        }
    }else if (isLxsc == "yes") {
        var bzhlxyj = $.trim(mini.get("bzhlxyj").getValue())
        if (!bzhlxyj) {
            s = s + "<br>请填写标准化立项意见";
        }

    } else if (isBzfzrsh == "yes") {
        var sffy = $.trim(mini.get("sffy").getValue())
        if (!sffy) {
            s = s + "<br>请选择是否需要复议";
        }
    } else if (isTdzj == "yes") {
        var mainDraftIds = $.trim(mini.get("mainDraftIds").getValue())
        if (!mainDraftIds) {
            s = s + "<br>请选择团队成员(主要起草人)";
        }
        var referStandardIds = $.trim(mini.get("referStandardIds").getValue())
        if (!referStandardIds) {
            s = s + "<br>请选择引用标准";

        }
        // 团队组建也是校验的立项草案，但立项草案已经校验过了没必要再校验

        // var result = "";
        // var message = "";
        // $.ajax({
        //     url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=start",
        //     type: 'get',
        //     async: false,
        //     contentType: 'application/json',
        //     success: function (data) {
        //         result = data.result;
        //         message = data.message;
        //     }
        // });
        // if (result == "false") {
        //     s = s + "<br>" + message;
        // }


    } else if (isFysh == "yes") {
        var fyjg = $.trim(mini.get("fyjg").getValue())
        if (!fyjg) {
            s = s + "<br>请选择复议是否通过";
        }
    } else if (isSffy == "yes") {
        // var fyjg = $.trim(mini.get("fyjg").getValue())
        // if (!fyjg) {
        //     s=s+"<br>请选择是否需要复议";
        // }
        //校验复议附件
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=fy",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });
        if (result == "false") {
            s = s + "<br>" + message;
        }
    } else if (isMkfzr == "yes") {
        var datas = teamDraftGrid.getData();
        if (datas.length <= 0) {
            s = s + "<br>请添加模块负责人意见";
        }
        for (var i = 0, l = datas.length; i < l; i++) {
            if (datas[i].isAgree == undefined||datas[i].isAgree == "" ) {
                s = s + "<br>请选择是否同意立项";
            }
            if (datas[i].opinion == undefined||datas[i].opinion == "" ) {
                s = s + "<br>填写详细意见";
            }
        }
    }
    else if (isDarft == "yes") {
        var referStandardIds = $.trim(mini.get("referStandardIds").getValue())
        if (!referStandardIds) {
            s = s + "<br>请选择引用标准";

        }
        var yjUserIds = $.trim(mini.get("yjUserIds").getValue())
        if (!yjUserIds) {
            s = s + "<br>请选择征求意见人员";
        }
        //标准草案必须提交 应用标准必须提交
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=darft",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });
        if (result == "false") {
            s = s + "<br>" + message;
        }
    } else if (isFb == "yes") {
        var standardId = $.trim(mini.get("standardId").getValue());
        if (standardId == "") {
            mini.alert(jsStandardManagementEdit_name24);
            return;

        }
    } else if (isPsg == "yes") {
        var bzscIds = $.trim(mini.get("bzscIds").getValue())
        if (!bzscIds) {
            s = s + "<br>请选择标准审查人员";
        }
        var ssqrIds = $.trim(mini.get("ssqrIds").getValue())
        if (!ssqrIds) {
            s = s + "<br>请选择实施确认人员";
        }
        var datas = messageListGrid.getData();
        if (datas.length > 0) {
            for (var i = 0, l = datas.length; i < l; i++) {
                if (datas[i].ifyj == 'yes' && !datas[i].feedback) {
                    s = s + "<br>请为意见" + datas[i].opinion + "添加意见反馈";
                }
            }
        }
        /*var psjl = $.trim(mini.get("psjl").getValue())
        if (!psjl) {
            mini.alert("请填写评审记录");
            return;
        }*/
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=psg",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });

        if (result == "false") {
            s = s + "<br>" + message;
        }
    } else if (isFbg == "yes") {
        var sffsgys = $.trim(mini.get("sffsgys").getValue())
        if (!sffsgys) {
            s = s + "<br>请选择是否发送供应商";
        }
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=fbg",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });

        if (result == "false") {
            mini.alert(message);
            return;
        }
    } else if (isYy == "yes") {
        var result = "";
        var message = "";
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/standardApply/getIsBzcn.do?applyId=' + applyId + "&stageKey=yy",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });

        if (result == "false") {
            mini.alert(message);
            return;
        }
    } else if (isSsqr == "yes") {
        if (grid_ssyjInfo.getChanges().length == 0) {
            s = s + "<br>请添加实施确认意见";
        }
        if (grid_ssyjInfo.getChanges().length > 0) {
            var formValid = draSsyj();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
    } else if (isZqyj == "yes") {
        var datas = messageListGrid.getData();
        if (datas.length <= 0) {
            s = s + "<br>请添加征求意见";
        }
    } else if (isCash == "yes") {
        var isupload = false;
        var fileList = fileListGrid.getData();
        for (var i = 0; i < fileList.length; i++) {
            if (fileList[i].typename == "标准征求意见稿") {
                isupload = true;
            }
        }
        if (!isupload) {
            mini.alert(jsStandardManagementEdit_name25);
            return;
        }
    }
    if (s != "") {
        mini.alert(s);
        return;
    }
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: jsStandardManagementEdit_name26,
        width: 800,
        height: 600
    });
}


/**
 * 关联标准反馈弹窗
 */
function selectStandardFeedback() {
    standardFeedbackWindow.show();
    searchStandardFeedback();
}

/**
 * 引用标准弹窗
 */
function selectQuoteStandard() {
    quoteStandardWindow.show();
    searchQuoteStandard();
}

function selectQuoteStandardOld(scene) {
    quoteStandardWindowOld.show();
    mini.get("scene").setValue(scene);
    searchQuoteStandardOld();
}


/**
 * 引用标准查询
 */
function searchQuoteStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterStandardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("filterStandardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    queryParam.push({name: "systemCategoryId", value: 'JS'});
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = quoteStandardGrid.getPageIndex();
    data.pageSize = quoteStandardGrid.getPageSize();
    data.sortField = quoteStandardGrid.getSortField();
    data.sortOrder = quoteStandardGrid.getSortOrder();
    //查询
    quoteStandardGrid.load(data);
}

function searchQuoteStandardOld() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterStandardNumberOld").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("filterStandardNameOld").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    queryParam.push({name: "systemCategoryId", value: 'JS'});
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = quoteStandardGridOld.getPageIndex();
    data.pageSize = quoteStandardGridOld.getPageSize();
    data.sortField = quoteStandardGridOld.getSortField();
    data.sortOrder = quoteStandardGridOld.getSortOrder();
    //查询
    quoteStandardGridOld.load(data);
}

/**
 * 查询反馈标准
 */
function searchStandardFeedback() {
    var queryParam = [];
    //其他筛选条件
    var id = $.trim(mini.get("applyId").getValue());
    if (id) {
        queryParam.push({name: "applyId", value: id});
    }
    queryParam.push({name: "instStatus", value: "SUCCESS_END"});

    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardFeedbackGrid.getPageIndex();
    data.pageSize = standardFeedbackGrid.getPageSize();
    data.sortField = standardFeedbackGrid.getSortField();
    data.sortOrder = standardFeedbackGrid.getSortOrder();
    //查询
    standardFeedbackGrid.load(data);
}


/**
 * 查询引用标准
 */
function openRowDblClick() {
    quoteStandardOK();
}

function openRowDblClickold() {
    quoteStandardOKold();
}

/**
 * 查询标准反馈
 */
function onRowDblClicks() {
    okWindow();
}

/**
 * 引用标准弹窗确定按钮
 */
function quoteStandardOK() {
    var referStandardIdsValue = $.trim(mini.get("referStandardIds").getValue());
    var referStandardIdsText = $.trim(mini.get("referStandardIds").getText());
    var rows = [];
    rows = quoteStandardGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(jsStandardManagementEdit_name27);
        return;
    }
    var referStandardIds = "";
    var referStandardNames = "";

    for (var i = 0, l = rows.length; i < l; i++) {
        var r = rows[i];
        if (i != (rows.length - 1)) {
            referStandardIds += r.id + ",";
            referStandardNames += "(" + r.standardNumber + ")" + r.standardName + "；";
        } else {
            referStandardIds += r.id;
            referStandardNames += "(" + r.standardNumber + ")" + r.standardName;
        }
    }
    if (referStandardIdsValue) {
        mini.get("referStandardIds").setText(referStandardIdsText + "；" + referStandardNames);
        mini.get("referStandardIds").setValue(referStandardIdsValue + "；" + referStandardIds);
    } else {
        mini.get("referStandardIds").setText(referStandardNames);
        mini.get("referStandardIds").setValue(referStandardIds);
    }
    hidesWindow();
}

function quoteStandardOKold() {
    var rows = [];
    rows = quoteStandardGridOld.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(jsStandardManagementEdit_name27);
        return;
    }
    var referStandardIds = "";
    var referStandardNames = "";

    for (var i = 0, l = rows.length; i < l; i++) {
        var r = rows[i];
        if (i != (rows.length - 1)) {
            referStandardIds += r.id + ",";
            referStandardNames += "(" + r.standardNumber + ")" + r.standardName + "；";
        } else {
            referStandardIds += r.id;
            referStandardNames += "(" + r.standardNumber + ")" + r.standardName;
        }
    }
    var scene = mini.get("scene").getValue();
    if (scene == 'old') {
        mini.get("oldStandardId").setText(referStandardNames);
        mini.get("oldStandardId").setValue(referStandardIds);
    } else if (scene == 'final') {
        mini.get("standardId").setText(referStandardNames);
        mini.get("standardId").setValue(referStandardIds);
    }

    hidesWindowOld();
}


/**
 * 引用标准弹窗取消按钮
 */
function quoteStandardHide() {
    quoteStandardWindow.hide();
    mini.get("filterStandardNumber").setValue('');
    mini.get("filterStandardName").setValue('');
}

function quoteStandardHideOld() {
    quoteStandardWindowOld.hide();
    mini.get("filterStandardNumberOld").setValue('');
    mini.get("filterStandardNameOld").setValue('');
}

/**
 * 标准反馈确定按钮
 */
function okWindow() {
    var selectRow = standardFeedbackGrid.getSelected();
    if (selectRow) {
        mini.get("feedbackId").setValue(selectRow.applyId);

        mini.get("feedbackId").setText(selectRow.applyId);
    }
    hideWindow()
}

/**
 * 标准反馈关闭按钮
 */
function hideWindow() {
    standardFeedbackWindow.hide();
    mini.get("applyId").setValue('');
}

/**
 * 引用标准关闭按钮
 */
function hidesWindow() {
    quoteStandardWindow.hide();
    mini.get("filterStandardNumber").setValue('');
    mini.get("filterStandardName").setValue('');
}

function hidesWindowOld() {
    quoteStandardWindowOld.hide();
    mini.get("filterStandardNumberOld").setValue('');
    mini.get("filterStandardNameOld").setValue('');
}

/**
 * 引用标准清除按钮
 * @param inputScene
 */
function quoteStandardCloseClick(inputScene) {
    mini.get("referStandardIds").setValue('');
    mini.get("referStandardIds").setText('');
}

function quoteStandardCloseClickOld(inputScene) {
    if (inputScene == 'old') {
        mini.get("oldStandardId").setText('');
        mini.get("oldStandardId").setValue('');
    } else if (inputScene == 'final') {
        mini.get("standardId").setText('');
        mini.get("standardId").setValue('');
    }
}

function typeChange() {
    var feedbackType = mini.get("feedbackType").getValue();
    if (feedbackType == 'problem') {
        $("#standardTd1").show();
        $("#standardTd2").show();
    } else {
        $("#standardTd1").hide();
        $("#standardTd2").hide();
        mini.get("problemStandardId").setValue('');
        mini.get("problemStandardId").setText('');
    }
}


function addStandardFile(applyId) {

    var stageKey = "";
    if (!applyId) {
        mini.alert(jsStandardManagementEdit_name28);
        return;
    }
    //@mh 团队组建也需要上传草稿
    if (isDarft == 'yes' ) {
        stageKey = "darft"
    } else if (isPsg == 'yes') {
        stageKey = "psg"
    } else if (isFbg == 'yes') {
        stageKey = "fbg"
    } else if (isYy == 'yes') {
        stageKey = "yy"
    } else if (isDjr == 'yes' && isCash == 'yes') {
        stageKey = "cash"
    } else if (isSffy == 'yes') {
        stageKey = "fy"
    } else if (isTeamDraft == 'yes') {
        stageKey = "team"
    }else if (action == "edit" ||isLx =="yes") {
        stageKey = "start"
    }else if (isTdzj == "yes") {
        stageKey = "tdzj"
    }else if (isLxsc == "yes") {
        stageKey = "lxsc"
    }


    mini.open({
        title: jsStandardManagementEdit_name29,
        url: jsUseCtxPath + "/standardManager/core/standardManagement/fileUploadWindow.do?applyId=" + applyId + "&stageKey=" + stageKey,
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


//打开标准的编辑页面（新增、编辑、明细）
function openStandardEditWindow(standardId, systemId, action, applyId) {

    if (!applyId) {
        mini.alert(jsStandardManagementEdit_name28);
        return;
    }

    mini.open({
        title: jsStandardManagementEdit_name30,
        url: jsUseCtxPath + "/standardManager/core/standardManagement/editUser.do?standardId=" + standardId + '&action=' + action + '&applyId=' + applyId + '&isZqyj=' + isZqyj + '&isPsg=' + isPsg,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
            // var iframe = this.getIFrameEl();
            // var data = {};  //传递上传参数
            // iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {

            var data = {};

            //查询
            // standardListGrid.load(data);
            messageListGrid.load();
        }
    });
}

function add_ssyjInfoRow() {
    var row = {};
    grid_ssyjInfo.addRow(row);
}

//移除
function remove_ssyjInfoRow() {
    var selecteds = grid_ssyjInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_ssyjInfo.removeRows(deleteArr);
}


function addTeamDraftRow() {
    var row = {"creatorName": currentUserName};
    teamDraftGrid.addRow(row);
}

//移除
function removeTeamDraftRow() {
    var selecteds = teamDraftGrid.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    teamDraftGrid.removeRows(deleteArr);
}

function wcTiemDisplay() {
    mini.get("sjcaTime").setEnabled(false);
    mini.get("sjzqTime").setEnabled(false);
    mini.get("sjpsTime").setEnabled(false);
    mini.get("sjbpTime").setEnabled(false);
    mini.get("sjyyTime").setEnabled(false);
}

function scssUser() {
    mini.get("bzscIds").setEnabled(false);
    mini.get("ssqrIds").setEnabled(false);
    // mini.get("psjl").setEnabled(false);
}

function detailSsyjProcess() {
    mini.get("addSsyj").setEnabled(false);
    mini.get("removeSsyj").setEnabled(false);
    grid_ssyjInfo.setAllowCellEdit(false);
    // $("#ssyjButtons").hide();
}

function draSsyj() {
    var checkMemberMust = checkMemberRequired(grid_ssyjInfo.getData(), false);
    if (!checkMemberMust.result) {
        return {"result": false, "message": checkMemberMust.message};
    }
    return {"result": true};
}

function checkMemberRequired(rowData, checkAll) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: ''};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.opinion) {
            return {result: false, message: jsStandardManagementEdit_name31};
        }
    }
    return {result: true};
}

function initForm() {
    $.ajaxSettings.async = false;
    if (applyId) {
        var url = jsUseCtxPath + "/standardManager/core/standardManagement/getSsyjList.do";
        $.post(
            url,
            {applyId: applyId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_ssyjInfo.setData(json);
                }
            });

    }
    $.ajaxSettings.async = true;
}

function bztoggleFieldSet(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

function typeChange2() {
    var rwlb = mini.get("rwlb").getValue();
    if (rwlb == '新制定') {
        $("#s1").show();
        $("#s2").hide();
        mini.get("oldStandardId").setValue('');
        mini.get("oldStandardId").setText('');
    } else {
        $("#s2").show();
        $("#s1").hide();
    }
}

function lyChange() {
    var rwly = mini.get("rwly").getValue();
    if (rwly == '技术项目') {
        $("#ly1").show();
        $("#ly2").hide();
        mini.get("feedbackId").setValue("");
        mini.get("feedbackId").setText("");
        $("#ly3").hide();
        mini.get("jsbzzcId").setValue("");
        $("#ly4").hide();
        mini.get("otherId").setValue("");
    } else if (rwly == '标准信息反馈') {
        $("#ly1").hide();
        mini.get("jsxmId").setValue("");
        // mini.get("ly1Id").setText("");
        $("#ly2").show();
        $("#ly3").hide();
        mini.get("jsbzzcId").setValue("");
        $("#ly4").hide();
        mini.get("otherId").setValue("");
    }else if (rwly == '技术标准自查') {
        $("#ly1").hide();
        mini.get("jsxmId").setValue("");
        $("#ly2").hide();
        mini.get("feedbackId").setValue("");
        mini.get("feedbackId").setText("");
        $("#ly3").show();
        $("#ly4").hide();
        mini.get("otherId").setValue("");
    }else if (rwly == '其他') {
        $("#ly1").hide();
        mini.get("jsxmId").setValue("");
        $("#ly2").hide();
        mini.get("feedbackId").setValue("");
        mini.get("feedbackId").setText("");
        $("#ly3").hide();
        mini.get("jsbzzcId").setValue("");
        $("#ly4").show();
    }
}

// 标准动态跳转到详情
function jumpStandardInfo() {
    if (finalStandardNumber) {
        var url = jsUseCtxPath + "/standardManager/core/standard/management.do?" +
            "standardNumber=" + finalStandardNumber +
            "&standardName=" + finalStandardName +
            "&systemCategory=JS";
        var winObj = window.open(url);
    }

}

//标准审查人员同步征求意见人员
function syncyjUser() {
    var yjUserIds = mini.get("yjUserIds").getValue();
    var yjUserNames = mini.get("yjUserIds").getText();
    mini.get("bzscIds").setValue(yjUserIds);
    mini.get("bzscIds").setText(yjUserNames);
}

function OnCellBeginEdit(e) {
    var field = e.field;
    // 非申请编制阶段，将部分字段设不可编辑
    if (field == "creatorName") {
        e.cancel = true;
    }
}