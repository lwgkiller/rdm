<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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

        .progressbar {
            position: relative;
            background: #bbb;
            width: 100%;
            height: 16px;
            overflow: hidden;
        }

        .progressbar-percent {
            position: absolute;
            height: 18px;
            background: blue;
            left: 0;
            top: 0px;
            overflow: hidden;
            z-index: 1;
        }

        .progressbar-label {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            font-size: 13px;
            color: White;
            z-index: 10;
            text-align: center;
            height: 16px;
            line-height: 16px;
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <%--111--%>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">新品试制进度总览</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年度：
                        <input id="signYear111" name="signYear111"
                               class="mini-combobox" onValuechanged="signYear111Change"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div class="mini-fit" style="height: 100%;">
                    <div id="newProductAssemblyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="10" allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div field="designModel" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">产品型号</div>
                            <div field="testQuantity" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试制台数</div>
                            <div field="theExplain" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">说明</div>
                            <div field="pin" width="180" headerAlign="center" align="center" allowSort="true" renderer="render">整机编号</div>
                            <div field="productCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品种类</div>
                            <div field="productDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品部门</div>
                            <div field="projectLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">项目负责人</div>
                            <div field="planCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">计划类型</div>
                            <div field="orderReleaseTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">
                                订单下达时间
                            </div>
                            <div field="prototypeOnLineTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">
                                样机上线时间
                            </div>
                            <div field="prototypeOutLineTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">
                                样机下线时间
                            </div>
                            <%--<div field="materialDepLoadingTime" width="100" headerAlign="center" align="center" allowSort="true"--%>
                                 <%--renderer="renderColor">--%>
                                <%--物料部装时间--%>
                            <%--</div>--%>
                            <%--<div field="downAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">--%>
                                <%--下车装配时间--%>
                            <%--</div>--%>
                            <%--<div field="upAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">上车装配时间--%>
                            <%--</div>--%>
                            <%--<div field="combinedAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">--%>
                                <%--合车装配时间--%>
                            <%--</div>--%>
                            <%--<div field="workingDeviceAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true"--%>
                                 <%--renderer="renderColor">--%>
                                <%--工作装置装配时间--%>
                            <%--</div>--%>
                            <%--<div field="wholeCommissionTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">--%>
                                <%--整机调试时间--%>
                            <%--</div>--%>
                            <div field="prototypeSequenceTime" width="100" headerAlign="center" align="center" allowSort="true"
                                 renderer="renderColor">
                                样机转序时间
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <%--222--%>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">月度-新品试制完成情况</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年度：
                        <input id="signYear222" name="signYear222"
                               class="mini-combobox" onValuechanged="signYear222Change"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div id="monthlyCompletionChart" class="containerBox">
                </div>
            </li>
            <%--333--%>
            <li class="gs-w" data-row="5" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">新品试制累计完成情况</span>
                </header>
                <div id="cumulativeCompletionChart" class="containerBox">
                </div>
            </li>
            <%--444--%>
            <li class="gs-w" data-row="8" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">新品试制完成情况(按种类)</span>
                </header>
                <div id="annualCompletionPrdChart" class="containerBox">
                </div>
            </li>
            <%--555--%>
            <li class="gs-w" data-row="8" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">新品试制完成情况(按产品部门)</span>
                </header>
                <div id="annualCompletionDepChart" class="containerBox">
                </div>
            </li>
            <%--666--%>
            <li class="gs-w" data-row="11" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">年度计划试制完成情况(按种类)</span>
                </header>
                <div id="annualCompletionPrdChartThreeBar" class="containerBox">
                </div>
            </li>
            <%--777--%>
            <li class="gs-w" data-row="11" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">年度计划试制完成情况(按产品部门)</span>
                </header>
                <div id="annualCompletionDepChartThreeBar" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var currentYear = "";
    var monthlyCompletionChart = echarts.init(document.getElementById('monthlyCompletionChart'));
    var cumulativeCompletionChart = echarts.init(document.getElementById('cumulativeCompletionChart'));
    var annualCompletionPrdChart = echarts.init(document.getElementById('annualCompletionPrdChart'));
    var annualCompletionDepChart = echarts.init(document.getElementById('annualCompletionDepChart'));
    var annualCompletionPrdChartThreeBar = echarts.init(document.getElementById('annualCompletionPrdChartThreeBar'));
    var annualCompletionDepChartThreeBar = echarts.init(document.getElementById('annualCompletionDepChartThreeBar'));


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
    $(function () {
        var date = new Date();
        currentYear = date.getFullYear().toString();
        //..111
        mini.get("signYear111").setValue(currentYear);
        iniNewProductAssemblyList();
        //..222
        mini.get("signYear222").setValue(currentYear);
        iniMonthlyCompletionChart();
        //..333
        iniCumulativeCompletionChart();
        //..444
        iniAnnualCompletionPrdChart();
        //..555
        iniAnnualCompletionDepChart();
        //..666
        iniAnnualCompletionPrdChartThreeBar();
        //..777
        iniAnnualCompletionDepChartThreeBar();

    })
    function renderColor(e) {
        if (e.value != null && e.value != '') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #0bb20c" >' + e.value + '</span>';
        }
        return html;
    }
    function signYear111Change() {
        //..111
        iniNewProductAssemblyList();
    }
    function signYear222Change() {
        //..222
        iniMonthlyCompletionChart();
        //..333
        iniCumulativeCompletionChart();
        //..444
        iniAnnualCompletionPrdChart();
        //..555
        iniAnnualCompletionDepChart();
        //..666
        iniAnnualCompletionPrdChartThreeBar();
        //..777
        iniAnnualCompletionDepChartThreeBar();
    }

    //..111当前试验执行begin
    function iniNewProductAssemblyList() {
        if (mini.get("signYear111").getValue()) {
            var signYearBegin = mini.get("signYear111").getText() + "-01-01";
            var signYearEnd = mini.get("signYear111").getText() + "-12-31";
        }
        var newProductAssemblyListGrid = mini.get("newProductAssemblyListGrid");
        newProductAssemblyListGrid.setUrl("${ctxPath}/newproductAssembly/core/kanban/dataListQuery.do?");
        var params = [];
        params.push({name: "orderReleaseTimeBegin", value: signYearBegin});
        params.push({name: "orderReleaseTimeEnd", value: signYearEnd});
        var data = {};
        data.filter = mini.encode(params);
        data.pageIndex = newProductAssemblyListGrid.getPageIndex();
        data.pageSize = newProductAssemblyListGrid.getPageSize();
        data.sortField = newProductAssemblyListGrid.getSortField();
        data.sortOrder = newProductAssemblyListGrid.getSortOrder();
        newProductAssemblyListGrid.load(data);
    }
    //..111当前试验执行end

    //..222月度新品试制完成情况begin
    function getCompletionData(signYear, action) {
        var resultData;
        var postData = {
            "signYear": signYear,
            "action": action
        };
        $.ajax({
            url: jsUseCtxPath + '/newproductAssembly/core/kanban/getCompletionData.do',
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
    function iniMonthlyCompletionChart() {
        monthlyCompletionChart.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCompletionData(signYear, "monthly");
        monthlyCompletionOption.dataset = resultData;
        monthlyCompletionChart.setOption(monthlyCompletionOption);
    }
    var monthlyCompletionOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>装配上线(台)：' + params.data.装配上线 + '</p>';
                res += '<p>转序完成(台)：' + params.data.转序完成 + '</p>';
                var percent = (params.data.转序完成 / params.data.装配上线 * 100).toFixed(2);
                res += '<p>完成率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['月份', '装配上线', '转序完成'],
            source: [
                {月份: '', 装配上线: 0, 转序完成: 0},
            ]
        },
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
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //..222月度新品试制完成情况end

    //..333累计新品完成情况begin
    function getCumulativeCompletionData(signYear, action) {
        var resultData;
        var postData = {
            "signYear": signYear,
            "action": action
        };
        $.ajax({
            url: jsUseCtxPath + '/newproductAssembly/core/kanban/getCompletionDataPie.do',
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
    function iniCumulativeCompletionChart() {
        cumulativeCompletionChart.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCumulativeCompletionData(signYear, "1");
//        var resultData2 = getCumulativeCompletionData(signYear, "2");
        cumulativeCompletionOption.series[0].data = resultData;
//        cumulativeCompletionOption.series[1].data = resultData2;
        cumulativeCompletionChart.setOption(cumulativeCompletionOption);
    }
    var cumulativeCompletionOption = {
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'horizontal',
            right: '10%'
        },
        color: ['#70a821', '#c59e39'],
        series: [
            {
                type: 'pie',
                radius: '70%',
                center: ["50%", "50%"],
                label: {
                    normal: {
                        formatter: '{b}：{c}台',
                        position: 'outside'
                    }
                },
                labelLine: {
                    show: true
                },
                data: [
                    {value: 0, name: '年度计划完成量'},
                    {value: 0, name: '新增计划完成量'}
                ]
            }
//            {
//                type: 'pie',
//                radius: '50%',
//                center: ["70%", "50%"],
//                label: {
//                    normal: {
//                        formatter: '{b}：{c}台\n占比：{d}%',
////                        formatter: '{d}%',
//                        position: 'outside'
//                    }
//                },
//                data: [
//                    {value: 0, name: '年度计划已完成量'},
//                    {value: 0, name: '年度计划未完成量'}
//                ]
//            }
        ]
    };
    //..333累计新品完成情况end

    //..444新品试制完成情况(按产品种类)begin
    function iniAnnualCompletionPrdChart() {
        annualCompletionPrdChart.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCompletionData(signYear, "prd");
        annualCompletionPrdOption.dataset = resultData;
        annualCompletionPrdChart.setOption(annualCompletionPrdOption);
    }
    var annualCompletionPrdOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
//                res += '<p>计划完成(台)：' + params.data.计划完成 + '</p>';
                res += '<p>实际完成(台)：' + params.data.实际完成 + '</p>';
//                var percent = (params.data.实际完成 / params.data.计划完成 * 100).toFixed(2);
//                res += '<p>完成率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
//            dimensions: ['种类', '计划完成', '实际完成'],
            dimensions: ['种类', '实际完成'],
            source: [
//                {种类: '', 计划完成: 0, 实际完成: 0},
                {种类: '', 实际完成: 0}
            ]
        },
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
        yAxis: {},
        series: [
//            {
//                type: 'bar',
//                barGap: '0%',
//                color: '#0c7aa8',
//                label: {
//                    show: true,
//                    position: 'top'
//                }
//            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //..444新品试制完成情况(按产品种类)end

    //..555新品试制完成情况(按产品部门)begin
    function iniAnnualCompletionDepChart() {
        annualCompletionDepChart.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCompletionData(signYear, "dep");
        annualCompletionDepOption.dataset = resultData;
        annualCompletionDepChart.setOption(annualCompletionDepOption);
    }
    var annualCompletionDepOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
//                res += '<p>计划完成(台)：' + params.data.计划完成 + '</p>';
                res += '<p>实际完成(台)：' + params.data.实际完成 + '</p>';
//                var percent = (params.data.实际完成 / params.data.计划完成 * 100).toFixed(2);
//                res += '<p>完成率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
//            dimensions: ['部门', '计划完成', '实际完成']
            dimensions: ['部门', '实际完成'],
            source: [
//                {部门: '', 计划完成: 0, 实际完成: 0}
                {部门: '', 实际完成: 0}
            ]
        },
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
        yAxis: {},
        series: [
//            {
//                type: 'bar',
//                barGap: '0%',
//                color: '#0c7aa8',
//                label: {
//                    show: true,
//                    position: 'top'
//                }
//            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //..555新品试制完成情况(按产品部门)end

    //..666年度计划试制完成情况(按产品种类)begin
    function iniAnnualCompletionPrdChartThreeBar() {
        annualCompletionPrdChartThreeBar.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCompletionData(signYear, "prdThreeBar");
        annualCompletionPrdOptionThreeBar.dataset = resultData;
        annualCompletionPrdChartThreeBar.setOption(annualCompletionPrdOptionThreeBar);
    }
    var annualCompletionPrdOptionThreeBar = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>计划完成(台)：' + params.data.计划完成 + '</p>';
                res += '<p>当前完成(台)：' + params.data.当前完成 + '</p>';
                res += '<p>当年完成(台)：' + params.data.当年完成 + '</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['种类', '计划完成', '当前完成', '当年完成'],
            source: [
                {种类: '', 计划完成: 0, 当前完成: 0, 当年完成: 0},
            ]
        },
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
                color: '#9715a8',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //..666年度计划试制完成情况(按产品种类)end

    //..777年度计划试制完成情况(按部门)begin
    function iniAnnualCompletionDepChartThreeBar() {
        annualCompletionDepChartThreeBar.clear();
        var signYear = '';
        if (mini.get("signYear222").getValue()) {
            signYear = mini.get("signYear222").getText();
        }
        var resultData = getCompletionData(signYear, "depThreeBar");
        annualCompletionDepOptionThreeBar.dataset = resultData;
        annualCompletionDepChartThreeBar.setOption(annualCompletionDepOptionThreeBar);
    }
    var annualCompletionDepOptionThreeBar = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>计划完成(台)：' + params.data.计划完成 + '</p>';
                res += '<p>当前完成(台)：' + params.data.当前完成 + '</p>';
                res += '<p>当年完成(台)：' + params.data.当年完成 + '</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['部门', '计划完成', '当前完成', '当年完成'],
            source: [
                {部门: '', 计划完成: 0, 当前完成: 0, 当年完成: 0},
            ]
        },
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
                color: '#9715a8',
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    //..777年度计划试制完成情况(按部门)end
</script>
</body>
</html>
