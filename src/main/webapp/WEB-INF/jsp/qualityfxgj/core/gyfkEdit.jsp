<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>工艺反馈信息说明编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/qualityfxgj/gyfkEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 100%">
		<form id="formGyfk" method="post" >
			<input id="gyfkId" name="gyfkId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					工艺信息反馈
				</caption>
				<tr>
					<td style="width: 7%">产品主管：</td>
					<td>
						<input id="repPerson" name="repPersonId" textname="repPerson"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   onvaluechanged="setRespDept()"/>
					</td>
					<td style="width: 7%">责任部门：</td>
					<td style="width: 33%;min-width:170px;font-size:14pt">
						<input id="repDep" name="repDepId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="repDep" single="true" initlogindep="false"/>
					</td>
					<td style="width: 7%">工艺审核室主任：</td>
					<td>
						<input id="checkZrName" name="checkZrId" textname="checkZrName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 7%;height: 750px">详细信息：</td>
					<td colspan="5">
						<div style="margin-top: 5px;margin-bottom: 2px">
							<a id="addGyfkDetail" class="mini-button"  onclick="addGyfkDetail()">添加</a>
							<a id="editGyfkDetail" class="mini-button"  onclick="editGyfkDetail()">编辑</a>
							<a id="removeGyfkDetail" class="mini-button"  onclick="removeGyfkDetail()">删除</a>
							<span style="color: red">注：添加前请先进行表单的保存/请处理责任人是当前用户的问题清单</span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/qualityfxgj/core/Gyfk/getGyfkDetailList.do?belongGyfkId=${gyfkId}" autoload="true"
							 multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div header="问题清单" headerAlign="center">
									<div property="columns">
										<div field="gytype"  width="40" headerAlign="center" align="center" >类型</div>
										<div field="problem"  width="80" headerAlign="center" align="center" >问题描述</div>
										<div field="model"  width="60" headerAlign="center" align="center" >机型</div>
										<div field="part"  width="60" headerAlign="center" align="center" >部件</div>
										<div field="involveModel"  width="60" headerAlign="center" align="center" >涉及机型</div>
										<div field="apply" width="40"  headerAlign='center' align="center">发起人</div>
										<div field="CREATE_TIME_" width="60" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">发起时间</div>
										<div field="res"  width="40" headerAlign="center" align="center">责任人</div>
									</div>
								</div>
								<div header="任务清单" headerAlign="center">
									<div property="columns">
										<div field="method"  width="120" headerAlign="center" align="center" renderer="duiceRender">对策</div>
										<div field="completion" align="center" headerAlign="center" width="120" renderer="finishDescRender">完成情况</div>
										<div field="finishTime" dateFormat="yyyy-MM-dd" headerAlign='center' width="60" align="center">完成时间</div>
									</div>
								</div>
								<div  align="center" headerAlign="center" width="40" renderer="fujian">附件</div>
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
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var formGyfk = new mini.Form("#formGyfk");
    var gyfkId="${gyfkId}";
    function fujian(e) {
        var record = e.record;
        var gyxjId = record.gyxjId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="gyfkFile(\'' + gyxjId + '\')">附件列表</span>';
        return s;
    }
    function setRespDept() {
        var userId=mini.get("repPerson").getValue();
        if(!userId) {
            mini.get("repDep").setValue('');
            mini.get("repDep").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("repDep").setValue(data.mainDepId);
                mini.get("repDep").setText(data.mainDepName);
            }
        });
    }



</script>
</body>
</html>
