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
                <div id="productInstituteChart" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
    <div class="gridster">
        <ul>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <div id="tonnageRangeChart" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var productInstituteChart = echarts.init(document.getElementById('productInstituteChart'));
    var tonnageRangeChart = echarts.init(document.getElementById('tonnageRangeChart'));

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
        iniProductInstituteChart();
        iniTonnageRangeChart();
    })

    //初始化产品所视图柱状图
    function iniProductInstituteChart() {
        productInstituteChart.clear();
        var resultData = getTotalprogressKanbanData("productInstitute");
        optionProductInstitute.dataset.source = resultData;
        productInstituteChart.setOption(optionProductInstitute);
    }
    //产品所柱状图容器
    var optionProductInstitute = {
        title: {text: '产品所转图工作计划完成情况'},
        legend: {
            right: '15%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>序时进度：' + (params.data.序时进度 * 100).toFixed(2) + '%</p>';
                res += '<p>实际进度：' + (params.data.实际进度 * 100).toFixed(2) + '%</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['产品所', '序时进度', '实际进度'],
            source: [
                {产品所: '小挖所', 序时进度: 0.1, 实际进度: 0.5},
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
                    position: 'top',
                    formatter: function (params) {
                        var res = (params.data.序时进度 * 100).toFixed(2) + '%';
                        return res;
                    }
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#2f4554',
                label: {
                    show: true,
                    position: 'top',
                    formatter: function (params) {
                        var res = (params.data.实际进度 * 100).toFixed(2) + '%';
                        return res;
                    }
                }
            }
        ]
    };

    //初始化吨位段视图柱状图
    function iniTonnageRangeChart() {
        tonnageRangeChart.clear();
        var resultData = getTotalprogressKanbanData("tonnageRange");
        optionTonnageRange.dataset.source = resultData;
        tonnageRangeChart.setOption(optionTonnageRange);
    }
    //吨位段柱状图容器
    var optionTonnageRange = {
        title: {text: '吨位段转图工作计划完成情况'},
        legend: {
            right: '15%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>序时进度：' + (params.data.序时进度 * 100).toFixed(2) + '%</p>';
                res += '<p>实际进度：' + (params.data.实际进度 * 100).toFixed(2) + '%</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['吨位段', '序时进度', '实际进度'],
            source: [
                {产品所: '1.5吨-3.5吨', 序时进度: 0.1, 实际进度: 0.5},
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
                    position: 'top',
                    formatter: function (params) {
                        var res = (params.data.序时进度 * 100).toFixed(2) + '%';
                        return res;
                    }
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#2f4554',
                label: {
                    show: true,
                    position: 'top',
                    formatter: function (params) {
                        var res = (params.data.实际进度 * 100).toFixed(2) + '%';
                        return res;
                    }
                }
            }
        ]
    };

    //获取看板数据
    function getTotalprogressKanbanData(action) {
        var resultData = [];
        var postData = {"action": action};
        $.ajax({
            url: jsUseCtxPath + '/digitization/core/datamigration/totalprogress/getTotalprogressKanbanData.do',
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
</script>
</body>
</html>
