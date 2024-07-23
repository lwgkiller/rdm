
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>物料主数据扩充说明</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	<div class="mini-fit" style="height: 100%;margin: 0 auto;width: 70%">
		<div id="propertyGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/materielExtend/apply/materielPropertyDesc.do" multiSelect="false" allowCellWrap="true" showColumnsMenu="false" showPager="false">
			<div property="columns">
                <div type="indexcolumn" width="10%"></div>
				<div field="operaterName" name="operaterName" width="20%" headerAlign="center" align="center" >填写人</div>
				<div field="propertyName" name="propertyName" width="20%" headerAlign="center" align="center" >属性名称</div>
				<div field="propertyDesc" name="propertyDesc" width="50%" headerAlign="center" align="left" >填写说明</div>
			</div>
		</div>
	</div>


	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";

        var propertyGrid=mini.get("propertyGrid");
        propertyGrid.load();
        propertyGrid.on("load", function () {
            propertyGrid.mergeColumns(["operaterName"]);
        });
	</script>
	<redxun:gridScript gridId="propertyGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>