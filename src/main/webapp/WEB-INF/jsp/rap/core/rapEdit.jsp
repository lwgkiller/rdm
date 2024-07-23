
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rap/rapEdit.js?version=${static_res_version}"type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="detailToolBar" class="topToolBar" >
    <div>
        <a id="save" name="save" class="mini-button" style="display: none" onclick="save()">保存</a>
        <a id="submit" name="submit" class="mini-button" style="display: none" onclick="submit()">提交</a>
        <a id="approve" name="approve" class="mini-button" style="display: none" onclick="approve()">通过</a>
        <a id="reject" name="reject" class="mini-button" style="display: none" onclick="reject()">驳回</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRap" method="post">
            <input id="rapId" name="rapId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%;font-size:14pt">法规/政策名称：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="rapName" name="rapName"  class="mini-textbox"  style="width:98%;" />
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left">排放阶段：</td>
                    <td>
                        <input id="rapStatus" name="rapStatus" class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'国四',text:'国四'},{id:'国三',text:'国三'}]"
                               required="true" allowInput="true" showNullItem="true" nullItemText="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;width: 20%;text-align: left">所属国家/省份：</td>
                    <td>
                        <input id="rapArea" name="rapArea" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left">实施日期：</td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="rapDate" name="rapDate" showTime="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/environment/core/Rap/getFileList.do?rapId=${rapId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
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
    var action="${action}";
    var jsUseCtxPath = "${ctxPath}";
    var rapListGrid = mini.get("rapListGrid");
    var rapId = "${rapId}";
    var formRap = new mini.Form("#formRap");
    var fileListGrid=mini.get("fileListGrid");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnRapPreviewSpan(record.fileName,record.fileId,record.rapId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadRapFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.rapId+'\')">下载</span>';
        //增加删除按钮
        if(action=='edit') {
            var deleteUrl="/environment/core/Rap/deleterapFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.rapId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>