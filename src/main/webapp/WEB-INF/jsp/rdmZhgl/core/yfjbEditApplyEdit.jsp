
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/yfjbEditApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()">流程信息</a>
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
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption >
					项目信息修改申请单
				</caption>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        销售型号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="saleModel"  readonly class="mini-textbox" style="width:100%;height:34px">
                    </td>
					<td align="center" style="white-space: nowrap;">
						设计型号：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="designModel" readonly class="mini-textbox" style="width:100%;height:34px">
					</td>
                </tr>
				<tr>
					<td style="text-align: center">修改说明：</td>
					<td colspan="3">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;height:100px;line-height:25px;"
								  label="修改说明" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true" required
								  emptytext="请输入修改..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
			</table>
		</form>
		<div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  200px;" sortField="UPDATE_TIME_"
			 sortOrder="desc"
			 url="${ctxPath}/rdmZhgl/core/yfjb/listInfo.do?mainId=${applyObj.mainId}" idField="id" showPager="false" allowCellWrap="true"
			 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
			 pagerButtons="#pagerButtons">
			<div property="columns">
				<div field="saleModel" name="saleModel" width="200px" headerAlign="center" align="center" allowSort="false">
					销售型号
				</div>
				<div field="designModel" name="designModel" width="200px" headerAlign="center" align="left"
					 allowSort="false">设计型号
				</div>
				<div field="infoStatus" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="onStatus">项目状态
				</div>
				<div width="150px" headerAlign="center" align="center" renderer="renderMember" allowSort="false">项目成员</div>
				<div field="orgItemCode" width="150px" headerAlign="center" align="center">原物料编码</div>
				<div field="orgItemName" width="200px" headerAlign="center" align="center" allowSort="false">原物料名称</div>
				<div field="orgItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="onHidePrice">原物料价格
				</div>
				<div field="orgSupplier" width="200px" headerAlign="center" align="center" allowSort="false">原供应商</div>
				<div field="basePrice" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="onHidePrice">基准价格
				</div>
				<div field="costType" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onJbfs">
					降本方式
				</div>
				<div field="costMeasure" width="200px" headerAlign="center" align="center" allowSort="false">降本措施</div>
				<div field="newItemCode" width="150px" headerAlign="center" align="center" allowSort="false">替代物料编码</div>
				<div field="newItemName" width="200px" headerAlign="center" align="center" allowSort="false">替代物料名称</div>
				<div field="newItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="onHidePrice">替代物料价格
				</div>
				<div field="newSupplier" width="200px" headerAlign="center" align="center" allowSort="false">新供应商</div>
				<div field="differentPrice" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="toFixNum">差额
				</div>
				<div field="perSum" width="150px" headerAlign="center" align="center" allowSort="false">单台用量</div>
				<div field="replaceRate" width="150px" headerAlign="center" align="center" allowSort="false">代替比例(%)</div>
				<div field="perCost" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="toFixNum">单台降本
				</div>
				<div field="achieveCost" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="toFixNum">已实现单台降本
				</div>
				<div field="risk" width="200px" headerAlign="center" align="center" allowSort="false">风险评估</div>
				<div field="isReplace" width="150px" headerAlign="center" align="center" allowSort="false"
					 renderer="onReplace">生产是否切换
				</div>
				<div field="jhsz_date" width="150px" headerAlign="center" align="center" allowSort="false">计划试制时间</div>
				<div field="sjsz_date" width="150px" headerAlign="center" align="center" allowSort="false">实际试制时间</div>
				<div field="jhxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">计划下发切换通知单时间
				</div>
				<div field="sjxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">实际下发切换通知单时间
				</div>
				<div field="jhqh_date" width="150px" headerAlign="center" align="center" allowSort="false">计划切换时间</div>
				<div field="sjqh_date" width="150px" headerAlign="center" align="center" allowSort="false">实际切换时间</div>
				<div field="deptName" width="150px" headerAlign="center" align="center" allowSort="false">所属部门</div>
				<div field="major" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onMajor">
					所属专业
				</div>
				<div field="responseMan" width="150px" headerAlign="center" align="center" allowSort="false">责任人</div>
				<div width="150px" headerAlign="center" align="center" renderer="process" allowSort="false">降本项目进度跟踪</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr='${nodeVars}';
	var action="${action}";
	let jsUseCtxPath="${ctxPath}";
	let ApplyObj=${applyObj};
	let status="${status}";
	let applyForm = new mini.Form("#applyForm");
	var listGrid = mini.get("listGrid");
	listGrid.frozenColumns(0, 3);
	listGrid.load();
	var jbfsList = getDics("YFJB-JBFS");
	var replaceList = getDics("YESORNO");
	var majorList = getDics("YFJB-SSZY");
	var statusList = getDics("YFJB-XMZT");
</script>
</body>
</html>
