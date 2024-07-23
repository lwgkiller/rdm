<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
<head>
	<title>可靠性活动信息</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
	<div id="systemTab" class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
		<div id="baseInfo" title="项目基本信息" name="baseInfo" url="${ctxPath}/reliable/core/baseInfo/baseInfoEditPage.do?action=${action}&&id=${id}"></div>
		<div id="sgyk" title="目标定义"  url=""></div>
		<div id="technology" title="新内容分析"  url=""></div>
		<div  title="可靠性继承"  url=""></div>
		<div  title="可靠性预测" url=""></div>
		<div  title="可靠性试验" url=""></div>
		<div  title="更新试验" url=""></div>
		<div  title="试验报告" url=""></div>
	</div>
</div>
<script>
	var ctxPath="${ctxPath}";
</script>

</body>
</html>
