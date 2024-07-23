$(function () {
    initBarChart();
});
// 指定图表的配置项和数据
barChartOption = {
    color: ['#3398DB'],
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [ '技术创新', '技术协同', '敬业表现', '职业发展', ],
            axisTick: {
                alignWithLabel: true
            }
        }
    ],
    yAxis: [
        {
            type: 'value'
        }
    ],
    series: [
        {
            name: '得分',
            type: 'bar',
            barWidth: '60%',
            data: []
        }
    ]
};

function initBarChart() {
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personScore.do?userId='+userId+'&&reportYear='+reportYear,
        type: 'POST',
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
                var teamWorkObj = bbsScore+patentReadScore+informationScore+analysisImproveScore+contractScore;
                var workObj = attendanceScore+notificationScore+performanceScore;
                var employeeObj = courseScore+cultureScore;
                var seriesData = [techScore,teamWorkObj,workObj,employeeObj];
                barChartOption.series[0].data = seriesData;
                barChart.setOption(barChartOption);
            }else{
                mini.alert(returnData.message);
            }
        }
    });
}

