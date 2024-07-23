<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/productEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">确定</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="productForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    修改计划时间
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						产品类型：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="productType" readonly class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
					<td align="center" style="white-space: nowrap;">
						产品设计型号：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input  name="productModel" readonly class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
				</tr>
				<tr>
					<td style="text-align: center">延期开始节点：<span style="color: red">*</span></td>
					<td >
						<input id="startItemId" name="startItemId" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="条目："
							   length="50"
							   only_read="false" required  allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="itemName" valueField="id" emptyText="请选择..."
							   url="${ctxPath}/rdmZhgl/core/productConfig/list.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td style="text-align: center">延期结束节点：<span style="color: red">*</span></td>
					<td >
						<input id="endItemId" name="endItemId" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="条目："
							   length="50"  onvaluechanged="verifyEndItem"
							   only_read="false" required allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="itemName" valueField="id" emptyText="请选择..."
							   url="${ctxPath}/rdmZhgl/core/productConfig/list.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						延期天数：<span style="color: red">*</span>
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input name="delayDays" required class="mini-spinner"  minValue="1" maxValue="10000" style="width:100%;height:34px">
					</td>
				</tr>
                </tbody>
            </table>
        </form>
    </div>


</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var productForm = new mini.Form("#productForm");
	var startItemIdCat = mini.get('startItemId');
	var endItemIdCat = mini.get('endItemId');
	function verifyEndItem(e) {
		var endObj = e.selected;
		var sort = endObj.sort;
		var startId = startItemIdCat.getValue();
		if(!startId){
			mini.alert("请先选择开始节点！");
			return;
		}
		let postData = {"startId":startId,"sort":sort};
		let _url = jsUseCtxPath + '/rdmZhgl/core/productConfig/verifyItem.do';
		let resultData = ajaxRequest(_url,'POST',false,postData);
		if(resultData&&!resultData.success){
			mini.alert(resultData.message);
			return;
		}
	}
	function saveData() {
		productForm.validate();
		if (!productForm.isValid()) {
			return;
		}
		var formData = productForm.getData();
		var config = {
			url: jsUseCtxPath+"/rdmZhgl/core/product/editDate.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					CloseWindow("ok");
				}else{
				};
			}
		}
		_SubmitJson(config);
	}

</script>
</body>
</html>
