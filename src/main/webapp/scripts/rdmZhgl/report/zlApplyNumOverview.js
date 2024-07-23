$(function () {
    // refreshAchievementType();
    zlTypeInit();
});

function zlTypeInit() {
    mini.get("professionalCategory").setValue("控制");
    mini.get("professionalCategory").setText("控制");
    mini.get("zlType").setValue("控制");
    mini.get("zlType").setText("控制");
    searchFrm();
    queryApplyChartData();
}



// 指定图表的配置项和数据
var zlApplyOption = {
    title: {
        show: false,
        text: '卡特彼勒各技术分支发展趋势',
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
            let res = '<p>' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data + '个';

            }
            return res;
        }
    },
    color: ['#4f81bd','#c0504d','#9bbb59','#e2e017','#0ddadc','#ef6400','#d25fe1','#00ca4d'],
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
                fontSize: 15,
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
            name: '\n数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};


function queryApplyChartData() {
    var zlType=mini.get("professionalCategory").getText();
    $.ajax({
        url: jsUseCtxPath + '/zljd/core/overview/queryApplyNum.do?professionalCategory='+zlType,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                zlApplyBar.clear();
                for (var i = 0; i < zlApplyOption.series.length; i++) {  //置空关键！！！  清空名字和数据
                    zlApplyOption.series[i].data = [];
                    zlApplyOption.series[i].name = '';
                    zlApplyOption.legend[i] = '';
                }
                zlApplyBar.setOption(zlApplyOption);
            }
            let returnData = resp.data;
            if (returnData) {
                zlApplyBar.clear();
                //对option进行赋值
                zlApplyOption.legend.data = returnData.legendData;
                zlApplyOption.xAxis[0].data = returnData.xAxisData;
                zlApplyOption.series = returnData.series;
                zlApplyBar.setOption(zlApplyOption);
            }
        }
    });
}

