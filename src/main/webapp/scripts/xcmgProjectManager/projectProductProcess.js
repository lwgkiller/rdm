//是否要检查交付物上传情况
var checkDelivery = "";
//每个阶段最后一个项目负责人操作的节点（可调整角色、系数，需进行阶段评价）
var stageRoleRatioEvaluate = "";
//是否检查必填项(立项评审前的一个节点)
var checkRequired = "";
//是否放开项目负责人的编辑权限
var editRespman = "";
//是否放开结项得分的编辑权限
var editKnotScore = "";
//是否放开项目成员打分评价编辑权限
var editMemberPjScore = "";
//是否放开项目负责人打分评价权限
var editProRespPjScore = "";
//是否当前是立项评审之前（有些信息还是可以更改）
var beforeBuildReview = "";
//是否是填单起始页面（来源、主部门可以修改）
var isStartNode = "";
//是否允许编辑项目编号(编辑标准分，编辑级别)
var editProjectNumber = "";
//指定具体的项目成员
var memberPoint = "";

//设计方案
var designScheme = "";


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'editRespman') {
            editRespman = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'checkDelivery') {
            checkDelivery = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'stageRoleRatioEvaluate') {
            stageRoleRatioEvaluate = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'beforeBuildReview') {
            beforeBuildReview = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'editKnotScore') {
            editKnotScore = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'editMemberPjScore') {
            editMemberPjScore = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'editProRespPjScore') {
            editProRespPjScore = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'checkRequired') {
            checkRequired = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isStartNode') {
            isStartNode = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'editProjectNumber') {
            editProjectNumber = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'memberPoint') {
            memberPoint = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'designScheme') {
            designScheme = nodeVars[i].DEF_VAL_;
        }
    }

    //立项评审节点前的权限控制
    if (beforeBuildReview == '1') {
        //填单界面不允许编辑类别
        var respmanId = $.trim(mini.get("respmanId").getValue());
        if(!currentUserId==respmanId){
            mini.get("ysbh").setEnabled(false);
        }
        mini.get("projectCategory").setEnabled(false);
        if (isStartNode != '1') {
            mini.get("projectSource").setEnabled(false);
            mini.get("mainDepId").setEnabled(false);
        }
        if (!isProjectManager || isStartNode!='1') {
            mini.get("cwddh").setEnabled(false);
            mini.get("gbcwddh").setEnabled(false);
        }
        //项目来源是“自主申报类”，主部门不允许编辑
        var projectSourceName = mini.get("projectSource").getText();
        if (projectSourceName == '自主申报类') {
            mini.get("mainDepId").setEnabled(false);
        }
        mini.get("knotScoreId").setEnabled(false);
        mini.get("knotRatioId").setEnabled(false);
        mini.get("beginLevel").setEnabled(false);
        mini.get("projectNumber").setEnabled(false);
        mini.get("pointStandardScore").setEnabled(false);
        mini.get("level").setEnabled(false);
        if (editRespman != '1') {
            mini.get("respmanId").setEnabled(false);
        }
    } else {
        //立项评审后的控制
        formProject.setEnabled(false);
        grid_project_measureTarget.setAllowCellEdit(false);
        grid_project_plan.setAllowCellEdit(false);
        $("#projectMemberButtons").hide();
        $("#diffAct").hide();
        $("#cxyMemberButtons").hide();
        $("#projectAchievementButtons").hide();
        $("#projectMeasureTargetButtons").hide();
        $("#projectInputButtons").hide();
        if (editKnotScore == '1') {
            mini.get("knotScoreId").setEnabled(true);
            mini.get("knotRatioId").setEnabled(true);
            mini.get("level").setEnabled(true);
        }
        if (editProjectNumber == '1') {
            mini.get("projectNumber").setEnabled(true);
            mini.get("pointStandardScore").setEnabled(true);
            mini.get("beginLevel").setEnabled(true);
        }
    }

    mini.get("fileNameFilter").setEnabled(true);
    if (designScheme == '1') {
        mini.get('productIds').setEnabled(true);
    }
}

function hideRoleRatio(e) {
    if ((action == 'task' && memberPoint == 'yes') || detailHideRoleRatio == "true") {
        return "***";
    } else {
        var record = e.record;
        return record.roleRatio;
    }
}

//检查角色与系数的对应范围是否正确
function checkRoleRatioRange(roleId, roleName, ratioValue) {
    if (!ratioValue) {
        return {result: true};
    }
    var ratioRange = findRatioRangeByRoleId(roleId);
    var parsedRatioValue = parseFloat(ratioValue);
    if (parsedRatioValue != ratioValue) {
        return {result: false, message: roleName + "角色系数范围：大于" + ratioRange.minRatio + "，小于等于" + ratioRange.maxRatio};
    }
    //允许为0
    if (parsedRatioValue == 0.0 || parsedRatioValue == 0) {
        return {result: true};
    }
    if (parsedRatioValue <= ratioRange.minRatio || parsedRatioValue > ratioRange.maxRatio) {
        return {result: false, message: roleName + "角色系数范围：大于" + ratioRange.minRatio + "，小于等于" + ratioRange.maxRatio};
    }
    return {result: true};
}

function findRatioRangeByRoleId(roleId) {
    for (var i = 0; i < roleRatioList.length; i++) {
        if (roleRatioList[i].roleId == roleId) {
            var minRatio = roleRatioList[i].minRatio;
            if (!minRatio) {
                minRatio = 0;
            }
            var maxRatio = roleRatioList[i].maxRatio;
            if (!maxRatio) {
                maxRatio = 0;
            }
            return {minRatio: minRatio, maxRatio: maxRatio};
        }
    }
    return {minRatio: 0, maxRatio: 0};
}


//审批或者下一步
function projectApprove() {
    //检查项目负责人是否填写
    if (editRespman == '1') {
        var respmanId = $.trim(mini.get("respmanId").getValue());
        if (!respmanId) {
            mini.alert('请选择项目负责人');
            return;
        }
    }
    //检查项目结项得分是否填写
    if (editKnotScore == '1') {
        var knotScore = $.trim(mini.get("knotScoreId").getValue());
        if (!knotScore) {
            mini.alert('请填写结项评价得分');
            return;
        }
        var knotRatio = $.trim(mini.get("knotRatioId").getValue());
        if (!knotRatio) {
            mini.alert('请填写结项等级系数');
            return;
        }
    }

    //立项评审之前由于可以修改，因此需要检查必填项(同编辑场景)
    if (beforeBuildReview == '1') {
        var proName = $.trim(mini.get("projectName").getValue())
        if (!proName) {
            mini.alert("请填写项目名称");
            return;
        }
        var levelId = $.trim(mini.get("beginLevel").getValue())
        if (!levelId) {
            mini.alert(projectProductEdit_name13);
            return;
        }

        var checkMemberMust = checkMemberRequired(grid_project_memberInfo.getData(), false, false);
        if (!checkMemberMust.result) {
            mini.alert(checkMemberMust.message);
            return;
        }

        var checkMemberOutMust = checkMemOutRequired(grid_cxy_memberInfo.getData(), false);
        if (!checkMemberOutMust.result) {
            mini.alert(checkMemberOutMust.message);
            return;
        }

        var checkMeasureTarget = checkMeasureTargetRequired(grid_project_measureTarget.getData());
        if (!checkMeasureTarget.result) {
            mini.alert(checkMeasureTarget.message);
            return;
        }
        //立项评审之前 填写项目成果信息
        var checkAchievementAllMust = checkAchievementRequired(grid_project_achievement.getData(), false);
        if (!checkAchievementAllMust.result) {
            mini.alert(checkAchievementAllMust.message);
            return;
        }

        //立项评审的前一个节点，此时需要目标、计划中都应该填写完毕
        if (checkRequired == '1') {
            //产学研类的不用检查项目指导人
            if (!isCxy) {
                var checkMemberHasGuidResult = checkMemberHasGuid(grid_project_memberInfo.getData());
                if (!checkMemberHasGuidResult.result) {
                    mini.alert(checkMemberHasGuidResult.message);
                    return;
                }
            }
            //产学研检查合作单位
            if (isCxy) {
                var hzdw = $.trim(mini.get("hzdw").getValue());
                if (!hzdw) {
                    mini.alert("请填写产学研项目合作单位");
                    return;
                }
            }
            //立项之前每个角色的承担工作内容和角色系数都要填好
            var checkMemberAllMust = checkMemberRequired(grid_project_memberInfo.getData(), true, true);
            if (!checkMemberAllMust.result) {
                mini.alert(checkMemberAllMust.message);
                return;
            }
            //非技术中心下的部门，成员姓名需要选择好
            var checkGlMemberAssign = checkProductGlUserAssign(grid_project_memberInfo.getData(), projectId,mini.get("projectCategory").getValue());
            if (!checkGlMemberAssign.result) {
                mini.alert(checkGlMemberAssign.message);
                return;
            }

            var checkMemberOutAllMust = checkMemOutRequired(grid_cxy_memberInfo.getData(), true);
            if (!checkMemberOutAllMust.result) {
                mini.alert(checkMemberOutAllMust.message);
                return;
            }

            //检查项目计划
            var checkProjectPlanResult = checkProjectPlanRequired(grid_project_plan.getData());
            if (!checkProjectPlanResult.result) {
                mini.alert(checkProjectPlanResult.message);
                return;
            }
            var ysbh = $.trim(mini.get("ysbh").getValue());
            if (!ysbh) {
                mini.alert("请填写预算编号");
                return;
            }
            var projectBuildReason = $.trim(mini.get("projectBuildReason").getValue());
            if (!projectBuildReason) {
                mini.alert("请填写项目背景及目标中的项目背景");
                return;
            }
            var describeTarget = $.trim(mini.get("describeTarget").getValue());
            if (!describeTarget) {
                mini.alert("请填写目背景及目标中的定性目标");
                return;
            }
            var mainTask = $.trim(mini.get("mainTask").getValue());
            if (!mainTask) {
                mini.alert("请填写目背景及目标中的主要任务");
                return;
            }
            var applyPlanCheck = checkProjectApplyPlan();
            if (!applyPlanCheck.result) {
                mini.alert(applyPlanCheck.message);
                return;
            }
            var measureTarget = grid_project_measureTarget.getData();
            if (!measureTarget || measureTarget.length <= 0) {
                mini.alert("请填写目背景及目标中的定量目标");
                return;
            }
        }
    }
    //检查必须交付物是否缺失
    if (checkDelivery) {
        // var checkDeliveryResult = checkDeliveryProductF();
        // if (!checkDeliveryResult) {
        //     return;
        // }

        var checkDeliveryResult = checkDeliveryF();
        if (!checkDeliveryResult) {
            return;
        }
        //检查当前阶段已上传的交付物是否审批完成
        var checkDeliveryApproval = checkDeliveryApprovalF();
        if (!checkDeliveryApproval) {
            return;
        }
    }

    if (editProjectNumber == '1' && isCxy) {
        var projectNumber = mini.get("projectNumber").getValue();
        if (!projectNumber) {
            mini.alert("产学研项目请填写项目编号");
            return;
        }
    }

    //检查自己部门的所有成员是否都指定完成
    if (memberPoint == 'yes') {
        var checkMemberPointResult = checkMemberPoint(grid_project_memberInfo.getData(), currentUserMainDeptId);
        if (!checkMemberPointResult.result) {
            mini.alert(checkMemberPointResult.message);
            return;
        }
    }
    if (designScheme) {
        var productIds = mini.get('productIds').getValue();
        if (!productIds) {
            mini.alert("请填写包含产品信息!");
            return;
        }
        //查询选择的交付物是否都选择产品
        var postData = {"projectId": projectId};
        var _url = jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/projectUserDelivery.do';
        var resultData = ajaxRequest(_url, 'POST', false, postData);
        if (resultData && resultData.success) {
            if (resultData.data) {
                mini.alert(resultData.data + '负责的交付物，尚未选择对应产品！请在“项目成员信息”中点击“负责交付物”，选择对应产品，然后再选择交付物对应产品');
                return;
            }
        }
    }

    //检查成员角色系数和阶段评价是否填写
    if (stageRoleRatioEvaluate == 'yes') {
        //    检查有效成员是否有系数和评价
        var memData = grid_project_memberInfo.getData();
        if (memData.length > 0) {
            var checkEvaluateUserIds = [];
            for (var index = 0; index < memData.length; index++) {
                if (memData[index].userValid != '02'&& memData[index].roleName != '项目指导人') {
                    if ((memData[index].roleRatio==null || memData[index].roleRatio==='' ||memData[index].roleRatio==undefined)) {
                        mini.alert('项目成员‘'+memData[index].userName + '’没有填写角色系数');
                        return;
                    }
                    checkEvaluateUserIds.push(memData[index].userId);
                }
            }
            if (checkEvaluateUserIds.length > 0) {
                var currentStageId = mini.get("currentStageId").getValue();
                var postData = {
                    projectId: projectId,
                    stageId: currentStageId,
                    userIds:checkEvaluateUserIds
                };
                //    后台检查是否都有评价
                $.ajax({
                    url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/checkEvaluate.do',
                    type: 'post',
                    async: false,
                    data: mini.encode(postData),
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            if (!data.success) {
                                mini.alert(data.message);
                            } else {
                                //    提示是否已调整完毕角色和系数
                                var message="当前节点允许调整项目角色和角色系数，确认已调整完毕？";
                                if(editProRespPjScore=='1') {
                                    message="当前节点允许调整项目负责人的角色系数，确认已调整完毕？"
                                }
                                mini.confirm(message, "提示",
                                    function (action) {
                                        if (action == "ok") {
                                            window.parent.approve();
                                        }
                                    }
                                );
                            }
                        }
                    }
                });
            }
        }
    } else if(editProjectNumber =='1'){
        var pointStandardScore = mini.get("pointStandardScore").getValue();
        if(pointStandardScore) {
            if (parseInt(pointStandardScore,10)!=pointStandardScore)  {
                mini.alert('标准分请填写整数');
                return;
            }
        }
        mini.confirm("当前节点允许调整项目编号、指定标准分、项目级别，确认已调整完毕？", "提示",
            function (action) {
                if (action == "ok") {
                    window.parent.approve();
                }
            }
        );
    } else {
        window.parent.approve();
    }
}

//流程提交后，立项评审之前，可以保存信息
function saveProjectInProcess() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = getData();
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/save.do',
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

//检查pdm交付物是否填写适用产品
function checkDeliveryProductF() {
    $.ajaxSettings.async = false;
    var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/checkDeliveryProduct.do";
    var data = {
        projectId: projectId,
    };
    var checkResult = {};
    $.post(
        url,
        data,
        function (json) {
            checkResult = json;
        });
    $.ajaxSettings.async = true;
    if (checkResult.result == 'true') {
        return true;
    } else {
        mini.alert(checkResult.content);
        return false;
    }
}

//检查交付物是否完成审批
function checkDeliveryApprovalF() {
    $.ajaxSettings.async = false;
    var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/checkDeliveryApproval.do";
    var currentStageId = mini.get("currentStageId").getValue();
    var data = {
        projectId: projectId,
        stageId: currentStageId,
    };
    var checkResult = {};
    $.post(
        url,
        data,
        function (json) {
            checkResult = json;
        });
    $.ajaxSettings.async = true;
    if (checkResult.result == 'true') {
        return true;
    } else {
        mini.alert(checkResult.content);
        return false;
    }
}

//检查交付物是否缺失
function checkDeliveryF(pointDeliveryName) {
    $.ajaxSettings.async = false;
    var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/checkStageDelivery.do";
    var currentStageId = mini.get("currentStageId").getValue();
    var levelId = mini.get("beginLevel").getValue();
    var sourceId = mini.get("projectSource").getValue();
    if (!pointDeliveryName) {
        pointDeliveryName = "";
    }
    var data = {
        projectId: projectId,
        stageId: currentStageId,
        levelId: levelId,
        sourceId: sourceId,
        pointDeliveryName: pointDeliveryName,
        categoryName: mini.get("projectCategory").getText()
    };
    var checkResult = {};
    $.post(
        url,
        data,
        function (json) {
            checkResult = json;
        });
    $.ajaxSettings.async = true;
    if (checkResult.result == 'true') {
        return true;
    } else {
        mini.alert(checkResult.content);
        return false;
    }
}

//下载文档
function downProjectLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var inputRelativeFilePath = $("<input>");
    inputRelativeFilePath.attr("type", "hidden");
    inputRelativeFilePath.attr("name", "relativeFilePath");
    inputRelativeFilePath.attr("value", record.relativeFilePath);
    var actionType = $("<input>");
    actionType.attr("type", "hidden");
    actionType.attr("name", "actionType");
    actionType.attr("value", "project");
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputRelativeFilePath);
    form.append(actionType);
    form.submit();
    form.remove();
}

//删除文档
function deleteProjectFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                //对于在审批流程中的文件，不允许删除
                if (record.fileapplyId) {
                    mini.alert("在审批单中的文件不允许删除。流程单号：" + record.fileapplyId);
                    return;
                }

                $.ajaxSettings.async = false;
                var url = jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/deleteProjectFiles.do";
                var data = {
                    projectId: record.projectId,
                    id: record.id,
                    relativeFilePath: record.relativeFilePath,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        refreshFile();
                    });
                $.ajaxSettings.async = true;
            }
        }
    );
}

function expandTree(_currentStageId) {
    var currentStageId;
    if (_currentStageId) {
        currentStageId = _currentStageId;
    } else {
        currentStageId = mini.get("currentStageId").getValue();
    }
    //查找到当前阶段要上传的文件夹节点
    var folderNodes = treegridFileInfo.findNodes(function (node) {
        if (node.id == currentStageId) {
            currentPid = node.id;
            currentRelativeFilePath = node.relativeFilePath + '/' + currentPid;
            return true;
        }
    });

    //查找到当前阶段已经上传的文件
    var fileNodes = treegridFileInfo.findNodes(function (node) {
        if (node.pid == currentStageId) {
            return true;
        }
    });
    //展开所有找到的节点(没有文件则展开文件夹)
    var finalNodes = fileNodes.length > 0 ? fileNodes : folderNodes;
    for (var i = 0, l = finalNodes.length; i < l; i++) {
        var node = finalNodes[i];
        treegridFileInfo.expandPath(node);
    }
    //第一个节点选中并滚动到视图
    var firstNode = finalNodes[0];
    if (firstNode) {
        treegridFileInfo.selectNode(firstNode);
        treegridFileInfo.scrollIntoView(firstNode);
    }
}

//根据结项评分的值，设置结项等级
function setRatingNameByKnotScore(onBlur) {
    var knotScore = $.trim(mini.get("knotScoreId").getValue());
    if (!knotScore) {
        return;
    }
    if (onBlur) {
        var r = new RegExp("^\\d+(\\.\\d+)?$");　　//正数
        if (!r.test(knotScore)) {
            mini.alert("结项评价得分请填写1-100内的正数");
            mini.get("knotScoreId").setValue("");
            mini.get("ratingNameId").setValue("");
            return;
        }
        if (knotScore > 100) {
            mini.alert("结项评价得分请填写1-100内的正数");
            mini.get("knotScoreId").setValue("");
            mini.get("ratingNameId").setValue("");
            return;
        }
    }
    var ratingRule = getRatingRuleByScore(knotScore);
    mini.get("ratingNameId").setValue(ratingRule.ratingName);
}

//填写结项系数前，需要先填写结项分数
function checkKnotScore() {
    var knotScore = $.trim(mini.get("knotScoreId").getValue());
    if (!knotScore) {
        // mini.get("#knotRatioId").blur();
        mini.get("#knotRatioId").setValue('');
        mini.alert('请先填写结项评价得分');
        return;
    }
}

//检查结项等级系数是不是在范围内
function checkKnotRatio() {
    var knotRatio = $.trim(mini.get("#knotRatioId").getValue());
    if (!knotRatio) {
        return;
    }
    var knotRatioRange = findKnotRationRange(mini.get("#ratingNameId").getValue());
    if (!knotRatio) {
        mini.alert("当前评价等级的系数范围：" + knotRatioRange.minRatio + "-" + knotRatioRange.maxRatio);
        return;
    }
    var parsedRatioValue = parseFloat(knotRatio);
    if (parsedRatioValue != knotRatio || (parsedRatioValue < knotRatioRange.minRatio || parsedRatioValue > knotRatioRange.maxRatio)) {
        mini.alert("当前评价等级的系数范围：" + knotRatioRange.minRatio + "-" + knotRatioRange.maxRatio);
        mini.get("#knotRatioId").setValue('');
        return;
    }
}

function findKnotRationRange(knotRatingName) {
    for (var i = 0; i < knotScore2RatingRule.length; i++) {
        if (knotScore2RatingRule[i].ratingName == knotRatingName) {
            return knotScore2RatingRule[i];
        }
    }
    return {minRatio: 0, maxRatio: 0.3};
}

//直接作废不需要审批
// discardInst()
function discardNoApply() {
    window.parent.discardInst('false');
}

function jumpToFzsjDetail(fzsjId) {
    var action = "detail";
    var url = jsUseCtxPath + "/fzsj/core/fzsj/fzsjEditPage.do?action=" + action + "&fzsjId=" + fzsjId;
    var winObj = window.open(url);
}

function cpkfDetail(cpkfId) {
    var action = "detail";
    var url = jsUseCtxPath + "/rdm/core/ProductRequire/editPage.do?action=" + action + "&cpkfId=" + cpkfId;
    var winObj = window.open(url);
}
