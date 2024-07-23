$(function () {
    queryJdqk();
});

// 指定图表的配置项和数据
jdqkOption = {
    title: {
        text: '进度情况分类统计',
        x: 'center'
    },
    tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ['正常', '轻微延误', '重度延误', '项目没有计划时间且延误超过30天']
    },
    series: [
        {
            name: '进度情况',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
                {value: 200, name: 'normal'},
                {value: 200, name: 'warning'},
                {value: 200, name: 'delay'},
                {value: 200, name: 'delayInStartUp'},
            ],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ],
    color: ['rgb(29, 132, 4)', 'rgb(202, 134, 34)', 'rgb(205, 51, 51)', 'rgb(155,0,252)']
};

function queryJdqk() {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/report/xcmgProject/deskHomeProjectProgress.do',
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                var seriesData = [];
                seriesData.push({
                    name: '正常',
                    value: returnData.normal
                });
                seriesData.push({
                    name: '轻微延误',
                    value: returnData.warning
                });
                seriesData.push({
                    name: '重度延误',
                    value: returnData.delay
                });
                // 策划阶段延误超过30天
                seriesData.push({
                    name: '项目没有计划时间且延误超过30天',
                    value: returnData.delayInStartUp
                });
                //对option进行赋值
                jdqkOption.series[0].data = seriesData;
                jdqkChart.setOption(jdqkOption);
            }
        }
    });
}