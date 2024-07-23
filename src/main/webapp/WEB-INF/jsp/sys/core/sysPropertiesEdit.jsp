
<%-- 
    Document   : [系统参数]编辑页
    Created on : 2017-06-21 11:22:36
    Author     : ray
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>[系统参数]编辑</title>
<%@include file="/commons/edit.jsp"%>
</head>
<body>

	<rx:toolbar toolbarId="toolbar1" pkId="sysProperties.id" />

	<div class="mini-fit">
	<div id="p1" class="form-container">
		<form id="form1" method="post">
				<input id="pkId" name="id" class="mini-hidden" value="${sysProperties.proId}" />
				<table class="table-detail column-four table-align" cellspacing="1" cellpadding="0">
					<caption>[系统参数]基本信息</caption>
					<tr>
						<td>
							名　　称<span class="star">*</span>
						</td>
						<td>
								<input name="name" value="${sysProperties.name}"
							class="mini-textbox"   style="width: 90%"  required/>
						</td>
						<td>是否全局</td>
						<td>
							<div  name="global"  class="mini-radiobuttonlist" repeatItems="2" repeatLayout="table" 
    textField="text" valueField="id"  value="${sysProperties.global}"
    data="[{'id':'YES','text':'是'},{'id':'NO','text':'否'}]" >
						</td>
					</tr>
					<tr>
					<td>
						<span class="starBox">
							别　　名<span class="star">*</span>
						</span>
					</td>
						<td>
								<input id="alias" class="mini-textbox" name="alias" required="true"  style="width: 90%" value="${sysProperties.alias}"  onvalidation="uniqueValidation"/>
						</td>
						<td>是否加密存储</td>
						<td>
							<div name="encrypt" class="mini-radiobuttonlist" repeatItems="2" repeatLayout="table" 
    textField="text" valueField="id"  value="${sysProperties.encrypt}"
    data="[{'id':'YES','text':'是'},{'id':'NO','text':'否'}]" >
						</td>
					</tr>
					<tr>
						<td>参  数  值</td>
						<td>
								<input name="value" value='${sysProperties.value}'
							class="mini-textbox"   style="width: 90%" />
						</td>
						<td>
							<span class="starBox">
								分　　类<span class="star">*</span>
							</span>
						</td>
						<td>
						<input id="category" class="mini-combobox" name="category" style="width:150px;" textField="key" valueField="key" 
    url="${ctxPath}/sys/core/sysProperties/getCategories.do" value="${sysProperties.category}" showNullItem="true" required="true" allowInput="true"/>        
							
						</td>
					</tr>
					<tr>
						<td>描　　述</td>
						<td colspan="3">
								<textarea class="mini-textarea" name="description" value="${sysProperties.description}"
							   style="width: 90%" ></textarea>
						</td>
					</tr>
				</table>
		</form>
	</div>
	</div>
	<rx:formScript formId="form1" baseUrl="sys/core/sysProperties"
		entityName="com.redxun.sys.core.entity.SysProperties" />
	<script type="text/javascript">
		addBody();
		//验证唯一性alias
		function uniqueValidation(e){
			var alias=mini.get("alias").getValue();
			var id=mini.get("pkId").getValue();
			if (e.isValid) {
				$.ajax({
					type:"post",
					async:false,
					url:"${ctxPath}/sys/core/sysProperties/uniqueValidation.do",
					data:{alias:alias,id:id},
					success:function (result){
						if(result.success){
							e.isValid = true;
						}else{
							e.errorText = "不允许与其他参数重复";
							e.isValid = false;
						}
					}
				});
			}
		}
	
	</script>
</body>
</html>