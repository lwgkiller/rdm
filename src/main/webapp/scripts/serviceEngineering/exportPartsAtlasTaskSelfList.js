$(function () {
    if(fwgcsUser || currentUserNo == 'admin') {
        mini.get("taskReleaseBtn").show();
        mini.get("slzzBtn").show();
        mini.get("zzwcBtn").show();
        mini.get("createAbnormalBtn").show();
    }
    if(archiveUser || currentUserNo == 'admin') {
        mini.get("gdqrBtn").show();
    }
    var statusData=[];
    if(fwgcsUser) {
        statusData=[{key:'已领取',value:'01YLQ'},{key:'机型制作申请中',value:'02JXZZSQ'},
            {key:'机型制作中',value:'03JXZZ'},{key:'实例制作中',value:'04SLZZ'},
            {key:'改制中',value:'05GZ'}
        ];
    } else if (archiveUser)  {
        statusData=[{key:'制作完成已转出',value:'06ZZWCYZC'}];
    } else if (currentUserNo=='admin')  {
        statusData=[{key:'已领取',value:'01YLQ'},{key:'机型制作申请中',value:'02JXZZSQ'},
            {key:'机型制作中',value:'03JXZZ'},{key:'实例制作中',value:'04SLZZ'},
            {key:'改制中',value:'05GZ'},{key:'制作完成已转出',value:'06ZZWCYZC'}];
    } else {
        $("#taskStatus_li").hide();
        // mini.get("taskStatus").hide();
    }
    mini.get("taskStatus").setData(statusData);
});

function statusHis(machineTaskId) {
    statusHisWindow.show();
    var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/statusHisQuery.do";
    url+="?busKeyId="+machineTaskId;
    url+="&scene=task";
    statusListGrid.setUrl(url);
    statusListGrid.load();
}

//仅支持“已领取”或“实例制作中”状态的释放
function taskRelease() {
    var selectRows=taskListGrid.getSelecteds();
    if (selectRows && selectRows.length>0) {
        var ids="";
        for(var index=0;index<selectRows.length;index++) {
            if(selectRows[index].taskStatus == '01YLQ' || selectRows[index].taskStatus == '04SLZZ') {
                ids+=selectRows[index].id+",";
            }
        }
        if(!ids) {
            mini.alert("仅支持“已领取”或“实例制作中”状态的释放！");
            return;
        }
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskReceiceOrRelease.do";
        $.post(
            url,
            {scene: "release",ids:ids.substr(0,ids.length-1)},
            function (returnData) {
                mini.alert(returnData.message,'提示',function (action) {
                    if(returnData.success) {
                        taskListGrid.reload();
                    }
                });
            });
    } else {
        mini.alert("请至少选择一条数据！");
    }
}

//从“制作完成转出”---->"档案室已接收"
function taskGdqr() {
    var selectRows=taskListGrid.getSelecteds();
    if (selectRows && selectRows.length>0) {
        var ids="";
        for(var index=0;index<selectRows.length;index++) {
            ids+=selectRows[index].id+",";
        }
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskNext.do?scene=gdqr";
        $.post(
            url,
            {ids:ids.substr(0,ids.length-1)},
            function (returnData) {
                mini.alert(returnData.message,'提示',function (action) {
                    if(returnData.success) {
                        taskListGrid.reload();
                    }
                });
            });
    } else {
        mini.alert("请至少选择一条数据！");
    }
}

//相同物料编码“已领取”任务的推进("已领取"--->"实例制作")
function nextSlzz() {
    var selectRows=taskListGrid.getSelecteds();
    if(!selectRows){
        mini.alert("请至少选择一条数据！");
        return;
    }
    //判断是否都是相同物料编码，状态都是“已领取”
    var checkOK=true;
    var taskIdArr=[];
    for (var index=0;index<selectRows.length;index++) {
        if(selectRows[index].matCode !=selectRows[0].matCode || selectRows[index].taskStatus !='01YLQ') {
            checkOK = false;
            break;
        }
        taskIdArr.push(selectRows[index].id);
    }
    if(!checkOK) {
        mini.alert("请选择任务状态为“已领取”、整机物料编码相同的数据！");
        return;
    }
    mini.confirm("确定任务向下推进？", "确认",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskNext.do?scene=slzz";
                $.post(
                    url,
                    {ids:taskIdArr.join(",")},
                    function (returnData) {
                        //对于需要进行机型制作的任务，需要进行confirm弹框确认（允许不建立机型制作流程，直接变成实例制作）
                        if(returnData.extPros=='startModelMade') {
                            mini.showMessageBox({
                                title: "机型制作确认",
                                buttons: ["ok", "no", "cancel"],
                                message: "缺少对应的机型图册。确定：启动机型制作流程；否：跳过机型制作流程。",
                                callback: function (action) {
                                    if (action == "ok") {
                                        startModelMadeFlow(selectRows[0].matCode,selectRows[0].designType);
                                    }else if(action == "no") {
                                        jumpModelMade2Slzz(taskIdArr.join(","));
                                    }
                                }
                            });

                        } else {
                            mini.alert(returnData.message,'提示',function (action) {
                                if(returnData.success) {
                                    taskListGrid.reload();
                                }
                            });
                        }
                    });
            }
        }
    );
}

// “实例制作中”/“改制中”----->“制作完成已转出”
function nextZzwc() {
    var selectRows=taskListGrid.getSelecteds();
    if(!selectRows){
        mini.alert("请选择一条数据！");
        return;
    }
    if (selectRows && selectRows.length>0) {
        //    检查是否所选的所有数据状态都是'05GZ'、'04SLZZ'
        var canMultiSelect=true;
        for(var index=0;index<selectRows.length;index++) {
            if(selectRows[index].taskStatus!='05GZ' && selectRows[index].taskStatus!='04SLZZ') {
                canMultiSelect=false;
                break;
            }
        }
        if(!canMultiSelect) {
            mini.alert("请选择状态为“实例制作中”/“改制中”的任务！");
            return;
        }
        mini.confirm("确定制作完成转出？", "确认",
            function (action) {
                if (action == "ok") {
                    mini.get("atlasFilePath").setValue(selectRows[0].atlasFilePath);
                    mini.get("fileDesc").setValue(selectRows[0].fileDesc);
                    fileInfoWindow.show();
                }
            }
        );
    }
}

//跳过机型制作流程（“已领取”----->“实例制作中”）
function jumpModelMade2Slzz(taskIdArrStr) {
    var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskNext.do?scene=jumpModelMade2Slzz";
    $.post(
        url,
        {ids:taskIdArrStr},
        function (returnData) {
            mini.alert(returnData.message,'提示',function (action) {
                if(returnData.success) {
                    taskListGrid.reload();
                }
            });
        });
}


//启动机型制作流程页面
function startModelMadeFlow(matCode,designType) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/LJTCJXZZ/start.do?matCode=" + matCode+"&designType="+designType;
    mini.open({
        title: "机型制作表单",
        url: url,
        width: 900,
        height: 500,
        showModal:true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function () {
            taskListGrid.reload();
        }
    });
}

//先处理单条任务
function taskBack() {
    var selectRows=taskListGrid.getSelecteds();
    if (selectRows && selectRows.length==1) {
        var id=selectRows[0].id;
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskBack.do";
        $.post(
            url,
            {id:id},
            function (returnData) {
                mini.alert(returnData.message,'提示',function (action) {
                    if(returnData.success) {
                        taskListGrid.reload();
                    }
                });
            });
    } else {
        mini.alert("请选择一条数据！");
    }
}

//“实例制作”、"改制中"----->"制作完成转出"
function setGdFileInfo() {
    var atlasFilePath = mini.get("atlasFilePath").getValue();
    if(!atlasFilePath) {
        mini.alert("请填写图册归档路径！");
        return;
    }
    var fileDesc = mini.get("fileDesc").getValue();
    var selectRows=taskListGrid.getSelecteds();
    var ids=[];
    for(var index=0;index<selectRows.length;index++) {
        ids.push(selectRows[index].id);
    }
    var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskNext.do?scene=zzwc";
    $.post(
        url,
        {ids:ids.join(','),atlasFilePath:atlasFilePath,fileDesc:fileDesc},
        function (returnData) {
            mini.alert(returnData.message,'提示',function (action) {
                if(returnData.success) {
                    closeGdFileInfoWindow();
                    taskListGrid.reload();
                }
            });
        });
}

function closeGdFileInfoWindow() {
    mini.get("atlasFilePath").setValue("");
    mini.get("fileDesc").setValue("");
    fileInfoWindow.hide();
}

function createAbnormal() {
    var selectRows=taskListGrid.getSelecteds();
    if (!selectRows) {
        mini.alert("请至少选择一条数据！");
        return;
    }
    //将存在运行中的流程的任务过滤掉
    var existRunningTask=false;
    var machinetaskIdArr=[];
    var machineCodeArr=[];
    for(var index=0;index<selectRows.length;index++) {
        if(selectRows[index].hasExistRunningAbnormal=='true') {
            existRunningTask=true;
        } else {
            machinetaskIdArr.push(selectRows[index].id);
            machineCodeArr.push(selectRows[index].machineCode);
        }
    }
    if(existRunningTask) {
        mini.alert("部分已存在审批中异常反馈流程的任务不会重复创建异常反馈！","提示",function () {
            if (machinetaskIdArr.length>0) {
                formAbnormal.setData({});
                mini.get("machinetaskId").setValue(machinetaskIdArr.join(","));
                mini.get("machineCodeAbnormal").setValue(machineCodeArr.join(","));
                abnormalSubmitWindow.show();
            }
        });
    } else {
        formAbnormal.setData({});
        mini.get("machinetaskId").setValue(machinetaskIdArr.join(","));
        mini.get("machineCodeAbnormal").setValue(machineCodeArr.join(","));
        abnormalSubmitWindow.show();
    }
}


function closeAbnormalSubmitWindow() {
    abnormalSubmitWindow.hide();
}

function abnormalSubmit() {
    var formData=formAbnormal.getData();
    formData.expectTime=mini.get("expectTime").getText();
    if(!formData.expectTime) {
        mini.alert("请填写预计完成时间！");
        return;
    }
    if(!formData.reasonDesc) {
        mini.alert("请填写原因描述！");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/saveAbnormal.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(formData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        closeAbnormalSubmitWindow();
                        taskListGrid.reload();
                    }
                });
            }
        }
    });
}