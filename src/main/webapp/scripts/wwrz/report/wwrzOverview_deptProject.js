// 指定图表的配置项和数据
reportDeptProjectChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    legend: {},
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
            name: '进展中',
            type: 'bar',
            color:'#28a1f8',
            stack: 'plan',
            label: {
                show: true
            },
            data: [],
            emphasis: {
                focus: 'series'
            },
        },
        {
            name: '已完成',
            type: 'bar',
            color:'green',
            label: {
                show: true
            },
            stack: 'finish',
            emphasis: {
                focus: 'series'
            },
            data: []
        },
    ]
};
function initReportDeptProjectChart() {
    var reportYearStart = '';
    if (mini.get("reportDeptProjectYearStart").getValue()) {
        reportYearStart = mini.get("reportDeptProjectYearStart").getText();
    }
    var reportYearEnd = '';
    if (mini.get("reportDeptProjectYearEnd").getValue()) {
        reportYearEnd = mini.get("reportDeptProjectYearEnd").getText();
    }
    if(reportYearStart&&reportYearEnd){
        if(reportYearStart>reportYearEnd){
            mini.alert("开始年份不能大于结束年份！");
            return;
        }
    }
    var resultData = getReportDeptProjectData(reportYearStart,reportYearEnd);
    var xAxisData = [];
    var seriesData1 = [];
    var seriesData2 = [];
    for(var i=0;i<resultData.length;i++){
        xAxisData[i] = resultData[i].deptName;
        seriesData1[i] = resultData[i].runningNum;
        seriesData2[i] = resultData[i].endNum;
    }
    reportDeptProjectChartOption.xAxis[0].data = xAxisData;
    reportDeptProjectChartOption.series[0].data = seriesData1;
    reportDeptProjectChartOption.series[1].data = seriesData2;
    reportDeptProjectChart.setOption(reportDeptProjectChartOption);
}

function getReportDeptProjectData(reportYearStart,reportYearEnd) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportDeptProject.do?reportYearStart=' + reportYearStart+'&reportYearEnd='+reportYearEnd,
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
