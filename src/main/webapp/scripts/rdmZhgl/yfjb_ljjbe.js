// 指定图表的配置项和数据
ljjbeChartOption = {
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
        data: ['预计将本总额（万元）', '实际累计将本额（万元）', '产量偏差（台）']
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
            name: '将本额',
            axisLabel: {
                formatter: '{value} 万元'
            }
        },
        {
            type: 'value',
            name: '产量偏差',
            axisLabel: {
                formatter: '{value} 台'
            }
        }
    ],
    series: [
        {
            name: '预计将本总额（万元）',
            type: 'bar',
            data: [
                2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
            ]
        },
        {
            name: '实际累计将本额（万元）',
            type: 'bar',
            data: [
                2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
            ]
        },
        {
            name: '产量偏差（台）',
            type: 'line',
            yAxisIndex: 1,
            data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
        }
    ]
};

function initLjjbeChart() {
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
    var resultData = getLjjbeData(reportYear, reportRule);
    var ycjb = resultData[0];
    var sjjb = resultData[1];
    var clys = resultData[2];
    var sjcl = resultData[3];
    ljjbeChartOption.series[0].data = [dealData(ycjb.january),dealData(ycjb.february),dealData(ycjb.march),dealData(ycjb.april),dealData(ycjb.may),dealData(ycjb.june),dealData(ycjb.july)
                                        ,dealData(ycjb.august),dealData(ycjb.september),dealData(ycjb.october),dealData(ycjb.november),dealData(ycjb.december)];
    ljjbeChartOption.series[1].data = [dealData(sjjb.january),dealData(sjjb.february),dealData(sjjb.march),dealData(sjjb.april),dealData(sjjb.may),dealData(sjjb.june),dealData(sjjb.july)
        ,dealData(sjjb.august),dealData(sjjb.september),dealData(sjjb.october),dealData(sjjb.november),dealData(sjjb.december)];
    ljjbeChartOption.series[2].data = [dealProductData(clys.january,sjcl.january),dealProductData(clys.february,sjcl.february),dealProductData(clys.march,sjcl.march),
        dealProductData(clys.april,sjcl.april),dealProductData(clys.may,sjcl.may),dealProductData(clys.june,sjcl.june),dealProductData(clys.july,sjcl.july)
        ,dealProductData(clys.august,sjcl.august),dealProductData(clys.september,sjcl.september),dealProductData(clys.october,sjcl.october),
        dealProductData(clys.november,sjcl.november),dealProductData(clys.december,sjcl.december)]
    ljjbeChart.setOption(ljjbeChartOption);
}
function dealData(totalMoney) {
    if(totalMoney){
        return (totalMoney/ 10000).toFixed(2);
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
    return planNum-actualNum;
}


function getLjjbeData(reportYear, reportRule) {
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
