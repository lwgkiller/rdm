<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <%@include file="/commons/edit.jsp" %>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 2px 2px 2px 2px;
        }

    </style>
</head>
<body>
<div class="mini-panel" style="width: 100%; height: 100%" showfooter="true" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <div id="grid_user_score" class="mini-datagrid"
         allowResize="false" allowSortColumn="false" multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" >
        <div property="columns">
            <div field="name"   width="80" headerAlign="center" align="center" ><spring:message code="page.xcmgScoreUserScoreShow.name" /></div>
            <div field="workName"  width="140" headerAlign="center" align="center" ><spring:message code="page.xcmgScoreUserScoreShow.name1" /></div>
            <div field="dutyName"    width="80" headerAlign="center" align="center" renderer="onDutyNameRender"><spring:message code="page.xcmgScoreUserScoreShow.name2" /></div>
            <div field="stageScore"  align="center" width="120" headerAlign="center"><spring:message code="page.xcmgScoreUserScoreShow.name3" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var gridData=${gridData};
    mini.get("grid_user_score").setData(gridData);
    function onDutyNameRender() {
        return "***";
    }
</script>
</body>
</html>
