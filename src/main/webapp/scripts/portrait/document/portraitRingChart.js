$(function () {
    initRingChart();
});

// 指定图表的配置项和数据
ringChartOption = {
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        left: 10,
        data: ['标准', '知识产权','科技项目','荣誉奖项','专有技术', '技术数据库', 'RDM论坛', '专利解读', '情报工程', '分析改进', '合同管理', '考勤','通报','月度绩效',
            '培训', '培养']
    },
    series: [
        {
            name: '积分类别',
            type: 'pie',
            selectedMode: 'single',
            radius: [0, '30%'],

            label: {
                position: 'inner'
            },
            labelLine: {
                show: false
            },
            data: [
            ]
        },
        {
            name: '积分子类',
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
                backgroundColor: '#eee',
                borderColor: '#aaa',
                borderWidth: 1,
                borderRadius: 4,
                rich: {
                    a: {
                        color: '#999',
                        lineHeight: 22,
                        align: 'center'
                    },
                    hr: {
                        borderColor: '#aaa',
                        width: '100%',
                        borderWidth: 0.5,
                        height: 0
                    },
                    b: {
                        fontSize: 16,
                        lineHeight: 33
                    },
                    per: {
                        color: '#eee',
                        backgroundColor: '#334455',
                        padding: [2, 4],
                        borderRadius: 2
                    }
                }
            },
            data: [
            ]
        }
    ]
};

function initRingChart() {
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
                var series1Data = [];
                var techObj = {"name":"技术创新","value":projectScore+standardScore+knowledgeScore+secretScore+technologyScore+rewardScore};
                var teamWorkObj = {"name":"技术协同","value":bbsScore+patentReadScore+informationScore+analysisImproveScore+contractScore};
                var workObj = {"name":"敬业表现","value":attendanceScore+notificationScore+performanceScore};
                var employeeObj = {"name":"职业发展","value":courseScore+cultureScore};
                series1Data.push(techObj);
                series1Data.push(teamWorkObj);
                series1Data.push(employeeObj);
                series1Data.push(workObj);
                var series2Data = [
                    {'name':'标准','value':standardScore},
                    {'name':'知识产权','value':knowledgeScore},
                    {'name':'科技项目','value':projectScore},
                    {'name':'荣誉奖项','value':rewardScore},
                    {'name':'专有技术','value':secretScore},
                    {'name':'技术数据库','value':technologyScore},
                    {'name':'RDM论坛','value':bbsScore},
                    {'name':'专利解读','value':patentReadScore},
                    {'name':'情报报告','value':informationScore},
                    {'name':'分析改进','value':analysisImproveScore},
                    {'name':'合同管理','value':contractScore},
                    {'name':'考勤','value':attendanceScore},
                    {'name':'通报','value':notificationScore},
                    {'name':'月度绩效','value':performanceScore},
                    {'name':'培训','value':courseScore},
                    {'name':'培养','value':cultureScore},
                ];
                ringChartOption.series[0].data = series1Data;
                ringChartOption.series[1].data = series2Data;
                ringChart.setOption(ringChartOption);
            }else{
                mini.alert(returnData.message);
            }
        }
    });
}

