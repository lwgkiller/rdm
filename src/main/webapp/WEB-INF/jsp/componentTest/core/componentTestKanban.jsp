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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
                        <input id="signYear" name="signYear"
                               class="mini-combobox" onValuechanged="signYearChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        承担单位：
                        <input id="laboratory" name="laboratory"
                               class="mini-combobox" onValuechanged="laboratoryChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=undertakeLaboratory"
                               valueField="key" textField="value" showNullItem="true"/>
                    </span>
                </header>
                <div id="componentTestCompletionStatusChart" class="containerBox">
                </div>
            </li>
            <%--222--%>
            <li class="gs-w" data-row="1" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        零部件类别：
                        <input id="componentCategory" name="componentCategory"
                               class="mini-combobox" onValuechanged="componentCategoryChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                               valueField="key" textField="value" showNullItem="true"/>
                    </span>
                </header>
                <div id="componentTestResultChart" class="containerBox">
                </div>
            </li>
            <%--333--%>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="iniComponentTestScheduleChart1()">按类别</a>
                        <a class="mini-button" onclick="iniComponentTestScheduleChart2()">按所部</a>
                    </span>
                </header>
                <div id="componentTestScheduleChart" class="containerBox">
                </div>
            </li>
            <%--444--%>
            <li class="gs-w" data-row="4" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="iniComponentTestReportChart1()">按类别</a>
                        <a class="mini-button" onclick="iniComponentTestReportChart2()">按所部</a>
                    </span>
                </header>
                <div id="componentTestReportChart" class="containerBox">
                </div>
            </li>
            <%--555--%>
            <li class="gs-w" data-row="7" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">当前试验执行</span>
                </header>
                <div class="mini-fit" style="height: 100%;">
                    <div id="componentTestImplementListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
                         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="10" allowAlternating="true"
                         pagerButtons="#pagerButtons" ondrawcell="onDrawCell">
                        <div property="columns">
                            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
                            <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
                            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
                            <div field="componentModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
                            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
                            <div field="testStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验运行状态</div>
                            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
                            <div field="testLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验负责人</div>
                            <div field="testProgress" width="100" headerAlign="center" align="center" allowSort="true" numberFormat="p">试验进度</div>
                        </div>
                    </div>
                </div>
            </li>
            <%--666--%>
            <li class="gs-w" data-row="8" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">试验异常监控</span>
                </header>
                <div class="mini-fit" style="height: 100%;">
                    <div id="componentTestAbnormalListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
                         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="10" allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
                            <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
                            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
                            <div field="componentModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
                            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
                            <%--<div field="machineModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机型号</div>--%>
                            <div field="completeTestMonth" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验完成时间
                            </div>
                            <div field="testResult" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验结果</div>
                            <div field="nonconformingDescription" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">
                                不合格项说明
                            </div>
                            <div field="unqualifiedStatus" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">
                                不合格零部件当前状态
                            </div>
                            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
                            <div field="testLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验负责人</div>
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
    var currentYear = "";
    var componentTestCompletionStatusChart = echarts.init(document.getElementById('componentTestCompletionStatusChart'));
    var componentTestResultChart = echarts.init(document.getElementById('componentTestResultChart'));
    var componentTestScheduleChart = echarts.init(document.getElementById('componentTestScheduleChart'));
    var componentTestReportChart = echarts.init(document.getElementById('componentTestReportChart'));
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
        mini.get("signYear").setValue(currentYear);
        //..111
        iniComponentTestCompletionStatusChart();
        //..222
        inicomponentTestResultChart();
        //..333
        iniComponentTestScheduleChart1();
        //..444
        iniComponentTestReportChart1()
        //..555
        iniComponentTestImplementList();
        //..666
        inicomponentTestAbnormalList();
    })
    function onDrawCell(e) {
        var node = e.node,
            column = e.column,
            field = e.field,
            value = e.value;
        //进度
        if (field == "testProgress") {
            e.cellHtml = '<div class="progressbar">'
                + '<div class="progressbar-percent" style="width:' + value * 100 + '%;"></div>'
                + '<div class="progressbar-label">' + value * 100 + '%</div>'
                + '</div>';
        }
    }
    function signYearChange() {
        //..111
        iniComponentTestCompletionStatusChart();
        //..222
        inicomponentTestResultChart();
        //..333
        iniComponentTestScheduleChart1();
        //..444
        iniComponentTestReportChart1();
        //..555
        iniComponentTestImplementList();
        //..666
        inicomponentTestAbnormalList();
    }
    function laboratoryChange() {
        //..111
        iniComponentTestCompletionStatusChart();
        //..222
        inicomponentTestResultChart();
        //..333
        iniComponentTestScheduleChart1();
        //..444
        iniComponentTestReportChart1();
        //..555
        iniComponentTestImplementList();
        //..666
        inicomponentTestAbnormalList();
    }
    function componentCategoryChange() {
        inicomponentTestResultChart();
    }
    //..111零部件试验完成情况begin
    function getComponentTestCompletionStatusData(signYear, laboratory) {
        var resultData;
        var postData = {
            "signYear": signYear,
            //2022-07-06增加承担单位条件
            "laboratory": laboratory
            //以上增加承担单位条件
        };
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/getComponentTestCompletionStatusData.do',
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
    function iniComponentTestCompletionStatusChart() {
        componentTestCompletionStatusChart.clear();
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestCompletionStatusData(signYear, laboratory);
        //以上增加承担单位条件
        //计划数，按照"改进后再次", "临时新增", "年度计划", '序时进度', '全年计划'排序
        componentTestCompletionStatusOption.series[0].data = resultData.planValueArr;
        //完成数，按照"改进后再次", "临时新增", "年度计划", '序时进度', '全年计划'排序
        componentTestCompletionStatusOption.series[1].data = resultData.actualValueArr;
        componentTestCompletionStatusChart.setOption(componentTestCompletionStatusOption);
    }
    var componentTestCompletionStatusOption = {
        title: {
            text: '零部件试验完成情况'
        },
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var res = '<p>计划类别：' + params[0].name + '</p>';
                res += '<p>' + params[0].seriesName + '：' + params[0].value + '</p>';
                res += '<p>' + params[1].seriesName + '：' + params[1].value + '</p>';
                var percent = (params[1].value / params[0].value * 100).toFixed(2);
                res += '<p>计划完成率：' + percent + '%<p>';
                return res;
            }
        },
        legend: {
            data: ['试验计划', '已开展试验'],
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
            data: ["改进后再次试验", "临时新增试验", "年度计划试验", '序时进度', '全年计划']
        },
        series: [
            {
                name: '试验计划',
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                data: [2, 4, 6, 8, 10],
                label: {
                    show: true,
                    position: 'insideRight'
                }
            },
            {
                name: '已开展试验',
                type: 'bar',
                barGap: '0%',
                color: '#129b2c',
                data: [1, 3, 5, 7, 9],
                label: {
                    show: true,
                    position: 'insideRight'
                }
            }
        ]
    };
    //..111零部件试验完成情况end
    //..222零部件试验结果begin
    function getComponentTestResultData(signYear, componentCategory, laboratory) {
        var resultData;
        var postData = {
            "signYear": signYear,
            "componentCategory": componentCategory,
            //2022-07-06增加承担单位条件
            "laboratory": laboratory
            //以上增加承担单位条件
        };
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/getComponentTestRestltData.do',
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
    function inicomponentTestResultChart() {
        componentTestResultChart.clear();
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        var componentCategory = "";
        if (mini.get("componentCategory").getValue()) {
            componentCategory = mini.get("componentCategory").getText();
            componentTestResultOption.title.text = '零部件试验结果:' + componentCategory;
        } else {
            componentTestResultOption.title.text = '零部件试验结果:全类别';
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestResultData(signYear, componentCategory, laboratory);
        //以上增加承担单位条件
        componentTestResultOption.series[0].data = resultData;
        componentTestResultChart.setOption(componentTestResultOption);
    }
    var componentTestResultOption = {
        title: {
            text: '零部件试验结果:全类别'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'horizontal',
            right: '10%'
        },
        color: ['#70a821', '#c59e39', '#0c7aa8'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "60%"],
                label: {
                    normal: {
                        formatter: '{b}：{c}个\n占比：{d}%',
                        position: 'outside'
                    }
                },
                data: [
                    {value: 0, name: '合格'},
                    {value: 0, name: '不合格'},
                    {value: 0, name: '研究性试验数据'}
                ]
            }
        ]
    };
    //..222零部件试验结果end
    //..333零部件试验计划及进度begin
    function getComponentTestScheduleData(signYear, laboratory, action) {
        var resultData;
        var postData = {
            "signYear": signYear,
            //2022-07-06增加承担单位条件
            "laboratory": laboratory,
            //以上增加承担单位条件
            "action": action
        };
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/getComponentTestScheduleData.do',
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
    function iniComponentTestScheduleChart1() {
        componentTestScheduleChart.clear();
        componentTestScheduleOption.title.text = '零部件试验计划及进度:按类别';
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestScheduleData(signYear, laboratory, "leibie");
        //以上增加承担单位条件
        componentTestScheduleOption.dataset = resultData;
        componentTestScheduleChart.setOption(componentTestScheduleOption);
    }
    function iniComponentTestScheduleChart2() {
        componentTestScheduleChart.clear();
        componentTestScheduleOption.title.text = '零部件试验计划及进度:按所部';
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestScheduleData(signYear, laboratory, "suobu");
        //以上增加承担单位条件
        componentTestScheduleOption.dataset = resultData;
        componentTestScheduleChart.setOption(componentTestScheduleOption);
    }
    var componentTestScheduleOption = {
        title: {text: '零部件试验计划及进度'},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>试验计划：' + params.data.试验计划 + '</p>';
                res += '<p>已开展试验：' + params.data.已开展试验 + '</p>';
                var percent = (params.data.已开展试验 / params.data.试验计划 * 100).toFixed(2);
                res += '<p>计划完成率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['类别', '试验计划', '已开展试验'],
            source: [
                {类别: '', 试验计划: 0, 已开展试验: 0},
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
    //..333零部件试验计划及进度end
    //..444零部件试验报告出具情况begin
    function getComponentTestReportData(signYear, laboratory, action) {
        var resultData;
        var postData = {
            "signYear": signYear,
            //2022-07-06增加承担单位条件
            "laboratory": laboratory,
            //以上增加承担单位条件
            "action": action
        };
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/getComponentTestReportData.do',
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
    function iniComponentTestReportChart1() {
        componentTestReportChart.clear();
        componentTestReportOption.title.text = '零部件试验报告出具情况:按类别';
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestReportData(signYear, laboratory, "leibie");
        //以上增加承担单位条件
        componentTestReportOption.dataset = resultData;
        componentTestReportChart.setOption(componentTestReportOption);
    }
    function iniComponentTestReportChart2() {
        componentTestReportChart.clear();
        componentTestReportOption.title.text = '零部件试验报告出具情况:按所部';
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
        }
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        var resultData = getComponentTestReportData(signYear, laboratory, "suobu");
        //以上增加承担单位条件
        componentTestReportOption.dataset = resultData;
        componentTestReportChart.setOption(componentTestReportOption);
    }
    var componentTestReportOption = {
        title: {text: '零部件试验报告出具情况'},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>已开展试验：' + params.data.已开展试验 + '</p>';
                res += '<p>已出报告：' + params.data.已出报告 + '</p>';
                var percent = (params.data.已出报告 / params.data.已开展试验 * 100).toFixed(2);
                res += '<p>报告出具率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['类别', '已开展试验', '已出报告'],
            source: [
                {类别: '', 已开展试验: 0, 已出报告: 0},
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
    //..444零部件试验报告出具情况end
    //..555当前试验执行begin
    function iniComponentTestImplementList() {
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
            var signYearBegin = signYear + "-01";
            var signYearEnd = signYear + "-12";
        }
        var componentTestImplementListGrid = mini.get("componentTestImplementListGrid");
        componentTestImplementListGrid.setUrl("${ctxPath}/componentTest/core/kanban/dataListQuery.do?");
        var params = [];
        params.push({name: "plannedTestMonthBegin", value: signYearBegin});
        params.push({name: "plannedTestMonthEnd", value: signYearEnd});
        params.push({name: "testStatus", value: "进行中"});
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        params.push({name: "laboratory", value: laboratory});
        //以上增加承担单位条件
        var data = {};
        data.filter = mini.encode(params);
        data.pageIndex = componentTestImplementListGrid.getPageIndex();
        data.pageSize = componentTestImplementListGrid.getPageSize();
        data.sortField = componentTestImplementListGrid.getSortField();
        data.sortOrder = componentTestImplementListGrid.getSortOrder();
        componentTestImplementListGrid.load(data);
    }
    //..555当前试验执行end
    //..666试验异常监控begin
    function inicomponentTestAbnormalList() {
        var signYear = '';
        if (mini.get("signYear").getValue()) {
            signYear = mini.get("signYear").getText();
            var signYearBegin = signYear + "-01";
            var signYearEnd = signYear + "-12";
        }
        var componentTestAbnormalListGrid = mini.get("componentTestAbnormalListGrid");
        componentTestAbnormalListGrid.setUrl("${ctxPath}/componentTest/core/kanban/dataListQuery.do?test=fuck");
        var params = [];
        params.push({name: "plannedTestMonthBegin", value: signYearBegin});
        params.push({name: "plannedTestMonthEnd", value: signYearEnd});
        params.push({name: "componentTestAbnormalTag", value: 'true'});
        //2022-07-06增加承担单位条件
        var laboratory = mini.get("laboratory").getValue();
        params.push({name: "laboratory", value: laboratory});
        //以上增加承担单位条件
        var data = {};
        data.filter = mini.encode(params);
        data.pageIndex = componentTestAbnormalListGrid.getPageIndex();
        data.pageSize = componentTestAbnormalListGrid.getPageSize();
        data.sortField = componentTestAbnormalListGrid.getSortField();
        data.sortOrder = componentTestAbnormalListGrid.getSortOrder();
        componentTestAbnormalListGrid.load(data);
    }
    //..666试验异常监控end
</script>
</body>
</html>
