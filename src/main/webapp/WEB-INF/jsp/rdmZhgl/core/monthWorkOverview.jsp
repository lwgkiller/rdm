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
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
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
    <script src="${ctxPath}/scripts/rdmZhgl/monthWork_wcqk.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/monthWork_jhtj.js?version=${static_res_version}"
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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.monthWorkOverview.name" /></p>
                    <span style="font-size: 13px;color: #333;vertical-align: middle;float:center">
						<spring:message code="page.monthWorkOverview.name1" /><input id="yearMonthStart" allowinput="false" onvaluechanged="reloadPlan"
                                   class="mini-monthpicker"
                                   style="width:100px" name="yearMonthStart"/>
                        <spring:message code="page.monthWorkOverview.name2" /><input id="yearMonthEnd" allowinput="false" onvaluechanged="reloadPlan"
                                class="mini-monthpicker"
                                style="width:100px" name="yearMonthEnd"/>
						<spring:message code="page.monthWorkOverview.name3" />
					</span>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="planGrid" class="mini-datagrid" allowResize="true" style="height: 100%;width: 100%"
                         url="${ctxPath}/rdmZhgl/core/monthWork/reportMonthPlan.do" idField="id" showPager="false"
                         allowCellWrap="true" showColumnsMenu="false"
                         allowAlternating="true"
                         allowHeaderWrap="true" onlyCheckSelection="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkOverview.name4" /></div>
                            <div field="projectName" name="projectName" width="150" headerAlign="center" align="left">
                                <spring:message code="page.monthWorkOverview.name5" />
                            </div>
                            <div field="responseMan" name="responseMan" width="50" headerAlign="center" align="left">
                                <spring:message code="page.monthWorkOverview.name6" />
                            </div>
                            <div field="workContent" name="workContent" width="150" headerAlign="center" align="left">
                                <spring:message code="page.monthWorkOverview.name7" />
                            </div>
                            <div field="finishFlag" name="finishFlag" width="150" headerAlign="center" align="left">
                                <spring:message code="page.monthWorkOverview.name8" />
                            </div>
                            <div field="finishStatusText" name="finishStatusText" width="50" headerAlign="center"
                                 align="center">
                                <spring:message code="page.monthWorkOverview.name9" />
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="4" data-sizex="6" data-sizey="3">
                <header>
                    <p><spring:message code="page.monthWorkOverview.name10" /></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
					<input id="yearMonthStart1" allowinput="false" onvaluechanged="reloadWcqk" class="mini-monthpicker"
                           style="width:100px" name="yearMonthStart"/>
                        <spring:message code="page.monthWorkOverview.name2" />
                        <input id="yearMonthEnd1" allowinput="false" onvaluechanged="reloadWcqk"
                               class="mini-monthpicker"
                               style="width:100px" name="yearMonthEnd"/>
					</span>
                </header>
                <div id="wcqk" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="7" data-sizex="6" data-sizey="3">
                <header>
                    <p><spring:message code="page.monthWorkOverview.name11" /></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
					<input id="yearMonthStart2" allowinput="false" onvaluechanged="reloadJhtj" class="mini-monthpicker"
                           style="width:100px" name="yearMonthStart"/>
                        <spring:message code="page.monthWorkOverview.name2" />
                        	<input id="yearMonthEnd2" allowinput="false" onvaluechanged="reloadJhtj"
                                   class="mini-monthpicker"
                                   style="width:100px" name="yearMonthEnd"/>
					</span>
                </header>
                <div id="jhtj" class="containerBox">
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
    var wcqkChart = echarts.init(document.getElementById('wcqk'));
    var jhtjChart = echarts.init(document.getElementById('jhtj'));
    var planGrid = mini.get("planGrid");
    $(function () {
        var year = new Date().getFullYear();
        var month = new Date().getMonth();
        month = month + 1;
        var nowDate = year + "-" + month;
        mini.get('yearMonthStart').setValue(nowDate);
        mini.get('yearMonthEnd').setValue(nowDate);
        mini.get('yearMonthStart1').setValue(nowDate);
        mini.get('yearMonthEnd1').setValue(nowDate);
        mini.get('yearMonthStart2').setValue(nowDate);
        mini.get('yearMonthEnd2').setValue(nowDate);
        loadPlanGrid();
        initJhtjChart();
        initWcqkChart();
    })
    planGrid.on("load", function () {
        planGrid.mergeColumns(["rowNum", "projectName", "responseMan", "finishStatusText"]);
    });

    function loadPlanGrid() {
        var paramArray = [{name: "yearMonthStart", value: mini.get('yearMonthStart').getText()}
            , {name: "yearMonthEnd", value: mini.get('yearMonthEnd').getText()}];
        var data = {};
        data.filter = mini.encode(paramArray);
        planGrid.load(data);
    }

    function reloadPlan() {
        loadPlanGrid();
    }

    function reloadWcqk() {
        initWcqkChart();
    }

    function reloadJhtj() {
        initJhtjChart();
    }

    function overview() {
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

    wcqkChart.on('click', function (params) {
        var yearMonthStart = '';
        var yearMonthEnd = '';
        var finishStatus = '';
        if (mini.get("yearMonthStart1").getValue()) {
            yearMonthStart = mini.get("yearMonthStart1").getText();
        }
        if (mini.get("yearMonthEnd1").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd1").getText();
        }
        var  seriesName = params.seriesName;
        if(seriesName=='未完成'){
            finishStatus = '1';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/monthWork/getUnFinishPage.do?finishStatus="+finishStatus+"&deptName=" + params.name + "&yearMonthStart=" + yearMonthStart + "&yearMonthEnd=" + yearMonthEnd;
        window.open(url);
    })

</script>
</body>
</html>
