<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>年度开发计划-年度预算</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhBudgetEdit.js?version=${static_res_version}"
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
    <div class="form-container">
        <form id="infoForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    年度开发计划-年度预算维护
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
                        所属年份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="budgetYear" name="budgetYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        项目编号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="projectCode" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
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
                        工作目标：<span style="color:red">*</span>
                    </td>
                    <td colspan="3">
						<textarea id="target" name="target" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:80px;line-height:25px;" required
                                  label="工作目标" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true" emptytext="工作目标..." hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        工作内容<br>(预算当年工作内容)：<span style="color:red">*</span>
                    </td>
                    <td colspan="3">
						<textarea id="content" name="content" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:80px;line-height:25px;" required
                                  label="工作内容" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true" emptytext="工作内容..." hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        关键/核心技术：<span style="color:red">*</span>
                    </td>
                    <td colspan="3">
						<textarea id="coreTechnology" name="coreTechnology" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:80px;line-height:25px;" required
                                  label="关键/核心技术" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true" emptytext="关键/核心技术..." hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        产出新产品合计（台套数）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="newProductNum"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        其中：首台套（名称）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="firstName"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        其中：升级产品（名称）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="upProductName"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        其中：改进产品（名称）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="improveName"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        首席工程师：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="chiefEngineer"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        发明专利（预算当年数）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="patentNum"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目开始日期：<span style="color: #ff0000">*</span>
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目结束日期：<span style="color: #ff0000">*</span>
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                </tr>
                </tbody>
            </table>
            <fieldset id="fdAchievementInfo" class="hideFieldset" >
                <legend>
                    <label style="font-size:17px">
                        产品信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="projectAchievementButtons">
                        <a class="mini-button" id="addProductButton"   plain="true" onclick="addProduct()">添加</a>
                        <a class="mini-button btn-red" id="delProductButton"  plain="true" onclick="delProduct()">删除</a>
                    </div>
                    <div id="grid_product" class="mini-datagrid"  allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div type="indexcolumn" headerAlign="center" align="center"  width="15">序号</div>
                            <div field="id" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                            <div field="products"  headerAlign="center" align="center" width="100">产品列表
                                <input property="editor" class="mini-textbox" />
                            </div>
                            <div field="charge"  displayField="userName" headerAlign="center" align="center" width="30">负责人
                                <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                       mainfield="no"  single="true" />
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var infoForm = new mini.Form("#infoForm");
    var grid_product = mini.get("#grid_product");
</script>
</body>
</html>
