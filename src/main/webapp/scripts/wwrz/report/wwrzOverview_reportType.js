// 指定图表的配置项和数据
reportTypeChartOption = {
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: ['大挖', '中挖','小挖', '特挖研究所', '国际化', '智控', '标准所', '仿真', '测试', '服务工程', '工程中心','技术管理部'],
            axisLabel: {
                interval: 0,
                rotate: 40
            },
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '个数',
            axisLabel: {
                formatter: '{value} 个'
            }
        },
    ],
    series: [
        {
            name: '个数',
            type: 'bar',
            color:'#409eff',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20, 6, 3],
            label: {
                show: true,
                position: 'top',
            }
        },
    ]
};
function initReportTypeChart() {
    var reportYearStart = '';
    if (mini.get("reportYearStart").getValue()) {
        reportYearStart = mini.get("reportYearStart").getText();
    }
    var reportYearEnd = '';
    if (mini.get("reportYearEnd").getValue()) {
        reportYearEnd = mini.get("reportYearEnd").getText();
    }
    if(reportYearStart&&reportYearEnd){
        if(reportYearStart>reportYearEnd){
            mini.alert("开始年份不能大于结束年份！");
            return;
        }
    }
    var resultData = getReportTypeData(reportYearStart,reportYearEnd);
    var xAxisData = [];
    var seriesData1 = [];
    for(var i=0;i<resultData.length;i++){
        xAxisData[i] = resultData[i].reportName;
        seriesData1[i] = resultData[i].totalNum;
    }
    reportTypeChartOption.xAxis[0].data = xAxisData;
    reportTypeChartOption.series[0].data = seriesData1;
    reportTypeChart.setOption(reportTypeChartOption);
}

function getReportTypeData(reportYearStart,reportYearEnd) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportTypeData.do?reportYearStart=' + reportYearStart+'&reportYearEnd='+reportYearEnd,
        type: 'POST',
        async: false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                resultDate = returnData.data;
            } else {
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}
