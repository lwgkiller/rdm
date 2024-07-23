$(function () {
    queryDimension();
    if (jsjlId) {
        var url = jsUseCtxPath + "/jsjl/core/queryJsjlById.do?jsjlId="+jsjlId;
        $.ajax({
            url:url,
            method:'get',
            success:function (json) {
                jsjlForm.setData(json);
            }
        });
        fileListGrid.load();
        planListGrid.load();
    } else {
        mini.get("deptId").setValue(currentUserMainGroupId);
        mini.get("deptId").setText(currentUserMainGroupName);
        mini.get("receiveUserId").setValue(currentUserId);
        mini.get("receiveUserId").setText(currentUserName);
    }

    //    不同场景的处理
    if(action=='detail') {
        jsjlForm.setEnabled(false);
    } else if(action=='edit' || action=='add') {
        mini.get("saveJsjlDraft").show();
        mini.get("commitJsjl").show();
        mini.get("fileButtons").show();
        mini.get("planButtons").show();
    } else if(action=='feedback') {
        mini.get("feedbackJsjl").show();
        mini.get("fileButtons").show();
        mini.get("planButtons").show();
        mini.get("deptId").setEnabled(false);
        mini.get("receiveUserId").setEnabled(false);
        mini.get("meetingUserIds").setEnabled(false);
        mini.get("communicateCompany").setEnabled(false);
        mini.get("dimensionId").setEnabled(false);
        mini.get("sendNotice").setEnabled(false);
        mini.get("generationNo").setEnabled(false);
    }
});

function queryDimension() {
    $.ajax({
        url: jsUseCtxPath + '/jsjl/core/config/dimensionListQuery.do?scene='+scene,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#dimensionId").load(data);
            }
        }
    });
}

function renderMeetingUserNames(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var meetingUserNames = record.meetingUserNames;
    if (meetingUserNames == null) {
        meetingUserNames = "";
    }
    html += '<span style="white-space:pre-wrap" >' + meetingUserNames + '</span>';
    html += '</div>'
    return html;
}

function renderContent() {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var contentAndConclusion = record.contentAndConclusion;
    if (contentAndConclusion == null) {
        contentAndConclusion = "";
    }
    html += '<span style="white-space:pre-wrap" >' + contentAndConclusion + '</span>';
    html += '</div>'
    return html;
}

function renderPlan() {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var planAndResult = record.planAndResult;
    if (planAndResult == null) {
        planAndResult = "";
    }
    html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
    html += '</div>'
    return html;
}

function saveJsjlDraft() {
    var dimensionId = mini.get("dimensionId").getValue();
    if(!dimensionId) {
        mini.alert("请选择“会议类型”！");
        return;
    }
    var postData = {};
    postData.id = $("#id").val();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.deptId = mini.get("deptId").getValue();
    postData.receiveUserId = mini.get("receiveUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.communicateTime = mini.get("communicateTime").getText();
    postData.communicateRoom = mini.get("communicateRoom").getValue();
    postData.communicateCompany = $.trim(mini.get("communicateCompany").getValue());
    postData.meetingTheme = $.trim(mini.get("meetingTheme").getValue());
    postData.dimensionId = mini.get("dimensionId").getValue();
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '草稿';
    postData.generationNo = mini.get("generationNo").checked;//todo:改了

    $.ajax({
        url: jsUseCtxPath + "/jsjl/core/saveJsjlDataDraft.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/jsjl/core/editPage.do?jsjlId=" + returnData.data + "&action=edit";
                        window.location.href = url;
                    }
                });
            }
        }
    });
}

function commitJsjl() {
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.deptId = mini.get("deptId").getValue();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.receiveUserId = mini.get("receiveUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.communicateTime = mini.get("communicateTime").getText();
    postData.communicateRoom = mini.get("communicateRoom").getValue();
    postData.communicateCompany = $.trim(mini.get("communicateCompany").getValue());
    postData.meetingTheme = $.trim(mini.get("meetingTheme").getValue());
    postData.dimensionId = mini.get("dimensionId").getValue();
    var meetingModelData=mini.get("dimensionId").getSelected();
    if(meetingModelData) {
        postData.meetingModelDescp =meetingModelData.descp;
    }
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '已提交';
    postData.sendNotice=mini.get("sendNotice").checked;
    postData.generationNo = mini.get("generationNo").checked;
    //检查必填项
    var checkResult=commitValidCheck(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/jsjl/core/commitJsjlData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        window.close();
                    }
                });
            }
        }
    });
}


function feedbackJsjl() {
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.deptId = mini.get("deptId").getValue();
    postData.receiveUserId = mini.get("receiveUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.communicateTime = mini.get("communicateTime").getText();
    postData.communicateRoom = mini.get("communicateRoom").getValue();
    postData.communicateCompany = $.trim(mini.get("communicateCompany").getValue());
    postData.meetingTheme = $.trim(mini.get("meetingTheme").getValue());
    postData.dimensionId = mini.get("dimensionId").getValue();
    var meetingModelData=mini.get("dimensionId").getSelected();
    if(meetingModelData) {
        postData.meetingModelDescp =meetingModelData.descp;
    }
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '已提交';
    postData.generationNo = mini.get("generationNo").checked;//todo:改了
    //检查必填项
    var checkResult=feedbackValidCheck(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/jsjl/core/feedbackJsjlData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        window.close();
                    }
                });
            }
        }
    });
}

function feedbackValidCheck(postData) {
    var checkResult={};
    if(!postData.communicateTime) {
        checkResult.success=false;
        checkResult.reason='请选择交流时间！';
        return checkResult;
    }
    if(!postData.communicateRoom) {
        checkResult.success=false;
        checkResult.reason='请填写交流地点！';
        return checkResult;
    }
    if(!postData.contentAndConclusion) {
        checkResult.success=false;
        checkResult.reason='请填写交流内容、结论（1500字以内）！';
        return checkResult;
    }else if(postData.contentAndConclusion.length>1500) {
        checkResult.success=false;
        checkResult.reason='“交流内容、结论”长度超过限制！';
        return checkResult;
    }
    if(!postData.planAndResult) {
        checkResult.success=false;
        checkResult.reason='请填写采取的计划、执行结果（1500字以内）！';
        return checkResult;
    }else if(postData.planAndResult.length>1500) {
        checkResult.success=false;
        checkResult.reason='“采取的计划、执行结果”长度超过限制！';
        return checkResult;
    }
    checkResult.success=true;
    return checkResult;
}

function commitValidCheck(postData) {
    var checkResult={};
    if(!postData.deptId) {
        checkResult.success=false;
        checkResult.reason='请选择组织部门！';
        return checkResult;
    }
    if(!postData.receiveUserId) {
        checkResult.success=false;
        checkResult.reason='请选择业务接待人！';
        return checkResult;
    }
    if(!postData.meetingUserIds) {
        checkResult.success=false;
        checkResult.reason='请选择参会人员！';
        return checkResult;
    }
    if(!postData.communicateTime) {
        checkResult.success=false;
        checkResult.reason='请选择交流时间！';
        return checkResult;
    }
    if(!postData.communicateRoom) {
        checkResult.success=false;
        checkResult.reason='请填写交流地点！';
        return checkResult;
    }
    if(!postData.communicateCompany) {
        checkResult.success=false;
        checkResult.reason='请填写交流单位！';
        return checkResult;
    }
    if(!postData.dimensionId) {
        checkResult.success=false;
        checkResult.reason='请选择会议类型！';
        return checkResult;
    }
    if(!postData.meetingTheme) {
        checkResult.success=false;
        checkResult.reason='请填写会议主题！';
        return checkResult;
    }
    checkResult.success=true;
    return checkResult;
}


function uploadJsjlFile() {
    var jsjlId = mini.get("id").getValue();
    if (!jsjlId) {
        mini.alert("请先点击‘保存草稿’进行表单创建！")
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/jsjl/core/file/openUploadWindow.do",
        width: 800,
        height: 350,
        showModal:false,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var passParams={};
            passParams.jsjlId = jsjlId;
            var data = { passParams: passParams };  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

function returnJsjlPreviewSpan(fileName,fileId,jsjlId) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+jsjlId+'\',\''+coverContent+ '\')">预览</span>';
    }else if(fileType=='office'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+jsjlId+'\',\''+coverContent +'\')">预览</span>';
    }else if(fileType=='pic'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+jsjlId+'\',\''+coverContent+ '\')">预览</span>';
    }
    return s;
}


//下载文档
function downLoadJsjlFile(fileName,fileId,jsjlId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/jsjl/core/file/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "jsjlId");
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

//删除文档
function deleteJsjlFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/jsjl/core/file/delete.do";
                var data = {
                    fileName: record.fileName,
                    id: record.id,
                    jsjlId: record.jsjlId
                };
                $.ajax({
                    url:url,
                    method:'post',
                    contentType: 'application/json',
                    data:mini.encode(data),
                    success:function (json) {
                        fileListGrid.load();
                    }
                });
            }
        }
    );
}

function previewPdf(fileName,fileId,jsjlId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!jsjlId) {
        jsjlId='';
    }
    var previewUrl = jsUseCtxPath + "/jsjl/core/file/fileDownload.do?action=preview&fileName="+encodeURIComponent(fileName)+"&fileId="+fileId+"&jsjlId="+jsjlId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
}

function previewPic(fileName,fileId,jsjlId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!jsjlId) {
        jsjlId='';
    }
    var previewUrl = jsUseCtxPath + "/jsjl/core/file/imagePreview.do?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&jsjlId="+jsjlId;
    window.open(previewUrl);
}

function previewDoc(fileName,fileId,jsjlId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!jsjlId) {
        jsjlId='';
    }
    var previewUrl = jsUseCtxPath + "/jsjl/core/file/officePreview.do?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&jsjlId="+jsjlId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverContent)+"&file=" + encodeURIComponent(previewUrl));
}

/**
 * 获取附件类型
 * */
function getFileType(fileName) {
    var suffix="";
    var suffixIndex=fileName.lastIndexOf('.');
    if(suffixIndex!=-1) {
        suffix=fileName.substring(suffixIndex+1).toLowerCase();
    }
    var pdfArray = ['pdf'];
    if(pdfArray.indexOf(suffix)!=-1){
        return 'pdf';
    }
    var officeArray = ['doc','docx','ppt','txt','xlsx','xls','pptx'];
    if(officeArray.indexOf(suffix)!=-1){
        return 'office';
    }
    var picArray = ['jpg','jpeg','jif','bmp','png','tif','gif'];
    if(picArray.indexOf(suffix)!=-1){
        return 'pic';
    }
    return 'other';
}

//..
function addMeetingPlan() {
    var newRow = {}
    newRow.id = "";
    newRow.meetingId = mini.get("id").getValue();
    newRow.meetingContent = "";
    newRow.meetingPlanRespUserIds = "";
    newRow.meetingPlanEndTime = "";
    newRow.meetingPlanCompletion = "";
    newRow.isComplete = "false";
    addRowGrid("planListGrid", newRow);
}
//..
function deleteMeetingPlan() {
    var row = planListGrid.getSelected();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    if (row.id == "") {
        delRowGrid("planListGrid");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var id = row.id;
            _SubmitJson({
                url: jsUseCtxPath + "/jsjl/core/deleteOneMeetingPlan.do",
                method: 'POST',
                data: {id: id,meetingId:jsjlId},
                success: function (returnData) {
                    if (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    planListGrid.reload();
                                }
                            });
                        }
                    }
                }
            });
        }
    });
}
//..
function saveMeetingPlan() {
    var meetingId = mini.get("id").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存草稿’进行计划创建！");
        return;
    }

    planListGrid.validate();
    if (!planListGrid.isValid()) {
        var error = planListGrid.getCellErrors()[0];
        planListGrid.beginEditCell(error.record, error.column);
        mini.alert(error.column.header + error.errorText);
        return;
    }

    var postData = planListGrid.data;
    var postDataJson = mini.encode(postData);
    $.ajax({
        url: jsUseCtxPath + "/jsjl/core/saveMeetingPlan.do?meetingId="+meetingId,
        type: 'POST',
        contentType: 'application/json',
        data: postDataJson,
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        planListGrid.reload();
                    }
                });
            }
        }
    });
    debugger;
}
//..
function isCompleteRenderer(e) {
    var record = e.record;
    var isComplete = record.isComplete;
    var arr = [{'key': 'true', 'value': '是', 'css': 'green'},
        {'key': 'false', 'value': '否', 'css': 'red'}];
    return $.formatItemValue(arr, isComplete);
}
//..
function onCellValidation(e) {
    if (e.field == 'meetingContent' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
    if (e.field == 'meetingPlanRespUserIds' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
    if (e.field == 'meetingPlanEndTime' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
}