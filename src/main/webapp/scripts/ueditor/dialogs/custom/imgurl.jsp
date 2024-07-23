
<%-- 
    Document   : [自定义表单配置设定]编辑页
    Created on : 2017-05-16 10:25:38
    Author     : mansan
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>[自定义表单配置设定]编辑</title>
<%@include file="/commons/edit.jsp"%>

<script type="text/javascript">
	mini.parse();
	var isone =${param['isone']};
	var inputUrl_num=1;

	function onOk(){
		var form = new mini.Form("#p1");   
		form.validate();
		if(!form.isValid()) return;
		CloseWindow("ok");
	} 
	
	function onCancel(){
		CloseWindow("cancel");
	} 
	
	function getUrl(){
		if(isone){
			return mini.get("inputUrl_1").getValue();
		}else {
			var dataList =[];
			for(var i=1;i<inputUrl_num+1;i++){
				var inputUrlObj =mini.get("inputUrl_"+i).getValue();
				if(inputUrlObj){
					dataList.push(inputUrlObj);
				}
			}
			return dataList;
		}
	}

	
	function setUrl(val){
		var obj=mini.get("inputUrl_1");
		obj.setValue(val);
		obj.setText(val);
	}
	
	function clean(e){
		var obj=e.sender;
		obj.setValue("");
		obj.setText("");
	}
	
	
	
	function onButtonEdit(e){
		var id=e.sender.id
		_UserImageDlg(true, function(imgs) {
			if (imgs.length == 0) return;
			
			var obj=mini.get(id);
			var url="/sys/core/file/imageView.do?thumb=true&fileId=" +imgs[0].fileId;
			obj.setValue(url);
			obj.setText(url);
		});
	}
	function addInputUrl() {
		inputUrl_num= inputUrl_num+1;
		var trUrl_id ="trUrl_"+inputUrl_num;
		var tdUrl_id ="tdUrl_"+inputUrl_num;
		var inputUrl_id ="inputUrl_"+inputUrl_num;

		var inputHtml =$('<input id="'+inputUrl_id+'" name="'+inputUrl_id+'" class="mini-buttonedit" ' +
				'width="100%" showClose="true" oncloseclick="clean" onbuttonclick="onButtonEdit" require="true" emptyText="请输入图片URL" >');

		var theTable = document.getElementById("tableObj");//table的id
		var rowCount = theTable.rows.length; //获得当前表格的行数
		var row = theTable.insertRow(rowCount);//在tale里动态的增加tr
		row.id=trUrl_id;
		var cell1 = row.insertCell(0);//在tr中动态的增加td
		cell1.innerText="输入URL";
		var cell2 = row.insertCell(1); //在tr中再动态的增加td
		cell2.id=tdUrl_id;

		var tdUrl_obj =$('#'+tdUrl_id);
		tdUrl_obj.append(inputHtml);
		mini.parse();
	}
	
</script>
</head>
<body>
	<div  class="topToolBar" >
	   <div>
		   <c:if test="${param['isone']==false}">
			   <a class="mini-button"  plain="true" onclick="addInputUrl">新增</a>
		   </c:if>
			<a class="mini-button"  plain="true" onclick="onOk">确定</a>
			<a class="mini-button btn-red" plain="true" onclick="onCancel">关闭</a>
	   </div>
	</div>
	<div class="mini-fit">
		<div  id="p1" class="form-container">
			<table  id="tableObj" class="table-detail column-two table-align">
				<tr>
					<td>输入URL</td>
					<td>
						<input id="inputUrl_1" name="inputUrl_1"
							   class="mini-buttonedit"
							   width="100%"
							   showClose="true"
							   oncloseclick="clean"
							   onbuttonclick="onButtonEdit"
							   require="true" emptyText="请输入图片URL"
						/>
					</td>
				</tr>
			</table>
		</div>
	</div>


</body>
</html>