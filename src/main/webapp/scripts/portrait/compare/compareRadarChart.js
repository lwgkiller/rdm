$(function () {
    initRadarChart();
});
// 指定图表的配置项和数据
radarChartOption = {
    title: {
        text: ''
    },
    tooltip: {},
    legend: {
        left: 'left',
        data: []
    },
    radar: {
        name: {
            textStyle: {
                color: '#fff',
                backgroundColor: '#999',
                borderRadius: 3,
                padding: [3, 5]
            }
        },
        indicator: [
            {name: '技术创新'},
            {name: '技术协同'},
            {name: '敬业表现'},
            {name: '职业发展'}
        ]
    },
    series: [{
        name: '',
        type: 'radar',
        data: []
    }]
};

function initRadarChart() {
    var resultData1 = getRadarData(reportYear, userId1);
    var resultData2 = getRadarData(reportYear, userId2);
    var legendDate = [resultData1.userName, resultData2.userName];
    var seriesData = [
        {
            'value': [resultData1.techScore, resultData1.teamWorkScore, resultData1.workScore, resultData1.employeeScore],
            'name': resultData1.userName
        }, {
            'value': [resultData2.techScore, resultData2.teamWorkScore, resultData2.workScore, resultData2.employeeScore],
            'name': resultData2.userName
        }
    ]
    radarChartOption.legend.data = legendDate;
    radarChartOption.series[0].data = seriesData;
    radarChart.setOption(radarChartOption);
}

function getRadarData(_reportYear, _userId) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personScore.do?userId=' + _userId + '&&reportYear=' + reportYear,
        type: 'POST',
        async: false,
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
                resultDate.userName = dataDetail.userName;
                resultDate.techScore = projectScore + standardScore + knowledgeScore + rewardScore + secretScore + technologyScore;
                resultDate.teamWorkScore = bbsScore + patentReadScore + informationScore + analysisImproveScore + contractScore;
                resultDate.workScore = attendanceScore + notificationScore + performanceScore;
                resultDate.employeeScore = courseScore + cultureScore;
            } else {
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}

