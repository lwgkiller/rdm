$(function () {
    patentApplyInit();
});

function patentApplyInit() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime = "2009-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("patentApplyBuildFrom").setValue(startTime);
    mini.get("patentApplyBuildTo").setValue(endTime);
    queryPatentApplyPieChartData()
}

// 指定图表的配置项和数据
var patentApplyPieChartOption = {
    title: {
        text: "中国专利申请量",
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
        data: ["分类1"]
    },
    series: [
        {
            name: '中国专利申请量',
            type: 'pie',
            radius: '55%',
            center: ['50%','60%'],
            data: [{name: 'fenlei', value: 123}],
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

function queryPatentApplyPieChartData() {
    var postData = {};
    if (mini.get("patentApplyBuildFrom").getValue()) {
        postData.startTime = mini.get("patentApplyBuildFrom").getValue();
    }
    if (mini.get("patentApplyBuildTo").getValue()) {
        postData.endTime = mini.get("patentApplyBuildTo").getValue();
    }
    postData.list = [];
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ZLAndFW/report/patenApplyPieChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                patentApplyPieChartOption.series[0].data = returnData.seriesData;
                patentApplyPieChartOption.legend.data = returnData.legendData;
                patentApplyPieChartOption.title.subtext = '总计: '+ returnData.total;
                patentApplyPieChart.setOption(patentApplyPieChartOption);
            }
        }
    });
}

function patentApplyBuildFromChanged() {
    queryPatentApplyPieChartData();
}

function patentApplyBuildToChanged() {
    queryPatentApplyPieChartData();
}

