function queryDeptChart() {
    $("#deptSum").addClass("clicked");
    $("#monthSum").removeClass("clicked");
    $.ajax({
        url: jsUseCtxPath + '/digitization/core/Szh/queryMenuClickData.do?menuId='+menuId+"&type=groupByDept",
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                deptDataValue.data=returnData.data;
                deptOption.series=[];
                deptOption.series.push(deptDataValue);
                deptOption.xAxis[0].data=returnData.deptNames;
                clickCountChart.setOption(deptOption);
            }
        }
    });
}



// 指定图表的配置项和数据
var deptOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    color:['#66b1ff'],
    grid: {
        left: '3.5%',
        right: '2%',
        top:'8%',
        bottom: '2%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            data : [],
            axisTick: {
                alignWithLabel: true
            },
            axisLabel:{
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
    dataZoom:[
        {
            type:'inside'
        }
    ],
    yAxis : [
        {
            type : 'value',
            minInterval:1
        }
    ],
    series : []
};

var deptDataValue={
    name:'次数',
    type:'bar',
    barWidth: 30,
    data:[]
};

