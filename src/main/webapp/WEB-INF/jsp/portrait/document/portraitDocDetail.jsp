<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
<head>
	<title>档案详情</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
	<div id="systemTab" class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
		<div id="baseInfo" title="基本信息" name="baseInfo" url="${ctxPath}/portrait/document/userInfoPage.do?userId=${userId}&reportYear=${reportYear}"></div>
		<div id="technology" title="技术创新"  url="${ctxPath}/portrait/document/innovatePersonListPage.do?userId=${userId}&reportYear=${reportYear}"></div>
		<div  title="技术协同"  url="${ctxPath}/portrait/document/teamWorkPersonListPage.do?userId=${userId}&reportYear=${reportYear}"></div>
		<div  title="敬业表现" url="${ctxPath}/portrait/document/employListPage.do?userId=${userId}&reportYear=${reportYear}"></div>
		<div  title="职业发展" url="${ctxPath}/portrait/document/otherListPage.do?userId=${userId}&reportYear=${reportYear}"></div>
	</div>
</div>
<script>
	var ctxPath="${ctxPath}";
	var userId="${userId}";
	var reportYear="${reportYear}";
</script>

</body>
</html>
