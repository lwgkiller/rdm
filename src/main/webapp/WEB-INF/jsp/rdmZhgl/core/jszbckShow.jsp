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
<%--<div class="mini-panel" style="width: 100%; height: 100%" showfooter="true" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">--%>
<div class="mini-fit" style="height: 100%;">
    <div id="grid_project_ruleInfo" class="mini-datagrid" style="width: 100%; height: 100%;"
         url="${ctxPath}/zhgl/core/jszb/getCkList.do" idField="id"
         allowResize="false" allowSortColumn="false" multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" >
        <div property="columns">
            <div field="lcfl"  name="lcfl"  width="80" headerAlign="center" align="center" allowSort="false">流程分类</div>
            <div field="num"  width="40" headerAlign="center" align="center" allowSort="false">序号</div>
            <div field="lcwjlx" align="center" width="100" headerAlign="center" allowSort="false">流程文件类型</div>
            <div field="sffgsp"  width="60" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">是否分管领导审批</div>
            <%--<div field="ssjd" width="50"  allowSort="false" headerAlign="center" align="center" >所属阶段</div>--%>
        </div>
    </div>
</div>
<%--</div>--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid_project_ruleInfo=mini.get("grid_project_ruleInfo");
    grid_project_ruleInfo.load();
    grid_project_ruleInfo.on("load", function () {
        grid_project_ruleInfo.mergeColumns(["lcfl"]);
    });
    function onStatusRenderer(e) {
        var record = e.record;
        var sffgsp = record.sffgsp;

        var arr = [ {'key' : 'yes','value' : '是'}
        ];
        return $.formatItemValue(arr,sffgsp);
    }
</script>
</body>
</html>
