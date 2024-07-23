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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name" /></span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        <spring:message code="page.decorationManualDomesticoverseasProgressKanban.name1" />：
						<input id="signYear" name="signYear"
                               class="mini-combobox"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                        <a class="mini-button" onclick="iniReportChartOverseas()"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name2" /></a>
                        <a class="mini-button" onclick="iniReportChartDomestic()"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name3" /></a>
					</span>
                </header>
                <div id="reportChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="2" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name4" /></span>
                </header>
                <div class="mini-fit" style="height: 100%;">
                    <div id="decorationManualDomesticOverseasProgressModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
                         allowResize="false" onSelect="select"
                         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[100,500]" pageSize="100" allowAlternating="true"
                         showPager="false">
                        <div property="columns">
                            <div field="salesModel" width="50" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name5" /></div>
                            <div field="designModel" width="50" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name6" /></div>
                            <div field="materialCode" name="modelTotal" width="100" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name7" /></div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="3" data-sizex="4" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle"><spring:message code="page.decorationManualDomesticoverseasProgressKanban.name8" /></span>
                </header>
                <div id="reportChart2" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var reportChart = echarts.init(document.getElementById('reportChart'));
    var reportChart2 = echarts.init(document.getElementById('reportChart2'));
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

    $(function () {
        var date = new Date();
        mini.get("signYear").setValue(date.getFullYear().toString());
        iniReportChartOverseas();
    })

    function iniReportChartOverseas() {
        reportChart.clear();
        if (mini.get("signYear").getValue()) {
            currentYear = mini.get("signYear").getText();
        }
        ////////
        var resultData = getReportData(currentYear, "海外计划");
        option.dataset.source = resultData;
        option.title.text = currentYear + "年" + "海外机型计划进度柱状图";
        reportChart.setOption(option);
        ///////
        reportChart2.clear();
        iniDecorationManualDomesticOverseasProgressModelList(currentYear, "海外计划");
    }

    function iniReportChartDomestic() {
        reportChart.clear();
        if (mini.get("signYear").getValue()) {
            currentYear = mini.get("signYear").getText();
        }
        ////////
        var resultData = getReportData(currentYear, "内销计划");
        option.dataset.source = resultData;
        option.title.text = currentYear + "年" + "内销机型计划进度柱状图";
        reportChart.setOption(option);
        ///////
        reportChart2.clear();
        iniDecorationManualDomesticOverseasProgressModelList(currentYear, "内销计划");
    }

    function getReportData(signYear, planType) {
        var resultData = {};
        var postData = {"signYear": signYear, "planType": planType};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManual/domesticOverseasProgress/kanbanData.do',
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

    function iniDecorationManualDomesticOverseasProgressModelList(currentYear, planType) {
        var decorationManualDomesticOverseasProgressModelListGrid =
            mini.get("decorationManualDomesticOverseasProgressModelListGrid");
        decorationManualDomesticOverseasProgressModelListGrid.setUrl(
            "${ctxPath}/serviceEngineering/core/decorationManual/domesticOverseasProgress/modelListQuery.do?signYear="
            + currentYear + "&planType=" + planType);
        decorationManualDomesticOverseasProgressModelListGrid.load(null, function () {
            var row = decorationManualDomesticOverseasProgressModelListGrid.getRow(0);
            decorationManualDomesticOverseasProgressModelListGrid.select(row, true);
        })
    }

    function select(sender) {
        iniReportChart2(sender.record);
    }

    function iniReportChart2(record) {
        reportChart2.clear();
        var resultData = getReportData2(record);
        option2.dataset.source = resultData;
        option2.title.text = record.salesModel + record.planType + "手册制作进度";
        reportChart2.setOption(option2);
    }

    function getReportData2(record) {
        debugger;
        var resultData = {};
        var postData = {
            "signYear": record.signYear,
            "planType": record.planType,
            "materialCode": record.materialCode,
            "salesModel": record.salesModel,
            "designModel": record.designModel
        };
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManual/domesticOverseasProgress/kanbanData2.do',
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

    var option = {
        title: {
            text: ''
        },
        legend: {},
        tooltip: {},
        dataset: {
            dimensions: ['手册类型', '完成数'],
            source: [
                {手册类型: '装修手册', 完成数: 10},
                {手册类型: '分解与组装手册', 完成数: 11},
                {手册类型: '结构功能与保养手册', 完成数: 12},
                {手册类型: '测试与调整手册', 完成数: 13},
                {手册类型: '故障诊断手册', 完成数: 14},
                {手册类型: '力矩及工具标准值表', 完成数: 15},
                {手册类型: '检修标准值表', 完成数: 16},
                {手册类型: '发动机手册', 完成数: 17},
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                rotate: '25'
            }
        },
        yAxis: {type: 'value'},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#17a884',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    var option2 = {
        title: {
            text: ''
        },
        legend: {},
        tooltip: {},
        dataset: {
            dimensions: ['手册类型', '完成率'],
            source: [
                {手册类型: '装修手册', 完成率: 10},
                {手册类型: '分解与组装手册', 完成率: 11},
                {手册类型: '结构功能与保养手册', 完成率: 12},
                {手册类型: '测试与调整手册', 完成率: 13},
                {手册类型: '故障诊断手册', 完成率: 14},
                {手册类型: '力矩及工具标准值表', 完成率: 15},
                {手册类型: '检修标准值表', 完成率: 16},
                {手册类型: '发动机手册', 完成率: 17},
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                rotate: '25'
            }
        },
        yAxis: {type: 'value'},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#5667a8',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
</script>
</body>
</html>
