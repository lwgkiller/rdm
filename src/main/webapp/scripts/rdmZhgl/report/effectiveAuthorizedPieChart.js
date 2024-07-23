$(function () {
    effectiveAuthorizedInit();
});

function effectiveAuthorizedInit() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime = "2009-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("effectiveAuthorizedBuildFrom").setValue(startTime);
    mini.get("effectiveAuthorizedBuildTo").setValue(endTime);
    queryEffectiveAuthorizedPieChartData()
}

// 指定图表的配置项和数据
var effectiveAuthorizedPieChartOption = {
    title: {
        text: "有效授权中国专利量",
        subtext: '',
        left: "center",
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a}<br/>{b}:{c} ({d}%)',
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: []
    },
    series: [
        {
            name: '有效授权中国专利量',
            type: 'pie',
            radius: '55%',
            center: ['50%','60%'],
            data: [],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgb(0,0,0,0.5)'
                }
            },
            label: {
                show: true,
                position: 'outside',
                formatter: '{b}: {c}'
            }
        }
    ]
};

function queryEffectiveAuthorizedPieChartData() {
    var postData = {};
    if (mini.get("effectiveAuthorizedBuildFrom").getValue()) {
        postData.startTime = mini.get("effectiveAuthorizedBuildFrom").getValue();
    }
    if (mini.get("effectiveAuthorizedBuildTo").getValue()) {
        postData.endTime = mini.get("effectiveAuthorizedBuildTo").getValue();
    }
    postData.list = ['026'];
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ZLAndFW/report/authorizedPieChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                effectiveAuthorizedPieChartOption.series[0].data = returnData.seriesData;
                effectiveAuthorizedPieChartOption.legend.data = returnData.legendData;
                effectiveAuthorizedPieChartOption.title.subtext = '总计: '+ returnData.total;
                effectiveAuthorizedChart.setOption(effectiveAuthorizedPieChartOption);
            }
        }
    });
}

function effectiveAuthorizedBuildFromChanged() {
    queryEffectiveAuthorizedPieChartData();
}

function effectiveAuthorizedBuildToChanged() {
    queryEffectiveAuthorizedPieChartData();
}

