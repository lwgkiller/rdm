<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的门户</title>
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
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <link  href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link  href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css" />

    <script src="${ctxPath}/scripts/rdmZhgl/report/effectiveAuthorizedPieChart.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/patentApplyPieChart.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/inventPatentApplyPieChart.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/effectAuthorizedBarChart.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/patentApplyBarChart.js?version=${static_res_version}" type="text/javascript"></script>
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
            <li class="gs-w" data-col="1" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <%--<p>有效授权中国专利量</p>--%>
                    <span style="float: right;font-size: 13px;color: #333;vertical-align: middle">
						授权日期从
						<input id="effectiveAuthorizedBuildFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectiveAuthorizedBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="effectiveAuthorizedBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectiveAuthorizedBuildToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
                </header>
                <div id="effectiveAuthorized" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="9" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <%--<p>中国专利申请量</p>--%>
                    <p style="float: right; font-size: 13px;color: #333;vertical-align: middle">
                        申请时间:
						<input id="patentApplyBuildFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="patentApplyBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="patentApplyBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="patentApplyBuildToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</p>
                </header>
                <div id="patentApplyPie" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <%--<p>中国发明专利申请量</p>--%>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						申请时间从
						<input id="inventPatentApplyBuildFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="inventPatentApplyBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="inventPatentApplyBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="inventPatentApplyBuildToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
                </header>
                <div id="inventPatentApply" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="24" data-row="2" data-sizex="24" data-sizey="1">
                <%--<header>--%>
                    <%--<p>${maxYear}年授权专利量</p>--%>
                    <%--<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">--%>
                        <%--年份--%>
                        <%--<input id="effectAuthorizedBarBuildFrom" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectAuthorizedBarBuildFromChanged()" onfocus="this.blur()" class="mini-spinner" min-value="1900" maxValue="${maxYear}" value=""/>--%>
                    <%--统计时间从
                    <input id="effectAuthorizedBarBuildFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectAuthorizedBarBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
                    至
                    <input id="effectAuthorizedBarBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectAuthorizedBarBuildToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>--%>
                    <%--&nbsp;&nbsp;结项
                    <input id="effectAuthorizedBarKnotFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectAuthorizedBarKnotFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
                    至
                    <input id="effectAuthorizedBarKnotTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="effectAuthorizedBarKnotToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>--%>
                    <%--</span>--%>
                <%--</header>--%>
                <div id="effectAuthorizedBar" class="containerBox" style="height: 100%;">
                </div>
            </li>
            <li class="gs-w" data-col="24" data-row="3" data-sizex="24" data-sizey="1">
                <div id="patentApplyBar" class="containerBox" style="height: 100%;">
                </div>
            </li>

        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var lastMonth="${lastMonth}";
    var effectiveAuthorizedChart = echarts.init(document.getElementById('effectiveAuthorized'));
    var patentApplyPieChart = echarts.init(document.getElementById('patentApplyPie'));
    var inventPatentApplyChart = echarts.init(document.getElementById('inventPatentApply'));
    var effectAuthorizedBarChart = echarts.init(document.getElementById('effectAuthorizedBar'));
    var patentApplyBarChart = echarts.init(document.getElementById('patentApplyBar'));

    function overview(){
        // var totalWidth=document.body.clientWidth;
        // if(window.parent.lastClickSysName&&window.parent.lastClickSysName=='首页'&&(!window.parent.lastMenuId||window.parent.lastMenuId=='')) {
        //     totalWidth=document.body.clientWidth-220;
        // }
        var width=(1679-3*8)/24 -8;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 480],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 24,
            widget_margins: [8, 8],
            draggable : {
                handle : 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        gridster.disable();
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

</script>
</body>
</html>
