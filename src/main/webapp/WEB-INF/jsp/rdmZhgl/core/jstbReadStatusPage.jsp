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
            <div field="deptname"  align="center" width="80" headerAlign="center">部门</div>
            <div field="readers"  align="center" width="80" headerAlign="center">姓名</div>
            <div field="firstReadTime"  width="100" headerAlign="center" align="center" >阅读时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var gridData=${gridData};
    mini.get("grid_user_score").setData(gridData);

    function readStatusRender(e) {
        var record=e.record;
        if(record.isRead == '已读') {
            return '<span style="color:green">已读</span>';
        }
    }
</script>
</body>
</html>
