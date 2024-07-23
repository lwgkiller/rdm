$(function () {
    // refreshAchievementType();
    timeReCreateInit();
});

function timeReCreateInit() {
    //今年1月1号到当前日期
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime =nowYear + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("reTimeFrom").setValue(startTime);
    mini.get("reTimeTo").setValue(endTime);
    queryreCreateChartData();
}



// 指定图表的配置项和数据
var reCreateOption = {
    title: {
        text: "再创新专利申请量",
        subtext: '',
        left: "center",
    },
    tooltip: {
        trigger: 'item',
        formatter: '{b}:{c}',
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: []
    },
    series: [
        {
            name: '再创新专利申请量',
            type: 'pie',
            radius: '55%',
            center: ['50%','60%'],
            data: [],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgb(0,0,0,0.5)'
                }
            },
            label: {
                show: true,
                position: 'outside',
                formatter: '{b}: {c}'
            }
        }
    ]
};


function queryreCreateChartData() {
    var startTime=mini.get("reTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择开始时间");
        return;
    }
    var endTime=mini.get("reTimeTo").getText();
    if (!endTime) {
        mini.alert("请选择结束时间");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/zljd/core/overview/queryReTotalNum.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                reCreateOption.legend.data = returnData.legendData;
                reCreateOption.series[0].data = returnData.series;
                reCreateBar.setOption(reCreateOption);
            }
        }
    });
}

