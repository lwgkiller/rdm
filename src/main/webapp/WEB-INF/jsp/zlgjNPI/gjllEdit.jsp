<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/zlgjNPI/gjllEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
                    <td style="width: 10%">问题类型：<span style="color:red">*</span></td>
                    <td style="width: 20%;min-width:140px">
                        <input id="wtlx" name="wtlx" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="true" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                    </td>
                    <td style="width: 20%;text-align: left">机型类别：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="jiXing" name="jiXing" class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'微挖',text:'微挖'},{id:'小挖',text:'小挖'}
                               ,{id:'中挖',text:'中挖'},{id:'大挖',text:'大挖'},{id:'轮挖',text:'轮挖'}]"
                               required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">机型：<span style="color: #ff0000">*</span></td>
                    <td style="width: 33%;min-width:170px;">
                        <input id="smallJiXing" name="smallJiXing" required="true" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">改进措施完成时间：</td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="qhTime" name="qhTime"
                               showTime="true"/>
                    </td>
                </tr>
                <tr>
                    <td>责任人：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="zrrId" name="zrrId" textname="zrrName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;"
                               allowinput="false"  length="50" maxlength="50"  mainfield="no"  single="true"/>
                    </td>
                    <td>责任部门：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="ssbmId" name="ssbmId" textname="ssbmName" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="ssbmName" length="500" maxlength="500" minlen="0" single="true"
                               initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">标准化文件：<span style="color: #ff0000">*</span></td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="tzdh" name="tzdh" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">预计切换车号：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="yjqhch" name="yjqhch" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">零部件：<span style="color: #ff0000">*</span></td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="gzlj" name="gzlj" class="mini-textbox" required="true" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">零部件供应商：</td>
                    <td>
                        <input id="lbjgys" name="lbjgys" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">问题描述：<span style="color: #ff0000">*</span></td>
                    <td colspan="3" style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="wtms" name="wtms" required="true" class="mini-textarea" style="width:98%;overflow: auto;height: 80px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">原因分析：<span style="color: #ff0000">*</span></td>
                    <td colspan="3" style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="reason" name="reason" required="true" class="mini-textarea" style="width:98%;overflow: auto;height: 80px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">改进方案：<span style="color: #ff0000">*</span></td>
                    <td colspan="3" style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="cqcs" name="cqcs" required="true" class="mini-textarea" style="width:98%;overflow: auto;height: 80px"/>
                    </td>
                </tr>

                <tr>
                    <td style="width: 14%;height: 300px">问题图片：</td>
                    <td colspan="3">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/zlgjNPI/core/Gjll/getFileList.do?gjId=${gjId}"
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
            var deleteUrl = "/zlgjNPI/core/Gjll/deleteGjllFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.gjId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>