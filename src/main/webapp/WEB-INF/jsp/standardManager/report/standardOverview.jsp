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
	<script src="${ctxPath}/scripts/standardManager/standardOverview.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_publish.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_category.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_preview.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_download.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_depPreview.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_depDownload.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardOverview_fieldList.js?version=${static_res_version}" type="text/javascript"></script>
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
		.item
		{
			float:left;
			width:120px;
			height:110px;
			border:solid 1px #ccc;
			border-radius:4px;
			margin-left:6px;
			margin-top:6px;
		}
		.item-inner
		{
			width:100%;
		}
		.item-image
		{

			height:50px;
			width:70px;
			margin:auto;
			margin-top:8px;
			display:block;
			cursor: pointer;
		}
		.item-text
		{
			text-align:center;
			font-size:10px;
			font-family:"微软雅黑";
			letter-spacing:5px;
			font-weight:bold;
			padding-top:5px;
			cursor: pointer;
		}
	</style>
</head>
<body>
<div style="width: 100%;height: 35px;background-color: white">
	<span style="float: right;	font-size: 15px;color: #333;vertical-align:text-bottom">
	<spring:message code="page.standardOverview.txlb" />:
	<input id="systemCategoryId" class="mini-combobox" style="width:150px;font-size: 15px;"
		   textfield="systemCategoryName" valuefield="systemCategoryId"
		   required="false" allowInput="false" showNullItem="false"
		   onvaluechanged="refreshAllChart()"/>
	</span>
</div>
<div class="personalPort">
	<div class="gridster">
		<ul>
			<li class="gs-w" data-col="1" data-row="1" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.fbtj" /></p>
					<span style="float: right;	font-size: 15px;color: #333;vertical-align: middle">
						<input id="publishTimeFrom" class="mini-combobox" style="width:105px;height: 30px"
								   textField="key" valueField="value" onvaluechanged="queryPublishChart()"
								   required="false" allowInput="false" showNullItem="false"/>
						<spring:message code="page.standardOverview.z" />
						<input id="publishTimeTo" class="mini-combobox" style="width:105px;height: 30px"
							   textField="key" valueField="value" onvaluechanged="queryPublishChart()"
							   required="false" allowInput="false" showNullItem="false"/>
					</span>
				</header>
				<div id="publishTimeChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="2" data-row="1" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.lytj" /></p>
				</header>
				<div id="fieldChart" class="containerBox">
					<div id="fieldListGrid" class="mini-datagrid" style="width:100%;height:100%;" showHeader="false"
						 url="${ctxPath}/standardManager/core/standardField/fieldStandardCountList.do"  idField="fieldId" allowResize="false" showPager="false"
						 viewType="cardview" itemRenderer="itemRenderer" showColumns="false">
						<div property="columns">
						</div>
					</div>
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="2" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.lbtj" /></p>
				</header>
				<div id="categoryChart" class="containerBox">
				</div>
			</li>

			<li class="gs-w" data-col="2" data-row="2" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.ylcs" /></p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="previewTimeFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						<spring:message code="page.standardOverview.z" />
						<input id="previewTimeTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="standardPreviewChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="3" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.xzcs" /></p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="downloadTimeFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDownloadChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						<spring:message code="page.standardOverview.z" />
						<input id="downloadTimeTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDownloadChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="standardDownloadChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="2" data-row="3" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.bzyl" /></p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="depPreviewTimeFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDepPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						<spring:message code="page.standardOverview.z" />
						<input id="depPreviewTimeTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDepPreviewChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="depPreviewChart" class="containerBox">
				</div>
			</li>
			<li class="gs-w" data-col="1" data-row="4" data-sizex="1" data-sizey="1">
				<header>
					<p><spring:message code="page.standardOverview.bzxz" /></p>
					<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<input id="depDownloadTimeFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDepDownloadChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
						至
						<input id="depDownloadTimeTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px" onvaluechanged="queryDepDownloadChart()" onfocus="this.blur()" class="mini-datepicker" value=""/>
					</span>
				</header>
				<div id="depDownloadChart" class="containerBox">
				</div>
			</li>
		</ul>
	</div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath="${ctxPath}";
    var systemCategoryValue="${systemCategoryValue}";
    var publishTimeChart = echarts.init(document.getElementById('publishTimeChart'));
	var categoryChart = echarts.init(document.getElementById('categoryChart'));
	var standardPreviewChart = echarts.init(document.getElementById('standardPreviewChart'));
	var standardDownloadChart = echarts.init(document.getElementById('standardDownloadChart'));
	var depPreviewChart = echarts.init(document.getElementById('depPreviewChart'));
	var depDownloadChart = echarts.init(document.getElementById('depDownloadChart'));
	var fieldListGrid=mini.get("fieldListGrid");


    function refreshAllChart() {
		//刷新发布时间统计
        queryPublishChart();
        //刷新类别统计
        queryCategoryChart();
        //刷新预览排行
        queryPreviewChart();
        //刷新下载排行
        queryDownloadChart();
        //刷新部门预览排行
        queryDepPreviewChart();
        //刷新部门下载排行
        queryDepDownloadChart();
        //刷新专业领域统计
        queryFieldChart();
    }

    function itemRenderer(record, rowIndex, meta, grid) {
        meta.rowCls = "item";
        var html = '<div class="item-inner">'
            + '<img class="item-image" src="${ctxPath}/styles/images/folder.png"/>'
            + '<div class="item-text">' + record.fieldName +'（'+record.fieldStandardNum +'个）</div></div>';
        return html;
    }
    $(fieldListGrid.el).on("click", function (event) {
        var record = fieldListGrid.getRowByEvent(event);
        var systemCategoryId=mini.get("systemCategoryId").getValue();
        var url=jsUseCtxPath + "/standardManager/core/standard/management.do?standardStatus=enable";
        if(systemCategoryId) {
            url+="&systemCategory="+systemCategoryId;
        }
        var fieldId=record.fieldId;
        if(fieldId) {
            url+="&fieldId="+fieldId;
        }
        window.open(url);
    });

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

    publishTimeChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (publishTimeChart.containPixel('grid', pointInPixel)) {
            var xIndex = publishTimeChart.convertFromPixel({ seriesIndex: 0 }, [params.offsetX, params.offsetY])[0];
            var option=publishTimeChart.getOption();
			var clickYear=option.xAxis[0].data[xIndex];
            var publishTimeFrom=clickYear+'-01-01';
            var publishTimeTo=clickYear+'-12-31';
            var systemCategoryId=mini.get("systemCategoryId").getValue();
            var categoryName='企业标准';
            var url=jsUseCtxPath + "/standardManager/core/standard/management.do?1=1";
			if(categoryName) {
				url+="&standardCategoryName="+categoryName;
			}
			if(systemCategoryId) {
				url+="&systemCategory="+systemCategoryId;
			}
			if(publishTimeFrom) {
                url+="&publishTimeFrom="+publishTimeFrom;
			}
            if(publishTimeTo) {
                url+="&publishTimeTo="+publishTimeTo;
            }
            window.open(url);
        }
    });
    categoryChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (categoryChart.containPixel('grid', pointInPixel)) {
            var xIndex = categoryChart.convertFromPixel({ seriesIndex: 0 }, [params.offsetX, params.offsetY])[0];
            var option=categoryChart.getOption();
            var clickCategoryName=option.xAxis[0].data[xIndex];
            var systemCategoryId=mini.get("systemCategoryId").getValue();
            var url=jsUseCtxPath + "/standardManager/core/standard/management.do?standardStatus=enable";
            if(clickCategoryName) {
                url+="&standardCategoryName="+clickCategoryName;
            }
            if(systemCategoryId) {
                url+="&systemCategory="+systemCategoryId;
            }
            window.open(url);
        }
    });
</script>
</body>
</html>
