// 指定图表的配置项和数据
ljjblChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            crossStyle: {
                color: '#999'
            }
        }
    },
    toolbox: {
    },
    legend: {
        data: ['预计降本率（%）', '实际累计降本率（%）', '单台材料成本偏差（元）']
    },
    xAxis: [
        {
            type: 'category',
            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
            axisPointer: {
                type: 'shadow'
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '降本率',
            axisLabel: {
                formatter: '{value} %'
            }
        },
        {
            type: 'value',
            name: '单台材料成本偏差',
            axisLabel: {
                formatter: '{value} 元'
            }
        }
    ],
    series: [
        {
            name: '预计降本率（%）',
            type: 'bar',
            data: [
                2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
            ]
        },
        {
            name: '实际累计降本率（%）',
            type: 'bar',
            data: [
                2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
            ]
        },
        {
            name: '单台材料成本偏差（元）',
            type: 'line',
            yAxisIndex: 1,
            data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
        }
    ]
};

function initLjjblChart() {
    var reportYear = mini.get('reportYear').getValue();
    var reportRule = mini.get('reportRule').getValue();
    if (!reportYear) {
        mini.alert("请选择统计年份");
        return
    }
    if (!reportRule) {
        mini.alert("请选择统计规则");
        return
    }
    var resultData = getLjjblData(reportYear, reportRule);
    var ycjbl = resultData[8];
    var sjjbl = resultData[9];
    var yjdtcb = resultData[6];
    var sjdtcb = resultData[7];
    ljjblChartOption.series[0].data = [dealData(ycjbl.january),dealData(ycjbl.february),dealData(ycjbl.march),dealData(ycjbl.april),dealData(ycjbl.may),dealData(ycjbl.june),dealData(ycjbl.july)
                                        ,dealData(ycjbl.august),dealData(ycjbl.september),dealData(ycjbl.october),dealData(ycjbl.november),dealData(ycjbl.december)];
    ljjblChartOption.series[1].data = [dealData(sjjbl.january),dealData(sjjbl.february),dealData(sjjbl.march),dealData(sjjbl.april),dealData(sjjbl.may),dealData(sjjbl.june),dealData(sjjbl.july)
        ,dealData(sjjbl.august),dealData(sjjbl.september),dealData(sjjbl.october),dealData(sjjbl.november),dealData(sjjbl.december)];
    ljjblChartOption.series[2].data = [dealProductData(yjdtcb.january,sjdtcb.january),dealProductData(yjdtcb.february,sjdtcb.february),dealProductData(yjdtcb.march,sjdtcb.march),
        dealProductData(yjdtcb.april,sjdtcb.april),dealProductData(yjdtcb.may,sjdtcb.may),dealProductData(yjdtcb.june,sjdtcb.june),dealProductData(yjdtcb.july,sjdtcb.july)
        ,dealProductData(yjdtcb.august,sjdtcb.august),dealProductData(yjdtcb.september,sjdtcb.september),dealProductData(yjdtcb.october,sjdtcb.october),
        dealProductData(yjdtcb.november,sjdtcb.november),dealProductData(yjdtcb.december,sjdtcb.december)]
    ljjblChart.setOption(ljjblChartOption);
}
function dealData(ratio) {
    if(ratio){
        ratio.replace
        return ratio.toString().replace('%','');
    }else{
        return 0;
    }
}
function dealProductData(planNum,actualNum) {
    if(!planNum){
        planNum = 0;
    }
    if(!actualNum){
        actualNum = 0;
    }
    return (planNum-actualNum).toFixed(2);
}


function getLjjblData(reportYear, reportRule) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/reportFigureData.do?reportYear=' + reportYear + '&reportRule=' + reportRule,
        type: 'POST',
        async: false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                resultDate = returnData.data;
            } else {
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}
