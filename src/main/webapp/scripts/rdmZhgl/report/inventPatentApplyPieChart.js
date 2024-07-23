$(function () {
    inventPatentApplyInit();
});

function inventPatentApplyInit() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime = "2009-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("inventPatentApplyBuildFrom").setValue(startTime);
    mini.get("inventPatentApplyBuildTo").setValue(endTime);
    queryInventPatentApplyPieChartData()
}

// 指定图表的配置项和数据
var inventPatentApplyPieChartOption = {
    title: {
        text: "中国发明专利申请量",
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
            name: '中国发明专利申请量',
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

function queryInventPatentApplyPieChartData() {
    var postData = {};
    if (mini.get("inventPatentApplyBuildFrom").getValue()) {
        postData.startTime = mini.get("inventPatentApplyBuildFrom").getValue();
    }
    if (mini.get("inventPatentApplyBuildTo").getValue()) {
        postData.endTime = mini.get("inventPatentApplyBuildTo").getValue();
    }
    postData.list = [];
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ZLAndFW/report/inventPatentApplyPieChart.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                inventPatentApplyPieChartOption.series[0].data = returnData.seriesData;
                inventPatentApplyPieChartOption.legend.data = returnData.legendData;
                inventPatentApplyPieChartOption.title.subtext = '总计: '+ returnData.total + "; 发明授权率: " + returnData.inventApplyPercentage + "%";
                inventPatentApplyChart.setOption(inventPatentApplyPieChartOption);
            }
        }
    });
}

function inventPatentApplyBuildFromChanged() {
    queryInventPatentApplyPieChartData();
}

function inventPatentApplyBuildToChanged() {
    queryInventPatentApplyPieChartData();
}

