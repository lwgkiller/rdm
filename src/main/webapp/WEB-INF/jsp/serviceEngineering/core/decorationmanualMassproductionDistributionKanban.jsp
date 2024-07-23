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
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle"><spring:message code="page.decorationmanualMassproductionDistributionKanban.name" /></span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        <spring:message code="page.decorationmanualMassproductionDistributionKanban.name1" />：
						<input id="signYear" name="signYear"
                               class="mini-combobox" onValuechanged="signYearChange"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
					</span>
                </header>
                <div id="reportChart2" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle"><spring:message code="page.decorationmanualMassproductionDistributionKanban.name2" /></span>
                </header>
                <div id="reportChart" class="containerBox">
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
        iniReportChart();
        iniReportChart2();
    })

    function signYearChange() {
        iniReportChart();
        iniReportChart2();
    }

    function iniReportChart() {
        reportChart.clear();
        if (mini.get("signYear").getValue()) {
            currentYear = mini.get("signYear").getText();
        }
        var resultData = getReportData(currentYear);
        option.dataset.source = resultData;
        option.title.text = currentYear + "年量产机型手册制作计划及进度";
        reportChart.setOption(option);
    }

    function getReportData(signYear) {
        var resultData = {};
        var postData = {"signYear": signYear};
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

    function iniReportChart2() {
        reportChart2.clear();
        if (mini.get("signYear").getValue()) {
            currentYear = mini.get("signYear").getText();
        }
        var resultData = getReportData2(currentYear);
        option2.series[0].data = resultData;
        debugger;
        var total = 0;
        for (i = 0; i < resultData.length; i++) {
            total += resultData[i].value;
        }
        option2.title.text = currentYear + "年量产机型数量：" + total;
        reportChart2.setOption(option2);
    }

    function getReportData2(signYear) {
        var resultData = {};
        var postData = {"signYear": signYear};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManual/massproductionDistribution/kanbanData2.do',
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

    var option2 = {
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            right: '30%',
            top: '10%'
        },
        series: [
            {
                type: 'pie',
                radius: '80%',
                center: ['35%', '50%'],
                label: {
                    normal: {
                        formatter: '{b}：{c}个\n占比：{d}%',
                        position: 'outside'
                    }
                },
                data: [
                    {value: 0, name: '小挖研究所'},
                    {value: 0, name: '中挖研究所'}
                ]
            }
        ]
    };

</script>
</body>
</html>
