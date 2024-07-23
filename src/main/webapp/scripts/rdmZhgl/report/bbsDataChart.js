$(function () {
    var currentDate = new Date();
    var year = currentDate.getFullYear();
    var month = currentDate.getMonth();
    var startTime = year+'-'+month+'-01';
    mini.get("data_startTime").setValue(startTime);
    mini.get("data_endTime").setValue(currentDate);
    initBbsDataChart();
    // bbsDataChart.setOption(bbsDataOption);
});
bbsDataOption = {
    xAxis: {
        type: 'category',
        data: ['总数', '已采纳', '未采纳', '已完成数','未完成数',]
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [120, 200, 150, 80,20],
        type: 'bar',
        label: {
            show: true,
            position: 'top'
        },
    }]
};
function initBbsDataChart() {
    var bbs_startTime = mini.get('data_startTime').getText();
    var bbs_endTime = mini.get('data_endTime').getText();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/bbs/report/bbsDataReport.do?bbs_startTime='+bbs_startTime+'&&bbs_endTime='+bbs_endTime,
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                var dataDetail = returnData.data;
                var seriesData = [];
                seriesData.push(dataDetail.totalNum);
                seriesData.push(dataDetail.adopt);
                seriesData.push(dataDetail.unAdopt);
                seriesData.push(dataDetail.finished);
                seriesData.push(dataDetail.unFinished);
                bbsDataOption.series[0].data = seriesData;
                bbsDataChart.setOption(bbsDataOption);
            } else {
                mini.alert(returnData.message);
            }
        }
    });
}

