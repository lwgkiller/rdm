// 指定图表的配置项和数据
jhtjChartOption = {
    legend: {},
    tooltip: {},
    dataset: {
        source: [
            ['product', '公司级计划个数'],
            ['大挖', 63],
            ['中挖', 40],
            ['小挖', 4],
            ['特挖研究所', 4],
            ['国际化', 17],
            ['智控', 25],
            ['标准所', 12],
            ['仿真', 24],
            ['测试', 34],
            ['服务工程', 5],
            ['工程中心', 16],
            ['微挖', 5]
        ]
    },
    xAxis: {
        type: 'category',
        axisLabel: {
            interval: 0,
            rotate: 40
        },
    },
    yAxis: {},
    series: [
        {
            type: 'bar', barWidth: '10px', color: '#6198ea',
            label: {
                show: true,
                position: 'top'
            }
        },
    ]
};

function initJhtjChart() {
    var yearMonthStart = '';
    var yearMonthEnd = '';
    if (mini.get("yearMonthStart2").getValue()) {
        yearMonthStart = mini.get("yearMonthStart2").getText();
    }
    if (mini.get("yearMonthEnd2").getValue()) {
        yearMonthEnd = mini.get("yearMonthEnd2").getText();
    }
    var resultData = getJhtjData(yearMonthStart, yearMonthEnd);
    var sourceDate = [
        ['product', '公司级计划个数'],
        ['大挖', resultData.dw],
        ['中挖', resultData.zw],
        ['小挖', resultData.xw],
        ['特挖研究所', resultData.xml],
        ['国际化', resultData.gjh],
        ['智控', resultData.zk],
        ['标准所', resultData.bzh],
        ['仿真', resultData.fz],
        ['测试', resultData.cs],
        ['服务工程', resultData.fwgc],
        ['工程中心', resultData.gczx],
        ['技术管理部', resultData.jsglb],
        ['绿色技术', resultData.lsjs],
        ['零部件产品', resultData.lbjcp],
    ]
    jhtjChartOption.dataset.source = sourceDate;
    jhtjChart.setOption(jhtjChartOption);
}

function getJhtjData(yearMonthStart, yearMonthEnd) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/monthWork/reportPlans.do?yearMonthStart=' + yearMonthStart + '&yearMonthEnd=' + yearMonthEnd + '&isCompanyLevel=1',
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

