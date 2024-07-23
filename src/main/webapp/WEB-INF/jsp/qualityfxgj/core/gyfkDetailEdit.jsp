<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>工艺反馈信息说明细节编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/qualityfxgj/gyfkDetailEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" onclick="savegyfkxj()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" style="height: 800px">
    <div class="form-container" style="margin: 0 auto; width: 100%;height: 100%">
        <form id="formGyfkDetail" method="post">
            <input id="gyxjId" name="gyxjId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    工艺信息反馈
                </caption>
                <tr>
                    <td style="width: 7%">类型<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="gytype" name="gytype" class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..." valueFromSelect="true"
                               data="[{id:'可靠性',text:'可靠性'},{id:'系列化与标准化',text:'系列化与标准化'},
							   {id:'制造成本',text:'制造成本'},{id:'生产效率',text:'生产效率'}]"
                               required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>
                    <td style="width: 7%">问题描述<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="problem" name="problem" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">机型<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="model" name="model" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">部件：</td>
                    <td>
                        <input id="part" name="part" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">涉及机型：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="involveModel" name="involveModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 7%">发起人<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="applyId" name="applyId" textname="apply"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="发起人" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">责任人<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="resId" name="resId" textname="res"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="责任人" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="width: 7%">完成时间<span style="color: #ff0000">*</span>：</td>
                    <td>
                        <input id="finishTime" name="finishTime" class="mini-datepicker"  style="width:100%;height:34px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">对策<span style="color: #ff0000">*</span>：</td>
                    <td colspan="3">
                        <input id="method" name="method" class="mini-textarea" style="width:98%;height: 100px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">完成情况<span style="color: #ff0000">*</span>：</td>
                    <td colspan="3">
                        <input id="completion" name="completion" class="mini-textarea" style="width:98%;height: 100px"/>
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
                             url="${ctxPath}/qualityfxgj/core/Gyfk/getGyfkFileList.do?belongDetailId=${gyxjId}"
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
    var formGyfkDetail = new mini.Form("#formGyfkDetail");
    var gyxjId = "${gyxjId}";
    var belongGyfkId = "${belongGyfkId}";
    var action = "${action}";
    var isChuangjian = "${isChuangjian}";
    var isZZRXZ = "${isZZRXZ}";
    var status = "${status}";
    var chuli = ${chuli};
    var zrr = ${zrr};
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



    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnGyfkPreviewSpan(record.fileName,record.fileId,record.belongDetailId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadGyfkFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongDetailId+'\')">下载</span>';

        //增加删除按钮
        if((action=='edit' ||action=='add')&&record.CREATE_BY_==currentUserId) {
            var deleteUrl="/qualityfxgj/core/Gyfk/deleteGyfkFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongDetailId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
