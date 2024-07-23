<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/index/rdmHome.js?static_res_version=${static_res_version}" type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }
        .item
        {
            float:left;
            width:190px;
            height:110px;
            border:solid 1px #ccc;
            border-radius:4px;
            margin-left:8px;
            margin-top:8px;
        }
        .item-inner
        {
            width:100%;
        }
        .item-image
        {

            height:75px;
            width:75px;
            margin:auto;
            margin-top:5px;
            display:block;
            cursor: pointer;
        }
        .item-text
        {
            text-align:center;
            font-size:10px;
            font-family:"微软雅黑";
            font-weight:bold;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="mini-panel" style="width: 100%; height: 100%" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <div class="mini-datagrid" id="subMenuGrid" style="width: 100%; height: 100%;" allowResize="false" showHGridLines="false"
         idField="id" allowAlternating="true" showPager="false" multiSelect="false" showColumns="false"
         viewType="cardview" itemRenderer="itemRenderer" >
        <div property="columns">
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var subMenuGrid = mini.get("subMenuGrid");

    function setSubMenuData(menuData) {
        if(menuData.children) {
            subMenuGrid.setData(menuData.children);
        }
        if(menuData.openWindows) {
            openWindows=menuData.openWindows;
        }
    }

    $(subMenuGrid.el).on("click", function (event) {
        var record = subMenuGrid.getRowByEvent(event);
        clickMenu(record)
    });

</script>
</body>
</html>
