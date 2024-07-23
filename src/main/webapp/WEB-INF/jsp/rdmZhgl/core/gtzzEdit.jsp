<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/gtzzEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
                    重大项目信息
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
                    <td align="center" colspan="1" rowspan="1">
                        <input name="projectName" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        年度<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="projectYear" name="projectYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        当前阶段<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="currentStage" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        完成进度：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="finishProcess" name="finishProcess" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="完成情况："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCJD"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        进度详述：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="processDetail"   class="mini-textarea rxc" style="width:100%;height:50px">
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
        <div class="mini-toolbar">
            <ul class="toolBtnBox">
                <li style="float: left">
                    <a class="mini-button" onclick="addItem()">新增</a>
                    <a class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
                </li>
            </ul>
        </div>
        <div class="mini-fit" style="margin-top: 10px">
            <div id="itemGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                 url="${ctxPath}/rdmZhgl/core/gtzz/items.do"
                 idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
                 allowCellSelect="true" allowCellWrap="true"
                 editNextOnEnterKey="true" editNextRowCell="true">
                <div property="columns">
                    <div type="checkcolumn" width="30px"></div>
                    <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
                    <div field="indexSort" displayfield="indexSort" width="100px" headerAlign="center" align="center">
                        编号<span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox" allowLimitValue="false"
                               required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                               decimalPlaces="0" style="width:100%;height:34px"/>
                    </div>
                    <div field="important"   width="60px"  headerAlign="center" renderer="onImportant" align="center">
                        重要度<span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                               emptyText="请选择..." required
                               showNullItem="false" nullItemText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZDXMZYJD"/>

                    </div>
                    <div field="projectName" displayfield="projectName" width="200px" headerAlign="center" align="left">
                        项目名称<span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox" allowLimitValue="false"
                               required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                               decimalPlaces="0" style="width:100%;height:34px"/>
                    </div>
                    <div field="projectTarget" displayfield="projectTarget" width="300px" headerAlign="center"
                         align="left">
                        项目目标<span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textarea" allowLimitValue="false"
                               required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                               decimalPlaces="0" style="width:100%;height:34px"/>
                    </div>
                    <div field="outputFile" displayfield="outputFile" width="300px" headerAlign="center" align="left">
                        输出物<span
							style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textarea" allowLimitValue="false"
                               required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                               decimalPlaces="0" style="width:100%;height:34px"/>
                    </div>
                    <div field="resDeptIds" displayField="resDeptIds" align="center" width="150px"  headerAlign="center">
                        责任部门<span
                            style="color: #ff0000">*</span>
						<input property="editor" class="mini-textbox" allowLimitValue="false"
							   required="true" only_read="false" allowinput="true" allowNull="true" value="null"
							   decimalPlaces="0" style="width:100%;height:34px"/>
                    </div>
					<div field="resUserIds" displayfield="resUserIds" width="150px" headerAlign="center" align="center">责任人<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textbox" allowLimitValue="false"
							   required="true" only_read="false" allowinput="true" allowNull="true" value="null"
							   decimalPlaces="0" style="width:100%;height:34px"/>
					</div>
					<div field="planStartDate" width="100px" headeralign="center" allowsort="true" align="center" dateFormat="yyyy-MM-dd">
						计划开始时间<span
                            style="color: #ff0000">*</span>
						<input property="editor" required="true"   class="mini-datepicker" required  style="width:100%;"/>
					</div>
					<div field="planEndDate" width="100px" headeralign="center" allowsort="true" align="center" dateFormat="yyyy-MM-dd">
						计划结束时间<span
                            style="color: #ff0000">*</span>
						<input property="editor"  required="true"  class="mini-datepicker"  style="width:100%;"/>
					</div>
					<div field="actStartDate" width="90px" headeralign="center" allowsort="true" align="center" dateFormat="yyyy-MM-dd">
						实际开始时间
						<input property="editor"   class="mini-datepicker"  style="width:100%;"/>
					</div>
					<div field="actEndDate" width="90px" headeralign="center" allowsort="true" align="center" dateFormat="yyyy-MM-dd">
						实际结束时间
						<input property="editor"   class="mini-datepicker"  style="width:100%;"/>
					</div>
					<div field="reason" displayfield="reason" width="150px" headerAlign="center" align="center">
						延期原因与补救措施
						<input property="editor" class="mini-textbox" allowLimitValue="false"
							   required="false" only_read="false" allowinput="true" allowNull="true" value="null"
							   decimalPlaces="0" style="width:100%;height:34px"/>
					</div>
                </div>
            </div>
        </div>
    </div>


</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var planForm = new mini.Form("#planForm");
    var itemGrid = mini.get("itemGrid");
    var importantList = getDics("ZDXMZYJD");


    function onImportant(e) {
        var record = e.record;
        var value = record.important;
        var resultText = '';
        for (var i = 0; i < importantList.length; i++) {
            if (importantList[i].key_ == value) {
                resultText = importantList[i].text;
                break
            }
        }
        return resultText;
    }
    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async: false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
</body>
</html>
