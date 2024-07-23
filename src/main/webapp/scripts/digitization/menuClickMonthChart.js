function queryMonthChart() {
    $("#monthSum").addClass("clicked");
    $("#deptSum").removeClass("clicked");
    $.ajax({
        url: jsUseCtxPath + '/digitization/core/Szh/queryMenuClickData.do?menuId='+menuId+"&type=groupByMonth",
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                monthDataValue.data=returnData.data;
                monthOption.series=[];
                monthOption.series.push(monthDataValue);
                monthOption.xAxis[0].data=returnData.yearMonths;
                clickCountChart.setOption(monthOption);
            }
        }
    });
}



// 指定图表的配置项和数据
var monthOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'line'        // 默认为直线，可选为：'line' | 'shadow'
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
                interval:0,
                formatter:function (param) {
                    return param;
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

var monthDataValue={
    name:'次数',
    type:'line',
    data:[]
};

