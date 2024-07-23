// 指定图表的配置项和数据
wcqkChartOption = {
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
            axisLabel: {
                interval: 0,
                rotate: 40
            },
            data: ['大挖', '中挖','小挖', '特挖研究所', '国际化', '智控', '标准所', '仿真', '测试', '服务工程', '工程中心','技术管理部','绿色技术','零部件产品'],
        }
    ],
    yAxis: [
        {
            type: 'value'
        },
        {
            type: 'value',
            name: '完成率',
            min: 0,
            max: 100,
            interval: 5,
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '项目计划',
            type: 'bar',
            stack: 'plan',
            color:'#28a1f8',
            emphasis: {
                focus: 'series'
            },
            data: [320, 332, 301, 334, 390, 330, 320, 334, 390, 330, 320]
        },
        {
            name: '未完成',
            type: 'bar',
            color:'red',
            stack: 'plan',
            emphasis: {
                focus: 'series'
            },
            data: [21, 22, 32, 42, 52, 62, 72, 22, 22, 22, 22]
        },
        {
            name: '非项目计划',
            type: 'bar',
            color:'#39bc91',
            stack: 'unPlan',
            emphasis: {
                focus: 'series'
            },
            data: [120, 132, 101, 134, 90, 230, 210, 334, 390, 330, 320]
        },
        {
            name: '未完成',
            type: 'bar',
            stack: 'unPlan',
            color:'red',
            emphasis: {
                focus: 'series'
            },
            data: [21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22]
        },
        {
            name: '计划外任务',
            type: 'bar',
            stack: 'task',
            color:'#e5c93b',
            emphasis: {
                focus: 'series'
            },
            data: [150, 232, 201, 154, 190, 330, 410, 334, 390, 330, 320]
        },
        {
            name: '未完成',
            type: 'bar',
            stack: 'task',
            color:'red',
            emphasis: {
                focus: 'series'
            },
            data: [44, 45, 46, 47, 23, 12, 67, 22, 22, 22, 22]
        },
        {
            name: '完成率',
            type: 'line',
            yAxisIndex: 1,
            data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0]
        },
        {
            name: '平均值',
            type: 'line',
            yAxisIndex: 1,
            data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0]
        }
    ]
};
function initWcqkChart() {
    var yearMonthStart = '';
    var yearMonthEnd = '';
    if (mini.get("yearMonthStart1").getValue()) {
        yearMonthStart = mini.get("yearMonthStart1").getText();
    }
    if (mini.get("yearMonthEnd1").getValue()) {
        yearMonthEnd = mini.get("yearMonthEnd1").getText();
    }
    var resultData = getWcqkData(yearMonthStart, yearMonthEnd);
    wcqkChartOption.series[0].data = [resultData.dwplan,resultData.zwplan,resultData.xwplan,resultData.xmlplan,resultData.gjhplan,
        resultData.zkplan,resultData.bzhplan,resultData.fzplan,resultData.csplan,resultData.fwgcplan,resultData.gczxplan,resultData.jsglbplan,resultData.lsjsplan,resultData.lbjcpplan];
    wcqkChartOption.series[1].data = [resultData.dwplanFinish,resultData.zwplanFinish,resultData.xwplanFinish,resultData.xmlplanFinish,resultData.gjhplanFinish,
        resultData.zkplanFinish,resultData.bzhplanFinish,resultData.fzplanFinish,resultData.csplanFinish,resultData.fwgcplanFinish,resultData.gczxplanFinish,resultData.jsglbplanFinish,resultData.lsjsplanFinish,resultData.lbjcpplanFinish];
    wcqkChartOption.series[2].data = [resultData.dwunPlan,resultData.zwunPlan,resultData.xwunPlan,resultData.xmlunPlan,resultData.gjhunPlan,
        resultData.zkunPlan,resultData.bzhunPlan,resultData.fzunPlan,resultData.csunPlan,resultData.fwgcunPlan,resultData.gczxunPlan,resultData.jsglbunPlan,resultData.lsjsunPlan,resultData.lbjcpunPlan];
    wcqkChartOption.series[3].data = [resultData.dwunPlanFinish,resultData.zwunPlanFinish,resultData.xwunPlanFinish,resultData.xmlunPlanFinish,resultData.gjhunPlanFinish,
        resultData.zkunPlanFinish,resultData.bzhunPlanFinish,resultData.fzunPlanFinish,resultData.csunPlanFinish,resultData.fwgcunPlanFinish,resultData.gczxunPlanFinish,resultData.jsglbunPlanFinish,resultData.lsjsunPlanFinish,resultData.lbjcpunPlanFinish];
    wcqkChartOption.series[4].data = [resultData.dwtask,resultData.zwtask,resultData.xwtask,resultData.xmltask,resultData.gjhtask,
        resultData.zktask,resultData.bzhtask,resultData.fztask,resultData.cstask,resultData.fwgctask,resultData.gczxtask,resultData.jsglbtask,resultData.lsjstask,resultData.lbjcptask];
    wcqkChartOption.series[5].data = [resultData.dwtaskFinish,resultData.zwtaskFinish,resultData.xwtaskFinish,resultData.xmltaskFinish,resultData.gjhtaskFinish,
        resultData.zktaskFinish,resultData.bzhtaskFinish,resultData.fztaskFinish,resultData.cstaskFinish,resultData.fwgctaskFinish,resultData.gczxtaskFinish,resultData.jsglbtaskFinish,resultData.lsjstaskFinish,resultData.lbjcptaskFinish];
    wcqkChartOption.series[6].data = [dealData(resultData.dwtotal,resultData.dwtotalFinish),dealData(resultData.zwtotal,resultData.zwtotalFinish),dealData(resultData.xwtotal,resultData.xwtotalFinish),
        dealData(resultData.xmltotal,resultData.xmltotalFinish),dealData(resultData.gjhtotal,resultData.gjhtotalFinish), dealData(resultData.zktotal,resultData.zktotalFinish),
        dealData(resultData.bzhtotal,resultData.bzhtotalFinish),dealData(resultData.fztotal,resultData.fztotalFinish),dealData(resultData.cstotal,resultData.cstotalFinish),
        dealData(resultData.fwgctotal,resultData.fwgctotalFinish),dealData(resultData.gczxtotal,resultData.gczxtotalFinish),dealData(resultData.jsglbtotal,resultData.jsglbtotalFinish),dealData(resultData.lsjstotal,resultData.lsjstotalFinish),,dealData(resultData.lbjcptotal,resultData.lbjcptotalFinish)];
    wcqkChartOption.series[7].data = [resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),
        resultData.ratio.toFixed(2), resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),
        resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2),resultData.ratio.toFixed(2)];
    wcqkChart.setOption(wcqkChartOption);
}
function dealData(totalNum,unFinishNum) {
    if(totalNum==0){
        return 100;
    }else{
        return ((totalNum-unFinishNum)/totalNum*100).toFixed(2);
    }
}

function getWcqkData(yearMonthStart, yearMonthEnd) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/monthWork/reportPlanData.do?yearMonthStart=' + yearMonthStart + '&yearMonthEnd=' + yearMonthEnd,
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
