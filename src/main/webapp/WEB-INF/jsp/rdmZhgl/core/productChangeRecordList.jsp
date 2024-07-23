
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>变更流程</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	<div class="mini-fit" style="height: 100%;">
		<div id="productChangeGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="startItem" align="center" headerAlign="center" >开始节点</div>
				<div field="endItem" align="center"  headerAlign="center"  >结束节点</div>
				<div field="delayDays" align="center" headerAlign="center" >延期天数</div>
				<div field="reason" align="center"  headerAlign="center" >延期原因</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" >创建时间</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var productChangeGrid=mini.get("productChangeGrid");
    var mainId = "${mainId}";
	var url=jsUseCtxPath+"/rdmZhgl/core/product/changeRecords.do";
	$(function () {
        queryProjectFiles();
	});

	function queryProjectFiles() {
        productChangeGrid.setUrl(url+"?mainId="+mainId);
        productChangeGrid.load();
    }
</script>
<redxun:gridScript gridId="productChangeGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
