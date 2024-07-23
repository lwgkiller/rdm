<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<%@include file="/commons/dynamic.jspf" %>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>员工对照图</title>
	<link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
	<!--[if lte IE 8]>
	<script src="js/html5shiv.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
	<![endif]-->
	<script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
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

	<script src="${ctxPath}/scripts/portrait/compare/compareRadarChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/compare/compareBarChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/portrait/compare/compareYearLineChart.js?version=${static_res_version}" type="text/javascript"></script>

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
<div class="personalPort">
	<div class="gridster">
		<ul>
			<li class="gs-w" data-col="1" data-row="1" data-sizex="2" data-sizey="3">
				<header>
					<p>雷达图</p>
				</header>
				<div id="radarChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="3" data-row="1" data-sizex="4" data-sizey="3">
				<header>
					<p>子项得分对比表</p>
				</header>
				<div id="ringChart" class="containerBox">
					<div id="compareScoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
						 allowResize="false"
						 url="${ctxPath}/portrait/document/compareScore.do?userId1=${userId1}&&userId2=${userId2}&&reportYear=${reportYear}"
						  showPager="false" idField="userId">
						<div property="columns">
							<div field="userName" width="50" headerAlign="center" align='center' >姓名</div>
							<div header="技术创新" style="text-align: center">
								<div property="columns">
									<div field="standardScore" headerAlign="center" align='center' width="60">标准维护</div>
									<div field="knowledgeScore" headerAlign="center" align='center' width="60" >知识产权</div>
									<div field="projectScore" headerAlign="center" align='center' width="60" >科技项目</div>
									<div field="rewardScore" headerAlign="center" align='center' width="60" >荣誉奖项</div>
									<div field="secretScore" headerAlign="center" align='center' width="60" >专有技术</div>
									<div field="technologyScore" headerAlign="center" align='center' width="60" >技术数据库</div>
								</div>
							</div>
							<div header="技术协同">
								<div property="columns">
									<div field="bbsScore" headerAlign="center" align='center' width="60" >RDM论坛</div>
									<div field="patentReadScore" headerAlign="center" align='center' width="50" >专利解读</div>
									<div field="informationScore" headerAlign="center" align='center' width="50" >情报报告</div>
									<div field="analysisImproveScore" headerAlign="center" align='center' width="50" >分析改进</div>
									<div field="contractScore" headerAlign="center" align='center' width="50" >合同管理</div>
								</div>
							</div>
							<div header="敬业表现">
								<div property="columns">
									<div field="attendanceScore" headerAlign="center" align='center' width="50" >考勤</div>
									<div field="notificationScore" headerAlign="center" align='center' width="50" >通报</div>
									<div field="performanceScore" headerAlign="center" align='center' width="50" >月度绩效</div>
								</div>
							</div>
							<div header="职业发展" style="text-align: center">
								<div property="columns">
									<div field="courseScore"  headerAlign="center" align='center'width="60">培训</div>
									<div field="cultureScore" width="60" headerAlign="center" align='center' >培养</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="4" data-sizex="6" data-sizey="3">
				<header>
					<p>分类柱状图</p>
				</header>
				<div id="barChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="7" data-sizex="6" data-sizey="3">
				<header>
					<p>年度趋势对比图</p>
				</header>
				<div id="lineYearChart" class="containerBox">
				</div>
			</li>
		</ul>
	</div>
</div>
<style type="text/css">
	.mini-grid-headerCell-nowrap{
		text-align: center;
	}
	.mini-grid-headerCell{
		border: #A5ACB5 solid 1px;
		height: 60px;
	}
	.mini-grid-cell{
		border: #A5ACB5 solid 1px;
		height: 60px;
	}
</style>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var lastMonth="${lastMonth}";
    var userId1 = "${userId1}";
	var userId2 = "${userId2}";
    var reportYear = "${reportYear}";
	var radarChart = echarts.init(document.getElementById('radarChart'));
	var barChart = echarts.init(document.getElementById('barChart'));
    var lineYearChart = echarts.init(document.getElementById('lineYearChart'));
	var compareScoreListGrid = mini.get("compareScoreListGrid");
	compareScoreListGrid.load();
    function overview(){
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [5, 5],
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
