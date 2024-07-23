
$(function () {
    responsibleDeptInit();
});

function responsibleDeptInit() {
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
    mini.get("responsibleDeptFrom").setValue(startTime);
    mini.get("responsibleDeptTo").setValue(endTime);
    mini.get("zrType").setValue("第一责任人");
    queryResponsibleDeptChartData()
}

// 指定图表的配置项和数据
var responsibleDeptChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            var res = '<p>部门：' + params[0].name + '</p>';
            for (var i = params.length - 2; i >= 0; i--) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
                var percent = 0;
                if (params[i].data && params[i].data != 0) {
                    percent = params[i].data * 100 / responsibleDeptSumData.data[params[i].dataIndex];
                }
                percent = percent.toFixed(2);
                res += '（' + percent + '%）</p>';
            }
            res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
            return res;
        }
    },
    color: [ '#1d8404','#08D721','darkorange'],
    legend: {
        data: [ "进行中","已完成(改进)","已完成(无需改进)"]
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
            nameGap: 25,
            nameRotate: 0
        }
    ],
    series: []
};

var responsibleDeptSumData = {
    name: '总计',
    type: 'bar',
    stack: '数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: function (param) {
                var result=param.data;
                var percent=0.00;
                if (param.data && param.data != 0) {
                    percent = (responsibleDeptChartOption.series[0].data[param.dataIndex]
                        +responsibleDeptChartOption.series[1].data[param.dataIndex]) * 100 / param.data;
                }
                percent = percent.toFixed(2);
                result += '\n(' + percent + '%)';
                return result;
            },
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

function queryResponsibleDeptChartData() {
    var postData = {};
    if (mini.get("responsibleDeptFrom").getText()) {
        postData.startTime = mini.get("responsibleDeptFrom").getText();
    }
    if (mini.get("responsibleDeptTo").getText()) {
        postData.endTime = mini.get("responsibleDeptTo").getText();
    }
    if (mini.get("wtlx").getValue()) {
        postData.wtlx = mini.get("wtlx").getValue();
    }
    if (mini.get("zrType").getValue()) {
        postData.zrType = mini.get("zrType").getValue();
    }
    postData.czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/responsibleDept.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                responsibleDeptSumData.data = returnData.sumData;
                responsibleDeptChartOption.xAxis[0].data = returnData.xAxisData;
                responsibleDeptChartOption.series = returnData.series;
                responsibleDeptChartOption.series.push(responsibleDeptSumData);
                responsibleDeptChart.setOption(responsibleDeptChartOption);
            }
        }
    });
}

function responsibleDeptBuildFromChanged() {
    queryResponsibleDeptChartData();
}

function responsibleDeptBuildToChanged() {
    queryResponsibleDeptChartData();
}
function responlxChanged() {
    queryResponsibleDeptChartData();
}

