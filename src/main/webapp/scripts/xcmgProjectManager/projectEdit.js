var knotScore2RatingRule = [];
var roleRankRequire = [];
var levelList = [];
var memRoleList = "";
var achieveTypeList = "";
var respManRole = "";
//上一次的项目负责人，用于判断是否变化
var originalRespManId = "";
var originalRespManName = "";
//上一次的项目类别
var originalCategoryId = "";
//上一次的项目级别Id
var originalLevelId = "";
//当前上传文档的父文件夹id(当前阶段id)
var currentPid = "";
//当前上传文档的相对路径(/项目id/当前阶段id)
var currentRelativeFilePath = "";

var originalProjectSource = "";
var projectData;
//角色与系数的对应范围
var roleRatioList = "";
//当前登录人是否是项目负责人
var isProjectManager = false;
//是否是产学研类项目
var isCxy = false;

$(function () {
    //初始化表单
    initForm();

    isProjectManager = whetherIsProjectManagerAll(currentUserRoles);
    if (isProjectManager) {
        mini.get("remark").setEnabled(true);
    } else {
        mini.get("remark").setEnabled(false);
    }
    //明细入口
    if (action == 'detail') {
        detailActionProcess();
    } else if (action == 'task') {
        document.getElementById('diffAct').innerHTML = "操作后请点击“暂存信息”进行保存，点击“审批”/“下一步”执行流程";
        taskActionProcess();
    } else if (action == 'edit') {
        document.getElementById('diffAct').innerHTML = "操作后请点击“保存草稿”进行保存，点击“提交”启动流程";
        editActionProcess();
    } else if (action == 'change') {
        document.getElementById('diffAct').innerHTML = "操作后请点击“保存变更”进行保存。具体成员由成员部门负责人确定，其他由负责人填写";
        changeActionProcess();
    } else if (action == 'editDelivery') {
        detailActionProcess();
        //交付物管理也放开后面阶段的上传
        $('#newInput').show();
        mini.get("selectStage").setEnabled(true);
        getPassStage();

        $("#uploadFile").show();
        $("#startFileApprovalBtn").show();
        $("#checkboxBaseInfo").attr("checked", false);
        $("#fdBaseInfo").addClass("hideFieldset");
        $("#checkboxMemberInfo").attr("checked", false);
        $("#fdMemberInfo").addClass("hideFieldset");
        $("#checkboxFileInfo").attr("checked", true);
        $("#fileInfo").removeClass("hideFieldset");
        refreshFile();
    }
});


function initForm() {
    mini.get("projectCategory").setEnabled(false);
    $.ajaxSettings.async = false;
    //查询一些规则、下拉信息并渲染
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectSource").load(data.source);
                mini.get("#projectCategory").load(data.category);
                mini.get("#level").load(data.level);
                mini.get("#beginLevel").load(data.level);
                knotScore2RatingRule = data.knot2Rating;
                memRoleList = data.memberRole;
                achieveTypeList = data.achieveType;
                levelList = data.level;
                roleRankRequire = data.roleRankRequire;
                roleRatioList = data.roleRatioList;
                //将项目负责人选项删掉，项目负责人不允许主动选择
                var respManIndex = -1;
                for (var i = 0; i < memRoleList.length; i++) {
                    if (memRoleList[i].roleName == '项目负责人') {
                        respManIndex = i;
                        break;
                    }
                }
                if (respManIndex != -1) {
                    respManRole = memRoleList.splice(respManIndex, 1)[0];
                }
            }
        }
    });

    //根据projectId查询项目信息并填充
    if (projectId) {
        var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/getJson.do";
        $.post(
            url,
            {ids: projectId},
            function (json) {
                //产学研的处理
                if (json && json.categoryName && json.categoryName.indexOf("产学研") != -1) {
                    isCxy = true;
                } else {
                    isCxy = false;
                }
                cxyProjectProcess();
                projectData = json;
                formProject.setData(json);
                originalCategoryId = json.categoryId;
                originalLevelId = json.levelId;
                setRatingNameByKnotScore(false);
                var members = json.xcmgProjectMembers;
                if (members != null && members.length > 0) {
                    grid_project_memberInfo.setData(json.xcmgProjectMembers);
                    for (var i = 0; i < members.length; i++) {
                        if (members[i].roleName == '项目负责人') {
                            mini.get("respmanId").setText(members[i].userName);
                            mini.get("respmanId").setValue(members[i].userId);
                            originalRespManId = members[i].userId;
                            originalRespManName = members[i].userName;
                            break;
                        }
                    }
                }
                var membersOut = json.xcmgProjectMemOut;
                if (membersOut != null && membersOut.length > 0) {
                    grid_cxy_memberInfo.setData(json.xcmgProjectMemOut);
                }
                var achievements = json.xcmgProjectAchievement;
                if (achievements != null && achievements.length > 0) {
                    grid_project_achievement.setData(achievements);
                }

                var extension = json.xcmgProjectExtensions;
                if (extension != null && extension.length > 0) {
                    mini.get("projectBuildReason").setValue(extension[0].projectBuildReason);
                    mini.get("describeTarget").setValue(extension[0].describeTarget);
                    mini.get("mainTask").setValue(extension[0].mainTask);
                    mini.get("applyPlan").setValue(extension[0].applyPlan);
                    var measureTarget = $.parseJSON(extension[0].measureTarget);
                    grid_project_measureTarget.setData(measureTarget);
                }
                grid_project_plan.setData(json.xcmgProjectPlans);


                //显示项目进度
                processShow(json, json.xcmgProjectPlans);
            });

    } else {
        $("#processDiv").hide();
        if (projectCategory) {
            mini.get("projectCategory").setValue(projectCategory);
            categoryChange();
        }
    }
    $.ajaxSettings.async = true;
}

function addproject_measureTargetRow() {
    var row = {};
    grid_project_measureTarget.addRow(row);
}

function removeproject_measureTargetRow() {
    var selecteds = grid_project_measureTarget.getSelecteds();
    grid_project_measureTarget.removeRows(selecteds);
}

//zwt
function addproject_memberInfoRow() {

    var beginLevelId = $.trim(mini.get("beginLevel").getValue())
    if (!beginLevelId) {
        mini.alert(xcmgProjectEdit_name7);
        return;
    }
    var row = {};
    grid_project_memberInfo.addRow(row, 0);
}

function deleteProject_memberInfoRow() {
    var selecteds = grid_project_memberInfo.getSelecteds();
    if (!selecteds || selecteds.length <= 0) {
        mini.alert('请至少选择一条数据');
        return;
    }
    mini.confirm("本操作会将所有项目中产生的数据（如项目参与情况、积分、评价等）彻底删除（已产生积分的成员不允许删除，只能冻结）。<br/>确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //当前会话的交付物信息
            var [currentRowsDelivery, deliveryId2Name] = currentDeliveryAssgin(grid_project_memberInfo.getData());
            for (var i = 0; i < selecteds.length; i++) {
                var row = selecteds[i];
                //交付物唯一标识
                var isUnique = false;
                if (row.roleName == '项目负责人') {
                    mini.alert('项目负责人不允许删除，如需替换，请在“项目基本信息”中“项目负责人”处操作');
                    continue;
                }
                if (row.score && (row.score > 0 || row.score < 0)) {
                    mini.alert('已发放积分的成员不允许删除');
                    continue;
                }
                //删除人员之前,必传交付物分配控制，冻结人员如满足删除条件，不再判断交付物情况，
                if (row.userValid != "02") {
                    if (row.respDeliveryIds) {
                        var tempDeliveryArray = row.respDeliveryIds.split(",");
                        var uniqueDelivery = "";
                        for (let j = 0; j < tempDeliveryArray.length; j++) {
                            if (currentRowsDelivery.has(tempDeliveryArray[j]) && currentRowsDelivery.get(tempDeliveryArray[j]) - 1 < 1) {
                                uniqueDelivery += deliveryId2Name.get(tempDeliveryArray[j]) + ",";//id转化为交付物名称
                                isUnique = true;
                            } else {
                                currentRowsDelivery.set(tempDeliveryArray[j], currentRowsDelivery.get(tempDeliveryArray[j]) - 1);
                            }
                        }
                        //存在必传交付物进行两种提示，需释放后操作
                        if (isUnique) {
                            mini.alert(row.userName + " 负责以下交付物：" + row.respDeliveryNames + "。且：<span style='color: red;'>" + uniqueDelivery.slice(0, -1) + "</span>仅能由<span style='color: red;'></span>该用户提交,请重新分配该交付物后再执行删除操作");
                            continue;
                        } else {
                            mini.alert(row.userName + " 负责以下交付物：" + row.respDeliveryNames + ",请释放交付物后再执行删除操作");
                            continue;
                        }
                    }
                }
                grid_project_memberInfo.removeRow(row);
            }
        }
    });
}

//冻结项目组成员
function removeproject_memberInfoRow() {
    var selecteds = grid_project_memberInfo.getSelecteds();
    if (!selecteds || selecteds.length <= 0) {
        mini.alert('请至少选择一条数据');
        return;
    }

    mini.confirm("成员冻结后，将不再参与后续的评价及积分奖励，同时也无法编辑成员信息。确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var [currentRowsDelivery, deliveryId2Name] = currentDeliveryAssgin(grid_project_memberInfo.getData());
            for (var i = 0; i < selecteds.length; i++) {
                var row = selecteds[i];
                var isUnique = false;
                if (row.roleName == '项目负责人') {
                    mini.alert('项目负责人不允许冻结，如需替换，请在“项目基本信息”中“项目负责人”处操作');
                    continue;
                }
                //存在交付物信息才进行判断
                if (row.respDeliveryIds) {
                    var tempDeliveryArray = row.respDeliveryIds.split(",");
                    var uniqueDelivery = "";
                    for (let j = 0; j < tempDeliveryArray.length; j++) {
                        if (currentRowsDelivery.has(tempDeliveryArray[j]) && currentRowsDelivery.get(tempDeliveryArray[j]) - 1 < 1) {
                            uniqueDelivery += deliveryId2Name.get(tempDeliveryArray[j]) + ",";
                            isUnique = true;
                        } else {
                            currentRowsDelivery.set(tempDeliveryArray[j], currentRowsDelivery.get(tempDeliveryArray[j]) - 1);
                        }
                    }
                    //存在必传交付物进行两种提示，需释放后操作
                    if (isUnique) {
                        mini.alert(row.userName + " 负责以下交付物：" + row.respDeliveryNames + "。且：<span style='color: red;'>" + uniqueDelivery.slice(0, -1) + "</span>仅能由<span style='color: red;'></span>该用户提交,请重新分配该交付物后再执行冻结操作");
                        continue;
                    } else {
                        mini.alert(row.userName + " 负责以下交付物：" + row.respDeliveryNames + ",请释放交付物后再执行冻结操作");
                        continue;
                    }
                }
                //移除成员，增加背景色，同时设置状态
                grid_project_memberInfo.addRowCls(row, "rmMem");
                grid_project_memberInfo.deselect(row, false);
                grid_project_memberInfo.updateRow(row, {userValid: '02'});
            }
        }
    });
}

//撤销项目组成员移除
function revokeRemoveproject_memberInfoRow() {
    var selecteds = grid_project_memberInfo.getSelecteds();
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.userValid == '02') {
            grid_project_memberInfo.removeRowCls(row, "rmMem");
            grid_project_memberInfo.deselect(row, false);
            grid_project_memberInfo.updateRow(row, {userValid: '01'});
        }
    }
}

//将某一条数据的人员替换
function replaceProject_memberInfoRow() {
    var selecteds = grid_project_memberInfo.getSelecteds();
    if (!selecteds || selecteds.length != 1) {
        mini.alert('只能选择一条数据进行替换');
        return;
    }

    mini.confirm("成员替换后，会将原有人员的所有数据（包括积分）替换给新成员。确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            if (selecteds[0].roleName == '项目负责人') {
                mini.alert('项目负责人不能替换，请联系管理员');
                return;
            }
            mini.get("replaceMemberId").setValue(selecteds[0].id);
            mini.get("originalUserId").setValue(selecteds[0].userId);
            mini.get("replaceUserId").setValue('');
            mini.get("replaceUserId").setText('');
            replaceMemberWindow.show();
        }
    });
}

function replaceMember() {
    var replaceUserId = mini.get("replaceUserId").getValue();
    if (!replaceUserId) {
        mini.alert('请选择要替换到的成员');
        return;
    }
    var currentMembers = grid_project_memberInfo.getData();
    if (currentMembers && currentMembers.length > 0) {
        for (var index = 0; index < currentMembers.length; index++) {
            if (currentMembers[index].userId == replaceUserId) {
                mini.alert('该成员当前已存在！');
                return;
            }
        }
    }
//    替换成员的关系和积分，并刷新表格
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/replaceMember.do?projectId=' + projectId + "&originalUserId=" + mini.get("originalUserId").getValue() + "&currentUserId=" + replaceUserId + "&memberId=" + mini.get("replaceMemberId").getValue(),
        success: function (returnObj) {
            if (returnObj) {
                var message = "";
                if (returnObj.result) {
                    mini.alert("操作成功", "提示信息");
                    queryMembersByPid(projectId);
                } else {
                    message = "操作失败：" + returnObj.message;
                    mini.alert(message, "提示信息");
                }
            }
        }
    });
}

function queryMembersByPid(projectId) {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryMembersByPid.do?projectId=' + projectId,
        success: function (returnObj) {
            if (returnObj) {
                grid_project_memberInfo.setData(returnObj);
            }
        }
    });
}

//添加产学研外部成员
function addCxy_memberInfoRow() {
    var row = {};
    grid_cxy_memberInfo.addRow(row, 0);
}

//移除产学研外部成员
function removeCxy_memberInfoRow() {
    var selecteds = grid_cxy_memberInfo.getSelecteds();
    grid_cxy_memberInfo.removeRows(selecteds);
}

//保存草稿
function saveProject(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startProjectProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var proName = $.trim(mini.get("projectName").getValue())
    if (!proName) {
        return {"result": false, "message": "请填写项目名称"};
    }
    var proSource = $.trim(mini.get("projectSource").getValue())
    if (!proSource) {
        return {"result": false, "message": "请选择项目来源"};
    }
    var proCategory = $.trim(mini.get("projectCategory").getValue())
    if (!proCategory) {
        return {"result": false, "message": "请选择项目类别"};
    }

    var beginLevelId = $.trim(mini.get("beginLevel").getValue())
    if (!beginLevelId) {
        return {"result": false, "message": xcmgProjectEdit_name7};
    }
    var mainDepId = $.trim(mini.get("mainDepId").getValue())
    if (!mainDepId) {
        return {"result": false, "message": "请选择项目牵头部门"};
    }
    var checkMemberMust = checkMemberRequired(grid_project_memberInfo.getData(), false, false);
    if (!checkMemberMust.result) {
        return {"result": false, "message": checkMemberMust.message};
    }

    var checkMemberOutMust = checkMemOutRequired(grid_cxy_memberInfo.getData(), false);
    if (!checkMemberOutMust.result) {
        return {"result": false, "message": checkMemberOutMust.message};
    }

    /*    var checkMemberZj=checkMemberRankOk(grid_project_memberInfo.getData());
        if(!checkMemberZj.result) {
            return {"result": false, "message": checkMemberZj.message};
        }*/
    var checkMeasureTarget = checkMeasureTargetRequired(grid_project_measureTarget.getData());
    if (!checkMeasureTarget.result) {
        return {"result": false, "message": checkMeasureTarget.message};
    }
    var checkAchievementTarget = checkAchievementRequired(grid_project_achievement.getData(), false);
    if (!checkAchievementTarget.result) {
        return {"result": false, "message": checkAchievementTarget.message};
    }

    var plans = grid_project_plan.getData();
    if (!plans || plans.length == 0) {
        return {"result": false, "message": "项目计划为空"};
    }
    return {"result": true};
}


//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formProject");
    if (formData.SUB_treegridFileInfo) {
        delete formData.SUB_treegridFileInfo;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'projectName', val: formData.projectName}];
    var categoryName = mini.get("projectCategory").getText();
    if (!categoryName) {
        categoryName = '';
    }
    formData.categoryName = categoryName;
    var changeAchievementArr = grid_project_achievement.getChanges();
    if (changeAchievementArr && changeAchievementArr.length > 0) {
        formData.changeAchievementArr = changeAchievementArr;
    }

    var changeMemberArr = grid_project_memberInfo.getChanges();
    if (changeMemberArr && changeMemberArr.length > 0) {
        formData.changeMemberArr = changeMemberArr;
    }

    return formData;
}

//展示结项评价标准窗口
function showKnotLevelDivideWin() {
    mini.open({
        title: "结项评价参考",
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/editRuleShow.do?scene=knotScoreRule",
        width: 750,
        height: 330,
        allowResize: true,
        onload: function () {

        }
    });
}

function showRoleRatioRange() {
    mini.open({
        title: "角色系数范围参考",
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/editRuleShow.do?scene=roleRatioRule",
        width: 750,
        height: 330,
        allowResize: true,
        onload: function () {

        }
    });
}

//根据分级评分的值，设置级别
function levelChange() {

    var levelId = mini.get("beginLevel").getValue();
    //根据大级别变化设置项目计划中的交付物列
    if (levelId != originalLevelId) {
        //变更场景下立项和结项级别不联动
        if (action != "change") {
            mini.get("level").setValue(levelId);
            originalLevelId = levelId;
            reloadDeliveryByLevelId();
            updatePointStandardScore();
        }


    }
}


//项目类别变化
function categoryChange() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    if (originalCategoryId != '') {
        mini.confirm("改变项目类别会重置项目计划，确定改变？", "提示",
            function (action) {
                if (action == "ok") {
                    //重新加载项目计划表
                    reloadProjectPlan();
                    originalCategoryId = currentCategoryId;
                } else {
                    mini.get("projectCategory").setValue(originalCategoryId);
                }
                // 产学研类的处理
                if (mini.get("projectCategory").getText().indexOf("产学研") != -1) {
                    isCxy = true;
                } else {
                    isCxy = false;
                }
                cxyProjectProcess();
            }
        );
    } else {
        //加载项目计划表
        reloadProjectPlan();
        originalCategoryId = currentCategoryId;
        // 产学研类的处理
        if (mini.get("projectCategory").getText().indexOf("产学研") != -1) {
            isCxy = true;
        } else {
            isCxy = false;
        }
        cxyProjectProcess();
    }
}

//打开文件上传窗口
function projectUploadFile(e) {
    if (!projectId) {
        mini.alert('请先保存项目！');
        return;
    }
    //stageId、sourceId必须有值
    var currentStageId = mini.get("currentStageId").getValue();
    /*if (!currentStageId) {
        mini.alert('请于流程启动后再进行文档上传');
        return;
    }*/
    var projectSourceId = mini.get("projectSource").getValue();
    if (!projectSourceId) {
        mini.alert('请选择项目来源');
        return;
    }
    var buluStage = mini.get("selectStage").getValue();
    if (buluStage) {
        currentStageId = buluStage;
        expandTree(currentStageId);
    }
    if (!currentPid || !currentRelativeFilePath) {
        mini.alert('没有找到当前阶段的文件夹');
        return;
    }

    var deliveryInfos = queryDeliverys(currentStageId, '', projectSourceId, '', projectId)
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/openUploadWindow.do",
        width: 930,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var projectParams = {};
            projectParams.projectId = mini.get("projectId").getValue();
            projectParams.relativeFilePath = currentRelativeFilePath;
            projectParams.pid = currentPid;
            projectParams.action = action;
            var data = {deliveryInfos: deliveryInfos, projectParams: projectParams};  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            refreshFile();
        }
    });
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

//根据项目负责人设置项目成员中的负责人、valueChanged()变化
function setRespMan() {
    var currentRespmanId = mini.get("respmanId").getValue();
    var currentRespmanName = mini.get("respmanId").getText();
    var currentRespmanInfo = {};
    if (currentRespmanId != '') {
        currentRespmanInfo = getUserInfoById(currentRespmanId);
        if (whetherIsDepRespman(currentRespmanInfo.userDeps) && !isCxy) {
            mini.get("respmanId").setValue(originalRespManId);
            mini.get("respmanId").setText(originalRespManName);
            mini.alert('部门负责人不能作为项目负责人');
            return;
        }
        // 判断当前用户是否有负责的超过2个以上的项目延误5天以上，存在则不允许
        if (judgeUserRespProjectDelay5(currentRespmanId)) {
            mini.get("respmanId").setValue(originalRespManId);
            mini.get("respmanId").setText(originalRespManName);
            mini.alert('所选用户负责的项目已有2项以上延误大于5天，当前不能作为项目负责人！');
            return;
        }
    }

    //项目负责人发生了变更
    if (currentRespmanId != originalRespManId) {
        if (currentRespmanId == '') {
            //冻结原有的
            var originalRespMembers = findRespMemberRows();
            if (originalRespMembers != null && originalRespMembers.length > 0) {
                //冻结项目负责人，并改角色为一般完成人
                grid_project_memberInfo.addRowCls(originalRespMembers[0], "rmMem");
                grid_project_memberInfo.updateRow(originalRespMembers[0], {
                    userValid: '02',
                    roleId: '4',
                    roleName: '项目一般完成人'
                });
            }
            originalRespManId = "";
            originalRespManName = "";
        } else if (originalRespManId == '') {
            //增加现在的
            var existRows = findMemberRowByUserId(currentRespmanId);
            if (existRows != null && existRows.length > 0) {
                grid_project_memberInfo.removeRowCls(existRows[0], "rmMem");
                grid_project_memberInfo.updateRow(existRows[0], {roleId: '2', roleName: '项目负责人', userValid: ''});
            } else {
                var addRow = {
                    userId: currentRespmanId,
                    userName: currentRespmanName,
                    memberDeptId: currentRespmanInfo.mainDepId,
                    memberDeptName: currentRespmanInfo.mainDepName,
                    userJob: currentRespmanInfo.GW,
                    gwno: currentRespmanInfo.gwno,
                    roleId: respManRole.roleId,
                    roleName: respManRole.roleName
                };
                grid_project_memberInfo.addRow(addRow);
            }
            originalRespManId = currentRespmanId;
            originalRespManName = currentRespmanName;
        } else {
            //冻结原有的
            var originalRespMembers = findRespMemberRows();
            if (originalRespMembers != null && originalRespMembers.length > 0) {
                // 冻结项目负责人，并改角色为一般完成人
                grid_project_memberInfo.addRowCls(originalRespMembers[0], "rmMem");
                grid_project_memberInfo.updateRow(originalRespMembers[0], {
                    userValid: '02',
                    roleId: '4',
                    roleName: '项目一般完成人'
                });
            }
            if (action == 'change') {
                changedRespManId = originalRespManId;
            }
            //增加现在的
            var existRows = findMemberRowByUserId(currentRespmanId);
            if (existRows != null && existRows.length > 0) {
                grid_project_memberInfo.updateRow(existRows[0], {roleId: '2', roleName: '项目负责人', userValid: ''});
            } else {
                var addRow = {
                    userId: currentRespmanId,
                    userName: currentRespmanName,
                    memberDeptId: currentRespmanInfo.mainDepId,
                    memberDeptName: currentRespmanInfo.mainDepName,
                    userJob: currentRespmanInfo.GW,
                    gwno: currentRespmanInfo.gwno,
                    roleId: respManRole.roleId,
                    roleName: respManRole.roleName,
                    projectTask: originalRespMembers[0].projectTask,
                    respDeliveryIds: originalRespMembers[0].respDeliveryIds,
                    respDeliveryNames: originalRespMembers[0].respDeliveryNames,
                    workHour: originalRespMembers[0].workHour,
                    roleRatio: originalRespMembers[0].roleRatio
                };
                grid_project_memberInfo.addRow(addRow);
            }
            originalRespManId = currentRespmanId;
            originalRespManName = currentRespmanName;
        }
    }
}

function findMemberRowByUserId(findUserId) {
    var rows = grid_project_memberInfo.findRows(function (row) {
        if (row.userId == findUserId) return true;
    });
    return rows;
}

function findRespMemberRows() {
    var rows = grid_project_memberInfo.findRows(function (row) {
        if (row.roleName == "项目负责人") return true;
    });
    return rows;
}

//根据类别查找项目阶段，根据类别、级别、来源查找交付物
function reloadProjectPlan() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    var currentSourceId = mini.get("projectSource").getValue();
    $.post(
        jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getNewPlan.do',
        {categoryId: currentCategoryId, levelId: mini.get("beginLevel").getValue(), sourceId: currentSourceId},
        function (data) {
            grid_project_plan.setData(data.newPlan);
            //设置项目进度
            var json = {};
            json.projectProcess = data.stage;
            if (projectData) {
                json.currentStageNo = projectData.currentStageNo;
            }
            if (projectData) {
                json.knotTime = projectData.knotTime;
            }
            if (projectData) {
                json.whetherDelay = projectData.whetherDelay;
            }
            //展示进度图
            processShow(json, data.newPlan);
            $("#processDiv").show();
        });
}

//用于保存当前的项目来源选项
function sourceClick() {
    originalProjectSource = mini.get("projectSource").getValue();
}

//项目来源变化，如果不满足条件则不能改变项目来源
function sourceChanged() {
    //“计划类”只能由部门负责人和项目管理人员选择，“公司决策新增类”只能由技术委员会和项目管理人员的人选择
    var nowSourceId = mini.get("projectSource").getValue();
    var nowSourceText = mini.get("projectSource").getText();
    if (nowSourceText == '计划类') {
        if (!whetherIsDepRespman(currentUserDeps) && !whetherIsProjectManager(currentUserRoles) && !whetherIsProjectManagerNotZSZX(currentUserRoles)) {
            mini.get("projectSource").setValue(originalProjectSource);
            mini.alert('仅部门负责人和项目管理人员允许选择“计划类”项目');
            return;
        }
    } else if (nowSourceText == '公司决策新增类') {
        if (!whetherIsJSWYHman(currentUserRoles) && !whetherIsProjectManager(currentUserRoles) && !whetherIsProjectManagerNotZSZX(currentUserRoles)) {
            mini.get("projectSource").setValue(originalProjectSource);
            mini.alert('仅技术委员会和项目管理人员允许选择“公司决策新增类”项目');
            return;
        }
    } else if (nowSourceText == '自主申报类') {
        if (whetherIsDepRespman(currentUserDeps) && !isCxy) {
            mini.get("projectSource").setValue(originalProjectSource);
            mini.alert('部门负责人不能作为项目负责人，不允许选择“自主申报类”项目');
            return;
        }
        // 判断当前用户是否有负责的超过2个以上的项目延误5天以上，存在则不允许
        if (judgeUserRespProjectDelay5(currentUserId)) {
            mini.get("projectSource").setValue(originalProjectSource);
            mini.alert('当前用户负责的项目已有2项以上延误大于5天，当前不能作为项目负责人！');
            return;
        }
    }
    reloadDeliveryBySourceId();
    setMainDepBySource();
}


//如果有类别和级别，则刷新交付物
function reloadDeliveryBySourceId() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    var levelId = mini.get("beginLevel").getValue();
    if (levelId != '' && currentCategoryId != '') {
        updatePlanDelivery(currentCategoryId, levelId, mini.get("projectSource").getValue());
    }
}

function setMainDepBySource() {
    var proSourceName = mini.get("projectSource").getText();
    if (proSourceName == '自主申报类') {
        mini.get("mainDepId").setEnabled(false);
        var userInfo = getUserInfoById(currentUserId);
        mini.get("mainDepId").setValue(userInfo.mainDepId);
        mini.get("mainDepId").setText(userInfo.mainDepName);
    } else {
        mini.get("mainDepId").setEnabled(true);
        mini.get("mainDepId").setValue("");
        mini.get("mainDepId").setText("");
        $("input[name='mainDepName']").css("background-color", "");
    }
    //设置项目负责人
    if (proSourceName == '自主申报类') {
        mini.get("respmanId").setValue(currentUserId);
        mini.get("respmanId").setText(currentUserName);
        mini.get("respmanId").doValueChanged();
    } else {//修改为其他类,恢复为空白状态
        mini.get("respmanId").setValue("");
        mini.get("respmanId").setText("");
        //激活valueChanged()事件
        mini.get("respmanId").doValueChanged();
    }
}

//项目大级别变化，如果有类别和来源，则刷新交付物
function reloadDeliveryByLevelId() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    var currentSourceId = mini.get("projectSource").getValue();
    if (currentSourceId != '' && currentCategoryId != '') {
        updatePlanDelivery(currentCategoryId, mini.get("beginLevel").getValue(), currentSourceId);
    }
}

//更新项目计划中的交付物明细
function updatePlanDelivery(categoryId, levelId, sourceId) {
    $.post(
        jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getDeliveryNamesByCategoryLevelSource.do',
        {categoryId: categoryId, levelId: levelId, sourceId: sourceId},
        function (data) {
            var rows = grid_project_plan.getData()
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                var stageId = row.stageId;
                grid_project_plan.updateRow(row, {deliveryName: data[stageId]});
            }
        });
}

function updatePointStandardScore() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    var levelId = mini.get("beginLevel").getValue();
    $.post(
        jsUseCtxPath + '/xcmgProjectManager/core/config/getStandardScoreByCategoryLevel.do',
        {categoryId: currentCategoryId, levelId: levelId},
        function (data) {
            if (data && data.score) {
                mini.get("pointStandardScore").setValue(data.score);
            }
        });
}


function detailActionProcess() {
    formProject.setEnabled(false);
    grid_project_memberInfo.setAllowCellEdit(false);
    grid_project_measureTarget.setAllowCellEdit(false);
    grid_project_plan.setAllowCellEdit(false);
    grid_project_achievement.setAllowCellEdit(false);
    $("#projectMemberButtons").hide();
    $("#cxyMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#projectMeasureTargetButtons").hide();
    $("#projectInputButtons").hide();
    $('#newInput').hide();
    $("#uploadFile").hide();
    $("#syncPDMBtn").hide();
    $("#startFileApprovalBtn").hide();
    $("#detailToolBar").show();
    //非草稿放开流程信息查看按钮
    if (status != 'DRAFTED') {
        $("#processInfo").show();
    }
    mini.get("fileNameFilter").setEnabled(true);
}

function editActionProcess() {
    $("#uploadFile").hide();
    $("#syncPDMBtn").hide();
    $("#startFileApprovalBtn").hide();
    mini.get("respmanId").setEnabled(false);
    mini.get("knotScoreId").setEnabled(false);
    mini.get("knotRatioId").setEnabled(false);
    mini.get("projectNumber").setEnabled(false);
    mini.get("pointStandardScore").setEnabled(false);
    mini.get("level").setEnabled(false);
    //项目来源是“自主申报类”，主部门不允许编辑
    var projectSourceName = mini.get("projectSource").getText();
    if (projectSourceName == '自主申报类') {
        mini.get("mainDepId").setEnabled(false);
    }
    var respmanId = $.trim(mini.get("respmanId").getValue());
    if(!currentUserId==respmanId){
        mini.get("ysbh").setEnabled(false);
    }
    if (!isProjectManager) {
        mini.get("cwddh").setEnabled(false);
        mini.get("gbcwddh").setEnabled(false);
    }
}

function toggleFieldSet(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
    if (id == 'fileInfo') {
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/ajaxAsync.do',
            contentType: 'application/json',
            success: function (data) {
                refreshFile();
            }
        });
    }
}

function queryDeliverys(stageId, levelPid, sourceId, queryPublic, projectId) {
    var queryParam = {};
    if (stageId) {
        queryParam.stageId = stageId;
    }
    if (levelPid) {
        queryParam.levelPid = levelPid;
    }
    if (sourceId) {
        queryParam.sourceId = sourceId;
    }
    if (queryPublic) {
        queryParam.queryPublic = queryPublic;
    }
    if (projectId) {
        queryParam.projectId = projectId;
    }

    var deliverys;
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getDeliverys.do',
        type: 'POST',
        data: queryParam,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            deliverys = data;
        }
    });
    return deliverys;
}

var processOption = {
    animation: false,
    tooltip: {
        formatter: function (params) {
            if (params.dataType == 'node') {
                var timeArr = params.value.split(",");
                return '计划结束：' + timeArr[0] + '<br>实际结束：' + timeArr[1];
            } else if (params.dataType == 'edge') {
                var tempArr = params.value.split(",");
                var content = "“" + tempArr[0] + "”阶段交付物：<br>";
                $.ajax({
                    url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProjectFile/queryStageFileList.do?projectId=' + projectId + "&stageId=" + tempArr[1],
                    async: false,
                    success: function (data) {
                        if (data && data.length > 0) {
                            for (var index = 0; index < data.length; index++) {
                                content += data[index].fileName + "<br>";
                            }
                        }
                    }
                });
                return content;
            }
        },
        position: 'bottom'
    },
    series: [
        {
            type: 'graph',
            layout: 'none',
            symbolSize: 16,
            roam: false,
            left: 0,
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
                    color: 'target',
                    opacity: 1,
                    width: 2,
                    curveness: 0
                }
            },
            label: {
                show: true
            }
        }
    ]
};

function processShow(json, plans) {
    var projectProcess = json.projectProcess;
    var currentStageNo = json.currentStageNo;
    if (json.knotTime) {
        currentStageNo = '999';
    }
    var whetherDelay = json.whetherDelay;
    var allWidth = $("#processDiv").width() - 50;
    processOption.series[0].width = $("#processDiv").width() - 50;
    var startPoint = {
        x: 0,
        y: 200,
        symbol: 'none'
    };
    processOption.series[0].data = [];
    processOption.series[0].data.push(startPoint);
    processOption.series[0].links = [];
    var sum = 0;
    for (var i = 0; i < projectProcess.length; i++) {
        var dataObj = {y: 200};
        dataObj.name = projectProcess[i].stageName + '(' + projectProcess[i].stagePercent + '%)';
        sum += parseInt(projectProcess[i].stagePercent, 10);
        dataObj.x = sum * allWidth / 100;
        dataObj.label = {show: true, distance: 10};
        dataObj.label.position = i % 2 == 0 ? 'top' : 'bottom';
        dataObj.itemStyle = {};
        if (projectProcess[i].stageNo < currentStageNo) {
            dataObj.itemStyle.color = '#32CD32';
        } else if (projectProcess[i].stageNo == currentStageNo) {
            if (whetherDelay == 2) {
                dataObj.itemStyle.color = '#fb0808';
            } else if (whetherDelay == 1) {
                dataObj.itemStyle.color = '#EEEE00';
            } else if (whetherDelay == 3) {
                dataObj.itemStyle.color = '#cccccc';
            } else {
                dataObj.itemStyle.color = '#C6E2FF';
            }
        } else {
            dataObj.itemStyle.color = '#cccccc';
        }
        if (plans[i].planEndTime) {
            dataObj.value = plans[i].planEndTime;
        } else {
            dataObj.value = '-';
        }
        if (plans[i].actualEndTime) {
            dataObj.value += "," + plans[i].actualEndTime;
        } else {
            dataObj.value += ",-";
        }

        processOption.series[0].data.push(dataObj);
        //链路
        var linkObj = {};
        linkObj.source = i;
        linkObj.target = i + 1;
        linkObj.value = projectProcess[i].stageName + "," + projectProcess[i].stageId;
        if (projectProcess[i].stageNo == currentStageNo) {
            linkObj.lineStyle = {normal: {type: 'dashed'}};
        }
        processOption.series[0].links.push(linkObj);
    }
    processEchart.setOption(processOption);
}

function expandFileAll() {
    treegridFileInfo.expandAll();
}

function collapseFileAll() {
    treegridFileInfo.collapseAll();
}

function refreshFile() {
    treegridFileInfo.reload();
    expandTree();
}

function queryFileByName() {
    var key = $.trim(mini.get("fileNameFilter").getValue());
    if (key == "") {
        treegridFileInfo.clearFilter();
    } else {
        key = key.toLowerCase();
        treegridFileInfo.filter(function (node) {
            var text = node.fileName ? node.fileName.toLowerCase() : "";
            if (text.indexOf(key) != -1) {
                return true;
            }
        });
    }
}

function addAchievement() {
    var row = {};
    grid_project_achievement.addRow(row);
}

function delAchievement() {
    var selecteds = grid_project_achievement.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_project_achievement.removeRows(deleteArr);
}

//TODO 同步PDM中的文件,传递projectId,currentStageId,deliveryIds
function syncPDM() {
    //同步成功之后，先将原有的数据删除，再插入新的
}

//发起文件审批
function startFileApproval() {
    mini.confirm("当前阶段所有“未提交”的文件，将按照审批流程类型创建审批单（项目负责人可发起所有文件的审批，项目成员仅能发起本人上传的文件）。<br/>" +
        "发起流程后文件不可删除，且只有审批完成后才能继续推进项目流程，确定继续？", "提醒",
        function (action) {
            if (action == "ok") {
                showLoading();
                //对于项目管理人员来说，可以补录过去阶段的项目，并发起审批（如果不选补录的阶段，默认查找当前阶段的）
                var currentStageId = mini.get("currentStageId").getValue();
                var buluStage = mini.get("selectStage").getValue();
                if (buluStage) {
                    currentStageId = buluStage;
                }
                $.ajax({
                    url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/startDeliveryApproval.do?projectId=' + projectId
                        + "&stageId=" + currentStageId + "&mainDepId=" + mini.get("mainDepId").getValue()
                        + "&mainDeptName=" + mini.get("mainDepId").getText() + "&currentUserProjectRoleName=" + currentUserProjectRoleName,
                    type: 'get',
                    contentType: 'application/json',
                    success: function (result) {
                        if (result) {
                            mini.alert(result.message);
                            refreshFile();
                        }
                    },
                    complete: function () {
                        hideLoading();
                    }
                });
            }
        }
    );
}

//项目管理人员进行文件补录，不传stageId，则返回所有的项目阶段
function getPassStage() {
    var currentCategoryId = mini.get("projectCategory").getValue();
    var stageId = mini.get("currentStageId").getValue();
    $.post(
        jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getPassStage.do',
        {categoryId: currentCategoryId, stageId: ""},
        function (data) {
            mini.get("selectStage").setData(data);
        });
}

function findRoleNameById(roleId) {
    for (var i = 0; i < memRoleList.length; i++) {
        if (memRoleList[i].roleId == roleId) {
            return memRoleList[i].roleName;
        }
    }
    return "";
}


function addOrEditInput(record) {
    if (!projectId) {
        mini.alert("请先保存项目！");
        return;
    }
    if (!record) {
        record = {projectId: projectId};
    }

    mini.open({
        title: "输入编辑",
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/inputEditPage.do",
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            iframe.contentWindow.SetData(record);
        },
        ondestroy: function (action) {
            grid_project_input.reload();
        }
    });
}


function deleteInput() {
    var rows = grid_project_input.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/deleteInput.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        grid_project_input.reload();
                    }
                });
            }
        }
    });
}

function detailInput(url) {
    window.open(jsUseCtxPath + url);
}

//todo:成果详情入口链接(老李mark暂留)
function showOutLinkPage(outPlanId, projectId, canOperateFile, typeName, belongSubSysKey) {
    if (!outPlanId) {
        mini.alert("请先保存此条计划！");
        return;
    }
    if (!projectId) {
        mini.alert("请先保存此项目！");
        return;
    }
    if (!typeName) {
        mini.alert("请先选择类别！");
        return;
    }

    mini.open({
        title: "成果产出详情",
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/outListPage.do?outPlanId=" + outPlanId + "&projectId=" + projectId + "&canOperateFile=" + canOperateFile + "&typeName=" + typeName + "&belongSubSysKey=" + belongSubSysKey,
        width: 1500,
        height: 600,
        showModal: true,
        allowResize: true
    });
}

//对于交付物管理进来的，限定为仅能选择当前阶段
function checkStageSelect() {
    if (action != 'editDelivery') {
        return;
    }
    var selectStage = mini.get("selectStage").getValue();
    var currentStageId = mini.get("currentStageId").getValue();
    if (selectStage < currentStageId) {
        mini.alert("不允许选择之前的阶段，请联系项目管理人员通过“变更”操作处理！");
        mini.get("selectStage").setValue("");
    }
}

function stageEvaluateClick(userId, roleName) {
    if (projectId && userId) {
        //在职状态判断
        var userInfo = getUserDutyStatus(userId);
        if (userInfo && userInfo.STATUS_ == "OUT_JOB") {
            mini.confirm("当前人员已离职！点击“确定”可继续当前操作。", "提示", function (action) {
                    if (action != "ok") {
                        return;
                    } else {
                        executeEvalute(userId, roleName);
                    }
                }
            );
        } else {
            executeEvalute(userId, roleName);
        }
    }
}

function executeEvalute(userId, roleName) {
    mini.get("evaluateUserId").setValue(userId);
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryStageEvaluateList.do?projectId=' + projectId + "&userId=" + userId,
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                evaluateList.setData(data);
            }
        }
    });
    mini.get("addEvaluateBtn").setEnabled(false);
    mini.get("delEvaluateBtn").setEnabled(false);
    mini.get("saveEvaluateBtn").setEnabled(false);
    if (action == 'task' && stageRoleRatioEvaluate == 'yes') {
        if (editProRespPjScore == '1') {
            if (roleName == '项目负责人') {
                mini.get("addEvaluateBtn").setEnabled(true);
                mini.get("delEvaluateBtn").setEnabled(true);
                mini.get("saveEvaluateBtn").setEnabled(true);
            } else {
                mini.get("addEvaluateBtn").setEnabled(false);
                mini.get("delEvaluateBtn").setEnabled(false);
                mini.get("saveEvaluateBtn").setEnabled(false);
            }
        } else {
            mini.get("addEvaluateBtn").setEnabled(true);
            mini.get("delEvaluateBtn").setEnabled(true);
            mini.get("saveEvaluateBtn").setEnabled(true);
        }
    }
    evaluateWindow.show();
}
function closeEvaluate() {
    evaluateWindow.hide();
}

function addEvaluate() {
    var currentStageId = mini.get("currentStageId").getValue();
    var currentStageName = mini.get("currentStageName").getValue();
    if (!currentStageId) {
        mini.alert("项目当前阶段为空！");
        return;
    }
    var data = evaluateList.getData();
    for (var index = 0; index < data.length; index++) {
        if (data[index].stageId == currentStageId) {
            mini.alert("当前阶段已存在！");
            return;
        }
    }
    var row = {stageId: currentStageId, stageName: currentStageName};
    evaluateList.addRow(row, 0);
}

function delEvaluate() {
    var selected = evaluateList.getSelected();
    if (!selected) {
        mini.alert("请选择当前阶段的数据！");
        return;
    }
    var currentStageId = mini.get("currentStageId").getValue();
    if (selected.stageId != currentStageId) {
        mini.alert("请选择当前阶段的数据！");
        return;
    }
    evaluateList.removeRow(selected);
}

function saveEvaluate() {
    var changes = evaluateList.getChanges();
    if (changes.length == 0) {
        mini.alert("保存成功", "提示", function () {
            var evaluateUserId = mini.get("evaluateUserId").getValue();
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryStageEvaluateList.do?projectId=' + projectId + "&userId=" + evaluateUserId,
                type: 'get',
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        evaluateList.setData(data);
                    }
                }
            });
        });
        return;
    }
    //校验数据正确性
    for (var i = 0; i < changes.length; i++) {
        if (!changes[i].gznr || !changes[i].gzzl || !changes[i].cxnl || !changes[i].zdx || !changes[i].gsnr) {
            mini.alert("请填写必填项！");
            return;
        }
    }
    //更新数据
    var postData = mini.encode({
        gridChange: changes,
        projectIdVal: projectId,
        userId: mini.get("evaluateUserId").getValue()
    });
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/saveStageEvaluate.do',
        type: 'POST',
        data: postData,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                if (data.success) {
                    mini.alert("保存成功", "提示", function () {
                        var evaluateUserId = mini.get("evaluateUserId").getValue();
                        $.ajax({
                            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryStageEvaluateList.do?projectId=' + projectId + "&userId=" + evaluateUserId,
                            type: 'get',
                            contentType: 'application/json',
                            success: function (data) {
                                if (data) {
                                    evaluateList.setData(data);
                                }
                            }
                        });
                    });
                } else {
                    mini.alert("保存失败！" + data.message);
                }
            }
        }
    });
}
