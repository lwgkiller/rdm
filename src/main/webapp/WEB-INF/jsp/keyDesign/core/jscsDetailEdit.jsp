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
        <form id="formJscsDetail" method="post">
            <input id="csId" name="csId" class="mini-hidden"/>
            <input id="oldcsId" name="oldcsId" class="mini-hidden"/>
            <input id="cstype" name="cstype" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    技术参数细节表
                </caption>
                <tr>
                    <td style="width: 7%">参数类型<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="type" name="type" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">参数名称<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="name" name="name" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">参数值<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="number" name="number" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">重要度<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="weight" name="weight" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">备注：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="note" name="note" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;height: 300px;font-size:14pt">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/jscs/getJscsFileList.do?belongCs=${csId}"
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
    var formJscsDetail = new mini.Form("#formJscsDetail");
    var csId = "${csId}";
    var oldcsId="${oldcsId}";
    var belongJs = "${belongJs}";
    var action = "${action}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    function fujian(e) {
        var record = e.record;
        var rapId = record.rapId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="rapFile(\'' + rapId + '\')">附件列表</span>';
        return s;
    }

    $(function () {
        if (csId) {
            var url = jsUseCtxPath + "/jscs/getJscsDetail.do";
            $.post(
                url,
                {csId: csId},
                function (json) {
                    formJscsDetail.setData(json);
                });
        }
        //变更入口
        if (action == "detail") {
            formJscsDetail.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    });

    function fileupload() {
        var csId = mini.get("csId").getValue();
        if (!csId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/jscs/openUploadWindow.do?csId=" + csId,
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


    function returnJscsPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/jscs/jscsPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/jscs/jscsOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/jscs/jscsImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadJscsFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/jscs/jscsPdfPreview.do?action=download');
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


    function validJscsXz() {
        var type = $.trim(mini.get("type").getValue())
        if (!type) {
            return {"result": false, "message": "请填写参数类型"};
        }
        var name = $.trim(mini.get("name").getValue())
        if (!name) {
            return {"result": false, "message": "请填写参数名称"};
        }
        var number = $.trim(mini.get("number").getValue())
        if (!number) {
            return {"result": false, "message": "请填写参数值"};
        }
        var weight = $.trim(mini.get("weight").getValue())
        if (!weight) {
            return {"result": false, "message": "请填写重要度"};
        }
        return {"result": true};
    }

    function savecs() {
        var formValid = validJscsXz();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = new mini.Form("formJscsDetail");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/jscs/saveJscsDetail.do?belongJs='+belongJs,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.alert(data.message, "提示消息", function (action) {
                        if (action == 'ok') {
                            // var url= jsUseCtxPath+"/jscs/editJscsDetail.do?" +
                            //     "csId=" + data.data + "&action=edit&jsId="+belongJs;
                            // window.location.href=url;
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnJscsPreviewSpan(record.fileName,record.fileId,record.belongCs,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJscsFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongCs+'\')">下载</span>';

        //增加删除按钮
        if((action=='edit' ||action=='add')&&record.CREATE_BY_==currentUserId) {
            var deleteUrl="/jscs/deleteJscsFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongCs+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
