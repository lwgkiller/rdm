
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>集采信息记录编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/matPriceReview/matPriceReviewRecord.js?version=${static_res_version}"
			type="text/javascript"></script>
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />

	<style>
		body .mini-textbox-disabled .mini-textbox-border
		{
			background: #dedbdb;
			color:#6D6D6D;
			cursor:default;
		}
		body .mini-buttonedit-disabled .mini-buttonedit-border,
		body .mini-buttonedit-disabled .mini-buttonedit-input
		{
			background:#dedbdb;color:#6D6D6D;cursor:default;
		}
	</style>
</head>


<body>
<div id="toolBar" class="topToolBar">
	<div>
		<a id="commitForm" class="mini-button" onclick="saveRecord()">保存</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="recordForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="reviewId" name="reviewId" class="mini-hidden"/>
			<input id="jclx" name="jclx" class="mini-hidden"/>
			<table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 200px">
				<tr>
					<td>物料编码：<span style="color:red">*</span></td>
					<td  >
                        <input id="matCode" name="matCode"  class="mini-textbox" style="width:98%;" />
					</td>
					<td>物料描述：<span style="color:red">*</span></td>
					<td  >
						<input id="matName" name="matName"  class="mini-textbox" style="width:85%;" />
                        <image id="syncRecordMatName" src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="自动获取" onclick="syncRecordInfo('matName')"/>
					</td>
					<td>采购组织：<span style="color:red">*</span></td>
					<td>
						<input id="cgzz" name="cgzz" class="mini-textbox" style="width:98%;"/>
					</td>
				</tr>
				<tr>
					<td  >采购公司：<span style="color:red">*</span></td>
					<td  >
						<input id="cggs" name="cggs"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >工厂：<span style="color:red">*</span></td>
					<td  >
						<input id="gc" name="gc"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >供应商名称：<span style="color:red">*</span></td>
					<td  >
						<input name="applierName" id="applierName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td  >供应商编码：<span style="color:red">*</span></td>
					<td  >
						<input name="applierCode" id="applierCode" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >采购单位：<span style="color:red">*</span></td>
					<td  >
						<input name="jldw" id="jldw" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >价格单位：<span style="color:red">*</span></td>
					<td  >
						<input name="jgdw" id="jgdw" class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td  >价格数量：<span style="color:red">*</span></td>
					<td  >
						<input id="jgNumber" name="jgNumber"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >基本单位：<span style="color:red">*</span></td>
					<td  >
						<input id="jbdw" name="jbdw"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >币种：<span style="color:red">*</span></td>
					<td  >
						<input id="bizhong" name="bizhong"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td  >未税单价：<span style="color:red">*</span></td>
					<td  >
						<input id="wsdj" name="wsdj"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >税码：<span style="color:red">*</span></td>
					<td  >
						<input id="shuima" name="shuima"  class="mini-textbox" style="width:98%;" />
					</td>
					<td  >税率：<span style="color:red">*</span></td>
					<td  >
						<input id="shuilv" name="shuilv"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td  >价格有效期从：<span style="color:red">*</span></td>
					<td  >
						<input name="priceValidStart" id="priceValidStart" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >价格有效期至：<span style="color:red">*</span></td>
					<td  >
						<input name="priceValidEnd" id="priceValidEnd" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >付款条件：</td>
					<td  >
						<input name="fktj" id="fktj" class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td  >结算方式：</td>
					<td  >
						<input name="jsfs" id="jsfs" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >暂估价：<span style="color:red">*</span></td>
					<td  >
						<input name="zgPrice" id="zgPrice" class="mini-textbox" style="width:98%;" />
					</td>
                    <td  >价格联动：<span style="color:red">*</span></td>
                    <td  >
                        <input name="jgld" id="jgld" class="mini-textbox" style="width:98%;" />
                    </td>

				</tr>
				<tr>
                    <td  >价格术语：</td>
                    <td  >
                        <input name="jgsy" id="jgsy" class="mini-textbox" style="width:98%;" />
                    </td>
					<td  >出票方：<span style="color:red">*</span></td>
					<td  >
						<input name="cpf" id="cpf" class="mini-textbox" style="width:98%;" />
					</td>
					<td  >行备注：</td>
					<td  >
						<input name="remark" id="remark" class="mini-textbox" style="width:98%;" />
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
    var recordObj=${recordObj};
    var recordForm = new mini.Form("#recordForm");

    $(function () {
        recordForm.setData(recordObj);
        if(action=='view') {
            recordForm.setEnabled(false);
            mini.get("commitForm").hide();
            $("#syncRecordMatName").hide();
        } else {
            recordForm.setEnabled(true);
            mini.get("commitForm").show();
            $("#syncRecordMatName").show();
        }
    });

</script>
</body>
</html>