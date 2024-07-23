<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>工艺反馈信息说明细节编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" onclick="savecs()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" style="height: 800px">
    <div class="form-container" style="margin: 0 auto; width: 100%;height: 100%">
        <form id="formYzbgDetail" method="post">
            <input id="detailId" name="detailId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    验证报告详细信息
                </caption>
                <tr>
                    <td style="width: 7%">报告类型<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="bgType" name="bgType" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">报告名称<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="name" name="name" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">验证机构<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="checkJg" name="checkJg" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">报告日期<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="checkTime" name="checkTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">备注：</td>
                    <td colspan="3" style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="note" name="note" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/yzbg/getYzbgFileList.do?belongId=${detailId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div gytype="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
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
    var jsUseCtxPath = "${ctxPath}";
    var detailListGrid = mini.get("detailListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var formYzbgDetail = new mini.Form("#formYzbgDetail");
    var detailId = "${detailId}";
    var belongBg = "${belongBg}";
    var action = "${action}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    

    $(function () {
        if (detailId) {
            var url = jsUseCtxPath + "/yzbg/getYzbgDetail.do";
            $.post(
                url,
                {detailId: detailId},
                function (json) {
                    formYzbgDetail.setData(json);
                });
        }
        //变更入口
        if (action == "detail") {
            formYzbgDetail.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    });

    function fileupload() {
        var detailId = mini.get("detailId").getValue();
        if (!detailId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/yzbg/openUploadWindow.do?detailId=" + detailId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }


    function returnYzbgPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/yzbg/yzbgPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/yzbg/yzbgOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/yzbg/yzbgImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadYzbgFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/yzbg/yzbgPdfPreview.do?action=download');
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


    function validYzbgXz() {
        var bgType = $.trim(mini.get("bgType").getValue())
        if (!bgType) {
            return {"result": false, "message": "请填写报告类型"};
        }
        var name = $.trim(mini.get("name").getValue())
        if (!name) {
            return {"result": false, "message": "请填写报告名称"};
        }
        var checkJg = $.trim(mini.get("checkJg").getValue())
        if (!checkJg) {
            return {"result": false, "message": "请填写验证机构"};
        }
        var checkTime = $.trim(mini.get("checkTime").getValue())
        if (!checkTime) {
            return {"result": false, "message": "请填写报告日期"};
        }
        return {"result": true};
    }

    function savecs() {
        var formValid = validYzbgXz();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = new mini.Form("formYzbgDetail");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/yzbg/saveYzbgDetail.do?belongBg='+belongBg,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.alert(data.message, "提示消息", function (action) {
                        if (action == 'ok') {
                            var url= jsUseCtxPath+"/yzbg/editYzbgDetail.do?" +
                                "detailId=" + data.data + "&action=edit&bgId="+belongBg;
                            window.location.href=url;
                        }
                    });
                }
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnYzbgPreviewSpan(record.fileName,record.fileId,record.belongId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadYzbgFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongId+'\')">下载</span>';

        //增加删除按钮
        if((action=='edit' ||action=='add')&&record.CREATE_BY_==currentUserId) {
            var deleteUrl="/yzbg/deleteYzbgFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
