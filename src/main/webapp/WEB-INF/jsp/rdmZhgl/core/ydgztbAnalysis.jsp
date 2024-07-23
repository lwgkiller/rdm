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
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echartsVersion47/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/mini/boot.js?static_res_version=${static_res_version}"></script>
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
            <li class="gs-w" data-col="1" data-row="10" data-sizex="6" data-sizey="4">
                <header>
                    <p>各部门月度提报提交统计</p>
                    <span style="float: right;	font-size: 16px;color: #333;vertical-align: middle">
						提报年月
						<input id="yearMonth" style="width: 105px;font-size: 10px;height: 30px"
                               onvaluechanged="ydtbBuildChanged()" onfocus="this.blur()" class="mini-monthpicker"
                               value=""/>
					</span>
                </header>
                <div id="ydtb" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var lastMonth = "${lastMonth}";
    //月度提报统计
    var ydtbSubmitChart = echarts.init(document.getElementById('ydtb'));

    function overview() {
        var totalWidth = document.body.clientWidth;
        if (window.parent.lastClickSysName && window.parent.lastClickSysName == '首页' && (!window.parent.lastMenuId || window.parent.lastMenuId == '')) {
            totalWidth = document.body.clientWidth - 220;
        }
        var width = (totalWidth - 3 * 8) / 2;
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
        //默认显示当前月
        var nowDate=new Date();
        var nowYear=nowDate.getFullYear();
        var nowMonth = nowDate.getMonth()+1;
        var currentYearMonth = nowYear + "-" + padMonth(nowMonth);
        mini.get("yearMonth").setValue(currentYearMonth);
        queryYdtb();
    });

    // 监听图例变化
    ydtbSubmitChart.on('legendselectchanged', function (selecteds) {
        var selectObject = selecteds.selected;
        var currentChoice = [];
        for (var key in selectObject) {
            var choice = selectObject[key];
            if (selectObject.hasOwnProperty(key) && choice) {
                currentChoice.push(key);
            }
        }
        var currentTotal = [];
        for (var seriesName of currentChoice) {
            for (var i=0; i<ydtbSubmitOption.series.length;i++) {
                var seriesObject = ydtbSubmitOption.series[i];
                if (seriesName == seriesObject.name && seriesObject.data) {
                    sumTotal(currentTotal,seriesObject.data)
                }
            }
        }
        sumData.data=currentTotal;
        ydtbSubmitOption.series.remove(sumData);
        ydtbSubmitOption.series.push(sumData);
        ydtbSubmitChart.setOption(ydtbSubmitOption);
    });
    function sumTotal(currentTotal,seriesData) {
        for (var i = 0; i < seriesData.length; i++) {
            if (currentTotal.length != seriesData.length) {
                currentTotal.push(seriesData[i]);
            } else {
                currentTotal[i] = currentTotal[i] + seriesData[i];
            }
        }
    }
    // 指定图表的配置项和数据
    var ydtbSubmitOption = {
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter:function (params) {
                var res='<p>'+params[0].name+'</p>';
                for(var i=params.length-2;i>=0;i--){
                    res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res+=params[i].seriesName+'：'+params[i].data;
                    var percent=0;
                    if(params[i].data &&params[i].data!=0) {
                        percent=params[i].data*100/sumData.data[params[i].dataIndex];
                    }
                    percent=percent.toFixed(2);
                    res+='（'+percent+'%）</p>';
                }
                res+='<p style="margin-left: 12px">'+params[params.length-1].seriesName+'：'+params[params.length-1].data+'</p>';
                return res;
            }
        },
        color:['rgba(75,246,134,0.94)','rgba(238,235,52,0.75)','rgb(155,154,154)'],
        legend: {
            data:[]
        },
        grid: {
            left: '3.5%',
            right: '2%',
            top:'8%',
            bottom: '2%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                data : [],
                axisLabel: {
                    show: true,
                    interval:0,
                    rotate:0,
                    color: '#333',
                    fontSize:16,
                    formatter: function(params){
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
        ],
        yAxis : [
            {
                type : 'value',
                name:'数\n量\n︵\n条\n︶',
                nameLocation :'center',
                nameGap:25,
                nameRotate:0
            }
        ],
        series : []
    };

    var sumData={
        name: '总计',
        type: 'bar',
        stack: '数量',
        label: {
            normal: {
                // offset:['50', '80'],
                show: true,
                position: 'insideBottom',
                formatter:'{c}',
                textStyle:{ color:'#000' }
            }
        },
        itemStyle:{
            normal:{
                color:'rgba(128, 128, 128, 0)'
            }
        },
        data: []
    };

    function queryYdtb() {
        var postData={};
        if(mini.get("yearMonth").getValue()) {
            postData.yearMonth=mini.get("yearMonth").getValue();
        }
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/ydgztb/queryTBSituation.do',
            type: 'POST',
            data:mini.encode(postData),
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData) {
                    //对option进行赋值
                    sumData.data=returnData.sumData;
                    ydtbSubmitOption.legend.data=returnData.levelList;
                    ydtbSubmitOption.xAxis[0].data=returnData.categoryList;
                    ydtbSubmitOption.series=returnData.series;
                    ydtbSubmitOption.series.push(sumData);
                    ydtbSubmitChart.setOption(ydtbSubmitOption);
                }
            }
        });
    }
    function ydtbBuildChanged() {
        queryYdtb();
    }
    function padMonth(month) {
        return month < 10 ? '0' + month : String(month);
    }


</script>
</body>
</html>
