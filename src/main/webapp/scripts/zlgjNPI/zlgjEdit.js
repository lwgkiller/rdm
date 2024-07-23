var isBianzhi = "";
var isBianzhiReason = "";
var isBianzhiSP = "";
var isBianzhiXG = "";
var reasonTypeList = "";
var isSHBianzhi = "";
var isZGBianzhi = "";
var isZrbmfzr = "";
var confirmRisk = "";
var selectLeader = "";
var recheckRisk = "";
var checkRisk = "";
var selectRes = "";
$(function () {
    initForm();
    queryReason();
    var ifgj = $.trim(mini.get("ifgj").getValue());
    if (ifgj == 'no') {
        $("#noReason textarea").css("background-color", "#F59E9E");
        $("#sfxygj").css("background-color", "#F59E9E");
        grid_zlgj_reasonInfo.setAllowCellEdit(false);
        grid_zlgj_linshicuoshi.setAllowCellEdit(false);
        grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
        grid_zlgj_fanganyz.setAllowCellEdit(false);
        grid_zlgj_fanganjj.setAllowCellEdit(false);
        grid_zlgj_riskUser.setAllowCellEdit(false);
        grid_zlgj_risk.setAllowCellEdit(false);
        $("#zlgjRiskUserButtons").hide();
        $("#zlgjRiskButtons").hide();
        $("#projectMemberButtons").hide();
        $("#projectAchievementButtons").hide();
        $("#zlgjYanzhengButtons").hide();
        $("#zlgjFanganButtons").hide();
        $("#zlgjFanganjjButtons").hide();
    }
    var sffgsp = $.trim(mini.get("sffgsp").getValue());
    if (!sffgsp) {
        mini.get("sffgsp").setValue('NO');
    }
    //明细入口1
    if (action == 'detail') {
        detailActionProcess(true);
    } else if (action == 'task') {
        taskActionProcess();
        if (isBianzhi != 'yes') {
            mini.get("wtlx").setEnabled(false);
        }
    } else if (action == 'edit') {
        var wtlx = $.trim(mini.get("wtlx").getValue());
        if (!wtlx) {
            mini.get("wtlx").setValue(wtlxtype);
        }
        edit1Process();
        if (ifCopy == "yes") {
            mini.get("jiXing").setValue(copyZlgjObj.jiXing);
            mini.get("smallJiXing").setValue(copyZlgjObj.smallJiXing);
            mini.get("zjbh").setValue(copyZlgjObj.zjbh);
            mini.get("wtms").setValue(copyZlgjObj.wtms);
            mini.get("jjcd").setValue(copyZlgjObj.jjcd);
            mini.get("sggk").setValue(copyZlgjObj.sggk);
            mini.get("jjsl").setValue(copyZlgjObj.jjsl);
            mini.get("gzsl").setValue(copyZlgjObj.gzsl);
            mini.get("gzl").setValue(copyZlgjObj.gzl);
            mini.get("zrcpzgId").setValue(copyZlgjObj.zrcpzgId);
            mini.get("zrcpzgId").setText(copyZlgjObj.zrcpzgName);
            mini.get("ssbmId").setValue(copyZlgjObj.ssbmId);
            mini.get("ssbmId").setText(copyZlgjObj.ssbmName);
        }
    } else if (action == "change") {
        formZlgj.setEnabled(true);
        $("#detailToolBar").show();
        $("#changeInfo").show();
    }

    var wtlx = mini.get("wtlx").getValue();
    if (wtlx && "XPZDSY" == wtlx) {
        $("#tdm_sylx_td1").show();
        $("#tdm_sylx_td2").show();
    }
    if (wtlx && "HWWT" == wtlx) {
        $("#hwxsdqtd1").show();
        $("#hwxsdqtd2").show();
    }
    if (wtlx && ("XPZDSY" == wtlx || "XPSZ" == wtlx || "XPLS" == wtlx || "WXBLX" == wtlx)) {
        $("#dateData").show();
        $("#gzData").show();
    }
    if (wtlx && "LBJSY" == wtlx) {
        mini.get("componentCategory").setEnabled(false);
        mini.get("componentName").setEnabled(false);
        mini.get("testType").setEnabled(false);
        mini.get("completeTestMonth").setEnabled(false);
        mini.get("testRounds").setEnabled(false);
        mini.get("sampleType").setEnabled(false);
        mini.get("testLeaderId").setEnabled(false);
        mini.get("lbjgys").setEnabled(false);
        $("#lbjTr1").show();
        $("#lbjTr2").show();
        $("#lbjTr3").show();
        $("#lbjTr4").show();
        $("#lbjTr5").show();
        $("#zlgjTr1").hide();
        $("#zlgjTr2").hide();
        $("#zlgjTr3").hide();
        $("#zlgjTr4").hide();
        $("#zlgjTr5").hide();

    }
});

function changeInfo() {
    var formData = _GetFormJsonMini("formZlgj");
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        formData.changeReasonData = grid_zlgj_reasonInfo.getChanges();
    }
    if (grid_zlgj_linshicuoshi.getChanges().length > 0) {
        formData.changeLscsData = grid_zlgj_linshicuoshi.getChanges();
    }
    if (grid_zlgj_yanzhengInfo.getChanges().length > 0) {
        formData.changeYyyzData = grid_zlgj_yanzhengInfo.getChanges();
    }
    if (grid_zlgj_fanganyz.getChanges().length > 0) {
        formData.changeFayzData = grid_zlgj_fanganyz.getChanges();
    }
    if (grid_zlgj_fanganjj.getChanges().length > 0) {
        formData.changeFajjData = grid_zlgj_fanganjj.getChanges();
    }
    if (grid_zlgj_riskUser.getChanges().length > 0) {
        formData.changeRiskUserData = grid_zlgj_riskUser.getChanges();
    }
    if (grid_zlgj_risk.getChanges().length > 0) {
        formData.changeRiskData = grid_zlgj_risk.getChanges();
    }
    var json = mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/saveZlgj.do',
        type: 'post',
        async: false,
        data: json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    window.location.reload();
                } else {
                    mini.alert(zlgjEdit_name1 + data.message);
                }
            }
        }
    });
}

//保存草稿
function saveZlgj(e) {
    var wtlx = $.trim(mini.get("wtlx").getValue())
    if (!wtlx) {
        mini.alert(zlgjEdit_name2);
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startZlgjProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    checkFile('pic');
    if (!checkFileResult.result) {
        mini.alert(checkFileResult.message);
        return;
    }
    window.parent.startProcess(e);
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var wtlx = $.trim(mini.get("wtlx").getValue());
    if (!wtlx) {
        return {"result": false, "message": zlgjEdit_name3};
    }
    var jjcd = $.trim(mini.get("jjcd").getValue());
    if (!jjcd) {
        return {"result": false, "message": zlgjEdit_name4};
    }
    var zrcpzgId = $.trim(mini.get("zrcpzgId").getValue());
    if (!zrcpzgId) {
        return {"result": false, "message": zlgjEdit_name13};
    }
    var ssbmId = $.trim(mini.get("ssbmId").getValue());
    if (!ssbmId) {
        return {"result": false, "message": zlgjEdit_name14};
    }
    var lbjgys = $.trim(mini.get("lbjgys").getValue());
    if (!lbjgys) {
        return {"result": false, "message": zlgjEdit_name16};
    }
    var improvementMethod = $.trim(mini.get("improvementMethod").getValue());
    if (!improvementMethod) {
        return {"result": false, "message": zlgjEdit_name22};
    }
    var yzcd = $.trim(mini.get("yzcd").getValue());
    if (!yzcd) {
        return {"result": false, "message": zlgjEdit_name23};
    }
    var gjyq = $.trim(mini.get("gjyq").getValue());
    if (!gjyq) {
        return {"result": false, "message": zlgjEdit_name21};
    }
    if(wtlx!="LBJSY"){
        var jiXing = $.trim(mini.get("jiXing").getValue());
        if (!jiXing) {
            return {"result": false, "message": zlgjEdit_name5};
        }
        var smallJiXing = $.trim(mini.get("smallJiXing").getValue());
        if (!smallJiXing) {
            return {"result": false, "message": zlgjEdit_name6};
        }
        var wtms = $.trim(mini.get("wtms").getValue());
        if (!wtms) {
            return {"result": false, "message": zlgjEdit_name7};
        }
        var gzxt = $.trim(mini.get("gzxt").getValue());
        if (!gzxt) {
            return {"result": false, "message": zlgjEdit_name8};
        }
        var gzbw = $.trim(mini.get("gzbw").getValue());
        if (!gzbw) {
            return {"result": false, "message": zlgjEdit_name9};
        }
        var gzlj = $.trim(mini.get("gzlj").getValue());
        if (!gzlj) {
            return {"result": false, "message": zlgjEdit_name10};
        }
        var zjbh = $.trim(mini.get("zjbh").getValue());
        if (!zjbh) {
            return {"result": false, "message": zlgjEdit_name11};
        }
        var gzxs = $.trim(mini.get("gzxs").getValue());
        if (!gzxs) {
            return {"result": false, "message": zlgjEdit_name12};
        }

        // @mh新增7个必填项
        var sggk = $.trim(mini.get("sggk").getValue());
        if (!sggk) {
            return {"result": false, "message": zlgjEdit_name15};
        }


        var jjsl = $.trim(mini.get("jjsl").getValue());
        if (!jjsl) {
            return {"result": false, "message": zlgjEdit_name17};
        }

        var gzsl = $.trim(mini.get("gzsl").getValue());
        if (!gzsl) {
            return {"result": false, "message": zlgjEdit_name18};
        }

        var wtpcjc = $.trim(mini.get("wtpcjc").getValue());
        if (!wtpcjc) {
            return {"result": false, "message": zlgjEdit_name19};
        }

        var xcczfa = $.trim(mini.get("xcczfa").getValue());
        if (!xcczfa) {
            return {"result": false, "message": zlgjEdit_name20};
        }



        // var yzTime = $.trim(mini.get("yzTime").getValue());
        // if (!yzTime) {
        //     return {"result": false, "message": zlgjEdit_name24};
        // }
        // var yjTime = $.trim(mini.get("yjTime").getValue());
        // if (!yjTime) {
        //     return {"result": false, "message": zlgjEdit_name25};
        // }
        var pfbz = $.trim(mini.get("pfbz").getValue());
        if (!pfbz) {
            return {"result": false, "message": zlgjEdit_name26};
        }
        if (wtlx && "XPZDSY" == wtlx) {
            var tdmSylx = mini.get("tdmSylx").getValue();
            if (!tdmSylx) {
                return {"result": false, "message": zlgjEdit_name27};
            }
        }
        if (wtlx && ("XPZDSY" == wtlx || "XPSZ" == wtlx || "XPLS" == wtlx || "WXBLX" == wtlx)) {
            var gzProgram = mini.get("gzProgram").getValue();
            if (!gzProgram) {
                return {"result": false, "message": "请填写故障现象"};
            }
        }
    }else {
        var componentModel = $.trim(mini.get("componentModel").getValue());
        if (!componentModel) {
            return {"result": false, "message": "请选择零部件型号"};
        }
    }

    return {"result": true};
}

//流程中暂存信息（如编制阶段）
function saveZlgjInProcess() {
    var formData = _GetFormJsonMini("formZlgj");
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        formData.changeReasonData = grid_zlgj_reasonInfo.getChanges();
    }
    if (grid_zlgj_linshicuoshi.getChanges().length > 0) {
        formData.changeLscsData = grid_zlgj_linshicuoshi.getChanges();
    }
    if (grid_zlgj_yanzhengInfo.getChanges().length > 0) {
        formData.changeYyyzData = grid_zlgj_yanzhengInfo.getChanges();
    }
    if (grid_zlgj_fanganyz.getChanges().length > 0) {
        formData.changeFayzData = grid_zlgj_fanganyz.getChanges();
    }
    if (grid_zlgj_fanganjj.getChanges().length > 0) {
        formData.changeFajjData = grid_zlgj_fanganjj.getChanges();
    }
    if (grid_zlgj_riskUser.getChanges().length > 0) {
        formData.changeRiskUserData = grid_zlgj_riskUser.getChanges();
    }
    if (grid_zlgj_risk.getChanges().length > 0) {
        formData.changeRiskData = grid_zlgj_risk.getChanges();
    }
    var json = mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/saveZlgj.do',
        type: 'post',
        async: false,
        data: json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    window.location.reload();
                } else {
                    mini.alert(zlgjEdit_name1 + data.message);
                }
            }
        }
    });
}

//流程中的审批或者下一步
function zlgjApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        checkFile('pic');
        if (!checkFileResult.result) {
            mini.alert(checkFileResult.message);
            return;
        }
    }
    if (isBianzhiReason == 'yes') {
        var ifgj = $.trim(mini.get("ifgj").getValue());
        var ifba = $.trim(mini.get("ifba").getValue());
        var togetherNum = $.trim(mini.get("togetherNum").getValue());
        if (!ifba) {
            mini.alert("请选择是否并案!");
            return;
        }
        if (ifba == 'yes') {
            if (!togetherNum) {
                mini.alert("请选择追溯问题编号!");
                return;
            }
        } else {
            if (!ifgj) {
                mini.alert(zlgjEdit_name28);
                return;
            }
            if (ifgj == 'yes') {
                // var formValidt = yzTimeData();
                // if (!formValidt.result) {
                //     mini.alert(formValidt.message);
                //     return;
                // }
                var formValidr = draReason();
                if (!formValidr.result) {
                    mini.alert(formValidr.message);
                    return;
                }
                var formValids = draRLFJ();
                if (!formValids.result) {
                    mini.alert(formValids.message);
                    return;
                }
                checkFile('reason');
                if (!checkFileResult.result) {
                    mini.alert(checkFileResult.message);
                    return;
                }
                // saveZlgjInProcess();
            } else {
                var noReason = $.trim(mini.get("noReason").getValue());
                if (!noReason) {
                    mini.alert(zlgjEdit_name29);
                    return;
                }
                checkNoFile();
                if (!checkNoFileResult.result) {
                    mini.alert(checkNoFileResult.message);
                    return;
                }
            }
        }
    }
    if (isBianzhiSP == 'yes') {
        var formValid = drafgSp();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        // saveZlgjInProcess();
    }
    if (isBianzhiXG == 'yes') {
        var formValid = draXg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        // saveZlgjInProcess();
    }
    if (isSHBianzhi == 'yes') {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var yzcd = $.trim(mini.get("yzcd").getValue())
        if (!yzcd) {
            mini.alert(zlgjEdit_name23);
            return;
        }
    }
    if (isZGBianzhi == 'yes') {
        var formValid = draftOrStartValidZG();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var ifba = $.trim(mini.get("ifba").getValue());
        var togetherNum = $.trim(mini.get("togetherNum").getValue());
        if (!ifba) {
            mini.alert("请选择是否并案!");
            return;
        }
        if (ifba == 'yes') {
            if (!togetherNum) {
                mini.alert("请选择追溯问题编号!");
                return;
            }
        }
    }
    if (isZrbmfzr == 'yes') {
        var ifgj = $.trim(mini.get("ifgj").getValue());
        // if (ifgj == 'no'){
        //     mini.alert("请确认是否需要改进，如果确认不需要改进请点击'不改进&作废'按钮，如果需要改进请点击'驳回'按钮。");
        //     return;
        // }
        var wtlx = $.trim(mini.get("wtlx").getValue());
        if (wtlx == 'XPSZ' || wtlx == 'XPZDSY' || wtlx == 'XPLS' || wtlx == 'WXBLX'||wtlx == 'LBJSY') {
            var formValid = drafgSpNew();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        var ifba = $.trim(mini.get("ifba").getValue());
        var togetherNum = $.trim(mini.get("togetherNum").getValue());
        if (!ifba) {
            mini.alert("请选择是否并案!");
            return;
        }
        if (ifba == 'yes') {
            if (!togetherNum) {
                mini.alert("请选择追溯问题编号!");
                return;
            }
        }
    }
    // if (selectLeader == 'yes') {
    //     var formValid = draSelectLeader();
    //     if (!formValid.result) {
    //         mini.alert(formValid.message);
    //         return;
    //     }
    // }
    if (selectRes == 'yes') {
        var formValid = draSelectUser();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (checkRisk == 'yes' || recheckRisk == 'yes') {
        var formValid = draEditRisk();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (confirmRisk == 'yes') {
        var formValid = draCheck();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    saveZlgjInProcess();
    window.parent.approve();
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formZlgj");
    if (formData.SUB_treegridFileInfo) {
        delete formData.SUB_treegridFileInfo;
    }
    var bigWtlx='';
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    if (formData.wtlx == 'XPSZ' || formData.wtlx == 'XPZDSY' || formData.wtlx == 'XPLS' || formData.wtlx == 'WXBLX'||formData.wtlx == 'LBJSY') {
        bigWtlx = '新产品导入'
    }else {
        bigWtlx = '分析改进'
    }
    formData.bos = [];
    formData.vars = [{key: 'smallJiXing', val: formData.smallJiXing},{key: 'bigWtlx', val: bigWtlx}];
    return formData;
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: zlgjEdit_name30,
        width: 800,
        height: 600
    });
}

function detailActionProcess(showDetailTool) {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    if (showDetailTool) {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
    }
    var ifgj = $.trim(mini.get("ifgj").getValue());
    if (ifgj == 'yes') {
        $("#sfxygj").css("background-color", "#F2F2F2");
    }
}

function edit1Process() {
    mini.get("sffgsp").setEnabled(false);
    mini.get("gjxg").setEnabled(false);
    mini.get("gzr").setEnabled(false);
    mini.get("gjhgzl").setEnabled(false);
    mini.get("sfyx").setEnabled(false);
    mini.get("ifba").setEnabled(false);
    mini.get("togetherNum").setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    mini.get("zrrId").setEnabled(false);
    mini.get("ssbmId").setEnabled(false);
    mini.get("ifgj").setEnabled(false);
    $("#sfxygj").css("background-color", "#F2F2F2");
    mini.get("noReason").setEnabled(false);
    mini.get("issue_part_no").setEnabled(false);
}

function edit3Process() {
    formZlgj.setEnabled(false);
    var yzcd = mini.get("yzcd").getValue();
    if (yzcd != 'A') {
        mini.get("sffgsp").setEnabled(true);
    }
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");

}

function edit4Process() {
    formZlgj.setEnabled(false);
    mini.get("gjxg").setEnabled(true);
    mini.get("gzr").setEnabled(true);
    mini.get("gjhgzl").setEnabled(true);
    mini.get("sfyx").setEnabled(true);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();

    $("#sfxygj").css("background-color", "#F2F2F2");

}

function edit6Process() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");

}

function edit7Process() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");

}

function edit8Process() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");

}

function edit9Process() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");

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
    animation: false,
    tooltip: {
        formatter: function (params) {
            if (params.dataType == 'node') {
                return zlgjEdit_name31 + params.value;
            }
        },
        position: 'inside'
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
            }
        }
    ]
};

//问题原因
function addZlgj_reasonInfoRow() {
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue())
    if (!wtId) {
        mini.alert(zlgjEdit_name32);
        return;
    }
    var row = {};
    grid_zlgj_reasonInfo.addRow(row);
}

//问题原因
function removeZlgj_reasonInfoRow() {
    var selecteds = grid_zlgj_reasonInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var linshicuoshis = grid_zlgj_linshicuoshi.getData();
        for (var j = 0; j < linshicuoshis.length; j++) {
            if (linshicuoshis[j].yyId = selecteds[i].yyId) {
                mini.alert(zlgjEdit_name33);
                return;
            }
        }
        var yanzhengInfos = grid_zlgj_yanzhengInfo.getData();
        for (var j = 0; j < yanzhengInfos.length; j++) {
            if (yanzhengInfos[j].yyId = selecteds[i].yyId) {
                mini.alert(zlgjEdit_name34);
                return;
            }
        }
        var fanganyzs = grid_zlgj_fanganyz.getData();
        for (var j = 0; j < fanganyzs.length; j++) {
            if (fanganyzs[j].yyId = selecteds[i].yyId) {
                mini.alert(zlgjEdit_name35);
                return;
            }
        }
        var fanganjjs = grid_zlgj_fanganjj.getData();
        for (var j = 0; j < fanganjjs.length; j++) {
            if (fanganjjs[j].yyId = selecteds[i].yyId) {
                mini.alert(zlgjEdit_name36);
                return;
            }
        }
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_reasonInfo.removeRows(deleteArr);
}

//临时措施
function addCuoshi() {
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        mini.alert(zlgjEdit_name);
        return;
    }
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    if (reasonData.length == 0) {
        mini.alert(zlgjEdit_name38);
        return
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_linshicuoshi.addRow(row);
}

//临时措施
function delCuoshi() {
    var selecteds = grid_zlgj_linshicuoshi.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.rounds != mini.get("rounds").getValue()) {
            mini.alert(zlgjEdit_name39);
            return;
        }
        deleteArr.push(row);
    }
    grid_zlgj_linshicuoshi.removeRows(deleteArr);
}

//原因验证
function addZlgj_yanzhengInfoRow() {
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        mini.alert(zlgjEdit_name);
        return;
    }
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    if (reasonData.length == 0) {
        mini.alert(zlgjEdit_name38);
        return
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_yanzhengInfo.addRow(row);
}

//原因验证
function removeZlgj_yanzhengInfoRow() {
    var selecteds = grid_zlgj_yanzhengInfo.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.rounds != mini.get("rounds").getValue()) {
            mini.alert(zlgjEdit_name40);
            return;
        }
        deleteArr.push(row);
    }
    grid_zlgj_yanzhengInfo.removeRows(deleteArr);
}

//方案验证
function addyzFangan() {
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        mini.alert(zlgjEdit_name);
        return;
    }
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    if (reasonData.length == 0) {
        mini.alert(zlgjEdit_name38);
        return
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_fanganyz.addRow(row);
}

//方案验证
function delyzFangan() {
    var selecteds = grid_zlgj_fanganyz.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.rounds != mini.get("rounds").getValue()) {
            mini.alert(zlgjEdit_name41);
            return;
        }
        deleteArr.push(row);
    }
    grid_zlgj_fanganyz.removeRows(deleteArr);
}

//永久解决方案
function addjjFangan() {
    if (grid_zlgj_reasonInfo.getChanges().length > 0) {
        mini.alert(zlgjEdit_name);
        return;
    }
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    var reasonData = grid_zlgj_reasonInfo.getData();
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    if (reasonData.length == 0) {
        mini.alert(zlgjEdit_name38);
        return
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_fanganjj.addRow(row);
}

//永久解决方案
function deljjFangan() {
    var selecteds = grid_zlgj_fanganjj.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.rounds != mini.get("rounds").getValue()) {
            mini.alert(zlgjEdit_name42);
            return;
        }
        deleteArr.push(row);
    }
    grid_zlgj_fanganjj.removeRows(deleteArr);
}

//风险排查人员
function addRiskUser() {
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_riskUser.addRow(row);
}

function delRiskUser() {
    var selecteds = grid_zlgj_riskUser.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_zlgj_riskUser.removeRows(deleteArr);
}

//风险排查
function addRisk() {
    //添加之前需要先保存
    var wtId = $.trim(mini.get("wtId").getValue());
    if (!wtId) {
        mini.alert(zlgjEdit_name37);
        return;
    }
    var row = {};
    row.rounds = mini.get("rounds").getValue()
    grid_zlgj_risk.addRow(row);
}

function delRisk() {
    var selecteds = grid_zlgj_risk.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        if (row.CREATE_BY_ != currentUserId) {
            mini.alert(zlgjEdit_name43);
            return;
        }
        deleteArr.push(row);
    }
    grid_zlgj_risk.removeRows(deleteArr);
}

//附件
function addPicFile() {
    var fjlx = "gztp";
    var wtId = mini.get("wtId").getValue();
    if (!wtId) {
        mini.alert(zlgjEdit_name44);
        return;
    }
    var canOperateFile = false;
    if ((action == 'edit' || action == '') || isZGBianzhi == 'yes' || isBianzhi == 'yes' || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function addlbjFile() {
    var componentModel = mini.get("componentModel").getValue();
    if (!componentModel) {
        mini.alert("请选择零部件型号");
        return;
    }
    mini.open({
        title: "测试报告",
        url: jsUseCtxPath + "/xjsdr/core/zlgj/lbjFileWindow.do?businessId=" + componentModel,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//改进不改进附件
function addgjFile() {
    var fjlx = "gjfj";
    var wtId = mini.get("wtId").getValue();
    if (!wtId) {
        mini.alert(zlgjEdit_name44);
        return;
    }
    var canOperateFile = false;
    if (isZGBianzhi == 'yes' || isBianzhiReason == 'yes' || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//临时措施附件
function addlszmclFile(faId, wtId, roundsOfMe, roundsMain) {
    var fjlx = "lszmcl";
    if (!faId) {
        mini.alert(zlgjEdit_name46);
        return;
    }
    if (!wtId) {
        mini.alert(zlgjEdit_name46);
        return;
    }
    var canOperateFile = false;
    if ((isBianzhiReason == 'yes' || isZGBianzhi == 'yes') && (roundsOfMe == roundsMain) || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action +
            "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx + "&faId=" + faId,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//原因验证附件
function addyyzmclFile(faId, wtId, roundsOfMe, roundsMain) {
    var fjlx = "yyzmcl";
    if (!faId) {
        mini.alert(zlgjEdit_name47);
        return;
    }
    if (!wtId) {
        mini.alert(zlgjEdit_name47);
        return;
    }
    var canOperateFile = false;
    if ((isBianzhiReason == 'yes' || isZGBianzhi == 'yes') && (roundsOfMe == roundsMain) || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action +
            "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx + "&faId=" + faId,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//方案验证附件
function addfazmclFile(faId, wtId, roundsOfMe, roundsMain) {
    var fjlx = "fazmcl";
    if (!faId) {
        mini.alert(zlgjEdit_name48);
        return;
    }
    if (!wtId) {
        mini.alert(zlgjEdit_name48);
        return;
    }
    var canOperateFile = false;
    if ((isBianzhiReason == 'yes' || isZGBianzhi == 'yes') && (roundsOfMe == roundsMain) || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action +
            "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx + "&faId=" + faId,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//永久解决方案附件
function addghPicFile(faId, wtId, roundsOfMe, roundsMain) {
    var fjlx = "ghtp";
    if (!faId) {
        mini.alert(zlgjEdit_name49);
        return;
    }
    if (!wtId) {
        mini.alert(zlgjEdit_name49);
        return;
    }
    var canOperateFile = false;
    if ((isBianzhiReason == 'yes' || isZGBianzhi == 'yes') && (roundsOfMe == roundsMain) || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action +
            "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx + "&faId=" + faId,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//改进效果跟踪附件
function addxgFile() {
    var fjlx = "gzxg";
    var wtId = mini.get("wtId").getValue();
    if (!wtId) {
        mini.alert(zlgjEdit_name44);
        return;
    }
    var canOperateFile = false;
    if (isBianzhiXG == 'yes' || action == 'change') {
        canOperateFile = true;
    }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

//风险排查
function addriskFile(faId, wtId, CREATE_BY_, roundsMain) {
    var fjlx = "risk";
    if (!faId) {
        mini.alert(zlgjEdit_name50);
        return;
    }
    if (!wtId) {
        mini.alert(zlgjEdit_name50);
        return;
    }
    var canOperateFile = false;
    if ((CREATE_BY_ == currentUserId && (checkRisk == 'yes' || recheckRisk == 'yes')) || action == 'change') {
        var canOperateFile = true;
    }
    // if (isBianzhiReason == 'yes' && (roundsOfMe == roundsMain)) {
    //     canOperateFile = true;
    // }
    mini.open({
        title: zlgjEdit_name45,
        url: jsUseCtxPath + "/xjsdr/core/zlgj/zlgjFileWindow.do?meetingId=" + wtId + "&action=" + action +
            "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent + "&fjlx=" + fjlx + "&faId=" + faId,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function taskActionProcess() {
    mini.get("issue_part_no").setEnabled(false);
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
        if (nodeVars[i].KEY_ == 'isBianzhiReason') {
            isBianzhiReason = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isBianzhiSP') {
            isBianzhiSP = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isBianzhiXG') {
            isBianzhiXG = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isSHBianzhi') {
            isSHBianzhi = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZGBianzhi') {
            isZGBianzhi = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZrbmfzr') {
            isZrbmfzr = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'selectLeader') {
            selectLeader = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'checkRisk') {
            checkRisk = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'confirmRisk') {
            confirmRisk = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'recheckRisk') {
            recheckRisk = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'selectRes') {
            selectRes = nodeVars[i].DEF_VAL_;
        }
    }
    if (isBianzhi == 'yes') {
        edit1Process();
    } else if (isSHBianzhi == 'yes') {
        edit1SHProcess();
    } else if (isZGBianzhi == 'yes') {
        edit3ZGProcess();
    } else if (isBianzhiReason == 'yes') {
        edit2Process();
    } else if (isBianzhiSP == 'yes') {
        edit3Process();
    } else if (isBianzhiXG == 'yes') {
        edit4Process();
    } else if (isZrbmfzr == 'yes') {
        edit5Process();
    }
    else if (selectLeader == 'yes') {
        edit6Process();
    }
    else if (selectRes == 'yes') {
        edit7Process();
    }
    else if (checkRisk == 'yes' || recheckRisk == 'yes') {
        edit8Process();
    }
    else if (confirmRisk == 'yes') {
        edit9Process();
    } else {
        detailActionProcess(false);
    }
}

function initForm() {
    $.ajaxSettings.async = false;
    if (wtId) {
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getReasonList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_reasonInfo.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getLscsList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_linshicuoshi.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getyzList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_yanzhengInfo.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getfayzList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_fanganyz.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getfajjList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_fanganjj.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getRiskUserList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_riskUser.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getRiskList.do";
        $.post(
            url,
            {ids: wtId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_zlgj_risk.setData(json);
                }
            });
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/getZlgjDetail.do";
        $.post(
            url,
            {wtId: wtId},
            function (json) {
                formZlgj.setData(json);
            });
    }
    $.ajaxSettings.async = true;

}

function draReason() {
    var checkMemberMust = checkMemberRequired(grid_zlgj_reasonInfo.getData(), false);
    if (!checkMemberMust.result) {
        return {"result": false, "message": checkMemberMust.message};
    }
    return {"result": true};
}

//下面四个没用
function draLscs() {
    var checkLscs = checkMemberLscs(grid_zlgj_linshicuoshi.getData(), false);
    if (!checkLscs.result) {
        return {"result": false, "message": checkLscs.message};
    }

    return {"result": true};
}

function draYyyz() {
    var checkYyyz = checkMemberYyyz(grid_zlgj_yanzhengInfo.getData(), false);
    if (!checkYyyz.result) {
        return {"result": false, "message": checkYyyz.message};
    }

    return {"result": true};
}

function draFayz() {
    var checkFayz = checkMemberFayz(grid_zlgj_fanganyz.getData(), false);
    if (!checkFayz.result) {
        return {"result": false, "message": checkFayz.message};
    }

    return {"result": true};
}

function draFajj() {
    var checkFajj = checkMemberFajj(grid_zlgj_fanganjj.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    return {"result": true};
}

//上面四个没用
function draSelectLeader() {
    var checkFajj = checkLeader(grid_zlgj_riskUser.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    return {"result": true};
}

function draSelectUser() {
    var checkFajj = checkRes(grid_zlgj_riskUser.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    var judgeResult = judgeRes(grid_zlgj_riskUser.getData());
    if (!judgeResult) {
        return {"result": false, "message": "无法跨部门选择责任人(室主任)!"};
    }
    return {"result": true};
}

function draEditRisk() {
    var checkFajj = checkRisks(grid_zlgj_risk.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    return {"result": true};
}

function draCheck() {
    var checkFajj = checkCheck(grid_zlgj_riskUser.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    return {"result": true};
}

//todo:注意，这里改为每轮都有相应信息，而不是暴力检查有没有数据
function draRLFJ() {
    var checkLscs = checkMemberLscs(grid_zlgj_linshicuoshi.getData(), false);
    if (!checkLscs.result) {
        return {"result": false, "message": checkLscs.message};
    }
    var checkYyyz = checkMemberYyyz(grid_zlgj_yanzhengInfo.getData(), false);
    if (!checkYyyz.result) {
        return {"result": false, "message": checkYyyz.message};
    }
    var checkFayz = checkMemberFayz(grid_zlgj_fanganyz.getData(), false);
    if (!checkFayz.result) {
        return {"result": false, "message": checkFayz.message};
    }
    var checkFajj = checkMemberFajj(grid_zlgj_fanganjj.getData(), false);
    if (!checkFajj.result) {
        return {"result": false, "message": checkFajj.message};
    }
    return {"result": true};

}

function checkMemberLscs(rowData, checkAll) {
    var isThisRoundsHave = false;
    for (var i = 0; i < rowData.length; i++) {
        if (rowData[i].rounds == mini.get("rounds").getValue()) {
            isThisRoundsHave = true;
        }
    }
    if (!isThisRoundsHave) {
        return {result: false, message: zlgjEdit_name51};
    }
    if (grid_zlgj_linshicuoshi.getChanges().length > 0) {
        return {result: false, message: zlgjEdit_name52};
    }
    //
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: zlgjEdit_name53};
        }
        if (!row.dcfa) {
            return {result: false, message: zlgjEdit_name54};
        }
        if (!row.ddpc) {
            return {result: false, message: zlgjEdit_name55};
        }
        // if (!row.zrr) {
        //     return {result: false, message: '请选择临时措施中的“责任人”'};
        // }
        if (!row.wcTime) {
            return {result: false, message: zlgjEdit_name56};
        }
        if (!row.tzdh) {
            return {result: false, message: zlgjEdit_name57};
        }
    }
    return {result: true};
}

function checkMemberYyyz(rowData, checkAll) {
    //只有七步法才进行必输验证
    if (mini.get("improvementMethod").getValue() == '七步法') {
        var isThisRoundsHave = false;
        for (var i = 0; i < rowData.length; i++) {
            if (rowData[i].rounds == mini.get("rounds").getValue()) {
                isThisRoundsHave = true;
            }
        }
        if (!isThisRoundsHave) {
            return {result: false, message: zlgjEdit_name58};
        }
        if (grid_zlgj_yanzhengInfo.getChanges().length > 0) {
            return {result: false, message: zlgjEdit_name58};
        }
        //
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: zlgjEdit_name59};
        }
        if (!row.jcjg) {
            return {result: false, message: zlgjEdit_name60};
        }
        if (!row.jielun) {
            return {result: false, message: zlgjEdit_name61};
        }

    }
    return {result: true};
}

function checkMemberFayz(rowData, checkAll) {
    //只有七步法才进行必输验证
    if (mini.get("improvementMethod").getValue() == '七步法') {
        var isThisRoundsHave = false;
        for (var i = 0; i < rowData.length; i++) {
            if (rowData[i].rounds == mini.get("rounds").getValue()) {
                isThisRoundsHave = true;
            }
        }
        if (!isThisRoundsHave) {
            return {result: false, message: zlgjEdit_name62};
        }
        if (grid_zlgj_fanganyz.getChanges().length > 0) {
            return {result: false, message: zlgjEdit_name63};
        }
        //
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: zlgjEdit_name64};
        }
        if (!row.gjfa) {
            return {result: false, message: zlgjEdit_name65};
        }
        // if (!row.yzzrr) {
        //     return {result: false, message: '请选择方案验证中的“验证责任人”'};
        // }
        if (!row.sjTime) {
            return {result: false, message: zlgjEdit_name66};
        }
        if (!row.yzjg) {
            return {result: false, message: zlgjEdit_name67};
        }

    }
    return {result: true};
}

function checkMemberFajj(rowData, checkAll) {
    if (!rowData) {
        return {result: false, message: zlgjEdit_name68};
    }
    //
    var isThisRoundsHave = false;
    for (var i = 0; i < rowData.length; i++) {
        if (rowData[i].rounds == mini.get("rounds").getValue()) {
            isThisRoundsHave = true;
        }
    }
    if (!isThisRoundsHave) {
        return {result: false, message: zlgjEdit_name69};
    }
    //
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.yyId) {
            return {result: false, message: zlgjEdit_name70};
        }
        if (!row.cqcs) {
            return {result: false, message: zlgjEdit_name71};
        }
        if (!row.wcTime) {
            return {result: false, message: zlgjEdit_name72};
        }
        if (!row.tzdh) {
            return {result: false, message: zlgjEdit_name73};
        }
        if (!row.yjqhch) {
            return {result: false, message: zlgjEdit_name74};
        }

    }
    return {result: true};
}

//
function checkMemberRequired(rowData, checkAll) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: zlgjEdit_name75};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.reason) {
            return {result: false, message: zlgjEdit_name76};
        }
    }
    return {result: true};
}

function checkLeader(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: zlgjEdit_name77};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.leaderId) {
            return {result: false, message: zlgjEdit_name78};
        }
    }
    return {result: true};
}

function checkRes(rowData) {
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.resId && row.leaderId == currentUserId) {
            return {result: false, message: '请选择风险排查相关人员选择中产品所长为' + row.leaderName + '的“风险排查负责人”'};
        }
    }
    return {result: true};
}

function checkRisks(rowData) {
    var checkType = 'risk';
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.riskContent) {
            return {result: false, message: zlgjEdit_name79};
        }
        if (!row.riskLevel) {
            return {result: false, message: zlgjEdit_name80};
        }
        if (row.riskLevel != '无风险') {
            if (!row.solution) {
                return {result: false, message: zlgjEdit_name81};
            }
            if (!row.market) {
                return {result: false, message: zlgjEdit_name82};
            }
            if (!row.handle) {
                return {result: false, message: zlgjEdit_name83};
            }
            if (!row.noticeNo) {
                return {result: false, message: zlgjEdit_name84};
            }
        }
    }

    if (grid_zlgj_risk.getChanges().length > 0) {
        mini.alert(zlgjEdit_name85);
        return;
    }
    var isSelect = false;
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (row.CREATE_BY_ == currentUserId) {
            isSelect = true;
            break;
        }
    }
    if (!rowData || rowData.length <= 0 || !isSelect) {
        return {result: false, message: zlgjEdit_name86};
    }
    checkFile(checkType);
    if (!checkFileResult.result) {
        mini.alert(checkFileResult.message);
        return;
    }
    return {result: true};
}

function checkCheck(rowData) {
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.qualified) {
            return {result: false, message: zlgjEdit_name87};
        }
        if (!row.unqualified && row.qualified == '不通过') {
            return {result: false, message: zlgjEdit_name88};
        }
    }
    return {result: true};
}

function drafgSp() {
    var sffgsp = $.trim(mini.get("sffgsp").getValue())
    if (!sffgsp) {
        return {"result": false, "message": zlgjEdit_name89};
    }
    return {"result": true};
}

function draXg() {
    var sfyx = $.trim(mini.get("sfyx").getValue())
    if (!sfyx) {
        return {"result": false, "message": zlgjEdit_name91};
    }
    var gjxg = $.trim(mini.get("gjxg").getValue())
    if (!gjxg) {
        return {"result": false, "message": zlgjEdit_name90};
    }
    var gzr = $.trim(mini.get("gzr").getValue())
    if (!gzr) {
        return {"result": false, "message": "请填写跟踪人并保存数据"};
    }
    if (sfyx == 'YES') {
        // var gjhgzl = $.trim(mini.get("gjhgzl").getValue())
        // if (!gjhgzl) {
        //     return {"result": false, "message": "请填写改进后故障率并保存数据"};
        // }
        // checkFile('gzxg');
        // if (!checkFileResult.result) {
        //     return {"result": false, "message": checkFileResult.message};
        // }
    }
    return {"result": true};
}

//reason下拉数据
function queryReason() {
    var reasonData = grid_zlgj_reasonInfo.getData();
    var yyIds = [];
    for (var i = 0, l = reasonData.length; i < l; i++) {
        var r = reasonData[i];
        yyIds.push(r.yyId);
    }
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/reasonListQuery.do',
        type: 'get',
        async: false,
        contentType: 'application/json',
        data: {ids: yyIds.join(',')},
        success: function (data) {
            if (data) {
                reasonTypeList = data;
            }
        }
    });
}

function yzTimeData() {
    var yzTime = $.trim(mini.get("yzTime").getValue())
    if (!yzTime) {
        return {"result": false, "message": zlgjEdit_name24};
    }
    var yjTime = $.trim(mini.get("yjTime").getValue())
    if (!yjTime) {
        return {"result": false, "message": zlgjEdit_name25};
    }
    return {"result": true};
}

function edit1SHProcess() {
    mini.get("sffgsp").setEnabled(false);
    mini.get("gjxg").setEnabled(false);
    mini.get("gzr").setEnabled(false);
    mini.get("gjhgzl").setEnabled(false);
    mini.get("sfyx").setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    mini.get("zrrId").setEnabled(false);
    mini.get("ssbmId").setEnabled(false);
    mini.get("ifgj").setEnabled(false);
    $("#sfxygj").css("background-color", "#F2F2F2");
    mini.get("noReason").setEnabled(false);
}

function edit3ZGProcess() {
    formZlgj.setEnabled(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    mini.get("zrrId").setEnabled(true);
    mini.get("pfbz").setEnabled(true);
    mini.get("yzTime").setEnabled(true);
    mini.get("yjTime").setEnabled(true);
    mini.get("ifgj").setEnabled(true);
    mini.get("ifba").setEnabled(true);
    mini.get("togetherNum").setEnabled(true);
    var ifgj = $.trim(mini.get("ifgj").getValue());
    if (ifgj == 'no') {
        mini.get("noReason").setEnabled(true);
    }
    var yzcd = mini.get("yzcd").getValue();
    if (yzcd != 'A') {
        mini.get("sffgsp").setEnabled(true);
    }
}

function edit2Process() {
    formZlgj.setEnabled(false);
    //非草稿放开流程信息查看按钮
    if (status != 'DRAFTED') {
        $("#processInfo").show();
    }
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    // mini.get("zrrId").setEnabled(true);
    mini.get("pfbz").setEnabled(true);
    mini.get("yzTime").setEnabled(true);
    mini.get("yjTime").setEnabled(true);
    mini.get("ifgj").setEnabled(true);
    mini.get("ifba").setEnabled(true);
    mini.get("togetherNum").setEnabled(true);
    var ifgj = $.trim(mini.get("ifgj").getValue());
    if (ifgj == 'no') {
        mini.get("noReason").setEnabled(true);
        // $("#bgjly").css("background-color","#f23806");
    }
    var yzcd = mini.get("yzcd").getValue();
    if (yzcd != 'A') {
        mini.get("sffgsp").setEnabled(true);
    }
    mini.get("issue_part_no").setEnabled(true);
}

function edit5Process() {
    formZlgj.setEnabled(false);
    grid_zlgj_reasonInfo.setAllowCellEdit(false);
    grid_zlgj_linshicuoshi.setAllowCellEdit(false);
    grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
    grid_zlgj_fanganyz.setAllowCellEdit(false);
    grid_zlgj_fanganjj.setAllowCellEdit(false);
    grid_zlgj_riskUser.setAllowCellEdit(false);
    grid_zlgj_risk.setAllowCellEdit(false);
    $("#zlgjRiskUserButtons").hide();
    $("#zlgjRiskButtons").hide();
    $("#projectMemberButtons").hide();
    $("#projectAchievementButtons").hide();
    $("#zlgjYanzhengButtons").hide();
    $("#zlgjFanganButtons").hide();
    $("#zlgjFanganjjButtons").hide();
    $("#sfxygj").css("background-color", "#F2F2F2");
    // mini.get("zrrId").setEnabled(true);
    mini.get("pfbz").setEnabled(true);
    mini.get("yzTime").setEnabled(true);
    mini.get("yjTime").setEnabled(true);
    mini.get("ifgj").setEnabled(true);
    mini.get("ifba").setEnabled(true);
    mini.get("togetherNum").setEnabled(true);
    var yzcd = mini.get("yzcd").getValue();
    if (yzcd != 'A') {
        mini.get("sffgsp").setEnabled(true);
    }
    var ifgj = $.trim(mini.get("ifgj").getValue());
    if (ifgj == 'yes') {
    }
}

function draftOrStartValidZG() {
    var wtlx = $.trim(mini.get("wtlx").getValue());
    var zrrId = $.trim(mini.get("zrrId").getValue());
    if (!zrrId) {
        return {"result": false, "message": zlgjEdit_name92};
    }
    //判断是不是责任部门的处理人(如果责任部门是挖掘机械研究院下的部门，则可以在挖掘机械研究院范围内选择)
    var ssbmId = mini.get("ssbmId").getValue();
    var ssbmName = mini.get("ssbmId").getText();
    var judgeResult = judgeWtProcessor(zrrId, ssbmId, ssbmName);
    if (!judgeResult) {
        mini.get("zrrId").setValue("");
        mini.get("zrrId").setText("");
        return {"result": false, "message": zlgjEdit_name93};
    }
    if(wtlx!="LBJSY") {
        var pfbz = $.trim(mini.get("pfbz").getValue());
        if (!pfbz) {
            return {"result": false, "message": zlgjEdit_name26};
        }
    }
    return {"result": true};
}

function judgeWtProcessor(zrrId, ssbmId, ssbmName) {
    var result = true;
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/zlgj/judgeWtProcessor.do?zrrId=' + zrrId + "&ssbmId=" + ssbmId + "&ssbmName=" + ssbmName,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            result = data.result;
        }
    });
    return result;
}


function drafgSpNew() {
    var sffgsp = $.trim(mini.get("sffgsp").getValue())
    if (!sffgsp) {
        return {"result": false, "message": zlgjEdit_name94};
    }
    return {"result": true};
}

function showJjcd() {
    jjcdWindow.show();
    var data = [];
    var item1 = {
        xx: '特急',
        sm: '需0.5天内电话告知发起人临时对策（问题发起人第一时间电话进行反馈）',
        yq: '该问题系统填报后，第一责任人在0.5天内完成审核及任务派发，问题处理人3天内在系统内输出永久对策方案'
    };
    data.push(item1);
    var item2 = {
        xx: '紧急',
        sm: '需1天内电话告知发起人临时对策（问题发起人第一时间电话进行反馈）',
        yq: '该问题系统填报后，第一责任人在1天内完成审核及任务派发，问题处理人5天内在系统内输出永久对策方案'
    };
    data.push(item2);
    var item3 = {xx: '一般', sm: '需3天内在RDM系统内输出临时对策', yq: '该问题系统填报后，第一责任人在1天内完成审核及任务派发，问题处理人10天内在系统内输出永久对策方案'};
    data.push(item3);
    jjcdListGrid.setData(data);
}

function showYzcd() {
    yzcdWindow.show();
    var data = [];
    var item1 = {
        xx: 'A', sm: '1、量产前必须要经过验证的方案；2、使用后会造成重大损失或客户极度不满的方案；3、关重件故障率及高的部件；4、严重的性能缺陷；5、与安全有关的问题',
        yq: '1、根据问题响应要求进行确定对策周期，若对策周期过长则需责任人与申请人确定具体改进完成日期；2、责任人认为无需或无法改进的问题，说明不改理由后所长进行审核，对于致命、严重问题的不改进项由耿总进行审批'
    };
    data.push(item1);
    var item2 = {
        xx: 'B', sm: '1、需要在生产部门实施的验证方案，量产前必须解决的问题；2、导致不能正常生产的问题；3、关重件或次要部件的故障率高；4、性能存在较为严重的缺陷；5、质量、操作性存在重大问题的',
        yq: '1、根据问题响应要求进行确定对策周期，若对策周期过长则需责任人与申请人确定具体改进完成日期；2、责任人认为无需或无法改进的问题，说明不改理由后所长进行审核，对于致命、严重问题的不改进项由耿总进行审批'
    };
    data.push(item2);
    var item3 = {
        xx: 'C', sm: '1、在生产中实施的解决方案，小批量生产时可解决的问题；2、容易维修的故障率较低的非关重件部件；3、轻微的性能、外观等缺陷；4、轻微的质量或性能问题，解决方案可满足可靠性目标',
        yq: '1、根据问题响应要求进行确定对策周期，若对策周期过长则需责任人与申请人确定具体改进完成日期；2、责任人认为无需或无法改进的问题，说明不改理由后所长进行审核，对于致命、严重问题的不改进项由耿总进行审批'
    };
    data.push(item3);
    var item4 = {
        xx: 'W', sm: '1、需要进行继续观察的问题；2、偶发、非主要性能或主观感觉差异造成的问题；3、无风险的问题；4、可以接受的风险或不可靠性的问题',
        yq: '1、根据问题响应要求进行确定对策周期，若对策周期过长则需责任人与申请人确定具体改进完成日期；2、责任人认为无需或无法改进的问题，说明不改理由后所长进行审核，对于致命、严重问题的不改进项由耿总进行审批'
    };
    data.push(item4);
    yzcdListGrid.setData(data);

}

function wtlxChange() {
    var wtlx = mini.get("wtlx").getValue();
    if (wtlx && "XPZDSY" == wtlx) {
        $("#tdm_sylx_td1").show();
        $("#tdm_sylx_td2").show();
    } else {
        mini.get("tdmSylx").setValue("");
        mini.get("tdmSylx").setText("");
        $("#tdm_sylx_td1").hide();
        $("#tdm_sylx_td2").hide();
    }
}

function yzcdChange() {
    var yzcd = mini.get("yzcd").getValue();
    if (yzcd == 'A') {
        mini.get("sffgsp").setValue('YES');
    } else {
        mini.get("sffgsp").setValue('NO');
    }
}

var checkFileResult = "";

function checkFile(checkType) {
    var wtId = mini.get("wtId").getValue();
    if (wtId == '') {
        checkFileResult = {"result": false, "message": zlgjEdit_name95};
        return;
    }
    var rounds = mini.get("rounds").getValue();
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/checkFile.do?wtId=' + wtId + '&checkType=' + checkType + '&rounds=' + rounds,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                checkFileResult = {"result": false, "message": data.message};
            } else {
                checkFileResult = {"result": true};
            }
        }
    });
}

var checkNoFileResult = "";

function checkNoFile() {
    var wtId = mini.get("wtId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/checkNoFile.do?wtId=' + wtId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data.message != "成功") {
                checkNoFileResult = {"result": false, "message": data.message};
            } else {
                checkNoFileResult = {"result": true};
            }
        }
    });
}

function judgeRes(rowData) {
    var result = true;
    for (var i = 0; i < rowData.length; i++) {
        var resId = rowData[i].resId;
        var leaderId = rowData[i].leaderId;
        if (rowData[i].resId && rowData[i].leaderId == currentUserId) {
            $.ajax({
                url: jsUseCtxPath + '/xjsdr/core/zlgj/judgeRes.do?resId=' + resId + "&leaderId=" + leaderId,
                type: 'get',
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    result = data.result;
                }
            });
        }
    }
    return result;
}

/**
 * 向PDM系统创建PR
 */
function createPr2Pdm() {
    mini.confirm("本操作会将“责任部门”是挖机研究院各所或工艺技术部的问题单推送至“问题处理人”的PDM中，用于创建PR，此操作仅允许执行一次，确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var wtId = mini.get("wtId").getValue();
            if (!wtId) {
                mini.alert("找不到问题单Id!");
                return;
            }
            $.ajax({
                url: jsUseCtxPath + '/xjsdr/core/zlgj/createPr2Pdm.do?wtId='+wtId,
                type: 'get',
                async: false,
                success: function (returnData) {
                    if (returnData) {
                        if (returnData.success) {
                            mini.alert("创建成功，PR单号是："+returnData.data);
                        } else {
                            mini.alert(returnData.message);
                        }
                    }
                }
            });
        }
    });
}

/**
 * 从PDM查询PR的状态
 */
function queryPrStatusFromPdm() {
    var wtId = mini.get("wtId").getValue();
    if (!wtId) {
        mini.alert("找不到问题单Id!");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/queryPrFromPdm.do?wtId='+wtId,
        type: 'get',
        async: false,
        success: function (returnData) {
            if (returnData) {
                if (returnData.success) {
                    mini.alert("查询成功。"+returnData.data);
                } else {
                    mini.alert(returnData.message);
                }
            }
        }
    });
}
