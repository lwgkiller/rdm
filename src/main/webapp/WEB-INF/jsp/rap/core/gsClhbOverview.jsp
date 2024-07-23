<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>标准制修订完成情况报表</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/mini/boot.js?static_res_version=${static_res_version}"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/share/dialog.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/list.js?static_res_version=${static_res_version}" type="text/javascript"></script>

    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>

    <script src="${ctxPath}/scripts/rap/gsClhbOverview.js?version=${static_res_version}"
            type="text/javascript"></script>

    <style>
        .gridster ul li header {
            background: none repeat scroll 0% 0% #f5f7fa;
            display: block;
            font-size: 20px;
            line-height: normal;
            padding: 4px 0px 6px;
            cursor: move;
            text-align: center;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }
    </style>
</head>
<body>
<div class="personalPort" style="min-width: 1679px;">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-col="24" data-row="2" data-sizex="24" data-sizey="1">
                <header>
                    <p>各产品所申请信息公开的数据汇总</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    开始时间从
                    <input id="deptNumTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryDeptNumData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="deptNumTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryDeptNumData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="deptNumDataBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="24" data-row="3" data-sizex="24" data-sizey="1">
                <header>
                    <p>国四环保信息公开月度完成量</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    完成时间从
                    <input id="monthNumTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryMonthNumData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="monthNumTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryMonthNumData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="monthNumDataBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="1" data-sizex="9" data-sizey="1">
                <header>
                    <p>各产品所超期30天未关闭的流程数量</p>
                </header>
                <div id="deptDelayDataBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="18" data-row="1" data-sizex="5" data-sizey="1">
                <header>
                    <p>国四环保信息公开年度完成量</p>
                </header>
                <div id="yearNumDataBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="8" data-row="1" data-sizex="10" data-sizey="1">
                <header>
                    <p>各发动机品牌产品数</p>
                </header>
                <div id="modelNumDataBar" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var lastMonth = "${lastMonth}";

    var deptNumDataBar = echarts.init(document.getElementById('deptNumDataBar'));
    var deptDelayDataBar = echarts.init(document.getElementById('deptDelayDataBar'));
    var yearNumDataBar = echarts.init(document.getElementById('yearNumDataBar'));
    var monthNumDataBar = echarts.init(document.getElementById('monthNumDataBar'));
    var modelNumDataBar = echarts.init(document.getElementById('modelNumDataBar'));


    function overview() {
        var width = (1679 - 3 * 8) / 24 - 8;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 480],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 24,
            widget_margins: [8, 8],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        gridster.disable();
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

    deptDelayDataBar.on('click', function (params) {
        var deptName = params.name;
        var url = jsUseCtxPath + "/environment/core/Cx/list.do?deptName=" + deptName+"&checkDelay=yes";
        window.open(url);
    })

</script>
</body>
</html>
