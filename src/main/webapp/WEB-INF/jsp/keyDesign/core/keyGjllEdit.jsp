<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="save" name="save" class="mini-button" style="display: none" onclick="save()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formGjll" method="post">
            <input id="gjId" name="gjId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">

                <tr>
                    <td style="width: 10%">零部件代号/型号：<span style="color: #ff0000">*</span></td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="model" name="model" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">零部件名称：<span style="color: #ff0000">*</span></td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="codeName2" name="codeName2" class="mini-hidden" style="width:98%;" readonly/>
                        <input id="codeName" name="codeName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">生产供应商：<span style="color: #ff0000">*</span></td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="supplier" name="supplier" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">问题描述：<span style="color: #ff0000">*</span></td>
                    <td colspan="5" style="width: 98%;min-width:170px;font-size:14pt">
                        <input id="problem" name="problem" class="mini-textarea" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">原因分析：<span style="color: #ff0000">*</span></td>
                    <td colspan="5" style="width: 98%;min-width:170px;font-size:14pt">
                        <input id="reason" name="reason" class="mini-textarea" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">改善措施：<span style="color: #ff0000">*</span></td>
                    <td colspan="5" style="width: 98%;min-width:170px;font-size:14pt">
                        <input id="solve" name="solve" class="mini-textarea" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left">改进文件：<span style="color: #ff0000">*</span></td>
                    <td >
                        <input id="changeFile" name="changeFile" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                    <td style="text-align: left">改进日期：<span style="color: #ff0000">*</span></td>
                    <td >
                        <input style="width:98%;" class="mini-datepicker" id="changeTime" name="changeTime"
                               showTime="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">实施情况：<span style="color: #ff0000">*</span></td>
                    <td colspan="5" style="width: 98%;min-width:170px;font-size:14pt">
                        <input id="situation" name="situation" class="mini-textarea" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>提醒通知人：<span style="color: #ff0000">*</span></td>
                    <td colspan="5">
                        <input id="noticeId" name="noticeId" textname="noticeName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;"
                               allowinput="false"  length="1000" maxlength="1000"  mainfield="no"  single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px">改善情况附件：</td>
                    <td colspan="5">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/Gjll/getFileList.do?gjId=${gjId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="fileType" width="80" headerAlign="center" align="center">文件类型</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var type="${type}";
    var codeName="${codeName}";
    var jsUseCtxPath = "${ctxPath}";
    var gjllListGrid = mini.get("gjllListGrid");
    var gjId = "${gjId}";
    var formGjll = new mini.Form("#formGjll");
    var fileListGrid = mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnGjllPreviewSpan(record.fileName, record.fileId, record.gjId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadGjllFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.gjId + '\')">下载</span>';
        //增加删除按钮
        if (action == 'edit') {
            var deleteUrl = "/Gjll/deleteGjllFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.gjId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    $(function () {
        if(gjId) {
            var url = jsUseCtxPath + "/Gjll/getGjllDetail.do";
            $.post(
                url,
                {gjId: gjId},
                function (json) {
                    formGjll.setData(json);
                });
        }else {
            mini.get("codeName2").setValue(codeName);
        }
        if (action == 'detail') {
            formGjll.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
        if (action == 'edit'||action == 'add') {
            $("#save").show();
        }
    });


    function save() {
        var formValid = validGjll();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData =new mini.Form("formGjll");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/Gjll/saveGjll.do?type='+type,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            // window.close();
                            var url=jsUseCtxPath+"/Gjll/edit.do?gjId="+returnData.data+"&action=edit&type="+type;
                            window.location.href=url;
                        }
                    });
                }
            }
        });
    }

    function validGjll() {
        var model=$.trim(mini.get("model").getValue())
        if(!model) {
            return {"result": false, "message": "请填写零部件代号/型号"};
        }
        var codeName=$.trim(mini.get("codeName").getValue())
        if(!codeName) {
            return {"result": false, "message": "请填写零部件名称"};
        }
        var supplier=$.trim(mini.get("supplier").getValue())
        if(!supplier) {
            return {"result": false, "message": "请填写生产供应商"};
        }
        var problem=$.trim(mini.get("problem").getValue())
        if(!problem) {
            return {"result": false, "message": "请填写问题描述"};
        }
        var reason=$.trim(mini.get("reason").getValue())
        if(!reason) {
            return {"result": false, "message": "请填写原因分析"};
        }
        var solve=$.trim(mini.get("solve").getValue())
        if(!solve) {
            return {"result": false, "message": "请填写改善措施"};
        }
        var changeFile=$.trim(mini.get("changeFile").getValue())
        if(!changeFile) {
            return {"result": false, "message": "请填写改进文件"};
        }
        var changeTime=$.trim(mini.get("changeTime").getValue())
        if(!changeTime) {
            return {"result": false, "message": "请选择改进日期"};
        }
        var situation=$.trim(mini.get("situation").getValue())
        if(!situation) {
            return {"result": false, "message": "请填写实施情况"};
        }
        var noticeId=$.trim(mini.get("noticeId").getValue())
        if(!noticeId) {
            return {"result": false, "message": "请选择提醒通知人"};
        }
        // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
        // if(!fileListGrid) {
        //     return {"result": false, "message": "请添加附件"};
        // }
        return {"result": true};
    }


    function fileupload() {
        var gjId = mini.get("gjId").getValue();
        if(!gjId){
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/Gjll/openUploadWindow.do?gjId="+gjId,
            width: 850,
            height: 550,
            showModal:true,
            allowResize: true,
            ondestroy: function () {
                if(fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }
    function downLoadGjllFile(fileName,fileId,formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/Gjll/gjllPdfPreview.do?action=download');
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
    function returnGjllPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/Gjll/gjllPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/Gjll/gjllOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/Gjll/gjllImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        return s;
    }




</script>
</body>
</html>