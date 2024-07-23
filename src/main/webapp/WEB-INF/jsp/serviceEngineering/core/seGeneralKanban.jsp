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
    <script src="${ctxPath}/scripts/serviceEngineering/purchasedPartsChart.js?version=${static_res_version}" type="text/javascript"></script>
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

        .wgjzlsj {
            background: #fff;
        }

        .gridster ul li div.wgjzlsjBox {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="iniPartsAtlasStorageChart()">入库</a>
                        <a class="mini-button" onclick="iniPartsAtlasShipmentChart()">发运</a>
                    </span>
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <div style="float:left;color:#ef1b01;" id="modelTotal">&nbsp;机型制作总数：</div><br/>
                        <div style="float:left;color:#ef1b01;" id="instanceTotal">&nbsp;实例制作总数：</div>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        开始期：
						<input id="yearMonthBegin" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonthBegin"/>
                        结束期：
						<input id="yearMonthEnd" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonthEnd"/>
					</span>
                </header>
                <div id="partsAtlasChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="1" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">外购件资料收集率及制作完成率</span>
                </header>
                <div clas="wgjzlsj">
                    <%--收集率--%>
                    <div id="sjlChart" class="wgjzlsjBox"></div>
                    <%--制作率--%>
                    <div id="zzlChart" class="wgjzlsjBox"></div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <div style="float:left;color:#ef1b01;" id="maintenanceManualTotal">&nbsp;操保手册制作总数：</div>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
                        <input id="signYearMaintenanceManual" name="signYearMaintenanceManual"
                               class="mini-combobox" onValuechanged="signYearMaintenanceManualChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
				</span>
                </header>
                <div id="maintenanceManualChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">装修手册机型计划进度</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
						<input id="signYearDecorationManual" name="signYearDecorationManualChange"
                               class="mini-combobox" onValuechanged="signYearDecorationManualChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
					</span>
                </header>
                <div id="decorationManualChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="7" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <div style="float:left;color:#ef1b01;" id="sparepartsVerificationTotal">&nbsp;备件核查总数：</div>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
                        <input id="signYearSparepartsVerification" name="signYearSparepartsVerification"
                               class="mini-combobox" onValuechanged="signYearSparepartsVerificationChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div id="sparepartsVerificationChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="7" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <div style="float:left;color:#ef1b01;">&nbsp;测试版制作总数：</div>
                        <div style="float:left;color:#ef1b01;" id="betaTotal"></div>
                        <div style="float:left;color:#ef1b01;">&nbsp;常规版制作总数：</div>
                        <div style="float:left;color:#ef1b01;" id="routineTotal"></div><br/>
                        <div style="float:left;color:#ef1b01;">&nbsp;完整版制作总数：</div>
                        <div style="float:left;color:#ef1b01;" id="completeTotal"></div>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        开始期：
						<input id="yearMonthBeginStandardvalue" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonthBegin" onValuechanged="yearMonthStandardvalueChange"/>
                        结束期：
						<input id="yearMonthEndStandardvalue" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonthEnd" onValuechanged="yearMonthStandardvalueChange"/>
					</span>
                </header>
                <div id="standardvalueChart" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var partsAtlasChart = echarts.init(document.getElementById('partsAtlasChart'));
    var sjlChart = echarts.init(document.getElementById('sjlChart'));
    var zzlChart = echarts.init(document.getElementById('zzlChart'));
    var maintenanceManualChart = echarts.init(document.getElementById('maintenanceManualChart'));
    var decorationManualChart = echarts.init(document.getElementById('decorationManualChart'));
    var sparepartsVerificationChart = echarts.init(document.getElementById('sparepartsVerificationChart'));
    var standardvalueChart = echarts.init(document.getElementById('standardvalueChart'));

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

    var currentYear = "";
    var currentMonth = "";

    $(function () {
        var date = new Date();
        if ((date.getMonth() + 1).toString().length == 1) {
            currentMonth = "0" + (date.getMonth() + 1).toString();
        } else {
            currentMonth = (date.getMonth() + 1).toString();
        }
        currentYear = date.getFullYear().toString();
        mini.get("yearMonthBegin").setValue(currentYear + "-01");
        mini.get("yearMonthEnd").setValue(currentYear + "-" + currentMonth);
        mini.get("signYearMaintenanceManual").setValue(currentYear);
        mini.get("signYearDecorationManual").setValue(currentYear);
        mini.get("yearMonthBeginStandardvalue").setValue(currentYear + "-01");
        mini.get("yearMonthEndStandardvalue").setValue(currentYear + "-" + currentMonth);
        mini.get("signYearSparepartsVerification").setValue(currentYear);
        //1
        iniPartsAtlasStorageTotal();
        iniPartsAtlasShipmentTotal();
        iniPartsAtlasStorageChart();
        //3
        iniMaintenanceManualTotal();
        iniMaintenanceManualChart();
        //4
        iniDecorationManualChart();
        //5
        iniSparepartsVerificationTotal();
        iniSparepartsVerificationChart();
        //6
        iniStandardvalueBetaTotal();
        iniStandardvalueRoutineTotal();
        iniStandardvalueCompleteTotal();
        iniStandardvalueChart();
    })
    //partsAtlas-begin-111
    //初始化机型制作总数
    function iniPartsAtlasStorageTotal() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getPartsAtlasModelTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (total) {
                document.getElementById("modelTotal").innerHTML =
                    document.getElementById("modelTotal").innerHTML + total;
            }
        })
    }
    //初始化实例制作总数
    function iniPartsAtlasShipmentTotal() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getPartsAtlasInstanceTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (total) {
                document.getElementById("instanceTotal").innerHTML =
                    document.getElementById("instanceTotal").innerHTML + total;
            }
        })
    }
    //初始化入库柱状图
    function iniPartsAtlasStorageChart() {
        partsAtlasChart.clear();
        optionPartsAtlas.title.text = '月度一机一册制作覆盖率:入库';
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText();
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText();
        }
        var resultData = getPartsAtlasKanbanData(yearMonthBegin, yearMonthEnd, "storage");
        optionPartsAtlas.dataset.source = resultData;
        partsAtlasChart.setOption(optionPartsAtlas);
    }
    //初始化发运柱状图
    function iniPartsAtlasShipmentChart() {
        partsAtlasChart.clear();
        optionPartsAtlas.title.text = '月度一机一册制作覆盖率:发运';
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText();
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText();
        }
        var resultData = getPartsAtlasKanbanData(yearMonthBegin, yearMonthEnd, "shipment");
        debugger;
        optionPartsAtlas.dataset.source = resultData;
        partsAtlasChart.setOption(optionPartsAtlas);
    }
    //获取零件图册柱状图看板数据
    function getPartsAtlasKanbanData(yearMonthBegin, yearMonthEnd, action) {
        var resultData = [];
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "action": action};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getPartsAtlasKanbanData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }
    //零件图册柱状图容器
    var optionPartsAtlas = {
        title: {text: '月度一机一册制作覆盖率'},
        legend: {
            right: '15%'
        },
        tooltip: {
            formatter: function (params) {
                console.log(params);
                var res = '<p>日期：' + params.data.日期 + '</p>';
                res += '<p>月度总数：' + params.data.月度总数 + '</p>';
                res += '<p>已发布数：' + params.data.已发布数 + '</p>';
                var percent = (params.data.已发布数 / params.data.月度总数 * 100).toFixed(2);
                res += '<p>X-GSS发布率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['日期', '月度总数', '已发布数'],
            source: [
                {日期: '', 月度总数: 0, 已发布数: 0},
            ]
        },
        xAxis: {type: 'category'},
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#2f4554',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //partsAtlas-end-111

    //maintenanceManual-begin-333
    //初始化操保手册制作总数
    function iniMaintenanceManualTotal() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getMaintenanceManualTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (total) {
                document.getElementById("maintenanceManualTotal").innerHTML =
                    document.getElementById("maintenanceManualTotal").innerHTML + total;
            }
        })
    }
    //初始化操保手册柱状图
    function iniMaintenanceManualChart() {
        maintenanceManualChart.clear();
        var signYearMaintenanceManual = '';
        if (mini.get("signYearMaintenanceManual").getValue()) {
            signYearMaintenanceManual = mini.get("signYearMaintenanceManual").getText();
        }
        var resultData = getMaintenanceManualKanbanData(signYearMaintenanceManual);
        optionMaintenanceManual.dataset.source = resultData;
        maintenanceManualChart.setOption(optionMaintenanceManual);
    }
    //获取操保手册柱状图看板数据
    function getMaintenanceManualKanbanData(signYearMaintenanceManual) {
        var resultData = [];
        var postData = {"signYearMaintenanceManual": signYearMaintenanceManual};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getMaintenanceManualKanbanData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }
    //..
    function signYearMaintenanceManualChange() {
        iniMaintenanceManualChart();
    }
    //操保手册柱状图容器
    var optionMaintenanceManual = {
        title: {text: '月度发运产品结构化操保手册覆盖率'},
        legend: {
            right: '15%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>日期：' + params.data.日期 + '</p>';
                var percent = (params.data.结构化操保手册数量 / params.data.发运产品数量 * 100).toFixed(2);
                res += '<p>结构化操保手册覆盖率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['日期', '发运产品数量', '结构化操保手册数量'],
            source: [
                {日期: '', 发运产品数量: 0, 结构化手册覆盖数量: 0},
            ]
        },
        xAxis: {type: 'category'},
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#2f4554',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //maintenanceManual-end-333

    //decorationManual-begin-444
    //初始化装修手册柱状图
    function iniDecorationManualChart() {
        decorationManualChart.clear();
        var signYearDecorationManual = '';
        if (mini.get("signYearDecorationManual").getValue()) {
            signYearDecorationManual = mini.get("signYearDecorationManual").getText();
        }
        var resultData = getDecorationManualKanbanData(signYearDecorationManual);
        optionDecorationManual.dataset.source = resultData;
        optionDecorationManual.title.text = signYearDecorationManual + "年量产机型手册制作计划及进度";
        decorationManualChart.setOption(optionDecorationManual);
    }
    //获取装修手册柱状图看板数据
    function getDecorationManualKanbanData(signYearDecorationManual) {
        var resultData = {};
        var postData = {"signYear": signYearDecorationManual};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManual/massproductionDistribution/kanbanData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }
    //..
    function signYearDecorationManualChange() {
        iniDecorationManualChart();
    }
    //装修手册柱状图容器
    var optionDecorationManual = {
        title: {
            text: ''
        },
        legend: {
            right: '10%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['产品所', '生产计划', '制作计划', '完成情况'],
            source: [
                {产品所: '小挖研究所', 生产计划: 100, 制作计划: 50, 完成情况: 30},
                {产品所: '中挖研究所', 生产计划: 110, 制作计划: 90, 完成情况: 20}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
//                rotate: '25'
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#a85044',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#2f5409',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //decorationManual-end-444

    //begin-555
    function iniSparepartsVerificationTotal() {
        var signYearSparepartsVerification = '';
        if (mini.get("signYearSparepartsVerification").getValue()) {
            signYearSparepartsVerification = mini.get("signYearSparepartsVerification").getText();
            var resultData = [];
            var postData = {"signYearSparepartsVerification": signYearSparepartsVerification};
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getSparepartsVerificationTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                document.getElementById("sparepartsVerificationTotal").innerHTML = "";
                document.getElementById("sparepartsVerificationTotal").innerHTML = "&nbsp;备件核查总数：" + total;
            }
        })
    }
    function iniSparepartsVerificationChart() {
        sparepartsVerificationChart.clear();
        var signYearSparepartsVerification = '';
        if (mini.get("signYearSparepartsVerification").getValue()) {
            signYearSparepartsVerification = mini.get("signYearSparepartsVerification").getText();
        }
        var resultData = getSparepartsVerificationKanbanData(signYearSparepartsVerification);
        optionSparepartsVerification.xAxis.data = resultData.month;
        optionSparepartsVerification.series[0].data = resultData.count;
        sparepartsVerificationChart.setOption(optionSparepartsVerification);
    }
    function getSparepartsVerificationKanbanData(signYearSparepartsVerification) {
        var resultData = {};
        var postData = {"signYear": signYearSparepartsVerification};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getSparepartsVerificationKanbanData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }
    //..
    function signYearSparepartsVerificationChange() {
        iniSparepartsVerificationTotal();
        iniSparepartsVerificationChart();
    }
    var optionSparepartsVerification = {
        title: {text: '月度代理商备件查询支持数量'},
        color: ['#808bc6'],
        xAxis: {
            type: 'category',
            data: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [150, 230, 224, 218, 135, 147, 260, 230, 123, 230, 323, 230],
                type: 'line',
                itemStyle: {
                    normal: {
                        label: {
                            show: true
                        }
                    }
                }
            }
        ]
    };
    //end-555

    //maintenanceManual-begin-666
    //初始化测试版制作总数
    function iniStandardvalueBetaTotal() {
        var yearMonthBeginStandardvalue = '';
        if (mini.get("yearMonthBeginStandardvalue").getValue()) {
            yearMonthBeginStandardvalue = mini.get("yearMonthBeginStandardvalue").getText();
        }
        var yearMonthEndStandardvalue = '';
        if (mini.get("yearMonthEndStandardvalue").getValue()) {
            yearMonthEndStandardvalue = mini.get("yearMonthEndStandardvalue").getText();
        }
        var postData = {
            "yearMonthBeginStandardvalue": yearMonthBeginStandardvalue,
            "yearMonthEndStandardvalue": yearMonthEndStandardvalue
        };
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getStandardvalueBetaTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                document.getElementById("betaTotal").innerHTML = "";
                document.getElementById("betaTotal").innerHTML =
                    document.getElementById("betaTotal").innerHTML + total;
            }
        })
    }
    //初始化常规版制作总数
    function iniStandardvalueRoutineTotal() {
        var yearMonthBeginStandardvalue = '';
        if (mini.get("yearMonthBeginStandardvalue").getValue()) {
            yearMonthBeginStandardvalue = mini.get("yearMonthBeginStandardvalue").getText();
        }
        var yearMonthEndStandardvalue = '';
        if (mini.get("yearMonthEndStandardvalue").getValue()) {
            yearMonthEndStandardvalue = mini.get("yearMonthEndStandardvalue").getText();
        }
        var postData = {
            "yearMonthBeginStandardvalue": yearMonthBeginStandardvalue,
            "yearMonthEndStandardvalue": yearMonthEndStandardvalue
        };
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getStandardvalueRoutineTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                document.getElementById("routineTotal").innerHTML = "";
                document.getElementById("routineTotal").innerHTML =
                    document.getElementById("routineTotal").innerHTML + total;
            }
        })
    }
    //初始化完整版制作总数
    function iniStandardvalueCompleteTotal() {
        var yearMonthBeginStandardvalue = '';
        if (mini.get("yearMonthBeginStandardvalue").getValue()) {
            yearMonthBeginStandardvalue = mini.get("yearMonthBeginStandardvalue").getText();
        }
        var yearMonthEndStandardvalue = '';
        if (mini.get("yearMonthEndStandardvalue").getValue()) {
            yearMonthEndStandardvalue = mini.get("yearMonthEndStandardvalue").getText();
        }
        var postData = {
            "yearMonthBeginStandardvalue": yearMonthBeginStandardvalue,
            "yearMonthEndStandardvalue": yearMonthEndStandardvalue
        };
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getStandardvalueCompleteTotalData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                document.getElementById("completeTotal").innerHTML = "";
                document.getElementById("completeTotal").innerHTML =
                    document.getElementById("completeTotal").innerHTML + total;
            }
        })
    }
    //初始化检修标准值柱状图
    function iniStandardvalueChart() {
        standardvalueChart.clear();
        var yearMonthBeginStandardvalue = '';
        if (mini.get("yearMonthBeginStandardvalue").getValue()) {
            yearMonthBeginStandardvalue = mini.get("yearMonthBeginStandardvalue").getText();
        }
        var yearMonthEndStandardvalue = '';
        if (mini.get("yearMonthEndStandardvalue").getValue()) {
            yearMonthEndStandardvalue = mini.get("yearMonthEndStandardvalue").getText();
        }
        var resultData = getStandardvalueKanbanData(yearMonthBeginStandardvalue, yearMonthEndStandardvalue);
        //质保部，测试所->应回传
        optionStandardvalue.series[0].data[0] = resultData.qa;
        optionStandardvalue.series[0].data[1] = resultData.test;
        //质保部，测试所->实际回传
        optionStandardvalue.series[1].data[0] = resultData.qaActual;
        optionStandardvalue.series[1].data[1] = resultData.testActual;
        standardvalueChart.setOption(optionStandardvalue);


    }
    //获取检修标准值柱状图看板数据
    function getStandardvalueKanbanData(yearMonthBeginStandardvalue, yearMonthEndStandardvalue) {
        var resultData = [];
        var postData = {
            "yearMonthBeginStandardvalue": yearMonthBeginStandardvalue,
            "yearMonthEndStandardvalue": yearMonthEndStandardvalue
        };
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban/getStandardvalueKanbanData.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }
    //..
    function yearMonthStandardvalueChange() {
        iniStandardvalueBetaTotal();
        iniStandardvalueRoutineTotal();
        iniStandardvalueCompleteTotal();
        iniStandardvalueChart();
    }
    //检修标准值柱状图容器
    var optionStandardvalue = {
        title: {
            text: '检修标准值实车测试数据回传报表'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['应回传数据', '实际回传数据'],
            right: '10%'
        },
        grid: {
            left: '0%',
            right: '10%',
            bottom: '8%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: ['质保部数据情况', '测试所数据情况']
        },
        series: [
            {
                name: '应回传数据',
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                data: [19325, 23489]
            },
            {
                name: '实际回传数据',
                type: 'bar',
                barGap: '0%',
                color: '#2f4554',
                data: [18203, 23438]
            }
        ]
    };
    //maintenanceManual-end-666
</script>
</body>
</html>
