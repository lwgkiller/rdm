<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/3/31
  Time: 16:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>创建新技术</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/xcmgjssjk/xcmgjssjkCreatexjs.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveNewjsDraft" class="mini-button" style="" onclick="saveNewjsDraft()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto;width: 100%;">
        <form id="formJssjk" method="post" >
            <input id="jssjkId" name="UUID" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 14%">项目编号：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="XMBH" name="XMBH"  class="mini-textbox" style="width:98%;" value=""/>
                    </td>
                    <td style="width: 14%">项目名称：</td>
                    <td style="width: 36%;">
                        <input id="XMMC" name="XMMC"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">项目负责人：</td>
                    <td style="width: 36%;min-width:140px">
                        <%--<input id="XMFZR_NAME" name="XMFZR_NAME"  class="mini-textbox" style="width:98%;" />--%>
                        <input id="XMFZR_NAME" name="XMFZR_NAME" textname="readUserNames" enabled="false" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
                            <input id="XMFZR_ID" name="XMFZR_ID" class="mini-hidden"/>
                    </td>
                    <td style="width: 14%">联系方式：</td>
                    <td style="width: 36%;">
                        <input id="LXDH" name="LXDH"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">实施计划：</td>
                    <td style="width: 36%;min-width:140px">
                        <a id="ssjh" name="ssjh"  class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addNjsFile">添加附件</a>
                    </td>
                    <td style="width: 14%">开发状态：</td>
                    <td style="width: 36%;">
                        <input id="KFZT" name="KFZT" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'RUNNING','value' : '开发中'},{'key' : 'SUCCESS_END','value' : '完成'},
                                   {'key' : 'APPLY_END','value' : '应用'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">项目章程：</td>
                    <td style="width: 36%;min-width:140px">
                        <%--<input id="xmzc" name="xmzc"  class="mini-textbox" style="width:98%;" />--%>
                        <a id="xmzcuploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addNjsFile2">添加附件</a>
                    </td>
                    <td style="width: 14%">项目类别：</td>
                    <td style="width: 36%;">
                        <%--<input id="XMLB" name="XMLB"  class="mini-textbox" style="width:98%;" />--%>
                        <input id="XMLB" name="XMLB" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'YEYA','value' : '液压技术'},{'key' : 'KONGZHI','value' : '电控技术'},
                                   {'key' : 'CESHI','value' : '测试仿真技术'},{'key' : 'QITA','value' : '其他技术'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">应用案例：</td>
                    <td style="width: 36%;min-width:140px">
                        <%--<input id="yyal" name="yyal"  class="mini-textbox" style="width:98%;" />--%>
                        <a id="yyaluploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addNjsFile3">添加附件</a>
                    </td>
                    <td style="width: 14%">NPI编号信息：</td>
                    <td style="width: 36%;">
                        <input id="NPIBHXX" name="NPIBHXX"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术描述：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="JSMS" name="JSMS" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">团队花名册：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="TDHMC" name="TDHMC" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">经验教训：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="JYJX" name="JYJX" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status="${status}";
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var formJssjk = new mini.Form("#formJssjk");
    var jssjkId="${jssjkId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var jssjkObj=${jssjkObj};
    var xmfzrid="${XMFZR_NAME}";
    var xmfzrName="${readUserNames}";


</script>
</body>
</html>
