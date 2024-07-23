$(function () {
    leftTwoInit();
});

function leftTwoInit() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startMonth = month - 4;
    if (startMonth < 0) {
        startMonth = 1;
    }
    let startTime = (nowYear) + "-" + startMonth + "-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("leftTwoBuildFrom").setValue(startTime);
    mini.get("leftTwoBuildTo").setValue(endTime);
    queryLeftTwoChartData()
}

// 指定图表的配置项和数据
var leftTwoChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            var res = '<p>月份：' + params[0].name + '</p>';
            for (var i = params.length - 2; i >= 0; i--) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
                var percent = 0;
                if (params[i].data && params[i].data != 0) {
                    percent = params[i].data * 100 / leftTwoSumData.data[params[i].dataIndex];
                }
                percent = percent.toFixed(2);
                res += '（' + percent + '%）</p>';
            }
            res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
            return res;
        }
    },
    color: ['#1d8404', 'darkorange'],
    legend: {
        data: ["已完成", "进行中"]
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        }
    ],
    series: []
};

var leftTwoSumData = {
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

function queryLeftTwoChartData() {
    var postData = {};
    if (!mini.get("leftTwoBuildFrom").getText()) {
        mini.alert("请选择开始时间！");
        return;
    }
    if (!mini.get("leftTwoBuildTo").getText()) {
        mini.alert("请选择结束时间！");
        return;
    }
    postData.startTime = mini.get("leftTwoBuildFrom").getText().substr(0,7);
    postData.endTime = mini.get("leftTwoBuildTo").getText().substr(0,7);
    postData.wtlx = "SCWT";
    postData.czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/WTWidgetChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {

                //对option进行赋值
                leftTwoSumData.data = returnData.sumData;
                leftTwoChartOption.xAxis[0].data = returnData.xAxisData;
                leftTwoChartOption.series = returnData.series;
                leftTwoChartOption.series.push(leftTwoSumData);
                leftTwoChart.setOption(leftTwoChartOption);
            }
        }
    });
}

function leftTwoBuildFromChanged() {
    queryLeftTwoChartData();
}

function leftTwoBuildToChanged() {
    queryLeftTwoChartData();
}

