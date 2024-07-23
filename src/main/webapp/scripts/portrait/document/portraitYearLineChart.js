$(function () {
    debugger
    initYearLineChart();
});

// 指定图表的配置项和数据
lineYearChartOption = {
    tooltip: {
        trigger: 'axis',
    },
    xAxis: {
        type: 'category',
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [],
        type: 'line'
    }]
};

function initYearLineChart() {
    //近5年数据分类统计
    var xAxisData = [];
    var currentYear = new Date().getFullYear();
    for(let i=0;i<=5;i++){
        var year = currentYear-(5-i);
        xAxisData[i] = year;
        var returnData = getData(year);
        lineYearChartOption.series[0].data[i] = returnData.totalScore.toFixed(3);
    }
    lineYearChartOption.xAxis.data = xAxisData;
    lineYearChart.setOption(lineYearChartOption);
}
function getData(_reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personYearScore.do?userId='+userId+'&&reportYear='+_reportYear,
        type: 'POST',
        async:false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                var dataDetail = returnData.data;
                var standardScore = dataDetail.standardScore;
                var knowledgeScore = dataDetail.knowledgeScore;
                var projectScore = dataDetail.projectScore;
                var rewardScore = dataDetail.rewardScore;
                var secretScore = dataDetail.secretScore;
                var technologyScore = dataDetail.technologyScore;
                var bbsScore = dataDetail.bbsScore;
                var patentReadScore = dataDetail.patentReadScore;
                var informationScore = dataDetail.informationScore;
                var analysisImproveScore = dataDetail.analysisImproveScore;
                var contractScore = dataDetail.contractScore;
                var attendanceScore = dataDetail.attendanceScore;
                var notificationScore = dataDetail.notificationScore;
                var performanceScore = dataDetail.performanceScore;
                var courseScore = dataDetail.courseScore;
                var cultureScore = dataDetail.cultureScore;
                var techScore = projectScore+standardScore+knowledgeScore+rewardScore+secretScore+technologyScore;
                var teamWorkScore = bbsScore+patentReadScore+informationScore+analysisImproveScore+contractScore;
                var workScore = attendanceScore+notificationScore+performanceScore;
                var emplyeeScore = courseScore+cultureScore;
                resultDate.totalScore = techScore+teamWorkScore+workScore+emplyeeScore;
            }else{
                mini.alert(returnData.message);
            }
        }
    });
    return resultDate;
}

