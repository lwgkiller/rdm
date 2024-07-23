var stageName='';
$(function () {
    initForm();
    //明细入口
    if (action == 'detail') {
        detailActionProcess();
    } else if(action=='task') {
        taskActionProcess();
    }else if(action=='edit'){
        editProcess();
    }
});

//保存草稿
function saveZlgj(e) {
    var sjth=$.trim(mini.get("sjth").getText());
    if (!sjth) {
        mini.alert("请填写设计图号");
        return;
    }
    window.parent.saveDraft(e);
}



//流程中暂存信息（如编制阶段）
function saveZlgjInProcess() {
    var sjth=$.trim(mini.get("sjth").getText());
    if (!sjth) {
        mini.alert("请填写设计图号");
        return;
    }
   var formData = getData();
    var json =mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/xppq/saveZlgj.do',
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败，"+data.message;
                }
                mini.alert(message,"提示",function () {
                    window.location.reload();
                });
            }
        }
    });
}


//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formZlgj");
    formData.changeProcessData=grid_xppqProcess.getChanges();
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
    formZlgj.setEnabled(false);
    grid_xppqProcess.setAllowCellEdit(false);
    $("#pqzxButtons").hide();
    if(action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }
}
function editProcess() {
    grid_xppqProcess.setAllowCellEdit(false);
    $("#pqzxButtons").hide();
}


function addPqzx() {
    //添加之前需要先保存
    var wtId=$.trim(mini.get("wtId").getValue());
    if(!wtId) {
        mini.alert('请先点击“保存草稿”');
        return;
    }
    var noGdData=findNoGdProcess(grid_xppqProcess.getData());
    if(noGdData) {
        mini.alert("当前存在未执行完毕的数据，不允许新增！");
        return;
    }
    var row={};
    grid_xppqProcess.addRow(row);
}

//移除
function removePqzx() {
    var selecteds = grid_xppqProcess.getSelecteds();
    if(selecteds.length>0) {
        var existGd=false;
        var rmRows=[];
        for(var index=0;index<selecteds.length;index++) {
            if(selecteds[index].ifGd=='yes') {
                existGd=true;
            } else {
                rmRows.push(selecteds[index]);
            }
        }
        if(existGd) {
            mini.alert("已执行完的数据不允许删除！")
        }
        if(rmRows.length>0) {
            grid_xppqProcess.removeRows(rmRows);
        }
    }
}


//附件
function addXppqFile(zxId,wtId,type,ifGd) {
    if(!zxId) {
        mini.alert('请先点击“暂存信息”保存本条数据后上传！');
        return;
    }
    if(!wtId) {
        mini.alert('请先点击“暂存信息”保存申请单！');
        return;
    }
    var canOperateFile = false;
    if (ifGd!='yes' && stageName == 'fazd' && type=='pqfa') {
        canOperateFile = true;
    }
    if (ifGd!='yes' && stageName == 'zzps' && (type=='pqfx'||type=='psjg')) {
        canOperateFile = true;
    }
    mini.open({
        title: "上传附件",
        url: jsUseCtxPath + "/zlgjNPI/core/xppq/zlgjFileWindow.do?wtId=" + wtId+ "&canOperateFile=" + canOperateFile
            + "&coverContent=" + coverContent + "&zxId="+zxId+"&fileType="+type,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
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
        if (nodeVars[i].KEY_ == 'stageName') {
            stageName = nodeVars[i].DEF_VAL_;
        }
    }
    if(stageName == 'jhsq'){
        editProcess();
    }else if(stageName =='fazd'){
        formZlgj.setEnabled(false);
    }else {
        formZlgj.setEnabled(false);
        $("#pqzxButtons").hide();
    }
}

function initForm() {
    $.ajaxSettings.async = false;
    if (wtId){
        var url = jsUseCtxPath + "/zlgjNPI/core/xppq/getZlgjDetail.do";
        $.post(
            url,
            {
                wtId: wtId
            },
            function (json) {
                formZlgj.setData(json.baseInfo);
                grid_xppqProcess.setData(json.process);
            });
    }
    $.ajaxSettings.async = true;

}

//启动流程
function startZlgjProcess(e) {
    var formValid = startProcessValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//提交流程时数据是否有效
function startProcessValid() {
    var sjth=$.trim(mini.get("sjth").getValue());
    if(!sjth) {
        return {"result": false, "message": "请填写设计图号"};
    }
    var pqfs=$.trim(mini.get("pqfs").getValue());
    if (!pqfs) {
        return {"result": false, "message": "请选择剖切方式"};
    }
    var pqjhwcTime=$.trim(mini.get("pqjhwcTime").getValue());
    if(!pqjhwcTime) {
        return {"result": false, "message": "请填写剖切计划完成日期"};
    }
    var gygcsId=$.trim(mini.get("gygcsId").getValue());
    if(!gygcsId) {
        return {"result": false, "message": "请选择工艺工程师"};
    }

    return {"result": true};
}

//流程中的审批或者下一步
function zlgjApprove() {
    if(stageName == 'jhsq'){
        var formValid = startProcessValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }else if(stageName =='fazd'){
    //    检查是否有未归档的执行数据，且要求的字段和附件是否存在
        var noGdData=findNoGdProcess(grid_xppqProcess.getData());
        if(!noGdData) {
            mini.alert("请点击“添加”新增一条执行数据！");
            return;
        }
        var pqfcName=noGdData.pqfcName;
        if(!pqfcName) {
            mini.alert("请选择剖切分厂！");
            return;
        }
        var pqfaFileExist=findFileExist(mini.get("wtId").getValue(),noGdData.zxId,"pqfa");
        if(!pqfaFileExist) {
            mini.alert("请上传剖切方案！");
            return;
        }
        var yjscTime=noGdData.yjscTime;
        if(!yjscTime) {
            mini.alert("请选择样件生产日期！");
            return;
        }
        var pqjhTime=noGdData.pqjhTime;
        if(!pqjhTime) {
            mini.alert("请选择剖切计划日期！");
            return;
        }
    }else if(stageName == 'yjhj'){
        var noGdData=findNoGdProcess(grid_xppqProcess.getData());
        if(!noGdData) {
            mini.alert("当前没有未执行的数据，请联系管理员处理！");
            return;
        }
        var hjwcTime=noGdData.hjwcTime;
        if(!hjwcTime) {
            mini.alert("请选择样件焊接完成日期！");
            return;
        }
    }else if(stageName =='yjjy'){
        var noGdData=findNoGdProcess(grid_xppqProcess.getData());
        if(!noGdData) {
            mini.alert("当前没有未执行的数据，请联系管理员处理！");
            return;
        }
        var jyjg=noGdData.jyjg;
        if(!jyjg) {
            mini.alert("请填写样件检验结果！");
            return;
        }
    }else if (stageName =='yjpq'){
        var noGdData=findNoGdProcess(grid_xppqProcess.getData());
        if(!noGdData) {
            mini.alert("当前没有未执行的数据，请联系管理员处理！");
            return;
        }
        var pqwcTime=noGdData.pqwcTime;
        if(!pqwcTime) {
            mini.alert("请选择剖切实际完成日期！");
            return;
        }
    }else if (stageName =='zzps') {
        var noGdData=findNoGdProcess(grid_xppqProcess.getData());
        if(!noGdData) {
            mini.alert("当前没有未执行的数据，请联系管理员处理！");
            return;
        }
        var pqfxFileExist=findFileExist(mini.get("wtId").getValue(),noGdData.zxId,"pqfx");
        if(!pqfxFileExist) {
            mini.alert("请上传剖切分析结果！");
            return;
        }
        var psjgDesc=noGdData.psjgDesc;
        if(!psjgDesc) {
            mini.alert("请填写评审结果！");
            return;
        }
        var psjgFileExist=findFileExist(mini.get("wtId").getValue(),noGdData.zxId,"psjg");
        if(!psjgFileExist) {
            mini.alert("请上传评审结果附件！");
            return;
        }
    }

    //检查通过
    window.parent.approve();
}

/**
 * 工艺工程师组织评审的驳回
 */
function zzpsBack() {
    //检查所有在这一步的信息是否都填写
    var noGdData=findNoGdProcess(grid_xppqProcess.getData());
    if(!noGdData) {
        mini.alert("当前没有未执行的数据，请联系管理员处理！");
        return;
    }
    var pqfxFileExist=findFileExist(mini.get("wtId").getValue(),noGdData.zxId,"pqfx");
    if(!pqfxFileExist) {
        mini.alert("请上传剖切分析结果！");
        return;
    }
    var psjgDesc=noGdData.psjgDesc;
    if(!psjgDesc) {
        mini.alert("请填写评审结果！");
        return;
    }
    var psjgFileExist=findFileExist(mini.get("wtId").getValue(),noGdData.zxId,"psjg");
    if(!psjgFileExist) {
        mini.alert("请上传评审结果附件！");
        return;
    }
    window.parent.reject();
}

//找到未归档锁定的执行数据
function findNoGdProcess(processArr) {
    if(!processArr || processArr.length==0) {
        return "";
    }
    for(var index=0;index<processArr.length;index++) {
        var ifGd=processArr[index].ifGd;
        if(!ifGd || ifGd!='yes') {
            return processArr[index];
        }
    }
    return "";
}

//检查某次执行某种附件是否存在
function findFileExist(wtId,zxId,type) {
    var defaultResult=true;
    $.ajax({
        url: jsUseCtxPath+"/zlgjNPI/core/xppq/files.do?wtId="+wtId +"&zxId="+zxId+"&fileType="+type,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data || data.length==0) {
                defaultResult=false;
            }
        }
    });
    return defaultResult;
}