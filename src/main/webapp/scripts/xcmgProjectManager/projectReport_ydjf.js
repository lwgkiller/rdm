$(function () {
    mini.get("#ydjfMonth").setValue(lastMonth);
    loadChartOutydjf();
});

// 指定图表的配置项和数据
var ydjfOption;

function loadChartOutydjf() {
    $.ajax({
        url: jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/queryYdjfDepsumscore.do",
        data: {
            time: mini.get("#ydjfMonth").getText()
        },
        dataType: "json",
        success: function (text) {
            var ydjfOptionyAxisData = [];
            var ydjfOptionseriesData = [];
            var returnJson = mini.decode(text);
            if (returnJson.success) {
                for (var i = returnJson.data.length-1; i >=0; i--) {
                    ydjfOptionyAxisData.add(returnJson.data[i].depname);
                    ydjfOptionseriesData.add(returnJson.data[i].depsumscore);
                }
                ydjfOption =
                    {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'shadow'
                            },
                            formatter: "部门：{b}<br/>当月项目积分：{c}分"
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true,
                            top:'top',
                            left:'left'
                        },
                        xAxis: {
                            type: 'value',
                            name: '积分（分）',
                            nameLocation: 'center',
                            nameGap: 20,
                            nameRotate: 0,
                        },
                        yAxis: {
                            type: 'category',
                            axisLabel: {
                                show: true,
                                interval: 0
                            },
                            data: ydjfOptionyAxisData
                        },
                        series: [
                            {
                                barWidth:20,
                                type: 'bar',
                                data: ydjfOptionseriesData
                            }
                        ]
                    };
                ydjfChart.setOption(ydjfOption);
            } else {
                mini.alert(returnJson.msg, "系统提示");
            }
        }
    });
}

function changeTime() {
    loadChartOutydjf();
}
