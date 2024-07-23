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
        <%--迁入：外购件资料收集及制作完成率--%>
        .gridster ul li div.wgjzlsjBox {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="1" data-sizex="4">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">备件供应形式清单统计&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                </header>
                <div id="bjqdCompletion" class="containerBox"></div>
            </li>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="5" data-sizex="4">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">可购买属性及PMS价格实时维护(最近六次)</span>
                </header>
                <div id="bjqdCompletionLine" class="containerBox"></div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    <%--var seKanbanAdmin = "${seKanbanAdmin}";--%>
    var currentUserNo = "${currentUserNo}";
    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 8,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }
    $(function () {
        iniBjqdAmount();//备件清单累计
        iniBjqdCompletion();//备件清单

        iniBjqdCompletionLine();//实时维护
    })

    //数量类统计-通用
    var bjqdAmount;//累计可购买属性确认数量
    var bjqdPMSAmount;//累计PMS价格维护数量
    function iniBjqdAmount() {
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Bjqd/getAmount.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                bjqdAmount = returnData.bjqdAmount;
                bjqdPMSAmount = returnData.bjqdPMSAmount;
            }
        })
    }

    var bjqdCompletion = echarts.init(document.getElementById('bjqdCompletion'));

    function getBjqdCompletionData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Bjqd/getBjqdCompletionData.do',
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
    function iniBjqdCompletion() {
        bjqdCompletion.clear();
        var resultData = getBjqdCompletionData();
        bjqdCompletionOption.dataset = resultData;
        bjqdCompletionOption.title.text = '累计可购买属性确认数量: ' + bjqdAmount + '\n' +
            '累计PMS价格维护数量: ' + bjqdPMSAmount;
        bjqdCompletion.setOption(bjqdCompletionOption);
    }
    var bjqdCompletionOption = {
        title: {
            text: '',
            textStyle: {
                fontSize: 13,
                lineHeight: 0
            }
        },
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>生产装配件：' + params.data.生产装配件 + '</p>';
                res += '<p>外购件子件：' + params.data.外购件子件 + '</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['统计数据', '生产装配件', '外购件子件'],
            source: [
                {统计数据: '', 生产装配件: 0, 外购件子件: 0},
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 12,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 10;
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
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };


    var bjqdCompletionLine = echarts.init(document.getElementById('bjqdCompletionLine'));

    function getBjqdCompletionLineData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Bjqd/getBjqdCompletionLineData.do',
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
    function iniBjqdCompletionLine() {
        bjqdCompletionLine.clear();
        var resultData = getBjqdCompletionLineData();
        bjqdCompletionLineOption.dataset = resultData;
        bjqdCompletionLine.setOption(bjqdCompletionLineOption);
    }
    var bjqdCompletionLineOption = {
        title: {
            text: '',
            textStyle: {
                fontSize: 13,
                lineHeight: 0
            }
        },
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>生产装配件：' + params.data.需求数量 + '</p>';
                res += '<p>外购件子件：' + params.data.可购买属性确认数量 + '</p>';
                res += '<p>外购件子件：' + params.data.PMS价格维护数量 + '</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['统计数据', '生产装配件', '外购件子件'],
            source: [
                {时间: '', 需求数量: 0, 可购买属性确认数量: 0, PMS价格维护数量: 0},
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 12,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 10;
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
                type: 'line',
                barGap: '0%',
                color: '#0c7aa8',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'line',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'line',
                barGap: '0%',
                color: '#75d460',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };

</script>
</body>
</html>
