<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/gzxmProjectEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" >
        <form id="planForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    国重项目信息
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        项目名称<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="projectName" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        项目负责人<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="responsor" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        项目承担单位：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="responseDept"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目开始日期<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目结束日期<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        进度状态：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="currentStage" name="currentStage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="进度状态："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCJD"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        填报人<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="reportUserId" name="reportUserId" class="mini-user" textname="reportUserName"  required single="false" allowInput="false" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        进度详述：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="processDetail" class="mini-textarea rxc" style="width:100%;height:50px">
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
        </div>
</div>
        <script type="text/javascript">
            mini.parse();
            var jsUseCtxPath = "${ctxPath}";
            var applyObj = ${applyObj};
            var action = '${action}';
            var planForm = new mini.Form("#planForm");
            var gzxmAdmin = ${gzxmAdmin};
            var isReporter = ${isReporter};
        </script>
</body>
</html>
