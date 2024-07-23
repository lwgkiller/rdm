<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件台架资源</title>
    <%@include file="/commons/list.jsp" %>
    <style type="text/css">
        .item {
            float: left;
            width: 80%;
            height: 400px;
            border: solid 1px #ccc;
            border-radius: 4px;
            margin-left: 4px;
            margin-top: 4px;
        }

        .item-image {
            width: 100%;
            height: 350px;
            margin: auto;
            margin-top: 0px;
            margin-left: 0px;
            display: block;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" showHeader="false"
         idField="id" allowResize="false" showPager="true" autoLoad="true"
         viewType="cardview" itemRenderer="cardItemRenderer" showColumns="false"
         sizeList="[3,5,10]" pageSize="3" pagerButtons="#pagerButtons"
         url="${ctxPath}/resourcesPlatform/core/masterData/masterDataListQuery.do">
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var businessListGrid = mini.get("businessListGrid");
    //..
    function cardItemRenderer(record, rowIndex, meta, grid) {
        meta.rowCls = "item";
        var src = "${ctxPath}/resourcesPlatform/core/masterData/imagePreview.do?fileId=" + record.id + "&fileName=" + record.fileName;
        var html = '<div>'
            + '<table class="table-detail" cellspacing="0" cellpadding="0" style="height: 100%;width: 100%;">'
            + '<tr style="height: 40px">'
            + '<td style="text-align: left;width: 30%">'
            + '<span style="font-size: large;color:black">' + record.orderNo + '. ' + record.platformName + '</span>'
            + '</td>'
            + '<td style="text-align: right;width: 30%">'
            + '<span style="font-size: large;color:black">月度利用率: ' + record.monthlyUtiRate * 100 + '% </span>'
            + '</td>'
            + '<td rowspan="2" style="text-align: left;width: 40%">'
            + '<span style="font-size: large;color:black;font-weight:bold;white-space: pre-wrap">' + record.remarks + '</span>'
            + '</td>'
            + '</tr>'
            + '<tr style="height: 350px">'
            + '<td colspan="2"><img class="item-image" src="' + src + '"/></td>'
            + '</tr>'
            + '</table>'
            + '</div>'
        return html;
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
