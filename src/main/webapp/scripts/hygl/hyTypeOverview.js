$(function () {
    // refreshAchievementType();
    timeHyTypeInit();
});

function timeHyTypeInit() {
    //今年1月1号到当前日期
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime =nowYear + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("numTimeFrom").setValue(startTime);
    mini.get("numTimeTo").setValue(endTime);
    queryHyTypeChartData();
}



// 指定图表的配置项和数据
var hyTypeOption = {
    title: {
        subtext: '',
        left: "center"
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
            name: '专利解读数量',
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


function queryHyTypeChartData() {
    var startTime=mini.get("numTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择开始时间");
        return;
    }
    var endTime=mini.get("numTimeTo").getText();
    if (!endTime) {
        mini.alert("请选择结束时间");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/meeting/core/overview/queryMeetingType.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                hyTypeOption.title.text = '各类型会议数量(会议总数:' + returnData.totalMeeting+')';
                hyTypeOption.legend.data = returnData.legendData;
                hyTypeOption.series[0].data = returnData.series;
                hyTypeBar.setOption(hyTypeOption);
            }
        }
    });
}

