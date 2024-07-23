$(function () {
    initPieChart();
});

// 指定图表的配置项和数据
pieChartOption = {
    title: {
        text: '分类得分占比',
        left: 'center'
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ['技术创新', '技术协同', '敬业表现', ,'职业发展']
    },
    series: [
        {
            name: '分类名称',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};
function initPieChart() {
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
                var seriesData = [];
                var techObj = {"name":"技术创新","value":projectScore+standardScore+knowledgeScore+secretScore+technologyScore+rewardScore};
                var teamWorkObj = {"name":"技术协同","value":bbsScore+patentReadScore+informationScore+analysisImproveScore+contractScore};
                var workObj = {"name":"敬业表现","value":attendanceScore+notificationScore+performanceScore};
                var employeeObj = {"name":"职业发展","value":courseScore+cultureScore};
                seriesData.push(techObj);
                seriesData.push(teamWorkObj);
                seriesData.push(workObj);
                seriesData.push(employeeObj);
                pieChartOption.series[0].data = seriesData;
                pieChart.setOption(pieChartOption);
            }else{
                mini.alert(returnData.message);
            }
        }
    });
}

