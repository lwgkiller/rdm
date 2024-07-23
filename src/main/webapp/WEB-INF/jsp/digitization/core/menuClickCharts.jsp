<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<%@include file="/commons/dynamic.jspf" %>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>菜单使用量</title>
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
	<script src="${ctxPath}/scripts/digitization/menuClickDeptChart.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/digitization/menuClickMonthChart.js?version=${static_res_version}" type="text/javascript"></script>
	<style>
		.clicked{
			background:#ebebeb !important;
			border-color:#adadad !important;
			box-shadow: inset 0 3px 5px rgba(0, 0, 0, .125);
		}
		.clicked .mini-button-text{
			color:#333333 !important;
		}
		.gridster ul li header {
			background: none repeat scroll 0% 0% #f5f7fa;
			display: block;
			font-size: 20px;
			line-height: normal;
			padding: 0px 0px 6px;
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
			<li class="gs-w" data-col="24" data-row="1" data-sizex="24" data-sizey="1">
				<header>
                    <span style="float: left;font-size: 13px;color: #333;vertical-align: middle">
                    <a class="mini-button" id="deptSum" style="margin-right: 5px"  plain="true" onclick="queryDeptChart()">部门汇总</a>
					<a class="mini-button" id="monthSum" style="margin-right: 5px"  plain="true" onclick="queryMonthChart()">月份汇总</a>
                    </span>
				</header>
				<div id="clickCountChart" class="containerBox">
				</div>
			</li>

		</ul>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var menuId="${menuId}";
    var clickCountChart = echarts.init(document.getElementById('clickCountChart'));
    queryDeptChart();

    function overview() {
        var width = 1200;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 520],
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
</script>
</body>
</html>
