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
    <script src="${ctxPath}/scripts/rdmZhgl/report/ndkfjh_zrszwcl.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/ndkfjh_gsjhwcl.js?version=${static_res_version}"
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
            <li class="gs-w" data-col="1" data-row="1" data-sizex="6" data-sizey="3">
                <header>
                    <p>责任所长完成率</p>
                    <span style="float: center;	font-size: 13px;color: #333;vertical-align: middle">
                        统计年月：
					<input id="yearMonth" allowinput="false" onvaluechanged="reloadData" class="mini-monthpicker"
                           style="width:100px" name="yearMonth"/>
					</span>
                </header>
                <div id="zrsz" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="4" data-sizex="6" data-sizey="3">
                <header>
                    <p>各所计划完成率</p>
                </header>
                <div id="gsjh" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="7" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header">未完成项简况</p>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  500px;" sortField="UPDATE_TIME_"
                         sortOrder="desc"
                         url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listProcess.do?yearMonth=${applyObj.yearMonth}" idField="id"
                         showPager="false" allowCellWrap="false"
                         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15"
                         allowAlternating="true" autoload="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                            <div field="productName" width="100px" headerAlign="center" align="center" renderer="showDetail"
                                 allowSort="false">计划名称
                            </div>
                            <div field="delayDays" width="80px" headerAlign="center" align="center" allowSort="false">延期天数</div>
                            <div field="remark" width="150px" headerAlign="center" align="center" allowSort="false">延期原因</div>
                            <div field="chargerManName" width="100px" headerAlign="center" align="center" allowSort="false">负责人
                            </div>
                            <div field="responsorName" width="100px" headerAlign="center" align="center" allowSort="false">责任所长
                            </div>
                        </div>
                    </div>
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
    var zrszChart = echarts.init(document.getElementById('zrsz'));
    var gsjhChart = echarts.init(document.getElementById('gsjh'));
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    var userName = "${userName}";
    $(function () {
        var year = new Date().getFullYear();
        var month = new Date().getMonth();
        month = month + 1;
        var nowDate = year + "-" + month;
        mini.get('yearMonth').setValue(nowDate);
        loadListGrid();
        initZrszChart();
        initGsjhChart();
    })
    function reloadData() {
        loadListGrid();
        initZrszChart();
        initGsjhChart();
    }
    function loadListGrid() {
        var yearMonth = mini.get('yearMonth').getText();
        var url=jsUseCtxPath+"/rdmZhgl/core/ndkfjh/planDetail/listProcess.do?yearMonth="+yearMonth;
        listGrid.setUrl(url);
        listGrid.load();
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

    zrszChart.on('click', function (params) {
        if(!permission&&params.name!=userName){
            return;
        }
        var yearMonth = '';
        var barType = '';
        if (mini.get("yearMonth").getValue()) {
            yearMonth = mini.get("yearMonth").getText();
        }
        var  seriesName = params.seriesName;
        if(seriesName=='计划'){
            barType = 'all';
        }else{
            barType = 'unFinish';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/plan/reportViewPage.do?barType="+barType+"&userName=" + params.name + "&yearMonth=" + yearMonth ;
        window.open(url);
    })
    gsjhChart.on('click', function (params) {
        if(!permission){
            return;
        }
        var yearMonth = '';
        var barType = '';
        if (mini.get("yearMonth").getValue()) {
            yearMonth = mini.get("yearMonth").getText();
        }
        var  seriesName = params.seriesName;
        if(seriesName=='计划'){
            barType = 'all';
        }else{
            barType = 'unFinish';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/plan/reportViewPage.do?barType="+barType+"&deptName=" + params.name + "&yearMonth=" + yearMonth ;
        window.open(url);
    })

    function showDetail(e) {
        var s = '';
        var record = e.record;
        var detailId = record.detailId;
        var productName = record.productName;
        var chargerManName = record.chargerManName;
        var responsorName = record.responsorName;
        if(!permission&&chargerManName!=userName&&responsorName!=userName){
            s = '<span  >' + productName + '</span>';
        }else{
            s = '<span   style="cursor: pointer;color: #0a7ac6" onclick="openDetail(\'' + detailId + '\')">' + productName + '</span>';
        }
        return s;
    }
    function openDetail(detailId) {
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/planDetail/planViewPage.do?detailId=" + detailId ;
        window.open(url);
    }

</script>
</body>
</html>
