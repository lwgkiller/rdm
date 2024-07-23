<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/jsStandardPublicEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveStandard('${applyId}')">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 20%">是否有意见:(选择有意见,下面'意见'为必填项)</td>
					<td colspan="2">
						<input id="ifyj" name="ifyj" class="mini-radiobuttonlist" style="width:100%;"
							   repeatItems="2"  value="yes" onvaluechanged="changeFunction"
							   textfield="jgxsName" valuefield="jgxsId"
							   data="[ {'jgxsName' : '有意见','jgxsId' : 'yes'},{'jgxsName' : '无意见','jgxsId' : 'no'}]"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">章节：</td>
					<td style="width: 36%;min-width:170px">
						<input id="chapter" name="chapter"  class="mini-textbox" style="width:98%;" />
					</td>


				</tr>
				<tr>
				<%--	<td style="width: 14%">意见：</td>
					<td style="width: 36%;min-width:140px">
						<input id="opinion" name="opinion"  class="mini-textbox" style="width:98%;" />
					</td>--%>
					<td style="width: 14%">意见(限500字)：<span style="color:red">*</span></td>
					<td colspan="2">
                                <textarea id="opinion" name="opinion" class="mini-textarea rxc"
										  plugins="mini-textarea"
										  style="width:99.1%;height:170px;line-height:25px;"
										  label="意见" datatype="varchar" allowinput="true"
										  emptytext="请输入意见..." mwidth="80" wunit="%" mheight="200"
										  hunit="px"></textarea>
					</td>

				</tr>
				<tr>
					<td style="width: 14%">意见反馈(限500字)：<span style="color:red">*</span></td>
					<td colspan="2">
                                <textarea id="feedback" name="feedback" class="mini-textarea rxc"
										  plugins="mini-textarea"
										  style="width:99.1%;height:170px;line-height:25px;"
										  label="意见反馈" datatype="varchar" allowinput="true"
										  emptytext="请输入意见反馈..." mwidth="80" wunit="%" mheight="200"
										  hunit="px"></textarea>
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
    var standardObj=${standardObj};

    var formStandard = new mini.Form("#formStandard");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";
    var isPsg="${isPsg}";
    var isZqyj="${isZqyj}";

    var grid = mini.get("formStandard");
	//当前体系类别
	var systemCategoryId="";
	//当前是否为大管理员
	var isPointManager="";

    function setData(data) {
        systemCategoryId='JS';
        isPointManager=data.isPointManager;
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    function changeFunction() {
        var ifyj = $.trim(mini.get("ifyj").getValue())
		if(ifyj == 'no'){
            mini.get("opinion").setValue("无意见");
		}
    }
</script>
</body>
</html>
