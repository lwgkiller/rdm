$(function () {
    initBarChart();
});
// 指定图表的配置项和数据
barChartOption = {
    legend: {},
    tooltip: {},
    dataset: {
        source: [
            ['product', '张三', '李四'],
            ['三高一可', 43.3, 85.8],
            ['技术创新', 83.1, 73.4],
            ['技术协同', -2, 65.2],
            ['Walnut Brownie', 72.4, 53.9]
        ]
    },
    xAxis: {type: 'category'},
    yAxis: {},
    series: [
        {type: 'bar', barWidth: '50px',},
        {type: 'bar', barWidth: '50px',}
    ]
};

function initBarChart() {
    var resultData1 = getBarData(reportYear,userId1);
    var resultData2 = getBarData(reportYear,userId2);

    var sourceDate =  [
        ['product', resultData1.userName, resultData2.userName],
        ['技术创新', resultData1.techScore, resultData2.techScore],
        ['技术协同', resultData1.teamWorkScore, resultData2.teamWorkScore],
        ['敬业表现', resultData1.workScore, resultData2.workScore],
        ['职业发展', resultData1.emplyeeScore, resultData2.emplyeeScore]
    ]
    barChartOption.dataset.source = sourceDate;
    barChart.setOption(barChartOption);
}
function getBarData(_reportYear,_userId) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personScore.do?userId='+_userId+'&&reportYear='+reportYear,
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
                resultDate.techScore = projectScore+standardScore+knowledgeScore+rewardScore+secretScore+technologyScore;
                resultDate.teamWorkScore = bbsScore+patentReadScore+informationScore+analysisImproveScore+contractScore;
                resultDate.workScore = attendanceScore+notificationScore+performanceScore;
                resultDate.emplyeeScore = courseScore+cultureScore;
            }else{
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}

