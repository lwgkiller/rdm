var isBianzhi="";
var isZgsh="";
var isPgtb="";
var isSzsh="";

$(function () {
    initForm();
    //明细入口
    if (action == 'detail') {
        detailActionProcess();
    } else if(action=='task') {
        taskActionProcess();
    }else if(action=='edit'){
        edit1Process();
    }
});


//保存草稿
function saveXpsx(e) {
    var xtfl=$.trim(mini.get("xtfl").getValue())
    if (!xtfl) {
        mini.alert("请选择系统分类");
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startXpsxProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var xtfl=$.trim(mini.get("xtfl").getValue());
    if(!xtfl) {
        return {"result": false, "message": "请选择系统分类"};
    }
    var lbj=$.trim(mini.get("lbj").getValue())
    if(!lbj) {
        return {"result": false, "message": "请填写零部件"};
    }
    var wtqd=$.trim(mini.get("wtqd").getValue())
    if(!wtqd) {
        return {"result": false, "message": "请填写问题清单"};
    }
    var gscs=$.trim(mini.get("gscs").getValue())
    if(!gscs) {
        return {"result": false, "message": "请填写改善措施"};
    }
    var gsrId=$.trim(mini.get("gsrId").getValue())
    if(!gsrId) {
        return {"result": false, "message": "请选择改善责任人"};
    }
    return {"result": true};
}

//流程中的审批或者下一步
function xpsxApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (isZgsh == 'yes') {
        var formValid = draftOrStartValidZG();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (isPgtb == 'yes') {
        var formValid = draFxpg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (isSzsh == 'yes'){
        var sffgsp=$.trim(mini.get("sffgsp").getValue())
        if (!sffgsp) {
            mini.alert("请选择是否分管领导审批");
            return;
        }
    }


    //检查通过
    window.parent.approve();
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formXpsx");
    if (formData.SUB_treegridFileInfo) {
        delete formData.SUB_treegridFileInfo;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[];
    formData.changeFxpgData=grid_xpsx_fxpg.getChanges();
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
    formXpsx.setEnabled(false);
    grid_xpsx_fxpg.setAllowCellEdit(false);
    $("#projectAchievementButtons").hide();
    $("#detailToolBar").show();
    //非草稿放开流程信息查看按钮
    if(status!='DRAFTED') {
        $("#processInfo").show();
    }
}
function edit1Process() {
    mini.get("zrrId").setEnabled(false);
    mini.get("sjsm").setEnabled(false);
    mini.get("sffgsp").setEnabled(false);
    grid_xpsx_fxpg.setAllowCellEdit(false);
    $("#projectAchievementButtons").hide();

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



function addFxpg() {
    var row={};
    grid_xpsx_fxpg.addRow(row);
}

function delFxpg() {
    var selecteds = grid_xpsx_fxpg.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_xpsx_fxpg.removeRows(deleteArr);
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
        if(nodeVars[i].KEY_ == 'isZgsh'){
            isZgsh = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isPgtb'){
            isPgtb = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isSzsh'){
            isSzsh = nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi == 'yes'){
        edit1Process();
    }else if(isZgsh == 'yes'){
        edit1SHProcess();
    }else if(isPgtb == 'yes'){
        edit3ZGProcess();
    }else if (isSzsh == 'yes'){
        edit1SZSHProcess();
    } else{
        editBZSHProcess();
    }
}

function initForm() {
    $.ajaxSettings.async = false;
    if (xpsxId){
        var url = jsUseCtxPath + "/zhgl/core/fxpg/getFxpgList.do";
        $.post(
            url,
            {ids: xpsxId},
            function (json) {
                if (json != null && json.length > 0) {
                    grid_xpsx_fxpg.setData(json);
                }
            });
        var url = jsUseCtxPath + "/zhgl/core/fxpg/getXpsxDetail.do";
        $.post(
            url,
            {xpsxId: xpsxId},
            function (json) {
                formXpsx.setData(json);
            });
    }

    $.ajaxSettings.async = true;

}

function draFxpg() {
    var sjsm=$.trim(mini.get("sjsm").getValue());
    if(!sjsm) {
        return {"result": false, "message": "请填写设计寿命"};
    }
    var checkLscs=checkMemberLscs(grid_xpsx_fxpg.getData(),false);
    if(!checkLscs.result) {
        return {"result": false, "message": checkLscs.message};
    }
    return {"result": true};
}


function checkMemberLscs(rowData,checkAll) {
    if (!rowData || rowData.length <= 0) {
        return {result: false, message: '请添加风险评估及对策'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.fxd) {
            return {result: false, message: '请填写风险点'};
        }
        if (!row.gkcs) {
            return {result: false, message: '请填写管控措施'};
        }
        if (!row.yzfa) {
            return {result: false, message: '请选择验证方法'};
        }
        if (!row.wcTime) {
            return {result: false, message: '请选择完成时间'};
        }
    }
    return {result: true};
}

function edit1SHProcess() {
    formXpsx.setEnabled(false);
    mini.get("zrrId").setEnabled(true);
    grid_xpsx_fxpg.setAllowCellEdit(false);
    $("#projectAchievementButtons").hide();

}
function edit3ZGProcess() {
    formXpsx.setEnabled(false);
    mini.get("sjsm").setEnabled(true);
}

function draftOrStartValidZG() {
    var zrrId=$.trim(mini.get("zrrId").getValue())
    if(!zrrId) {
        return {"result": false, "message": "请选择责任人"};
    }
    return {"result": true};
}
function editBZSHProcess() {
    formXpsx.setEnabled(false);
    grid_xpsx_fxpg.setAllowCellEdit(false);
    $("#projectAchievementButtons").hide();

}
function edit1SZSHProcess() {
    formXpsx.setEnabled(false);
    mini.get("sffgsp").setEnabled(true);
    var sffgsp=mini.get("sffgsp").getValue();
    if(!sffgsp) {
        mini.get("sffgsp").setValue('NO');
    }
    grid_xpsx_fxpg.setAllowCellEdit(false);
    $("#projectAchievementButtons").hide();

}


