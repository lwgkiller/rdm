$(function () {
    inventJsjdsSubmitNumBarInit();
});

function inventJsjdsSubmitNumBarInit() {
    //今年1月1号到当前日期
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime =nowYear + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("inventJsjdsSubmitNumBarBuildFrom").setValue(startTime);
    mini.get("inventJsjdsSubmitNumBarBuildTo").setValue(endTime);
    queryInventJsjdsSubmitNumBarChartData()
}

// 指定图表的配置项和数据
var inventJsjdsSubmitNumBarChartOption = {
    title: {
        show: true,
        text: new Date().getFullYear() + '年各部门提交发明类技术交底书数量',
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
            for (let i = 0; i< params.length; i++) {
                if (params[i].seriesType === 'line') {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data + '%';
                } else {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data;
                }
                // var percent = 0;
                // if (params[i].data && params[i].data != 0) {
                //     percent = params[i].data * 100 / inventJsjdsSubmitNumBarSumData.data[params[i].dataIndex];
                // }
                // percent = percent.toFixed(2);
                // res += '（' + percent + '%）</p>';
            }
            // res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
            return res;
        }
    },
    color: ['#2fc27a', '#ea6a6a','#2fc27a'],
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
            name: '交\n底\n发\n明\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left'
        },
        {
            type: 'value',
            name: '发\n明\n完\n成\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'right',
            axisLabel: {
                formatter: '{value}%'
            }

        }
    ],
    series: []
};

function queryInventJsjdsSubmitNumBarChartData() {
    var postData = {};
    if (mini.get("inventJsjdsSubmitNumBarBuildFrom").getText()) {
        postData.startTime = mini.get("inventJsjdsSubmitNumBarBuildFrom").getText();
    }
    if (mini.get("inventJsjdsSubmitNumBarBuildTo").getText()) {
        postData.endTime = mini.get("inventJsjdsSubmitNumBarBuildTo").getText();
    }
    postData.zllx = 'FM';
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jsjds/report/queryInventJsjdsByDept.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                inventJsjdsSubmitNumBarChartOption.legend.data = returnData.legendData;
                inventJsjdsSubmitNumBarChartOption.xAxis[0].data = returnData.xAxisData;
                inventJsjdsSubmitNumBarChartOption.series = returnData.series;
                sumData.data=returnData.totalData;
                inventJsjdsSubmitNumBarChartOption.series.push(sumData);
                inventJsjdsSubmitNumBarChart.setOption(inventJsjdsSubmitNumBarChartOption);
            }
        }
    });
}

function inventJsjdsSubmitNumBarBuildFromChanged() {
    queryInventJsjdsSubmitNumBarChartData();
}

function inventJsjdsSubmitNumBarBuildToChanged() {
    queryInventJsjdsSubmitNumBarChartData();
}
var sumData={
    name: '总数',
    type: 'bar',
    stack: 'bar1',
    /*label: {
        normal: {
            offset:['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter:'{c}',
            textStyle:{ color:'#000' }
        }
    },*/
    itemStyle:{
        normal:{
            color:'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};

