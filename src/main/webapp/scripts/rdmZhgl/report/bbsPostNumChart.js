$(function () {
    var currentDate = new Date();
    var year = currentDate.getFullYear();
    var month = currentDate.getMonth();
    var startTime = year+'-'+month+'-01';
    mini.get("post_startTime").setValue(startTime);
    mini.get("post_sendTime").setValue(currentDate);
    initPostRankChart();
    bbsPostChart.setOption(bbsPostOption);
});
bbsPostOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // Use axis to trigger tooltip
            type: 'shadow'        // 'shadow' as default; can also be 'line' or 'shadow'
        }
    },
    legend: {
        data: ['改进提案', '知识分享', '公开讨论']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'value'
    },
    yAxis: {
        type: 'category',
        data: ['第十名', '第九名', '第八名', '第七名','第六名','第五名','第四名','第三名','第二名','第一名']
    },
    series: [
        {
            name: '改进提案',
            type: 'bar',
            stack: 'total',
            label: {
                show: true
            },
            emphasis: {
                focus: 'series'
            },
            data: []
        },
        {
            name: '知识分享',
            type: 'bar',
            stack: 'total',
            label: {
                show: true
            },
            emphasis: {
                focus: 'series'
            },
            data: []
        },
        {
            name: '公开讨论',
            type: 'bar',
            stack: 'total',
            label: {
                show: true
            },
            emphasis: {
                focus: 'series'
            },
            data: []
        }
    ]
};
function initPostRankChart() {
    var bbs_startTime = mini.get('post_startTime').getText();
    var bbs_endTime = mini.get('post_sendTime').getText();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/bbs/report/bbsPostRankReport.do?bbs_startTime='+bbs_startTime+'&&bbs_endTime='+bbs_endTime,
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                var dataDetail = returnData.data;
                var ySeriesData = [];
                for(var i = dataDetail.length-1; i >=0; i--){
                    ySeriesData[i] = dataDetail[dataDetail.length-1-i].postor;
                    bbsPostOption.series[0].data[i] = dataDetail[dataDetail.length-1-i].gjtaNum;
                    bbsPostOption.series[1].data[i] = dataDetail[dataDetail.length-1-i].zsfxNum;
                    bbsPostOption.series[2].data[i] = dataDetail[dataDetail.length-1-i].gktlNum;
                }
                bbsPostOption.yAxis.data = ySeriesData;
                bbsPostChart.setOption(bbsPostOption);
            } else {
                mini.alert(returnData.message);
            }
        }
    });
}

