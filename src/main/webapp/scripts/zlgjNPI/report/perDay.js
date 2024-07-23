$(function () {
    perDayInit();
});

function perDayInit() {
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let endTime = nowYear + "-" + month + "-" + day;
    let nowDate2 = new Date().sub(30);
    let nowYear2 = nowDate2.getFullYear();
    let month2 = nowDate2.getMonth() + 1;
    let day2 = nowDate2.getDate();
    let startTime = nowYear2 + "-" + month2 + "-" + day2;
    mini.get("perDayFrom").setValue(startTime);
    mini.get("perDayTo").setValue(endTime);
    queryPerDayChartData()
}

//指定图表的配置项和数据
var perDayChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {// 坐标轴指示器，坐标轴触发有效
            type: 'shadow'// 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            var res = '<p>日期：' + params[0].name + '</p>';
            res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '%</p>';
            for (var i = params.length - 2; i >= 0; i--) {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data;
                    var percent = 0;
                    if (params[i].data && params[i].data != 0) {
                        percent = params[i].data * 100 / sumChartData.data[params[i].dataIndex];
                    }
                    percent = percent.toFixed(2);
                    res += '（' + percent + '%）</p>';
            }
            return res;
        }
    },
    color: ['#1d8404','darkorange', 'darkred'],
    legend: {
        data: ["已完成",'进行中', "完成率"]
    },
    grid: {
        left: '3.5%',
        right: '2%',
        top: '8%',
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
    yAxis: [
        {
            type: 'value',
            name: '总\n计\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        }

    ],
    series: []
};

var sumChartData = {
    name: '总计',
    type: 'bar',
    data: []
};

var runChartData = {
    name: '进行中',
    type: 'bar',
    stack: '数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'top',
            formatter: function (params) {
                return sumChartData.data[params.dataIndex];
            },
            textStyle: {color: '#000'}
        }
    },
    data: []
};

var endChartData = {
    name: '已完成',
    type: 'bar',
    stack: '数量',
    data: []
};


var percentChartData = {
    name: '完成率',
    type: 'line',
    data: []
};


function queryPerDayChartData() {
    var postData = {};
    if (!mini.get("perDayFrom").getText()) {
        mini.alert("请选择开始时间！");
        return;
    }
    if (!mini.get("perDayTo").getText()) {
        mini.alert("请选择结束时间！");
        return;
    }
    //操作性评价
    postData.czxpj = mini.get("czxpj").getValue();
    postData.startTime = mini.get("perDayFrom").getText();
    postData.endTime = mini.get("perDayTo").getText() + ' 23:59:59';
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/perDayChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                perDayChartOption.series=[];
                perDayChartOption.xAxis[0].data = returnData.xAxisData;
                sumChartData.data = returnData.sumData;

                endChartData.data = returnData.endData;
                perDayChartOption.series.push(endChartData);

                runChartData.data = returnData.runData;
                perDayChartOption.series.push(runChartData);

                percentChartData.data = returnData.percentData;
                perDayChartOption.series.push(percentChartData);

                perDayChart.setOption(perDayChartOption);
            }
        }
    });
}

function perDayFromChanged() {
    queryPerDayChartData();
}

function perDayToChanged() {
    queryPerDayChartData();
}

