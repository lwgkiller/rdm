<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<%@include file="/commons/dynamic.jspf" %>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>员工画像</title>
	<link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
	<!--[if lte IE 8]>
	<script src="js/html5shiv.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
	<![endif]-->
	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
	<script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
	<script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
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

	<script src="${ctxPath}/scripts/portrait/document/portraitPieChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/document/portraitRingChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/document/portraitLineChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/document/portraitYearLineChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/document/portraitTableChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/document/portraitBarChart.js?version=${static_res_version}" type="text/javascript"></script>
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
		.mini-buttonedit-input
		{
			font-size: 13px;
		}
		.jfzb-jb{
			width:43px;
			float: left;
			text-align: left;
			color: #333
		}
		.jfzb-fs{
			width:44px;
			float: left;
			text-align: center;
			color: #333
		}
		.jfzb-zb{
			width:51px;
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
			<li class="gs-w" data-col="1" data-row="1" data-sizex="3" data-sizey="3">
				<header>
					<p>分项得分占比</p>
				</header>
				<div id="pieChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="4" data-row="1" data-sizex="3" data-sizey="3">
				<header>
					<p>子项分类图</p>
				</header>
				<div id="ringChart" class="containerBox">
				</div>
			</li>

			<li class="gs-w" data-col="1" data-row="4" data-sizex="3" data-sizey="3">
				<header>
                    <p>柱状图</p>
				</header>
				<div id="barChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="4" data-row="4" data-sizex="3" data-sizey="3">
				<header>
					<p>分类积分</p>
				</header>
				<div id="tableChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="7" data-sizex="6" data-sizey="3">
				<header>
					<p>年度分类得分趋势图</p>
				</header>
				<div id="lineChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="10" data-sizex="6" data-sizey="3">
				<header>
					<p>年度总得分趋势图</p>
				</header>
				<div id="lineYearChart" class="containerBox">
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
    var cgjhFrom="${cgjhFrom}";
    var cgjhTo="${cgjhTo}";
    var userId = "${userId}";
    var reportYear = "${reportYear}";
    var pieChart = echarts.init(document.getElementById('pieChart'));
	var ringChart = echarts.init(document.getElementById('ringChart'));
	var lineChart = echarts.init(document.getElementById('lineChart'));
	var lineYearChart = echarts.init(document.getElementById('lineYearChart'));
	var tableChart = echarts.init(document.getElementById('tableChart'));
	var barChart = echarts.init(document.getElementById('barChart'));


    function overview(){
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [6, 6],
            draggable : {
                handle : 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

</script>
</body>
</html>
