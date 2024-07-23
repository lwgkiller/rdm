<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>研发将本-图形总览</title>
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
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_delayReport.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_ljjbe.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_ljjbl.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_bmjbxm.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_major.js?version=${static_res_version}"
            type="text/javascript"></script>
<%--    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_quarterReport.js?version=${static_res_version}"--%>
<%--            type="text/javascript"></script>--%>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjb_quaReport.js?version=${static_res_version}"
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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <p> 各所延期率统计</p>
                </header>
                <div id="delayReport" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="3" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <p> 各所降本率统计</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:right">
                    统计年份：
                    <input id="quarterYear" name="quarterYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reload"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                    </span>
                </header>
                <div id="quarterReport" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <p> 挖掘机械研究院全年降本预测与实际降本对比</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:right">
					    统计规则：
                         <input id="reportRule" name="reportRule" class="mini-combobox rxc" plugins="mini-combobox"
                                style="width:200px;height:34px" label="统计规则："
                                length="50" onvaluechanged="reload"
                                only_read="false" required="false" allowinput="false" mwidth="100"
                                wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                textField="text" valueField="key_" emptyText="请选择..."
                                url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-TJGZ"
                                nullitemtext="请选择..." emptytext="请选择..."/>
                       统计年份：
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reload"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div id="ljjbe" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="9" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                </header>
                <div id="ljjbl" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="13" data-col="1" data-sizex="3" data-sizey="4">
                <header class="deskHome-header">
                    <p>降本项目-部门统计</p>
                </header>
                <div id="deptReport" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="13" data-col="4" data-sizex="3" data-sizey="4">
                <header class="deskHome-header">
                    <p>降本项目-专业统计</p>
                </header>
                <div id="majorReport" class="containerBox"></div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var lastMonth = "${lastMonth}";
    var delayChart = echarts.init(document.getElementById('delayReport'));
    var quarterChart = echarts.init(document.getElementById('quarterReport'));
    var ljjbeChart = echarts.init(document.getElementById('ljjbe'));
    var ljjblChart = echarts.init(document.getElementById('ljjbl'));
    var deptReportChart = echarts.init(document.getElementById('deptReport'));
    var majorReport = echarts.init(document.getElementById('majorReport'));
    $(function () {
        var year = new Date().getFullYear();
        mini.get('reportYear').setValue(year);
        mini.get('quarterYear').setValue(year);
        mini.get('reportRule').setValue("new");
        initLjjbeChart();
        initLjjblChart();
        initDeptReportChart();
        initMajorChart();
        initDeptDelayReportChart();
        initDeptQuarterReportChart();
    })
    function reload() {
        initLjjbeChart();
        initLjjblChart();
        initDeptQuarterReportChart();
    }
    delayChart.on('click', function (params) {
        var  deptName = params.name;
        var seriesName = params.seriesName;
        var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/delayListPage.do?deptName="+deptName+"&delayName="+seriesName;
        window.open(url);
    })


    majorReport.on('click', function (params) {
        var  name = params.name;
        var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getListPage.do?majorName="+name;
        window.open(url);
    })
    deptReportChart.on('click', function (params) {
        var  deptName = params.name;
        var seriesName = params.seriesName;
        var reportType = '';
        if(seriesName=="累计已切换"){
            reportType = '1';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getListPage.do?deptName="+deptName+"&reportType="+reportType;
        window.open(url);
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
