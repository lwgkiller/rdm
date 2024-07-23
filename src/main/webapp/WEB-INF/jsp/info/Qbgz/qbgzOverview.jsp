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

	<script src="${ctxPath}/scripts/qbgz/qbgzDeptProvide.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/qbgz/qbgzType.js?version=${static_res_version}" type="text/javascript"></script>
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
			<li class="gs-w" data-col="1" data-row="1" data-sizex="1" data-sizey="1">
				<header>
					<p>情报报告提供数量总览</p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="previewTimeFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="previewTimeTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="deptProvideChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="2" data-row="1" data-sizex="1" data-sizey="1">
				<header>
					<p>情报报告各公司数量</p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="previewTimeFrom2" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryTypeChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="previewTimeTo2" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryTypeChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="typeChart" class="containerBox">
				</div>
			</li>
		</ul>
	</div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var typeChart = echarts.init(document.getElementById('typeChart'));
    var deptProvideChart = echarts.init(document.getElementById('deptProvideChart'));

    function initTime() {
        var nowDate=new Date();
        var nowMonthEnd = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, 0);
        var endStr=nowMonthEnd.getFullYear() + "-" + (nowMonthEnd.getMonth()+1) +'-'+ nowMonthEnd.getDate();
        var begingMonthStart = new Date(nowDate.getFullYear(), nowDate.getMonth()-1, 1);
        var startStr = begingMonthStart.getFullYear() + "-" + (begingMonthStart.getMonth()+1) + "-" + begingMonthStart.getDate();
		return {startStr:startStr,endStr:endStr};
    }

    function overview(){
        var totalWidth=document.body.clientWidth;
        if(window.parent.lastClickSysName&&window.parent.lastClickSysName=='首页'&&(!window.parent.lastMenuId||window.parent.lastMenuId=='')) {
            totalWidth=document.body.clientWidth-220;
        }
        var width=(totalWidth-3*8)/2;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 360],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [8, 8],
            draggable : {
                handle : 'header'
            },
            avoid_overlapped_widgets: false
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

</script>
</body>
</html>
