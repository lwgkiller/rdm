<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div style="margin-top: 2px">
        <a style="margin-top: 2px" id="addChangeFile" class="mini-button" onclick="fileupload()">添加附件</a>
    </div>
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/serviceEngineer/core/SchematicApply/getSchematicApplyFileList.do?id=${id}"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn" align="center" width="20">序号</div>
            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
            <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var action = "${action}";
    var stageName = "${stageName}";
    var fileListGrid = mini.get("#fileListGrid");
    var id = "${id}";
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var currentUserId = "${currentUserId}";
    var isFwgc = '${isFwgc}';

    $(function () {
         if (stageName != "third"&&stageName != "forth") {
             $("#addChangeFile").hide();
         }
    });

    function fileupload() {
        if (!id) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/serviceEngineer/core/SchematicApply/openUploadWindow.do?id=" + id,
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
    function returnSchematicApplyPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyApplyPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSchematicApplyPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        var downLoadUrl = '/serviceEngineer/core/SchematicApply/schematicApplyPdfPreview.do';
        if(isFwgc=='true') {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + downLoadUrl + '\')">下载</span>';
        }
        //增加删除按钮
        if ((record.CREATE_BY_ == currentUserId && action != 'detail')||stageName=='forth') {
            var deleteUrl = "/serviceEngineer/core/SchematicApply/deleteSchematicApplyFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
</script>
</body>
</html>
