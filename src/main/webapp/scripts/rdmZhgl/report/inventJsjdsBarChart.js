$(function () {
    inventJsjdsBarInit();
});

function inventJsjdsBarInit() {
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
    // mini.get("approvedJsjdsBarBuildFrom").setValue(nowYear);
    // mini.get("approvedJsjdsBarBuildTo").setValue(endTime);
    queryInventJsjdsBarChartData()
}

// 指定图表的配置项和数据
var inventJsjdsBarChartOption = {
    title: {
        show: true,
        text: new Date().getFullYear() + '年发明类技术交底书数量',
        subtext: '年度计划: *个, 进度: *%',
        subtextStyle: {
            color: 'black',
            fontSize: 14
        },
        // left: 'center',
        itemGap: 0
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
                //     percent = params[i].data * 100 / approvedJsjdsBarSumData.data[params[i].dataIndex];
                // }
                // percent = percent.toFixed(2);
                // res += '（' + percent + '%）</p>';
            }
            // res += '<p style="margin-left: 12px">' + params[params.length - 1].seriesName + '：' + params[params.length - 1].data + '</p>';
            return res;
        }
    },
    color: ['#c23531', '#2f4554', '#61a0a8', '#d48265'],
    legend: {
        top: '13%',
        data: []
    },
    grid: {
        left: '3.5%',
        right: '2%',
        top: '20%',
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
            nameGap: 35,
            nameRotate: 0
        }
    ],
    series: []
};

function queryInventJsjdsBarChartData() {
    var postData = {};
    // postData.year = new Date().getFullYear();
    // if (mini.get("approvedJsjdsBarBuildFrom").getValue()) {
    //     postData.year = mini.get("approvedJsjdsBarBuildFrom").getValue();
    // }
    // if (mini.get("approvedJsjdsBarBuildTo").getValue()) {
    //     postData.endTime = mini.get("approvedJsjdsBarBuildTo").getValue();
    // }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jsjds/report/queryInventJsjdsPlanBarChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                inventJsjdsBarChartOption.title.subtext = '年度计划: '+returnData.inventJsjdsPlanTotal+'个, 进度: '+returnData.progress+'%';
                inventJsjdsBarChartOption.legend.data = returnData.legendData;
                inventJsjdsBarChartOption.xAxis[0].data = returnData.xAxisData;
                inventJsjdsBarChartOption.series = returnData.series;
                inventJsjdsBarChart.setOption(inventJsjdsBarChartOption);
            }
        }
    });
}

// function approvedJsjdsBarBuildFromChanged() {
//     queryApprovedJsjdsBarChartData();
// }
//
// function approvedJsjdsBarBuildToChanged() {
//     queryApprovedJsjdsBarChartData();
// }

