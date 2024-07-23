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
	<script src="${ctxPath}/scripts/rdmZhgl/xpsz_jdzt.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/xpsz_jdtj.js?version=${static_res_version}" type="text/javascript"></script>
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
					<p>进度状态</p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						年度
						 <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
								style="width: 105px;font-size: 10px;height: 30px" label="年度："
								length="50"  onvaluechanged="initJdztChart()"
								only_read="false" required="true" allowinput="false" mwidth="100"
								wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
								textField="text" valueField="value" emptyText="请选择..."
								url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
								nullitemtext="请选择..." emptytext="请选择..."/>
					</span>
				</header>
				<div id="jdzt" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="4" data-sizex="3" data-sizey="3">
				<header>
					<p>试制进度统计</p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						从
						 <input id="reportStartDate" name="reportStartDate"  onvaluechanged="initJdtjChart()" class="mini-datepicker"  format="yyyy-MM-dd" style="font-size:14pt;width:105px;"/>
						至
						 <input id="reportEndDate" name="reportEndDate" class="mini-datepicker"  onvaluechanged="initJdtjChart()"  format="yyyy-MM-dd" style="font-size:14pt;width:105px;"/>
					</span>
				</header>
				<div id="jdtj" class="containerBox">
				</div>
			</li>

			<li class="gs-w" data-row="4" data-col="1" data-sizex="6" data-sizey="4">
				<header class="deskHome-header">
					<p class="span-header">重点产品当前阶段
						<a class="mini-button" plain="true" onclick="exportBtn()">导出</a></p>
					<span style="float: left;margin-left: 10px">全部产品</span>
					<div id="sendSupplier" name="sendSupplier" class="mini-checkbox" onclick="showAll()"  style="float: left;margin-left: 10px;cursor: pointer"></div>
				</header>
				<div id="deskHomeProgress" class="containerBox">
					<div id="productGrid" class="mini-datagrid" allowResize="true" style="height: 100%;width: 100%"
						 url="${ctxPath}/rdmZhgl/core/product/baseList.do" idField="id" showPager="false" allowCellWrap="true"
						 allowCellEdit="true" allowCellSelect="true"
						 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
						 oncellendedit="cellendedit" allowHeaderWrap="true"  onlyCheckSelection="true"
						 pagerButtons="#pagerButtons">
						<div property="columns">
							<div type="indexcolumn"   align="center" headerAlign="center" width="50">序号</div>
							<div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
							<div field="productType" name="productType" width="150" headerAlign="center" align="center">产品类型</div>
							<div field="productModel" name="productModel" width="150" headerAlign="center" align="center">产品型号</div>
							<div field="productLeaderName" name="productLeaderName" width="150" headerAlign="center" align="center">产品主管</div>
							<div field="processInfo" name="processInfo" width="150" headerAlign="center" align="center">当前阶段</div>
							<div field="plan_date"  width="150" headerAlign="center" align="center">计划完成日期</div>
							<div field="processStatus" name="processStatus" width="150" headerAlign="center" align="center" renderer="onProcessStatus">进度状态</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
</div>
<form id="excelForm" action="${ctxPath}/rdmZhgl/core/product/exportStageExcel.do" method="post"
	  target="excelIFrame">
	<input type="hidden" name="pageIndex"/>
	<input type="hidden" name="pageSize"/>
	<input type="hidden" id="filter" name="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var lastMonth="${lastMonth}";
    var jdztChart = echarts.init(document.getElementById('jdzt'));
    var jdtjChart = echarts.init(document.getElementById('jdtj'));
	var productGrid = mini.get("productGrid");
	$(function () {
		loadProductGrid("false");
	})
	function loadProductGrid(_showAll) {
		var paramArray = [{name: "finished", value: '0'},{name: "showAll", value: _showAll}];
		var data = {};
		data.filter = mini.encode(paramArray);
		productGrid.load(data);
	}
	function showAll() {
		var flag = mini.get('sendSupplier').getValue();
		loadProductGrid(flag);
	}
	function onActionRenderer(e) {
		var record = e.record;
		var id = record.id;
		var s = '<span  title="详情" style="cursor: pointer;color:green" onclick="viewForm(\'' + id + '\')">详情</span>';
		return s;
	}
	//修改
	function viewForm(id) {
		var url = jsUseCtxPath + "/rdmZhgl/core/xpszApply/detail.do?id=" + id;
		window.open(url);
	}
	function onProcessStatus(e) {
		var processStatus = e.record.processStatus;
		var _html = '';
		if(processStatus=='1'){
			_html = '<span style="color: green">正常</span>'
		}else if(processStatus=='2'){
			_html = '<span style="color: #d9d90a">轻微落后</span>'
		}else if(processStatus=='3'){
			_html = '<span style="color: red">严重滞后</span>'
		}
		return _html;
	}
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
	function exportBtn(){
		var flag = mini.get('sendSupplier').getValue();
		var params = [{name: "finished", value: '0'},{name: "showAll", value: flag}];
		$("#filter").val(mini.encode(params));
		var excelForm = $("#excelForm");
		excelForm.submit();
	}
	jdztChart.on('click', function (params) {
		var reportYear = '';
		if (mini.get("reportYear").getValue()) {
			reportYear = mini.get("reportYear").getText();
		}
		var  seriesName = params.seriesName;
		var barName = params.name;
		var important = '';
		if(barName=='重点产品'){
			important = '3';
		}
		if(barName=='全部产品'){
			important = '';
		}
		if(seriesName=='正常'){
			processStatus = '1';
		}else if(seriesName=='轻微落后'){
			processStatus = '2';
		}else if(seriesName=='严重滞后'){
			processStatus = '3';
		}
		var url = jsUseCtxPath + "/rdmZhgl/core/product/getListPage.do?processStatus="+processStatus+"&important=" + important + "&belongYear=" + reportYear ;
		window.open(url);
	})
    jdtjChart.on('click', function (params) {
        var dateStart = '';
        var dateEnd = '';
        if (mini.get("reportStartDate").getValue()) {
            dateStart = mini.get("reportStartDate").getText();
        }
        if (mini.get("reportEndDate").getValue()) {
            dateEnd = mini.get("reportEndDate").getText();
        }
        var  seriesName = params.seriesName;
        var barName = params.name;
        var important = '';
        var reportType = '';
        if(seriesName=='重点产品'){
			important = '3';
        }
        if(seriesName=='全部产品'){
			important = '';
        }
        if(barName=='图纸归档'){
            reportType = 'tzgd_date';
        }else if(barName=='试制通知'){
            reportType = 'fsztzd_date';
        }else if(barName=='样机完成'){
            reportType = 'yjwczcs_date';
        }else if(barName=='工业考核'){
            reportType = 'gykhkssj_date';
        }else if(barName=='小批量'){
            reportType = 'xplwcsj_date';
        }else if(barName=='上市'){
            reportType = 'ssrq_date';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/product/getListPage.do?important="+important+"&reportType=" + reportType + "&dateStart=" + dateStart+ "&dateEnd=" + dateEnd ;
        window.open(url);
    })
</script>
</body>
</html>
