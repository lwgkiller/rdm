$(function () {
    patentApplyBarInit();
});

function patentApplyBarInit() {
    //今年1月1号到今年12月31号
    // let nowDate = new Date();
    // let nowYear = nowDate.getFullYear();
    // let month = nowDate.getMonth() + 1;
    // let day = nowDate.getDate();
    // let startMonth = month - 4;
    // if (startMonth < 0) {
    //     startMonth = 1;
    // }
    // let startTime = (nowYear) + "-" + startMonth + "-01";
    // let endTime = nowYear + "-" + month + "-" + day;
    // mini.get("patentApplyBarBuildFrom").setValue(nowYear);
    // mini.get("patentApplyBarBuildTo").setValue(endTime);
    queryPatentApplyBarChartData()
}

// 指定图表的配置项和数据
var patentApplyBarChartOption = {
    title: {
        show: true,
        text: new Date().getFullYear() + '年中国专利申请量',
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
            var res = '<p>月份：' + params[0].name + '</p>';
            for (var i = params.length-1; i >= 0; i--) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
                // var percent = 0;
                // if (params[i].data && params[i].data != 0) {
                //     percent = params[i].data * 100 / patentApplyBarSumData.data[params[i].dataIndex];
                // }
                // percent = percent.toFixed(2);
                // res += '（' + percent + '%）</p>';
            }
            // res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
            return res;
        }
    },
    color: ['#c23531', '#61a0a8', '#2f4554', '#d48265'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '2%',
        top: '15%',
        // bottom: '2%',
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0
        }
    ],
    series: []
};

function queryPatentApplyBarChartData() {
    var postData = {};
    postData.year = new Date().getFullYear();
    // if (mini.get("patentApplyBarBuildFrom").getValue()) {
    //     postData.year = mini.get("patentApplyBarBuildFrom").getValue();
    // }
    // if (mini.get("patentApplyBarBuildTo").getValue()) {
    //     postData.endTime = mini.get("patentApplyBarBuildTo").getValue();
    // }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ZLAndFW/report/patenApplyBarChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                patentApplyBarChartOption.legend.data = returnData.legendData;
                patentApplyBarChartOption.xAxis[0].data = returnData.xAxisData;
                patentApplyBarChartOption.series = returnData.series;
                patentApplyBarChart.setOption(patentApplyBarChartOption);
            }
        }
    });
}

function patentApplyBarBuildFromChanged() {
    queryPatentApplyBarChartData();
}

function patentApplyBarBuildToChanged() {
    queryPatentApplyBarChartData();
}

