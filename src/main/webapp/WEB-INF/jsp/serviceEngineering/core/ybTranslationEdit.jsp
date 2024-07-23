<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveYb()">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formYb" method="post">
			<input id="chineseId" name="chineseId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">原始中文：<span style="color:red">*</span></td>
					<td style="width: 36%;min-width:140px">
						<input id="originChinese" name="originChinese"  class="mini-textbox" style="width:98%;" />

					</td>
					<td style="width: 14%">英文：</td>
					<td style="width: 36%;min-width:140px">
						<input id="englishName" name="englishName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">俄文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="russianName" name="russianName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">葡文：</td>
					<td style="width: 36%;">
						<input id="portugueseName" name="portugueseName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">德文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="germanyName" name="germanyName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">西文：</td>
					<td style="width: 36%;">
						<input id="spanishName" name="spanishName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">法文：</td>
					<td style="width: 36%;min-width:170px">
						<input id="frenchName" name="frenchName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">意文：</td>
					<td style="width: 36%;">
						<input id="italianName" name="italianName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>


                <tr>
                    <td style="width: 14%">波兰语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="polishName" name="polishName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 14%">土耳其语：</td>
                    <td style="width: 36%;">
                        <input id="turkishName" name="turkishName"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">瑞典语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="swedishName" name="swedishName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 14%">丹麦文：</td>
                    <td style="width: 36%;">
                        <input id="danishName" name="danishName"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">荷兰语：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="dutchName" name="dutchName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 14%">繁体字：</td>
                    <td style="width: 36%;">
                        <input id="chineseTName" name="chineseTName"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
				<tr>
					<td style="width: 14%">斯洛文尼亚语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="sloveniaName" name="sloveniaName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">罗马尼亚语：</td>
					<td style="width: 36%;">
						<input id="romaniaName" name="romaniaName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">泰语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="thaiName" name="thaiName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">匈牙利语：</td>
					<td style="width: 36%;">
						<input id="hungarianName" name="hungarianName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">挪威语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="norwegianName" name="norwegianName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">韩语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="koreanName" name="koreanName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">印尼语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="indoneName" name="indoneName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">阿拉伯语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="arabicName" name="arabicName"  class="mini-textbox" style="width:98%;" />
					</td>

				</tr>
				<tr>
					<td style="width: 14%">日语：</td>
					<td style="width: 36%;min-width:170px">
						<input id="japaneseName" name="japaneseName"  class="mini-textbox" style="width:98%;" />
					</td>

				</tr>
			</table>
		</form>
	</div>

</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var ybObj=${ybObj};
    var chineseId = "${chineseId}";
    var formYb = new mini.Form("#formYb");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";


    $(function () {
        formYb.setData(ybObj);
    });


    /**
     * 检验表单是否必填
     *
     * @returns {*}
     */
    function validYbUser() {
        var originChinese = $.trim(mini.get("originChinese").getValue())
        if (!originChinese) {
            return {"result": false, "message": "请填写原始中文"};
        }
        return {"result": true};
    }

    /**
     * 新增或者更新的保存
     */
    function saveYb() {
        var formData = formYb.getData();
        var formValid = validYbUser();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var originChinese = $.trim(mini.get("originChinese").getValue())
        //判断该零部件是否存在
        var result = "";
        $.ajax({
            url: jsUseCtxPath + '/yfgj/core/mulitilingualTranslation/getYbExist.do?originChinese=' + originChinese+'&chineseId='+chineseId,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
            }
        });
        if (result == "true") {
            mini.alert("该零部件已存在，请重新填写");
            return;
        }
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/yfgj/core/mulitilingualTranslation/saveYb.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }

</script>
</body>
</html>
