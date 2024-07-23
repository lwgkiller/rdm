$(function () {
    var currentDate = new Date();
    var year = currentDate.getFullYear();
    var month = currentDate.getMonth();
    var startTime = year+'-'+month+'-01';
    mini.get("gjta_startTime").setValue(startTime);
    mini.get("gjta_endTime").setValue(currentDate);
    initGjtaRankChart();
    // bbsGjtaChart.setOption(bbsGjtaOption);
});
bbsGjtaOption = {
    xAxis: {
        type: 'category',
        data: ['第一名', '第二名', '第三名', '第四名','第五名','第六名','第七名','第八名','第九名','第十名']
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [10, 9, 8, 7,6,5,4,3,2,1],
        type: 'bar',
        color:'#23bdeb',
        label: {
            show: true,
            position: 'top'
        },
    }]
};
function initGjtaRankChart() {
    var bbs_startTime = mini.get('gjta_startTime').getText();
    var bbs_endTime = mini.get('gjta_endTime').getText();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/bbs/report/bbsGjtaRankReport.do?bbs_startTime='+bbs_startTime+'&&bbs_endTime='+bbs_endTime,
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                var dataDetail = returnData.data;
                var xSeriesData = [];
                var seriesData = [];
                for(var i=0;i<dataDetail.length;i++){
                    xSeriesData[i] = dataDetail[i].postor;
                    seriesData[i] = dataDetail[i].totalNum;
                }
                bbsGjtaOption.xAxis.data = xSeriesData;
                bbsGjtaOption.series[0].data = seriesData;
                bbsGjtaChart.setOption(bbsGjtaOption);
            } else {
                mini.alert(returnData.message);
            }
        }
    });
}

