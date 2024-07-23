<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新品附属件细节编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" onclick="savefsjxj()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" style="height: 400px">
    <div class="form-container" style="margin: 0 auto; width: 100%;height: 100%">
        <form id="formFsjDetail" method="post">
            <input id="detailId" name="detailId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    新品附属件细节表
                </caption>
                <tr>
                    <td style="width: 7%">部件名称<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="bjname" name="bjname" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">供应商名称<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="supplier" name="supplier" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">设计B10寿命：</td>
                    <td style="min-width:170px;font-size:14pt">
                        <input id="bten" name="bten" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">3000h失效率：</td>
                    <td>
                        <input id="sqsx" name="sqsx" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">7000h失效率：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="qqsx" name="qqsx" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">整机三包期：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="zjsb" name="zjsb" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">部件三包期：</td>
                    <td>
                        <input id="bjsb" name="bjsb" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">差异：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="cy" name="cy" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">寿命验证方法：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="smyz" name="smyz" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">完成日期：</td>
                    <td>
                        <input id="wcrq" name="wcrq" class="mini-datepicker" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">工作装置：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="gzzz" name="gzzz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">破碎锤厂家：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="psccj" name="psccj" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">破碎锤型号：</td>
                    <td>
                        <input id="pscxh" name="pscxh" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">破碎管路：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="psgl" name="psgl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">破碎锤物料号：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="pscwlh" name="pscwlh" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">设计破碎管路物料号：</td>
                    <td>
                        <input id="sjglwl" name="sjglwl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">备件破碎管路物料号：</td>
                    <td style="width: 25%;min-width:170px;font-size:14pt">
                        <input id="bjglwl" name="bjglwl" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <%--<tr>--%>
                    <%--<td style="width: 15%;height: 300px;font-size:14pt">附件列表：</td>--%>
                    <%--<td colspan="3">--%>
                        <%--<div style="margin-top: 20px;margin-bottom: 2px">--%>
                            <%--<a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>--%>
                        <%--</div>--%>
                        <%--<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"--%>
                             <%--allowResize="false"--%>
                             <%--idField="id" autoload="true"--%>
                             <%--url="${ctxPath}/zlgjNPI/core/Fsj/getFsjFileList.do?belongDetailId=${detailId}"--%>
                             <%--multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">--%>
                            <%--<div property="columns">--%>
                                <%--<div gytype="indexcolumn" align="center" width="20">序号</div>--%>
                                <%--<div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>--%>
                                <%--<div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>--%>
                                <%--<div field="action" width="100" headerAlign='center' align="center"--%>
                                     <%--renderer="operationRendererl">操作--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</td>--%>
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
    var formFsjDetail = new mini.Form("#formFsjDetail");
    var detailId = "${detailId}";
    var belongId = "${belongId}";
    var action = "${action}";
    var first = "${first}";
    var status = "${status}";
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
        if (detailId) {
            var url = jsUseCtxPath + "/zlgjNPI/core/Fsj/getFsjDetail.do";
            $.post(
                url,
                {detailId: detailId},
                function (json) {
                    formFsjDetail.setData(json);
                });
        }
        //变更入口
        if (action == "detail") {
            formFsjDetail.setEnabled(false);
            $("#saveBtn").hide();
            // mini.get("saveBtn").setEnabled(false);
            // mini.get("addFile").setEnabled(false);
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
            url: jsUseCtxPath + "/zlgjNPI/core/Fsj/openUploadWindow.do?detailId=" + detailId,
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


    function returnFsjPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/zlgjNPI/core/Fsj/fsjPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/zlgjNPI/core/Fsj/fsjOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/zlgjNPI/core/Fsj/fsjImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadFsjFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/zlgjNPI/core/Fsj/fsjPdfPreview.do?action=download');
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


    function validFsjChuangjian() {
        var bjname = $.trim(mini.get("bjname").getValue())
        if (!bjname) {
            return {"result": false, "message": "请填写部件名称"};
        }
        var supplier = $.trim(mini.get("supplier").getValue())
        if (!supplier) {
            return {"result": false, "message": "请填写供应商名称"};
        }
        return {"result": true};
    }
    

    function savefsjxj() {
        if(status=="DRAFTED"||first=='yes'){
            var formValid = validFsjChuangjian();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        var formData = new mini.Form("formFsjDetail");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/zlgjNPI/core/Fsj/saveFsjDetail.do?belongId='+belongId,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.alert(data.message, "提示消息", function (action) {
                        if (action == 'ok') {
                            var url= jsUseCtxPath+"/zlgjNPI/core/Fsj/editFsjDetail.do?" +
                                "detailId=" + data.data + "&action=edit&fsjId="+belongId+"&first="+first;
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
        cellHtml=returnFsjPreviewSpan(record.fileName,record.fileId,record.belongDetailId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFsjFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongDetailId+'\')">下载</span>';

        //增加删除按钮
        if((action=='edit' ||action=='add')&&record.CREATE_BY_==currentUserId) {
            var deleteUrl="/zlgjNPI/core/Fsj/deleteFsjFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongDetailId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
