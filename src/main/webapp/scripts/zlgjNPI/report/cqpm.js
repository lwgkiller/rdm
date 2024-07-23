$(function () {
    cqpm();
});

function cqpm() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let startTime = (nowYear) + "-01";
    mini.get("yyTimeFrom").setValue(startTime);
    queryCqChartData()
}
// 指定图表的配置项和数据
var cqpmBarChartOption = {
        title: {
            show: false,
            text:'暂无数据',
            textStyle: {
                lineHeight: 80,
                height: 80,
            },
            textAlign: 'auto',
            textVerticalAlign: 'top'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function (params) {
                let res = '<p>部门：' + params[0].name + '</p>';
                for (let i = params.length-2; i>=0; i--) {
                    var percent = 0;
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    if (params[i].seriesType === 'line') {
                        res += params[i].seriesName + '：' + params[i].data + '%';
                    } else {
                        res += params[i].seriesName + '：' + params[i].data;
                        if (params[i].data && params[i].data != 0) {
                            percent = params[i].data * 100 / cqSumData.data[params[i].dataIndex];
                        }
                    }
                    percent = percent.toFixed(2);
                    res += '（' + percent + '%）</p>';
                }
                res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
                return res;
            }
        },
        color: ['#07D3CC','#1d8404','darkorange','#D91520'],
        legend: {
            data: []
        },
        grid: {
            left: '3.5%',
            right: '3.5%',
            top: '15%',
            bottom: '2%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: [],
                axisLabel: {
                    show: true,
                    interval: 0,
                    rotate: 0,
                    color: '#333',
                    fontSize: 10,
                    formatter: function (params) {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 2;
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
        yAxis: [
            {
                type: 'value',
                name: '超\n期\n数\n量',
                nameLocation: 'center',
                nameGap: 35,
                nameRotate: 0,
                position: 'left',
                minInterval:1
            }
        ],
        series: []
    };
var cqSumData = {
    name: '总计',
    type: 'bar',
    stack: '数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: '{c}',
            textStyle: {color: '#000'}
        }
    },
    itemStyle: {
        normal: {
            color: 'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};

function queryCqChartData() {
    var startTime=mini.get("yyTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择查询月份");
        return;
    }
    var wtlxCq = '';
    if (mini.get("wtlxCq").getValue()) {
        wtlxCq = mini.get("wtlxCq").getValue();
    }
    var czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/queryZrrTime.do?startTime='+startTime + '&czxpj=' + czxpj+ '&wtlxCq=' + wtlxCq,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                cqpmBar.clear();
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                cqSumData.data = returnData.finishData;
                cqpmBarChartOption.legend.data = returnData.legendData;
                cqpmBarChartOption.xAxis[0].data = returnData.xAxisData;
                cqpmBarChartOption.series = returnData.series;
                cqpmBarChartOption.series.push(cqSumData);
                cqpmBar.clear();
                cqpmBar.setOption(cqpmBarChartOption);
            }
        }
    });
}

function queryTimeChangeChartData() {
    queryCqChartData();
}



