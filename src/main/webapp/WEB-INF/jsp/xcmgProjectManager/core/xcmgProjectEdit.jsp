
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>项目信息</title>
	<%@include file="/commons/edit.jsp"%>
	<%--<%@include file="/WEB-INF/jsp/sys/echarts/echartsTheme.jsp"%>--%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectProcess.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectChange.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
	<script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
	<link  href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css" />
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />

	<style type="text/css">
		fieldset
		{
			border:solid 1px #aaaaaab3;
			min-width: 920px;
		}
		.hideFieldset
		{
			border-left:0;
			border-right:0;
			border-bottom:0;
		}
		.hideFieldset .fieldset-body
		{
			display:none;
		}
		.processStage{
			background-color: #ccc !important;
			font-size:15px !important;
			font-family:'微软雅黑' !important;
			text-align:center !important;
			vertical-align:middle !important;
			color: #201f35 !important;
			height: 30px !important;
			border-right:solid 0.5px #666;
		}
		.rmMem .mini-grid-cell-inner{
			color: red !important;
		}
	</style>
</head>


<body>
<a class="mini-button" id="anchor" style="display: none" href=""><spring:message code="page.xcmgProjectEdit.name1" /></a>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.xcmgProjectEdit.name2" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.xcmgProjectEdit.name3" /></a>
	</div>
</div>
<div id="changeToolBar" class="topToolBar" style="display: none">
	<div>
<%--		保存变更--%>
		<a id="saveChange" class="mini-button" onclick="saveChange()"><spring:message code="page.xcmgProjectEdit.name4" /></a>
		<a class="mini-button" onclick="CloseWindow()"><spring:message code="page.xcmgProjectEdit.name3" /></a>
	</div>
</div>
<div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
<div class="mini-fit" id="content">
	<div class="form-container" style="margin: 0 auto">
<%--		整个项目表单--%>
		<form id="formProject" method="post">
			<input id="projectId" name="projectId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="progressRunStatus" name="progressRunStatus" class="mini-hidden"/>
			<input id="scoreGetTime" name="scoreGetTime" class="mini-hidden"/>
			<div id="processDiv" style="min-width: 920px;height:85px;margin-bottom: 5px">
			</div>
			<fieldset id="fdBaseInfo" >
				<legend>
					<label style="font-size:17px">
						<input type="checkbox" checked id="checkboxBaseInfo" onclick="toggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name5" />
					</label>
				</legend>

				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<table class="table-detail" cellspacing="1" cellpadding="0">
						<tr>
                            <td style="width: 14%"><spring:message code="page.xcmgProjectEdit.name6" />：<span style="color: #ff0000">*</span></td>
                            <td style="width: 21%">
                                <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:98%;"
                                       textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
                                       allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..."  readonly/>
                            </td>
							<td style="width: 14%"><spring:message code="page.xcmgProjectEdit.name8" />：<span style="color: #ff0000">*</span></td>
							<td style="width: 19%;min-width:170px">
								<input id="projectName" name="projectName"  class="mini-textbox" style="width:98%;" />
							</td>
							<td style="width: 14%"><spring:message code="page.xcmgProjectEdit.name9" />：<span style="color: #ff0000">*</span></td>
							<td style="width: 18%;min-width:140px">
								<input id="projectSource" name="sourceId" class="mini-combobox" style="width:98%;"
									   textField="sourceName" valueField="sourceId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
									   allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..." onclick="sourceClick()" onvaluechanged="sourceChanged()"/>
							</td>
						</tr>
						<tr>
							<td><spring:message code="page.xcmgProjectEdit.name10" />：</td>
							<td>
								<input id="projectNumber" name="number"  class="mini-textbox" style="width:98%;" />
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name13" />：<span style="color: #ff0000">*</span></td>
							<td >
								<div style="width: 95%;height:100%;">
									<input id="beginLevel" name="beginLevelId" class="mini-radiobuttonlist" style="width:100%;"
										   repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
										   textfield="levelName" valuefield="levelId" onvaluechanged="levelChange()" />
								</div>
							</td>
                            <td><spring:message code="page.xcmgProjectEdit.name14" />：</td>
                            <td>
                                <input id="pointStandardScore" name="pointStandardScore" class="mini-textbox" style="width:98%;"/>
                            </td>
						</tr>
                        <tr>
                            <td><spring:message code="page.xcmgProjectEdit.name15" />：<span style="color: #ff0000">*</span></td>
                            <td>
                                <input id="mainDepId" name="mainDepId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="mainDepName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                            </td>
                            <td><spring:message code="page.xcmgProjectEdit.name16" />：</td>
                            <td>
                                <input id="otherDepId" name="otherDepId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="otherDepName" length="500" maxlength="500" minlen="0" single="false" initlogindep="false"/>
                            </td>
                            <td><spring:message code="page.xcmgProjectEdit.name17" />：<span style="color: #ff0000">*</span></td>
                            <td>
                                <input id="ysbh" name="ysbh"  class="mini-textbox" style="width:98%;" />
                            </td>
                        </tr>
						<tr>
                            <td><spring:message code="page.xcmgProjectEdit.name18" />：</td>
                            <td>
                                <input id="cwddh" name="cwddh"  class="mini-textbox" style="width:98%;" />
                            </td>
                            <td><spring:message code="page.xcmgProjectEdit.name19" />：</td>
                            <td>
                                <input id="gbcwddh" name="gbcwddh"  class="mini-textbox" style="width:98%;" />
                            </td>
                            <td><spring:message code="page.xcmgProjectEdit.name20" />：<span style="color: #ff0000">*</span></td>
							<td>
								<input id="respmanId" name="respman" showclose="false" textname="respmanName" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.xcmgProjectEdit.name20" />" length="50" maxlength="50"  mainfield="no"
                                       single="true" onvaluechanged="setRespMan()"/>
							</td>
						</tr>
						<tr id="hzdwTr" style="display: none">
							<td><spring:message code="page.xcmgProjectEdit.name21" />：<span style="color: #ff0000">*</span></td>
							<td colspan="5">
								<input id="hzdw" name="hzdw"  class="mini-textbox" style="width:98%;" />
							</td>
						</tr>
						<tr>
							<td><spring:message code="page.xcmgProjectEdit.name22" />：</td>
							<td>
								<input id="creator" name="creator" class="mini-textbox" style="width:98%;" readonly/>
							</td>
							<td style="display: none"><spring:message code="page.xcmgProjectEdit.name23" />：</td>
							<td style="display: none">
								<input id="createTime" name="createTime" class="mini-hidden" style="width:98%;"/>
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name24" />：</td>
							<td>
								<input id="buildTime" name="buildTime" class="mini-textbox" style="width:98%;" readonly/>
							</td>

							<td style="display: none"><spring:message code="page.xcmgProjectEdit.name25" />：</td>
							<td style="display: none">
								<input id="currentStageId" name="currentStageId" class="mini-hidden"/>
								<input id="currentStageNo" name="currentStageNo" class="mini-hidden"/>
								<input  id="currentStageName" name="currentStageName"  class="mini-textbox" style="width:98%;" readonly/>
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name26" />：</td>
							<td>
								<input name="knotTime"  class="mini-textbox" style="width:98%;" readonly/>
							</td>
						</tr>
						<tr>
							<td><spring:message code="page.xcmgProjectEdit.name27" />(1-100)：<span style="color: #ff0000">*</span></td>
							<td>
								<input id="knotScoreId" name="knotScore" class="mini-textbox" style="width:98%;" onBlur="setRatingNameByKnotScore(true)" />
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name28" />：</td>
							<td>
								<input id="ratingNameId" name="ratingName"  class="mini-textbox" style="width:98%;" readonly/>
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name29" />：<span style="color: #ff0000">*</span></td>
							<td>
								<input id="knotRatioId" name="knotRatio"  class="mini-textbox" style="width:60%;" onclick="checkKnotScore()" onBlur="checkKnotRatio()" />
								<u style="float: right;"><a href="#" onclick="showKnotLevelDivideWin()"><spring:message code="page.xcmgProjectEdit.name30" /></a></u>
							</td>
						</tr>
						<tr>
							<td><spring:message code="page.xcmgProjectEdit.name135" />：</td>
							<td >
								<div style="width: 95%;height:100%;">
									<input id="level" name="levelId" class="mini-radiobuttonlist" style="width:100%;"
										   repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
										   textfield="levelName" valuefield="levelId"  />
								</div>
							</td>
							<td><spring:message code="page.xcmgProjectEdit.name31" />：</td>
							<td colspan="3">
								<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;height:80px; line-height:25px;" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"   mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
							</td>
						</tr>
					</table>
				</div>
			</fieldset>
			<br>

			<fieldset id="fdMemberInfo" class="hideFieldset">
				<legend>
					<label style="font-size:17px">
						<input type="checkbox" id="checkboxMemberInfo" onclick="toggleFieldSet(this, 'fdMemberInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name32" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<div class="mini-toolbar" >
						<li id="projectMemberButtons" style="display: inline-block">
<%--							添加成员--%>
							<a class="mini-button"   plain="true" onclick="addproject_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name33" /></a>
							<a class="mini-button btn-red"  plain="true" onclick="deleteProject_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name34" /></a>
						</li>
						<li id="changeButtons" style="display: none">
							<a class="mini-button btn-red"  plain="true" onclick="removeproject_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name35" /></a>
							<a class="mini-button btn-red"  plain="true" onclick="revokeRemoveproject_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name36" /></a>
							<%--<a class="mini-button btn-red"  plain="true" onclick="replaceProject_memberInfoRow">替换成员</a>--%>
						</li>
						<p id = "diffAct" style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
<%--							（<spring:message code="page.xcmgProjectEdit.name37" />）--%>
						</p>
					</div>
<%--					项目成员信息--%>
					<div id="grid_project_memberInfo" class="mini-datagrid"  allowResize="false"
						 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
						 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
						<div property="columns">
							<div type="checkcolumn" width="15"></div>
							<div field="id"  headerAlign="left" visible="false">id</div>
<%--							成员部门--%>
							<div field="memberDeptId" displayField="memberDeptName" headerAlign="center" align="center" width="65"><spring:message code="page.xcmgProjectEdit.name132" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-dep rxc" plugins="mini-dep"
									   style="width:90%;height:34px;" allowinput="false" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
							</div>
<%--							项目角色--%>
							<div field="roleId"  displayField="roleName" headerAlign="center" align="center" width="55"><spring:message code="page.xcmgProjectEdit.name133" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-combobox" style="width:90%;"
									   textField="roleName" valueField="roleId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
									   allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..." />
							</div>
<%--							成员姓名--%>
							<div field="userId"  displayField="userName" headerAlign="center" align="center" width="30"><spring:message code="page.xcmgProjectEdit.name134" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
							</div>
<%--							岗位--%>
							<div field="userJob"  headerAlign="center" align="center" width="55"><spring:message code="page.xcmgProjectEdit.name38" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="gwno" visible="false"></div>
<%--							承担工作内容--%>
							<div field="projectTask"  headerAlign="center" align="center" width="170"><spring:message code="page.xcmgProjectEdit.name39" />
								<input property="editor" class="mini-textarea" style="overflow: auto" /></div>
<%--							交付物--%>
							<div field="respDeliveryIds"  displayField="respDeliveryNames" headerAlign="center" align="center" width="180"><spring:message code="page.xcmgProjectEdit.name40" />
								<input property="editor" class="mini-combobox" style="width:90%;"  oncloseclick="onMemberDeliveryCloseClick"
									   multiSelect="true"  showClose="true"
									   textField="deliveryName" valueField="deliveryId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
									   allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..." />
							</div>
                            <div field="workHour" headerAlign="center" align="center" width="70"><spring:message code="page.projectProductEdit.name138" />
                                <input property="editor" class="mini-textbox" required="true"/>
                            </div>
                            <div field="roleRatio" headerAlign="center" align="center" width="65"
                                 renderer="hideRoleRatio"><spring:message code="page.projectProductEdit.name45" />
                                (<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="showRoleRatioRange()"><spring:message code="page.projectProductEdit.name46" /></a>)
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stageEvaluate" headerAlign="center" align="center" width="40"
                                 renderer="stageEvaluateRender"><spring:message code="page.projectProductEdit.name47" />
                            </div>
                            <div field="score" headerAlign="center" align="center" width="40"renderer="onUserScoreRenderer"><spring:message code="page.projectProductEdit.name48" /></div>
                            <div field="userValid" visible="false">userValid</div>
						</div>
					</div>

					<%--产学研外部人员--%>
					<div id="cxyMemOut" style="display: none;margin-top: 30px">
						<div class="mini-toolbar" id="cxyMemberButtons">
							<a class="mini-button"   plain="true" onclick="addCxy_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name45" /></a>
							<a class="mini-button btn-red"  plain="true" onclick="removeCxy_memberInfoRow"><spring:message code="page.xcmgProjectEdit.name46" /></a>
						</div>
						<div id="grid_cxy_memberInfo" class="mini-datagrid"  allowResize="false"
							 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
							 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
							<div property="columns">
								<div type="checkcolumn"></div>
								<div field="id"  headerAlign="left" visible="false">id</div>
								<div field="userName"  headerAlign="center" align="center"><spring:message code="page.xcmgProjectEdit.name47" /><span style="color: #ff0000">*</span>
									<input property="editor" class="mini-textbox"/>
								</div>
								<div field="userDeptName"  headerAlign="center" align="center"><spring:message code="page.xcmgProjectEdit.name48" /><span style="color: #ff0000">*</span>
									<input property="editor" class="mini-textbox"/>
								</div>
								<div field="userRole"  headerAlign="center" align="center"><spring:message code="page.xcmgProjectEdit.name49" /><span style="color: #ff0000">*</span>
									<input property="editor" class="mini-textbox"/>
								</div>
								<div field="userTask"  headerAlign="center" align="center"><spring:message code="page.xcmgProjectEdit.name50" /><span style="color: #ff0000">*</span>
									<input property="editor" class="mini-textbox"/>
								</div>

							</div>
						</div>
					</div>
				</div>
			</fieldset>
			<br>

			<fieldset id="fdPlanInfo" class="hideFieldset" >
				<legend>
					<label style="font-size:17px">
						<input type="checkbox" id="checkboxPlanInfo" onclick="toggleFieldSet(this, 'fdPlanInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name51" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
<%--					项目计划--%>
					<div id="grid_project_plan" class="mini-datagrid"  allowResize="false"
						 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
						 multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false">
						<div property="columns">
							<div field="id"  headerAlign="left" visible="false">id</div>
							<div field="stageId"  headerAlign="left" visible="false">stageId</div>
							<div field="stageNo"  headerAlign="left" width="15"><spring:message code="page.xcmgProjectEdit.name52" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="stageName"  headerAlign="left" width="30"><spring:message code="page.xcmgProjectEdit.name53" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="stagePercent"  headerAlign="left" width="22"><spring:message code="page.xcmgProjectEdit.name54" />(%)
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="deliveryName"  headerAlign="center" align="center"><spring:message code="page.xcmgProjectEdit.name55" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="money"  headerAlign="center" width="18"><spring:message code="page.xcmgProjectEdit.name56" />
								<input property="editor" class="mini-textbox" /></div>
							<div field="planStartTime"  headerAlign="left" width="25" dateFormat="yyyy-MM-dd"><spring:message code="page.xcmgProjectEdit.name57" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-datepicker" style="width:100%"/></div>
							<div field="planEndTime"  headerAlign="left" width="25" dateFormat="yyyy-MM-dd"><spring:message code="page.xcmgProjectEdit.name58" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-datepicker" style="width:100%"/></div>
							<div field="actualStartTime"  headerAlign="left" width="25" dateFormat="yyyy-MM-dd"><spring:message code="page.xcmgProjectEdit.name59" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="actualEndTime"  headerAlign="left" width="25" dateFormat="yyyy-MM-dd"><spring:message code="page.xcmgProjectEdit.name60" />
								<input property="editor" class="mini-textbox" readonly/></div>
							<div field="deductScore"  headerAlign="center" align="center" width="20"><spring:message code="page.xcmgProjectEdit.name61" />
								<input property="editor" class="mini-textbox" readonly/></div>
						</div>
					</div>
				</div>
			</fieldset>
			<br>
			<fieldset id="fdAchievementInfo" class="hideFieldset" >
				<legend>
					<label style="font-size:17px">
						<input type="checkbox"   id="checkboxAchievementInfo" onclick="toggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name62" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<div class="mini-toolbar" id="projectAchievementButtons">
						<a class="mini-button"   plain="true" onclick="addAchievement()"><spring:message code="page.xcmgProjectEdit.name63" /></a>
						<a class="mini-button btn-red"  plain="true" onclick="delAchievement()"><spring:message code="page.xcmgProjectEdit.name64" /></a>
					</div>
					<div id="grid_project_achievement" class="mini-datagrid"  allowResize="false"
						 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
						 multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
						<div property="columns">
							<div type="checkcolumn" width="10"></div>
							<div type="indexcolumn" headerAlign="center" align="center"  width="15"><spring:message code="page.xcmgProjectEdit.name52" /></div>
							<div field="id" align="center"  width="1"  headerAlign="left" visible="false" >id</div>
<%--							部门，必填字段约束--%>
							<div field="deptId"  align="center"  displayField="deptName" headerAlign="center"  width="80"><spring:message code="page.xcmgProjectEdit.name65" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-dep rxc" plugins="mini-dep"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
							</div>
							<div field="hzdw"  headerAlign="center" align="center" width="60"><spring:message code="page.xcmgProjectEdit.name66" />
								<input property="editor" class="mini-textbox" />
							</div>
							<div field="typeId" align="center"   displayField="typeName" headerAlign="center" width="120"><spring:message code="page.xcmgProjectEdit.name67" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-combobox" style="width:90%;"
									   textField="typeName" valueField="typeId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
									   allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..." />
							</div>
							<div field="num" align="center"   headerAlign="center"  width="30"><spring:message code="page.xcmgProjectEdit.name68" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-spinner"  minValue="0" maxValue="1000"/></div>
							<div field="description"  headerAlign="center" align="center"   width="180"><spring:message code="page.xcmgProjectEdit.name69" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-textbox" />
							</div>
							<div field="output_time"  headerAlign="center" align="center"  dateFormat="yyyy-MM-dd" width="80"><spring:message code="page.xcmgProjectEdit.name70" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-datepicker" style="width:100%;"/>
							</div>
                            <div headerAlign="center" align="center" width="100" renderer="outLinkRender"><spring:message code="page.xcmgProjectEdit.name71" />
                            </div>
						</div>
					</div>
				</div>
			</fieldset>
			<br>
			<fieldset id="fdTargetInfo" class="hideFieldset">
				<legend>
					<label style="font-size:17px">
<%--						项目背景、目标及输入--%>
						<input type="checkbox" id="checkboxTargetInfo" onclick="toggleFieldSet(this, 'fdTargetInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name72" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<p style="font-size: 16px">
						<spring:message code="page.xcmgProjectEdit.name73" />：
					</p>
					<textarea id="projectBuildReason" name="projectBuildReason" class="mini-textarea rxc"
                              plugins="mini-textarea" style="width:95%;;height:150px;line-height:25px;" label="<spring:message code="page.xcmgProjectEdit.name74" />" datatype="varchar" length="500"
                              vtype="length:500" minlen="0" allowinput="true"  emptytext="<spring:message code="page.xcmgProjectEdit.name75" />..."></textarea>

					<div style="height: 15px"></div>
					<p style="font-size: 16px">
						<spring:message code="page.xcmgProjectEdit.name76" />：
					</p>
					<textarea id="describeTarget" name="describeTarget" class="mini-textarea rxc"
                              plugins="mini-textarea" style="width:95%;height:150px;line-height:25px;" label="<spring:message code="page.xcmgProjectEdit.name77" />" datatype="varchar"
                              length="500" vtype="length:500" minlen="0" allowinput="true"  emptytext="<spring:message code="page.xcmgProjectEdit.name78" />..."
                              ></textarea>

					<div style="height: 15px"></div>
					<p style="font-size: 16px">
						<spring:message code="page.xcmgProjectEdit.name79" />：
					</p>
					<textarea id="mainTask" name="mainTask" class="mini-textarea rxc" plugins="mini-textarea"
                              style="width:95%;height:150px;line-height:25px;" label="<spring:message code="page.xcmgProjectEdit.name79" />" datatype="varchar" length="500" vtype="length:500" minlen="0"
                              allowinput="true"  emptytext="<spring:message code="page.xcmgProjectEdit.name80" />..." ></textarea>

					<div style="height: 15px"></div>
					<p style="font-size: 16px">
						<spring:message code="page.xcmgProjectEdit.name81" />：
					</p>
					<textarea id="applyPlan" name="applyPlan" class="mini-textarea rxc" plugins="mini-textarea"
                              style="width:95%;height:150px;line-height:25px;" label="<spring:message code="page.xcmgProjectEdit.name81" />" datatype="varchar" length="500" vtype="length:500" minlen="0"
                              allowinput="true"  emptytext="<spring:message code="page.xcmgProjectEdit.name82" />..." ></textarea>
					<p style="font-size: 13px;color: #757575"><spring:message code="page.xcmgProjectEdit.name83" /></p>

					<div style="height: 15px"></div>
					<p style="font-size: 16px;">
						<spring:message code="page.xcmgProjectEdit.name84" />：
					</p>
					<div class="mini-toolbar" id="projectMeasureTargetButtons">
						<a class="mini-button"   plain="true" onclick="addproject_measureTargetRow"><spring:message code="page.xcmgProjectEdit.name63" /></a>
						<a class="mini-button btn-red"  plain="true" onclick="removeproject_measureTargetRow"><spring:message code="page.xcmgProjectEdit.name64" /></a>
					</div>
<%--					定量目标--%>
					<div id="grid_project_measureTarget" class="mini-datagrid"  allowResize="false" style="height:220px"
						 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
						 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true">
						<div property="columns">
							<div type="checkcolumn" width="5%"></div>
							<div field="id"  headerAlign="left" visible="false">id</div>
							<div field="number"  headerAlign="center"  width="8%" align="center"><spring:message code="page.xcmgProjectEdit.name85" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-textbox" /></div>
							<div field="target"  headerAlign="center" ><spring:message code="page.xcmgProjectEdit.name86" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-textbox" /></div>
							<div field="targetNowLevel"  headerAlign="center" ><spring:message code="page.xcmgProjectEdit.name87" />
								<input property="editor" class="mini-textbox" /></div>
							<div field="targetIndustryLevel"  headerAlign="center" ><spring:message code="page.xcmgProjectEdit.name88" />
								<input property="editor" class="mini-textbox" /></div>
							<div field="targetProjectLevel"  headerAlign="center" ><spring:message code="page.xcmgProjectEdit.name89" /><span style="color: #ff0000">*</span>
								<input property="editor" class="mini-textbox"  style="text-align:center" /></div>
						</div>
					</div>

                    <div style="height: 15px"></div>
                    <p style="font-size: 16px;">
						<spring:message code="page.xcmgProjectEdit.name90" />：
                    </p>
                    <div class="mini-toolbar" id="projectInputButtons">
                        <a class="mini-button"   plain="true" onclick="addOrEditInput()"><spring:message code="page.xcmgProjectEdit.name63" /></a>
                        <a class="mini-button btn-red"  plain="true" onclick="deleteInput()"><spring:message code="page.xcmgProjectEdit.name64" /></a>
                    </div>
<%--					项目输入--%>
                    <div id="grid_project_input" class="mini-datagrid"  allowResize="false" style="height:220px"
                         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false" url="${ctxPath}/xcmgProjectManager/core/xcmgProject/queryInputList?projectId=${projectId}"
                         autoload="true" multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div name="action" cellCls="actionIcons"  headerAlign="center" align="center" renderer="inputOperateRender" width="12"><spring:message code="page.xcmgProjectEdit.name111" />
                            </div>
                            <div field="inputType"  headerAlign="center" align="center" width="25"><spring:message code="page.xcmgProjectEdit.name91" />
                            </div>
                            <div field="inputNumber"  headerAlign="center" align="center" width="40"><spring:message code="page.xcmgProjectEdit.name92" />
                            </div>
                            <div field="inputName"  headerAlign="center" align="center" width="60" renderer="inputDetailRender"><spring:message code="page.xcmgProjectEdit.name93" />
                            </div>
                            <div field="referContent"  headerAlign="center" align="center" width="80"><spring:message code="page.xcmgProjectEdit.name94" />
                            </div>
                            <div field="relation"  headerAlign="center" align="center" width="80"><spring:message code="page.xcmgProjectEdit.name95" />
                            </div>
                        </div>
                    </div>
				</div>
			</fieldset>
			<br>
			<fieldset id="fileInfo" class="hideFieldset">
				<legend>
					<label style="font-size:17px">
						<input type="checkbox"  id="checkboxFileInfo" onclick="toggleFieldSet(this, 'fileInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name96" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<div id="newInput" style="float: left;margin-right: 20px;display: none">
						<span style="color: red"><spring:message code="page.xcmgProjectEdit.name97" />：</span>
						<input id="selectStage"  name="selectStage" class="mini-combobox" style="width:200px;" required="true"
							   textField="stageName" valueField="stageId" emptyText="<spring:message code="page.xcmgProjectEdit.name7" />..."
							   allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.xcmgProjectEdit.name7" />..." onvaluechanged="checkStageSelect"/>
					</div>
					<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="projectUploadFile()"><spring:message code="page.xcmgProjectEdit.name98" /></a>
					<p style="display: inline-block;color: #f30b0b;font-size:15px;vertical-align: middle"><spring:message code="page.xcmgProjectEdit.name99" /></p>
					<div style="display: inline-block;float: right;margin-bottom: 5px">
						<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle"><spring:message code="page.xcmgProjectEdit.name100" />:</p>
						<input id="fileNameFilter" class="mini-textbox" style="width:150px" />
						<a class="mini-button"  onclick="queryFileByName()"><spring:message code="page.xcmgProjectEdit.name101" /></a>
						<a class="mini-button"  onclick="refreshFile()"><spring:message code="page.xcmgProjectEdit.name102" /></a>
						<a class="mini-button"  onclick="expandFileAll()"><spring:message code="page.xcmgProjectEdit.name103" /></a>
						<a class="mini-button"  onclick="collapseFileAll()"><spring:message code="page.xcmgProjectEdit.name104" /></a>
						<%--<a class="mini-button"  id="syncPDMBtn" onclick="syncPDM()">同步PDM</a>--%>
						<a class="mini-button"  id="startFileApprovalBtn" onclick="startFileApproval()"><spring:message code="page.xcmgProjectEdit.name105" /></a>
					</div>
					<div id="treegridFileInfo" class="mini-treegrid" style="width:100%;height:800px;" showTreeIcon="true" allowCellWrap="true"
						 treeColumn="name" idField="id" parentField="pid" resultAsTree="false" autoload="false"
						 url="${ctxPath}/xcmgProjectManager/core/fileUpload/getProjectFiles.do?projectId=${projectId}"
						 expandOnLoad="false">
						<div property="columns">
							<div name="name" field="fileName" width="160" ><spring:message code="page.xcmgProjectEdit.name100" /></div>
							<div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.xcmgProjectEdit.name106" /></div>
							<div field="deliveryName" headerAlign='center' width="120" align="center"><spring:message code="page.xcmgProjectEdit.name107" /></div>
							<div field="creator" width="60" headerAlign='center' align="center"><spring:message code="page.xcmgProjectEdit.name108" /></div>
							<div field="CREATE_TIME_" width="90" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.xcmgProjectEdit.name109" /></div>
							<div field="approvalStatus" width="100" headerAlign='center' align="center"><spring:message code="page.xcmgProjectEdit.name110" /></div>
							<div field="action" width="120" headerAlign='center' align="center"><spring:message code="page.xcmgProjectEdit.name111" /></div>
						</div>
					</div>
				</div>
			</fieldset>
			<br>
			<fieldset id="changeListInfo" class="hideFieldset" >
				<legend>
					<label style="font-size:17px">
						<input type="checkbox"   id="checkboxChangeList" onclick="toggleFieldSet(this, 'changeListInfo')" hideFocus/>
						<spring:message code="page.xcmgProjectEdit.name112" />
					</label>
				</legend>
				<div class="fieldset-body" style="margin: 10px 10px 10px 10px">
					<div id="grid_project_changeList" class="mini-datagrid"  allowResize="false" autoload="true"
						 url="${ctxPath}/xcmgProjectManager/core/xcmgProjectChange/queryList.do?projectId=${projectId}&action=queryOneProject"
						 idField="applyId" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
						 showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
						<div property="columns">
							<div type="indexcolumn" headerAlign="center" align="center"  width="15"><spring:message code="page.xcmgProjectEdit.name52" /></div>
							<div field="id" sortField="id" width="80" headerAlign="center" align="center" allowSort="true" renderer="jumpToChangeDetail"><spring:message code="page.xcmgProjectEdit.name113" /></div>
							<div field="applyUserName" sortField="applyUserName" width="40" headerAlign="center" align="center"
								 allowSort="true"><spring:message code="page.xcmgProjectEdit.name114" />
							</div>
							<div field="reason" width="150" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectEdit.name115" /></div>
							<div field="instStatus" width="50" headerAlign="center" align="center" renderer="onStatusRenderer"
								 allowSort="false"><spring:message code="page.xcmgProjectEdit.name116" />
							</div>
							<div field="currentProcessTask" sortField="currentProcessTask" width="50" align="center"
								 headerAlign="center" allowSort="false"><spring:message code="page.xcmgProjectEdit.name117" />
							</div>
							<div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
								 headerAlign="center" allowSort="false"><spring:message code="page.xcmgProjectEdit.name118" />
							</div>
							<div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd" align="center" width="70"
								 headerAlign="center" allowSort="true"><spring:message code="page.xcmgProjectEdit.name119" />
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</form>
	</div>
</div>

<div id="replaceMemberWindow" title="<spring:message code="page.xcmgProjectEdit.name120" />" class="mini-window" style="width:500px;height:200px;"
	 showModal="true" showFooter="false" allowResize="true">
	<div class="topToolBar" style="text-align:right;">
		<a class="mini-button" onclick="replaceMember()"><spring:message code="page.xcmgProjectEdit.name121" /></a>
	</div>
	<div class="mini-fit" style="font-size: 14px">
		<form id="postForm" method="post">
			<input id="replaceMemberId" name="replaceMemberId" class="mini-hidden"/>
			<input id="originalUserId" name="originalUserId" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
				<tr>
					<td align="center" style="width: 10%"><spring:message code="page.xcmgProjectEdit.name122" />：</td>
					<td align="center" colspan="3" style="width: 85%">
						<input id="replaceUserId" name="replaceUserId" textname="replaceUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.xcmgProjectEdit.name123" />" length="50"
							   mainfield="no"  single="true" />
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%--阶段评价--%>
<div id="evaluateWindow" title="<spring:message code="page.projectProductEdit.name130" />" class="mini-window" style="width:1420px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
            （添加、修改、删除后点击“保存”按钮进行保存）
        </p>
        <a class="mini-button" id="addEvaluateBtn" onclick="addEvaluate()"><spring:message code="page.projectProductEdit.name68" /></a>
        <a class="mini-button" id="delEvaluateBtn" onclick="delEvaluate()"><spring:message code="page.projectProductEdit.name69" /></a>

        <a class="mini-button" id="saveEvaluateBtn" onclick="saveEvaluate()"><spring:message code="page.projectProductEdit.name131" /></a>
        <a class="mini-button btn-red" onclick="closeEvaluate()"><spring:message code="page.projectProductEdit.name2" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <input id="evaluateUserId" class="mini-hidden"/>
        <div id="evaluateList" class="mini-datagrid" allowResize="false" multiSelect="false" style="height: 100%"
             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
             showColumnsMenu="false" showPager="false" allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="40"></div>
                <div field="projectId" visible="false"></div>
                <div field="userId" visible="false"></div>
                <div field="stageId" displayField="stageName" width="120" headerAlign="center" align="center" >阶段
                </div>
                <div field="gznr" width="200" headerAlign="center" align="center" allowSort="false">工作内容<span style="color:red">*</span>
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="gzzl" width="200" headerAlign="center" align="center" allowSort="false">工作质量<span style="color:red">*</span>
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="cxnl" width="200" headerAlign="center" align="center" allowSort="false">创新能力<span style="color:red">*</span>
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="zdx" width="200" headerAlign="center" align="center" allowSort="false">主动/积极性<span style="color:red">*</span>
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="gsnr" width="150" headerAlign="center" align="center" allowSort="false">改善内容<span style="color:red">*</span>
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="kfx" width="100" headerAlign="center" align="center" allowSort="false">扣分项
                    <input property="editor" class="mini-textbox"/>
                </div>
                <div field="creator" width="70" headerAlign="center" align="center" >评价人
                </div>
                <div field="CREATE_TIME_" width="100" headerAlign="center" align="center" >评价时间
                </div>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    var nodeVarsStr='${nodeVars}';
    var nodeId="${nodeId}";
    var status="${status}";
    var projectId="${projectId}";
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";
    var currentUserDeps=${currentUserDeps};
    var currentUserRoles=${currentUserRoles};
    var processEchart = echarts.init(document.getElementById('processDiv'));
	var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
	var currentUserProjectRoleName="${currentUserProjectRoleName}";
	var currentUserMainDeptId = "${currentUserMainDeptId}";
    var projectCategory= "${projectCategory}";

	//记录变更场景下被修改的项目负责人
	var changedRespManId="";

    mini.parse();
    var grid_project_measureTarget = mini.get("#grid_project_measureTarget");
    var grid_project_input = mini.get("#grid_project_input");
    var grid_project_memberInfo = mini.get("#grid_project_memberInfo");
    var grid_project_plan=mini.get("#grid_project_plan");
    var formProject = new mini.Form("#formProject");
    var treegridFileInfo=mini.get("treegridFileInfo");
	var grid_project_achievement = mini.get("#grid_project_achievement");
    var grid_cxy_memberInfo = mini.get("#grid_cxy_memberInfo");
	var replaceMemberWindow=mini.get("replaceMemberWindow");
	var grid_project_changeList=mini.get("grid_project_changeList");
    var evaluateWindow = mini.get("evaluateWindow");
    var evaluateList = mini.get("evaluateList");
    var detailHideRoleRatio="${detailHideRoleRatio}";

    grid_project_input.on("update",function(e){
        handGridButtons(e.sender.el);
    });


    grid_project_memberInfo.on("drawcell",function(e){
        var record=e.record;
        var userValid=record.userValid;
        if(userValid=='02') {
            e.rowCls="rmMem";
        }
    });

    //修改文件图标，按照真正是否是文件夹
    treegridFileInfo.on("drawcell",function(e){
        var record=e.record;
        var isFolder=record.isFolder;
        var currentStageId=mini.get("currentStageId").getValue();
        if(isFolder=='1') {
            if(e.field=='fileName') {
                e.iconCls="mini-tree-folder";
            } else {
                e.cellHtml='';
			}
    	} else {
            if(e.field=='action') {
                if(record.fileSource && 'pdm'==record.fileSource) {
                    //PDM的文件不能预览、下载、删除
					e.cellHtml='<span  title="' + xcmgProjectEdit_name +record.filePath+'" style="color: #409EFF" >' + xcmgProjectEdit_name +record.filePath+'</span>';
				} else if(record.fileSource && 'sdm'==record.fileSource) {
                    //sDM的流程跳转
                    e.cellHtml='<span style="color: #409EFF;cursor: pointer;" onclick="jumpToFzsjDetail(\''+record.id+'\')">' + xcmgProjectEdit_name14 +record.filePath+'</span>';
                } else {
                    e.cellHtml=returnPreviewSpan(record.fileName,record.relativeFilePath,'project',coverContent);
                    e.cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + xcmgProjectEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                        'onclick="downProjectLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + xcmgProjectEdit_name1 + '</span>';

                    //增加删除按钮
                    if(record.pid==currentStageId && action!='detail'&& action!='editDelivery') {
                        e.cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + xcmgProjectEdit_name2 + '  style="color:#409EFF;cursor: pointer;" ' +
                            'onclick="deleteProjectFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + xcmgProjectEdit_name2 + ' </span>';
                    }
                    if(record.pid==currentStageId && action=='editDelivery'){
                        if(currentUserProjectRoleName=='项目负责人' || record.CREATE_BY_==currentUserId) {
                            e.cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + xcmgProjectEdit_name2 + '  style="color:#409EFF;cursor: pointer;" ' +
                                'onclick="deleteProjectFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + xcmgProjectEdit_name2 + ' </span>';
                        }
                    }
                    if(isProjectManager&&action == 'change') {
                        e.cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + xcmgProjectEdit_name2 + '  style="color:#409EFF;cursor: pointer;" ' +
                            'onclick="deleteProjectFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + xcmgProjectEdit_name2 + ' </span>';
                    }
				}

			} else if(e.field=='approvalStatus') {
				var status=record.approvalStatus;
                var color='green';
				if(record.fileSource && 'pdm'==record.fileSource) {
				    if('已发布'!=status) {
                        color='red';
					}
				} else if(record.fileSource && 'sdm'==record.fileSource) {
                    if('审批中'==status) {
                        color='orange';
                    }
                } else {
				    if(status.indexOf("审批中")!=-1) {
				        color='orange';
					} else if('无需审批'!=status&&'审批完成'!=status) {
                        color='red';
					}
				}
				var titleStr="";
				if(record.fileapplyId) {
                    titleStr="交付物审批流程单号："+record.fileapplyId;
				}
                e.cellHtml='<span style="color: '+color+';cursor: pointer" title="'+titleStr+'">'+status+'</span>';
			}

		}
    });

    grid_project_memberInfo.on("cellbeginedit", function (e) {
        var record = e.record;
		if(!e.editor) {
			return;
		}
		//已冻结成员不允许做任何更改，如要更改需要先解冻
        if(record.userValid=='02') {
            e.editor.setEnabled(false);
            return;
		}

        if(beforeBuildReview=='1'||action=='edit' ||action=='change') {
            e.editor.setEnabled(true);
            //成员部门
            if (e.field == "memberDeptId") {
                if(record.roleName=='项目负责人') {
                    mini.alert(xcmgProjectEdit_name8);
                    e.editor.setEnabled(false);
                }
            }
            //项目角色
            if (e.field == "roleId") {
                if(record.roleName=='项目负责人') {
                    mini.alert(xcmgProjectEdit_name3);
                    e.editor.setEnabled(false);
                }else {
                    e.editor.setData(memRoleList);
                }
            }
            //成员姓名
            if (e.field == "userId") {
                if (!record.memberDeptId) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name15);
                    return;
                }
                if (!record.roleName) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name4);
                    return;
                }

                if(record.roleName=='项目负责人') {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name3);
                    return;
                }
            }

            //角色系数
            if(e.field == "roleRatio") {
                if (!record.roleName) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name4);
                } else if (record.roleName=='项目指导人') {
                    e.editor.setEnabled(false);
                }
            }
			//负责交付物
			if(e.field == 'respDeliveryIds') {
                var projectSource=mini.get("projectSource").getValue();
                if(!$.trim(projectSource)) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name5);
                }
			    var projectCategory=mini.get("projectCategory").getValue();
			    if(!$.trim(projectCategory)) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name6);
				}
			    //zwt
                var beginLevelId=mini.get("beginLevel").getValue();
                if(!$.trim(beginLevelId)) {
                    e.editor.setEnabled(false);
                    mini.alert(xcmgProjectEdit_name7);
                }//查询交付物列表
                $.ajax({
                    url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectDeliverys.do?projectSourceId='+projectSource+"&projectCategoryId="+projectCategory+"&projectLevelId="+mini.get("beginLevel").getValue(),
                    type: 'get',
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            e.editor.setData(data);
                        }
                    }
                });
			}
		} else {
            e.editor.setEnabled(false);
			if(memberPoint=='yes') {
				if (e.field == "userId") {
					if(record.roleName=='项目负责人') {
						alert(xcmgProjectEdit_name9);
					} else {
					    if(currentUserMainDeptId==record.memberDeptId) {
                            e.editor.setEnabled(true);
                        }
					}
				}else {
                    e.cancel = true;
                }
			}
            if (stageRoleRatioEvaluate == 'yes') {
                if (e.field == "roleId" && record.roleName != '项目负责人' && record.roleName != '项目指导人' && editProRespPjScore!='1') {
                    e.editor.setEnabled(true);
                    e.editor.setData(memRoleList);
                } else if(e.field == "roleRatio" && record.roleName != '项目指导人') {
                    if(editProRespPjScore=='1') {
                        if(record.roleName == '项目负责人') {
                            e.editor.setEnabled(true);
                        }
                    } else {
                        e.editor.setEnabled(true);
                    }
                }
            }
		}

    });

    grid_cxy_memberInfo.on("cellbeginedit", function (e) {
        var record = e.record;
        if(beforeBuildReview=='1'||action=='edit' ||action=='change') {
            e.editor.setEnabled(true);
        } else {
            e.editor.setEnabled(false);
        }
    });

	grid_project_achievement.on("cellbeginedit", function (e) {
	    if(!e.editor) {
	        return;
        }
		var record = e.record;
		if(beforeBuildReview=='1'||action=='edit' ||action=='change') {
			e.editor.setEnabled(true);
			if (e.field == "typeId") {
				e.editor.setData(achieveTypeList);
			}
		}else {
			e.editor.setEnabled(false);
		}

	});

    grid_project_memberInfo.on("cellcommitedit", function (e) {
        var grid = e.sender,
            record = e.record;
		//检查成员部门信息是否发生修改
		if (e.field == "memberDeptId") {
			if (record.userName) {
				//成员部门信息不一致
				var userInfo=getUserInfoById(record.userId);
				if(userInfo && userInfo.mainDepId!=e.value) {
					mini.alert(xcmgProjectEdit_name16);
					e.value= "";
					e.text = "";
					return;
				}
			}
		}
		//成员姓名的变化
		if (e.field == "userId") {//单元格为userId字段
            var oldUserId = record.userId;
            var oldUserName = record.userName;
            var userId=e.value;
            var userName =e.text;
            //原来没有成员姓名的场景，直接更新成当前选择的
            if (!oldUserId) {
                if(!userId) {
                    grid.updateRow(record, {userJob: "",gwno:""});
                    return;
                }
                //判断是否已经有这个成员
                var gridData=grid.data;
                for(var i=0;i<gridData.length;i++) {
                    if(gridData[i].userId==userId) {
                        mini.alert(xcmgProjectEdit_name10);
                        e.value='';
                        e.text='';
                        return;
                    }
                }
                var userInfo=getUserInfoById(userId);
                if(userInfo && userInfo.mainDepId==record.memberDeptId) {
                    grid.updateRow(record, {userJob: userInfo.GW,gwno:userInfo.gwno});
                } else {
                    mini.alert(xcmgProjectEdit_name11);
                    e.value='';
                    e.text='';
                }
            } else {
            //原来有成员姓名，需要将原有的冻结，然后新增一条新的
                //检查新选的成员是否合法
                var userJob = "";
                var gwno = "";
                if (userId) {
                    //是否已存在
                    var gridData=grid.data;
                    for(var i=0;i<gridData.length;i++) {
                        if(gridData[i].userId==userId) {
                            mini.alert(xcmgProjectEdit_name10);
                            e.value=oldUserId;
                            e.text=oldUserName;
                            return;
                        }
                    }
                    //是否是要求的部门
                    var userInfo=getUserInfoById(userId);
                    if(userInfo && userInfo.mainDepId==record.memberDeptId) {
                        userJob = userInfo.GW;
                        gwno = userInfo.gwno;
                    } else {
                        mini.alert(xcmgProjectEdit_name11);
                        e.value=oldUserId;
                        e.text=oldUserName;
                        return;
                    }
                }
                mini.confirm("此操作会将原有成员冻结，并新增一条成员信息（原有冻结的数据如需删除，可稍后勾选后点击“删除成员”）", "确定继续？",
                    function (action) {
						if (action == "ok") {
							var currentIndex = grid_project_memberInfo.indexOf(record);
							//冻结原有的成员
							grid_project_memberInfo.addRowCls(record, "rmMem");
							grid.updateRow(record, {userId: oldUserId, userName: oldUserName, userValid: '02'});
							grid_project_memberInfo.moveRow(record, grid.data.length);
							//新增一条新的
							var addRow = {
								userId: userId,
								userName: userName,
								memberDeptId: "",
								memberDeptName: "",
								userJob: userJob,
								gwno: gwno,
								roleId: record.roleId,
								roleName: record.roleName,
								projectTask: record.projectTask,
								respDeliveryIds: record.respDeliveryIds,
								respDeliveryNames: record.respDeliveryNames,
								workHour: record.workHour,
								roleRatio: record.roleRatio
							};
							grid_project_memberInfo.addRow(addRow, currentIndex);
						} else {
							grid.updateRow(record, {userId: oldUserId,userName:oldUserName});
						}
                    }
                );
            }
        }

        if(e.field == "roleRatio") {
            //检查角色系数是否在范围内
            var checkResult=checkRoleRatioRange(record.roleId,record.roleName,e.value);
            if(!checkResult.result) {
                mini.alert(checkResult.message);
                e.value='';
            }
		}
        if (e.field == "roleId") {
            var roleName=findRoleNameById(e.value);
            if(roleName=='项目指导人') {
                grid.updateRow(record, {roleRatio:''});
            }
            //检查角色系数是否在范围内
            var checkResult = checkRoleRatioRange(e.value, e.text, record.roleRatio);
            if (!checkResult.result) {
                mini.alert(checkResult.message);
                grid.updateRow(record, {roleRatio: ''});
            }
        }
    });

    grid_project_measureTarget.on("cellcommitedit", function (e) {
        if (e.field == "number") {
            var r = /^\+?[1-9][0-9]*$/;　　//正整数
            if(!r.test(e.value)){
                mini.alert(xcmgProjectEdit_name12);
                e.value='';
            }
        }
    });

    //变更界面已完成的阶段不允许更改数据
    grid_project_plan.on("cellbeginedit", function (e) {
        var record = e.record;
        if(action=='change') {
            e.editor.setEnabled(true);
            var currentStageNo=mini.get("#currentStageNo").getValue();
			if(record.stageNo<currentStageNo) {
                e.editor.setEnabled(false);
			}
        }
    });


    function showLoading() {
        $("#loading").css('display','');
        $("#content").css('display','none');
    }

    function hideLoading() {
        $("#loading").css('display','none');
        $("#content").css('display','');
    }


    processEchart.on('click',function (params) {
        //展开
        $("#checkboxFileInfo").attr("checked",true);
        $("#fileInfo").removeClass("hideFieldset");
		//收缩
        $("#checkboxBaseInfo").attr("checked",false);
        $("#fdBaseInfo").addClass("hideFieldset");

        $("#checkboxMemberInfo").attr("checked",false);
        $("#fdMemberInfo").addClass("hideFieldset");

        $("#checkboxPlanInfo").attr("checked",false);
        $("#fdPlanInfo").addClass("hideFieldset");

        $("#checkboxAchievementInfo").attr("checked",false);
        $("#fdAchievementInfo").addClass("hideFieldset");

        $("#checkboxTargetInfo").attr("checked",false);
        $("#fdTargetInfo").addClass("hideFieldset");

        //定位
        $("#anchor").attr("href","#fileInfo");
        $("#anchor")[0].click();
        collapseFileAll();
        var tempArr=params.value.split(",");
        expandTree(tempArr[1]);
    });

    function jumpToChangeDetail(e) {
        var record = e.record;
        var projectId = record.projectId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailChangeApply(\'' + record.id + '\',\'' + record.instStatus + '\')">'+record.id+'</a>';
        return s;
    }

    function detailChangeApply(id,status) {
        var action = "detail";
        var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProjectChange/edit.do?action="+action+"&id=" + id+"&status="+status;
        var winObj = window.open(url);
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    function onMemberDeliveryCloseClick(e) {
        var obj = e.sender;
        obj.setText("");
        obj.setValue("");
    }

    function stageEvaluateRender(e) {
        var record = e.record;
        if (!record.roleName || record.roleName == '项目指导人' || !record.userId ||!record.id ||record.userValid=='02'||!record.memberDeptId) {
            return "";
        }
        var stageEvaluate ='<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="stageEvaluateClick(\'' + record.userId +'\',\''+record.roleName+ '\')">阶段评价</span>';

        return stageEvaluate;
    }

	function inputOperateRender(e) {
        var record = e.record;
        var s = '';
        if(action=='detail' || (action=='task' && beforeBuildReview != '1')) {
            s='<span  title=' + xcmgProjectEdit_name13 + ' style="color: silver">' + xcmgProjectEdit_name13 + '</span>';
        } else {
            s='<span  title=' + xcmgProjectEdit_name13 + ' onclick="addOrEditInput(' +JSON.stringify(record).replace(/"/g, '&quot;')+ ')">' + xcmgProjectEdit_name13 + '</span>';
        }
        return s;
    }

    function inputDetailRender(e) {
        var record = e.record;
        if(!record.inputName) {
            return "";
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailInput(\'' + record.inputUrl+ '\')">'+record.inputName+'</a>';
        return s;
    }

    //todo:成果详情入口链接(老李mark暂留)
    function outLinkRender(e) {
        var record=e.record;
        var outPlanId='';
        if(record.id) {
            outPlanId=record.id;
        }
        var projectId='';
        if(record.projectId) {
            projectId=record.projectId;
        }
        var typeName='';
        if(record.typeName) {
            typeName=record.typeName;
        }
        var belongSubSysKey = '';
        if (record.belongSubSysKey) {
            belongSubSysKey = record.belongSubSysKey;
        }
        var currentRespmanId=mini.get("respmanId").getValue();
        var canOperateFile="no";
        // 项目负责人和项目管理人员能编辑
        if(isProjectManager || currentUserId == currentRespmanId) {
            canOperateFile='yes';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="showOutLinkPage(\'' + outPlanId + '\',\'' + projectId + '\',\'' + canOperateFile + '\',\'' + typeName +'\',\''+belongSubSysKey+ '\')">成果详情</a>';
        return s;
    }
    function onUserScoreRenderer(e) {
        var record = e.record;
        var projectId = record.projectId;
        var userId=record.userId;
        var score =record.score;
        if(!score) {
            return;
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="showStageScores(\'' + projectId +'\',\''+userId+ '\')">'+record.score+'</a>';
        return s;
    }
    function showStageScores(projectId,userId) {
        mini.open({
            title: xcmgScoreList_name1,
            url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgScore/projectStageScoreShow.do?projectId="+projectId+"&userId="+userId,
            width: 750,
            height: 560,
            allowResize: true
        });
    }

    evaluateList.on("cellbeginedit", function (e) {
        if (!e.editor) {
            return;
        }
        var record = e.record;
        var currentStageId = mini.get("currentStageId").getValue();
        if (action == 'task' && stageRoleRatioEvaluate=='yes' && currentStageId && record.stageId==currentStageId) {
            e.editor.setEnabled(true);
        } else {
            e.editor.setEnabled(false);
        }

    });
</script>
</body>
</html>
