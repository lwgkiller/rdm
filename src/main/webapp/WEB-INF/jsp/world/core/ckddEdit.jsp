<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>海外特殊订单流程信息</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/world/ckddEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.ckddEdit.sc" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.ckddEdit.gb" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 98%">
		<form id="formCkdd" method="post" >
			<input id="ckddId" name="ckddId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="rdmSpecialddNumber" name="rdmSpecialddNumber" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					<spring:message code="page.ckddEdit.ckddsp" />
				</caption>
				<tr>
					<td style="width: 15%"><spring:message code="page.ckddEdit.yf" />：</td>
					<td>
						<input  id="ckMonth" name="ckMonth" class="mini-datepicker"  format="yyyy-MM-dd" style="width:98%;"/>
					</td>
					<td style="width: 15%"><spring:message code="page.ckddEdit.cpxh" />：</td>
					<td>
						<input id="model" name="model" class="mini-textbox" style="  width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.ckddEdit.ckgj" />：</td>
					<td>
						<input id="country" name="country" class="mini-textbox" style="  width:98%;"/>
					</td>
					<td style="width: 15%"><spring:message code="page.ckddEdit.ddfzr" />：</td>
					<td>
						<input id="res" name="resId" textname="res"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   />
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.ckddEdit.cpzg" />：</td>
					<td>
						<input id="cpzg" name="cpzgId" textname="cpzg"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   onvaluechanged="setRespDept()"/>
					</td>
					<td style="width: 15%"><spring:message code="page.ckddEdit.zrbm" />：</td>
					<td>
						<input id="dep" name="depId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="dep" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.ckddEdit.ddh" />：</td>
					<td>
						<input id="ddNumber" name="ddNumber" class="mini-textbox" style="  width:98%;"/>
					</td>
					<td style="width: 15%"><spring:message code="page.ckddEdit.xqsl" />：</td>
					<td>
						<input id="need" name="need" class="mini-textbox" style="  width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width:15%;height: 300px"><spring:message code="page.ckddEdit.sjjlb" />：</td>
					<td colspan="3">
						<div style="margin-top: 5px;margin-bottom: 2px">
							<a id="addTime" class="mini-button"  onclick="addTime()"><spring:message code="page.ckddEdit.tj" /></a>
							<a id="removeTime" class="mini-button"  onclick="removeTime()"><spring:message code="page.ckddEdit.shac" /></a>
							<span style="color: red"><spring:message code="page.ckddEdit.z" /></span>
						</div>
						<div id="timeListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/Ckdd/getTimeList.do?belongId=${ckddId}" autoload="true"
							 allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditTime"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div field="timeType"  width="40" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.sjlx" />
									<input property="editor"  class="mini-combobox" style="width:98%;"
										   textField="value" valueField="key" emptyText="<spring:message code="page.ckddEdit.qxz" />..."
										   required="false" allowInput="false" showNullItem="true"
										   nullItemText="<spring:message code="page.ckddEdit.qxz" />..."
										   data="[
										   {'key' : '要求交货日期','value' : '要求交货日期'}
										   ,{'key' : '入库日期','value' : '入库日期'}
										   ,{'key' : '发车日期','value' : '发车日期'}]"
									/></div>
								<div field="timeSelect" dateFormat="yyyy-MM-dd" headerAlign='center' width="60" align="center"><spring:message code="page.ckddEdit.sj" />
									<input  property="editor" class="mini-datepicker"   style="width:98%;"/>
								</div>
								<div field="total"  width="40" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.ts" />
									<input property="editor" class="mini-textbox" />
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="width:15%;height: 800px"><spring:message code="page.ckddEdit.cppzjfxkzcs" />：</td>
					<td colspan="3">
						<div style="margin-bottom: 2px">
							<a id="addCkddDetail" class="mini-button"  onclick="addCkddDetail()"><spring:message code="page.ckddEdit.tj" /></a>
							<a id="copyCkddDetail" class="mini-button"  onclick="copyCkddDetail()"><spring:message code="page.ckddEdit.fz" /></a>
							<a id="removeCkddDetail" class="mini-button"  onclick="removeCkddDetail()"><spring:message code="page.ckddEdit.shac" /></a>
							<span style="color: red"><spring:message code="page.ckddEdit.z" /></span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/Ckdd/getDetailList.do?belongId=${ckddId}" autoload="true"
							 allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditDetail"
							 multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div field="config" name="config"  width="60" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.cppz" />
									<input property="editor" class="mini-textbox" /></div>
								<div field="risk"  width="60" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.fxd" />
									<input property="editor" class="mini-textbox" /></div>
								<div field="solveId" displayField ="solve" width="30" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.zrr" />
									<input  property="editor" class="mini-user rxc" plugins="mini-user"
											style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
											mainfield="no" single="true" name="solveId" textname="solve" /></div>
								<div field="measures"  width="60" headerAlign="center" align="center" ><spring:message code="page.ckddEdit.kzcs" />
									<input property="editor" class="mini-textbox" /></div>
								<div field="finishTime" dateFormat="yyyy-MM-dd" headerAlign='center' width="40" align="center"><spring:message code="page.ckddEdit.wcsj" />
									<input  property="editor" class="mini-datepicker" style="width:98%;"/></div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;height: 200px "><spring:message code="page.ckddEdit.fjlb" />：</td>
					<td colspan="3">
						<div style="margin-top: 2px;margin-bottom: 2px">
							<a id="addFile" class="mini-button" onclick="fjupload()"><spring:message code="page.ckddEdit.tjfj" /></a>
						</div>
						<div id="ckFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
							 allowResize="false"
							 idField="id" autoload="true"
							 url="${ctxPath}/Ckdd/getCkddFileList.do?belongId=${ckddId}"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="15"><spring:message code="page.ckddEdit.xh" /></div>
								<div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.ckddEdit.fjmc" /></div>
								<div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.ckddEdit.fjdx" /></div>
								<div field="action" width="100" headerAlign='center' align="center"
									 renderer="operationRendererF"><spring:message code="page.ckddEdit.cz" />
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
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var currentTime="${currentTime}";
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var timeListGrid=mini.get("timeListGrid");
    var ckFileListGrid=mini.get("ckFileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var formCkdd = new mini.Form("#formCkdd");
    var ckddId="${ckddId}";
    detailListGrid.on("load", function () {
        detailListGrid.mergeColumns(["config"]);
    });
    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if (field == "config" && (action == "edit"||first=="yes")) {
            e.cancel = false;
        }
        if (field == "risk" && (action == "task"&&second=="yes")) {
            e.cancel = false;
        }
        if (field == "solveId" && (action == "task"&&second=="yes")) {
            e.cancel = false;
        }
        if ((field == "measures" && (action == "task"&&third=="yes"))&&record.solveId==currentUserId) {
            e.cancel = false;
        }
        if (field == "finishTime" && (action == "task"&&third=="yes")&&record.solveId==currentUserId) {
            e.cancel = false;
        }
    }

    function OnCellBeginEditTime(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if (action == "edit"||first=="yes"||forth=="yes") {
            e.cancel = false;
        }
    }

    function setRespDept() {
        var userId=mini.get("cpzg").getValue();
        if(!userId) {
            mini.get("dep").setValue('');
            mini.get("dep").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("dep").setValue(data.mainDepId);
                mini.get("dep").setText(data.mainDepName);
            }
        });
    }

    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnCkddPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + ckddEdit_xz + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCkddFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.ckddEdit.xz" /></span>';
        //增加删除按钮
        if (action!="detail"&&record.CREATE_BY_==currentUserId) {
            var deleteUrl = "/Ckdd/deleteCkddFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + ckddEdit_schu + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteCkddFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.ckddEdit.shac" /></span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
