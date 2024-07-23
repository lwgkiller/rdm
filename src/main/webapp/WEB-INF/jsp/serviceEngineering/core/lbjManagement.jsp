<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/serviceEngineering/lbjManagement.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveLbj('${chineseId}')">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">

				<tr>
					<td style="width: 14%">物料编码：</td>
					<td style="width: 36%;min-width:170px">
						<input id="materialCode" name="materialCode"  class="mini-textbox" style="width:98%;" />

					</td>
					<td style="width: 14%">原始中文：<span style="color:red">*</span></td>
					<td style="width: 36%;min-width:140px">
						<input id="originChineseName" name="originChineseName"  class="mini-textbox" style="width:98%;" />

					</td>
				</tr>
				<tr>
					<td style="width: 14%">中文：<span style="color:red">*</span></td>
					<td style="width: 36%;min-width:170px">
						<input id="chineseName" name="chineseName"  class="mini-textbox" style="width:98%;" />
						<input id="chineseId" name="chineseId"  class="mini-hidden" style="width:98%;" />
					</td>
					<td style="width: 14%">英文：</td>
					<td style="width: 36%;min-width:140px">
						<input id="englishName" name="englishName"  class="mini-textbox" style="width:98%;" />
						<input id="englishId" name="englishId"  class="mini-hidden" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">俄文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="russianName" name="russianName"  class="mini-textbox" style="width:98%;" />
						<input id="russianId" name="russianId"  class="mini-hidden" style="width:98%;" />
					</td>
					<td style="width: 14%">葡文：</td>
					<td style="width: 36%;">
						<input id="portugueseName" name="portugueseName"  class="mini-textbox" style="width:98%;" />
						<input id="portugueseId" name="portugueseId"  class="mini-hidden" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">德文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="germanyName" name="germanyName"  class="mini-textbox" style="width:98%;" />
						<input id="germanyId" name="germanyId" class="mini-hidden" style="width:98%;" />
					</td>
					<td style="width: 14%">西文：</td>
					<td style="width: 36%;">
						<input id="spanishName" name="spanishName"  class="mini-textbox" style="width:98%;" />
						<input id="spanishId" name="spanishId"  class="mini-hidden" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">法文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="frenchName" name="frenchName"  class="mini-textbox" style="width:98%;" />
						<input id="frenchId" name="frenchId"  class="mini-hidden" style="width:98%;" />
					</td>
					<td style="width: 14%">意文：</td>
					<td style="width: 36%;">
						<input id="italianName" name="italianName"  class="mini-textbox" style="width:98%;" />
						<input id="italianId" name="italianId"  class="mini-hidden" style="width:98%;" />
					</td>
				</tr>


                <tr>
                    <td style="width: 14%">波兰语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="polishName" name="polishName"  class="mini-textbox" style="width:98%;" />
                        <input id="polishId" name="polishId"  class="mini-hidden" style="width:98%;" />
                    </td>
                    <td style="width: 14%">土耳其语：</td>
                    <td style="width: 36%;">
                        <input id="turkishName" name="turkishName"  class="mini-textbox" style="width:98%;" />
                        <input id="turkishId" name="turkishId"  class="mini-hidden" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">瑞典语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="swedishName" name="swedishName"  class="mini-textbox" style="width:98%;" />
                        <input id="swedishId" name="swedishId"  class="mini-hidden" style="width:98%;" />
                    </td>
                    <td style="width: 14%">丹麦文：</td>
                    <td style="width: 36%;">
                        <input id="danishName" name="danishName"  class="mini-textbox" style="width:98%;" />
                        <input id="danishId" name="danishId"  class="mini-hidden" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">荷兰语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="dutchName" name="dutchName"  class="mini-textbox" style="width:98%;" />
                        <input id="dutchId" name="dutchId"  class="mini-hidden" style="width:98%;" />
                    </td>
                    <td style="width: 14%">繁体字：</td>
                    <td style="width: 36%;">
                        <input id="chineseTName" name="chineseTName"  class="mini-textbox" style="width:98%;" />
                        <input id="chineseTId" name="chineseTId"  class="mini-hidden" style="width:98%;" />
                    </td>
                </tr>
				<tr>
					<td style="width: 14%">斯洛文尼亚语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="sloveniaName" name="sloveniaName"  class="mini-textbox" style="width:98%;" />
						<input id="sloveniaId" name="sloveniaId"  class="mini-hidden" style="width:98%;" />
					</td>
					<td style="width: 14%">罗马尼亚语：</td>
					<td style="width: 36%;">
						<input id="romaniaName" name="romaniaName"  class="mini-textbox" style="width:98%;" />
						<input id="romaniaId" name="romaniaId"  class="mini-hidden" style="width:98%;" />
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
    var lbjObj=${lbjObj};

    var formStandard = new mini.Form("#formStandard");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";

    var lbjGrid = mini.get("formStandard");


    function setData(data) {
        systemCategoryId='JS';
        isPointManager=data.isPointManager;
    }

</script>
</body>
</html>
