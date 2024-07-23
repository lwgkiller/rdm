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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="6">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        开始期：
						<input id="yearMonthBegin" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonth"/>
                        结束期：
						<input id="yearMonthEnd" allowinput="false" class="mini-monthpicker"
                               style="width:100px" name="yearMonth"/>
                        选择指标：
                        <div id="account" class="mini-combobox" style="width:800px;" popupWidth="800" textField="value" valueField="key"
                             url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SGYKZB"
                             multiSelect="true" showClose="true" oncloseclick="onCloseClick">
                            <div property="columns">
                                <div header="指标编码" field="key"></div>
                                <div header="指标描述" field="value"></div>
                            </div>
                        </div>
                        <a class="mini-button" onclick="iniReportChart()">生成图表</a>
					</span>
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
    //............................
    function onCloseClick(e) {
        var obj = e.sender;
        obj.setText("");
        obj.setValue("");
    }


    //..
    function iniReportChart() {
        reportChart.clear();
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        var account = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText();
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText();
        }
        if (mini.get("account").getValue()) {
            account = mini.get("account").getValue();
        }

        var resultData = getSgykCircleData(yearMonthBegin, yearMonthEnd, account);

        var accounts = [];
        var periods = [];
        var series = [];
        var isok = false;
        for (var key in resultData) {
            accounts.push(key.split(':')[1]);
            if (isok == false) {
                for (var yearMonthKey in resultData[key]) {
                    periods.push(yearMonthKey);
                }
            }
            isok = true;
            debugger;
            var values = [];
            for (var yearMonthKey in resultData[key]) {
                values.push(resultData[key][yearMonthKey]);
            }
            series.push({
                name: key.split(':')[1],
                type: 'line',
                smooth: true,
                data: values
            });
        }
        option.legend.data = accounts;
        option.xAxis.data = periods;
        option.series = series;
        reportChart.setOption(option);
    }
    //..
    function getSgykCircleData(yearMonthBegin, yearMonthEnd, account) {
        var resultDate = {};
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "account": account};
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/sgyk/sgykCycleReportQuery.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultDate = returnData;
            }
        })
        return resultDate;
    }

    var option = {
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'axis'
        },
        //科目-标题
        legend: {
            type: 'scroll',
            data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
        },
        grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        //x轴-周期
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        //y轴值
        yAxis: {
            type: 'value'
        },
        //每个科目渲染
        series: [
            {
                name: '邮件营销',
                type: 'line',
                data: [120, 132, 101, 134, 90, 230, 210]
            },
            {
                name: '联盟广告',
                type: 'line',
                data: [220, 182, 191, 234, 290, 330, 310]
            },
            {
                name: '视频广告',
                type: 'line',
                data: [150, 232, 201, 154, 190, 330, 410]
            },
            {
                name: '直接访问',
                type: 'line',
                data: [320, 332, 301, 334, 390, 330, 320]
            },
            {
                name: '搜索引擎',
                type: 'line',
                data: [820, 932, 901, 934, 1290, 1330, 1320]
            }
        ]
    };
</script>
</body>
</html>
