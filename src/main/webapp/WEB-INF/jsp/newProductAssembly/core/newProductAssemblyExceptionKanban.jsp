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
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">新品试制异常统计</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年度：
                        <input id="signYear333" name="signYear333"
                               class="mini-combobox" onValuechanged="signYear333Change"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div class="mini-fit" style="height: 100%;">
                    <div id="newProductAssemblyExceptionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="10" allowAlternating="true"
                         pagerButtons="#pagerButtons" showPager="false">
                        <div property="columns">
                            <div field="designModel" width="130" headerAlign="center" align="center" allowSort="true">产品型号</div>
                            <div field="productDep" width="100" headerAlign="center" align="center" allowSort="true">产品部门</div>
                            <div field="projectLeader" width="100" headerAlign="center" align="center" allowSort="true">项目负责人</div>
                            <div field="exceptionQuantity" width="100" headerAlign="center" align="center" allowSort="false">过程异常问题条数</div>
                            <div field="closedLoopQuantity" width="100" headerAlign="center" align="center" allowSort="false">闭环管理条数</div>
                            <div field="closedLoopRate" width="100" headerAlign="center" align="center" allowSort="false" numberFormat="p">闭环率</div>
                        </div>
                    </div>
                </div>
            </li>
            <%--222--%>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">年度-试制异常汇总(按部件分类)</span>
                </header>
                <div id="annualExceptionPartChart" class="containerBox">
                </div>
            </li>
            <%--333--%>
            <li class="gs-w" data-row="5" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">年度-试制异常汇总(按异常类型)</span>
                </header>
                <div id="annualExceptionEtypeChart" class="containerBox">
                </div>
            </li>
            <%--444--%>
            <li class="gs-w" data-row="8" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">试制异常汇总(按责任部门)</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        月份：
                        <input id="signMonth" name="signMonth" showNullItem="true"
                               class="mini-combobox" onValuechanged="signYear444Change"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                               valueField="key" textField="value"/>
                    </span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年度：
                        <input id="signYear444" name="signYear444"
                               class="mini-combobox" onValuechanged="signYear444Change"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div id="annualExceptionDepChart" class="containerBox">
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
    var annualExceptionPartChart = echarts.init(document.getElementById('annualExceptionPartChart'));
    var annualExceptionEtypeChart = echarts.init(document.getElementById('annualExceptionEtypeChart'));
    var annualExceptionDepChart = echarts.init(document.getElementById('annualExceptionDepChart'));

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
        mini.get("signYear333").setValue(currentYear);
        iniNewProductAssemblyExceptionList();
        //..222
        iniAnnualExceptionPartChart();
        //..333
        iniAnnualExceptionEtypeChart();
        //..444
        mini.get("signYear444").setValue(currentYear);
        iniAnnualExceptionDepChart();

    })
    function renderColor(e) {
        if (e.value != null && e.value != '') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #0bb20c" >' + e.value + '</span>';
        }
        return html;
    }
    function signYear333Change() {
        //..111
        iniNewProductAssemblyExceptionList();
        //..222
        iniAnnualExceptionPartChart();
        //..333
        iniAnnualExceptionEtypeChart();
    }
    function signYear444Change() {
        //..444
        iniAnnualExceptionDepChart();
    }

    //..111新品试制异常统计begin
    function iniNewProductAssemblyExceptionList() {
        if (mini.get("signYear333").getValue()) {
            var signYearBegin = mini.get("signYear333").getText() + "-01-01";
            var signYearEnd = mini.get("signYear333").getText() + "-12-31";
        }
        var newProductAssemblyExceptionListGrid = mini.get("newProductAssemblyExceptionListGrid");
        newProductAssemblyExceptionListGrid.setUrl("${ctxPath}/newproductAssembly/core/kanban/exceptionStatisticListQuery.do?");
        var params = [];
        params.push({name: "orderReleaseTimeBegin", value: signYearBegin});
        params.push({name: "orderReleaseTimeEnd", value: signYearEnd});
        var data = {};
        data.filter = mini.encode(params);
        data.pageIndex = newProductAssemblyExceptionListGrid.getPageIndex();
        data.pageSize = newProductAssemblyExceptionListGrid.getPageSize();
        data.sortField = newProductAssemblyExceptionListGrid.getSortField();
        data.sortOrder = newProductAssemblyExceptionListGrid.getSortOrder();
        newProductAssemblyExceptionListGrid.load(data);
    }
    //..111新品试制异常统计end

    //..222年度-试制异常汇总(按部件分类)begin
    function getExceptionData(signYear, action) {
        var resultData;
        var postData = {
            "signYear": signYear,
            "action": action
        };
        $.ajax({
            url: jsUseCtxPath + '/newproductAssembly/core/kanban/getExceptionData.do',
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
    function iniAnnualExceptionPartChart() {
        annualExceptionPartChart.clear();
        var signYear = '';
        if (mini.get("signYear333").getValue()) {
            signYear = mini.get("signYear333").getText();
        }
        var resultData = getExceptionData(signYear, "partsCategory");
        annualExceptionPartOption.dataset = resultData;
        annualExceptionPartChart.setOption(annualExceptionPartOption);
    }
    var annualExceptionPartOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>异常数量(个)：' + params.data.异常数量 + '</p>';
                res += '<p>闭环解决(个)：' + params.data.闭环解决 + '</p>';
                var percent = (params.data.闭环解决 / params.data.异常数量 * 100).toFixed(2);
                res += '<p>闭环率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['部件', '异常数量', '闭环解决'],
            source: [
                {部件: '', 异常数量: 0, 闭环解决: 0},
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
    //..222年度-试制异常汇总(按部件分类)end

    //..333年度-试制异常汇总(按异常类型)begin
    function iniAnnualExceptionEtypeChart() {
        annualExceptionEtypeChart.clear();
        var signYear = '';
        if (mini.get("signYear333").getValue()) {
            signYear = mini.get("signYear333").getText();
        }
        var resultData = getExceptionData(signYear, "exceptionType");
        annualExceptionEtypeOption.dataset = resultData;
        annualExceptionEtypeChart.setOption(annualExceptionEtypeOption);
    }
    var annualExceptionEtypeOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>异常数量(个)：' + params.data.异常数量 + '</p>';
                res += '<p>闭环解决(个)：' + params.data.闭环解决 + '</p>';
                var percent = (params.data.闭环解决 / params.data.异常数量 * 100).toFixed(2);
                res += '<p>闭环率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['异常类型', '异常数量', '闭环解决'],
            source: [
                {异常类型: '', 异常数量: 0, 闭环解决: 0},
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
    //..333年度-试制异常汇总(按异常类型)end

    //..444试制异常汇总(按责任部门)begin
    function iniAnnualExceptionDepChart() {
        debugger;
        annualExceptionDepChart.clear();
        var signYear = '';
        var signMonth = '';
        if (mini.get("signYear444").getValue()) {
            signYear = mini.get("signYear444").getText();
        }
        if (mini.get("signMonth").getValue()) {
            signMonth = mini.get("signMonth").getText();
            signYear = signYear + '-' + signMonth;
        }
        var resultData = getExceptionData(signYear, "dep");
        annualExceptionDepOption.dataset = resultData;
        annualExceptionDepChart.setOption(annualExceptionDepOption);
    }
    var annualExceptionDepOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>异常数量(个)：' + params.data.异常数量 + '</p>';
                res += '<p>闭环解决(个)：' + params.data.闭环解决 + '</p>';
                var percent = (params.data.闭环解决 / params.data.异常数量 * 100).toFixed(2);
                res += '<p>闭环率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['责任部门', '异常数量', '闭环解决'],
            source: [
                {责任部门: '', 异常数量: 0, 闭环解决: 0},
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
    //..444试制异常汇总(按责任部门)end
</script>
</body>
</html>
