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
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="internal()">内部</a>
                        <a class="mini-button" onclick="external()">外部</a>
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
                <div id="byDepartmentChart" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                </header>
                <div id="byModelChart" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var byDepartmentChart = echarts.init(document.getElementById('byDepartmentChart'));
    var byModelChart = echarts.init(document.getElementById('byModelChart'));
    var currentYear = "";
    var currentMonth = "";
    var totalCount;
    var flag = "内部";
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
        if ((date.getMonth() + 1).toString().length == 1) {
            currentMonth = "0" + (date.getMonth() + 1).toString();
        } else {
            currentMonth = (date.getMonth() + 1).toString();
        }
        currentYear = date.getFullYear().toString();
        mini.get("yearMonthBegin").setValue(currentYear + "-01");
        mini.get("yearMonthEnd").setValue(currentYear + "-" + currentMonth);
        iniInternalTotal();
        iniInternalByDepartmentChart();
        iniInternalByModelChart();
    })

    //..外部按钮
    function internal() {
        flag = "内部"
        iniInternalTotal();
        iniInternalByDepartmentChart();
        iniInternalByModelChart();
    }
    //..内部按钮
    function external() {
        flag = "外部"
        iniExternalTotal();
        iniExternalByDepartmentChart();
        iniExternalByModelChart();
    }
    //初始化内部会议总数
    function iniInternalTotal() {
        var postData = {
            "yearMonthBegin": mini.get("yearMonthBegin").getText() + "-01",
            "yearMonthEnd": mini.get("yearMonthEnd").getText() + "-31"
        };
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/hyglGeneralKanban/getInternalTotal.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                totalCount = total;
            }
        })
    }
    //初始化外部会议总数
    function iniExternalTotal() {
        var postData = {
            "yearMonthBegin": mini.get("yearMonthBegin").getText() + "-01",
            "yearMonthEnd": mini.get("yearMonthEnd").getText() + "-31"
        };
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/hyglGeneralKanban/getExternalTotal.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                totalCount = total;
            }
        })
    }
    //获取看板数据
    function getKanbanData(yearMonthBegin, yearMonthEnd, action) {
        var resultData = [];
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "action": action};
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/hyglGeneralKanban/getKanbanData.do',
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
    //2--按部门统计
    //初始化内部会议部门柱状图
    function iniInternalByDepartmentChart() {
        byDepartmentChart.clear();
        optionByDepartment.title.text = '内部会议按部门统计      总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "internalByDepartment");
        optionByDepartment.dataset.source = resultData;
        byDepartmentChart.setOption(optionByDepartment);
    }
    //初始化外部会议部门柱状图
    function iniExternalByDepartmentChart() {
        byDepartmentChart.clear();
        optionByDepartment.title.text = '外部会议按部门统计      总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "externalByDepartment");
        optionByDepartment.dataset.source = resultData;
        byDepartmentChart.setOption(optionByDepartment);
    }
    //部门柱状图容器
    var optionByDepartment = {
        title: {text: ''},
        legend: {
            right: '15%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['部门', '数量',],
            source: [
                {日期: '111', 数量: 111}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                rotate: '25'
            }
        },
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
            }
        ]
    };
    byDepartmentChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (byDepartmentChart.containPixel('grid', pointInPixel)) {
            var xIndex = byDepartmentChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
            var option = byDepartmentChart.getOption();
            var clickDepartment = option.dataset[0].source[xIndex].部门;
            var yearMonthBegin = '';
            var yearMonthEnd = '';
            if (mini.get("yearMonthBegin").getValue()) {
                yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
            }
            if (mini.get("yearMonthEnd").getValue()) {
                yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
            }
            var url;
            if (flag == "内部") {
                url = jsUseCtxPath + "/zhgl/core/hyglInternal/dataListPage.do?clickDepartment=" + clickDepartment +
                    "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd + "&recordStatus=已提交";
            } else if (flag = "外部") {
                url = jsUseCtxPath + "/jsjl/core/dataListPage.do?clickDepartment=" + clickDepartment +
                    "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd + "&recordStatus=已提交";
            }

            window.open(url);
        }
    });
    //2--end
    //3--按模块统计
    //初始化内部会议模块柱状图
    function iniInternalByModelChart() {
        byModelChart.clear();
        optionByModel.title.text = '内部会议按模块统计      总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "internalByModel");
        optionByModel.dataset.source = resultData;
        byModelChart.setOption(optionByModel);
    }
    //初始化外部会议模块柱状图
    function iniExternalByModelChart() {
        byModelChart.clear();
        optionByModel.title.text = '外部会议按模块统计      总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "externalByModel");
        optionByModel.dataset.source = resultData;
        byModelChart.setOption(optionByModel);
    }
    //模块柱状图容器
    var optionByModel = {
        title: {text: ''},
        legend: {
            right: '15%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['模块', '数量',],
            source: [
                {日期: '111', 数量: 111}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                rotate: '15'
            }
        },
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
            }
        ]
    };
    byModelChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (byModelChart.containPixel('grid', pointInPixel)) {
            var xIndex = byModelChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
            var option = byModelChart.getOption();
            var clickModel = option.dataset[0].source[xIndex].模块;
            var yearMonthBegin = '';
            var yearMonthEnd = '';
            if (mini.get("yearMonthBegin").getValue()) {
                yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
            }
            if (mini.get("yearMonthEnd").getValue()) {
                yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
            }
            var url;
            if (flag == "内部") {
                url = jsUseCtxPath + "/zhgl/core/hyglInternal/dataListPage.do?clickModel=" + clickModel +
                    "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd + "&recordStatus=已提交";
            } else if (flag = "外部") {
                url = jsUseCtxPath + "/jsjl/core/dataListPage.do?clickModel=" + clickModel +
                    "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd + "&recordStatus=已提交";
            }
            window.open(url);
        }
    });
    //3--end
</script>
</body>
</html>
