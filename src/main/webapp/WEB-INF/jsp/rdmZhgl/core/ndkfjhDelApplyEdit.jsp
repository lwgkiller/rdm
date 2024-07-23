<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhDelApplyEdit.js?version=${static_res_version}"
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
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    年度开发计划删除申请表单
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
                    <td style="text-align: center">申请理由：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;"
                                  label="申请理由" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true" required
                                  emptytext="请输入申请理由..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
		<div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  500px;" sortField="UPDATE_TIME_"
			 sortOrder="desc"
			 url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/list.do?id=${applyObj.mainId}" idField="id" showPager="false" allowCellWrap="false"
			 multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true" autoload="true"
			 pagerButtons="#pagerButtons">
			<div property="columns">
				<div field="planCode" name="planCode" width="80px" headerAlign="center" align="center" allowSort="false">编号</div>
				<div field="productName" width="100px" headerAlign="center" align="center" allowSort="false">项目/产品名称</div>
				<div field="target" name="target" width="200px" headerAlign="center" align="center" allowSort="false">年度目标</div>
				<div field="planSource" width="100px" headerAlign="center"  renderer="onPalnSorce" align="center">计划来源</div>
				<div field="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false">项目开始结束时间</div>
				<div field="process" name="process" width="240px" headerAlign="center" align="center">当前进度
					<div property="columns" align="center">
						<div field="currentStage" width="100px" headerAlign="center" align="center" allowSort="false">当前阶段</div>
						<div field="stageFinishDate" width="160px" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
						<div field="finishRate" width="80px" headerAlign="center" align="center" allowSort="false">完成率</div>
						<div field="isDelay" width="80px" headerAlign="center" align="center" renderer="onDelay" allowSort="false">是否延期</div>
						<div field="delayDays" width="80px" headerAlign="center" align="center" allowSort="false">延期天数</div>
						<div field="remark" width="150px" headerAlign="center" align="center" allowSort="false">备注</div>
					</div>
				</div>
				<div field="chargerManName" width="100px" headerAlign="center" align="center" allowSort="false">负责人</div>
				<div field="chargerDeptName" width="100px" headerAlign="center" align="center" allowSort="false">部门</div>
				<div field="managerName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">管控人</div>
				<div field="responsorName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">责任所长</div>
			</div>
		</div>
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
	var yesOrNo = getDics("YESORNO");
	var sourceList = getDics("ndkfjh_source");
	function onPalnSorce(e) {
		var record = e.record;
		var planSource = record.planSource;
		var resultText = '';
		for (var i = 0; i < sourceList.length; i++) {
			if (sourceList[i].key_ == planSource) {
				resultText = sourceList[i].text;
				break
			}
		}
		return resultText;
	}
	function onDelay(e) {
		var record = e.record;
		var isDelay = record.isDelay;
		var resultText = '';
		for (var i = 0; i < yesOrNo.length; i++) {
			if (yesOrNo[i].key_ == isDelay) {
				resultText = yesOrNo[i].text;
				break
			}
		}
		return resultText;
	}
</script>
</body>
</html>
