<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>新品量产前评审表单</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/zlgjNPI/newItemLcpsinfoEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveXplcpsxx('${xplcId}')">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formXplcpsxx" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">产品：</td>
					<td style="width: 36%;min-width:170px">
						<input id="product" name="product"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">机型：</td>
					<td style="width: 36%;min-width:170px">
						<input id="jixin" name="jixin"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">零部件：</td>
					<td style="width: 36%;min-width:170px">
						<input id="lbj" name="lbj"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">问题清单：</td>
					<td style="width: 36%;min-width:170px">
						<input id="wtqd" name="wtqd"  class="mini-textarea" style="width:98%;overflow: auto" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">责任人：</td>
					<td style="width: 36%;min-width:170px">
						<input id="zrrId" name="zrrId" textname="zrrName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="项目负责人" length="50" maxlength="50"  mainfield="no"  single="true" onvaluechanged="setUserDept()"/>
					</td>
					<td style="width: 14%">责任部门：</td>
					<td style="width: 36%;min-width:170px">
						<input id="bmId" name="bmId" textname="bmName" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="bmName" length="500" maxlength="500" minlen="0" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">后续优化措施：</td>
					<td colspan="3">
                                <textarea id="yhcs" name="yhcs" class="mini-textarea rxc"
										  plugins="mini-textarea"
										  style="width:99.1%;height:120px;line-height:25px;"
										  label="" datatype="varchar" allowinput="true"
										  emptytext="请输入优化措施..." mwidth="80" wunit="%" mheight="300"
										  hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">完成时间</td>
					<td style="width: 36%;min-width:170px">
						<input id="wcTime" name="wcTime" class="mini-datepicker" format="yyyy-MM-dd"
							   style="width:98%"/>
					</td>
				</tr>

			</table>
		</form>
	</div>

</div>

<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var xplcxxObj=${xplcxxObj};
    var formXplcpsxx = new mini.Form("#formXplcpsxx");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";
    var isFqBianZhi="${isFqBianZhi}";
    var isShBianZhi="${isShBianZhi}";
    var isHqBianZhi="${isHqBianZhi}";

    function setUserDept() {
        var userId=mini.get("zrrId").getValue();
        if(!userId) {
            mini.get("bmId").setValue('');
            mini.get("bmId").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("bmId").setValue(data.mainDepId);
                mini.get("bmId").setText(data.mainDepName);
            }
        });
    }


</script>
</body>
</html>
