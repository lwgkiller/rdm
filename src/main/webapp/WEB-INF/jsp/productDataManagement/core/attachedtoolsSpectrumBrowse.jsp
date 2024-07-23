<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/commons/list.jsp" %>
    <style type="text/css">
        .item {
            float: left;
            width: 200px;
            height: 250px;
            border: solid 1px #ccc;
            border-radius: 4px;
            margin-left: 4px;
            margin-top: 4px;
        }

        .item-inner {
            width: 100%;
        }

        .item-image {
            width: 200px;
            height: 190px;
            margin: auto;
            margin-top: 8px;
            display: block;
        }

        .item-text {
            text-align: center;
            font-size: 16px;
            font-family: "微软雅黑";
            letter-spacing: 5px;
            font-weight: bold;
            padding-top: 5px;
        }

        .item-action {
            text-align: right;
            padding-right: 6px;
        }

        .button {
            display: inline-block;
            padding: 3px 8px;
            font-size: 13px;
            font-weight: normal;
            line-height: 1.4;
            text-align: center;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            border: 1px solid transparent;
            border-radius: 4px;
            color: #fff;
            background-color: #337ab7;
            border-color: #2e6da4;
        }
    </style>
</head>
<body>
<%----%>
<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
    <div showHeader="false" region="west" width="250" maxWidth="250" minWidth="100" showSplitIcon="true">
        <div id="leftTree" class="mini-outlookmenu" onitemselect="attachedtoolsTypeChanged"
             idField="id" parentField="pid" textField="text" borderStyle="border:0" expandOnLoad="false"
             style="font-size: 16px;"
             url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/modelGroupQuery.do">
        </div>
    </div>
    <div title="center" region="center" bodyStyle="overflow:hidden;">
        <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" showHeader="false"
             idField="id" allowResize="false" showPager="false" autoLoad="false"
             viewType="cardview" itemRenderer="cardItemRenderer" showColumns="false"
             url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/modelQueryByGroup.do?attachedtoolsType=attachedtoolsType">
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var layout1 = mini.get("layout1");
    var leftTree = mini.get("leftTree");
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {

    });
    //..
    function attachedtoolsTypeChanged(e) {
        var item = e.item;
        var url = '${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/modelQueryByGroup.do?attachedtoolsType=' + item.id;
        businessListGrid.setUrl(url);
        businessListGrid.load();
    }
    //..
    function cardItemRenderer(record, rowIndex, meta, grid) {
        meta.rowCls = "item";
        var src = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/imageView.do?fileId=" +
            record.id + "&fileName=" + record.fileName;
        var html = '<div class="item-inner">'
            + '<div class="item-text">' + record.attachedtoolsType2 + '</div>'
            + '<img class="item-image" src="' + src + '"/>'
            + '<div class="item-action"><button class="button add">查看</button></div></div>';
        return html;
    }
    //..
    $(businessListGrid.el).on("click", ".add", function (event) {
        var record = businessListGrid.getRowByEvent(event);
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/configPage.do?mainId=" + record.id + "&action=browse";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
            }
        }, 1000);
    });
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>