
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/xpszFinishEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
		<form id="productApplyForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input  name="mainId" class="mini-hidden"/>
			<input id="item" name="item" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption >
					新品试制完成日期审批单
				</caption>
				<tr>
					<td style="text-align: center;width: 15%">完成项：</td>
					<td colspan="1">
						<input name="itemName" readonly class="mini-textbox"  style="width: 100%" />
					</td>
					<td style="text-align: center">完成时间<span style="color: #ff0000">*</span>：</td>
					<td colspan="1">
						<input id="finishDate" name="finishDate" allowinput="false" required  class="mini-datepicker"  style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr id="produceNoticeTr">
					<td style="text-align: center">量产通知编号<span style="color: #ff0000">*</span>：</td>
					<td colspan="1">
						<input id="produceNotice" name="produceNotice"  class="mini-textbox"  style="width: 100%" />
					</td>
				</tr>
				<tr>
					<td style="text-align: center">备注：</td>
					<td colspan="3">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;height:100px;line-height:25px;"
								  label="备注" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
								  emptytext="请输入备注..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;height: 300px">证明材料：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button" onclick="addFile()">添加文件</a>
							<span style="color: red">注：添加证明材料前，请先进行草稿的保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true"
							 idField="id" url="${ctxPath}/common/core/file/files.do?tableName=xpsz_finishapply&&mainId=${applyObj.id}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20">序号</div>
								<div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
								<div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
								<div field="fileDesc" align="center" headerAlign="center" width="100">文件描述</div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererSq">操作</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</form>
		<div class="mini-fit" style="height:  100%;margin-top: 5px">
			<div id="productGrid" class="mini-datagrid" allowResize="true" style="height: 100%"
				 url="${ctxPath}/rdmZhgl/core/product/list.do" idField="id" showPager="false" allowCellWrap="true"
				 allowCellEdit="true" allowCellSelect="true" ondrawcell="onDrawCell"
				 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
				 oncellendedit="cellendedit" allowHeaderWrap="true" onlyCheckSelection="true"
				 pagerButtons="#pagerButtons">
				<div property="columns">
					<div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px">序号</div>
					<div field="productType" name="productType" width="200px" headerAlign="center" align="center">产品类型</div>
					<div field="productModel" name="productModel" width="100px" headerAlign="center" align="center">产品设计型号
					</div>
					<div field="important" name="important" width="100px" headerAlign="center" align="center"
						 renderer="onImportant">重要度
					</div>
					<div field="taskFrom" name="taskFrom" width="100px" headerAlign="center" align="center">任务来源</div>
					<div field="processStatus" name="processStatus" width="100px" headerAlign="center" align="center"
						 renderer="onProcessStatus">进度状态
					</div>
					<div field="itemType" name="itemType" width="100px" headerAlign="center" align="center"
						 renderer="onTimeType">时间类别
					</div>
					<div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
						<div property="columns" align="center">
							<div field="fah_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
								方案会
							</div>
							<div field="tzgd_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								图纸归档
							</div>
							<div field="tzxf_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								图纸下发
							</div>
							<div field="sapjl_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								SAP建立
							</div>
							<div field="czqj_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								长周期件一次性采购通知
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">工艺技术部
						<div property="columns" align="center">
							<div field="pbomjl_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								PBOM建立
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
						<div property="columns" align="center">
							<div field="zkjdh_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								召开交底会、发试制通知单
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">工艺技术部
						<div property="columns" align="center">
							<div field="gyfa_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								工艺方案
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">制造管理部
						<div property="columns" align="center">
							<div field="scdd_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								生产订单
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">采购管理部
						<div property="columns" align="center">
							<div field="xljdhsj_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								下料件到货时间
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">供方发展部
						<div property="columns" align="center">
							<div field="mjwcsj_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								模具完成时间（包括结构铸件）
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">采购管理部
						<div property="columns" align="center">
							<div field="gzjdw_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
								关重件到位
								<input property="editor" class="mini-datepicker" style="width:100%;"/>
							</div>
							<div field="qbptjwc_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
								全部配套件完成
								<input property="editor" class="mini-datepicker" style="width:100%;"/>
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">制造管理部
						<div property="columns" align="center">
							<div field="yjwczcs_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								样机完成转测试
							</div>
							<div renderer="modelPic" align="center" headerAlign="center">样机照片
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">质量保证部
						<div property="columns" align="center">
							<div field="szgcwtqd_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								试制过程问题清单ppt
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
						<div property="columns" align="center">
							<div field="zjtx_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								整机调试
							</div>
							<div field="cs_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								测试
							</div>
							<div field="yjtzzgwcsj_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								样机图纸整改完成时间
							</div>
							<div field="gykhkssj_date" width="100px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								工业考核开始时间
							</div>
							<div field="xplwcsj_date" width="160px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								小批量完成时间
							</div>
							<div field="sgsywc_date" width="160px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								三高试验完成
							</div>
							<div field="jswj_date" width="160px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								技术文件
							</div>
							<div field="xssy_date" width="160px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								型式试验/欧美认证/农机认证
							</div>
							<div field="cpjd_date" width="160px" headeralign="center" allowsort="true" align="center"
								 dateFormat="yyyy-MM-dd">
								产品鉴定
							</div>
						</div>
					</div>
					<div field="" width="240px" headerAlign="center" align="center">制造管理部
						<div property="columns" align="center">
							<div field="jblczt_date" width="100px" headeralign="center" align="center"
								 dateFormat="yyyy-MM-dd">
								具备量产状态
							</div>
						</div>
					</div>
					<div field="productLeaderName" name="productLeaderName" width="100px" headerAlign="center"
						 align="center">
						产品主管
					</div>
					<div field="modelStatus" name="modelStatus" width="100px" headerAlign="center" align="center"
						 renderer="onModelStatus">样机状态
					</div>
					<div field="isNewModel" name="isNewModel" width="120px" headerAlign="center" align="center"
						 renderer="onIsNewModel">是否为新销售型号
					</div>
					<div field="responseManName" name="responseManName" width="200px" headerAlign="center" align="center">
						项目负责人
					</div>
					<div field="remark" name="remark" width="100px" headerAlign="center" align="center">备注</div>
					<div field="changeReason" name="changeReason" width="100px" headerAlign="center" align="center">变更原因
					</div>
					<div field="planChange" name="planChange" width="100px" headerAlign="center" align="center">计划变更</div>
				</div>
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
	var productGrid = mini.get("productGrid");
	let productApplyForm = new mini.Form("#productApplyForm");
	var importantList = getDics("ZYD");
	var modelStatusList = getDics("YJZT");
	var yesOrNoList = getDics("YESORNO");
	var timeTypeList = getDics("timeType");
	var coverContent = "徐州徐工挖掘机械有限公司";
	var fileListGrid = mini.get("fileListGrid");
	productGrid.frozenColumns(0, 3);
	productGrid.on("load", function () {
		productGrid.mergeColumns(["mainId", "rowNum", "productType", "taskFrom", "processStatus", "productModel", "important", "timeType", "productLeaderName",
			"modelStatus",  "isNewModel", "responseManName", "remark", "changeReason", "planChange"]);
	});
	function onActionRenderer(e) {
		var record = e.record;
		var id = record.id;
		var s = '<span  title="详情" style="cursor: pointer;color:green" onclick="viewForm(\'' + id + '\')">详情</span>';
		return s;
	}
	//修改
	function viewForm(id) {
		var url = jsUseCtxPath + "/rdmZhgl/core/xpszApply/detail.do?id=" + id;
		window.open(url);
	}

	function onDrawCell(e) {
		var record = e.record;
		var field = e.field;
		if (record.itemType != '4') {
			return
		} else {
			if (field) {
				if (field.endsWith("_date")) {
					var mainId = record.mainId;
					var value = e.cellHtml;
					if (value) {
						var result = compareDate(field, value, mainId);
						if (result) {
							e.cellStyle = "background-color:" + result;
						}
					}
				}
			}
		}
	}

	function onProcessStatus(e) {
		var processStatus = e.record.processStatus;
		var _html = '';
		if (processStatus == '1') {
			_html = '<span style="color: green">正常</span>'
		} else if (processStatus == '2') {
			_html = '<span style="color: #d9d90a">轻微落后</span>'
		} else if (processStatus == '3') {
			_html = '<span style="color: red">严重滞后</span>'
		}
		return _html;
	}

	function onIsNewModel(e) {
		var record = e.record;
		var value = record.isNewModel;
		var resultText = '';
		for (var i = 0; i < yesOrNoList.length; i++) {
			if (yesOrNoList[i].key_ == value) {
				resultText = yesOrNoList[i].text;
				break
			}
		}
		return resultText;
	}

	function onModelStatus(e) {
		var record = e.record;
		var value = record.modelStatus;
		var resultText = '';
		for (var i = 0; i < modelStatusList.length; i++) {
			if (modelStatusList[i].key_ == value) {
				resultText = modelStatusList[i].text;
				break
			}
		}
		return resultText;
	}

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

	function onTimeType(e) {
		var record = e.record;
		var value = record.itemType;
		var resultText = '';
		for (var i = 0; i < timeTypeList.length; i++) {
			if (timeTypeList[i].key_ == value) {
				resultText = timeTypeList[i].text;
				break
			}
		}
		return resultText;
	}

	function modelPic(e) {
		var record = e.record;
		var id = record.id;
		var mainId = record.mainId;
		var s = '';
		if (record.itemType == '4') {
			s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
					'onclick="showFilePage(\'' + id + '\',\'' + mainId + '\')">照片列表</a>';
		}
		return s;
	}

	function showFilePage(id, mainId) {
		mini.open({
			title: "样机照片",
			url: jsUseCtxPath + "/rdmZhgl/core/product/fileWindow.do?id=" + id + "&mainId=" + mainId + "&coverContent=" + coverContent + "&editable=false",
			width: 1000,
			height: 500,
			showModal: false,
			allowResize: true,
			onload: function () {
			},
			ondestroy: function (action) {
				// searchDoc();
			}
		});
	}

	function compareDate(field, value, mainId) {
		var result = "";
		var param = {mainId: mainId};
		var url = __rootPath + "/rdmZhgl/core/product/getObj.do";
		var resultData = ajaxRequest(url, "POST", false, param);
		if(resultData.success){
			var planDate = resultData.data[field];
			if (planDate) {
				s2 = new Date(value.replace(/-/g, "/"));
				var time = s2.getTime() - planDate;
				var days = parseInt(time / (1000 * 60 * 60 * 24));
				if (days >= 4 && days <= 8) {
					result = 'yellow';
					yellowFlag = true;
				} else if (days > 8) {
					result = 'red';
					redFlag = true
				}
			}
		}
		return result;
	}
</script>
</body>
</html>
