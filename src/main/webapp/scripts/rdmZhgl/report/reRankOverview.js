$(function () {
    rankInit();
});

function rankInit() {
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();

    let startTime = (nowYear) + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("rerankTimeFrom").setValue(startTime);
    mini.get("rerankTimeTo").setValue(endTime);
    queryReRankChartData()
}

// 指定图表的配置项和数据
var reRankBarOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            var res = params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#526FC6'],
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
                fontSize: 14,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 7;
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
            name: '分\n数\n',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        }
    ],
    series: []
};



function queryReRankChartData() {
    var postData = {};
    if (!mini.get("rerankTimeFrom").getText()) {
        mini.alert("请选择开始时间！");
        return;
    }
    if (!mini.get("rerankTimeTo").getText()) {
        mini.alert("请选择结束时间！");
        return;
    }
    var rerankTimeFrom = mini.get("rerankTimeFrom").getText().substr(0,7);
    var rerankTimeTo = mini.get("rerankTimeTo").getText().substr(0,7);
    $.ajax({
        url: jsUseCtxPath + '/zljd/core/overview/queryReNumByuser.do?startTime='+rerankTimeFrom+"&endTime="+rerankTimeTo,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                reRankBarOption.xAxis[0].data = returnData.xAxisData;
                reRankBarOption.series = returnData.series;
                reRankBar.setOption(reRankBarOption);
            }
        }
    });
}





