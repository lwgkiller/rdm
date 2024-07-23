$(function () {
    initYearLineChart();
});

// 指定图表的配置项和数据
lineYearChartOption = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: []
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '',
            type: 'line',
            data: []
        },
        {
            name: '',
            type: 'line',
            data: []
        }
    ]
};

function initYearLineChart() {
    //近5年数据分类统计
    var xAxisData = [];
    var legendData = [];
    var currentYear = new Date().getFullYear();
    for(let i=0;i<=5;i++){
        var year = currentYear-(5-i);
        xAxisData[i] = year;
        var returnData1 = getData(year,userId1);
        var returnData2 = getData(year,userId2);
        lineYearChartOption.series[0].data[i] = returnData1.totalScore;
        lineYearChartOption.series[1].data[i] = returnData2.totalScore;
        lineYearChartOption.series[0].name = returnData1.userName;
        lineYearChartOption.series[1].name = returnData2.userName;
        legendData[0] = returnData1.userName;
        legendData[1] = returnData2.userName;
    }
    lineYearChartOption.legend.data = legendData;
    lineYearChartOption.xAxis.data = xAxisData;
    lineYearChart.setOption(lineYearChartOption);
}
function getData(_reportYear,_userId) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personYearScore.do?userId='+_userId+'&&reportYear='+_reportYear,
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
                resultDate.totalScore = (techScore+teamWorkScore+workScore+emplyeeScore).toFixed(2);
                resultDate.userName = dataDetail.userName;
            }else{
                mini.alert(returnData.message);
            }
        }
    });
    return resultDate;
}

