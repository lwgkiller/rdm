<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echartsVersion47/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/mini/boot.js?static_res_version=${static_res_version}"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>

    <script src="${ctxPath}/scripts/xcmgProjectManager/projectReport_lbtj.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectReport_jfzb.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectReport_ydjf.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectReport_ywph.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectReport_cgjh.js?version=${static_res_version}"
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

        .mini-buttonedit-input {
            font-size: 13px;
        }

        .jfzb-jb {
            width: 43px;
            float: left;
            text-align: left;
            color: #333
        }

        .jfzb-fs {
            width: 44px;
            float: left;
            text-align: center;
            color: #333
        }

        .jfzb-zb {
            width: 51px;
            float: left;
            text-align: right;
            color: #333
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-col="1" data-row="1" data-sizex="3" data-sizey="4">
                <header>
                    <p><spring:message code="page.xcmgProjectOverview.xmlbtj"/></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<spring:message code="page.xcmgProjectOverview.xmlrc"/>
						<input id="lbtjBuildFrom" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="lbtjBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						<spring:message code="page.xcmgProjectOverview.z"/>
						<input id="lbtjBuildTo" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="lbtjBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						<%--&nbsp;&nbsp;结项
						<input id="lbtjKnotFrom" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px" onvaluechanged="lbtjKnotFromChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="lbtjKnotTo" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px" onvaluechanged="lbtjKnotToChanged()" onfocus="this.blur()" class="mini-datepicker" value=""/>--%>
					</span>
                </header>
                <div id="lbtj" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="5" data-sizex="3" data-sizey="4">
                <header>
                    <p><spring:message code="page.xcmgProjectOverview.bmywxmzb"/></p>
                </header>
                <div id="ywph" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="4" data-row="1" data-sizex="3" data-sizey="4">
                <header>
                    <p><spring:message code="page.xcmgProjectOverview.bmxmbzfzb"/></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<spring:message code="page.xcmgProjectOverview.xmlrc"/>
						<input id="jfzbBuildFrom" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="jfzbBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						<spring:message code="page.xcmgProjectOverview.z"/>
						<input id="jfzbBuildTo" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="jfzbBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="jfzb" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="4" data-row="5" data-sizex="3" data-sizey="4">
                <header>
                    <p><spring:message code="page.xcmgProjectOverview.bmydjf"/></p>
                    <input id="ydjfMonth" style="float: right;width: 100px" onfocus="this.blur()"
                           class="mini-monthpicker" onvaluechanged="changeTime()" value=""/>
                </header>
                <div id="ydjf" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="10" data-sizex="6" data-sizey="4">
                <header>
                    <p><spring:message code="page.xcmgProjectOverview.xmcgjh"/></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<spring:message code="page.xcmgProjectOverview.yjccsj"/>
						<input id="cgjhFrom" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="cgjhTimeChange()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						<spring:message code="page.xcmgProjectOverview.z"/>
						<input id="cgjhTo" format="yyyy-MM-dd" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="cgjhTimeChange()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="cgjh" class="containerBox">
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
    //项目类别统计
    var lbtjChart = echarts.init(document.getElementById('lbtj'));
    //部门标准分占比
    var jfzbChart = echarts.init(document.getElementById('jfzb'));
    //部门月度积分
    var ydjfChart = echarts.init(document.getElementById('ydjf'));
    //延误项目占比
    var ywphChart = echarts.init(document.getElementById('ywph'));
    //项目成果计划
    var cgjhChart = echarts.init(document.getElementById('cgjh'));

    function overview() {
        var totalWidth = document.body.clientWidth;
        if (window.parent.lastClickSysName && window.parent.lastClickSysName == '首页' && (!window.parent.lastMenuId || window.parent.lastMenuId == '')) {
            totalWidth = document.body.clientWidth - 220;
        }
        var width = (totalWidth - 3 * 8) / 2;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

    ywphChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (ywphChart.containPixel('grid', pointInPixel)) {
            var yIndex = ywphChart.convertFromPixel({seriesIndex: 0}, [params.offsetX, params.offsetY])[1];
            var option = ywphChart.getOption();
            var clickDeptName = option.yAxis[0].data[yIndex];
            var url = jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/progressReport.do?1=1&clickDeptName=" + clickDeptName;
            window.open(url);
        }
    });

    lbtjChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (lbtjChart.containPixel('grid', pointInPixel)) {
            var xIndex = lbtjChart.convertFromPixel({seriesIndex: 0}, [params.offsetX, params.offsetY])[0];
            var option = lbtjChart.getOption();
            var categoryName = option.xAxis[0].data[xIndex];
            var startTime = mini.get("lbtjBuildFrom").getText();
            var endTime = mini.get("lbtjBuildTo").getText();
            var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/list.do?pointCategoryName=" + categoryName;
            if (startTime) {
                url += "&projectStartTime=" + startTime;
            }
            if (endTime) {
                url += "&projectEndTime=" + endTime;
            }
            window.open(url);
        }
    });

    jfzbChart.on('click', function (param) {
        var deptName = param.name;
        if (!deptName) {
            return;
        }
        var jfzbBuildFrom = mini.get("jfzbBuildFrom").getText();
        var jfzbBuildTo = mini.get("jfzbBuildTo").getText();
        var url = jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/evaluateScorePage.do?pointDeptName=" + deptName;
        if (jfzbBuildFrom) {
            url += "&projectStartTime=" + jfzbBuildFrom;
        }
        if (jfzbBuildTo) {
            url += "&projectEndTime=" + jfzbBuildTo;
        }
        window.open(url);
    });

    ydjfChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (ydjfChart.containPixel('grid', pointInPixel)) {
            var yIndex = ydjfChart.convertFromPixel({seriesIndex: 0}, [params.offsetX, params.offsetY])[1];
            var option = ydjfChart.getOption();
            var clickDeptName = option.yAxis[0].data[yIndex];
            var url = jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/deptScore.do?1=1&clickDeptName=" + clickDeptName;
            var ydjfMonth = mini.get("ydjfMonth").getText();
            if (ydjfMonth) {
                url += "&ydjfMonth=" + ydjfMonth;
            }
            window.open(url);
        }
    });

</script>
</body>
</html>
