$(function () {
    queryYwph();
});

var delayRate = [];
// 指定图表的配置项和数据
var ywphOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter:function (params) {
            var res='<p>部门：'+params[0].name+'</p>';
            for(var i=params.length-1;i>=0;i--){
                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res+=params[i].seriesName+'：'+Math.abs(params[i].data)+'（个）</p>';
            }
            var delayRateVal=delayRate[params[0].dataIndex]*100;
            delayRateVal=delayRateVal.toFixed(2);
            res+='<p style="margin-left: 12px">延误率：'+delayRateVal+'%</p>';
            return res;
        }
    },
    color:['#1d8404','#CD3333'],
    legend: {
        data: ['进度延误', '进度正常']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
        top:'8%',
        left:'left'
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
                margin:20,
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
            barWidth:20,
            label: {
                normal: {
                    show: true,
                    position: 'right',
                    formatter:function (params) {
                        var value=params.value;
                        if(value==0) {
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
            barWidth:20,
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

function queryYwph() {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/report/xcmgProject/queryYwph.do',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                //对option进行赋值
                ywphOption.yAxis[0].data=returnData.yAxis;
                ywphOption.series[0].data=returnData.processOk;
                ywphOption.series[1].data=returnData.processDelay;
                delayRate=returnData.delayRate;
                ywphChart.setOption(ywphOption);
            }
        }
    });
}
