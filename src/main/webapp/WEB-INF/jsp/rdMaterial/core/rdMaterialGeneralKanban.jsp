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

        .gridster ul li div.containerBox2 {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
        }

    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster" style="min-width: 1800px;">
        <ul>
            <%--滞留物料统计1--%>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="1" data-sizex="3">
                <header>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份:
                        <input id="theYear" name="theYear" class="mini-combobox"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value" showNullItem="true" onvaluechanged="theYearChange"/>
                    </span>
                </header>
                <div id="zhiLiuWuLiaoTongJiChat" class="containerBox"></div>
            </li>
            <%--滞留物料统计2--%>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="4" data-sizex="3">
                <header></header>
                <div id="zhiLiuWuLiaoTongJiChat2" class="containerBox"></div>
            </li>
            <%--出入库物料处理情况--%>
            <li class="gs-w" data-row="4" data-sizey="3" data-col="1" data-sizex="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="iniChuRuKuWuLiaoChuLiChartA()">按物料类型</a>
                        <a class="mini-button" onclick="iniChuRuKuWuLiaoChuLiChartB()">按入库原因</a>
                    </span>
                </header>
                <div id="chuRuKuWuLiaoChuLiChart" class="containerBox"></div>
            </li>
            <%--处理物料统计--%>
            <li class="gs-w" data-row="4" data-sizey="3" data-col="4" data-sizex="3">
                <header></header>
                <div id="chuLiWuLiaoTongJiChat" class="containerBox"></div>
            </li>
        </ul>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
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
    //..
    $(function () {
        zhiLiuWuLiaoTongJiIni();
        zhiLiuWuLiaoTongJiIni2();
        iniChuRuKuWuLiaoChuLiChartA();
        chuLiWuLiaoTongJiIni();
    })
    //..
    function theYearChange() {
        zhiLiuWuLiaoTongJiIni();
        zhiLiuWuLiaoTongJiIni2();
        iniChuRuKuWuLiaoChuLiChartA();
        chuLiWuLiaoTongJiIni();
    }
    //..滞留物料统计1
    var zhiLiuWuLiaoTongJiChat = echarts.init(document.getElementById('zhiLiuWuLiaoTongJiChat'));
    zhiLiuWuLiaoTongJiChat.on('click', function (params) {
        var untreatedTimespanBegin, untreatedTimespanEnd;
        if (params.name == '未超期') {
            untreatedTimespanBegin = "";
            untreatedTimespanEnd = "60";
        } else if (params.name == '2-5个月') {
            untreatedTimespanBegin = "60";
            untreatedTimespanEnd = "150";
        } else if (params.name == '超过5个月') {
            untreatedTimespanBegin = "150";
            untreatedTimespanEnd = "";
        }
        var theYear = mini.get("theYear").getText();
        var url = jsUseCtxPath + "/rdMaterial/core/summary/itemListPage.do?untreatedTimespanBegin=" + untreatedTimespanBegin +
            "&untreatedTimespanEnd=" + untreatedTimespanEnd + "&theYear=" + theYear + "&untreatedQuantityNotEqual=0";
        window.open(url);
    });
    var zhiLiuWuLiaoTongJiOption = {
        title: {
            text: '滞留物料按滞留时长统计'
        },
        legend: {
            right: '10%',
            show: false
        },
        tooltip: {},
        dataset: {},
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '滞\n留\n数\n量',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        },
        series: [
            {
                type: 'bar',
                barGap: '0%',
                barWidth: '50%',
                label: {
                    show: true,
                    position: 'inside',
                },
                itemStyle: {
                    normal: {
                        color: function (params) {
                            var colorList = [
                                '#0C7AA8', '#C59E39', '#A80926'
                            ];
                            return colorList[params.dataIndex]
                        }
                    }
                }
            }
        ]
    };
    function zhiLiuWuLiaoTongJiIni() {
        var theYear = mini.get("theYear").getText();
        $.ajax({
            url: jsUseCtxPath + '/rdMaterial/core/generalKanban/zhiLiuWuLiaoTongJi.do?theYear=' + theYear,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    zhiLiuWuLiaoTongJiOption.dataset = resultData;
                    zhiLiuWuLiaoTongJiChat.setOption(zhiLiuWuLiaoTongJiOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
    //..滞留物料统计2
    var zhiLiuWuLiaoTongJiChat2 = echarts.init(document.getElementById('zhiLiuWuLiaoTongJiChat2'));
    zhiLiuWuLiaoTongJiChat2.on('click', function (params) {
        var untreatedTimespanBegin, untreatedTimespanEnd;
        if (params.seriesName == '2-5个月') {
            untreatedTimespanBegin = "60";
            untreatedTimespanEnd = "150";
        } else if (params.seriesName == '超过5个月') {
            untreatedTimespanBegin = "150";
            untreatedTimespanEnd = "";
        }
        var responsibleDep = params.name;
        var theYear = mini.get("theYear").getText();
        var url = jsUseCtxPath + "/rdMaterial/core/summary/itemListPage.do?untreatedTimespanBegin=" + untreatedTimespanBegin +
            "&untreatedTimespanEnd=" + untreatedTimespanEnd + "&theYear=" + theYear + "&responsibleDep=" + responsibleDep +
            "&untreatedQuantityNotEqual=0";
        window.open(url);
    });
    var zhiLiuWuLiaoTongJiOption2 = {
        title: {
            text: '滞留物料按部门统计'
        },
        legend: {
            right: '10%'
        },
        tooltip: {},
        dataset: {},
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '滞\n留\n数\n量',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        },
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#C59E39',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#A80926',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };
    function zhiLiuWuLiaoTongJiIni2() {
        var theYear = mini.get("theYear").getText();
        $.ajax({
            url: jsUseCtxPath + '/rdMaterial/core/generalKanban/zhiLiuWuLiaoTongJi2.do?theYear=' + theYear,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    zhiLiuWuLiaoTongJiOption2.dataset = resultData;
                    zhiLiuWuLiaoTongJiChat2.setOption(zhiLiuWuLiaoTongJiOption2);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
    //..出入库物料处理情况1
    var chuRuKuWuLiaoChuLiChart = echarts.init(document.getElementById('chuRuKuWuLiaoChuLiChart'));
    var chuRuKuWuLiaoChuLiOption = {
        title: {
            text: ''
        },
        legend: {
            right: '10%'
        },
        tooltip: {},
        dataset: {},
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '出\n入\n库\n数\n量',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        },
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#17a884',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#C59E39',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };
    function iniChuRuKuWuLiaoChuLiChartA() {
        var theYear = mini.get("theYear").getText();
        chuRuKuWuLiaoChuLiChart.clear();
        $.ajax({
            url: jsUseCtxPath + '/rdMaterial/core/generalKanban/chuRuKuWuLiaoChuLi.do?theYear=' + theYear + '&type=materialType',
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    chuRuKuWuLiaoChuLiOption.dataset = resultData;
                    chuRuKuWuLiaoChuLiOption.title.text = '出入库物料处理情况:按物料类型  总计:已处理(' +
                        resultData.treated + ')未处理(' + resultData.untreated + ")";
                    chuRuKuWuLiaoChuLiChart.setOption(chuRuKuWuLiaoChuLiOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
    function iniChuRuKuWuLiaoChuLiChartB() {
        var theYear = mini.get("theYear").getText();
        chuRuKuWuLiaoChuLiChart.clear();
        chuRuKuWuLiaoChuLiOption.title.text = '出入库物料处理情况:按入库原因';
        $.ajax({
            url: jsUseCtxPath + '/rdMaterial/core/generalKanban/chuRuKuWuLiaoChuLi.do?theYear=' + theYear + '&type=reasonForStorage',
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    chuRuKuWuLiaoChuLiOption.dataset = resultData;
                    chuRuKuWuLiaoChuLiOption.title.text = '出入库物料处理情况:按物料类型  总计:已处理(' +
                        resultData.treated + ')未处理(' + resultData.untreated + ")";
                    chuRuKuWuLiaoChuLiChart.setOption(chuRuKuWuLiaoChuLiOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
    //..处理物料统计
    var chuLiWuLiaoTongJiChat = echarts.init(document.getElementById('chuLiWuLiaoTongJiChat'));
    var chuLiWuLiaoTongJiOption = {
        title: {
            text: '处理物料统计'
        },
        legend: {
            right: '10%',
            show: false
        },
        tooltip: {},
        dataset: {},
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '处\n理\n数\n量',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        },
        series: [
            {
                type: 'bar',
                barGap: '0%',
                barWidth: '50%',
                color: '#0C7AA8',
                label: {
                    show: true,
                    position: 'inside',
                }
            }
        ]
    };
    function chuLiWuLiaoTongJiIni() {
        var theYear = mini.get("theYear").getText();
        $.ajax({
            url: jsUseCtxPath + '/rdMaterial/core/generalKanban/chuLiWuLiaoTongJi.do?theYear=' + theYear,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    chuLiWuLiaoTongJiOption.dataset = resultData;
                    chuLiWuLiaoTongJiChat.setOption(chuLiWuLiaoTongJiOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
</script>
</body>
</html>
