$(function () {
    initLineChart();
});

// 指定图表的配置项和数据
lineChartOption = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: ['技术创新', '技术协同', '敬业表现', '职业发展']
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
            name: '技术创新',
            type: 'line',
            data: []
        },
        {
            name: '技术协同',
            type: 'line',
            data: []
        },
        {
            name: '敬业表现',
            type: 'line',
            data: []
        },
        {
            name: '职业发展',
            type: 'line',
            data: []
        }
    ]
};

function initLineChart() {
    //近5年数据分类统计
    var xAxisData = [];
    var currentYear = new Date().getFullYear();
    for(let i=0;i<=5;i++){
        var year = currentYear-(5-i);
        xAxisData[i] = year;
        var returnData = getLineScoreData(year);
        lineChartOption.series[0].data[i] = returnData.techScore;
        lineChartOption.series[1].data[i] = returnData.teamWorkScore;
        lineChartOption.series[2].data[i] = returnData.workScore;
        lineChartOption.series[3].data[i] = returnData.emplyeeScore;
    }
    lineChartOption.xAxis.data = xAxisData;
    lineChart.setOption(lineChartOption);
}
function getLineScoreData(_reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personScore.do?userId='+userId+'&&reportYear='+_reportYear,
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
    });
    return resultDate;
}

