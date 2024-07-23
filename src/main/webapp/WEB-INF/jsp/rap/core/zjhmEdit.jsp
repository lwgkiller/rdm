<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>整机豁免信息收集页面</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rap/zjhmEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formProject" method="post">
            <input id="projectId" name="projectId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td rowspan="2" width="15%">流程基本信息<br>(流程发起人勾选或填写)</td>
                    <td style="width: 10% ">机械型号：</td>
                    <td style="width: 20%">
                        <input id="jxxh" name="jxxh" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 10% ">产品主管：</td>
                    <td style="width: 20%;min-width:170px ">
                        <input id="cpzgId" name="cpzgId" textname="cpzgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: left">是否需要发动机相关环保认证证书：</td>
                    <td style="width: 20%;text-align:center">
                        <input  id="needFile" name="needFile" class="mini-checkbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td rowspan="1" width="15%">动力工程师处理人<br>(产品主管填写)</td>
                    <td style="  width: 10%;text-align: left">动力工程师：</td>
                    <td style="width: 20%;min-width:170px ">
                        <input id="dlgcsId" name="dlgcsId" textname="dlgcsName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td rowspan="2" width="15%">发动机信息和相关环保认证证书<br>(动力工程师填写或上传)</td>
                    <td style="  width: 10%;text-align: left">发动机信息公开编号：</td>
                    <td style="width: 20%">
                        <input id="fdjgkbh" name="fdjgkbh" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="  width: 10%;text-align: left">发动机制造商：</td>
                    <td style="width: 20%">
                        <input id="fdjzzs" name="fdjzzs" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 10%;text-align: left">发动机型号：</td>
                    <td style="width: 20%">
                        <input id="fdjxh" name="fdjxh" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td rowspan="2">上传发动机相关环保认证证书</td>
                </tr>
                <tr>
                    <td style="width: 10%;height: 300px ">相关信息附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Zjhm/getZjhmFileList.do?projectId=${projectId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
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
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var projectId = "${projectId}";
    var formProject = new mini.Form("#formProject");
    var fileListGrid = mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.belongId, coverContent);
        // cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        //     'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        //增加删除按钮
        if ((isdlgcs == '1') && record.CREATE_BY_==currentUserId) {
            var deleteUrl = "/environment/core/Zjhm/delUploadFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        //复用接口
        var downloadUrl =  "/environment/core/Zjhm/fileDownload.do";
        var s='';
        if(fileType=='other'){
            s = '<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        }else if(fileType=='pdf'){
            var url='/environment/core/Zjhm/fileDownload.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+downloadUrl+ '\')">' + "预览" + '</span>';
        }else if(fileType=='office'){
            var url='/environment/core/Zjhm/officePreview.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">' + "预览" + '</span>';
        }else if(fileType=='pic'){
            var url='/environment/core/Zjhm/imagePreview.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">' + "预览" + '</span>';
        }
        s += '&nbsp;&nbsp;&nbsp;<span title=' + '下载' + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\''+fileName+'\',\''+fileId+'\',\''+formId+'\',\''+downloadUrl+'\')">' + '下载' + '</span>';
        return s;
    }
</script>
</body>
</html>