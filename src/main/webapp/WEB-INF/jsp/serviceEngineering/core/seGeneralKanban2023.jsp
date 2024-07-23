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
    <%--<script src="${ctxPath}/scripts/serviceEngineering/seGeneralKanban2023.js?version=${static_res_version}" type="text/javascript"></script>--%>
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
            <%----%>
            <li class="gs-w" data-row="1" data-sizex="3" data-col="1" data-sizey="3">
                <header>
                    <p>装修手册个人指标</p>
                </header>
                <div id="zhuangXiuShouCeGeRenZhiBiaoChat" class="containerBox"></div>
            </li>
            <%----%>
            <li class="gs-w" data-row="1" data-sizex="3" data-col="4" data-sizey="3">
                <header>
                    <p>资料收集超期</p>
                </header>
                <div id="ziLiaoShouJiChaoQiChat" class="containerBox"></div>
            </li>
        </ul>
    </div>
</div>
<%--查看装修手册个人指标异常窗口--%>
<div id="zhuangXiuShouCeGeRenZhiBiaoExpWindow" title="查看异常" class="mini-window" style="width:1300px;height:550px;" multiSelect="true"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
    </div>
    <div class="mini-fit">
        <div id="zhuangXiuShouCeGeRenZhiBiaoExpGrid" class="mini-datagrid" style="width: 100%; height: 100%;" idField="id" showPager="false">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.decorationManualDemandList.name"/></div>
                <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.decorationManualDemandList.name1"/></div>
                <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.decorationManualDemandList.name2"/></div>
                <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.decorationManualDemandList.name3"/></div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render"><spring:message
                        code="page.decorationManualDemandEdit.name24"/>
                </div>
                <div field="manualType" width="100" headerAlign="center" align="center" renderer="render">手册类型
                </div>
                <div field="repUserId" displayField="repUser" width="100" align="center" headerAlign="center">负责人
                </div>
                <div field="accomplishTime" displayField="accomplishTime" width="100" align="center" headerAlign="center">完成时间
                </div>
                <div field="manualCode" width="100" headerAlign="center" align="center" renderer="render"><spring:message
                        code="page.decorationManualDemandEdit.name25"/></div>
                <div field="businessStatus" width="100" headerAlign="center" align="center" renderer="render"><spring:message
                        code="page.decorationManualDemandEdit.name26"/></div>
            </div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var seKanbanAdmin = "${seKanbanAdmin}";
    var currentUserNo = "${currentUserNo}";
    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 12,
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
        zhuangXiuShouCeGeRenZhiBiaoIni();
        ziLiaoShouJiChaoQiIni();
    })
    //..装修手册个人指标
    var zhuangXiuShouCeGeRenZhiBiaoChat = echarts.init(document.getElementById('zhuangXiuShouCeGeRenZhiBiaoChat'));
    zhuangXiuShouCeGeRenZhiBiaoChat.getZr().on('click', function (params) {
        debugger;
        var pointInPixel = [params.offsetX, params.offsetY];
        if (zhuangXiuShouCeGeRenZhiBiaoChat.containPixel('grid', pointInPixel)) {
            var yIndex = zhuangXiuShouCeGeRenZhiBiaoChat.convertFromPixel({seriesIndex: 0}, [params.offsetX, params.offsetY])[1];
            var option = zhuangXiuShouCeGeRenZhiBiaoChat.getOption();
            var repUserId = zhuangXiuShouCeGeRenZhiBiaoRepuserIds[yIndex];
            var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualDemand/getItemListExp.do?repUserId=" + repUserId;
            mini.get("zhuangXiuShouCeGeRenZhiBiaoExpWindow").show();
            mini.get("zhuangXiuShouCeGeRenZhiBiaoExpGrid").setUrl(url);
            mini.get("zhuangXiuShouCeGeRenZhiBiaoExpGrid").load();
        }
    });
    var zhuangXiuShouCeGeRenZhiBiaoRepuserIds = [];
    var zhuangXiuShouCeGeRenZhiBiaoDelayRate = [];
    var zhuangXiuShouCeGeRenZhiBiaoOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function (params) {
                var res = '<p>责任人：' + params[0].name + '</p>';
                for (var i = params.length - 1; i >= 0; i--) {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + Math.abs(params[i].data) + '（个）</p>';
                }
                var delayRateVal = zhuangXiuShouCeGeRenZhiBiaoDelayRate[params[0].dataIndex] * 100;
                delayRateVal = delayRateVal.toFixed(2);
                res += '<p style="margin-left: 12px">延误率：' + delayRateVal + '%</p>';
                return res;
            }
        },
        color: ['#1d8404', '#CD3333'],
        legend: {
            right: '10%',
            data: ['进度延误', '进度正常']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
            top: '8%',
            left: 'left'
        },
        xAxis: [
            {
                name: '数量（个）',
                nameLocation: 'center',
                nameGap: 20,
                nameRotate: 0,
                type: 'value',
                minInterval: 1,
                axisLabel: {
                    formatter: function (value) {
                        return Math.abs(value);
                    }
                }
            }
        ],
        yAxis: [
            {
                type: 'category',
                nameGap: 25,
                axisTick: {show: false},
                axisLabel: {
                    margin: 20,
                    show: true,
                    interval: 0
                },
                data: []
            }
        ],
        series: [
            {
                name: '进度正常',
                type: 'bar',
                stack: '总量',
                //barWidth: 20,
                label: {
                    normal: {
                        show: true,
                        position: 'right',
                        formatter: function (params) {
                            var value = params.value;
                            if (value == 0) {
                                return '';
                            }
                        }
                    }
                },
                data: []
            },
            {
                name: '进度延误',
                type: 'bar',
                stack: '总量',
                //barWidth: 20,
                label: {
                    normal: {
                        show: true,
                        position: 'left',
                        formatter: function (params) {
                            var value = params.value;
                            if (value != 0) {
                                return Math.abs(value);
                            } else {
                                return '';
                            }
                        }
                    }
                },
                data: []
            }
        ]
    };
    function zhuangXiuShouCeGeRenZhiBiaoIni() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban2023/zhuangXiuShouCeGeRenZhiBiaoIni.do',
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    zhuangXiuShouCeGeRenZhiBiaoOption.yAxis[0].data = resultData.yAxis;
                    zhuangXiuShouCeGeRenZhiBiaoOption.series[0].data = resultData.processOk;
                    zhuangXiuShouCeGeRenZhiBiaoOption.series[1].data = resultData.processDelay;
                    zhuangXiuShouCeGeRenZhiBiaoDelayRate = resultData.delayRate;
                    zhuangXiuShouCeGeRenZhiBiaoRepuserIds = resultData.repUserIds;
                    zhuangXiuShouCeGeRenZhiBiaoChat.setOption(zhuangXiuShouCeGeRenZhiBiaoOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }
    //..资料收集超期
    var ziLiaoShouJiChaoQiChat = echarts.init(document.getElementById('ziLiaoShouJiChaoQiChat'));
    ziLiaoShouJiChaoQiChat.on('click', function (params) {
        var expIds = ziLiaoShouJiChaoQiDepNameToExpId[params.name];
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/dataListPage.do?expIds=" + expIds;
        window.open(url);
    });
    var ziLiaoShouJiChaoQiDepNameToExpId;
    var ziLiaoShouJiChaoQiOption = {
        legend: {
            right: '10%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['部门', '超期数量'],
            source: [
                {部门: 'A', 超期数量: 10},
                {部门: 'B', 超期数量: 20},
                {部门: 'C', 超期数量: 10},
                {部门: 'D', 超期数量: 130}
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
                barWidth: '50%',
                label: {
                    show: true,
                    position: 'inside',
                }
            }
        ]
    };
    function ziLiaoShouJiChaoQiIni() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban2023/ziLiaoShouJiChaoQiIni.do',
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultData = returnData.data;
                    ziLiaoShouJiChaoQiOption.dataset = resultData;
                    ziLiaoShouJiChaoQiDepNameToExpId = resultData.depNameToExpId;
                    ziLiaoShouJiChaoQiChat.setOption(ziLiaoShouJiChaoQiOption);
                } else {
                    mini.alert("加载失败:" + returnData.message);
                }
            }
        });
    }

</script>
</body>
</html>
