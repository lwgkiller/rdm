<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhAddApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    年度开发计划新增申请表单
                </caption>
				<tr>
					<td style="text-align: center;width: 20%">申请人：</td>
					<td style="min-width:170px">
						<input id="applyUserId" name="applyUserId" textname="applyUserName"
							   class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
							   label="申请人" length="50" mainfield="no" single="true" enabled="false"/>
					</td>
					<td style="text-align: center;width: 20%">部门：</td>
					<td style="min-width:170px">
						<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
							   maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
							   mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
					</td>
				<tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        项目/产品名称<span style="color: red">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productName" required  class="mini-textbox" style="width:100%;height:34px">
                    </td>
				<td align="center" style="white-space: nowrap;">
					责任所长<span style="color: red">*</span>：
				</td>
				<td align="center" colspan="1" rowspan="1">
					<input id="responsor" name="responsor" textname="responsorName" required
						   class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
						   label="责任所长" length="50" mainfield="no" single="true" enabled="true"/>
				</td>
                </tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						项目开始日期<span style="color: red">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="startDate" name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						项目结束日期<span style="color: red">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="endDate" name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td style="text-align: center">年度目标<span style="color: red">*</span>：</td>
					<td colspan="3">
						<textarea id="target" name="target" class="mini-textarea rxc" plugins="mini-textarea"
								  style="width:99%;height:50px;line-height:25px;"
								  label="年度目标" datatype="varchar" length="500" vtype="length:500" minlen="0"
								  allowinput="true" required
								  emptytext="请输入年度目标..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						负责人：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="chargerMan" name="chargerMan" textname="chargerManName"
							   class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
							   label="负责人" length="50" mainfield="no" single="true" enabled="true"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						部门：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="chargerDept" name="chargerDept" class="mini-dep rxc" plugins="mini-dep"
							   style="width:100%;height:34px" readonly emptytext="后台自动带入..."
							   allowinput="false" textname="chargerDeptName" length="200" maxlength="200" minlen="0"
							   single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						进度年月<span style="color: red">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="yearMonth" allowinput="false" class="mini-monthpicker" required="true"
							   style="width:100%;height:34px" name="yearMonth"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						当前阶段<span style="color: red">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="currentStage" required name="currentStage"  class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						当前阶段要求完成时间<span style="color: red">*</span>：
					</td>
					<td colspan="1">
						<input id="stageFinishDate" name="stageFinishDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
					<td align="center" style="white-space: nowrap;">
						完成率：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="finishRate"  class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						是否延期<span style="color: red">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="isDelay" name="isDelay" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px" label="是否延期："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" style="white-space: nowrap;">
						延期天数：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="delayDays" name="delayDays"  class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
				</tr>
				<tr>
					<td style="text-align: center">备注说明：</td>
					<td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
								  style="width:99%;height:50px;line-height:25px;"
								  label="备注说明" datatype="varchar" length="500" vtype="length:500" minlen="0"
								  allowinput="true"
								  emptytext="请输入备注说明..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
            </table>
        </form>
    </div>

</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    let jsUseCtxPath = "${ctxPath}";
    let ApplyObj =${applyObj};
    let status = "${status}";
    let applyForm = new mini.Form("#applyForm");
</script>
</body>
</html>
