<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>海外特殊订单流程信息</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/world/lccpEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.lccpEdit.lcxx" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.lccpEdit.gb" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 98%">
		<form id="formlccp" method="post" >
			<input id="lccpId" name="lccpId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					<spring:message code="page.lccpEdit.lccpdrxscsp" />
				</caption>
				<tr>
					<td style="width: 15%"><spring:message code="page.lccpEdit.cjsj" />：</td>
					<td>
						<input  id="ckMonth" name="ckMonth" class="mini-datepicker"  format="yyyy-MM-dd" style="width:98%;"/>
					</td>
					<td style="width: 15%"><spring:message code="page.lccpEdit.shxh" />：</td>
					<td>
						<input id="model" name="model" class="mini-textbox" style="  width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.lccpEdit.ckqy" />：</td>
					<td>
						<input id="region" name="region" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="<spring:message code="page.lccpEdit.qxz" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.lccpEdit.qxz" />..."
							   data="[{'key' : '欧洲','value' : '欧洲'},{'key' : '美洲','value' : '美洲'},
									   {'key' : '澳洲','value' : '澳洲'},{'key' : '印度','value' : '印度'},
									   {'key' : '巴西','value' : '巴西'},{'key' : '一般区域','value' : '一般区域'}
									   ]"
						/>
					</td>
					<td style="width: 15%"><spring:message code="page.lccpEdit.ckgj" />：</td>
					<td>
						<input id="country" name="country" class="mini-buttonedit" style="width:98%;"
							   allowInput="false" onbuttonclick="addRelatedProject()" showClose="true" oncloseclick="relatedCloseClick()"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.lccpEdit.ddfzr" />：</td>
					<td>
						<input id="res" name="resId" textname="res"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   />
					</td>
					<td style="width: 15%"><spring:message code="page.lccpEdit.cpzg" />：</td>
					<td>
						<input id="cpzg" name="cpzgId" textname="cpzg"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   onvaluechanged="setRespDept()"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%"><spring:message code="page.lccpEdit.zrbm" />：</td>
					<td>
						<input id="dep" name="depId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="dep" single="true" initlogindep="false"/>
					</td>
					<td style="width: 15%"><spring:message code="page.lccpEdit.lsh" />：</td>
					<td>
						<input id="ddNumber" name="ddNumber" class="mini-textbox" style="width:98%;"/>
					</td>
				</tr>
				<tr>

					<td style="width: 15%"><spring:message code="page.lccpEdit.scrl" />：<br/><span style="color: red"><spring:message code="page.lccpEdit.z" /></span></td>
					<td>
						<input id="need" name="need" class="mini-textbox" style="  width:98%;"/>
					</td>
				<tr>
					<td style="width: 15%;height: 200px "><spring:message code="page.lccpEdit.cpjlscfj" />：</td>
					<td colspan="3">
						<div style="margin-top: 2px;margin-bottom: 2px">
							<a id="addFile1" class="mini-button" onclick="fjupload('1')"><spring:message code="page.lccpEdit.tjfj" /></a>
							<a id="downLoadModel" class="mini-button" onclick="downLoadModel()"><spring:message code="page.lccpEdit.xzmb" /></a>
						</div>
						<div id="ckFileListGrid1" class="mini-datagrid" style="width: 100%; height: 85%"
							 allowResize="false"
							 idField="id" autoload="true"
							 url="${ctxPath}/Lccp/getLccpFileList.do?belongId=${lccpId}&location=1"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="15"><spring:message code="page.lccpEdit.xh" /></div>
								<div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjmc" /></div>
								<div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjdx" /></div>
								<div field="action" width="100" headerAlign='center' align="center"
									 renderer="operationRendererF1"><spring:message code="page.lccpEdit.cz" />
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;height: 200px "><spring:message code="page.lccpEdit.cpzgscfj" />：</td>
					<td colspan="3">
						<div style="margin-top: 2px;margin-bottom: 2px">
							<a id="addFile2" class="mini-button" onclick="fjupload(2)"><spring:message code="page.lccpEdit.tjfj" /></a>
						</div>
						<div id="ckFileListGrid2" class="mini-datagrid" style="width: 100%; height: 85%"
							 allowResize="false"
							 idField="id" autoload="true"
							 url="${ctxPath}/Lccp/getLccpFileList.do?belongId=${lccpId}&location=2"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="15"><spring:message code="page.lccpEdit.xh" /></div>
								<div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjmc" /></div>
								<div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjdx" /></div>
								<div field="action" width="100" headerAlign='center' align="center"
									 renderer="operationRendererF2"><spring:message code="page.lccpEdit.cz" />
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;height: 200px "><spring:message code="page.lccpEdit.zlbzbscfj" />：</td>
					<td colspan="3">
						<div style="margin-top: 2px;margin-bottom: 2px">
							<a id="addFile3" class="mini-button" onclick="fjupload(3)"><spring:message code="page.lccpEdit.tjfj" /></a>
						</div>
						<div id="ckFileListGrid3" class="mini-datagrid" style="width: 100%; height: 85%"
							 allowResize="false"
							 idField="id" autoload="true"
							 url="${ctxPath}/Lccp/getLccpFileList.do?belongId=${lccpId}&location=3"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="15"><spring:message code="page.lccpEdit.xh" /></div>
								<div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjmc" /></div>
								<div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjdx" /></div>
								<div field="action" width="100" headerAlign='center' align="center"
									 renderer="operationRendererF3"><spring:message code="page.lccpEdit.cz" />
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;height: 200px "><spring:message code="page.lccpEdit.cpxscdrpsfj" />：</td>
					<td colspan="3">
						<div style="margin-top: 2px;margin-bottom: 2px">
							<a id="addFile4" class="mini-button" onclick="fjupload(4)"><spring:message code="page.lccpEdit.tjfj" /></a>
						</div>
						<div id="ckFileListGrid4" class="mini-datagrid" style="width: 100%; height: 85%"
							 allowResize="false"
							 idField="id" autoload="true"
							 url="${ctxPath}/Lccp/getLccpFileList.do?belongId=${lccpId}&location=4"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="15"><spring:message code="page.lccpEdit.xh" /></div>
								<div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjmc" /></div>
								<div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.lccpEdit.fjdx" /></div>
								<div field="action" width="100" headerAlign='center' align="center"
									 renderer="operationRendererF4"><spring:message code="page.lccpEdit.cz" />
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<div id="projectWindow" title="<spring:message code="page.lccpEdit.gjxzck" />" class="mini-window" style="width:800px;height:700px;"
	 showModal="true" showFooter="false" allowResize="true">
	<div class="mini-toolbar" >
		<div class="searchBox">
			<form id="searchGrid" class="search-form" style="margin-bottom: 25px">
				<ul>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.lccpEdit.zwmc" />: </span>
						<input class="mini-textbox" id="country_name" name="country_name" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.lccpEdit.ywmc" />: </span>
						<input class="mini-textbox" id="english_name" name="english_name">
					</li>
					<li style="margin-right: 15px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchProcessData()"><spring:message code="page.lccpEdit.cx" /></a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="cleanProcessData()"><spring:message code="page.lccpEdit.qkcx" /></a>
					</li>
					<li style="display: inline-block;float: right;">
						<a id="importBtn" class="mini-button" onclick="choseRelatedProject()"><spring:message code="page.lccpEdit.qr" /></a>
						<a id="closeProjectWindow" class="mini-button btn-red" onclick="closeProjectWindow()"><spring:message code="page.xzxgwjjxyxgs" /></a>
					</li>
				</ul>

			</form>
		</div>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true" idField="id" allowCellWrap="true"
			 url="${ctxPath}/Lccp/getCountryList.do"
			 multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200,400]" pageSize="400" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div field="projectId" visible="false"></div>
				<div type="indexcolumn" headerAlign="center" align="center" width="30"><spring:message code="page.lccpEdit.xh" /></div>
				<div field="country_name"  sortField="country_name"  width="170" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpEdit.zwmc" /></div>
				<div field="english_name"  sortField="english_name" width="110" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpEdit.ywmc" /></div>
			</div>
		</div>
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
    var ckFileListGrid1=mini.get("ckFileListGrid1");
	var ckFileListGrid2=mini.get("ckFileListGrid2");
	var ckFileListGrid3=mini.get("ckFileListGrid3");
	var ckFileListGrid4=mini.get("ckFileListGrid4");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var formlccp = new mini.Form("#formlccp");
    var lccpId="${lccpId}";
	var projectWindow = mini.get("projectWindow");
	var projectListGrid=mini.get("projectListGrid");

	//复用
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

    function operationRendererF1(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnlccpPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.xz" />" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadlccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.lccpEdit.xz" /></span>';
        //增加删除按钮
        if (action!="detail"&&record.CREATE_BY_==currentUserId&&first == 'yes') {
            var deleteUrl = "/Lccp/deleteLccpFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.sc" />" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deletelccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.lccpEdit.sc" /></span>';
        }else {
			cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="<spring:message code="page.lccpEdit.sc" />" style="color: silver"><spring:message code="page.lccpEdit.sc" /></span>';
		}
        return cellHtml;
    }
	function operationRendererF2(e) {
		var record = e.record;
		var cellHtml = '';
		cellHtml = returnlccpPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
		cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.xz" />" style="color:#409EFF;cursor: pointer;" ' +
				'onclick="downLoadlccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.lccpEdit.xz" /></span>';
		//增加删除按钮
		if (action!="detail"&&record.CREATE_BY_==currentUserId&&second == 'yes') {
			var deleteUrl = "/Lccp/deleteLccpFile.do"
			cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.sc" />" style="color:#409EFF;cursor: pointer;" ' +
					'onclick="deletelccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.lccpEdit.sc" /></span>';
		}else {
			cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="<spring:message code="page.lccpEdit.sc" />" style="color: silver"><spring:message code="page.lccpEdit.sc" /></span>';
		}
		return cellHtml;
	}
	function operationRendererF3(e) {
		var record = e.record;
		var cellHtml = '';
		cellHtml = returnlccpPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
		cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.xz" />" style="color:#409EFF;cursor: pointer;" ' +
				'onclick="downLoadlccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.lccpEdit.xz" /></span>';
		//增加删除按钮
		if (action!="detail"&&record.CREATE_BY_==currentUserId&&third == 'yes') {
			var deleteUrl = "/Lccp/deleteLccpFile.do"
			cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.sc" />" style="color:#409EFF;cursor: pointer;" ' +
					'onclick="deletelccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.lccpEdit.sc" /></span>';
		}else {
			cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="<spring:message code="page.lccpEdit.sc" />" style="color: silver"><spring:message code="page.lccpEdit.sc" /></span>';
		}
		return cellHtml;
	}
	function operationRendererF4(e) {
		var record = e.record;
		var cellHtml = '';
		cellHtml = returnlccpPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
		cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.xz" />" style="color:#409EFF;cursor: pointer;" ' +
				'onclick="downLoadlccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.lccpEdit.xz" /></span>';
		//增加删除按钮
		if (action!="detail"&&record.CREATE_BY_==currentUserId&&forth == 'yes') {
			var deleteUrl = "/Lccp/deleteLccpFile.do"
			cellHtml += '&nbsp;&nbsp;&nbsp;<span title="<spring:message code="page.lccpEdit.sc" />" style="color:#409EFF;cursor: pointer;" ' +
					'onclick="deletelccpFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.lccpEdit.sc" /></span>';
		}else {
			cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="<spring:message code="page.lccpEdit.sc" />" style="color: silver"><spring:message code="page.lccpEdit.sc" /></span>';
		}
		return cellHtml;
	}

</script>
</body>
</html>
