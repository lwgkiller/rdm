<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>送货总览报表</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
	<script src="js/html5shiv.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
	<![endif]-->
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
    <%--	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/echarts-wordcloud.min.js"></script>--%>
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
    <script src="${ctxPath}/scripts/rdmZhgl/report/bbsTypeChart.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/bbsDataChart.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/bbsPostNumChart.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/bbsGjtaNumChart.js?version=${static_res_version}"
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
            <li class="gs-w" data-col="1" data-row="1" data-sizex="2" data-sizey="3">
                <header>
                    <p>帖子分类统计</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						统计时间从
						<input id="bbsType_startTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initBbsTypeChart()" class="mini-datepicker"
                               value=""/>
						至
						<input id="bbsType_endTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initBbsTypeChart()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="bbsTypeChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="3" data-row="1" data-sizex="4" data-sizey="3">
                <header>
                    <p>改进提案关键节点</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						统计时间从
						<input id="data_startTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initBbsDataChart()" class="mini-datepicker"
                               value=""/>
						至
						<input id="data_endTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initBbsDataChart()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="bbsDataChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="4" data-sizex="3" data-sizey="4">
                <header>
                    <p>发帖数/人（TOP10）</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						统计时间从
						<input id="post_startTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initPostRankChart()" class="mini-datepicker"
                               value=""/>
						至
						<input id="post_sendTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initPostRankChart()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="bbsPostChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="4" data-row="4" data-sizex="3" data-sizey="4">
                <header>
                    <p>改进提案类发帖数/人（TOP10）</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						统计时间从
						<input id="gjta_startTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initGjtaRankChart()" class="mini-datepicker"
                               value=""/>
						至
						<input id="gjta_endTime" format="yyyy-MM-dd"
                               style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="initGjtaRankChart()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="bbsGjtaChart" class="containerBox">
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
    var cgjhFrom = "${cgjhFrom}";
    var cgjhTo = "${cgjhTo}";
    var userId = "${userId}";
    var reportYear = "${reportYear}";
    var bbsTypeChart = echarts.init(document.getElementById('bbsTypeChart'));
    var bbsDataChart = echarts.init(document.getElementById('bbsDataChart'));
    var bbsPostChart = echarts.init(document.getElementById('bbsPostChart'));
    var bbsGjtaChart = echarts.init(document.getElementById('bbsGjtaChart'));
    /**
     * 帖子分类统计
     * */
    bbsTypeChart.on('click', function (params) {
        if (params.componentIndex == 0) {
            var bbs_startTime = '';
            var bbs_endTime = '';
            if (mini.get("bbsType_startTime").getValue()) {
                bbs_startTime = mini.get("bbsType_startTime").getText();
            }
            if (mini.get("bbsType_endTime").getValue()) {
                bbs_endTime = mini.get("bbsType_endTime").getText();
            }
            var url = jsUseCtxPath + "/rdmZhgl/core/bbs/report/listPage.do?reportType=bbsTypeReport&barName=" + params.name + "&bbs_startTime=" + bbs_startTime + "&bbs_endTime=" + bbs_endTime;
            window.open(url);
        }
    })
    /**
     * 改进提案关键节点
     * */
    bbsDataChart.on('click', function (params) {
        if (params.componentIndex == 0) {
            var bbs_startTime = '';
            var bbs_endTime = '';
            if (mini.get("data_startTime").getValue()) {
                bbs_startTime = mini.get("data_startTime").getText();
            }
            if (mini.get("data_endTime").getValue()) {
                bbs_endTime = mini.get("data_endTime").getText();
            }
            var url = jsUseCtxPath + "/rdmZhgl/core/bbs/report/listPage.do?reportType=bbsData&barName=" + params.name + "&bbs_startTime=" + bbs_startTime + "&bbs_endTime=" + bbs_endTime;
            window.open(url);
        }
    })
    /**
     * 发帖数/人（TOP10）
     * */
    bbsPostChart.on('click', function (params) {
        var bbs_startTime = '';
        var bbs_endTime = '';
        if (mini.get("post_startTime").getValue()) {
            bbs_startTime = mini.get("post_startTime").getText();
        }
        if (mini.get("post_sendTime").getValue()) {
            bbs_endTime = mini.get("post_sendTime").getText();
        }
        var seriesName = params.seriesName;
        var url = jsUseCtxPath + "/rdmZhgl/core/bbs/report/listPage.do?reportType=postRank&barName=" + params.name + "&bbs_startTime=" + bbs_startTime + "&bbs_endTime=" + bbs_endTime + "&seriesName=" + seriesName;
        window.open(url);
    })
    /**
     * 改进提案类发帖数/人（TOP10）
     * */
    bbsGjtaChart.on('click', function (params) {
        if (params.componentIndex == 0) {
            var bbs_startTime = '';
            var bbs_endTime = '';
            if (mini.get("gjta_startTime").getValue()) {
                bbs_startTime = mini.get("gjta_startTime").getText();
            }
            if (mini.get("gjta_endTime").getValue()) {
                bbs_endTime = mini.get("gjta_endTime").getText();
            }
            var url = jsUseCtxPath + "/rdmZhgl/core/bbs/report/listPage.do?reportType=gjtaPostRank&barName=" + params.name + "&bbs_startTime=" + bbs_startTime + "&bbs_endTime=" + bbs_endTime;
            window.open(url);
        }
    })


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

</script>
</body>
</html>
